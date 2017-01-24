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

    static final Integer PIECE_TIMEOUT_MS = 60000;
    static final Integer MOVE_TIMEOUT_MS = 5000;
    private static final Integer FKO_Q = 1;
    private static final Integer FKO_P = 1;
    private static final Integer FGAME_Q = 10;
    private static final Map<Class<? extends Operator>, Integer> OP_VALUE;

    static {
        OP_VALUE = new HashMap<>();
        ParamsAndEvaluators.OP_VALUE.put(None.class, 0);
        ParamsAndEvaluators.OP_VALUE.put(Backwards.class, 1);
        ParamsAndEvaluators.OP_VALUE.put(Forward.class, 1);
        ParamsAndEvaluators.OP_VALUE.put(Sideways.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(NotHorizontal.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(OnlyCapture.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(WithoutCapture.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(MaxTimes.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(MinTimes.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(ExactlyTimes.class, 3);
        ParamsAndEvaluators.OP_VALUE.put(SelfCaptureInstead.class, 4);
        ParamsAndEvaluators.OP_VALUE.put(OverEnemyPieceInstead.class, 6);
        ParamsAndEvaluators.OP_VALUE.put(OverEnemyPieceInsteadEndingNormally.class, 7);
        ParamsAndEvaluators.OP_VALUE.put(OverOwnPieceInstead.class, 6);
        ParamsAndEvaluators.OP_VALUE.put(OverOwnPieceInsteadEndingNormally.class, 7);
        ParamsAndEvaluators.OP_VALUE.put(OnlyEven.class, 8);
        ParamsAndEvaluators.OP_VALUE.put(OnlyOdd.class, 8);
        ParamsAndEvaluators.OP_VALUE.put(Inwards.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(Outwards.class, 2);
    }

    /**
     * @param clazz class of operator
     * @return operator value
     */
    public static int fo(Class<? extends Operator> clazz) {
        return ParamsAndEvaluators.OP_VALUE.getOrDefault(clazz, 0);
    }

    /**
     * evaluates collection of operators
     *
     * @param operators
     * @return sum of operator OP_VALUE
     */
    public static int fo(Set<Operator> operators) {
        return operators
                .stream()
                .map(Operator::getValue)
                .reduce(0, Integer::sum);
    }

    /**
     * @param pieceClass
     * @param operators
     * @return fo(pieceClass) * fk(operators)
     */
    public static int fko(PieceClass pieceClass, Set<Operator> operators) {
        int i = fo(operators);
        int j = fk(pieceClass);
        return (i + FKO_Q) * (j + FKO_P);
    }

    /**
     * resolver is like a pair: (pieceClass, operators)
     * <p>
     * used for pieces with description: like (resolver1) and then (resolver2)
     *
     * @param resolver1
     * @param resolver2
     * @return fko(resolver1) * fko(resolver2)
     */
    public static int fko1ko2(SimplePieceResolver resolver1, SimplePieceResolver resolver2) {
        int i = fko(resolver1.getPieceClass(), resolver1.getOperators());
        int j = fko(resolver2.getPieceClass(), resolver2.getOperators());
        return i * j;
    }

    /**
     * @param piecesCount map of pieceName : number of pieces on board
     * @param pieceValues map of pieceName : NDL value
     * @return value of game
     */
    public static int fgame(Map<String, Integer> piecesCount, Map<String, Integer> pieceValues) {

        Integer numberOfPiecesOnBoard = piecesCount.values().stream().reduce(0, Integer::sum);
        Integer numberOfDifferentPieces = piecesCount.size();

        Integer v = 0;
        for (String pieceName : piecesCount.keySet()) {
            v += pieceValues.getOrDefault(pieceName, 0)
                    * piecesCount.getOrDefault(pieceName, 0);
        }
        return (v / numberOfPiecesOnBoard) * (FGAME_Q + numberOfDifferentPieces);
    }


    /**
     * calculates value of xy as a sum of:
     * distance to (0,0) and a distance to the closest diagonal, vertical or horizontal
     *
     * @param x
     * @param y
     * @return
     */
    public static int fxy(int x, int y) {
        int absx = Math.abs(x);
        int absy = Math.abs(y);
        int distanceToVerticalOrHorizontal = Math.min(absx, absy);
        int distanceToDiagonal = Math.abs(absx - absy) / 2 + (Math.abs(absx - absy) % 2);
        int distanceToBeginning = Math.max(absx, absy);
        return Math.min(distanceToDiagonal, distanceToVerticalOrHorizontal) + distanceToBeginning;
    }

    /**
     * piece class value is value of its (x,y) parameter
     * like (2,1) rider etc.
     *
     * @param pieceClass
     * @return fxy(xy)
     */
    public static int fk(PieceClass pieceClass) {
        int x = Math.abs(pieceClass.getXy().getKey());
        int y = Math.abs(pieceClass.getXy().getValue());

        return fxy(x, y);
    }

}
