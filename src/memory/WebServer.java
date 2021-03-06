/* Copyright (c) 2017-2020 MIT 6.031 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package memory;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import memory.web.ExceptionsFilter;
import memory.web.HeadersFilter;
import memory.web.LogFilter;

/**
 * HTTP web game server.
 * 
 * <p>PS4 instructions: the specifications of {@link #WebServer(Board, int)},
 *    {@link #port()}, {@link #start()}, and {@link #stop()} may not be changed.
 */
public class WebServer {
    
    private final HttpServer server;
    
    // Abstraction function:
    //   AF(server) = game server
    // Representation invariant:
    //   True
    // Safety from rep exposure:
    //   fields are private final and never exposed to client.
    // Thread safety argument:
    //   TODO
    
    /**
     * Make a new web game server using board that listens for connections on port.
     * 
     * @param board shared game board
     * @param port server port number
     * @throws IOException if an error occurs starting the server
     */
    public WebServer(Board board, int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // handle concurrent requests with multiple threads
        server.setExecutor(Executors.newCachedThreadPool());
        
        HeadersFilter headers = new HeadersFilter(Map.of(
                // allow requests from web pages hosted anywhere
                "Access-Control-Allow-Origin", "*",
                // all responses will be plain-text UTF-8
                "Content-Type", "text/plain; charset=utf-8"
                ));
        List<Filter> filters = List.of(new ExceptionsFilter(), new LogFilter(), headers);
        
        // handle requests for paths that start with /hello/, e.g. /hello/world
        HttpContext hello = server.createContext("/hello/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                handleHello(exchange);
            }
        });
        // -or- use a lambda expression with "->"
        //HttpContext hello = server.createContext("/hello/", exchange -> handleHello(exchange));
        // -or- use a method reference with "::"
        //HttpContext hello = server.createContext("/hello/", this::handleHello);
        
        // add logging to the /hello/ handler and set required HTTP headers
        //   (do this on all your handlers)
        hello.getFilters().addAll(filters);
        
        // handle requests for /look/player
        HttpContext look = server.createContext("/look/", exchange -> handleLook(board,exchange));
        look.getFilters().addAll(filters);
        
        // handle requests for /flip/player/row,column
        HttpContext flip = server.createContext("/flip/", exchange -> handleFlip(board,exchange));
        flip.getFilters().addAll(filters);
        
        // handle requests for /watch/player
        HttpContext watch = server.createContext("/watch/", exchange -> handleWatch(board,exchange));
        watch.getFilters().addAll(filters);
    }
    
    
    /**
     * @return the port on which this server is listening for connections
     */
    public int port() {
        return server.getAddress().getPort();
    }
    
    /**
     * Start this server in a new background thread.
     */
    public void start() {
        System.err.println("Server will listen on " + server.getAddress());
        server.start();
    }
    
    /**
     * Stop this server. Once stopped, this server cannot be restarted.
     */
    public void stop() {
        System.err.println("Server will stop");
        server.stop(0);
    }
    
    /*
     * Handle a request for /hello/<what> by responding with "Hello, <what>!" if
     *   <what> is a single word; error 404 otherwise.
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     */
    private void handleHello(HttpExchange exchange) throws IOException {
        // if you want to know the requested path:
        final String path = exchange.getRequestURI().getPath();
        
        // it will always start with the base path from server.createContext():
        final String base = exchange.getHttpContext().getPath();
        assert path.startsWith(base);
        
        final String whatToGreet = path.substring(base.length());
        
        final String response;
        if (whatToGreet.matches("\\w+")) {
            // if the request is valid, respond with HTTP code 200 to indicate success
            // - response length 0 means a response will be written
            // - you must call this method before calling getResponseBody()
            exchange.sendResponseHeaders(200, 0);
            response = "Hello, " + whatToGreet + "!";
        } else {
            // otherwise, respond with HTTP code 404 to indicate an error
            exchange.sendResponseHeaders(404, 0);
            response = "Go away, " + whatToGreet + ".";
        }
        // write the response to the output stream using UTF-8 character encoding
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);
        // println(..) will append a newline and auto-flush
        // - to write without a newline, use e.g. print(..) and flush()
        out.println(response);
        
        // if you do not close the exchange, the response will not be sent!
        exchange.close();
    }
    
    /*
     * Handle a request for /look/<playerName> by responding with string representation of
     * board viewed by player
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     */
    private void handleLook(Board board, HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        final String base = exchange.getHttpContext().getPath();
        
        final String playerName = path.substring(base.length());
        board.registerPlayer(playerName);
        final Player player = board.getPlayer(playerName);
        
        final String response;
        
        exchange.sendResponseHeaders(200, 0);
        response = board.viewBy(player);
            
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);

        out.println(response);
        
        exchange.close();
    }
    
    /*
     * Handle a request for /flip/<playerName>/<row>,<col> by modifying the game state and
     * responding with string representation of board viewed by player
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     */
    private void handleFlip(Board board, HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        final String base = exchange.getHttpContext().getPath();
        
        final String str = path.substring(base.length());
        Matcher m = Pattern.compile("(\\w+)/([0-9]+),([0-9]+)").matcher(str);
        m.matches();
        String playerName = m.group(1);
        int r = Integer.valueOf(m.group(2));
        int c = Integer.valueOf(m.group(3));
        board.registerPlayer(playerName);
        final Player player = board.getPlayer(playerName);
        
        board.turn(player, r, c);
        
        final String response;
        exchange.sendResponseHeaders(200, 0);
        response = board.viewBy(player);
            
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);

        out.println(response);
        
        exchange.close();
    }
    
    /*
     * Handle a request for /watch/<playerName> by responding with string representation of
     * board viewed by player when a change occurs
     * 
     * @param exchange HTTP request/response, modified by this method to send a
     *                 response to the client and close the exchange
     */
    private void handleWatch(Board board, HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        final String base = exchange.getHttpContext().getPath();
        
        final String playerName = path.substring(base.length());
        board.registerPlayer(playerName);
        final Player player = board.getPlayer(playerName);
        
        final String response;
        
        exchange.sendResponseHeaders(200, 0);
        response = board.watch(player);
            
        OutputStream body = exchange.getResponseBody();
        PrintWriter out = new PrintWriter(new OutputStreamWriter(body, UTF_8), true);

        out.println(response);
        
        exchange.close();
    }
}
