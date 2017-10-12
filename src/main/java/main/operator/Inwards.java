package main.operator;

import java.util.function.Predicate;

import main.model.Move;
import main.model.OneMove;

/**
 * means that distance to (0,0) at each point is not decreasing
 *
 * Created by lukza on 21.01.2017.
 */
public class Inwards extends Operator {

    @Override
    public Predicate<OneMove> matches() {
        return om -> {
            int startX = om.getVector().getKey();
            int startY = om.getVector().getValue();
            int x = startX;
            int y = startY;
            for (Move m : om.getMoves()) {

                x += m.getDx();
                y += m.getDy();

                if (startX != 0) {
                    if (startX > 0 ? x > startX : x < startX) {
                        return false;
                    }
                }

                if (startY != 0) {
                    if (startY > 0 ? y > startY : y < startY) {
                        return false;
                    }
                }
            }
            return true;
        };
    }

    @Override
    public String getDescription() {
        return "Inwards";
    }
}
