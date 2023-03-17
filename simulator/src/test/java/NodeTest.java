import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.block.ProofOfWorkBlock;
import simblock.node.Node;
import simblock.node.routing.BitcoinCoreTable;
import simblock.simulator.Main;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

public class NodeTest {

    private static Node baseNode;
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";
    private static final long INTERVAL = 1000 * 60 * 10;

    public void setupOutputFiles(){
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            Main.CONF_FILE_URI = classLoader.getResource("simulator.conf").toURI();
            Main.OUT_FILE_URI = Main.CONF_FILE_URI.resolve(new URI("output"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            Main.OUT_JSON_FILE = new PrintWriter(
                    new BufferedWriter(new FileWriter(new File(Main.OUT_FILE_URI.resolve("./output.json")))));
            Main.STATIC_JSON_FILE = new PrintWriter(
                    new BufferedWriter(new FileWriter(new File(Main.OUT_FILE_URI.resolve("./static.json")))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Before
    public void init(){
        Timer.InitTimer();
        Simulator.InitSimulator(ALGO, INTERVAL);
        setupOutputFiles();
        this.baseNode = new Node(0, 1, 1, 10, TABLE, true, true);
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
        this.baseNode.addNeighbor(newNode);

        Assert.assertTrue(baseNode.getNeighbors().contains(newNode));

        baseNode.removeNeighbor(newNode);
        Assert.assertTrue(!(baseNode.getNeighbors().contains(newNode)));
    }

    @Test
    public void testReceiveBlockWithValidBlock(){
        int taskQueueStartSize = Timer.getSimulationTimer().getTasks().size();

        Node newNode = new Node(1, 1, 1, 10, TABLE, true, true);
        ProofOfWorkBlock block = new ProofOfWorkBlock((ProofOfWorkBlock) baseNode.getCurrentBlock(), baseNode, 1, BigInteger.TWO);
        newNode.receiveBlock(block);

        Assert.assertNotNull(newNode.getCurrentBlock());
        Assert.assertEquals(block, newNode.getCurrentBlock());

        if(taskQueueStartSize <= Timer.getSimulationTimer().getTasks().size()){
            //means the minting task was not sucessful so the current task should be null
            Assert.assertNull(newNode.getMintingTask());
        }
        else{
            Assert.assertNotNull(newNode.getMintingTask());
        }
    }

    @Test
    public void testReceiveMessage(){
        // should recieveMessage be overloaded instead of if if if if?
    }

    @Test
    public void testGenesisBlock() {
        Node node1 = new Node(2,0, 1, 10, TABLE, true, true);
        node1.genesisBlock();

        // verify node has a block, and the block has the attributes of a genesis block
        Assert.assertNotNull(node1.getCurrentBlock());
        Assert.assertEquals(null, node1.getCurrentBlock().getParent());
        Assert.assertEquals(BigInteger.ZERO, ((ProofOfWorkBlock) node1.getCurrentBlock()).getDifficulty());
    }

}