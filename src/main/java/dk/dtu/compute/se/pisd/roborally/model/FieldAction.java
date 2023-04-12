package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

/**
 * Simple interface that can support an action on a field (Space).
 * This can eg. be the Conveyor Belts that move the players.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */

public interface FieldAction {

    /**
     * doAction method which we implement in the Checkpoint record
     * @param gameController
     * @param space
     * @return
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    boolean doAction(GameController gameController, Space space);
}
