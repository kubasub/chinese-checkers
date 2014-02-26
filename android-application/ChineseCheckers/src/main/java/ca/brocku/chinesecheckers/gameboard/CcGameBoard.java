package ca.brocku.chinesecheckers.gameboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The implementation of GameBoard - This board being specifically for chinese checkers.
 *
 * Author: Peter Pobojewski
 * Student #: 4528311
 * Date: 2/13/2014
 */
public class CcGameBoard extends GameBoard{
    /**
     * Total number of spaces on the board
     */
    private Piece[][] board;

    /**
     * Construct a new board for a specified number of players.
     * @param numPlayers    The number of players in the game.
     */
    public CcGameBoard(int numPlayers) {
        this(numPlayers, null);
    }

    /**
     * Load a game that has already been started.
     * @param pieceList The state of all the pieces from the existing games.
     */
    public CcGameBoard(Piece[] pieceList) {
        this(pieceList, null);
    }

    /**
     * Initialize a new game with the specified number of players.
     * @param numPlayers    The number of players in the game. {2,3,4,6}
     * @param handler       The event handler to set for the game.
     */
    public CcGameBoard(int numPlayers, GameBoardEvents handler) {
        // Check to make sure the numPlayers argument is in range.
        if(!Arrays.asList(2, 3, 4, 6).contains(numPlayers)) {
            throw new IllegalArgumentException("The number of players must be {2,3,4,6}.");
        }

        board = constructBoard();
        populateNewGame(numPlayers);
        setGameBoardEventsHandler(handler);
    }

    /**
     * Load a game board from an initial set of pieces.
     * @param pieceList The list of pieces to initialize the board with.
     * @param handler   The event handler to set for the game.
     */
    public CcGameBoard(Piece[] pieceList, GameBoardEvents handler) {
        if((pieceList.length%10) != 0 || pieceList.length > 60) {
            throw new IllegalArgumentException("The number of pieces on the board doesn't match the number of players playing.");
        }

        board = constructBoard();
        loadBoard(pieceList);
        setGameBoardEventsHandler(handler);
    }


    /**
     * Populates the board with pieces in the starting location for each player.
     *
     * @param  playerNum Number of players playing.
     */
    private void populateNewGame(int playerNum) {
        int k, h, start;
        int[] playerList = generatePlayerList(playerNum);
        for(int x = 0; x<playerList.length; x++) {
            start = playerList[x];
            for(int i=0; i<4;i++) {
                for(int j=0; j<i+1; j++) {
                    k = getOffsetRow(start, i);
                    h = getOffsetIndex(start, j);
                    setPiece(new GridPosition(k, h), playerList[x]);
                }
            }
        }
    }

    /**
     * Fill the board with the specified set of pieces.
     * @param pieceList The list of pieces to initialize the board with.
     */
    private void loadBoard(Piece[] pieceList) {
        if(checkEmpty()) {
            for(int i = 0; i<pieceList.length; i++) {
                setPiece(pieceList[i].getPosition(), pieceList[i].getPlayerNumber());
            }
        }
        else throw new BoardNotEmptyException("Board is not empty, game cannot be loaded.");

    }

    /**
     * Checks to see if a player satisfies the win condition by checking that all positions in their
     * goal area have one of their pieces in that position.
     *
     * @param  playerNumber The player for which checking of win condition is required.
     *
     */
    private void checkWinCondition(int playerNumber) {
        boolean winCheck = true;
        int k, h;
        int winArea;
        if(playerNumber < 4) {
            winArea = playerNumber + 3;
        }
        else {
            winArea = playerNumber + 3 - 6;
        }
        for(int i=0; i<4;i++) {
            for(int j=0; j<i+1; j++) {
                k = getOffsetRow(winArea, i);
                h = getOffsetIndex(winArea, j);
                if(board[k][h]==null || board[k][h].getPlayerNumber()!=playerNumber){
                    winCheck = false;
                    break;
                }
            }
        }

        if(winCheck && this.gameBoardEventsHandler != null) {
            this.gameBoardEventsHandler.onPlayerWon(playerNumber);
        }
    }

