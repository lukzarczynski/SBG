package main.resolvers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import main.ParamsAndEvaluators;
import main.operator.Backwards;
import main.operator.ExactlyTimes;
import main.operator.Forward;
import main.operator.MaxTimes;
import main.operator.MinTimes;
import main.operator.None;
import main.operator.NotHorizontal;
import main.operator.OnlyCapture;
import main.operator.OnlyEven;
import main.operator.OnlyOdd;
import main.operator.Operator;
import main.operator.OverEnemyPieceInstead;
import main.operator.OverEnemyPieceInsteadEndingNormally;
import main.operator.OverOwnPieceInstead;
import main.operator.OverOwnPieceInsteadEndingNormally;
import main.operator.SelfCaptureInstead;
import main.operator.Sideways;
import main.operator.WithOneEnemyPiece;
import main.operator.WithOneOwnPiece;
import main.operator.WithoutCapture;

/**
 * Created by lukasz on 10.12.16.
 */
public final class Resolvers {

    public static final Set<Set<Operator>> ops;
    public static final List<Set<Operator>> opsSorted;
    private static final Comparator<Set<Operator>> setComparator =
            Comparator.comparingInt(ParamsAndEvaluators::fo);

    private static final Map<Class<? extends Operator>, Set<Class<? extends Operator>>> RESTRICTIONS;

    static {
        ops = new HashSet<>();
        RESTRICTIONS = new HashMap<>();

        Set<Operator> allOperators = setOf(
                new None(),
                new Forward(),
                new Backwards(),
                new Sideways(),
                new NotHorizontal(),
                new OnlyCapture(),
                new WithoutCapture(),
                new OnlyEven(),
                new OnlyOdd(),
                new MaxTimes(2),
                new MaxTimes(3),
                new MaxTimes(4),
                new MaxTimes(5),
                new MaxTimes(6),

                new MinTimes(2),
                new MinTimes(3),
                new MinTimes(4),
                new MinTimes(5),
                new MinTimes(6),

                new ExactlyTimes(2),
                new ExactlyTimes(3),
                new ExactlyTimes(4),
                new ExactlyTimes(5),
                new ExactlyTimes(6),

                new OverEnemyPieceInstead(),
                new OverOwnPieceInstead(),
                new SelfCaptureInstead(),
                new OverOwnPieceInsteadEndingNormally(),
                new OverEnemyPieceInsteadEndingNormally(),

                new WithOneEnemyPiece(),
                new WithOneOwnPiece()
        );


        addRestrictions(Forward.class, None.class, Backwards.class);
        addRestrictions(Backwards.class, None.class, Forward.class);
        addRestrictions(Sideways.class, None.class);
        addRestrictions(NotHorizontal.class, None.class);
        addRestrictions(OnlyCapture.class, None.class, WithoutCapture.class);
        addRestrictions(WithoutCapture.class, None.class, OnlyCapture.class);
        addRestrictions(OnlyEven.class, None.class, OnlyOdd.class, MaxTimes.class, MinTimes.class, ExactlyTimes.class);
        addRestrictions(OnlyOdd.class, None.class, OnlyEven.class, MaxTimes.class, MinTimes.class, ExactlyTimes.class);
        addRestrictions(MaxTimes.class, None.class, MaxTimes.class, MinTimes.class, ExactlyTimes.class);
        addRestrictions(MinTimes.class, None.class, MaxTimes.class, MinTimes.class, ExactlyTimes.class);
        addRestrictions(ExactlyTimes.class, None.class, MaxTimes.class, MinTimes.class, ExactlyTimes.class);
        addRestrictions(OverEnemyPieceInstead.class, None.class, OverOwnPieceInstead.class);
        addRestrictions(OverOwnPieceInstead.class, None.class, OverEnemyPieceInstead.class);
        addRestrictions(OverOwnPieceInsteadEndingNormally.class, None.class,
                OverEnemyPieceInstead.class,
                OverOwnPieceInstead.class, OverOwnPieceInsteadEndingNormally.class);
        addRestrictions(OverEnemyPieceInsteadEndingNormally.class, None.class,
                OverEnemyPieceInstead.class,
                OverOwnPieceInstead.class);

        addRestrictions(WithOneEnemyPiece.class,
                OverEnemyPieceInstead.class,
                OverEnemyPieceInsteadEndingNormally.class,
                None.class
        );
        addRestrictions(WithOneOwnPiece.class,
                OverOwnPieceInstead.class,
                OverOwnPieceInsteadEndingNormally.class,
                None.class
        );

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

    private static void addRestrictions(Class<? extends Operator> op, Class<? extends Operator>... restrictions) {
        RESTRICTIONS.putIfAbsent(op, setOf(op));
        RESTRICTIONS.get(op).addAll(Arrays.asList(restrictions));
        RESTRICTIONS.get(op).forEach(r -> {
            RESTRICTIONS.putIfAbsent(r, setOf(r));
            RESTRICTIONS.get(r).add(op);
        });
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
