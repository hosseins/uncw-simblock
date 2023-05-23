package simblock.node;

import simblock.block.Block;
import simblock.block.ChiaBlock;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;

import java.math.BigInteger;
import java.util.PriorityQueue;

import static simblock.settings.SimulationConfiguration.CHIA_K;
import static simblock.settings.SimulationConfiguration.NUM_OF_NODES;
import static simblock.simulator.Simulator.arriveBlock;

public class Farmer extends Node{

    // this is used to specify the size of the PoSpaceCount array and corresponds to how many levels/depths we keep track of
    private static final int DEPTH = NUM_OF_NODES * 2;
    // used with Paths to determine how many paths each node keeps track of
    private static final int PATH_COUNT = CHIA_K * 2;

    // how many blocks this farmer has mined at a particular level
    private int[] PoSpaceCount;

    // priority queue for keeping track of x number of paths during the simulation.
    private PriorityQueue<ChiaBlock> Paths;
    public Farmer(
            int nodeID, int numConnection, int region, long miningPower, String routingTableName, boolean useCBR, boolean isChurnNode
    ) {
        super(nodeID, numConnection, region, miningPower, routingTableName, useCBR, isChurnNode);
        PoSpaceCount = new int[DEPTH];
        Paths = new PriorityQueue<ChiaBlock>(PATH_COUNT);
    }


    @Override
    public ChiaBlock getCurrentBlock(){
        return (ChiaBlock) super.getCurrentBlock();
    }
    @Override
    public void addToChain(Block newBlock){
        // TODO may need to have something that removes a VDF task from the simulator

        // assertion: we have validated newBlock and know that it belongs on the chain.
        // we determine which branch the block belongs on or replaces
        // then we update the chain accordingly


        BigInteger newQuality = ((ChiaBlock) newBlock).getChainQuality();
        boolean replaced = false;
        int idx = -1;

        // Note: Chain.length refers to the number of chains/branches we keep track of
        // this is specified by the 'chia_k' parameter in the configs. Chia default is 3
        ChiaBlock[] currentBlocks = Paths.toArray(new ChiaBlock[PATH_COUNT]);
        for(int i = 0; i < PATH_COUNT; i++){
            // if we have not yet seen the max number of paths, add the block to the empty branch tracker
            if(currentBlocks[i] == null) {
                Paths.add((ChiaBlock) newBlock);
                replaced = true;
                break;
            }
            // if the block is the child of the current branch head, extend that branch with the child
            else if(newBlock.getParent() != null && newBlock.getParent().getId() == currentBlocks[i].getId()) {
                Paths.remove(currentBlocks[i]);
                Paths.add((ChiaBlock) newBlock);
                replaced = true;
                break;
            }

            // if the new block belongs to a branch with a better quality than an existing branch
            // we want to remove the lowest quality, not just the first one with a
            // lower quality than the new block
            if(currentBlocks[i].getChainQuality().compareTo(newQuality) < 0){
                idx = i;
                break;
            }
        }

        if(idx >= 0 && !replaced){
            System.out.println(Paths);
            Paths.poll();
            System.out.println(Paths);
            Paths.add((ChiaBlock) newBlock);
            replaced = true;
        }
        // if one of the conditions above are met, then we will have a valid idx
        if(replaced) {
            this.setCurrentBlock(newBlock);
        }
        else {
            System.err.println("Block was not added to chain - investigate");
        }
        printAddBlock(newBlock);
        arriveBlock(newBlock, this);
    }

    @Override
    public void minting() {
        ChiaBlock block = this.getCurrentBlock();
        if(block == null || this.PoSpaceCount[block.getHeight() + 1] == CHIA_K){ return; }

        this.PoSpaceCount[block.getHeight() + 1]++;

        AbstractMintingTask task = Simulator.getConsensusAlgo().CreateMintingTask(this);
        Timer.getSimulationTimer().putTask(task);
    }

    @Override
    public void receiveBlock(Block block) {
        // skip un-finalized blocks
        if(!((ChiaBlock) block).isFinalized()) {
            return;
        }

        // Algorithm 2 from Chia paper
        boolean validBlock = false;

        ChiaBlock[] currentBlocks = Paths.toArray(new ChiaBlock[PATH_COUNT]);
        for (int i = 0; i < PATH_COUNT; i++) {
            validBlock = validBlock || Simulator.getConsensusAlgo().isReceivedBlockValid(block, currentBlocks[i]);
        }

        if (validBlock) {
            this.addToChain(block);
            this.minting();
            this.sendInv(block);
        } else if (!this.getOrphans().contains(block)) {
            this.addOrphans(block, this.getCurrentBlock());
            arriveBlock(block, this);
        }
    }
}
