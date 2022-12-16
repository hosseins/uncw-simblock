import org.junit.Assert;
import org.junit.Test;
import simblock.block.Block;
import simblock.node.Node;
import simblock.simulator.Timer;
import simblock.task.InvMessageTask;
import simblock.task.RecMessageTask;
import simblock.task.Task;

public class TimerTest {
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";

    @Test
    public void emptyTask() {
        Assert.assertEquals(null, Timer.getTask());
    }

    @Test
    public void oneTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        Node tn = new Node(1, 1, 1, 10, TABLE,
                ALGO, true, true);
        Block b = Block.genesisBlock(fn);
        Task t = new InvMessageTask(fn, tn, b);
        Timer.putTask(t);
        Assert.assertEquals(1, Timer.getTasks().size());
    }

    @Test
    public void runInVMessageTaskCreatesRecMessageTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        Node tn = new Node(1, 1, 1, 10, TABLE,
                ALGO, true, true);
        Block b = Block.genesisBlock(fn);
        Task t = new InvMessageTask(fn, tn, b);
        Timer.putTask(t);
        Timer.runTask();
        // Running InVMessageTask will result in RecMessageTask
        Assert.assertEquals(1, Timer.getTasks().size());
        Task t2 = Timer.getTask();
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
        Timer.putTask(t);
        Timer.putTask(t);
        Assert.assertEquals(1, Timer.getTasks().size());
    }

    @Test
    public void unorderedTasks(){
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        Node tn = new Node(1, 1, 1, 10, TABLE,
                ALGO, true, true);
        Block b = Block.genesisBlock(fn);
        Task t = new InvMessageTask(fn, tn, b);
        Timer.putTaskAbsoluteTime(t, 10);
    }

}
