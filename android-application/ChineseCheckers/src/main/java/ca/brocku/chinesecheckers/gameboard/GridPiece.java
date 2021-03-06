package ca.brocku.chinesecheckers.gameboard;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * The implementation of Piece used to represent the pieces players may use on a chinese checkers
 * board.
 *
 * Author: Peter Pobojewski
 * Student #: 4528311
 * Date: 2/13/2014
 */
public class GridPiece implements Piece, Serializable {
    private Position position;
    private int player;

    public GridPiece (Position pos, int pl) {
        position = pos;
        player = pl;
    }

    /**
     * Returns the position of a piece on the board.
     *
     * @return  Position of piece
     */
    @Override
    public Position getPosition() {
        return position;
    }

    /**
     * Returns the owner of the piece.
     *
     * @return  Player that owns the piece.
     */
    @Override
    public int getPlayerNumber() {
        return player;
    }

    /**
     * Sets a new position for a piece.
     *
     */
    public void setPosition(Position at) {
        position=at;
    }
    /**
     * Sets a new player for a piece (May not be needed).
     *
     */
    public void setPlayer(int np) {
        player = np;
    }


    /**
     * Constructor for parcel.
     * @param p
     */
    private GridPiece(Parcel p) {
        position = p.readParcelable(Position.class.getClassLoader());
        player = p.readInt();
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(position, 0);
        dest.writeInt(player);
    }

    /**
     * Recreate this instance
     */
    public static final Parcelable.Creator<GridPiece> CREATOR =
            new Parcelable.Creator<GridPiece>() {

        public GridPiece createFromParcel(Parcel in) {
            return new GridPiece(in);
        }

        public GridPiece[] newArray(int size) {
            return new GridPiece[size];
        }
    };
}
