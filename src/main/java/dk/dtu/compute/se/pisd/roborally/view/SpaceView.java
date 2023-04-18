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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 50; // 60; // 75;
    final public static int SPACE_WIDTH = 50;  // 60; // 75;

    public final Space space;

    /**
     * Initializes the spaces. Gives them the color blue if the space is a checkpoint; if not, they are
     * colored black and white in a checkerboard pattern.
     *
     * @param space
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        if (space.isCheckpoint()) {
            this.setStyle("-fx-background-color: blue;");
        } else if (!space.isCheckpoint()) {
            if ((space.x + space.y) % 2 == 0) {
                this.setStyle("-fx-background-color: white;");
            } else {
                this.setStyle("-fx-background-color: black;");
            }
        }


        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    /**
     * @author Johan Holmsteen S224568
     */

    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if (subject == this.space) {
            if (space.isCheckpoint()) {
                this.setStyle("-fx-background-color: blue;");
            } else if (!space.isCheckpoint()) {
                if ((space.x + space.y) % 2 == 0) {
                    this.setStyle("-fx-background-color: white;");
                } else {
                    this.setStyle("-fx-background-color: black;");
                }
            }
            for(FieldAction action : space.getActions()){
                if (action instanceof Checkpoint) {
                    this.setStyle("-fx-background-color: blue;");
                } else if(action instanceof ConveyorBelt) {
                    drawConveyorBelt(space.getConveyorBelt());
                }
            }
            drawWalls();
            updatePlayer();
        }
    }

    private void drawWalls(){
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        if(!space.getActions().isEmpty() || !space.getWalls().isEmpty()) {
            if(!space.getWalls().isEmpty()){
                gc.setStroke(Color.RED);
                gc.setLineWidth(5);
                gc.setLineCap(StrokeLineCap.ROUND);
                Heading[] wallHeadings = space.getWalls().toArray(new Heading[0]);

                for(int i = 0; i <wallHeadings.length; i++) {
                    switch (wallHeadings[i]) {
                        case SOUTH:
                            gc.strokeLine(2, SPACE_HEIGHT-2, SPACE_WIDTH-2, SPACE_HEIGHT-2);
                            break;
                        case WEST:
                            gc.strokeLine(2, 2, 2, SPACE_HEIGHT-2);
                            break;
                        case NORTH:
                            gc.strokeLine(2, 2, SPACE_WIDTH-2, 2);
                            break;
                        case EAST:
                            gc.strokeLine(SPACE_WIDTH-2, 2, SPACE_WIDTH-2, SPACE_HEIGHT-2);
                            break;
                    }
                }
            }
            this.getChildren().add(canvas);
        }
    }

    private void drawConveyorBelt(ConveyorBelt conveyorBelt){
        Canvas canvas = new Canvas(SPACE_WIDTH, SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.save();
        gc.translate(SPACE_WIDTH / 2, SPACE_HEIGHT / 2);

        switch (conveyorBelt.getHeading()){
            case SOUTH:
                break;
            case WEST:
                gc.rotate(90);
                break;
            case NORTH:
                gc.rotate(180);
                break;
            case EAST:
                gc.rotate(270);
                break;
        }
        gc.setFill(Color.LIGHTGRAY);
        gc.fillPolygon(new double[]{0, -18, 18}, new double[]{18, -18, -18},3);

        gc.restore();
        this.getChildren().add(canvas);
    }
}
