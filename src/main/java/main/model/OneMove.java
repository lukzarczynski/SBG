package main.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import main.Point;

/**
 * Created by lukasz on 26.11.16.
 */
public class OneMove {

    public static final OneMove EMPTY_MOVE = new OneMove();

    private List<Move> moves = new ArrayList<>();
    private Point vector = Point.of(0, 0);
    private List<OneMove> parts = new ArrayList<>();

    public static OneMove of(List<Move> moves) {
        final OneMove om = new OneMove();
        om.setMoves(moves);
        return om;
    }

    public static Set<OneMove> parse(String regex, Point vector) {
        return parse(regex).stream()
                .peek(m -> m.setVector(vector))
                .collect(Collectors.toSet());
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
        return new HashSet<>(oneMoves);
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

    public void initializeParts() {

        List<List<Move>> moveList = new ArrayList<>();

        Move previous = null;
        List<Move> currentList = new ArrayList<>();
        for (Move current : this.getMoves()) {
            if (previous == null || current.equalsWithoutType(previous)) {
                currentList.add(current);
            } else if (!current.equals(previous)) {
                moveList.add(currentList);
                currentList = new ArrayList<>();
                currentList.add(current);
            }
            previous = current;
        }
        moveList.add(currentList);

        int i = 1;
        while (i < moveList.size()) {
            final List<Move> single = moveList.get(i);

            if (single.size() == 1) {
                Point singlePair = Point.of(single.get(0).getDx(), single.get(0).getDy());

                final List<Move> prevList = moveList.get(i - 1);
                Point anyPair = Point.of(prevList.get(0).getDx(), prevList.get(0).getDy());

                if (singlePair.equals(anyPair)) {
                    prevList.addAll(single);
                    moveList.remove(i);
                }
            } else {
                i++;
            }

        }

        Point curVector = Point.of(0, 0);
        for (List<Move> l : moveList) {
            OneMove om = OneMove.of(l);
            om.setVector(curVector);
            curVector = Point.of(
                    curVector.getX() + l.stream().map(Move::getDx).reduce(0, Integer::sum),
                    curVector.getY() + l.stream().map(Move::getDy).reduce(0, Integer::sum)
            );
            this.parts.add(om);
        }
    }

    public OneMove withoutPrefix(OneMove prefix) {
        final List<Move> resultMoves = new ArrayList<>();
        final List<Move> prefixMoves = prefix.getMoves();

        int x = 0;
        int y = 0;
        for (int i = 0; i < moves.size(); i++) {
            if (i < prefixMoves.size()) {
                x += prefixMoves.get(i).getDx();
                y += prefixMoves.get(i).getDy();
            } else {
                resultMoves.add(moves.get(i).copy());
            }
        }

        final OneMove result = new OneMove();
        result.setMoves(resultMoves);
        result.setVector(Point.of(x, y));
        return result;
    }

    public Optional<Move> getFirst() {
        return moves.isEmpty() ? Optional.empty() : Optional.of(moves.get(0));
    }

    public Point getVector() {
        return vector;
    }

    public void setVector(Point vector) {
        this.vector = vector;
    }

    public boolean isValid(int width, int height) {
        return isValid(width, height, vector.getX(), vector.getY());
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

    public List<OneMove> getParts() {
        return parts;
    }

    @Override
    public String toString() {
        return moves.stream().map(Move::toString).collect(Collectors.joining());
    }

    public boolean isValid(Point xy) {
        return isValid(xy.getKey(), xy.getValue());
    }


}
