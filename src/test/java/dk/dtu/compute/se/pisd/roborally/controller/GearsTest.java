package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.EAST;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.NORTH;

/**
 * Test to check if gears rotate players as intended. Creates a board and adds a player and a gear on a space.
 * The test then executes the gear action and controls if the players heading is the right orientation both for
 * a clockwise and counterclockwise gear rotation.
 *
 * @author Joes Hasselriis Nicolaisen, s224564
 */
public class GearsTest {

    @Test
    void testGearFunctionality() {
        Board board = new Board(8, 8, "testbboard");
        GameController gameController = new GameController(board);

        Player player1 = new Player(board,"white", "player1");

        board.addPlayer(player1);

        Space space = board.getSpace(0, 0);
        player1.setSpace(space);
        player1.setHeading(NORTH);

        Gear gear = new Gear();
        String direction = "clockwise";
        space.actions.add(gear);
        gear.setDirection(direction);
        gameController.executeActions();

        // assert that player1's heading has been updated correctly
        Assertions.assertEquals(EAST, player1.getHeading(), "Player should be facing east after clockwise turn");

        // set the direction for the action
        direction = "counterclockwise";
        gear.setDirection(direction);

        // call the doAction() method
        gameController.executeActions();

        // assert that player1's heading has been updated correctly
        Assertions.assertEquals(NORTH, player1.getHeading(), "Player should be facing north after counterclockwise turn");
    }
}
