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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;

import java.util.Collection;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;

    private boolean checkpoint;

    private Player player;

    public List<Heading> getWallHeading() {
        wallHeading = spaceTemplate.walls;
        return wallHeading;
    }

    private List<Heading> wallHeading;

    private boolean hasWalls;

    private SpaceTemplate spaceTemplate;

    private ConveyorBelt conveyorBelt;


    /**
     * @param board
     * @param x
     * @param y
     * @param checkpoint
     * @param hasWalls
     * @author Johan Holmsteen, s224568
     */
    public Space(Board board, int x, int y, boolean checkpoint, boolean hasWalls) {
        this.board = board;
        this.x = x;
        this.y = y;
        this.checkpoint = checkpoint;
        player = null;
        this.spaceTemplate = new SpaceTemplate(); // initialize spaceTemplate with a new object
    }

    /**
     * Sets checkpoint
     *
     * @param checkpoint
     * @author Mikkel Nørgaard
     */
    public void setCheckpoint(boolean checkpoint) {
        this.checkpoint = checkpoint;
    }

    /**
     * Gets checkpoint
     *
     * @return checkpoint
     * @author Mikkel Nørgaard
     */
    public boolean isCheckpoint() {
        return checkpoint;
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

    public boolean isHasWalls() {
        return hasWalls;
    }

    public void setHasWalls(boolean hasWalls) {
        this.hasWalls = hasWalls;
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
        return spaceTemplate.actions;
    }

    public Collection<Heading> getWalls() {
        return spaceTemplate.walls;
    }

}
