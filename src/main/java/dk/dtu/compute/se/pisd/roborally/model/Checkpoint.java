package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

/**
 *
 *
 *
 * @author Mikkel Brunstedt Nørgaard
 */

public class Checkpoint implements FieldAction {

    public final int checkpointNumber;

    public Checkpoint(int checkpointNumber) {
        this.checkpointNumber = checkpointNumber;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            player.setCurrentCheckpoint(this.checkpointNumber);
            if (player.getCurrentCheckpoint() >= gameController.board.getCheckpoints().size()) {
                gameController.playerHasWon(player);
            }
        }
        return true;
    }
}
