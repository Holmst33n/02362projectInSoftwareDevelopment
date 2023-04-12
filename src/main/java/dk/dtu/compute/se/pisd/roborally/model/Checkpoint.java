package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;

/**
 * @author Mikkel Brunstedt Nørgaard s224562
 */

public record Checkpoint(int checkpointNumber) implements FieldAction {

    /**
     * doAction method which is called every time each player has taken their turn
     * @param gameController
     * @param space
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            player.setCurrentCheckpoint(this.checkpointNumber);
            System.out.println("Player" + space.getPlayer().getName() + " has reached checkpoint");
            if (player.getCurrentCheckpoint() >= gameController.board.getCheckpoints().size()) {
                gameController.playerHasWon(player);
            }
        }
        return true;
    }
}
