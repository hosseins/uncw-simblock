package simblock.block;

import simblock.node.Node;

public class PoSpaceBlock extends Block{

    /**
     * Instantiates a new Block.
     *
     * @param parent the parent
     * @param minter the minter
     * @param time   the time
     */
    public PoSpaceBlock(Block parent, Node minter, long time) {
        super(parent, minter, time);
    }
}
