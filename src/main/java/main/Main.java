package main;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by lukasz on 26.11.16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Scanning");

//        scanDirectory();

        System.out.println("Scanned");

        final File allMovesFile = new File("src/main/resources/allMoves.sbg1");

        final Set<Piece> pieces = loadPieces(allMovesFile);

        System.out.println("Loaded");


        new PrintWriter("src/main/resources/parsedMoves.sbg1").close();
        new PrintWriter("src/main/resources/failedMoves.sbg1").close();
        File parsedFile = new File("src/main/resources/parsedMoves.sbg1");
        File failedFile = new File("src/main/resources/failedMoves.sbg1");

        FileOutputStream parsedFOS = new FileOutputStream(parsedFile, true);
        FileOutputStream failedFOS = new FileOutputStream(failedFile, true);

        resolve(pieces, parsedFOS, failedFOS);

        System.out.println("Finished");
    }

    public static Set<Piece> loadPieces(File allMovesFile) throws FileNotFoundException {
        final Set<Piece> pieces = new HashSet<>();

        final Scanner scanner = new Scanner(allMovesFile);
        while (scanner.hasNextLine()) {
            final String pieceString = scanner.nextLine();
            if (!pieceString.startsWith("//")) {
                pieces.add(Piece.parse(pieceString));
            }
        }
        return pieces;
    }

    public static void resolve(Set<Piece> pieces, FileOutputStream parsedFOS, FileOutputStream failedFOS) throws IOException {
        final AtomicInteger index = new AtomicInteger(0);

        pieces.parallelStream().forEach(pa -> {
            try {
                String text = resolve(pa);
                synchronized (parsedFOS) {
                    try {
                        parsedFOS.write(text.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (PieceResolverException e) {
                String text = pa.toString() + "\n";
                synchronized (failedFOS) {
                    try {
                        failedFOS.write(text.getBytes());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            System.out.println("Piece no: " + index.getAndIncrement());
        });

        System.out.println("Resolved");
}

    public static String resolve(Piece piece) throws PieceResolverException {
        final String resolve = PieceResolver.resolve(piece);
        return "//" + resolve + "\n" + piece.toString() + "\n";
    }

    private static void scanDirectory() throws IOException {
        final File dir = new File("/home/lukasz/Documents/mgr/xxxya/ggp_sbgRAPP/Fitness^1");

        final Iterator<File> fileIterator = FileUtils.iterateFiles(dir, new String[]{"sbg"}, true);

        final File allMovesFile = new File("allMoves.sbg1");

        FileOutputStream allMovesOS = new FileOutputStream(allMovesFile, false);

        final List<String> pieces = new ArrayList<>();
        fileIterator
                .forEachRemaining(f -> {
                    Scanner scanner = null;
                    try {
                        scanner = new Scanner(f);
                    } catch (FileNotFoundException ignored) {
                        throw new RuntimeException();
                    }

                    Boolean p = false;
                    StringBuilder sb = new StringBuilder();
                    while (scanner.hasNext()) {
                        final String line = scanner.nextLine();

                        if (StringUtils.isBlank(line)) {
                            continue;
                        }
                        if (line.contains("<--PIECES-->")) {
                            p = true;
                            continue;
                        } else if (line.contains("<--GOALS-->")) {
                            p = false;
                            continue;
                        }

                        if (p && sb.length() == 0) {
                            sb.append(f.getParent()).append(f.getName());
                        }

                        if (p && !line.startsWith("/")) {
                            sb.append(line);
                            if (line.contains("&")) {
                                pieces.add(sb.toString());
                                sb = new StringBuilder();
                            }
                        }
                    }
                });


        final Set<Piece> collect = pieces.stream().map(Piece::parse).collect(Collectors.toSet());

        final StringBuilder builder = new StringBuilder();
        collect.stream().map(Piece::toString).forEach(ps -> {
            builder.append(ps);
            builder.append("\n");
        });
        allMovesOS.write(builder.toString().getBytes());

    }

}
