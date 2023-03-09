import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.node.Node;
import simblock.node.consensus.ProofOfWork;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.MiningTask;

public class ProofOfWorkTest {
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";

    private static final long INTERVAL = 1000 * 60 * 10;

    @Before
    public void init() {
        Timer.InitTimer();
        Simulator.InitSimulator(ALGO, INTERVAL);
    }

    @Test
    public void ProofOfWorkWithoutBlockTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE, true, true);
        Simulator.addNode(fn);
        ProofOfWork pow = new ProofOfWork();
        MiningTask mt = pow.CreateMintingTask(fn);
        Assert.assertNull(mt);
    }
    /*@Test
    public void ProofOfWorkWithBlockTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE, true, true);
        Simulator.addNode(fn);
        fn.minting();
        fn.genesisBlock();
        ProofOfWork pow = new ProofOfWork();
        MiningTask mt = pow.CreateMintingTask(fn);
        Assert.assertNotEquals(null, mt);
    }*/
    /*@Test
    public void ProofOfWorkWithBlockTask2() {
        Node fn = new Node(0, 1, 1, 10, TABLE, true, true);
        Simulator.addNode(fn);
        fn.minting();
        MiningTask mt = (MiningTask)fn.getMintingTask();
        Assert.assertNotEquals(null, mt);
        System.out.println(mt.getInterval());
        System.out.println(fn.getMiningPower());
        System.out.println(((ProofOfWorkBlock)fn.getCurrentBlock()).getNextDifficulty());
    }*/

}
