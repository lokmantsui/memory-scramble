/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.io.IOException;

/**
 * TODO
 */
public class PlayerTest {
    
    // Testing strategy
    // Player():
    // getName():
    // getScore():
    // add():
    // contains():
    // relinquishAll():
    // size():
    // turnOver():
    // finish():
    // equals():
    // hashCode():
    
    @Test
    public void testAssertionsEnabled() {
        assertThrows(AssertionError.class, () -> { assert false; },
                "make sure assertions are enabled with VM argument '-ea'");
    }
    
    // TODO tests
    
    @Test
    public void testinit() {
        Player p1 = new Player("Smith");
        assertEquals("Smith", p1.getName());
    }
    
    public void testaddcontains() {
        Player p1 = new Player("Smith");
        Card c1 = new Card("A");
        p1.add(c1);
        assertEquals(true, p1.contains(c1));
        assertEquals(1,p1.size());
    }
    
    @Test
    public void testTurnOver() {
        Card c1 = new Card("A");
        Player p1 = new Player("Simon");
        p1.turnOver(c1); //1-B
        assertEquals(c1.getOwner(),p1);
        assertEquals(c1.isUp(),true);
        assertEquals(p1.size(),1);
        Card c2 = new Card("B");
        c2.remove();
        assertEquals(c2.isEmpty(),true);
        Player p2 = new Player("James");
        p2.turnOver(c2); //1-A
        assertEquals(p2.size(),0);
        assertEquals(c2.isControlled(),false);
        assertEquals(c2.getOwner()==null,true);
        Card c3 = new Card("B");
        assertEquals(c3.isUp(),false);
        p1.turnOver(c3); //2-E
        assertEquals(c3.isUp(),true); //2-C
        assertEquals(p1.size(),2);
        assertEquals(c1.isControlled(),false);
        assertEquals(c3.isControlled(),false);
        assertEquals(p1.getScore(),0);
        Card c4 = new Card("A");
        p1.turnOver(c4); //3-B
        assertEquals(c1.isUp(),false);
        assertEquals(c3.isUp(),false);
        p1.turnOver(c1); //2-D
        assertEquals(c1.isControlled(),true);
        assertEquals(c4.isControlled(),true);
        p1.turnOver(c3); //3-A
        assertEquals(c1.isEmpty(),true);
        assertEquals(c4.isEmpty(),true);
        assertEquals(c1.isControlled(),false);
        assertEquals(c4.isControlled(),false);
        assertEquals(p1.getScore(),1);
    }
    
}
