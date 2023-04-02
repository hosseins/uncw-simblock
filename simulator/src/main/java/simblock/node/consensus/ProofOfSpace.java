package simblock.node.consensus;

import simblock.block.Block;
import simblock.block.ProofOfWorkBlock;
import simblock.node.Node;
import simblock.simulator.Simulator;
import simblock.simulator.Timer;
import simblock.task.AbstractMintingTask;
import simblock.task.MiningTask;
import simblock.task.WinningTask;

import java.math.BigInteger;

import static simblock.simulator.Main.random;

public class ProofOfSpace extends AbstractConsensusAlgo{

    public ProofOfSpace(){
        for(int i = 1; i<=10; i++){
            Timer.getSimulationTimer().putTask(new WinningTask());
        }
    }
    @Override
    public MiningTask CreateMintingTask(Node node){
         try {

             ProofOfWorkBlock block = (ProofOfWorkBlock) node.getCurrentBlock();
             BigInteger difficulty = block.getNextDifficulty();
             if(difficulty.intValue()==0){
                 System.err.println("difficulty is 0");
                 return null;
             }
             double p = 1.0 / difficulty.doubleValue();
             double u = random.nextDouble();
             return p <= Math.pow(2, -53) ? null : new MiningTask(node, 1, difficulty);
         }
         catch (Exception e){
             System.out.println("No node ready");
             return null;
         }

     }

    @Override
    public boolean isReceivedBlockValid(Block receivedBlock, Block currentBlock){
        if (!(receivedBlock instanceof ProofOfWorkBlock)) {
            return false;
        }
        ProofOfWorkBlock recPoWBlock = (ProofOfWorkBlock) receivedBlock;
        ProofOfWorkBlock currPoWBlock = (ProofOfWorkBlock) currentBlock;
        int receivedBlockHeight = receivedBlock.getHeight();
        ProofOfWorkBlock receivedBlockParent = receivedBlockHeight == 0 ? null :
                (ProofOfWorkBlock) receivedBlock.getBlockWithHeight(receivedBlockHeight - 1);

        //TODO - dangerous to split due to short circuit operators being used, refactor?
        return (
                receivedBlockHeight == 0 ||
                        recPoWBlock.getDifficulty().compareTo(receivedBlockParent.getNextDifficulty()) >= 0
        ) && (
                currentBlock == null ||
                        recPoWBlock.getTotalDifficulty().compareTo(currPoWBlock.getTotalDifficulty()) > 0
        );
    }

    @Override
    public Block genesisBlock(Node node){ return ProofOfWorkBlock.genesisBlock(node);}

}
