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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ...
 *
 * Represents a tile (space) on the game board which can hold a player,
 * various field actions and walls.
 *
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Johan Holmsteen, s224568
 * @author Mikkel Brunstedt Nørgaard s224562
 *
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;

    public List<Heading> walls = new ArrayList<>();

    public Collection<FieldAction> actions = new ArrayList<>();
    private Player player;

    private List<Heading> wallHeading;

    public List<Heading> getWallHeading() {
        wallHeading = walls;
        return wallHeading;
    }

    public boolean isHasWalls() {
        return hasWalls;
    }

    private boolean hasWalls;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
        this.wallHeading = new ArrayList<>();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Collection<FieldAction> getActions() {
        return actions;
    }

    public Collection<Heading> getWalls() {
        return walls;
    }

    public ConveyorBelt getConveyorBelt(){
        ConveyorBelt conveyorBelt = null;
        for (FieldAction action : this.actions) {
            if(action instanceof ConveyorBelt){
                conveyorBelt = (ConveyorBelt) action;
            }
        }
        return conveyorBelt;
    }

    public Gear getGear(){
        Gear gear = null;
        for (FieldAction action : this.actions) {
            if(action instanceof Gear){
                gear = (Gear) action;
            }
        }
        return gear;
    }

    public Checkpoint getCheckpoint(){

        Checkpoint checkpoint = null;

        for (FieldAction action : this.actions) {
            if(action instanceof Checkpoint){
                checkpoint = (Checkpoint) action;
            }
        }
        return checkpoint;
    }

    public void setHasWalls(boolean hasWalls) {
        this.hasWalls = hasWalls;
    }
}
