package simblock.task;

import simblock.node.Node;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;

public class WinningTask implements Task{

    public static int k = 0;

    public long getInterval(){
        return 50 * (++k);
    }

    /**
     * Run the task.
     */
    public void run(){
        Node node;
        try {
            node = Simulator.getNextNode();
            Timer.getSimulationTimer().putTask(node.getMintingTask());
        }
        catch(Exception e){
            node = null;
        }

    }
}
