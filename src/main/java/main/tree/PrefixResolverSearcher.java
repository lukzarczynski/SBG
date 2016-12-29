package main.tree;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.Move;
import main.OneMove;
import main.Piece;
import main.operator.Operator;
import main.piececlass.PieceCache;
import main.piececlass.XYLeaper;
import main.piececlass.XYRider;

/**
 * Created by lzarczynski on 29.12.2016.
 */
public class PrefixResolverSearcher {

  private Resolver resolver1;

  public static Set<Resolver> findPrefixResolvers(Set<OneMove> moves, Piece piece, Set<Operator>
      operators1) {

    Set<XYLeaper> leapers = findLeapers(moves, piece, operators1);
    Set<XYRider> riders = findRiders(moves, piece, operators1);

   return  Stream.concat(leapers.stream(), riders.stream()).map(pc -> new Resolver(pc,
       operators1)).collect(Collectors.toSet());


  }

  private static Set<XYLeaper> findLeapers(Set<OneMove> moves, Piece piece, Set<Operator>
      operators) {
    return getCandidates(moves).stream()
        .map(PieceCache::getLeaper)
        .filter(r -> r.matchesPrefix(piece.getMoves(), operators))
        .collect(Collectors.toSet());
  }

  private static Set<XYRider> findRiders(Set<OneMove> moves, Piece piece, Set<Operator> operators) {
    return getCandidates(moves).stream()
        .map(PieceCache::getRider)
        .filter(r -> r.matchesPrefix(piece.getMoves(), operators))
        .collect(Collectors.toSet());
  }

  private static Set<Pair<Integer, Integer>> getCandidates(Set<OneMove> moves) {
    List<Move> firstMoves = moves.stream().map(om -> om.getMoves().get(0)).collect(Collectors.toList());

    return firstMoves.stream().map(m -> Pair.of(Math.abs(m.getDx()), Math.abs(m.getDy())))
        .collect(Collectors.toSet());
  }


}
