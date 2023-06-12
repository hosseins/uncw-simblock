import json
import os
import statistics

# returns a list of json objects
def create_json_list(json_file_name):
    json_file = open(json_file_name)
    list_form = json_file.read()[1:-1].split('},')
    json_file.close()

    list_of_stringified_json = [i + '}' for i in list_form[:-1]] + [list_form[-1]]
    return [json.loads(x) for x in list_of_stringified_json]


# get number of nodes added to the network
def get_num_nodes(json_list):
    num_nodes = 0
    for event in json_list:
        if event['kind'] == 'add-node':
            num_nodes += 1
    return num_nodes


# get min, max, median block propagation times
def get_propagation_info(json_list):
    num_nodes = get_num_nodes(json_list)

    # stores block_id: [prop_start_time, prop_end_time]
    block_propagation_times = {}
    # stores block_id: {nodes who've seen the block}
    block_propagation = {}
    for event in json_list:
        if event['kind'] == 'add-block':
            block_id = event['content']['block-id']
            node_id = event['content']['node-id']

            # if this block has been seen by a node before
            if block_id in block_propagation:
                # add the new node in the list of receivers
                block_propagation[block_id].add(node_id)
                # the first time the block is seen by all nodes, mark the propagation end timestamp
                if len(block_propagation[block_id]) == num_nodes and len(block_propagation_times[block_id]) == 1:
                    block_propagation_times[block_id].append(event['content']['timestamp'])
            else:
                # create the entry, we use a set because we don't want to store duplicates
                block_propagation[block_id] = {node_id}
                # log the propagation start time, we use list because we want to maintain order
                block_propagation_times[block_id] = [event['content']['timestamp']]

    # assertion: block_propagation_times is now a hashtable with keys consisting of each block id
    # and a value which is the propagation start timestamp and a propagation end timestamp

    propagation_times = []
    for x in block_propagation_times.keys():
        if len(block_propagation_times[x]) >= 2:
            propagation_times.append(block_propagation_times[x][1] - block_propagation_times[x][0])

    min_propagation_time = min(propagation_times)
    max_propagation_time = max(propagation_times)
    median_propagation_time = statistics.median(propagation_times)



    return min_propagation_time, max_propagation_time, median_propagation_time


def get_number_forks(blocklist_filename):
    with open(blocklist_filename) as block_file:
        num_forks = 0
        for line in block_file:
            if "Orphan" in line:
                num_forks += 1
        return num_forks


def analyze_run_data(json_file_name, blocklist_file_name):
    json_list = create_json_list(json_file_name)
    prop_info = get_propagation_info(json_list)

    num_forks = get_number_forks(blocklist_file_name)

    output_filename = 'simulation_results.csv'
    with open(output_filename, 'a') as output_file:
        # min,median,max,num_forks
        output_file.write("{0},{1},{2},{3}\n".format(prop_info[0], prop_info[2], prop_info[1], num_forks))


if __name__ == "__main__":
    json_output_files = os.listdir("jsonOutput")

    for i in range(len(json_output_files)):
        analyze_run_data(f"jsonOutput/output{i}.json", f"blockOutput/blockList{i}.json")
