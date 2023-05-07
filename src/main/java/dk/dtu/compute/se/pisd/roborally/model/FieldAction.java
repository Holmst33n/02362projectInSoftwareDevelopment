package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

/**
 * Simple interface that can support an action on a field (Space).
 * This can e.g. be the Conveyor Belts that move the players.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */

public interface FieldAction {

    //doAction method, will be implemented in Checkpoint, Gear, Antenna etc
    boolean doAction(GameController gameController, Space space);
}
