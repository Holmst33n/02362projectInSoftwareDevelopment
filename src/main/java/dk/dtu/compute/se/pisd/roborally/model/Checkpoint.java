package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Mikkel Brunstedt Nørgaard s224562
 */

public class Checkpoint implements FieldAction {

    private int checkpointNumber;

    /**
     * doAction method which is called every time each player has taken their turn
     * @param gameController
     * @param space
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            player.setCurrentCheckpoint(this.checkpointNumber);
            if (player.getCurrentCheckpoint() >= gameController.board.getCheckpointAmount()) {
                player.setWon(true);
            }
        }
        return true;
    }
    public int getCheckpointNumber(){
        return checkpointNumber;
    }
}
