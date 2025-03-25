package com.checkersgui.UI;

import java.util.Scanner;
import com.checkersgui.Core.CheckersComputerPlayer;
import com.checkersgui.Core.CheckersLogic;
import javafx.application.Application;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.application.Platform;

/** CheckersGUI class is the graphical implementation of my checkers game
 * 
 * @author Eric Romero
 * @version 1.0 10/29/2023
 */
public class CheckersGUI extends Application {

    private int TILE_SIZE = 100;
    private int WIDTH = 8;
    private int HEIGHT = 8;
    char[][] board;
    CheckersTextConsole checkers = new CheckersTextConsole();
    boolean comp;
    MoveResult compResult;

    private Tile[][] tiles = new Tile[WIDTH][HEIGHT];

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();
    
    /** createContent creates the board with pieces for Javafx
     * 
     * @return - returns root
     */
    public Parent createContent() {
        Scanner starter = new Scanner(System.in);
        checkers.freshBoard();
        System.out.println("Against computer: c or player: p");
        String temp = starter.nextLine();
        if(temp.charAt(0)=='c') {
            comp = true;
        }
        else {
            comp = false;
        }
        board = checkers.board;
        checkers.playerTurn = 'x';
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);
        starter.close();

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                tiles[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (board[7-y][x]=='o') {
                    //System.out.println("FOR RED x: " + x + "y: " + y);
                    piece = makePiece(PieceType.RED, x, y);
                }

                if (board[7-y][x]=='x') {
                    //System.out.println("FOR BLACK x: " + x + "y: " + y);
                    piece = makePiece(PieceType.BLACK, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }

        return root;
    }

    /** winConditions checks for win conditions in the Checkers game.
     * 
     */
    private void winConditions() {
        CheckersLogic logic_check = new CheckersLogic();
        if(checkers.x_count==0 || !logic_check.canMove(board, 'x')) {
            System.out.println("Player red wins!");
            Platform.exit();
        }
        else if(checkers.o_count==0 || !logic_check.canMove(board, 'o')) {
            System.out.println("Player black wins!");
            Platform.exit();
        }
        else {
            return;
        }
    }

    /** computerMove deals with computer moves in GUI class
     * 
     */
    private void computerMove() {
        CheckersComputerPlayer comp_play = new CheckersComputerPlayer(checkers.board, checkers.o_count);
        int[][] moveProp = comp_play.guiCompMove();
        // System.out.println(moveProp[0][1] + " " + (7-moveProp[0][0]));
        // System.out.println(moveProp[1][1] + " " + (7-moveProp[1][0]));
        Piece piece = tiles[moveProp[0][1]][(7-moveProp[0][0])].getPiece();
        compResult = tryMove(piece, moveProp[1][1], (7-moveProp[1][0]));
        computerMoveAction(piece, compResult, moveProp[0][1], 7-moveProp[0][0], moveProp[1][1], 7-moveProp[1][0]);
    }

    /** tryMove tests a moves validity and returns the result
     * 
     * @param piece - The piece that is desired to be moved
     * @param newX - The x value of the new placement
     * @param newY - The y value of the new placement
     * @return returns the type of move associated with request
     */
    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (tiles[newX][newY].hasPiece() || (newX + newY) % 2 == 0 || (piece.getType()==PieceType.RED && checkers.playerTurn=='x') 
        || (piece.getType()==PieceType.BLACK && checkers.playerTurn=='o')) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());
        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (tiles[x1][y1].hasPiece() && tiles[x1][y1].getPiece().getType() != piece.getType()) {
                return new MoveResult(MoveType.JUMP, tiles[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    /** Converts unusable coordinate into usable integer associated with position (x/y)
     * 
     * @param pixel - The unusable x or y value
     * @return - returns the usable integer
     */
    private int toBoard(double pixel) {
        return (int)(pixel + TILE_SIZE / 2) / TILE_SIZE;
    }

    /** computerMoveAction executes the computers move
     * 
     * @param piece - piece that will be moved
     * @param result - Type of move associated
     * @param oldX - old position x value
     * @param oldY - old position y value
     * @param newX - new position x value
     * @param newY -  new position y value
     */
    private void computerMoveAction(Piece piece, MoveResult result, int oldX, int oldY, int newX, int newY) {
        switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    checkers.viewBoard();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    tiles[oldX][oldY].setPiece(null);
                    tiles[newX][newY].setPiece(piece);
                    checkers.movePiece(7-oldY,oldX,7-newY,newX);
                    checkers.switchPlayer();
                    winConditions();
                    checkers.viewBoard();
                    break;
                case JUMP:
                int oldi=7-oldY, oldj=oldX, newi=7-newY, newj=newX;
                    piece.move(newX, newY);
                    tiles[oldX][oldY].setPiece(null);
                    tiles[newX][newY].setPiece(piece);
                    checkers.movePiece(oldi, oldj, newi, newj);
                    if(newj-oldj>0) {
                        checkers.board[oldi-1][oldj+1] = '_';
                        }
                    else {
                        checkers.board[oldi-1][oldj-1] = '_';
                    }
                    checkers.x_count--;
                    
                    Piece otherPiece = result.getPiece();
                    checkers.board[7-(oldY)-1][oldX+(newX-oldX)] = '_';
                    tiles[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    checkers.switchPlayer();
                    winConditions();
                    checkers.viewBoard();
                    System.out.println("Computer: Wow, this is too easy..");
                    break;
            }
    }

    /** start is the necessary start function for javafx
     * 
     */
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        primaryStage.setTitle("CheckersApp");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** makePiece makes pieces when requested and also executes player moves based on mouse swipes
     * 
     * @param type - Color of piece (RED or BLACK)
     * @param x - x value of piece to be made
     * @param y - y value of piece to be made
     * @return
     */
    private Piece makePiece(PieceType type, int x, int y) {

        Piece piece = new Piece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result;

            if (newX < 0 || newY < 0 || newX >= WIDTH || newY >= HEIGHT) {
                result = new MoveResult(MoveType.NONE);
            } else {
                result = tryMove(piece, newX, newY);
            }

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    tiles[x0][y0].setPiece(null);
                    tiles[newX][newY].setPiece(piece);
                    checkers.movePiece(7-y0,x0,7-newY,newX);
                    checkers.switchPlayer();
                    if(checkers.playerTurn=='o' && comp==true) {
                        computerMove();
                    }
                    winConditions();
                    break;
                case JUMP:
                    int oldi=7-y0, oldj=x0, newi=7-newY, newj=newX;

                    piece.move(newX, newY);
                    tiles[x0][y0].setPiece(null);
                    tiles[newX][newY].setPiece(piece);
                    checkers.movePiece(oldi, oldj, newi, newj);
                    if(checkers.playerTurn=='x') {
                        if(newj-oldj>0) {
                            checkers.board[oldi+1][oldj+1] = '_';
                        }
                        else {
                            checkers.board[oldi+1][oldj-1] = '_';
                        }
                        checkers.o_count--;
                    }
                    else {
                        if(newj-oldj>0) {
                            checkers.board[oldi-1][oldj+1] = '_';
                        }
                        else {
                            checkers.board[oldi-1][oldj-1] = '_';
                        }
                        checkers.x_count--;
                    }
                    
                    Piece otherPiece = result.getPiece();
                    tiles[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    checkers.switchPlayer();
                    if(checkers.playerTurn=='o' && comp==true) {
                        computerMove();
                    }
                    winConditions();
                    break;
            }
        });

        return piece;
    }

/** MoveResult class that can store types of moves
 * 
 */
class MoveResult {

    private MoveType type;

    /** getType returns type of move
     * 
     * @return - type of move
     */
    public MoveType getType() {
        return type;
    }
    
    private Piece piece;
    
    /** getPiece returns the piece associated with MoveResult
     * 
     * @return - returns the piece
     */
    public Piece getPiece() {
        return piece;
    }

    /** MoveResult sets type then sets piece to null
     * 
     * @param type - type of move
     */
    public MoveResult(MoveType type) {
        this(type, null);
    }

    /** MoveResult sets type and piece to input
     * 
     * @param type - type of move
     * @param piece - piece associated
     */
    public MoveResult(MoveType type, Piece piece) {
        this.type = type;
        this.piece = piece;
    }
}

/** enum MoveType makes move types easier to label and read
 * 
 */
enum MoveType {
    NONE, NORMAL, JUMP
}

/** Piece class tracks piece locations as well as builds pieces
 * 
 */
class Piece extends StackPane {

    private PieceType type;

    private double mouseX, mouseY;
    private double oldX, oldY;
    
    /** getType returns the type of the piece
     * 
     * @return
     */
    public PieceType getType() {
        return type;
    }

    /** getOldX returns old x location of piece
     * 
     * @return
     */
    public double getOldX() {
        return oldX;
    }

    /** getOldY returns old y location of piece
     * 
     * @return
     */
    public double getOldY() {
        return oldY;
    }

    /** Piece creates pieces and assigns values upon clicking them
     * 
     * @param type - type of piece
     * @param x - x value of where piece is to be located
     * @param y - y value of where piece is to be located
     */
    public Piece(PieceType type, int x, int y) {
        this.type = type;

        move(x, y);

        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.32, TILE_SIZE * 0.32);
        ellipse.setFill(type == PieceType.RED ? Color.valueOf("#c40003") : Color.valueOf("#000000"));

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.32 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.32 * 2) / 2);

