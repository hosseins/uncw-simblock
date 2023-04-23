package simblock.block;

import simblock.node.Node;

import java.math.BigInteger;

public class PoSpaceTrunk extends Block{
    /**
     * Instantiates a new Block.
     *
     * @param parent the parent
     * @param minter the minter
     * @param time   the time
     */
    public PoSpaceTrunk(Block parent, Node minter, long time) {
        super(parent, minter, time);
    }
}
