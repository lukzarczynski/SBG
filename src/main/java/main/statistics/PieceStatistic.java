package main.statistics;

import main.model.Piece;

/**
 * Created by lukza on 24.01.2017.
 */
public class PieceStatistic {

    private Piece piece;
    private Integer value;
    private String description;
    private String file;

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PieceStatistic that = (PieceStatistic) o;

        return piece.equals(that.piece);
    }

    @Override
    public int hashCode() {
        return piece.hashCode();
    }
}
