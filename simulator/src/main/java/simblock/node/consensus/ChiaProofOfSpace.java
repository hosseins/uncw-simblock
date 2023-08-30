package simblock.node.consensus;

import simblock.block.Block;
import simblock.block.ChiaBlock;
import simblock.block.PoSpaceFoliage;
import simblock.block.PoSpaceTrunk;
import simblock.node.Farmer;
import simblock.node.Node;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;
import simblock.task.VDFTask;

import java.math.BigInteger;

import static simblock.settings.SimulationConfiguration.VDF_MIN_TIME;
import static simblock.settings.SimulationConfiguration.VDF_TIME_RANGE;

public class ChiaProofOfSpace extends AbstractConsensusAlgo {
    @Override
    public AbstractMintingTask CreateMintingTask(Node node) {
        if (node == null || node.getCurrentBlock() == null || !(node instanceof Farmer)) {
            return null;
        }

        ChiaBlock unFinalizedBlock = this.createUnFinalizedBlock((Farmer) node);

        // Should the delay be random like this or based on the quality?
        // Should we add a check here to prevent blocks under a certain quality from being propagated?
        // Do we need to propogate un-finalized blocks to the farmers? Is there any reason to?
        long delay = VDF_MIN_TIME + (long) (Math.random() * VDF_TIME_RANGE);

        return new VDFTask(node, delay, unFinalizedBlock, ((Farmer) node).getMintingBlock());
    }

    @Override
    public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock) {
        // NOTE: in PoW this method checks that the received block has height 0 or has a difficulty greater than
        //       the parents nextDifficulty attribute - do we need to check for that in PoSpace?

        return (receivedBlock instanceof ChiaBlock) && (
                (currentBlock == null) ||
                        (((ChiaBlock) receivedBlock).getChainQuality().compareTo(((ChiaBlock) currentBlock).getChainQuality()) > 0)
        );
    }

    @Override
    public Block genesisBlock(Node node) {
        PoSpaceTrunk trunk = new PoSpaceTrunk();
        PoSpaceFoliage foliage = new PoSpaceFoliage(trunk, 1);
        return new ChiaBlock(null, node, 0, foliage, trunk, true, BigInteger.ONE);
    }

    private ChiaBlock createUnFinalizedBlock(Farmer farmer) {
        ChiaBlock parent = farmer.getCurrentBlock();

        // assign a random quality
        BigInteger quality = BigInteger.valueOf((long) 1 + (int) (Math.random() * 20));
        PoSpaceTrunk trunk = new PoSpaceTrunk();
        PoSpaceFoliage foliage = new PoSpaceFoliage(trunk, 1);
        return new ChiaBlock(parent, farmer, Timer.getClock(), foliage, trunk, false, quality);

    }
}
