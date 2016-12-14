package main;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by lukasz on 26.11.16.
 */
public class Piece {

    private String name;
    private Set<OneMove> moves;

    public static Piece parse(String regex) {
        final Piece piece = new Piece();

        final String moves = regex.substring(regex.indexOf("(")).replace("&", "");
        final String pieceName = StringUtils.deleteWhitespace(regex.substring(0, regex.indexOf("(")));
        final String[] movesArray = StringUtils.deleteWhitespace(moves).split("\\+");

        piece.setName(pieceName);
        piece.setMoves(Arrays.stream(movesArray)
                               .map(OneMove::parse)
                               .flatMap(Collection::stream)
                               .collect(Collectors.toSet()));

        return piece;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<OneMove> getMoves() {
        return moves;
    }

    public void setMoves(Set<OneMove> moves) {
        this.moves = moves;
    }


    public boolean containsAll(Set<OneMove> p) {
        return MoveUtil.containsAll(getMoves(), p);
    }

    public Set<OneMove> sum(Set<OneMove> p) {
        return MoveUtil.sum(getMoves(), p);
    }

    public Set<OneMove> intersection(Set<OneMove> p) {
        return MoveUtil.intersection(getMoves(), p);
    }

    public Set<OneMove> subtract(Set<OneMove> p) {
        return MoveUtil.subtract(getMoves(), p);
    }

    @Override public String toString() {
        return String.format("%s %s &", name,
                             moves.stream().map(OneMove::toString)
                                     .collect(Collectors.joining(" + ")));
    }
}
