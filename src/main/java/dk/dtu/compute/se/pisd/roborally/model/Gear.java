package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.jetbrains.annotations.NotNull;

/**
 *
 * Implementation of the active space Gear
 *
 * @author Mikkel Brunstedt Nørgaard s224562
 */

public class Gear implements FieldAction {

    private String direction;

    /**
     * doAction method which is called every time each player has taken their turn; turns player clockwise or anticlockwise
     * @param gameController
     * @param space
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            if(direction.equals("clockwise")) {
                space.getPlayer().setHeading(player.getHeading().next());
            }
            else if (direction.equals("counterclockwise")){
                space.getPlayer().setHeading(player.getHeading().prev());
            }
        }
        return true;
    }

    public String getDirection(){
        return this.direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
