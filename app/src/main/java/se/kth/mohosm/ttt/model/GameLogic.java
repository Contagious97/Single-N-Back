package se.kth.mohosm.ttt.model;

import android.util.Log;

import java.util.Random;

public class GameLogic {

    private int n_back;
    private int rounds;
    private int[] positionHistory;
    private int[] positions;
    private int currPosition;
    int score = 0;
    boolean gameStarted;

    private static final String TAG = "GAME_LOGIC";

    public GameLogic(int n_back, int rounds){
        this.n_back = n_back;
        this.rounds = rounds;
        gameStarted = false;
    }



    private void generatePositions(){
        positions = new int[rounds];
        Random random = new Random();
        /*for (int i = 0; i< positions.length; i++){
            positions[i] = random.nextInt(9);
            Log.i(TAG,"Pos: " +positions[i]);
        }*/

        int codedPosistions[] = new int[]{1,4,1,3,5,3,7,3,8,4,8,2,1,5,1,6,6,7,6,7};

        int codedPositions2[] = new int[]{1,2,1,4,3,4};
        for (int i = 0; i<positions.length; i++){
            if (rounds == 5)
                positions[i] = codedPositions2[i];
            else
                positions[i] = codedPosistions[i];
        }

    }

    public boolean isCorrectGuess(){
        if (getN_backPosition() < 0)
            return false;
        Log.i(TAG,"Curr value: " + positions[getCurrPosition()]);
        Log.i(TAG,"N-back value: " + getN_backPosition());
        return positions[currPosition] == getN_backPosition();
    }

    public void makeMove(){
        if (currPosition == positions.length-1)
            return;
        currPosition++;
    }

    public boolean gameOver(){
        return currPosition == positions.length-1;
    }

    public int getCurrPosition(){
        return currPosition;
    }

    public int getPosition(){
        if (currPosition> positions.length-1)
            return positions[positions.length -1];
        return positions[currPosition];
    }

    public int getPrevPosition(){
        return positions[currPosition-1];
    }

    private int getN_backPosition(){
        if (currPosition >= 2)
            return positions[currPosition-2];
        else return Integer.MIN_VALUE;
    }

    public void start(){
        gameStarted = true;
        generatePositions();
        currPosition = -1;
    }

    public void reset(){
        for (int i = 0; i<positions.length; i++){
            positions[i] = 0;
        }
        currPosition = 0;
    }

}
