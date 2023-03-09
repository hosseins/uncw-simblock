import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.block.Block;
import simblock.node.Node;
import simblock.node.consensus.ProofOfWork;
import simblock.node.routing.BitcoinCoreTable;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;

public class NodeTest {

    private static Node baseNode;
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";
    private static final long INTERVAL = 1000 * 60 * 10;
    @Before
    public void init(){
        Timer.InitTimer();
        Simulator.InitSimulator(ALGO, INTERVAL);
        baseNode = new Node(0, 1, 1, 10, TABLE, true, true);
    }

    @Test
    public void verifySetup(){
        Assert.assertEquals(0, baseNode.getNodeID());
        Assert.assertEquals(1, baseNode.getRegion());
        Assert.assertEquals(10, baseNode.getMiningPower());
        Assert.assertTrue(baseNode.getRoutingTable() instanceof BitcoinCoreTable);
    }

    @Test
    public void testAddRemoveNeighbor(){
        Node newNode = new Node(1, 1, 1, 10, TABLE, true, true);
        baseNode.addNeighbor(newNode);

        Assert.assertTrue(baseNode.getNeighbors().contains(newNode));

        baseNode.removeNeighbor(newNode);
        Assert.assertTrue(!(baseNode.getNeighbors().contains(newNode)));
    }

    @Test
    public void testReceiveBlock(){

    }

    @Test
    public void testReceiveMessage(){

    }

    @Test
    public void testGenesisBlock() {

    }

    @Test
    public void sendNextBlockMessage() {

    }

}