package com.afrasilv.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.afrasilv.dao.Piece;

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
     * @return ArrayList of suffle pieces
     */
    public static ArrayList<Piece> splitImage(Bitmap bitmap, int rows, int cols, Context context) {

        // To store all the small image chunks in bitmap format in this list
        ArrayList<Piece> piecesList = new ArrayList<>(rows * cols);

        int width = getScreenHeight(context);

        bitmap=Bitmap.createScaledBitmap(bitmap, width, width, true);


        //Size for next chunk
        int chunkSideLength = bitmap.getHeight() / rows;

        int yCoord = 0;
        for (int y = 0; y < rows; y++) {
            int xCoord = 0;
            for (int x = 0; x < cols; x++) {
                Piece piece = new Piece();
                piece.setBlankImage(false);

                piece.setImage(Bitmap.createBitmap(bitmap, xCoord, yCoord, chunkSideLength, chunkSideLength));

                int initialPos = x;
                if(y!=0) {
                    if(x!=0)
                        initialPos = initialPos + (y*rows);
                    else
                        initialPos = rows * y;
                }

                piece.setInitialPosition(initialPos + 1);

                piecesList.add(piece);

                xCoord += chunkSideLength;
            }

            yCoord += chunkSideLength;
        }

        //shuffle arrays
        Collections.shuffle(piecesList);

        return piecesList;
    }

    private static int getScreenHeight(Context context) {
        int height;

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        } else {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            height = display.getHeight();  // deprecated
        }

        return height;
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
     * @param cols number of cols that form the image
     * @param rows number of rows that form the image
     * @return merge image with disorder chunks
     */
    public static Bitmap mergeImage(ArrayList<Piece> imageChunks, int cols, int rows) {

        // create a bitmap of a size which can hold the complete image after merging
        Bitmap bitmap = Bitmap.createBitmap(imageChunks.get(0).getImage().getWidth() * cols, imageChunks.get(0).getImage().getHeight() * rows, Bitmap.Config.ARGB_4444);

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
                currentChunk = imageChunks.get(count).getImage();
                canvas.drawBitmap(currentChunk, xCoord, yCoordinates[x], null);
                xCoord += currentChunk.getWidth();
                yCoordinates[x] += currentChunk.getHeight();
                count++;
            }
        }

        return bitmap;
    }
}