    /**
     * Returns a constructed chinese checks board in the form of a ragged two dimensional Piece array
     *
     * @return  A ragged two dimensional Piece array representing a chinese checkers board
     */
    private Piece[][] constructBoard() {
        Piece[][] board = new Piece[17][];
        for(int i=0; i<board.length;i++) {
            board[i] = new Piece[ROW_POSITION_COUNT[i]];
        }
        return board;
    }

    /**
     * Return all the pieces that are on the board in no specific order.
     *
     * @return  All the pieces on the board.
     */
    @Override
    public Piece[] getAllPieces() {
        List<Piece> allPieces = new ArrayList<Piece>();
        for(int i=0; i<board.length;i++) {
            for(int j=0; j<board[i].length; j++) {
                if(board[i][j]!=null) {
                    allPieces.add(board[i][j]);
                }
            }
        }
        return allPieces.toArray(new Piece[allPieces.size()]);
    }
    /**
     * Move a piece from one position to another. Prints a statement if the move is invalid for any
     * reason.
     *
     * @param piece The piece to move.
     * @param to    The new position of the piece.
     */
    @Override
    public void movePiece(Piece piece, Position to) {
        if(isValidMove(piece, to)) {
            setPiece(to, piece.getPlayerNumber());
            int oldRow = piece.getPosition().getRow();
            int oldIndex = piece.getPosition().getIndex();
            board[oldRow][oldIndex] = null;
            this.checkWinCondition(piece.getPlayerNumber());
        }
        else{
            throw new IllegalMoveException("This piece cannot move to this position");
        }
    }

