package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mikkel Brunstedt Nørgaard s224562
 */

public class Gear implements FieldAction {

    private String direction;

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
            switch(direction){
                case "clockwise":
                    gameController.turnRight(player);
                case "anticlockwise":
                    gameController.turnLeft(player);
            }
        }
        return true;
    }
}
