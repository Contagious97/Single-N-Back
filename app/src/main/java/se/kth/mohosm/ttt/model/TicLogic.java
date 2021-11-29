package se.kth.mohosm.ttt.model;

public class TicLogic {

    /*
     Game logic part
     */
    public static final int SIZE = 3;

    public enum Player {CROSS, NOUGHT, NONE}

    private Player[][] board;
    private int moves;
    private Player currentPlayer;
    private boolean isDecided;

    public void reset() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                board[r][c] = Player.NONE;
            }
        }
        moves = 0;
        currentPlayer = Player.CROSS;
        isDecided = false;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isEmpty(int row, int col) {
        return board[row][col] == Player.NONE;
    }

    // might be "NONE"
    public Player getWinner() {
        if (isDecided) {
            return currentPlayer;
        }
        return Player.NONE;
    }

    public boolean isDecided() {
        return isDecided;
    }

    public int getMoves() {
        return moves;
    }

    public boolean isLegalMove(int row, int col) {
        if (isDecided) return false;
        if (isEmpty(row, col)) return true;
        return false;
    }

    public boolean makeMove(int row, int col) {
        if (!isLegalMove(row, col)) return false;
        // make the move
        board[row][col] = currentPlayer;
        moves++;
        // update state, winner and current player
        checkIfDecided(row, col); // state might be altered
        if (!isDecided) {
            currentPlayer = currentPlayer == Player.CROSS ? Player.NOUGHT : Player.CROSS;
        }
        return true;
    }

    public Player[][] getCopyOfBoard() {
        Player[][] copy = new Player[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                copy[r][c] = board[r][c];
            }
        }
        return copy;
    }

    private void checkIfDecided(int row, int col) {
        // row
        int c;
        for (c = 0; c < SIZE; c++) {
            if (board[row][c] != currentPlayer) break;
        }
        if (c == SIZE) {
            isDecided = true;
            return;
        }
        // col
        int r;
        for (r = 0; r < SIZE; r++) {
            if (board[r][col] != currentPlayer) break;
        }
        if (r == SIZE) {
            isDecided = true;
            return;
        }
        // first diagonal
        if (row == col) {
            int pos;
            for (pos = 0; pos < SIZE; pos++) {
                if (board[pos][pos] != currentPlayer) break;
            }
            if (pos == SIZE) {
                isDecided = true;
                return;
            }
        }
        // second diagonal
        if (row + col == SIZE - 1) {
            int pos;
            for (pos = 0; pos < SIZE; pos++) {
                // [i][(n-1)-i]
                if (board[pos][SIZE - 1 - pos] != currentPlayer) break;
            }
            if (pos == SIZE) {
                isDecided = true;
                return;
            }
        }
        // draw?
        if (moves == SIZE * 3) {
            isDecided = true;
            currentPlayer = Player.NONE; // represents a draw if state DECIDED
            return;
        }
    }

    /*
    Singleton part
     */
    public static TicLogic getInstance() {
        if (ticLogic == null) {
            ticLogic = new TicLogic();
        }
        return ticLogic;
    }

    private static TicLogic ticLogic = null;

    private TicLogic() { // NB! Must be private - Singleton implementation
        board = new Player[SIZE][SIZE];
        reset();
    }
}
