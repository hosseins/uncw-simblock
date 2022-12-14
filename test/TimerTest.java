import org.junit.Assert;
import org.junit.Test;
import simblock.block.Block;
import simblock.node.Node;
import  simblock.simulator.Timer;
import simblock.task.InvMessageTask;
import simblock.task.Task;

public class TimerTest {
    @Test
    public void getTaskTestEmpty() {
        Assert.assertEquals(null, Timer.getTask());
    }

    @Test
    public void getTaskTestAdd() {
        Node fn = new Node(0, 1, 1, 10, "simblock.node.routing.BitcoinCoreTable",
                "simblock.node.consensus.ProofOfWork", true, true);
        Node tn = new Node(1, 1, 1, 10, "simblock.node.routing.BitcoinCoreTable",
                "simblock.node.consensus.ProofOfWork", true, true);
        Block b = new Block(null,  fn, Timer.getCurrentTime());
        Task t = new InvMessageTask(fn, tn, b);
        Timer.putTask(t);
        Assert.assertEquals(1, Timer.getTasks().size());
    }

}
