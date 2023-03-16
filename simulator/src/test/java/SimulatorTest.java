import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.block.Block;
import simblock.block.ProofOfWorkBlock;
import simblock.node.Node;
import simblock.node.consensus.ProofOfWork;
import simblock.simulator.Main;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class SimulatorTest {
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
    public void init() {
        Timer.InitTimer();
        Simulator.InitSimulator(ALGO, INTERVAL);
        setupOutputFiles();
    }

    @Test
    public void VerifyInitialization() {
        Assert.assertTrue(Simulator.getConsensusAlgo() instanceof ProofOfWork);
        Assert.assertEquals(Simulator.getTargetInterval(), INTERVAL);
    }

    @Test
    public void UnseenBlockArrives() {
        int previousSize = Simulator.getObservedBlocksSize();
        Node node = new Node(0, 1, 1, 5, TABLE, true, true);
        Simulator.addNode(node);
        node.joinNetwork();
        node.genesisBlock();
        Assert.assertEquals(previousSize + 1, Simulator.getObservedBlocksSize());
    }

    @Test
    public void SeenBlockArrives() {
        int previousSize = Simulator.getObservedBlocksSize();
        Node node1 = new Node(0, 1, 1, 5, TABLE, true, true);
        Node node2 = new Node(1, 1, 1, 5, TABLE, true, true);
        Simulator.addNode(node1);
        Simulator.addNode(node2);
        node1.joinNetwork();
        node2.joinNetwork();
        Block newBlock = ProofOfWorkBlock.genesisBlock(node1);
        node1.receiveBlock(newBlock);
        node2.receiveBlock(newBlock);
        Assert.assertEquals(previousSize + 1, Simulator.getObservedBlocksSize());
    }

    @Test
    public void AddNodeWithConnection() {
        Node node1 = new Node(0, 1, 1, 5, TABLE, true, true);
        Node node2 = new Node(1, 1, 1, 5, TABLE, true, true);
        Simulator.addNode(node1);
        Simulator.addNodeWithConnection(node2);

        Assert.assertEquals(node1.getNeighbors().get(0), node2);
    }
}