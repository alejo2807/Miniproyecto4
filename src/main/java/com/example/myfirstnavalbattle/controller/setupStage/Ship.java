package com.example.myfirstnavalbattle.controller.setupStage;

import com.example.myfirstnavalbattle.view.AnimationsManager;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class Ship extends Rectangle {

    private ImagePattern currentImage;
    private ImagePattern imageVertical;
    private ImagePattern imageHorizontal;

    private boolean vertical;
    private int size;

    public static Ship currentlyDraggedShip;

    public Ship(int size) {
        switch (size){
            case 1:
                setImages(1);
                setHeight(40);
                this.size = 1;
                break;
            case 2:
                setImages(2);
                setHeight(90);
                this.size = 2;
                break;
            case 3:
                setImages(3);
                setHeight(140);
                this.size = 3;
                break;
            case 4:
                setImages(4);
                setHeight(190);
                this.size = 4;
                break;
            default:
                System.out.println("Error, numero invalido");
        }
        setWidth(48);
        vertical = true;
    }

    private void setImages(int numImage){
        imageVertical = createImage(numImage, true);
        imageHorizontal = createImage(numImage, false);
        currentImage = imageVertical;
        setFill(currentImage);
    }


    public void rotateShip(){
        if(vertical){
            vertical = false;
            currentImage = imageHorizontal;
        }
        else{
            vertical = true;
            currentImage = imageVertical;
        }
        setFill(currentImage);
        double newHeight = getHeight();
        setHeight(getWidth());
        setWidth(newHeight);
    }


    private ImagePattern createImage(int size, boolean vertical){
        String rotation = (vertical)? "VERTICAL" : "HORIZONTAL";
        return new ImagePattern(new Image(
                Objects.requireNonNull(AnimationsManager.class.getResourceAsStream(
                        "/com/example/myfirstnavalbattle/Images/ships/SIZE_"+ size +"_"+rotation+".png"))));
    }

    public Image getImage(){
        return currentImage.getImage();
    }

    public boolean isVertical() {
        return vertical;
    }
    public int getSize() {
        return size;
    }
}
