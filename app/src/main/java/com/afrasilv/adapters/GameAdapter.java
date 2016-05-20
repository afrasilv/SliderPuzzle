package com.afrasilv.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afrasilv.dao.Piece;
import com.afrasilv.sliderpuzzle.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by alex on 20/05/16.
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.PieceViewHolders> {

    private List<Piece> itemList;
    private Context context;
    private int maxRows = 4;

    public GameAdapter(Context context, List<Piece> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public PieceViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.piece_layout, null);
        PieceViewHolders pvh = new PieceViewHolders(layoutView);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PieceViewHolders holder, int position) {
        String text = String.valueOf(itemList.get(position).getInitialPosition());
        holder.initialPosition.setText(text);

        holder.pieceImage.setImageBitmap(itemList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public class PieceViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView initialPosition;
        public ImageView pieceImage;

        public PieceViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            initialPosition = (TextView) itemView.findViewById(R.id.initial_position);
            pieceImage = (ImageView) itemView.findViewById(R.id.piece_image);
        }

        @Override
        public void onClick(View view) {
            Piece piece = itemList.get(getAdapterPosition());

            int posX = piece.getIndexX();
            int posY = piece.getIndexY();
            boolean change = false;

            if(posX != 0){
                if(itemList.get(getAdapterPosition()-1).getBlankImage()){
                    int pos = getAdapterPosition()-1;
                    Piece blankPiece = itemList.get(pos);
                    itemList.remove(pos);

                    piece.setIndexX(blankPiece.getIndexX());
                    piece.setIndexY(blankPiece.getIndexY());
                    blankPiece.setIndexX(posX);
                    blankPiece.setIndexY(posY);

                    itemList.add(pos, piece);
                    itemList.set(getAdapterPosition(), blankPiece);
                    change = true;
                }
            }
            if((!change) && (posX != maxRows -1)){
                if(itemList.get(getAdapterPosition()+1).getBlankImage()){
                    int pos = getAdapterPosition()+1;
                    Piece blankPiece = itemList.get(pos);
                    itemList.remove(pos);

                    piece.setIndexX(blankPiece.getIndexX());
                    piece.setIndexY(blankPiece.getIndexY());
                    blankPiece.setIndexX(posX);
                    blankPiece.setIndexY(posY);

                    itemList.set(getAdapterPosition(), blankPiece);
                    itemList.add(pos, piece);
                    change = true;
                }
            }
            if((!change) && (posY != 0)){
                if(itemList.get(getAdapterPosition()-maxRows).getBlankImage()){
                    int pos = getAdapterPosition()-maxRows;
                    Piece blankPiece = itemList.get(pos);
                    itemList.remove(pos);

                    piece.setIndexX(blankPiece.getIndexX());
                    piece.setIndexY(blankPiece.getIndexY());
                    blankPiece.setIndexX(posX);
                    blankPiece.setIndexY(posY);

                    itemList.add(pos, piece);
                    itemList.set(getAdapterPosition(), blankPiece);
                    change = true;
                }
            }
            if((!change) && (posY != maxRows -1)){
                if(itemList.get(getAdapterPosition()+maxRows).getBlankImage()){
                    int pos = getAdapterPosition()+maxRows;
                    Piece blankPiece = itemList.get(pos);
                    itemList.remove(pos);

                    piece.setIndexX(blankPiece.getIndexX());
                    piece.setIndexY(blankPiece.getIndexY());
                    blankPiece.setIndexX(posX);
                    blankPiece.setIndexY(posY);

                    itemList.set(getAdapterPosition(), blankPiece);
                    itemList.add(pos, piece);
                    change = true;
                }
            }

            if(change) {
                boolean finish = true;
                for(int i =0; i<itemList.size(); i++){
                    if(i != itemList.get(i).getInitialPosition() -1) {
                        finish = false;
                        break;
                    }
                }

                if(!finish)
                    notifyDataSetChanged();
                else
                    Toast.makeText(context, "WOW! YOU FINISHED! WELLDONE!", Toast.LENGTH_LONG).show();
            }

        }
    }
}
