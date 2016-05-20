package com.afrasilv.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by alex on 19/05/16.
 */
public  class Utils {

    /**
     * Splits the source image and return a list of bitmaps
     *
     * @param bitmap
     *            The source image to split
     * @param rows
     *            Image rows
     * @param cols
     *            Image cols
     *
     * @return ArrayList of suffle bitmaps
     */
    public static ArrayList<Bitmap> splitImage(Bitmap bitmap, int rows, int cols) {

        // To store all the small image chunks in bitmap format in this list
        ArrayList<Bitmap> chunkedImage = new ArrayList<>(rows * cols);

        //Size for next chunk
        int chunkSideLength = bitmap.getHeight() / rows;

        int yCoord = 0;
        for (int y = 0; y < rows; y++) {
            int xCoord = 0;
            for (int x = 0; x < cols; x++) {
                chunkedImage.add(Bitmap.createBitmap(bitmap, xCoord, yCoord, chunkSideLength, chunkSideLength));
                xCoord += chunkSideLength;
            }

            yCoord += chunkSideLength;
        }

        //shuffle arrays
        Collections.shuffle(chunkedImage);

        return chunkedImage;
    }

    /**
     * Returns a random number
     *
     * @param maxNum maxValue of random number
     * @return random number
     */
    public static int randomInt(int maxNum){
        Random random = new Random(System.currentTimeMillis());

        return random.nextInt(maxNum);
    }

    /**
     * Return a bitmap with all pieces
     *
     * @param imageChunks list of pieces
     * @param cols
     * @param rows
     * @return
     */
    public static Bitmap mergeImage(ArrayList<Bitmap> imageChunks, int cols, int rows) {

        // create a bitmap of a size which can hold the complete image after merging
        Bitmap bitmap = Bitmap.createBitmap(imageChunks.get(0).getWidth() * cols, imageChunks.get(0).getHeight() * rows, Bitmap.Config.ARGB_4444);

        // create a canvas for drawing all those small images
        Canvas canvas = new Canvas(bitmap);
        int count = 0;
        Bitmap currentChunk;

        //Array of previous row chunks bottom y coordinates
        int[] yCoordinates = new int[cols];
        Arrays.fill(yCoordinates, 0);

        for (int y = 0; y < rows; ++y) {
            int xCoord = 0;
            for (int x = 0; x < cols; ++x) {
                currentChunk = imageChunks.get(count);
                canvas.drawBitmap(currentChunk, xCoord, yCoordinates[x], null);
                xCoord += currentChunk.getWidth();
                yCoordinates[x] += currentChunk.getHeight();
                count++;
            }
        }

        return bitmap;
    }
}
