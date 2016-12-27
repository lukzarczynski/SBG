package main.operator;

import java.util.function.Predicate;

import main.MoveType;
import main.OneMove;

/**
 * moves that end with capturing enemy piece
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class OnlyCapture extends Operator {

    private static final Operator instance = new OnlyCapture();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> move.getMoves().get(move.getMoves().size() - 1).getMoveType().equals(MoveType.PIECE);
    }

    @Override public String getDescription() {
        return "Only Capture";
    }
}
