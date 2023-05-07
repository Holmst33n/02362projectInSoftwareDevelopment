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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.view.observer.Subject;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * The player class represents the robots on the game board.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Johan Holmsteen, s224568
 * @author Mikkel Brunstedt Nørgaard s224562
 * @author Joes Nicolaisen, s224564
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;

    private int currentCheckpoint = 0;

    private CommandCardField[] program;
    private CommandCardField[] cards;

    private boolean won = false;

    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * getter and setter to see which checkpoint a player is currently at.
     * @return currentCheckpoint
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public int getCurrentCheckpoint() {
        return currentCheckpoint;
    }

    public void setCurrentCheckpoint(int checkpointNumber) {
        //check if the current checkpoint is supposed to be after the last checkpoint the player was on
        if(checkpointNumber == (this.currentCheckpoint)) {
            this.currentCheckpoint++;
            //remove this printline when message is shown in the view instead
            System.out.println(space.getPlayer().getName() + " has reached checkpoint " + currentCheckpoint);
        }
    }

    public void setCurrentCheckpointDB(int checkpointNumber){
        currentCheckpoint = checkpointNumber;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Getter for a specific programming card that a player has in its register
     * @param i the card's  placement in the programming card array
     * @return the chosen program card
     * @author Joes Nicolaisen, s224564
     * @author Johan Holmsteen, s224568
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * Getter for a specific hand card that a player has in its hand
     * @param i the card's placement in the hand card array
     * @return the chosen hand card
     * @author Joes Nicolaisen, s224564
     * @author Johan Holmsteen, s224568
     */
    public CommandCardField getCardField(int i) {
        return cards[i];
    }


    public CommandCardField[] getProgram() {
        return program;
    }


    public CommandCardField[] getCards() {
        return cards;
    }

    /**
     * Setter a specific programming card that a player has in its register
     * @param card the command card that is to be inserted into the register
     * @param i the card's  placement in the programming card array
     * @author Joes Nicolaisen, s224564
     * @author Johan Holmsteen, s224568
     */
    public void setProgramField(CommandCardField card, int i) {
        this.program[i] = card;

    }

    /**
     * Setter a specific hand card that a player has in its hand
     * @param card the hand card that is to be inserted into its hand
     * @param i the card's  placement in the hand card array
     * @author Joes Nicolaisen, s224564
     * @author Johan Holmsteen, s224568
     */
    public void setCardField(CommandCardField card, int i) {
        this.cards[i] = card;
    }

    public boolean hasWon(){
        return won;
    }

    public void setWon(Boolean bool){
        won = bool;
        board.setWon(bool);
    }

    /**
     * Getter for the image of a players robot on the board.
     * @return A string of the image path for the player robot.
     * @author Joes Nicolaisen s224564
     */
    public String getImage() {
        return "/images/player" + (board.getPlayerNumber(this)+1) + ".png";
    }
}
