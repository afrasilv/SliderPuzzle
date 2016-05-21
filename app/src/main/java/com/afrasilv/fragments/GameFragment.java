package com.afrasilv.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afrasilv.adapters.GameAdapter;
import com.afrasilv.dao.Piece;
import com.afrasilv.interfaces.OnStartDragListener;
import com.afrasilv.sliderpuzzle.MainActivity;
import com.afrasilv.sliderpuzzle.R;
import com.afrasilv.utils.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

/**
 * Created by alex on 19/05/16.
 */
public class GameFragment extends Fragment implements OnStartDragListener {
    private ArrayList<Piece> pieceList;
    private GameAdapter gameAdapter;

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pieceList = ((MainActivity) getActivity()).getPieceList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View game_view = inflater.inflate(R.layout.game_layout, container, false);

        RecyclerView recyclerView = (RecyclerView) game_view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        String numRowsCols = ((MainActivity) getActivity()).getSharedPref("num_rowscols");
        int maxRows = Integer.valueOf(numRowsCols);

        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(maxRows, 1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        gameAdapter = new GameAdapter(getContext(), pieceList);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(gameAdapter);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(gameAdapter);

        return game_view;
    }

    public Object[] getItemList(){
        return gameAdapter.getItemList();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {}

    public void setSolutionList(ArrayList<String> solutionList){
        gameAdapter.setSolutionList(solutionList);
    }
}
