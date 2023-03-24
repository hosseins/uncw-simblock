# median transmission time: time it takes the block to reach half the nodes on the network
# fork frequency: number of forks and max time
# how do I get number of forks?

# look for addBlock messages with the block id and the node id. count how many nodes receive each block and grab the
# timestamp of the lsat node recorded when the number of nodes reaches 50%

import json

file = open("simulator/src/dist/output/output.json")

# there are commas within the json so we can't split by just comma
list_form = file.read()[1:-1].split('},')
# here we add back the } to all except the last json entry
list_of_stringified_json = [i + '}' for i in list_form[:-1]]

list_of_json = [json.loads(x) for x in list_of_stringified_json]
event_types = {0}
num_nodes = 0
for event in list_of_json:
    if event['kind'] == 'add-node':
        num_nodes += 1
    event_types.add(event['kind'])
block_propogation_times = {}

# block id: set(nodes who've got the block)
block_propogation = {}
# now we want to search for all add-block events
for event in list_of_json:
    if event['kind'] == 'add-block':
        #grab block and node id to improve readability
        block_id = event['content']['block-id']
        node_id = event['content']['node-id']

        # if this block has been seen by a node before
        if block_id in block_propogation.keys():
            #and the new node in the list of receivers
            block_propogation[block_id].add(node_id)
            # the first time we hit 50% visibility, grab the time
            if len(block_propogation[block_id]) >= num_nodes/2 and len(block_propogation_times[block_id]) < 2:
                block_propogation_times[block_id].append(event['content']['timestamp'])
        else:
            # create the entry, we use sets for fast membership checks
            block_propogation[block_id] = {node_id}
            # log the propogation start time, we use list because we want to maintain order
            block_propogation_times[block_id] = [event['content']['timestamp']]

# assertion: block_propogation_times is now a hashtable with keys consisting of each block id
# and a value which is the propogation start timestamp and the blocks median propogation timestamp

# [1] - [0] gives time to propogate
# sum and divide by len to get average
average_median_propogation_time = sum([block_propogation_times[x][1] - block_propogation_times[x][0] for x in block_propogation_times.keys()]) / len(block_propogation_times)
print(average_median_propogation_time)



# based on main.java -> fork frequency is number of orphan blocks -- confirm this
block_file = open("simulator/src/dist/output/blockList.txt")
num_forks = 0
for line in block_file:
    if "Orphan" in line:
        num_forks += 1

print(num_forks)
