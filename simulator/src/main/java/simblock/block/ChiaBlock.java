package simblock.block;

import simblock.node.Node;

import java.math.BigInteger;

public class ChiaBlock extends Block implements Comparable<ChiaBlock>{

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
    public ChiaBlock(Block parent, Node minter, long time, PoSpaceFoliage foliage, PoSpaceTrunk trunk, boolean finalized, BigInteger quality) {
        super(parent, minter, time);
        this.foliagePiece = foliage;
        this.trunkPiece = trunk;
        this.finalized = finalized;
        if(this.getParent() != null){
            chainQuality = ((ChiaBlock) this.getParent()).getChainQuality().add(quality);
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

    public boolean isFinalized() {
        return finalized;
    }

    @Override
    public int compareTo(ChiaBlock other) {
        return this.chainQuality.compareTo(other.getChainQuality());
    }
}
