package simblock.task;

import simblock.block.PoSpaceBlock;
import simblock.node.Node;

public class VDFTask extends AbstractMintingTask{

    private PoSpaceBlock unfinalizedBlock;
    /**
     * Instantiates a new Abstract minting task.
     *
     * @param minter   the minter
     * @param interval the interval in milliseconds
     */
    public VDFTask(Node minter, long interval, PoSpaceBlock unfinalizedBlock) {
        super(minter, interval);
        this.unfinalizedBlock = unfinalizedBlock;
    }

    @Override
    public void run() {
        this.unfinalizedBlock.setFinalized(true);
        this.getMinter().receiveBlock(this.unfinalizedBlock);
    }
}
