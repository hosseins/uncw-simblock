package simblock.node;

import simblock.block.Block;
import simblock.block.PoSpaceBlock;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;

import java.math.BigInteger;

import static simblock.settings.SimulationConfiguration.chia_k;

public class Farmer extends Node{

    private int[] PoSpaceCount;
    private PoSpaceBlock[] Chain;
    public Farmer(
            int nodeID, int numConnection, int region, long miningPower, String routingTableName, boolean useCBR, boolean isChurnNode, int depth
    ) {
        super(nodeID, numConnection, region, miningPower, routingTableName, useCBR, isChurnNode);
        PoSpaceCount = new int[depth];
        Chain = new PoSpaceBlock[chia_k];
    }

    @Override
    public void addToChain(Block newBlock){
        PoSpaceBlock parent = (PoSpaceBlock) newBlock.getParent();
        BigInteger minQuality = ((PoSpaceBlock) newBlock).getChainQuality();
        int idx = -1;

        for(int i = 0; i < Chain.length; i++){
            if(parent == Chain[i]) {
                Chain[i] = (PoSpaceBlock) newBlock;
                return;
            }
            if(Chain[i].getChainQuality().compareTo(minQuality) < 0){
                idx = i;
                minQuality = Chain[i].getChainQuality();
            }
        }
        if(idx >= 0){
            Chain[idx] = (PoSpaceBlock) newBlock;
        }
    }

    @Override
    public void minting() {
        PoSpaceBlock block = (PoSpaceBlock) this.getCurrentBlock();
        if(block == null || this.PoSpaceCount[block.getHeight() + 1] == chia_k){ return; }

        this.PoSpaceCount[block.getHeight() + 1]++;

        AbstractMintingTask task = Simulator.getConsensusAlgo().CreateMintingTask(this);
        Timer.getSimulationTimer().putTask(task);
    }
}