        getChildren().addAll(ellipse);

        setOnMousePressed(e -> {
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - mouseX + oldX, e.getSceneY() - mouseY + oldY);
        });
    }

    /** move moves the piece
     * 
     * @param x - x value of where to be moved
     * @param y - y value of where to be moved
     */
    public void move(int x, int y) {
        oldX = x * TILE_SIZE;
        oldY = y * TILE_SIZE;
        relocate(oldX, oldY);
    }

    /** abortMove cancels the move and puts the piece back at it's original location
     * 
     */
    public void abortMove() {
        relocate(oldX, oldY);
    }
}

/** PieceType makes reading/assigning piece type easier
 * 
 */
enum PieceType {
    RED(1), BLACK(-1);

    final int moveDir;
    
    /** Assigns move direction to piece type
     * 
     * @param moveDir - which direction a piece can move
     */
    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }
}

/** Tile creates/is each square on the checkerboard
 * 
 */
class Tile extends Rectangle {

    private Piece piece;

    /** Checks if piece is present on tile
     * 
     * @return - returns true if there is a piece present
     */
    public boolean hasPiece() {
        return piece != null;
    }

    /** getPiece retrieves the piece associated with the tile
     * 
     * @return
     */
    public Piece getPiece() {
        return piece;
    }
    
    /** setPiece sets the piece associated with the tile
     * 
     * @param piece
     */
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /** Tile creates a tile of particular color for checkerboard
     * 
     * @param light - true if piece is the lighter of the two
     * @param x - x value of the tile location
     * @param y - y value of the tile location
     */
    public Tile(boolean light, int x, int y) {
        setWidth(TILE_SIZE);
        setHeight(TILE_SIZE);

        relocate(x * TILE_SIZE, y * TILE_SIZE);

        setFill(light ? Color.valueOf("#FFDAB9") : Color.valueOf("#8B4513"));
    }
}
}