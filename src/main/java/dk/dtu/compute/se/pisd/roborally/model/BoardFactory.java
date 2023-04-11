package dk.dtu.compute.se.pisd.roborally.model;

/**
 * This class acts as a factory to create our board
 *
 * @author Johan Holmsteen, s224568
 * @author Joes Nicolaisen, s22456?
 *
 */

public class BoardFactory {
    public Board createBoard() {
       return new Board(8,8, "firstBoard");
    }
}