    /**
     * Get the piece that is at a position on the board.
     *
     * @param at    The position the piece is at.
     *
     * @return      The piece that was at the position specified, returns null if the position is
     *               empty or out of bounds.
     */
    @Override
    public Piece getPiece(Position at) {
        int row = at.getRow();
        int index = at.getIndex();

        try {
            return board[row][index];

        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Sets a piece at a given position for a given player. This method will mostly be used for
     * unit testing, and once unit testing is complete, for assistance in setting up the board.
     *
     * @param at    The Position that the player wishes to set the piece.
     * @param pl    The player that has ownership of the Piece.
     */
    public void setPiece(Position at, int pl) {
        int row = at.getRow();
        int index = at.getIndex();
        if(!isOccupied(at)) {
            board[row][index] = new GridPiece(at, pl);
        }
    }

    /**
     * Checks a given piece for any possible openings for that piece to move to. The process is
     * static and done almost on a case basis.
     *
     * @param forPiece  The piece to check positions for.
     *
     * @return          The list of positions the piece can move to. Or an empty array if there
     *                  is nowhere to move.
     */
    @Override
    public Position[] getPossibleMoves(Piece forPiece) {
        Position[] possibleMoves = new GridPosition[12];
        int row = forPiece.getPosition().getRow();
        int index = forPiece.getPosition().getIndex();
        int posindex = 0;
        { // immediateNeighbours
            if (row>12 || (row>3 && row<8)) { // if y is between 4 and 8 and greater than 12
                if(row==4) {
                    possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index-5));
                    posindex=posindex+1;
                    possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index-5+1));
                    posindex=posindex+1;
                }
                else if(row==13) {
                    possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index+4));
                    posindex=posindex+1;
                    possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index+4+1));
                    posindex=posindex+1;
                }
                else {
                    possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index));
                    posindex=posindex+1;
                    possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index+1));
                    posindex=posindex+1;
                }
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index-1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index+1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index-1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index));
                posindex=posindex+1;
            }
            else if (row==8) { // dead middle of the board
                possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index+1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index-1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index+1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index+1));
                posindex=posindex+1;
            }
            else { // if y is between 9 and 11 or less than 4
                possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index-1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row-1, index));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index-1));
                posindex=posindex+1;
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index+1));
                posindex=posindex+1;
                if(row==3) {
                    possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index+4));
                    posindex=posindex+1;
                    possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index+4+1));
                    posindex=posindex+1;
                }
                else if(row==12) {
                    possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index-5));
                    posindex=posindex+1;
                    possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index-5+1));
                    posindex=posindex+1;
                }
                else {
                    possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index));
                    posindex=posindex+1;
                    possibleMoves[posindex] = checkPosition(new GridPosition(row+1, index+1));
                    posindex=posindex+1;
                }
            }
        }
        {
            if (row>10 || (row>3 && row<8)) { // if y is between 4 and 8 and greater than 10
                if(row==11) {
                    if(isOccupied(new GridPosition(row+1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index-5));
                        posindex=posindex+1;
                    }
                }
                else if(row==12) {
                    if(isOccupied(new GridPosition(row+1, index-4))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index-6));
                        posindex=posindex+1;
                    }
                }
                else if(row==7) {
                    if(isOccupied(new GridPosition(row+1, index-1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index-1));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row+1, index-1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index-2));
                        posindex=posindex+1;
                    }
                }
            }
            else { // if y is between 9 and 10 or less than 4
                if(row==3) {
                    if(isOccupied(new GridPosition(row+1, index+4))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index+3));
                        posindex=posindex+1;
                    }
                }
                else if(row==2) {
                    if(isOccupied(new GridPosition(row+1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index+4));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row+1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index));
                        posindex=posindex+1;
                    }
                }
            }
        }
        { // down right
            if (row>10 || (row>3 && row<8)) { // if row is between 4 and 8 and greater than 10
                if(row==11) {
                    if(isOccupied(new GridPosition(row+1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index-3));
                        posindex=posindex+1;
                    }
                }
                else if(row==12) {
                    if(isOccupied(new GridPosition(row+1, index-4))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index-4));
                        posindex=posindex+1;
                    }
                }
                else if(row==7) {
                    if(isOccupied(new GridPosition(row+1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index+1));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row+1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index));
                        posindex=posindex+1;
                    }
                }
            }
            else { // if row is between 9 and 10 or less than 4
                if(row==2) {
                    if(isOccupied(new GridPosition(row+1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index+6));
                        posindex=posindex+1;
                    }
                }
                else if(row==3) {
                    if(isOccupied(new GridPosition(row+1, index+5))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index+5));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row+1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row+2, index+2));
                        posindex=posindex+1;
                    }
                }
            }
        } // end downRight
        { // upperLeft
            if (row>12 || (row>5 && row<9)) { // if row is between 4 and 8 and greater than 10
                if(row==14) {
                    if(isOccupied(new GridPosition(row-1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index+4));
                        posindex=posindex+1;
                    }
                }
                else if(row==13) {
                    if(isOccupied(new GridPosition(row-1, index+4))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index+3));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row-1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index));
                        posindex=posindex+1;
                    }
                }
            }
            else { // if row is between 9 and 10 or less than 4
                if(row==4) {
                    if(isOccupied(new GridPosition(row-1, index-5))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index-6));
                        posindex=posindex+1;
                    }
                }
                if(row==9) {
                    if(isOccupied(new GridPosition(row-1, index-1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index-1));
                        posindex=posindex+1;
                    }
                }
                else if(row==5) {
                    if(isOccupied(new GridPosition(row-1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index-5));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row-1, index-1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index-2));
                        posindex=posindex+1;
                    }
                }
            }
        } // end upperLeft
        { // upperRight
            if (row>12 || (row>5 && row<9)) { // if row is between 4 and 8 and greater than 10
                if(row==14) {
                    if(isOccupied(new GridPosition(row-1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index+6));
                        posindex=posindex+1;
                    }
                }
                else if(row==13) {
                    if(isOccupied(new GridPosition(row-1, index+5))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index+5));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row-1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index+2));
                        posindex=posindex+1;
                    }
                }
            }
            else { // if row is between 9 and 10 or less than 4
                if(row==4) {
                    if(isOccupied(new GridPosition(row-1, index-4))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index-4));
                        posindex=posindex+1;
                    }
                }
                if(row==9) {
                    if(isOccupied(new GridPosition(row-1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index+1));
                        posindex=posindex+1;
                    }
                }
                else if(row==5) {
                    if(isOccupied(new GridPosition(row-1, index+1))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index-3));
                        posindex=posindex+1;
                    }
                }
                else {
                    if(isOccupied(new GridPosition(row-1, index))) {
                        possibleMoves[posindex] = checkPosition(new GridPosition(row-2, index));
                        posindex=posindex+1;
                    }
                }
            }
        } // end upperRight
        { // leftAndRight
            if(isOccupied(new GridPosition(row, index-1))) {
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index-2));
                posindex=posindex+1;
            }
            if(isOccupied(new GridPosition(row, index+1))) {
                possibleMoves[posindex] = checkPosition(new GridPosition(row, index+2));
            }
        } // end leftAndRight
        return possibleMoves;
    }

    /**
     * Checks if a move is valid by verifying the possible moves for the given piece and evaluating
     * the given position against those positions. If the piece is within the possibleMoves array,
     * the move is valid. Otherwise it is invalid.
     *
     * @param piece The piece making the move.
     * @param to    The position the piece is trying to move to.
     * @return      True if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(Piece piece, Position to) {
        Position[] possibleMoves = getPossibleMoves(piece);
        for(int i=0; i<possibleMoves.length; i++) {
            if (possibleMoves[i]!=null) {
                if(possibleMoves[i].getRow() == to.getRow() && possibleMoves[i].getIndex() == to.getIndex()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a position on the game board is occupied or out of bounds.
     *
     * @param at    The position to be checked
     * @return      True if the position is out of bounds or occupied by another piece, false
     * otherwise
     */
    private boolean isOccupied (Position at) {
        int row = at.getRow();
        int index = at.getIndex();

        try {
            return board[row][index]!=null;

        }
        catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    /**
     * Assistant method for getPossibleMoves(), implements an easier way of checking if a position
     * is valid and simply returning the position if it is.
     *
     * @param at    The position to be checked
     * @return      Returns null if the position is occupied, returns "at" if the position is open
     */
    private Position checkPosition (Position at) {
        if(isOccupied(at)) {
            return null;
        }
        return at;
    }

    /**
     * An assisting function for checkWinCondition and populateBoard that returns an offset row value
     * based on the location of the targeted area and the current iteration of the loop.
     *
     * @param  location The targeted area, see supporting location documentation.
     * @param  i The iteration of the loop
     * @return returns the offset of the row.
     */
    private int getOffsetRow(int location, int i) {
        switch (location) {
            case 1: return 16-i;
            case 2: return 9+i;
            case 3: return 7-i;
            case 4: return i;
            case 5: return 7-i;
            case 6: return 9+i;
            default: return -1;
        }
    }

    /**
     * An assisting function for checkWinCondition and populateBoard that returns an offset column or index value
     * based on the location of the targeted area and the current iteration of the loop.
     *
     * @param  location The targeted area, see supporting location documentation.
     * @param  j The iteration of the loop
     * @return returns the offset of the index.
     */
    private int getOffsetIndex(int location, int j) {
        if(location < 5) {
            return j;
        }
        else {
            return j+9;
        }
    }
    private boolean checkEmpty() {
        for(int i=0; i<board.length;i++) {
            for(int j=0; j<board[i].length; j++) {
                if(board[i][j]!=null) {
                    return false;
                }
            }
        }
        return true;
    }
    private int[] generatePlayerList(int playerNum) {
        if(playerNum==2) {
            int[] playerList = {1,4};
            return playerList;
        }
        else if(playerNum==3) {
            int[] playerList = {1,3,5};
            return playerList;
        }
        else if(playerNum==4) {
            int[] playerList = {1,3,4,6};
            return playerList;
        }
        else if(playerNum==6) {
            int[] playerList = {1,2,3,4,5,6};
            return playerList;
        }
        else {
            throw new IllegalArgumentException("The number of players provided is not 2,3,4,or 6");
        }

    }
}

