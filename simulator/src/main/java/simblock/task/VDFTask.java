package simblock.task;

import simblock.block.Block;
import simblock.block.ChiaBlock;
import simblock.node.Node;

public class VDFTask extends AbstractMintingTask {

    private final ChiaBlock unfinalizedBlock;

    /**
     * Instantiates a new Abstract minting task.
     *
     * @param minter   the minter
     * @param interval the interval in milliseconds
     */
    public VDFTask(Node minter, long interval, ChiaBlock unfinalizedBlock, Block parentBlock) {
        super(minter, interval, parentBlock);
        this.unfinalizedBlock = unfinalizedBlock;
    }

    @Override
    public void run() {
        this.unfinalizedBlock.setFinalized(true);
        this.getMinter().receiveBlock(this.unfinalizedBlock);
    }
}
