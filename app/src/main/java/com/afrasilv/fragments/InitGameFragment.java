package com.afrasilv.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afrasilv.sliderpuzzle.R;
import com.squareup.picasso.Picasso;

/**
 * Created by alex on 19/05/16.
 */
public class InitGameFragment extends Fragment {


    public static InitGameFragment newInstance() {
        InitGameFragment fragment = new InitGameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.init_game_layout, container, false);

        ImageView imgBoard = (ImageView) v.findViewById(R.id.imageView);

        Picasso.with(getContext())
                .load(R.drawable.im1)
                .into(imgBoard);

        return v;
    }
}
