package simblock.node.consensus;

import simblock.block.Block;
import simblock.block.PoSpaceBlock;
import simblock.block.PoSpaceFoliage;
import simblock.block.PoSpaceTrunk;
import simblock.node.Node;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;
import simblock.task.VDFTask;

import java.math.BigInteger;

public class ChiaProofOfSpace extends AbstractConsensusAlgo{
    @Override
    public AbstractMintingTask CreateMintingTask(Node node) {
        if(node==null || node.getCurrentBlock()==null){
            System.err.println("Node or block is null!");
            return null;
        }

        PoSpaceBlock block = (PoSpaceBlock) node.getCurrentBlock();

        BigInteger quality = BigInteger.valueOf((long) 1 + (int) (Math.random() * 20));
        PoSpaceTrunk trunk = new PoSpaceTrunk(block.getTrunkPiece(), node, Timer.getClock());
        PoSpaceFoliage foliage = new PoSpaceFoliage(block.getFoliagePiece(), node, Timer.getClock(), trunk, 1);
        PoSpaceBlock unfinalizedBlock = new PoSpaceBlock(block, node, Timer.getClock(), foliage, trunk, false, quality);
        long delay = 1 + (long) (Math.random() * 10);

        return new VDFTask(node, delay, unfinalizedBlock);
    }

    @Override
    public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock) {
        if(!(receivedBlock instanceof PoSpaceBlock)){ return false; }
        // need to think about what should go in here?
        return true;
    }

    @Override
    public Block genesisBlock(Node node) {
        PoSpaceTrunk trunk = new PoSpaceTrunk(null, node, 0);
        PoSpaceFoliage foliage = new PoSpaceFoliage(null, node, 0, trunk, 1);
        PoSpaceBlock genBlock = new PoSpaceBlock(null, node, 0, foliage, trunk, true, BigInteger.ONE);

        return genBlock;
    }
}
