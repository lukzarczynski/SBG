package main.operator;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import main.Move;
import main.OneMove;

/**
 * moves that end only without capturing enemy piece
 * <p>
 * Created by lukasz on 06.12.16.
 */
public class OverEnemyPieceInstead extends Operator {

    private static final Operator instance = new OverEnemyPieceInstead();

    public static Operator instance(int priority) {
        instance.priority = priority;
        return instance;
    }

    @Override
    public Predicate<OneMove> matches() {
        return move -> move.getMoves().stream().anyMatch(Move::getPiece);
    }

    @Override public Function<OneMove, OneMove> map() {
        return move -> {
            OneMove om = new OneMove();
            om.setMoves(move.getMoves().stream().map(m -> {
                final Move copy = m.copy();
                if (!copy.getPiece()) {
                    copy.setPiece(true);
                }
                return copy;
            }).collect(Collectors.toList()));
            return om;
        };
    }

    @Override public String getDescription() {
        return "Over Enemy Piece Instead";
    }
}
