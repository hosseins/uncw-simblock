import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.node.Node;
import simblock.node.consensus.ProofOfSpace;
import simblock.node.consensus.ProofOfWork;
import simblock.simulator.Main;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.MiningTask;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class ProofOfSpaceTest {
    private static final String ALGO  = "simblock.node.consensus.ProofOfSpace";
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
    public void ProofOfSpaceWithBlockTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE, true, true);
        Simulator.addNode(fn);
        fn.minting();
        fn.genesisBlock();
        ProofOfSpace pos = new ProofOfSpace();
        MiningTask mt = pos.CreateMintingTask(fn);
        Assert.assertNotEquals(null, mt);
    }
    @Test
    public void ProofOfSpaceWithoutBlockTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE, true, true);
        Simulator.addNode(fn);
        ProofOfSpace pos = new ProofOfSpace();
        MiningTask mt = pos.CreateMintingTask(fn);
        Assert.assertNull(mt);
    }
}
