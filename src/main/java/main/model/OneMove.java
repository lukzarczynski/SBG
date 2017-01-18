package main.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 26.11.16.
 */
public class OneMove {

    public static final OneMove EMPTY_MOVE = new OneMove();

    private List<Move> moves = new ArrayList<>();
    private Pair<Integer, Integer> vector = Pair.of(0, 0);

    public static OneMove of(List<Move> moves) {
        final OneMove om = new OneMove();
        om.setMoves(moves);
        return om;
    }

    public static Set<OneMove> parse(String regex, Pair<Integer, Integer> vector) {
        return parse(regex).stream().map(m -> {
            m.setVector(vector);
            return m;
        }).collect(Collectors.toSet());
    }

    public static Set<OneMove> parse(String regex) {
        if (regex.isEmpty()) {
            return new HashSet<>();
        }
        List<OneMove> oneMoves = new ArrayList<>();
        oneMoves.add(new OneMove());
        final String[] mm = regex.split("(?=\\()");
        for (String m : mm) {
            final List<List<Move>> parsedMoves = Move.parse(m);
            if (parsedMoves.size() == 1) {
                oneMoves.forEach(om -> om.getMoves().addAll(parsedMoves.get(0)));
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

    private static Set<OneMove> addAll(OneMove move, List<List<Move>> parsed) {
        return parsed.stream().map(m -> {
            final OneMove copy = move.copy();
            if (m.size() != 0) {
                copy.getMoves().addAll(m);
            }
            return copy;
        }).collect(Collectors.toSet());

    }

    public Pair<Integer, Integer> getVector() {
        return vector;
    }

    public void setVector(Pair<Integer, Integer> vector) {
        this.vector = vector;
    }

    public boolean isValid(int width, int height) {
        return isValid(width, height, vector.getKey(), vector.getValue());
    }

    public boolean isValid(int width, int height, int vectorX, int vectorY) {
        int x = vectorX;
        int y = vectorY;
        for (Move m : getMoves()) {
            x += m.getDx();
            y += m.getDy();

            if (Math.abs(x) >= width || Math.abs(y) >= height) {
                return false;
            }
        }
        return true;
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

    @Override
    public int hashCode() {
        return getMoves().hashCode();
    }

    @Override
    public boolean equals(Object o) {
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

    public boolean isValid(Pair<Integer, Integer> xy) {
        return isValid(xy.getKey(), xy.getValue());
    }


}
