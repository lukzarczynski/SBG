package main;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import main.tree.Node;
import main.tree.ResolveResult;
import main.resolvers.Resolver;
import main.tree.Resolvers;
import main.tree.Root;
import main.resolvers.SimplePieceResolverSearcher;

/**
 * Created by lukasz on 25.12.16.
 */
public class PieceResolver {

  public static String resolve(Piece piece) throws PieceResolverException {

    Set<OneMove> movesToInterpret = new HashSet<>(piece.getMoves());
    Map<OneMove, List<Resolver>> simpleParse = new HashMap<>();
    Map<OneMove, List<Resolver>> prefixes = new HashMap<>();


    Resolvers.ops.forEach(ops -> SimplePieceResolverSearcher
        .search(piece.getMoves(), piece, ops)
        .forEach(resolver -> {
          ResolveResult apply = resolver.apply(piece.getMoves());

          apply.getParsed().forEach(parsed -> {
            simpleParse.putIfAbsent(parsed, new ArrayList<>());
            simpleParse.get(parsed).add(resolver);
          });
        }));

//    Resolvers.ops.forEach(ops -> {
//      Set<Resolver> prefixResolvers = PrefixResolverSearcher.findPrefixResolvers(piece.getMoves(), piece, ops);
//
//      System.out.println(prefixResolvers);
//      prefixResolvers.forEach(r -> {
//        Set<OneMove> oneMoves = r.applyPrefixes(piece.getMoves());
//      });
//
//    });

    Set<OneMove> parsedMoves = simpleParse.keySet();

    if (parsedMoves.size() == movesToInterpret.size()) {
      return resolve2(piece, simpleParse.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }
    throw new PieceResolverException(String.format("Could parse: %s out of %s", parsedMoves.size(), movesToInterpret.size()));
  }

  public static String resolve2(Piece piece, List<Resolver> resolvers) throws PieceResolverException {
    final Root root = new Root(piece);

    Set<Node> nodes = new HashSet<>();
    Set<Node> visited = new HashSet<>();
    nodes.add(root);
    int i = 0;
    Node leaf = null;
    while (i < nodes.size()) {
      Node n = nodes.stream()
          .filter(c -> !visited.contains(c))
          .filter(c -> !c.isLeaf())
          .sorted(Comparator.comparingInt(a -> a.movesToInterpret.size()))
          .findFirst().orElse(null);
      if (isNull(n)) {
        break;
      }

      visited.add(n);
      n.expandNode(resolvers, nonNull(leaf) ? leaf.getValue() : Long.MAX_VALUE);
      final Set<Node> children = n.children;
      nodes.addAll(children);


      leaf = nodes.stream()
          .filter(Node::isLeaf)
          .sorted(Comparator.comparing(Node::getValue))
          .findFirst()
          .orElse(null);

      Node bestMatch = nodes.stream()
          .sorted((a, b) -> {
            int compare = Integer.compare(a.movesToInterpret.size(), b.movesToInterpret.size());
            if (compare == 0) {
              return a.getValue().compareTo(b.getValue());
            }
            return compare;

          })
//                    .sorted(Comparator.comparingInt(a -> a.movesToInterpret.size()))
          .findFirst().orElse(root);

//            System.out.println(String.format("Best match: m: %s v: %s, nodes: %s, visited: %s",
//                    bestMatch.movesToInterpret.size(), bestMatch.getValue(),
//                    nodes.size(),
//                    visited.size()));
    }

    if (leaf == null) {
      Node bestMatch = nodes.stream()
          .sorted(Comparator.comparingInt(a -> a.movesToInterpret.size()))
          .findFirst().orElse(root);
      throw new PieceResolverException(getDescription(bestMatch));
    } else {
      return getDescription(leaf);
    }
  }

  private static String getDescription(Node leaf) {
    StringBuilder description = new StringBuilder();
    Node x = leaf;
    while (x.parent != null) {
      description.append(x.getDescription());
      x = x.parent;
    }

    return description.toString();
  }

}


