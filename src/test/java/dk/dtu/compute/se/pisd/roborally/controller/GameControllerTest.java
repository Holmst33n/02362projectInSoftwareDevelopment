package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }


    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void turnRight(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Heading firstHeading = current.getHeading();
        gameController.turnRight(current);
        Heading nextHeading = current.getHeading();

        Assertions.assertEquals(firstHeading.next(), nextHeading, "Player " + current.getName() + " should be turned right!");
    }

    @Test
    void turnLeft(){
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();
        Heading firstHeading = current.getHeading();
        gameController.turnLeft(current);
        Heading nextHeading = current.getHeading();

        Assertions.assertEquals(firstHeading.prev(), nextHeading, "Player " + current.getName() + " should be turned right!");
    }

    @Test
    void push(){
        Board board = gameController.board;
        Player player1 = new Player(board, "blue", "Player 1");
        Player player2 = new Player(board, "red", "player 2");

        Space space1 = board.getSpace(7, 1);
        Space space2 = board.getSpace(7, 2);

        player1.setSpace(board.getSpace(7, 0));
        player1.setHeading(Heading.SOUTH);
        player2.setSpace(board.getSpace(7, 1));

        gameController.moveForward(player1);

        Assertions.assertEquals(space1, player1.getSpace(), "Player " + player1.getName() + " should be on space " + space1);
        Assertions.assertEquals(space2, player2.getSpace(), "Player " + player2.getName() + " should be on space " + space2);

    }

}