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
import com.afrasilv.sliderpuzzle.MainActivity;
import com.afrasilv.sliderpuzzle.R;
import com.afrasilv.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by alex on 19/05/16.
 */
public class InitGameFragment extends Fragment {

    private ArrayList<Piece> pieceList;
    private int maxRows;


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

        CardView mixCv = (CardView) init_game_view.findViewById(R.id.mix_image_button);

        mixCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(getContext())
                        .load(R.drawable.im1)
                        .into(imgBoard, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap bitmap = ((BitmapDrawable) imgBoard.getDrawable()).getBitmap();

                                getMaxRows();

                                ArrayList<Piece> piecesTempList =  Utils.splitImage(bitmap, maxRows, maxRows, getContext());

                                //piecesTempList = insertBlankImage(piecesTempList);

                                setPieceList(piecesTempList);

                                Bitmap finalImg = Utils.mergeImage(piecesTempList, maxRows, maxRows);

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

        CardView startGame = (CardView) init_game_view.findViewById(R.id.start_game_button);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((pieceList == null) || (pieceList.isEmpty())){
                   Snackbar.make(init_game_view, "You have mix the image before!", Snackbar.LENGTH_LONG)
                           .show();
                }
                else{
                    ((MainActivity) getActivity()).changeFragment(0);
                }
            }
        });

        return init_game_view;
    }

    public ArrayList<Piece> insertBlankImage(ArrayList<Piece> piecesTempList){

        int blankPieceIndex = Utils.randomInt(piecesTempList.size());

        Piece piece = piecesTempList.get(blankPieceIndex);
        piecesTempList.remove(blankPieceIndex);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.blankpiece);

        piece.setBlankImage(true);

        piece.setImage(Bitmap.createScaledBitmap(bm, piecesTempList.get(0).getImage().getWidth(), piecesTempList.get(0).getImage().getHeight(), true));

        piecesTempList.add(blankPieceIndex, piece);

        setPieceList(piecesTempList);


        return piecesTempList;
    }

    public void getMaxRows(){
        String numRowsCols = ((MainActivity) getActivity()).getSharedPref("num_rowscols");

        this.maxRows =  Integer.valueOf(numRowsCols);
    }

    public void setPieceList(ArrayList<Piece> piecesTempList){
        for(int i=0; i<piecesTempList.size(); i++){
            piecesTempList.get(i).setIndexX(i % this.maxRows);
            piecesTempList.get(i).setIndexY( (i / this.maxRows) % this.maxRows);
        }

        this.pieceList = piecesTempList;

        ((MainActivity) getActivity()).setPieceList(this.pieceList);

    }
}
