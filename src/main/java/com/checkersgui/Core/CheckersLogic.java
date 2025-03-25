package com.checkersgui.Core;

/**
 * CheckersLogic class is the logic behind operations in the Checkers game.
 * @author Eric Romero
 * @version 1.0 10/22/2023
 */
public class CheckersLogic {
    public CheckersLogic(){};

    /** canMove checks if the current player has any possible moves.
     * 
     * @param board - The current state of the Checkers board.
     * @param player - The current player's turn
     * @return - Returns true if the player can make any move, false if no move is possible.
     */
    public boolean canMove(char[][] board, char player) {
        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                if(board[i][j]==player) {
                    if(allowedMove(board, player, i, j, i+1, j+1) || allowedMove(board, player, i, j, i-1, j-1)) {
                        return true;
                    }
                    else if(allowedMove(board, player, i, j, i+1, j-1) || allowedMove(board, player, i, j, i-1, j+1)) {
                        return true;
                    }
                    else if(allowedMove(board, player, i, j, i-2, j+2) || allowedMove(board, player, i, j, i+2, j-2)) {
                        return true;
                    }
                    else if(allowedMove(board, player, i, j, i+2, j+2) || allowedMove(board, player, i, j, i-2, j-2)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** canJump checks if jumping is possible for a certain piece.
     * 
     * @param board - Current state of the Checkers board
     * @param playerTurn - Current player's turn.
     * @param i - Index i for board[i][j] of particular piece.
     * @param j - Index j for board[i][j] of particular piece.
     * @return - Returns true if jump is possible, false if no jump is possible.
     */
    public boolean canJump(char[][] board, char playerTurn, int i, int j) {
        if(playerTurn == 'x') {
            if(allowedMove(board, playerTurn, i, j, i+2, j-2) && board[i+1][j-1]=='o') {
                return true;
            }
            else if(allowedMove(board, playerTurn, i, j, i+2, j+2) && board[i+1][j+1]=='o') {
                return true;
            }
            else {return false;}
        }
        else {
            if(allowedMove(board, playerTurn, i, j, i-2, j-2) && board[i-1][j-1]=='x') {
                return true;
            }
            else if(allowedMove(board, playerTurn, i, j, i-2, j+2) && board[i-1][j+1]=='x') {
                return true;
            }
            else {return false;}
        }
    }

    /** allowedMove checks if a certain move for a particular piece is valid.
     * 
     * @param board - Current state of the checkers board.
     * @param player - Current player's turn
     * @param fromI - is the index i in board[i][j] associated with the current position.
     * @param fromJ - is the index j in board[i][j] associated with the current position.
     * @param toI - is the index i in board[i][j] associated with the desired position.
     * @param toJ - is the index j in board[i][j] associated with the desired position.
     * @return - Return true if it is a valid move, false if it is not.
     */
    public boolean allowedMove(char[][] board, char player, int fromI, int fromJ, int toI, int toJ) {
        char opponent;
        //Checking bounds
        if(toI>7 || toI<0 || toJ>7 || toJ<0) {
            return false;
        }

        //
        if(player=='x') {
            opponent = 'o';

            //Making sure x isn't trying to move wrong direction
            if(fromI>=toI) { return false; }

            //Making sure space isn't occupied
            if(board[toI][toJ]!='_') {
                return false;
            }
            //Checking if there is a piece to jump in scenario and returning accordingly
            else if((toI-fromI)==2 && Math.abs(fromJ-toJ)==2) {
                if(toJ-fromJ>0) {
                    if(board[fromI+1][fromJ+1]==opponent) {
                        return true;
                    }
                    else {return false;}
                }
                else {
                    if(board[fromI+1][fromJ-1]==opponent) {
                        return true;
                    }
                    else {return false;}
                }
            }

            //Checking if jump is within one space
            else if((toI-fromI)<2 && Math.abs(toJ-fromJ)<2) {
                return true;
            }

            else {return false;}
        }
        else {
            opponent = 'x';

            //Making sure o isn't trying to move wrong direction
            if(fromI<=toI) { return false; }

            //Making sure space isn't occupied
            if(board[toI][toJ]!='_') {
                return false;
            }
            //Checking if there is a piece to jump in scenario and returning accordingly
            else if((fromI-toI)==2 && Math.abs(fromJ-toJ)==2) {
                if(toJ-fromJ>0) {
                    if(board[fromI-1][fromJ+1]==opponent) {
                        return true;
                    }
                    else {return false;}
                }
                else {
                    if(board[fromI-1][fromJ-1]==opponent) {
                        return true;
                    }
                    else {return false;}
                }
            }

            //Checking if jump is within one space
            else if((fromI-toI)<2 && Math.abs(toJ-fromJ)<2) {
                return true;
            }

            else {return false;}
        }
    }
}