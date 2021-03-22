/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
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
            Spot[][] boardarr = new Spot[R][C];
            for (int r=0;r<R;r++) {
                for (int c=0;c<C;c++) {
                    String sym = br.readLine();
                    boardarr[r][c] = new Card(sym);
                }
            }
            return new Board(R,C,boardarr);
        }
    }
    
    public Board(int R, int C, Spot[][] boardarr) {
        this.R = R;
        this.C = C;
        this.boardarr = boardarr;
    }
    
    // TODO fields
    private final int R;
    private final int C;
    private Spot[][] boardarr;
    
    public Spot getSpot(int i, int j) {
        return boardarr[i][j];
    }
    
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (int i=0;i<R;i++) {
            for (int j=0;j<C;j++) {
                out.append(boardarr[i][j].toString()+" ");
            }
            out.append("\n");
        }
        return out.toString();
    }
    
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
