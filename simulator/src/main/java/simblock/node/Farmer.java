package simblock.node;

import simblock.block.Block;
import simblock.block.ChiaBlock;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;

import java.math.BigInteger;

import static simblock.settings.SimulationConfiguration.chia_k;
import static simblock.simulator.Simulator.arriveBlock;

public class Farmer extends Node{

    private int[] PoSpaceCount;
    private ChiaBlock[] Chain;
    public Farmer(
            int nodeID, int numConnection, int region, long miningPower, String routingTableName, boolean useCBR, boolean isChurnNode, int depth
    ) {
        super(nodeID, numConnection, region, miningPower, routingTableName, useCBR, isChurnNode);
        PoSpaceCount = new int[depth];
        Chain = new ChiaBlock[chia_k];
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


        BigInteger minQuality = ((ChiaBlock) newBlock).getChainQuality();
        int idx = -1;

        // Note: Chain.length refers to the number of chains/branches we keep track of
        // this is specified by the 'chia_k' parameter in the configs. Chia default is 3
        for(int i = 0; i < Chain.length; i++){
            // if the block is the child of the current branch head, extend that branch with the child
            if(newBlock.getParent() == Chain[i]) {
                idx = i;
                break;
            }
            // (used only early in the simulation)
            // if we have not yet seen three branches, add the block to the empty branch tracker
            else if(Chain[i] == null) {
                idx = i;
                break;
            }
            // if the new block belongs to a branch with a better quality than an existing branch
            // we want to remove the lowest quality, not just the first one with a
            // lower quality than the new block
            if(Chain[i].getChainQuality().compareTo(minQuality) < 0){
                idx = i;
                minQuality = Chain[i].getChainQuality();
            }
        }

        // if one of the conditions above are met, then we will have a valid idx
        if(idx >= 0) {
            Chain[idx] = (ChiaBlock) newBlock;
            this.setCurrentBlock(newBlock);
            printAddBlock(newBlock);
        }
        else {
            System.err.println("Block was not added to chain - investigate");
        }
    }

    @Override
    public void minting() {
        ChiaBlock block = this.getCurrentBlock();
        if(block == null || this.PoSpaceCount[block.getHeight() + 1] == chia_k){ return; }

        this.PoSpaceCount[block.getHeight() + 1]++;

        AbstractMintingTask task = Simulator.getConsensusAlgo().CreateMintingTask(this);
        Timer.getSimulationTimer().putTask(task);
    }

    @Override
    public void receiveBlock(Block block) {
        // TODO do we need logic to handle orphans? I'm not sure what the intent was by including that in Node.java
        // Algorithm 2 from Chia paper
        boolean validBlock = false;
        for (int i = 0; i < Chain.length; i++) {
            validBlock = validBlock || Simulator.getConsensusAlgo().isReceivedBlockValid(block, Chain[i]);
        }
        if (validBlock) {
            this.addToChain(block);
            this.minting();
            this.sendInv(block);
        }
        else {
            arriveBlock(block, this);
        }
    }
}
