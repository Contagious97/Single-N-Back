package se.kth.mohosm.ttt.model;

import android.util.Log;

import java.util.Random;

public class GameLogic {

    private int n_back;
    private int rounds;
    private int[] positions;
    private char[] auditory;
    private int currPosition;
    int score = 0;
    boolean gameStarted;
    private boolean isAuditory;

    private static final String TAG = "GAME_LOGIC";

    public GameLogic(int n_back, int rounds){
        this.n_back = n_back;
        this.rounds = rounds;
        gameStarted = false;
        isAuditory = false;
    }


    private void generateAuditory(){
        char letters[] = new char[]{'C','K','L','M','T','G','O'};
        auditory = new char[rounds];
        Random random = new Random();
        int randomIndex = -1;
        for (int i =0; i<auditory.length; i++){
            randomIndex = random.nextInt(7);
            auditory[i] = letters[randomIndex];
        }
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
            if (rounds == 6)
                positions[i] = codedPositions2[i];
            else
                positions[i] = codedPosistions[i];
        }

    }

    public boolean isCorrectGuess(){
        if (isAuditory){
            if (getN_backValue() < 0)
                return false;
            Log.i(TAG,"Curr value: " + auditory[currPosition]);
            Log.i(TAG,"N-back value: " + getN_backValue());
            return auditory[currPosition] == getN_backValue();
        } else{
            if (getN_backValue() < 0)
                return false;
            Log.i(TAG,"Curr value: " + positions[currPosition]);
            Log.i(TAG,"N-back value: " + getN_backValue());
            return positions[currPosition] == getN_backValue();
        }
    }

    public void makeMove(){
        currPosition++;
    }

    public boolean gameOver(){
        return currPosition == rounds;
    }

    public int getCurrPosition(){
        return currPosition;
    }

    public int getPosition(){
        if (currPosition> positions.length-1)
            return positions[positions.length -1];
        if (currPosition == -1)
            return -1;
        return positions[currPosition];
    }

    public int getPrevValue(){
        if (currPosition < 1)
            return -1;
        return positions[currPosition-1];
    }



    private int getN_backValue(){
        if (isAuditory){
            if (currPosition >= n_back){
                return auditory[currPosition-n_back];
            } else return Integer.MIN_VALUE;
        }else{
            if (currPosition >= n_back)
                return positions[currPosition-n_back];
            else return Integer.MIN_VALUE;
        }
    }

    public void start(){
        gameStarted = true;
        if (isAuditory){
            generateAuditory();
        }else
            generatePositions();

        currPosition = -1;
    }

    public void reset(){
        for (int i = 0; i<positions.length; i++){
            positions[i] = 0;
        }

        currPosition = -1;
    }

}
