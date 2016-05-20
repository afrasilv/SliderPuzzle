package com.afrasilv.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afrasilv.dao.Piece;
import com.afrasilv.sliderpuzzle.R;
import com.afrasilv.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 19/05/16.
 */
public class InitGameFragment extends Fragment {

    private List<Piece> pieceList;
    private final int maxRows = 4;


    public static InitGameFragment newInstance() {
        return new InitGameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View init_game_view = inflater.inflate(R.layout.init_game_layout, container, false);

        final ImageView imgBoard = (ImageView) init_game_view.findViewById(R.id.imageView);

        Picasso.with(getContext())
                .load(R.drawable.im1)
                .into(imgBoard);

        CardView cv = (CardView) init_game_view.findViewById(R.id.init_game_button);

        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(getContext())
                        .load(R.drawable.im1)
                        .into(imgBoard, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap bitmap = ((BitmapDrawable) imgBoard.getDrawable()).getBitmap();

                                ArrayList<Bitmap> bitmapList =  Utils.splitImage(bitmap, maxRows, maxRows);

                                bitmapList = insertBlankImage(bitmapList);

                                Bitmap finalImg = Utils.mergeImage(bitmapList, maxRows, maxRows);

                                imgBoard.setImageBitmap(finalImg);
                            }

                            @Override
                            public void onError() {
                                Snackbar.make(init_game_view, "Error on load image", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });



            }
        });

        return init_game_view;
    }

    public ArrayList<Bitmap> insertBlankImage(ArrayList<Bitmap> bitmapList){

        int blankPieceIndex = Utils.randomInt(bitmapList.size());

        bitmapList.remove(blankPieceIndex);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.blankpiece);

        bm=Bitmap.createScaledBitmap(bm, bitmapList.get(0).getWidth(), bitmapList.get(0).getHeight(), true);

        bitmapList.add(blankPieceIndex, bm);

        setPieceList(bitmapList, blankPieceIndex);

        return bitmapList;
    }

    public void setPieceList(ArrayList<Bitmap> bitmapList, int blankPos){
        this.pieceList = new ArrayList<>();

        for(int i=0; i<bitmapList.size(); i++){
            Piece piece = new Piece();
            piece.setImage(bitmapList.get(i));
            piece.setIndexX(i % this.maxRows);
            piece.setIndexY( (i / this.maxRows) % this.maxRows);

            if(i == blankPos)
                piece.setBlankImage(true);
            else
                piece.setBlankImage(false);

            this.pieceList.add(piece);
        }
    }
}
