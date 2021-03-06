package main.model;

import main.MoveUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 26.11.16.
 */
public class Piece {

    private String name;
    private Set<OneMove> moves;

    public static Piece parse(String regex, int width, int height) {
        final Piece piece = new Piece();

        final String moves = regex.substring(regex.indexOf("(")).replace("&", "");
        final String pieceName = StringUtils.deleteWhitespace(regex.substring(0, regex.indexOf("(")));
        final String[] movesArray = StringUtils.deleteWhitespace(moves).split("\\+");

        piece.setName(pieceName);
        piece.setMoves(Arrays.stream(movesArray)
                .map(OneMove::parse)
                .flatMap(Collection::stream)
                .filter(om -> StringUtils.isNoneEmpty(om.toString()))
                .filter(om -> om.isValid(width, height))
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

    @Override
    public String toString() {
        return String.format("%s %s &", name,
                moves.stream().map(OneMove::toString)
                        .filter(StringUtils::isNoneEmpty)
                        .collect(Collectors.joining(" + ")));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Piece piece = (Piece) o;

        return getMoves().equals(piece.getMoves());

    }

    @Override
    public int hashCode() {
        return getMoves().hashCode();
    }
}
