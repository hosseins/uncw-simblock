package simblock.node.consensus;

import simblock.block.Block;
import simblock.node.Node;
import simblock.task.AbstractMintingTask;

public class ChiaProofOfSpace extends AbstractConsensusAlgo{
    @Override
    public AbstractMintingTask CreateMintingTask(Node node) {
        return null;
    }

    @Override
    public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock) {
        return false;
    }

    @Override
    public Block genesisBlock(Node node) {
        return null;
    }
}
