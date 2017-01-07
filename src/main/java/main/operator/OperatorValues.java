package main.operator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lukza on 28.12.2016.
 */
public class OperatorValues {

    private static final Map<Class<? extends Operator>, Integer> values;

    static {
        values = new HashMap<>();
        values.put(Backwards.class, 2);
        values.put(Diagonal.class, 4);
        values.put(Forward.class, 2);
        values.put(Horizontal.class, 4);
        values.put(None.class, 1);
        values.put(OnlyCapture.class, 6);
        values.put(OnlyEven.class, 10);
        values.put(OnlyOdd.class, 10);
        values.put(Orthogonal.class, 4);
        values.put(OverEnemyPieceInstead.class, 8);
        values.put(SelfCaptureInstead.class, 8);
        values.put(OverOwnPieceInstead.class, 8);
        values.put(RestrictedToX.class, 8);
        values.put(Sideways.class, 3);
        values.put(WithoutCapture.class, 6);
    }

    public static int getForClass(Class<? extends Operator> clazz) {
        return values.getOrDefault(clazz, 0);
    }
}
