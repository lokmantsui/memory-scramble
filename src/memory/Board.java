/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.IOException;

/**
 * TODO specification
 * Mutable and threadsafe.
 * 
 * <p>PS4 instructions: the specification of static method
 *    {@link #parseFromFile(String)} may not be changed.
 */
public class Board {
    
    /**
     * Make a new board by parsing a file.
     * 
     * @param filename path to a game board file
     * @return a new board with the size and cards from the given file
     * @throws IOException if an error occurs reading or parsing the file
     */
    public static Board parseFromFile(String filename) throws IOException {
        throw new RuntimeException("unimplemented");
    }
    
    // TODO fields
    
    // Abstraction function:
    //   TODO
    // Representation invariant:
    //   TODO
    // Safety from rep exposure:
    //   TODO
    // Thread safety argument:
    //   TODO
    
    // TODO constructor(s)
    
    // TODO checkRep
    
    // TODO other methods
    
}
