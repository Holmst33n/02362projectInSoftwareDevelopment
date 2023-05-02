package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * test to check checkpoints; creates two players, adds a checkpoint on the space one of the players is on,
 * executes the checkpoints and then checks if the player on the checkpoint had their currentCheckpoint
 * incremented.
 * @author Mikkel Brunstedt Nørgaard, s224562
 */
public class CheckpointTest {
    @Test
    public void testCheckpointFunctionality() {
        int checkpointNumber = 1;
        Board board = new Board(8, 8, "defaultboard");
        GameController gameController = new GameController(board);

        Player player1 = new Player(board, "blue", "Player 1");
        Player player2 = new Player(board, "red", "Player 2");
        board.addPlayer(player1);
        board.addPlayer(player2);

        Space player1Space = board.getSpace(2, 2);
        Space player2Space = board.getSpace(4, 4);
        player1Space.setPlayer(player1);
        player1.setSpace(player1Space);
        player2Space.setPlayer(player2);
        player2.setSpace(player2Space);

        Checkpoint checkpoint = new Checkpoint();
        player1Space.actions.add(checkpoint);
        gameController.executeCheckpoints();


        assertEquals(checkpointNumber, player1.getCurrentCheckpoint(), "Player1's checkpoint should be incremented");
        assertEquals(0, player2.getCurrentCheckpoint(), "Player2's checkpoint should not be incremented");
    }


}
