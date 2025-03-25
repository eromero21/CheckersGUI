package com.checkersgui.UI;
import com.checkersgui.Core.CheckersLogic;
import com.checkersgui.Core.CheckersComputerPlayer;
import java.util.Scanner;
//JAVAFX
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

/** CheckersTextConsole class is the actual operation of the Checkers game.
 * 
 * @author Eric Romero
 * @version 1.1 10/29/2023
 */
public class CheckersTextConsole extends Application{
    public char[][] board;
    public char playerTurn;
    public char opponent;
    public int o_count;
    public int x_count;
    public boolean comp = false;

    public CheckersTextConsole() {
    }
    /** newGame sets values to variables and call for a fresh board. It determines player or computer opponent then starts game.
     * 
     */
    public void newGame() {
        Scanner myScan = new Scanner(System.in);
        playerTurn = 'x';
        opponent = 'o';
        freshBoard();
        System.out.println("If you'd like to play with a computer input y, if not enter n:");
        String temp = myScan.nextLine();
        if(temp.charAt(0)=='y') {
            comp = true;
        }
        coreGame();
    }

    /** coreGame is the actual playing of the game. It will allow people or bot to take their turn, check validity, then
     * call for the move to be made.
     * 
     */
    public void coreGame() {
        CheckersLogic logic_check = new CheckersLogic();
        Scanner myScan = new Scanner(System.in);
        int fromi, fromj, toi, toj;

        if(o_count==0) {
            System.out.println("x wins! End game.");
            myScan.close();
            return;
        }
        if(x_count==0) {
            System.out.println("o wins! End game.");
            myScan.close();
            return;
        }

        if(!logic_check.canMove(board, playerTurn)) {
            if(playerTurn=='x') {
                System.out.println("Player o wins! Game End.");
            }
            else {
                System.out.println("Player x wins! Game End.");
                return;
            }
        }
        if(comp==false || playerTurn=='x') {
            viewBoard();
            System.out.println("Player " + playerTurn + "'s turn. Please choose your move in the format: 3c-4d");
            String temp = myScan.nextLine();

            if(temp.length()!=5) {
                System.out.println("Format not accepted. Try again.");
                coreGame();
            }

            temp.toLowerCase();
            fromi = temp.charAt(0) - 49;
            fromj = temp.charAt(1) - 97;
            toi = temp.charAt(3) - 49;
            toj = temp.charAt(4) - 97;

            if(logic_check.allowedMove(board, playerTurn, fromi, fromj, toi, toj)) {
                movePiece(fromi, fromj, toi, toj);

                if(Math.abs(fromi-toi)==2 && Math.abs(fromj-toj)==2) {
                    if(playerTurn=='x') {
                        if(toj-fromj<0) {
                            board[fromi+1][fromj-1] = '_';
                        }
                        else {
                            board[fromi+1][fromj+1] = '_';
                        }
                        o_count--;
                    }
                    else {
                        if(toj-fromj<0) {
                            board[fromi-1][fromj-1] = '_';
                        }
                        else {
                            board[fromi-1][fromj+1] = '_';
                        }
                        x_count--;
                    }

                    if(logic_check.canJump(board, playerTurn, toi, toj)) {
                        chainJump(toi,toj);
                    }
                }

                switchPlayer();
                coreGame();
            }
        else {
            System.out.println("Invalid move. Try again!");
            coreGame();
        }
    }
    else {
        CheckersComputerPlayer comp_play = new CheckersComputerPlayer(board, o_count);
        board = comp_play.compMove();
        switchPlayer();
        coreGame();
    }

    }

