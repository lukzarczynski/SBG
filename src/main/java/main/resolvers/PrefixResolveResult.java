package main.resolvers;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;
import java.util.stream.Collectors;

import main.OneMove;

/**
 * Created by lzarczynski on 30.12.2016.
 */
public class PrefixResolveResult {

  private final Set<Pair<OneMove, OneMove>> move;
  private final Set<OneMove> notMatchedPrefixes;

  public PrefixResolveResult(Set<Pair<OneMove, OneMove>> move, Set<OneMove> notMatchedPrefixes) {
    this.move = move;
    this.notMatchedPrefixes = notMatchedPrefixes;
  }

  public Set<Pair<OneMove, OneMove>> getMove() {
    return move;
  }

  public Set<OneMove> getNotMatchedPrefixes() {
    return notMatchedPrefixes;
  }

  public Set<OneMove> getSuffixes() {
    return move.stream().map(Pair::getValue).collect(Collectors.toSet());
  }

  public Set<OneMove> getPrefixes() {
    return move.stream().map(Pair::getKey).collect(Collectors.toSet());
  }
}
