package main;

import main.operator.*;
import main.piececlass.PieceClass;
import main.resolvers.SimplePieceResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lukza on 17.01.2017.
 */
public final class ParamsAndEvaluators {

    public static final Integer PIECE_TIMEOUT_MS = 600000;
    public static final Integer MOVE_TIMEOUT_MS = 100000;
    private static final Map<Class<? extends Operator>, Integer> values;

    static {
        values = new HashMap<>();
        ParamsAndEvaluators.values.put(Backwards.class, 2);
        ParamsAndEvaluators.values.put(Forward.class, 2);
        ParamsAndEvaluators.values.put(None.class, 1);
        ParamsAndEvaluators.values.put(OnlyCapture.class, 6);
        ParamsAndEvaluators.values.put(OnlyEven.class, 12);
        ParamsAndEvaluators.values.put(OnlyOdd.class, 12);
        ParamsAndEvaluators.values.put(OverEnemyPieceInstead.class, 8);
        ParamsAndEvaluators.values.put(OverOwnPieceInsteadEndingNormally.class, 14);
        ParamsAndEvaluators.values.put(SelfCaptureInstead.class, 8);
        ParamsAndEvaluators.values.put(OverOwnPieceInstead.class, 8);
        ParamsAndEvaluators.values.put(MaxTimes.class, 8);
        ParamsAndEvaluators.values.put(MinTimes.class, 8);
        ParamsAndEvaluators.values.put(ExactlyTimes.class, 10);
        ParamsAndEvaluators.values.put(Sideways.class, 3);
        ParamsAndEvaluators.values.put(WithoutCapture.class, 6);
    }

    public static int getOperatorValueForClass(Class<? extends Operator> clazz) {
        return ParamsAndEvaluators.values.getOrDefault(clazz, 0);
    }

    public static int evaluateMultipleOperators(Set<Operator> operators) {
        return operators
                .stream()
                .map(Operator::getValue)
                .reduce(0, Integer::sum)
                + ((operators.size() - 1) * 20);
    }

    public static int evaluateSimpleResolver(SimplePieceResolver r) {
        return evaluateSimpleResolver(r.getPieceClass(), r.getOperators());
    }

    public static int evaluateSimpleResolver(PieceClass pc, Set<Operator> ops) {
        int i = evaluateMultipleOperators(ops);
        int j = evaluatePieceClass(pc);
        return i * j;
    }

    public static int evaluateCompositResolver(SimplePieceResolver r1, SimplePieceResolver r2) {
        int i = evaluateSimpleResolver(r1);
        int j = evaluateSimpleResolver(r2);
        return i * j;
    }

    public static int evaluatePosition(int x, int y) {
        int absx = Math.abs(x);
        int absy = Math.abs(y);
        int distanceToVerticalOrHorizontal = Math.min(absx, absy);
        int distanceToDiagonal = Math.abs(absx - absy) / 2 + (Math.abs(absx - absy) % 2);
        int distanceToBeginning = Math.max(absx, absy);
        return Math.min(distanceToDiagonal, distanceToVerticalOrHorizontal) + distanceToBeginning;
    }

    private static int evaluatePieceClass(PieceClass pieceClass) {
        int x = Math.abs(pieceClass.getXy().getKey());
        int y = Math.abs(pieceClass.getXy().getValue());

        return evaluatePosition(x, y);
    }

}
