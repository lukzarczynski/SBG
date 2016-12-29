package main.operator;

import main.MoveType;
import main.OneMove;

import java.util.function.Predicate;

/**
 * moves that end with capturing enemy piece
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class OnlyCapture extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return move -> move.getMoves().get(move.getMoves().size() - 1).getMoveType().equals(MoveType.PIECE);
    }

    @Override
    public String getDescription() {
        return "Only Capture";
    }
}
