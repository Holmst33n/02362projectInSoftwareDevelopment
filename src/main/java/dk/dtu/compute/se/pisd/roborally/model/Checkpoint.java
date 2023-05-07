package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * Implementation of the checkpoint space
 *
 * @author Mikkel Brunstedt Nørgaard s224562
 */

public class Checkpoint implements FieldAction {

    private int checkpointNumber;

    /**
     * doAction method which is called every time each player has taken their turn.
     * Updates the current checkpoint for the player occupying a given space
     * and checks if they have won the game.
     *
     * @param gameController the game controller object controlling the game
     * @param space the space where the player is currently located
     * @return true if the action was successful
     *
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
