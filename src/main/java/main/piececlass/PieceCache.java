package main.piececlass;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import main.Point;

/**
 * Created by lzarczynski on 29.12.2016.
 */
public class PieceCache {

    private static final Map<Point, XYLeaper> leapersCache = new ConcurrentHashMap<>();
    private static final Map<Point, XYYXLeaper> xyyxLeapersCache = new ConcurrentHashMap<>();
    private static final Map<Point, XYRider> ridersCache = new ConcurrentHashMap<>();

    public static XYLeaper getLeaper(Point p) {
        final Point pair = Point.of(Math.abs(p.getKey()), Math.abs(p.getValue()));
        if (!leapersCache.containsKey(pair)) {
            XYLeaper value = new XYLeaper(pair.getX(), pair.getY());
            leapersCache.put(pair, value);
        }
        return leapersCache.get(pair);
    }

    public static XYYXLeaper getXYYXLeaper(Point p) {
        final Point pair = Point.of(Math.abs(p.getKey()), Math.abs(p.getValue()));
        if (!xyyxLeapersCache.containsKey(pair)) {
            XYYXLeaper value = new XYYXLeaper(pair.getX(), pair.getY());
            xyyxLeapersCache.put(pair, value);
            if (!Objects.equals(pair.getX(), pair.getY())) {
                xyyxLeapersCache.put(Point.of(pair.getY(), pair.getX()), value);
            }
        }
        return xyyxLeapersCache.get(pair);
    }

    public static XYRider getRider(Point p) {
        final Point pair = Point.of(Math.abs(p.getKey()), Math.abs(p.getValue()));
        if (!ridersCache.containsKey(pair)) {
            XYRider value = new XYRider(pair.getX(), pair.getY());
            ridersCache.put(pair, value);
        }
        return ridersCache.get(pair);
    }
}
