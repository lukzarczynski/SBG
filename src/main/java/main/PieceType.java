package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 27.11.16.
 */
public class PieceType {

    public static final List<PieceType> PIECES;

    private static final String ROOK_UP = or("(0,1,e)^{0,1,2,3,4,5,6}(0,1,p)", "(0,1,e)^{1,2,3,4,5,6,7}");
    private static final String ROOK_DOWN = or("(0,-1,e)^{0,1,2,3,4,5,6}(0,-1,p)", "(0,-1,e)^{1,2,3,4,5,6,7}");
    private static final String ROOK_LEFT = or("(-1,0,e)^{0,1,2,3,4,5,6}(-1,0,p)", "(-1,0,e)^{1,2,3,4,5,6,7}");
    private static final String ROOK_RIGHT = or("(1,0,e)^{0,1,2,3,4,5,6}(1,0,p)", "(1,0,e)^{1,2,3,4,5,6,7}");
    private static final String BISHOP_UP_RIGHT = or("(1,1,e)^{0,1,2,3,4,5,6}(1,1,p)", "(1,1,e)^{1,2,3,4,5,6,7}");
    private static final String BISHOP_DOWN_RIGHT = or("(1,-1,e)^{0,1,2,3,4,5,6}(1,-1,p)", "(1,-1,e)^{1,2,3,4,5,6,7}");
    private static final String BISHOP_UP_LEFT = or("(-1,1,e)^{0,1,2,3,4,5,6}(-1,1,p)", "(-1,1,e)^{1,2,3,4,5,6,7}");
    private static final String BISHOP_DOWN_LEFT = or("(-1,-1,e)^{0,1,2,3,4,5,6}(-1,-1,p)", "(-1,-1,e)^{1,2,3,4,5,6,7}");

    static {
        final String rook = or(ROOK_DOWN, ROOK_LEFT, ROOK_RIGHT, ROOK_UP);
        final String bishop = or(BISHOP_DOWN_LEFT, BISHOP_DOWN_RIGHT, BISHOP_UP_LEFT, BISHOP_UP_RIGHT);

        PIECES = new ArrayList<>();
        PIECES.add(PieceType.of("Queen", "Q " + or(bishop, rook) + " &"));
        PIECES.add(PieceType.of("Bishop", "B " + bishop + " &"));
        PIECES.add(PieceType.of("Rook", "R " + rook + " &"));
        PIECES.add(PieceType.of(
                "Knight", "N (2,1,e) + (2,1,p) + " +
                        "(2,-1,e) + (2,-1,p) + " +
                        "(-2,1,e) + (-2,1,p) + " +
                        "(-2,-1,e) + (-2,-1,p) + " +
                        "(1,2,e) + (1,2,p) + " +
                        "(1,-2,e) + (1,-2,p) + " +
                        "(-1,2,e) + (-1,2,p) + " +
                        "(-1,-2,e) + (-1,-2,p) &"));
        PIECES.add(PieceType.of("Pawn",
                "P " + or("(0,1,e)",
                        "(-1,1,p)",
                        "(1,1,p)",
                        "(0,1,e)(0,5,e)(0,-4,e)",
                        "(0,1,e)(0,5,p)(0,-4,e)")
                        + "& "));
    }

    private Piece piece;
    private String description;

    public PieceType(Piece piece, String description) {
        this.piece = piece;
        this.description = description;
    }

    private static String or(String... args) {
        return Arrays.stream(args).collect(Collectors.joining(" + "));
    }

    public static PieceType of(String description, String regex) {
        return new PieceType(Piece.parse(regex), description);
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
