package simblock.block;

import simblock.node.Node;

import java.math.BigInteger;

public class PoSpaceBlock extends Block{

    private PoSpaceFoliage foliagePiece;
    private PoSpaceTrunk trunkPiece;
    private boolean finalized;
    private BigInteger chainQuality;
    /**
     * Instantiates a new Block.
     *
     * @param parent the parent
     * @param minter the minter
     * @param time   the time
     */
    public PoSpaceBlock(Block parent, Node minter, long time, PoSpaceFoliage foliage, PoSpaceTrunk trunk, boolean finalized, BigInteger quality) {
        super(parent, minter, time);
        this.foliagePiece = foliage;
        this.trunkPiece = trunk;
        this.finalized = finalized;
        if(this.getParent() != null){
            chainQuality = ((PoSpaceBlock) this.getParent()).getChainQuality().add(quality);
        }
        else{
            chainQuality = quality;
        }
    }

    public BigInteger getChainQuality(){ return chainQuality; }

    public PoSpaceTrunk getTrunkPiece(){ return this.trunkPiece; }
    public PoSpaceFoliage getFoliagePiece(){ return this.foliagePiece; }

    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }
}
