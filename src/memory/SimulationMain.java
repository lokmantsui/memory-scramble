/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.IOException;
import java.util.Random;

/**
 * Example code.
 * 
 * <p>PS4 instructions: you may use, modify, or remove this class.
 */
public class SimulationMain {
    
    /**
     * Simulate a game.
     * 
     * @param args unused
     * @throws IOException if an error occurs reading or parsing the board
     */
    public static void main(String[] args) throws IOException {
        final String filename = "boards/ab.txt";
        final Board board = Board.parseFromFile(filename);
        final int size = 5;
        final int players = 1;
        final int tries = 20;
        
        for (int ii = 0; ii < players; ii++) {
            Player player = new Player(String.valueOf(ii)); 
            new Thread(() -> {
                final Random random = new Random();
                
                for (int jj = 0; jj < tries; jj++) {
                    //  try to flip over a first card at (random.nextInt(size), random.nextInt(size))
                    //      which might block until this player can control that card
                    board.turn(player, random.nextInt(size), random.nextInt(size));
                    
                    //  and if that succeeded,
                    //      try to flip over a second card at (random.nextInt(size), random.nextInt(size))
                    if (player.size()==1) {
                        board.turn(player, random.nextInt(size), random.nextInt(size));
                    }
                    
                }
            }).start();
        }
    }
}
