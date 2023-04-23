package simblock.node;

import simblock.block.Block;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;

import java.util.ArrayList;

public class Farmer extends Node{

    // global var for k
    // override addToChain to match updateChain
    // override minting to match farm
    // figure out how to store chain

    private int[] PoSpaceCount;
    private ArrayList<String> Chain;
    public Farmer(
            int nodeID, int numConnection, int region, long miningPower, String routingTableName, boolean useCBR, boolean isChurnNode, int depth
    ) {
        super(nodeID, numConnection, region, miningPower, routingTableName, useCBR, isChurnNode);
        PoSpaceCount = new int[depth];
        Chain = new ArrayList<String>();
    }

    @Override
    public void addToChain(Block newBlock){
        return;
    }

    @Override
    public void minting() {
        return;
    }
}
