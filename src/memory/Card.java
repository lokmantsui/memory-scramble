package memory;


/**
 * 
 * 
 * @author lt
 *
 */

public class Card implements Spot {
    private final String symbol;
    
    public Card(String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    @Override
    public boolean equals(Object that){
        return (that instanceof Card) && symbol.equals(((Card)that).symbol);
        }
    
    @Override
    public int hashCode(){
        return symbol.hashCode();
        }
    
    @Override
    public String toString(){
        return symbol.toString();
        }
}
