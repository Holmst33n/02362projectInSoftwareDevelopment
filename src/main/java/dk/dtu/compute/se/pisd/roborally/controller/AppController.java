/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.view.observer.Observer;
import dk.dtu.compute.se.pisd.roborally.view.observer.Subject;

import dk.dtu.compute.se.pisd.roborally.RoboRally;

import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.BoardFactory;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Johan Holmsteen, s224568
 * @author Joes Nicolaisen, s224564
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private RoboRally roboRally;
    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * The player is introduced to the gameboard available in resources/boards as .json files
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Johan Holmsteen, s224568
     */

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            // Below the board is loaded from the json file the player chooses

            File folder = new File("src/main/resources/boards");
            File[] listOfFiles = folder.listFiles();
            List<String> jsonFiles = new ArrayList<>();
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".json")) {
                    String fileNameWithoutExtension = file.getName().replaceAll("\\.json$", "");
                    jsonFiles.add(fileNameWithoutExtension);
                }
            }

            ChoiceDialog<String> choseBoardDialog = new ChoiceDialog<>(null, jsonFiles);
            choseBoardDialog.setTitle("Choose a board");
            choseBoardDialog.setHeaderText("Choose a board from the list below");
            choseBoardDialog.setContentText("Board:");
            Optional<String> boardResult = choseBoardDialog.showAndWait();

            if (boardResult.isPresent()) {
                String fileNameWithoutExtension = boardResult.get().replaceAll("\\.json$", "");
                Board board = BoardFactory.createBoard(fileNameWithoutExtension);
                gameController = new GameController(board);
                int no = result.get();
                for (int i = 0; i < no; i++) {
                    Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                    board.addPlayer(player);
                    player.setSpace(board.getSpace(i % board.width, i));
                }

                gameController.startProgrammingPhase();

                roboRally.createBoardView(gameController);
            }
        }
    }

    /**
     * Saves the game, in its current state to the database.
     *
     * @author Joes Nicolaisen, s224564
     * @author Johan Holmsteen, s224568
     */
    public void saveGame() {
        if(gameController.board.getGameId() == null) {
            RepositoryAccess.getRepository().createGameInDB(gameController.board);
        } else {
            RepositoryAccess.getRepository().updateGameInDB(gameController.board);
        }
    }

    /**
     * Opens up a list of games previously saved to the game database
     *
     * @author Joes Nicolaisen, s224564
     * @author Johan Holmsteen, s224568
     */
    public void loadGame() {
        List<GameInDB> savedGames = RepositoryAccess.getRepository().getGames();
        ChoiceDialog<GameInDB> dialog = new ChoiceDialog<>(null, savedGames);
        dialog.setTitle("Choose a game");
        dialog.setHeaderText("Choose a game from the list below");
        dialog.setContentText("Game:");
        Optional<GameInDB> result = dialog.showAndWait();

        if (result.isPresent()) {
            GameInDB selectedGame = result.get();
            Board board = RepositoryAccess.getRepository().loadGameFromDB(selectedGame.id);
            gameController = new GameController(board);
            roboRally.createBoardView(gameController);
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally? All unsaved progress will be lost!");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
