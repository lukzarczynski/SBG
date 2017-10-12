package main;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.model.Move;
import main.model.OneMove;
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
import main.operator.Outwards;
import main.operator.OutwardsX;
import main.operator.OutwardsY;
import main.operator.OverEnemyPieceInstead;
import main.operator.OverEnemyPieceInsteadEndingNormally;
import main.operator.OverOwnPieceInstead;
import main.operator.OverOwnPieceInsteadEndingNormally;
import main.operator.SelfCaptureInstead;
import main.operator.Sideways;
import main.operator.WithOneEnemyPiece;
import main.operator.WithOneOwnPiece;
import main.operator.WithoutCapture;
import main.piececlass.PieceClass;
import main.resolvers.Resolver;
import main.resolvers.SimplePieceResolver;

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
        ParamsAndEvaluators.OP_VALUE.put(SelfCaptureInstead.class, 3); // 4 => 3
        ParamsAndEvaluators.OP_VALUE.put(OverEnemyPieceInstead.class, 4); // 6 => 3
        ParamsAndEvaluators.OP_VALUE.put(OverEnemyPieceInsteadEndingNormally.class, 4); // 7 => 4
        ParamsAndEvaluators.OP_VALUE.put(OverOwnPieceInstead.class, 3); // 6 => 3
        ParamsAndEvaluators.OP_VALUE.put(OverOwnPieceInsteadEndingNormally.class, 4); // 7 => 4
        ParamsAndEvaluators.OP_VALUE.put(OnlyEven.class, 5); // 8 => 5
        ParamsAndEvaluators.OP_VALUE.put(OnlyOdd.class, 5); // 8 => 5
        ParamsAndEvaluators.OP_VALUE.put(Outwards.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(OutwardsY.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(OutwardsX.class, 2);
        ParamsAndEvaluators.OP_VALUE.put(WithOneOwnPiece.class, 4);
        ParamsAndEvaluators.OP_VALUE.put(WithOneEnemyPiece.class, 4);
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
     * @return sum of operator OP_VALUE
     */
    public static int fo(Set<Operator> operators) {
        return operators
                .stream()
                .map(Operator::getValue)
                .reduce(0, Integer::sum);
    }

    /**
     * @return fo(pieceClass) * fk(operators)
     */
    public static int fko(PieceClass pieceClass, Set<Operator> operators) {
        int i = fk(pieceClass);
        int j = fo(operators);
        return i * (j + FKO_P); // it was (i + FKO_Q) * (j + FKO_P);
    }

    /**
     * @return fo(pieceClass) * fk(operators)
     */
    public static int fko(SimplePieceResolver r) {
        int i = fk(r.getPieceClass());
        int j = fo(r.getOperators());
        return i * (j + FKO_P); // it was (i + FKO_Q) * (j + FKO_P);
    }

    /**
     * resolver is like a pair: (pieceClass, operators) <p> used for pieces with description: like
     * (resolver1) and then (resolver2)
     *
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
        if (numberOfPiecesOnBoard == 0) {
            numberOfPiecesOnBoard = 1;
            System.err.println("NUMBER OF PIECES IS 0 !?");
        }
        Integer numberOfDifferentPieces = Math.toIntExact(piecesCount.entrySet().stream()
                .filter(e -> e.getValue() > 0).count());

        Double v = 0.0;
        for (String pieceName : piecesCount.keySet()) {
            v += pieceValues.getOrDefault(pieceName, 0)
                    * piecesCount.getOrDefault(pieceName, 0);
        }
        return (int) ((v / numberOfPiecesOnBoard) * (FGAME_Q + numberOfDifferentPieces));
    }


    /**
     * calculates value of xy as a sum of: distance to (0,0) and a distance to the closest diagonal,
     * vertical or horizontal
     */
    public static int fxy(int x, int y) {
        int absx = Math.abs(x);
        int absy = Math.abs(y);
        int distanceToVerticalOrHorizontal = Math.min(absx, absy);
        int distanceToDiagonal = Math.abs(absx - absy) / 2 + (Math.abs(absx - absy) % 2);
        int distanceToBeginning = Math.max(absx, absy);
        return FKO_Q + Math.min(distanceToDiagonal, distanceToVerticalOrHorizontal) + distanceToBeginning;
    }

    public static int fp(Collection<Resolver> resolvers) {
        return resolvers.stream().map(Resolver::getValue).reduce(0, Integer::sum);
    }

    /**
     * piece class value is value of its (x,y) parameter like (2,1) rider etc.
     *
     * @return fxy(xy)
     */
    public static int fk(PieceClass pieceClass) {
        int x = Math.abs(pieceClass.getXy().getKey());
        int y = Math.abs(pieceClass.getXy().getValue());

        return fxy(x, y);
    }


    public static int fsc(OneMove om) {
        int x = 0;
        int y = 0;

        int result = 1; // it was 1;

        for (Move m : om.getMoves()) {
            x += m.getDx();
            y += m.getDy();

            result *= ParamsAndEvaluators.fxy(x, y);

        }

        return result;
    }

}
