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

public class ChiaProofOfSpace extends AbstractConsensusAlgo{
    @Override
    public AbstractMintingTask CreateMintingTask(Node node) {
        if (node == null || node.getCurrentBlock() == null || !(node instanceof Farmer)) {
            return null;
        }

        ChiaBlock unFinalizedBlock = this.createUnFinalizedBlock((Farmer) node);
        long delay = 1 + (long) (Math.random() * 10);

        return new VDFTask(node, delay, unFinalizedBlock);
    }

    @Override
    public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock) {
        // TODO BRENNON
        // returns true if received block is a PoSpace block that extends or replaces current block
        // will extend if it is a child of current block
        // will replace if current block is null or if currentBlock has a lower chain quality
        return (receivedBlock instanceof ChiaBlock) && (
                (receivedBlock.getParent() == currentBlock) ||
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
