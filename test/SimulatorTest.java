import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.block.Block;
import simblock.node.Node;
import simblock.node.consensus.ProofOfWork;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;

public class SimulatorTest {
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";

    private static final long INTERVAL = 1000 * 60 * 10;

    @Before
    public void init() {
        Timer.InitTimer();
        Simulator.InitSimulator(ALGO, INTERVAL);
    }

    @Test
    public void VerifyInitialization() {
        Assert.assertTrue(Simulator.getConsensusAlgo() instanceof ProofOfWork);
        Assert.assertEquals(Simulator.getTargetInterval(), INTERVAL);
    }

    @Test
    public void UnseenBlockArrives() {
        Node node = new Node(0, 1, 1, 5, TABLE, true, true);
        Simulator.addNode(node);
        Block newBlock = new Block(null, node, 5);
        node.receiveBlock(newBlock);
        Assert.assertEquals(1, Simulator.getObservedBlocksSize());
    }

    @Test
    public void SeenBlockArrives() {
        Node node1 = new Node(0, 1, 1, 5, TABLE, true, true);
        Node node2 = new Node(1, 1, 1, 5, TABLE, true, true);
        Simulator.addNode(node1);
        Simulator.addNode(node2);
        node1.joinNetwork();
        node2.joinNetwork();
        Block newBlock = new Block(null, node1, 5);
        node1.receiveBlock(newBlock);
        node2.receiveBlock(newBlock);
        Assert.assertEquals(1, Simulator.getObservedBlocksSize());
    }

    @Test
    public void AddNodeWithConnection() {
        Node node1 = new Node(0, 1, 1, 5, TABLE, true, true);
        Node node2 = new (1, 1, 1, 5, TABLE, true, true);
        Simulator.addNode(node1);
        Simulator.addNodeWithConnection(node2);

        Assert.assertEquals(node1.getNeighbors[0], node2);
    }
}