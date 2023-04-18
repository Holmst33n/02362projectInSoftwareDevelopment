package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;

/**
 * This class acts as a factory to create our board
 *
 * @author Johan Holmsteen, s224568
 * @author Joes Nicolaisen, s22456?
 *
 */

public class BoardFactory {
    public static Board createBoard(String boardname) {
        return LoadBoard.loadBoard(boardname);
    }

}