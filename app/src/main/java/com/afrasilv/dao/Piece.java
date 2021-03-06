package com.afrasilv.dao;

import android.graphics.Bitmap;

/**
 * Created by alex on 19/05/16.
 */
public class Piece {

    public int indexX;
    public int indexY;
    public int initialPosition;
    public Bitmap image;
    public boolean blankImage;

    public Piece() {
    }

    public int getIndexX() {
        return indexX;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    public int getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(int initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean getBlankImage() {
        return blankImage;
    }

    public void setBlankImage(boolean blankImage) {
        this.blankImage = blankImage;
    }

}
