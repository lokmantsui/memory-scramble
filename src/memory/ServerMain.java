/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * Game server runner.
 * 
 * <p>PS4 instructions: you are encouraged *not* to modify this class.
 */
public class ServerMain {
    
    /**
     * Start a game server using the given arguments.
     * 
     * <p> Command-line usage:
     * <pre> java memory.ServerMain PORT FILENAME </pre>
     * where:
     * 
     * <p> PORT is an integer that specifies the server's listening port number,
     *     according to the spec of {@code java.net.ServerSocket(int)}.
     *     0 specifies that a random unused port will be automatically chosen.
     * <p> FILENAME is the path to a valid board file, which will be loaded as
     *     the starting game board.
     * 
     * <p> For example, to start a web server on a randomly-chosen port using the
     *     board in {@code boards/hearts.txt}:
     * <pre> 0 boards/hearts.txt </pre>
     * 
     * @param args arguments as described above
     * @throws IOException if an error occurs parsing a file or starting a server
     */
    public static void main(String[] args) throws IOException {
        final Queue<String> arguments = new LinkedList<>(Arrays.asList(args));
        
        final int port;
        final Board board;
        
        try {
            port = Integer.parseInt(arguments.remove());
        } catch (NoSuchElementException | NumberFormatException e) {
            throw new IllegalArgumentException("missing or invalid PORT", e);
        }
        
        if (arguments.size() == 1) {
            board = Board.parseFromFile(arguments.remove());
        } else {
            throw new IllegalArgumentException("expected FILENAME");
        }
        
        new WebServer(board, port).start();
    }
}
