package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConveyorBeltTest {
    @Test
    void conveyorBelt(){
        Board board = new Board(8, 8, "defaultboard");
        GameController gameController = new GameController(board);

        Player player1 = new Player(board, "blue", "Player 1");
        board.addPlayer(player1);
        player1.setSpace(board.getSpace(1, 1));

        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setHeading(Heading.SOUTH);

        Space origin = player1.getSpace();
        origin.actions.add(conveyorBelt);
        Space destination = board.getNeighbour(origin, Heading.SOUTH);
        gameController.executeActions();
        Space actual = player1.getSpace();

        assertEquals(actual, destination, "Player " + player1.getName() + " should be on space " + destination.getX() + ", " + destination.getY());
    }

    @Test
    void conveyorBeltPush(){
        Board board = new Board(8, 8, "defaultboard");
        GameController gameController = new GameController(board);

        Player player1 = new Player(board, "blue", "Player 1");
        Player player2 = new Player(board, "red", "Player 2");
        board.addPlayer(player1);
        board.addPlayer(player2);
        player1.setSpace(board.getSpace(1, 1));
        player2.setSpace(board.getSpace(1,2));

        ConveyorBelt conveyorBelt = new ConveyorBelt();
        conveyorBelt.setHeading(Heading.SOUTH);

        Space origin = player1.getSpace();
        origin.actions.add(conveyorBelt);
        Space destination = board.getNeighbour(origin, Heading.SOUTH);
        gameController.executeActions();
        Space actual = player1.getSpace();

        assertEquals(actual, destination, "Player " + player1.getName() + " should be on space (1,2)");
        assertEquals(player2.getSpace(), board.getNeighbour(destination, Heading.SOUTH), "Player " + player2.getName() + " should be on space (1,3)");
    }

}
