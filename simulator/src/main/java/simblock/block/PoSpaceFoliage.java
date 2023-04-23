package simblock.block;

import simblock.node.Node;

public class PoSpaceFoliage extends Block{

    private PoSpaceTrunk trunkBlock;
    private int data;
    /**
     * Instantiates a new Block.
     *
     * @param parent the parent
     * @param minter the minter
     * @param time   the time
     */
    public PoSpaceFoliage(PoSpaceFoliage parent, Node minter, long time, PoSpaceTrunk trunkBlock, int data) {
        super(parent, minter, time);
        this.trunkBlock = trunkBlock;
        this.data = data;
    }
}
