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

import dk.dtu.compute.se.pisd.roborally.Exception.ImpossibleMoveException;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Johan Holmsteen, s224568
 * @author Joes Nicolaisen, s224564
 * @author Mikkel Brunstedt Nørgaard s224562
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        if(space.getPlayer() == null) {     //checks if a player is already on the space (defensive programming)
            Player currentPlayer = board.getCurrentPlayer();    //sets currentPlayer
            if(currentPlayer != null) {     //checks if the player exists (defensive programming)
                space.setPlayer(currentPlayer);     //sets the currentPlayer on the space

                int currentPlayerNumber = board.getPlayerNumber(currentPlayer);         //finds the player who has the turn
                int nextPlayerNumber = (currentPlayerNumber + 1) % board.getPlayersNumber();    //changes nextPlayerNumber to be the next player in line

                Player nextPlayer = board.getPlayer(nextPlayerNumber);      //sets next player depending on nextPlayerNumber
                board.setCurrentPlayer(nextPlayer);     //hands the turn over to nextPlayer

                board.setCounter(board.getCounter()+1);    //updates our counter once per turn;
            }
        }
    }

    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Executes every step in the activation phase: moves robots depending on command cards, and then executes actions and checkpoints.
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()){
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                if(currentPlayer.hasWon()){
                    startWonPhase();
                }
                else if (board.getPhase() == Phase.ACTIVATION) {
                    if (board.getPlayerNumber(currentPlayer) + 1 < board.getPlayersNumber()) {
                        board.setCurrentPlayer(board.getPlayer(board.getPlayerNumber(currentPlayer) + 1));
                    } else {
                        executeActions();
                        step++;
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    }
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    executeCheckpoints();
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                        board.notifyChange();
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Executes the actions on the board; conveyorbelts and gears
     *
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void executeActions() {
        for (Player player : board.getPlayers()) {
            for (FieldAction action : player.getSpace().getActions()) {
                if (player.hasWon())
                    break;
                else if (action instanceof Checkpoint)
                    break;
                else
                    action.doAction(this, player.getSpace());
            }
        }
    }

    /**
     * executes the checkpoints on the board
     *
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void executeCheckpoints() {
        for (Player player : board.getPlayers()) {
            for (FieldAction action : player.getSpace().getActions()) {
                if (player.hasWon())
                    break;
                else if (action instanceof Checkpoint)
                    action.doAction(this, player.getSpace());
            }
        }
    }

    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
            }
        }
    }

     public void executeCommandOptionAndContinue(@NotNull Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.PLAYER_INTERACTION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                    board.setPhase(Phase.ACTIVATION);
                    executeCommand(currentPlayer, option);
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    executeActions();
                    executeCheckpoints();
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * Moves the player one space forward in the direction of their
     * heading on the game board. If the space is not a valid move
     * or the player is not on the same board, the move is not executed.
     *
     * @param player the player to move
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Johan Holmsteen, s224568
     * @author Joes Nicolaisen, s224564
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void moveForward(@NotNull Player player) {
        if (player.board == board) {
            Space space = player.getSpace();
            Heading heading = player.getHeading();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {

                }
            }
        }
    }

    /**
     *
     * Moves the player forward on the conveyorbelt; very similar to moveForward, but
     * takes an extra parameter (heading) from the conveyorbelt to move the player
     * in the direction of the conveyorbelt
     *
     * @param player the player to move
     * @param heading the direction to move the player
     *
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void moveForwardConveyorBelt(@NotNull Player player, @NotNull Heading heading) {
        if (player.board == board) {
            Space space = player.getSpace();

            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {

                }
            }
        }
    }

    /**
     *
     * Moves the player to a space in a given heading.
     * If there is another player on the space, the method
     * will recursively push that player in the direction of the heading.
     *
     * @param player the player to be moved
     * @param space the target space for the player to be moved to
     * @param heading the heading direction in which the player will move
     * @throws ImpossibleMoveException if the movement is impossible (e.g.
     * if a wall blocks the movement)
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Johan Holmsteen, s224568
     * @author Joes Nicolaisen, s224564
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void moveToSpace(@NotNull Player player,@NotNull Space space,@NotNull Heading heading) throws ImpossibleMoveException {

        Player other = space.getPlayer();
        if(other != null) {
            Space target = board.getNeighbour(space, heading);
            if(target != null) {
                moveToSpace(other, target, heading);
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        player.setSpace(space);
    }


    /**
     * moves the player forward 3 times via moveForward method
     *
     * @param player the player to be moved
     *
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void fastForward(@NotNull Player player) {
        for (int i = 0; i < 3; i++) {
            Space space = player.getSpace();
            if (space != null) {
                moveForward(player);
            }
        }
    }

    /**
     * turns player right
     *
     * @param player the player to be turned
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Johan Holmsteen, s224568
     * @author Joes Nicolaisen, s224564
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void turnRight(@NotNull Player player) {
        Space space = player.getSpace();
        if (space != null){
            player.setHeading(player.getHeading().next());
        }
    }

    /**
     * turns player left
     *
     * @param player the player to be turned
     *
     * @author Ekkart Kindler, ekki@dtu.dk
     * @author Johan Holmsteen, s224568
     * @author Joes Nicolaisen, s224564
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public void turnLeft(@NotNull Player player) {
        Space space = player.getSpace();
        if (space != null){
            player.setHeading(player.getHeading().prev());
        }
    }

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Method to show that a player has won.
     * Is called if a player has reached all checkpoints in the correct order
     *
     * @author Mikkel Brunstedt Nørgaard s224562
     */

    public void startWonPhase(){
        board.setPhase(Phase.PLAYER_WON);
        board.setStep(0);
    }
}
