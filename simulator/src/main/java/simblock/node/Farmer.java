package simblock.node;

import simblock.block.Block;
import simblock.block.PoSpaceBlock;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;
import simblock.settings.SimulationConfiguration.*;
import java.util.ArrayList;

import static simblock.settings.SimulationConfiguration.chia_k;

public class Farmer extends Node{

    // override addToChain to match updateChain
    // figure out how to store chain

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
        return;
    }

    @Override
    public void minting() {
        PoSpaceBlock block = (PoSpaceBlock) this.getCurrentBlock();
        if(this.PoSpaceCount[block.getHeight() + 1] == chia_k){ return; }

        this.PoSpaceCount[block.getHeight() + 1]++;

        AbstractMintingTask task = Simulator.getConsensusAlgo().CreateMintingTask(this);
        Timer.getSimulationTimer().putTask(task);
    }
}
