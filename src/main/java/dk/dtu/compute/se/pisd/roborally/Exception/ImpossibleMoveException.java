package dk.dtu.compute.se.pisd.roborally.Exception;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 *
 * An exception to indicate that a move is impossible.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Joes Nicolaisen, s224564
 *
 */
public class ImpossibleMoveException extends Exception {

    private Player player;
    private Space space;
    private Heading heading;

    /**
     * Constructor to create a new instance of the exception class with
     * the given player, space and heading information.
     *
     * @param player the player attempting to make an impossible move
     * @param space the space the player is trying to move to
     * @param heading the heading the player is trying to move towards
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Joes Nicolaisen, s224564
     *
     */
    public ImpossibleMoveException(Player player, Space space, Heading heading) {
        super("move impossible");
        this.player = player;
        this.space = space;
        this.heading = heading;
    }
}
