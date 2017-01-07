package main.tree;

import main.operator.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lukasz on 10.12.16.
 */
public final class Resolvers {

    public static final Set<Set<Operator>> ops;
    public static final List<Set<Operator>> opsSorted;
    private static final Comparator<Set<Operator>> setComparator = (op1, op2) -> {
        Integer sum1 = op1.stream().map(Operator::getValue).reduce(0, Integer::sum);
        Integer sum2 = op2.stream().map(Operator::getValue).reduce(0, Integer::sum);
        return Integer.compare(sum1, sum2);
    };

    private static final Map<Class<? extends Operator>, Set<Class<? extends Operator>>> RESTRICTIONS;

    static {
        ops = new HashSet<>();
        RESTRICTIONS = new HashMap<>();

        Set<Operator> allOperators = setOf(
                new None(),
                new Forward(),
                new Backwards(),
                new Sideways(),
                new Diagonal(),
                new Horizontal(),
                new Orthogonal(),
                new Or(new Forward(), new Horizontal()),
                new Or(new Backwards(), new Horizontal()),
                new OnlyCapture(),
                new WithoutCapture(),
                new OnlyEven(),
                new OnlyOdd(),
                new RestrictedToX(1),
                new RestrictedToX(2),
                new RestrictedToX(3),
                new RestrictedToX(4),
                new RestrictedToX(5),
                new RestrictedToX(6),
                new OverEnemyPieceInstead(),
                new OverOwnPieceInstead(),
                new SelfCaptureInstead()
        );


        RESTRICTIONS.put(Forward.class, setOf(None.class, Backwards.class));
        RESTRICTIONS.put(Backwards.class, setOf(None.class, Forward.class));
        RESTRICTIONS.put(Sideways.class, setOf(None.class, Orthogonal.class, Horizontal.class));
        RESTRICTIONS.put(OnlyCapture.class, setOf(None.class, WithoutCapture.class));
        RESTRICTIONS.put(WithoutCapture.class, setOf(None.class, OnlyCapture.class));
        RESTRICTIONS.put(Diagonal.class, setOf(None.class, Horizontal.class, Orthogonal.class));
        RESTRICTIONS.put(Orthogonal.class, setOf(None.class, Sideways.class, Diagonal.class, Horizontal.class));
        RESTRICTIONS.put(Horizontal.class, setOf(None.class, Sideways.class, Diagonal.class, Orthogonal.class));
        RESTRICTIONS.put(OnlyEven.class, setOf(None.class, OnlyOdd.class));
        RESTRICTIONS.put(OnlyOdd.class, setOf(None.class, OnlyEven.class));
        RESTRICTIONS.put(RestrictedToX.class, setOf(None.class, RestrictedToX.class));
        RESTRICTIONS.put(OverEnemyPieceInstead.class, setOf(None.class, OverOwnPieceInstead.class));
        RESTRICTIONS.put(OverOwnPieceInstead.class, setOf(None.class, OverEnemyPieceInstead.class));

        allOperators.forEach(o1 -> {
            ops.add(setOf(o1));

            final Set<Class<? extends Operator>> r1 = RESTRICTIONS.getOrDefault(o1.getClass(), setOf(o1.getClass()));

            allOperators.stream().filter(o2 -> !r1.contains(o2.getClass())).forEach(o2 -> {
                ops.add(setOf(o1, o2));

                final Set<Class<? extends Operator>> r2 = RESTRICTIONS.getOrDefault(o2.getClass(), setOf(o2.getClass()));

                allOperators.stream().filter(o3 -> !r1.contains(o3.getClass()) && !r2.contains(o3.getClass())).forEach(o3 -> {
                    ops.add(setOf(o1, o2, o3));

                    final Set<Class<? extends Operator>> r3 = RESTRICTIONS.getOrDefault(o3.getClass(), setOf(o3.getClass()));

                    allOperators.stream().filter(o4 -> !r1.contains(o4.getClass()) && !r2.contains(o4.getClass()) && !r3.contains(o4.getClass())).forEach(o4 -> {
                        ops.add(setOf(o1, o2, o3, o4));
                    });
                });
            });

        });

        opsSorted = ops.stream().sorted(setComparator).collect(Collectors.toList());

    }

    public static final List<Set<Operator>> getSortedOps() {
        return opsSorted;
    }

    private static Set<Class<? extends Operator>> setOf(Class<? extends Operator>... op) {
        return new HashSet<>(Arrays.asList(op));
    }

    private static Set<Operator> setOf(Operator... op) {
        return new HashSet<>(Arrays.asList(op));
    }

}
