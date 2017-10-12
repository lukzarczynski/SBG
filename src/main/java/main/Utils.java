package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BinaryOperator;

import main.model.Move;
import main.model.OneMove;

/**
 * Created by lukza on 21.01.2017.
 */
public final class Utils {

    public static final BinaryOperator<Point> PAIR_SUM
            = (p1, p2) -> Point.of(p1.getKey() + p2.getKey(), p1.getValue() + p2.getValue());

    public static <T> Set<T> setOf(T... o) {
        final Set<T> set = new HashSet<>();
        Collections.addAll(set, o);
        return set;
    }

    public static <T> Set<T> sum(Set<T> s1, Set<T> s2) {
        final Set<T> set = new HashSet<>(s1);
        set.addAll(s2);
        return set;
    }

    public static <T> Set<T> sum(Set<T> s1, T s2) {
        final Set<T> set = new HashSet<>(s1);
        set.add(s2);
        return set;
    }

    public static OneMove joinMoves(OneMove o1, OneMove o2) {
        final OneMove om = new OneMove();
        om.setMoves(new ArrayList<>(o1.getMoves()));
        om.getMoves().addAll(o2.getMoves());
        return om;
    }

    public static Point asVector(OneMove omPrefix) {
        int x = 0;
        int y = 0;
        for (Move m : omPrefix.getMoves()) {
            x += m.getDx();
            y += m.getDy();
        }
        return Point.of(x, y);
    }

    public static Point getNewDimensions(Point oldXY, OneMove prefix) {
        Point prefixAsVector = asVector(prefix);
        return Point.of(
                oldXY.getKey() - Math.abs(prefixAsVector.getKey()),
                oldXY.getValue() - Math.abs(prefixAsVector.getValue())
        );

    }

    public static Point sum(Point currentVector, Point of) {
        return Point.of(
                currentVector.getX() + of.getX(),
                currentVector.getY() + of.getY()
        );
    }
}
