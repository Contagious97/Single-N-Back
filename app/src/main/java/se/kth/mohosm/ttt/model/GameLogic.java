package se.kth.mohosm.ttt.model;

import android.util.Log;

import java.util.Random;

public class GameLogic {

    private int n_back;
    private int rounds;
    private int[] positions;
    private char[] auditory;
    private int currPosition;
    boolean gameStarted;
    private boolean isAuditory;
    private final char letters[] = new char[]{'C','K','L','M','T','G','O'};
    public static final int SIZE = 3;


    private static final String TAG = "GAME_LOGIC";

    private static GameLogic gameLogic = null;

    private GameLogic(int n_back, int rounds){
        this.n_back = n_back;
        this.rounds = rounds;
        gameStarted = false;
        isAuditory = GameSettings.isAudioStimuli();
    }

    public static GameLogic getInstance(){
        if (gameLogic == null){
            gameLogic = new GameLogic(GameSettings.getValueOfN(),GameSettings.getNrOfEvents());
        }
        return gameLogic;
    }


    public boolean isGameStarted() {
        return gameStarted;
    }

    private void generateAuditory(){
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
        for (int i = 0; i< positions.length; i++){
            positions[i] = random.nextInt(9);
            Log.i(TAG,"Pos: " +positions[i]);
        }

        /*int codedPosistions[] = new int[]{1,4,1,3,5,3,7,3,8,4,8,2,1,5,1,6,6,7,6,7};

        int codedPositions2[] = new int[]{1,2,1,4,3,4};
        for (int i = 0; i<positions.length; i++){
            if (rounds == 6)
                positions[i] = codedPositions2[i];
            else
                positions[i] = codedPosistions[i];
        }*/

    }

    public boolean isCorrectGuess(){
        if (isAuditory){
            if (getN_backValue() < 0)
                return false;
            Log.i(TAG,"Curr value: " + auditory[currPosition== auditory.length?currPosition-1:currPosition]);
            Log.i(TAG,"N-back value: " + getN_backValue());
            return auditory[currPosition== auditory.length?currPosition-1:currPosition] == getN_backValue();
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
        if (currPosition == rounds)
            gameStarted = false;
        return currPosition == rounds;
    }

    public int getCurrPosition(){
        return currPosition;
    }

    public int getPosition(){
        if (isAuditory){
            if (currPosition > auditory.length -1)
                return auditory[auditory.length -1];
            if (currPosition == -1)
                return -1;
            return auditory[currPosition];


        } else {
            if (currPosition > positions.length-1)
                return positions[positions.length -1];
            if (currPosition == -1)
                return -1;
            return positions[currPosition];
        }
    }

    public char getLetter(){
        if (currPosition > auditory.length -1)
            return auditory[auditory.length -1];
        if (currPosition == -1)
            return Character.MIN_VALUE;
        return auditory[currPosition];
    }

    public int getPrevValue(){
        if (isAuditory){
            if (currPosition < 1)
                return -1;
            return auditory[currPosition-1];
        } else {
            if (currPosition < 1)
                return -1;
            return positions[currPosition-1];
        }
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
        isAuditory = GameSettings.isAudioStimuli();
        if (isAuditory){
            generateAuditory();
        }else
            generatePositions();

        //generateAuditory();
        //isAuditory = false;
        //rounds = GameSettings.getNrOfEvents();
        rounds = GameSettings.getNrOfEvents();
        n_back = GameSettings.getValueOfN();
        currPosition = -1;
    }

    public void reset(){
        /*for (int i = 0; i<positions.length; i++){
            positions[i] = 0;
        }*/
        gameStarted = true;
        currPosition = -1;
    }

    public boolean isAuditory() {
        return isAuditory;
    }
}
