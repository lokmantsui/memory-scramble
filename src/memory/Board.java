/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO specification
 * Mutable and threadsafe.
 * 
 * <p>PS4 instructions: the specification of static method
 *    {@link #parseFromFile(String)} may not be changed.
 */
public class Board {
    // Abstraction function:
    //   AF(R,C,arr) = board with R rows and C columns, with cards at i th row and j th column
    //        stored as arr[i][j]
    // Representation invariant:
    //   R>0 and C>0 and dimension(arr) = R x C
    // Safety from rep exposure:
    //   All fields are private final
    // Thread safety argument:
    //   TODO

    private final int R;
    private final int C;
    private final Card[][] arr;
    private final ConcurrentMap<String,Player> playerDir=new ConcurrentHashMap<String,Player>();
        
    /*
     * private Constructor: can only construct instance through parseFromFile
     */
    private Board(int R, int C, Card[][] arr) {
        this.R = R;
        this.C = C;
        this.arr = arr;
        checkRep();
    }
    
    /**
     * Make a new board by parsing a file.
     * 
     * @param filename path to a game board file
     * @return a new board with the size and cards from the given file
     * @throws IOException if an error occurs reading or parsing the file
     */
    public static Board parseFromFile(String filename) throws IOException {
        
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String str = br.readLine();
            Matcher m = Pattern.compile("([0-9]+)x([0-9]+)").matcher(str);
            m.matches();
            int R = Integer.valueOf(m.group(1));
            int C = Integer.valueOf(m.group(2));
            Card[][] arr = new Card[R][C];
            for (int i=0;i<R;i++) {
                for (int j=0;j<C;j++) {
                    String sym = br.readLine();
                    arr[i][j] = new Card(sym);
                }
            }
            return new Board(R,C,arr);
        }
    }
    
    /*
     * Requires 0<=i<R, 0<=j<C
     * 
     */
    public Card getCard(int i, int j) {
        checkRep();
        return arr[i][j];
    }
    
    public void registerPlayer(String name) {
        Player player = new Player(name);
        playerDir.putIfAbsent(name, player);
    }
    
    public void turn(Player player, int i, int j) {
        Card c = getCard(i,j);
        System.out.println("Player "+player.getName()+" plays ("+i+", "+j+" ,"+c.getSymbol()+") score: "+player.getScore());
        player.turnOver(c);
        System.out.println(toString());
        checkRep();
    }
    
    /*
     * Get a Player from his/her name
     * @param name
     * @return player
     * 
     */
    public Player getPlayer(String name) {
        return playerDir.get(name);
    }
    
    private void checkRep() {
        assert R>0;
        assert C>0;
        assert arr.length==R;
        assert arr[0].length==C;
    }
    
    //https://stackoverflow.com/questions/388461/how-can-i-pad-a-string-in-java
    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);  
   }
    
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(R+"x"+C+"\n");
        for (int i=0;i<R;i++) {
            for (int j=0;j<C;j++) {
                out.append(padRight(arr[i][j].toString(),7));
            }
            out.append("\n");
        }
        checkRep();
        return out.toString();
    }
    
    public String viewBy(Player player) {
        StringBuilder out = new StringBuilder();
        out.append(R+"x"+C+"\n");
        for (int i=0;i<R;i++) {
            for (int j=0;j<C;j++) {
                out.append(arr[i][j].viewBy(player)+"\n");
            }
        }
        checkRep();
        return out.toString();
    }

}
