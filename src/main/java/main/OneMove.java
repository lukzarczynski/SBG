package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 26.11.16.
 */
public class OneMove {

    private List<Move> moves = new ArrayList<>();

    public static OneMove of(List<Move> moves) {
        final OneMove om = new OneMove();
        om.setMoves(moves);
        return om;
    }

    public static Set<OneMove> parse(String regex) {
        List<OneMove> oneMoves = new ArrayList<>();
        oneMoves.add(new OneMove());
        final String[] mm = regex.split("(?=\\()");
        for (String m : mm) {
            final List<Move> parsedMoves = Move.parse(m);
            if (parsedMoves.size() == 1) {
                oneMoves.forEach(om -> om.getMoves().addAll(parsedMoves));
            } else {
                oneMoves =
                        oneMoves.stream()
                                .map(om -> addAll(om, parsedMoves))
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList());
            }
        }
        return oneMoves.stream()
                .collect(Collectors.toSet());
    }

    public static OneMove parse(int x, int y, String target) {
        return parse(String.format("(%s,%s,%s)", x, y, target)).iterator().next();
    }

    private static Set<OneMove> addAll(OneMove move, List<Move> parsed) {
        return parsed.stream().map(m -> {
            final OneMove copy = move.copy();
            if (m.getPower() != 0) {
                copy.getMoves().add(m);
            }
            return copy;
        }).collect(Collectors.toSet());

    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public OneMove copy() {
        final OneMove copy = new OneMove();
        copy.setMoves(
                getMoves().stream().map(Move::copy).collect(Collectors.toList()));
        return copy;
    }

    @Override public int hashCode() {
        return getMoves().hashCode();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OneMove oneMove = (OneMove) o;

        return getMoves().equals(oneMove.getMoves());

    }

    @Override
    public String toString() {
        return moves.stream().map(Move::toString).collect(Collectors.joining());
    }

}
