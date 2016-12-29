package main.piececlass;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lzarczynski on 29.12.2016.
 */
public class PieceCache {


  private static final Map<Pair<Integer, Integer>, XYLeaper> leapersCache = new ConcurrentHashMap<>();
  private static final Map<Pair<Integer, Integer>, XYRider> ridersCache = new ConcurrentHashMap<>();

  public static XYLeaper getLeaper(Pair<Integer, Integer> pair) {
    if (!leapersCache.containsKey(pair)) {
      XYLeaper value = new XYLeaper(pair.getLeft(), pair.getRight());
      leapersCache.put(pair, value);
      if (!Objects.equals(pair.getLeft(), pair.getRight())) {
        leapersCache.put(Pair.of(pair.getRight(), pair.getLeft()), value);
      }
    }
    return leapersCache.get(pair);
  }

  public static XYRider getRider(Pair<Integer, Integer> pair) {
    if (!ridersCache.containsKey(pair)) {
      XYRider value = new XYRider(pair.getLeft(), pair.getRight());
      ridersCache.put(pair, value);
    }
    return ridersCache.get(pair);
  }
}
