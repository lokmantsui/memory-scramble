package memory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * AF(symbol, isUp, isEmpty, owner): 
 *      if !isEmpty: 
 *          card carrying symbol, which faces up/down if isUp is true/false, which belongs to player owner.
 *      if isEmpty:
 *          empty card slot
 * RI: if owner is non-empty: !isEmpty and isUp and owner.contains(card)
 * 
 * @author lt
 *
 */

public class Card {
    private final String symbol;
    private boolean isUp = false;
    private boolean isEmpty = false;
    private final BlockingQueue<Player> owner=new ArrayBlockingQueue<Player>(1);
    
    public Card(String symbol) {
        this.symbol = symbol;
        checkRep();
    }
    
    public String getSymbol() {
        if (!isEmpty) {
            return symbol;
        }
        return null;
    }
    
    /*
     * Set owner of card. Blocks if card is already owned by another player.
     * TODO: if player 2 blocks here, should not block owner to relinquish control to avoid deadlock
     */
    public void setOwner(Player player) {
        try{
            owner.put(player);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        checkRep();
    }
    
    public boolean isControlled() {
        return owner.size()>0;
    }
    
    /*
     * Relinquish control of the card. The card must be under control.
     */
    public void relinquish() {
        assert isControlled();
        try{
            owner.take();
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
        checkRep();
    }
    
    /*
     * Returns owner, or null if owner is empty
     */
    public Player getOwner() {
        return owner.peek();
    }
    
    public boolean isUp() {
        return isUp;
    }
    
    public void setUp(boolean result) {
        isUp = result;
    }
    
    public boolean isEmpty() {
        return isEmpty;
    }
    
    public void remove() {
        isEmpty = true;
    }
    
    /*
     * String representation of card viewed from a player's perspective
     * @param player
     * @return string rep of card
     * 
     */
    public String viewBy(Player player) {
        if (isEmpty) return "none";
        if (!isUp) return "down";
        else if (player.equals(getOwner())) return "my "+symbol;
        else return "up "+symbol;
    }
    
    private void checkRep() {
        if (isControlled()) {
            assert !isEmpty;
            assert isUp;
            assert getOwner().contains(this);
        }
    }
    
    @Override
    public String toString(){
        if (isEmpty) return "none";
        String face = isUp? "up" : "down";
        return face+" "+symbol;
        }
}
