/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.io.IOException;

/**
 * TODO
 */
public class BoardTest {
    
    // Testing strategy
    //   TODO
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    // TODO tests
    
    @Test
    public void testinit() {
        try{
            Board board = Board.parseFromFile("boards/ab.txt");
            System.out.print(board.toString());
            assertEquals(((Card) board.getCard(4,4)).getSymbol(),"C");
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
}
