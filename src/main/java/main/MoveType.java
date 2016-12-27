package main;

import java.util.Arrays;

/**
 * Created by lukza on 26.12.2016.
 */
public enum MoveType {

    PIECE("p"), OWN("w"), EMPTY("e");

    private String code;

    MoveType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static MoveType byCode(String code) {
        return Arrays.stream(MoveType.values()).filter(mt -> mt.getCode().equals(code)).findAny().orElse(null);
    }
}
