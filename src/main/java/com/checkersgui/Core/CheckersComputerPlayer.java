package com.checkersgui.Core;
import java.util.Random;

/** CheckersComputerPlayer class implements an automated turn for a simulated opponent
 * 
 * @author Eric Romero
 * @version 1.0 10/29/2023
 */
public class CheckersComputerPlayer {
    private Random generator;
    private boolean jump;
    private char[][] board;
    private int o_count;
    private int[][] gui;
    int[][] locations;
    boolean graphic;

    /** CheckersComputerPlayer constructor sets values based on the game being played.
     * 
     * @param board - The current state of the board.
     * @param o_count - The count of computer pieces present on the board.
     */
    public CheckersComputerPlayer(char[][] board, int o_count) {
        generator = new Random();
        jump = false;
        this.board = board;
        this.o_count = o_count;
        locations = new int[o_count][2];
        gui = new int[2][2];
    }

    /** compMove Executes the computer move after receiving information about piece locations.
     * 
     * @return - Returns the new state of the board.
     */
    public char[][] compMove() {
        graphic = false;
        int num=0, i=0, j=0;
        locations = movesAcquisition();
        CheckersLogic log_check = new CheckersLogic();

        if(jump==true) {
            return board;
        }
        if(o_count>1) {
            num = generator.nextInt(o_count-1);
            i = locations[num][0];
            j = locations[num][1];
            if(log_check.allowedMove(board, 'o', i, j, i-1, j-1) && log_check.allowedMove(board, 'o', i, j, i-1, j+1)) {
                num = generator.nextInt(1);
                if(num==1) {
                    board[i][j] = '_';
                    board[i-1][j-1] = 'o';
                }
                else {
                    board[i][j] = '_';
                    board[i-1][j+1] = 'o';
                }
            }
            else if(log_check.allowedMove(board, 'o', i, j, i-1, j-1)) {
                board[i][j] = '_';
                board[i-1][j-1] = 'o';
            }
            else if(log_check.allowedMove(board, 'o', i, j, i-1, j+1)) {
                board[i][j] = '_';
                board[i-1][j+1] = 'o';
            }
            else {
                compMove();
            }
        }
        return board;
    }

    /** guiCompMove is created to work better with GUI implementation
     * 
     * @return - returns desired move x and y value
     */
    public int[][] guiCompMove() {
        graphic = true;
        int num=0, i=0, j=0;
        locations = movesAcquisition();
        CheckersLogic log_check = new CheckersLogic();

        if(jump==true) {
            if(log_check.allowedMove(board, 'o', gui[0][0], gui[0][1], gui[0][0]-2, gui[0][1]-2)) {
                gui[1][0] = gui[0][0]-2;
                gui[1][1] = gui[0][1]-2;
            }
            else {
                gui[1][0] = gui[0][0]-2;
                gui[1][1] = gui[0][1]+2;
            }
            jump = false;
            return gui;
        }
        if(o_count>1) {
            num = generator.nextInt(o_count-1);
            i = locations[num][0];
            j = locations[num][1];
            gui[0][0]=i;
            gui[0][1]=j;
            if(log_check.allowedMove(board, 'o', i, j, i-1, j-1) && log_check.allowedMove(board, 'o', i, j, i-1, j+1)) {
                num = generator.nextInt(1);
                if(num==1) {
                    // board[i][j] = '_';
                    // board[i-1][j-1] = 'o';
                    gui[1][0] = gui[0][0]-1;
                    gui[1][1] = gui[0][1]-1;
                }
                else {
                    // board[i][j] = '_';
                    // board[i-1][j+1] = 'o';
                    gui[1][0] = gui[0][0]-1;
                    gui[1][1] = gui[0][1]+1;
                }
            }
            else if(log_check.allowedMove(board, 'o', i, j, i-1, j-1)) {
                // board[i][j] = '_';
                // board[i-1][j-1] = 'o';
                gui[1][0] = gui[0][0]-1;
                gui[1][1] = gui[0][1]-1;
            }
            else if(log_check.allowedMove(board, 'o', i, j, i-1, j+1)) {
                // board[i][j] = '_';
                // board[i-1][j+1] = 'o';
                gui[1][0] = gui[0][0]-1;
                gui[1][1] = gui[0][1]+1;
            }
            else {
                guiCompMove();
            }
        }
        return gui;
    }

    /** movesAcquisition retrieves the positions of pieces and looks for the possiblity to jump opponent piece.
     * 
     * @return - Returns the positions of the computer's checkers pieces.
     */
    public int[][] movesAcquisition() {
        int k=0;
        CheckersLogic log_check = new CheckersLogic();
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                if(board[i][j]=='o') {
                    if(log_check.canJump(board, 'o', i, j)) {
                        if(graphic==false){
                            mustJump(i, j);
                        }
                        gui[0][0]=i;
                        gui[0][1]=j;
                        jump = true;
                        return new int[0][0];
                    }
                    else {
                        locations[k][0] = i;
                        locations[k][1] = j;
                        k++;
                    }
                }
            }
        }
        return locations;
    }

    /** mustJump executes a jump over opponent piece & chain jumps if possible.
     * 
     * @param i - Index i of piece that can jump.
     * @param j - Index j of piece that can jump.
     */
    public void mustJump(int i, int j) {
        CheckersLogic log_check = new CheckersLogic();
        if(log_check.allowedMove(board, 'o', i, j, i-2, j-2)) {
            board[i][j] = '_';
            board[i-1][j-1] = '_';
            board[i-2][j-2] = 'o';
            i -= 2;
            j -= 2;
        }
        else {
            board[i][j] = '_';
            board[i-1][j+1] = '_';
            board[i-2][j+2] = 'o';
            i -= 2;
            j += 2;
        }
        if(log_check.canJump(board, 'o', i, j)) {
            mustJump(i, j);
        }
        else {
            return;
        }
    }
}
