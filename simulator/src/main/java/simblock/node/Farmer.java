package simblock.node;

import simblock.block.Block;
import simblock.block.ChiaBlock;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;

import static simblock.settings.SimulationConfiguration.CHIA_K;
import static simblock.settings.SimulationConfiguration.NUM_OF_NODES;
import static simblock.simulator.Simulator.arriveBlock;

public class Farmer extends Node {

    // this is used to specify the size of the PoSpaceCount array and corresponds to how many levels/depths we keep track of
    private static final int DEPTH = NUM_OF_NODES * 2;

    // how many blocks this farmer has mined at a particular level
    private final int[] PoSpaceCount;

    // block that is going to be minted/farmed off of
    private ChiaBlock mintingBlock;

    public Farmer(int nodeID, int numConnection, int region, long miningPower, String routingTableName, boolean useCBR, boolean isChurnNode) {
        super(nodeID, numConnection, region, miningPower, routingTableName, useCBR, isChurnNode);
        PoSpaceCount = new int[DEPTH];
    }

    /**
     * @return a string representing the farmers local view of the blockchain
     * from left to right it represents the oldest to most recent blocks the farmer is aware of and considers part of the canonical chain.
     */
    public String getLocalView() {
        String localView = this.getCurrentBlock().getId() + "";

        Block currBlock = this.getCurrentBlock().getParent();
        while (currBlock != null) {
            localView = currBlock.getId() + ", " + localView;
            currBlock = currBlock.getParent();
        }

        return localView;
    }

    @Override
    public ChiaBlock getCurrentBlock() {
        return (ChiaBlock) super.getCurrentBlock();
    }

    @Override
    public void addToChain(Block newBlock) {
        // Update the current block
        this.setCurrentBlock(newBlock);
        printAddBlock(newBlock);
        // Observe and handle new block arrival
        arriveBlock(newBlock, this);
    }

    @Override
    public void minting() {
        ChiaBlock block = this.getMintingBlock();
        if (block == null || this.PoSpaceCount[block.getHeight() + 1] == CHIA_K) {
            return;
        }

        this.PoSpaceCount[block.getHeight() + 1]++;


        AbstractMintingTask task = Simulator.getConsensusAlgo().CreateMintingTask(this);
        this.setMintingTask(task);
        if (task != null) {
            Timer.getSimulationTimer().putTask(task);
        }
    }

    @Override
    public void receiveBlock(Block block) {
        /**
         * Algorithm 2 from Chia paper
         */

        // ensure that the block is a ChiaBlock as farmers are not intended to handle non-chia blocks
        if (!(block instanceof ChiaBlock)) {
            System.out.println("Block not instance of Chia Block: " + block.toString() + " ID: " + block.getId() + " Farmer: " + block.getMinter());
        }

        // update the farmers local view of the chain (for both finalized and non-finalized blocks)
        this.updateChain((ChiaBlock) block);
        // for finalized blocks - the farmer should run the farm() algorithm (#3 in Chia green paper)
        if (((ChiaBlock) block).isFinalized()) {
            this.farm((ChiaBlock) block);
        }
    }

    public void updateChain(ChiaBlock block) {
        if (Simulator.getConsensusAlgo().isReceivedBlockValid(block, this.getCurrentBlock())) {
            if (this.getCurrentBlock() != null && !this.getCurrentBlock().isOnSameChainAs(block)) {
                // If orphan mark orphan
                this.addOrphans(this.getCurrentBlock(), block);
            }
            this.addToChain(block);
            this.sendInv(block);
        } else if (!this.getOrphans().contains(block) && !block.isOnSameChainAs(this.getCurrentBlock())) {
            this.addOrphans(block, this.getCurrentBlock());
            arriveBlock(block, this);
        }
    }

    public void farm(ChiaBlock finalizedBlock) {
        this.mintingBlock = finalizedBlock;
        this.minting();
    }

    public ChiaBlock getMintingBlock() {
        return mintingBlock;
    }


}
