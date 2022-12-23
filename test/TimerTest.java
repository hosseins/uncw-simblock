import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.block.Block;
import simblock.node.Node;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.InvMessageTask;
import simblock.task.RecMessageTask;
import simblock.task.Task;

public class TimerTest {
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";

    @Before
    public void init() {
        Timer.InitTimer();
        Simulator.InitSimulator(ALGO);
    }

    @Test
    public void emptyTask() {
        Assert.assertEquals(null, Timer.getSimulationTimer().getTask());
    }

    @Test
    public void oneTask() {

        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        Node tn = new Node(1, 1, 1, 10, TABLE,
                ALGO, true, true);
        Block b = Block.genesisBlock(fn);
        Task t = new InvMessageTask(fn, tn, b);
        Timer.getSimulationTimer().putTask(t);
        Assert.assertEquals(1, Timer.getSimulationTimer().getTasks().size());
    }

    @Test
    public void runInVMessageTaskCreatesRecMessageTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        Node tn = new Node(1, 1, 1, 10, TABLE,
                ALGO, true, true);
        Block b = Block.genesisBlock(fn);
        Task t = new InvMessageTask(fn, tn, b);
        Timer.getSimulationTimer().putTask(t);
        Timer.getSimulationTimer().runTask();
        // Running InVMessageTask will result in RecMessageTask
        Assert.assertEquals(1, Timer.getSimulationTimer().getTasks().size());
        Task t2 = Timer.getSimulationTimer().getTask();
        Assert.assertTrue(t2  instanceof RecMessageTask);
    }

    @Test
    public void sameTask(){
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        Node tn = new Node(1, 1, 1, 10, TABLE,
                ALGO, true, true);
        Block b = Block.genesisBlock(fn);
        Task t = new InvMessageTask(fn, tn, b);
        Timer.getSimulationTimer().putTask(t);
        Timer.getSimulationTimer().putTask(t);
        Assert.assertEquals(1, Timer.getSimulationTimer().getTasks().size());
    }

    @Test
    public void unorderedTasks(){
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        Node tn = new Node(1, 1, 1, 10, TABLE,
                ALGO, true, true);
        Block b = Block.genesisBlock(fn);

        Task t1 = new InvMessageTask(fn, tn, b);
        Timer.getSimulationTimer().putTaskAbsoluteTime(t1, 10);

        Task t2 = new InvMessageTask(fn, tn, b);
        Timer.getSimulationTimer().putTaskAbsoluteTime(t2, 5);

        // getTask gets the first element
        // but does not remove it from the priority queue
        Assert.assertEquals(t2, Timer.getSimulationTimer().getTask());
    }

}
