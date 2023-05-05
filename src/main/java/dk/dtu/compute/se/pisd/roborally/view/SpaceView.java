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

import dk.dtu.compute.se.pisd.roborally.view.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    /**
     * Updates the player view; shows an image instead of the simple colored triange. Depending on player number, an image is picked.
     * @author Ekki
     * @author Mikkel Brunstedt Nørgaard, s224562
     */
    private void updatePlayer() {
        Player player = space.getPlayer();
        String imagePath = "", imagePathWon = "";
        if(player != null) {
            String playerName = player.getName();
            switch (playerName) {
                case "Player 1":
                    imagePath = "/images/player1.png";
                    break;
               case "Player 2":
                   imagePath = "/images/player2.png";
                   break;
               case "Player 3":
                   imagePath = "/images/player3.png";
                   break;
               case "Player 4":
                    imagePath = "/images/player4.png";
                    break;
               case "Player 5":
                    imagePath = "/images/player5.png";
                    break;
               case "Player 6":
                    imagePath = "/images/player6.png";
                    break;
               case "Player 7":
                    imagePath = "/images/player7.png";
                    break;
               case "Player 8":
                    imagePath = "/images/player8.png";
                    break;
                }
                if(player.hasWon()){
                    imagePath = "/images/player1won.png";
                    System.out.println("siii");
                }
                if(imagePath != null) {
                    InputStream imageStream = getClass().getResourceAsStream(imagePath);
                    Image img = new Image(imageStream);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitHeight(SPACE_HEIGHT * 0.75);
                    imageView.setFitWidth(SPACE_WIDTH * 0.75);

                    StackPane stack = new StackPane();
                    stack.getChildren().addAll(imageView);
                    imageView.setRotate(((90 * player.getHeading().ordinal()) % 360)+180);
                    this.getChildren().add(stack);
                }
            }
        }


    /**
     * @author Ekki
     * @author Johan Holmsteen s224568
     */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        String imagePath = "/images/blank.PNG";
        if (subject == this.space) {
            drawImage(imagePath, 1, 1);

            for(FieldAction action : space.getActions()){
                if(action instanceof ConveyorBelt) {
                    drawConveyorBelt(space.getConveyorBelt());
                } if(action instanceof Gear) {
                    drawGear(space.getGear());
                } if (action instanceof Checkpoint) {
                    drawCheckpoint(space.getCheckpoint());
                }
            }
            drawWalls();
            updatePlayer();
        }
    }

    /**
     * method to draw walls on the board, uses hashmap to decide which image(s) to show on the board.
     * @author Johan Holmsteen s224568
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    private void drawWalls() {
        Heading[] wallHeadings = space.getWalls().toArray(new Heading[0]);
        StackPane stack = new StackPane();

        if (wallHeadings != null) {
            Map<Heading, String> imagePathMap = new HashMap<>();
            imagePathMap.put(Heading.SOUTH, "/images/wallsouth.png");
            imagePathMap.put(Heading.WEST, "/images/wallwest.png");
            imagePathMap.put(Heading.NORTH, "/images/wallnorth.png");
            imagePathMap.put(Heading.EAST, "/images/walleast.png");

            for (Heading heading : wallHeadings) {
                String imagePath = imagePathMap.get(heading);

                if (imagePath != null) {
                    InputStream imageStream = getClass().getResourceAsStream(imagePath);
                    Image img = new Image(imageStream);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitHeight(SPACE_HEIGHT);
                    imageView.setFitWidth(SPACE_WIDTH);

                    stack.getChildren().addAll(imageView);
                }
            }
            this.getChildren().add(stack);
        }
    }

    /**
     * Draw gears; used in the updateView to show gears. Gears are drawn with files clockwise.png and counterclockwise.png in resource folder
     * @param gear
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    private void drawGear(Gear gear){
        String direction = gear.getDirection();
        if (!space.getActions().isEmpty()) {
            String imagePath = null;
            if (direction.equals("clockwise")) {
                imagePath = "/images/clockwise.JPG";
            } else if (direction.equals("counterclockwise")) {
                imagePath = "/images/counterclockwise.JPG";
            }
            if (imagePath != null) {
                drawImage(imagePath, 1, 1);
            }
        }
    }

    /**
     * Draw gears; used in the updateView to show checkpoints.
     * @param checkpoint
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    private void drawCheckpoint(Checkpoint checkpoint){
        String imagePath = "";
        if(checkpoint != null){
            int checkpointNumber = checkpoint.getCheckpointNumber();
            switch(checkpointNumber) {
                case 0:
                    imagePath = "/images/checkpoint1.png";
                    break;
                case 1:
                    imagePath = "/images/checkpoint2.png";
                    break;
                case 2:
                    imagePath = "/images/checkpoint3.png";
                    break;
                case 3:
                    imagePath = "/images/checkpoint4.png";
                    break;
                case 4:
                    imagePath = "/images/checkpoint5.png";
                    break;
                case 5:
                    imagePath = "/images/checkpoint6.png";
                    break;
                case 6:
                    imagePath = "/images/checkpoint7.png";
                    break;
                case 7:
                    imagePath = "/images/checkpoint8.png";
                    break;
            }

            if(imagePath != null) {
                drawImage(imagePath, 1, 1);
            }
        }
    }

    /**
     * Draws conveyor belt on the canvas
     * @param conveyorBelt
     * @author Joes Nicolaisen s224564
     */
    private void drawConveyorBelt(ConveyorBelt conveyorBelt){
        String imagePath = "/images/conveyor.JPG";
        if(imagePath != null) {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            Image img = new Image(imageStream);
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(SPACE_HEIGHT);
            imageView.setFitWidth(SPACE_WIDTH);

            StackPane stack = new StackPane();
            stack.getChildren().addAll(imageView);
            imageView.setRotate(((90 * conveyorBelt.getHeading().ordinal()) % 360)+180);
            this.getChildren().add(stack);
        }
    }

    /**
     * method to easily draw an image from imagepath and scale
     * @param imagePath
     * @param scaleY
     * @param scaleX
     * @author Mikkel Brunstedt Nørgaard s224562
     */
    private void drawImage(String imagePath, int scaleY, int scaleX){
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image img = new Image(imageStream);
        ImageView imageView = new ImageView(img);
        imageView.setFitHeight(SPACE_HEIGHT*scaleY);
        imageView.setFitWidth(SPACE_WIDTH*scaleX);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(imageView);
        this.getChildren().add(stack);
    }
}
