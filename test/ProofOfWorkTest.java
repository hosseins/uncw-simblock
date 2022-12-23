import org.junit.Assert;
import org.junit.Test;
import simblock.node.Node;
import simblock.node.consensus.ProofOfWork;
import simblock.task.MiningTask;

public class ProofOfWorkTest {
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";

    @Test
    public void ProofOfWorkWithoutBlockTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        ProofOfWork pow = new ProofOfWork(fn);
        MiningTask mt = pow.minting();
        Assert.assertEquals(null, mt);
    }
    @Test
    public void ProofOfWorkWithBlockTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE,
                ALGO, true, true);
        ProofOfWork pow = new ProofOfWork(fn);
        MiningTask mt = pow.minting();
        Assert.assertEquals(null, mt);
    }
}
