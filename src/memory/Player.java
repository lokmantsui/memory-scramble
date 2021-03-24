package memory;

import java.util.List;
import java.util.ArrayList;

/*
 * AF(name, turned, status, score) = player with name name, under status status, who has turned over cards in turned 
 * RI: turned.size()<=2, and ( UNFINISHED or (NOMATCH and turned.size()>0) or (MATCH and turned.size()==2))
 * Safety from rep exposure:
 *    All fields are private, the mutable List turned is never returned
 * 
 */
public class Player {
    
    private final String name;
    private final List<Card> turned = new ArrayList<Card>();
    private static enum Status {UNFINISHED,MATCH,NOMATCH}
    private Status status = Status.UNFINISHED;
    private int score = 0;
    
    public Player(String name) {
        this.name = name;
        checkRep();
    }
    
    public String getName() {
        return name;
    }
    
    public int getScore() {
        return score;
    }
        
    public boolean contains(Card card) {
        return turned.contains(card);
    }
    
    /*
     * Requires all cards in turned are controlled by player
     */
    private void relinguishAll() {
        for (Card c:turned) {
            c.relinquish();
        }
        checkRep();
    }
    
    public int size() {
        return turned.size();
    }

    /*
     * Attempt to turn over card. Implements main game mechanics.
     * @param card Card to be turned over
     */
    public synchronized void turnOver(Card card){
        if (status!=Status.UNFINISHED) {
            finish();
        }
        if (size()==0) {
            if (card.isEmpty()) {//1-A Empty card
                return ;
            }
            try{
                if (!card.isUp()) {// 1-B down card
                    card.setUp(true);
                }
                card.setOwner(this); //1-CD, up not controlled, up controlled, may block
                turned.add(card);
            }catch(EmptyCardException e) {
                return ;
            }
        }else if (size()==1) {
            status = Status.NOMATCH;
            if (!card.isEmpty() && !(card.isUp() && card.isControlled())) { //2-CDE
                try{
                    if (!card.isUp()) {// 2-C down card
                        card.setUp(true);
                    }
                    card.setOwner(this); //2-DE up not controlled, should not block
                    turned.add(card);
                }catch(EmptyCardException e) {
                    throw new RuntimeException("should not reach here");
                }
                if (turned.get(0).getSymbol().equals(turned.get(1).getSymbol())) { //2-D Matched
                    status = Status.MATCH;
                } //2-E not Matched
            } //2-B up controlled
            if (status==Status.NOMATCH) { //2-ABCE Empty card, up controlled, down not matched, up not matched
                relinguishAll();
            }
        }else {
            throw new RuntimeException("should not reach here");
        }
    }
    
    /*
     * Finish previous play (3-AB)
     */
    private void finish() {
        if (status==Status.MATCH) { //3-A Matched
            relinguishAll();
            for (Card c:turned) {
                c.remove();
            }
            score+=1;
        }else { //3-B Not matched
            for (Card c:turned) {
                if (!c.isEmpty() && c.isUp() && !c.isControlled()) {
                    c.setUp(false);
                }
            }
        }
        turned.clear();
        status=Status.UNFINISHED;
        checkRep();
    }
    
    private void checkRep() {
        assert size()<=2;
        switch (status) {
        case UNFINISHED:
            break;
        case NOMATCH:
            assert size()>0;
            break;
        case MATCH:
            assert size()==2;
            break;
        }
    }
    
    @Override
    public boolean equals(Object that){
        if (! (that instanceof Player)) return false;
        return ((Player)that).getName().equals(name);
        }
    
    @Override
    public int hashCode(){
        return name.hashCode();
        }
}
