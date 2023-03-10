import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import simblock.node.Node;
import simblock.node.consensus.ProofOfWork;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.MiningTask;
import simblock.simulator.Main;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class ProofOfWorkTest {
    private static final String ALGO  = "simblock.node.consensus.ProofOfWork";
    private static final String TABLE  = "simblock.node.routing.BitcoinCoreTable";

    private static final long INTERVAL = 1000 * 60 * 10;

    public void setupOutputFiles(){
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            Main.CONF_FILE_URI = classLoader.getResource("C:\\Users\\Brie\\Codes\\uncw-simblock\\simulator\\src\\dist\\conf\\simulator.conf").toURI();
            Main.OUT_FILE_URI = Main.CONF_FILE_URI.resolve(new URI("../output/"));
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
    public void ProofOfWorkWithoutBlockTask() {
        Node fn = new Node(0, 1, 1, 10, TABLE, true, true);
        Simulator.addNode(fn);
        ProofOfWork pow = new ProofOfWork();
        MiningTask mt = pow.CreateMintingTask(fn);
        Assert.assertNull(mt);
    }
    @Test
    public void ProofOfWorkWithBlockTask() {
        System.out.println("START TEST ____________________");
        Node fn = new Node(0, 1, 1, 10, TABLE, true, true);
        Simulator.addNode(fn);
        fn.minting();
        fn.genesisBlock();
        ProofOfWork pow = new ProofOfWork();
        MiningTask mt = pow.CreateMintingTask(fn);
        Assert.assertNotEquals(null, mt);
        System.out.println("END TEST ____________________");

    }
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
