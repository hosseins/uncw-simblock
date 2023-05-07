package simblock.node.consensus;

import simblock.block.Block;
import simblock.block.PoSpaceBlock;
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

        PoSpaceBlock unFinalizedBlock = this.createUnFinalizedBlock((Farmer) node);
        long delay = 1 + (long) (Math.random() * 10);

        return new VDFTask(node, delay, unFinalizedBlock);
    }

    @Override
    public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock) {
        // returns true if received block is a PoSpace block that extends or replaces current block
        // will extend if it is a child of current block
        // will replace if current block is null or if currentBlock has a lower chain quality
        return (receivedBlock instanceof PoSpaceBlock) && (
                (receivedBlock.getParent() == currentBlock) ||
                        (currentBlock == null) ||
                        (((PoSpaceBlock) receivedBlock).getChainQuality().compareTo(((PoSpaceBlock) currentBlock).getChainQuality()) > 0)
                );
    }

    @Override
    public Block genesisBlock(Node node) {
        PoSpaceTrunk trunk = new PoSpaceTrunk(null, node, 0);
        PoSpaceFoliage foliage = new PoSpaceFoliage(null, node, 0, trunk, 1);
        return new PoSpaceBlock(null, node, 0, foliage, trunk, true, BigInteger.ONE);
    }

    private PoSpaceBlock createUnFinalizedBlock(Farmer farmer) {
        PoSpaceBlock parent = farmer.getCurrentBlock();

        // assign a random quality
        BigInteger quality = BigInteger.valueOf((long) 1 + (int) (Math.random() * 20));
        PoSpaceTrunk trunk = new PoSpaceTrunk(parent.getTrunkPiece(), farmer, Timer.getClock());
        PoSpaceFoliage foliage = new PoSpaceFoliage(parent.getFoliagePiece(), farmer, Timer.getClock(), trunk, 1);
        return new PoSpaceBlock(parent, farmer, Timer.getClock(), foliage, trunk, false, quality);

    }
}