    /** chainJump is called when a jump is made and another jump is possible.
     * 
     * @param i - is the index i in board[i][j] associated with the piece being tracked.
     * @param j - is the index j in board[i][j] associated with the piece being tracked.
     */
    public void chainJump(int i, int j) {
        Scanner myScan = new Scanner(System.in);
        CheckersLogic logic_check = new CheckersLogic();
        int toi, toj;

        viewBoard();
        System.out.println("The ability to jump again has been detected." +
        " Please select where you'd like to jump (ex: 3c) or input n to stop your turn.");
        String temp = myScan.nextLine();

        temp.toLowerCase();
        if(temp.charAt(0)=='n') {
            switchPlayer();
            coreGame();
        }
        else {
            toi = temp.charAt(0) - 49;
            toj = temp.charAt(1) - 97;

            if(logic_check.allowedMove(board, playerTurn, i, j, toi, toj)) {
                movePiece(i, j, toi, toj);
                if(playerTurn == 'x') {
                    if(toj-j<0) {
                        board[i+1][j-1] = '_';
                    }
                    else {
                        board[i+1][j+1] = '_';
                    }
                    o_count--;
                }
                else {
                    if(toj-j<0) {
                        board[i-1][j-1] = '_';
                    }
                    else {
                        board[i-1][j+1] = '_';
                    }
                    x_count--;
                }

                if(logic_check.canJump(board, playerTurn, toi, toj)) {
                    chainJump(toi, toj);
                }
                else {
                    switchPlayer();
                    coreGame();
                }
            }
            else {
                System.out.println("Invalid move! Try again!");
                chainJump(i, j);
            }
        }

    }

    /** movePiece moves the desired piece from one position to another valid position.
     * 
     * @param fromI - is the index i in board[i][j] associated with the current position.
     * @param fromJ - is the index j in board[i][j] associated with the current position.
     * @param toI - is the index i in board[i][j] associated with the desired position.
     * @param toJ - is the index j in board[i][j] associated with the desired position.
     */
    public void movePiece(int fromI, int fromJ, int toI, int toJ) {
        if(playerTurn == 'x') {
            board[fromI][fromJ] = '_';
            board[toI][toJ] = 'x';
        }
        else {
            board[fromI][fromJ] = '_';
            board[toI][toJ] = 'o';
        }
    }

    /** switchPlayer switches the playerTurn variable to the other player. 
     * 
     */
    public void switchPlayer() {
        if(playerTurn == 'x') {
            playerTurn = 'o';
            opponent = 'x';
        }
        else {
            playerTurn = 'x';
            opponent = 'o';
        }
    }

    /** freshBoard makes a fresh playing board with the pieces at their starting positions.
     * 
     */
    public void freshBoard() {
        o_count = 12;
        x_count = 12;
        board = new char[8][8];

        for(int i=1; i<=8; i++) {
            for(int j=1; j<=8; j++) {
                if(i%2==0 && j%2==0 && i<4) {
                    board[i-1][j-1] = 'x'; //x
                }
                else if(i%2!=0 && j%2!=0 && i<4) {
                    board[i-1][j-1] = 'x'; //x
                }
                else if(i%2==0 && j%2==0 && i>5) {
                    board[i-1][j-1] = 'o'; //o
                }
                 else if(i%2!=0 && j%2!=0 && i>5) {
                    board[i-1][j-1] = 'o'; //o
                }
                else {board[i-1][j-1] = '_';}
            }
        }
    }
    
    /** viewBoard prints the board with the current piece locations.
     * 
     */
    public void viewBoard() {
        
        for(int i=7; i>=0; i--) {
            System.out.print(i+1 + " |");
            for(int j=0; j<8; j++) {
                System.out.print(" " + board[i][j] + " |");
            }
            System.out.println("\n");
        }
        System.out.println("    a   b   c   d   e   f   g   h");
    }

    
    /** main method used for testing
     * @param args - main method
     */
    public static void main(String[] args) {
        System.out.println("Terminal based: t or GUI based: g");
        Scanner starter = new Scanner(System.in);
        String temp = starter.nextLine();
        if(temp.charAt(0)=='t') {
            CheckersTextConsole test_game = new CheckersTextConsole();
            test_game.newGame();
        }
        else if(temp.charAt(0)=='g') {
            launch(args);
        }
        else {
            System.out.println("Invalid input.");
        }
        starter.close();
    }

    public void start(Stage primaryStage) throws Exception  {
        CheckersGUI gui = new CheckersGUI();
        primaryStage.setTitle("Checkers Game");
        primaryStage.setScene(new Scene(gui.createContent()));
        primaryStage.show();
    }
}