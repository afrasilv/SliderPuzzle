package com.afrasilv.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afrasilv.dao.Piece;
import com.afrasilv.interfaces.ItemTouchHelperAdapter;
import com.afrasilv.interfaces.ItemTouchHelperViewHolder;
import com.afrasilv.sliderpuzzle.MainActivity;
import com.afrasilv.sliderpuzzle.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 20/05/16.
 */
public class GameAdapter extends RecyclerView.Adapter<GameAdapter.PieceViewHolders> implements ItemTouchHelperAdapter {

    private List<Piece> itemList;
    private Context context;
    private int maxRows = 4;
    private boolean showHints = true;
    private int lastPosChangeFromSol = 0;

    private Handler handler;
    private Runnable runnable;

    public GameAdapter(Context context, List<Piece> itemList) {
        this.itemList = itemList;
        this.context = context;

        String numRowsCols = ((MainActivity) context).getSharedPref("num_rowscols");
        maxRows = Integer.valueOf(numRowsCols);

        String boolHints = ((MainActivity) context).getSharedPref("show_hints");
        showHints = Boolean.valueOf(boolHints);
    }


    /**
     * Method that return an object array with actual positions to find a solution
     * @return object array with actual position
     */
    public Object[] getItemList(){
        List<Integer> listItems = new ArrayList<>();

        for(int i=0; i<this.itemList.size(); i++){
            if(itemList.get(i).getBlankImage())
                listItems.add(0);
            else
                listItems.add(this.itemList.get(i).getInitialPosition());
        }

        Object[] test = listItems.toArray();

        return test;
    }

    @Override
    public PieceViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.piece_layout, null);
        return new PieceViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(PieceViewHolders holder, int position) {
        if(showHints) {
            String text = String.valueOf(itemList.get(position).getInitialPosition());
            holder.initialPosition.setText(text);
        }

        holder.pieceImage.setImageBitmap(itemList.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Piece from = this.itemList.get(fromPosition);
        Piece dest = this.itemList.get(toPosition);

        boolean nextTo = false;
        if((fromPosition == toPosition - 1) || (fromPosition == toPosition + 1)
                ||(fromPosition == toPosition - maxRows) || (fromPosition == toPosition + maxRows))
            nextTo = true;

        if((nextTo) && ((from.getBlankImage()) || (dest.getBlankImage()))) {
            int posX = from.getIndexX();
            int posY = from.getIndexY();
            from.setIndexX(dest.getIndexX());
            from.setIndexY(dest.getIndexY());
            dest.setIndexX(posX);
            dest.setIndexY(posY);

            Collections.swap(this.itemList, fromPosition, toPosition);
            notifyDataSetChanged();
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position) {

    }

    /**
     * Method that do an animation with a solution list
     * @param solutionList string list with solution moves
     */
    public void setSolutionList(final ArrayList<String> solutionList) {
        int i=0;
        boolean findIt = false;
        while((!findIt) &&(i<this.itemList.size())){
            if(this.itemList.get(i).getBlankImage())
                findIt = true;
            i++;
        }

        lastPosChangeFromSol = i-1;
        handler = new Handler();
        runnable = new Runnable() {

            public void run() {
                if(!solutionList.isEmpty()){
                    switch (solutionList.get(0)){
                        case "N":
                            checkNextTo(lastPosChangeFromSol-maxRows);
                            lastPosChangeFromSol=lastPosChangeFromSol-maxRows;
                            break;
                        case "S":
                            checkNextTo(lastPosChangeFromSol+maxRows);
                            lastPosChangeFromSol=lastPosChangeFromSol+maxRows;
                            break;
                        case "E":
                            checkNextTo(lastPosChangeFromSol+1);
                            lastPosChangeFromSol=lastPosChangeFromSol+1;
                            break;
                        case "W":
                            checkNextTo(lastPosChangeFromSol-1);
                            lastPosChangeFromSol=lastPosChangeFromSol-1;
                            break;
                    }

                    notifyDataSetChanged();
                    solutionList.remove(0);
                }
                else{
                    Snackbar.make(((MainActivity) context).getView(), "Welldone!! You solved the puzzle! (It'll be our secret)", Snackbar.LENGTH_LONG)
                            .show();

                    handler.removeCallbacks(runnable);
                    return;
                }

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);
    }

    /**
     * Check next to pieces
     * @param clickPos position clicked
     * @return boolean to check if piece was changed
     */
    public boolean checkNextTo(int clickPos){
        Piece piece = itemList.get(clickPos);

        int posX = piece.getIndexX();
        int posY = piece.getIndexY();

        if(posX != 0){
            if(itemList.get(clickPos-1).getBlankImage()){
                int pos = clickPos-1;
                Piece blankPiece = itemList.get(pos);
                itemList.remove(pos);

                piece.setIndexX(blankPiece.getIndexX());
                piece.setIndexY(blankPiece.getIndexY());
                blankPiece.setIndexX(posX);
                blankPiece.setIndexY(posY);

                itemList.add(pos, piece);
                itemList.set(clickPos, blankPiece);
                return true;
            }
        }
        if(posX != maxRows -1){
            if(itemList.get(clickPos+1).getBlankImage()){
                int pos = clickPos+1;
                Piece blankPiece = itemList.get(pos);
                itemList.remove(pos);

                piece.setIndexX(blankPiece.getIndexX());
                piece.setIndexY(blankPiece.getIndexY());
                blankPiece.setIndexX(posX);
                blankPiece.setIndexY(posY);

                itemList.set(clickPos, blankPiece);
                itemList.add(pos, piece);
                return true;
            }
        }
        if(posY != 0){
            if(itemList.get(clickPos-maxRows).getBlankImage()){
                int pos = clickPos-maxRows;
                Piece blankPiece = itemList.get(pos);
                itemList.remove(pos);

                piece.setIndexX(blankPiece.getIndexX());
                piece.setIndexY(blankPiece.getIndexY());
                blankPiece.setIndexX(posX);
                blankPiece.setIndexY(posY);

                itemList.add(pos, piece);
                itemList.set(clickPos, blankPiece);
                return true;
            }
        }
        if(posY != maxRows -1){
            if(itemList.get(clickPos+maxRows).getBlankImage()){
                int pos = clickPos+maxRows;
                Piece blankPiece = itemList.get(pos);
                itemList.remove(pos);

                piece.setIndexX(blankPiece.getIndexX());
                piece.setIndexY(blankPiece.getIndexY());
                blankPiece.setIndexX(posX);
                blankPiece.setIndexY(posY);

                itemList.set(clickPos, blankPiece);
                itemList.add(pos, piece);
                return true;
            }
        }

        return false;
    }

    /**
     * Check row and col to find a blank piece and move it
     * @param piece piece select
     * @return boolean to check if piece was changed
     */
    public boolean checkRowAndCol(Piece piece){
        int posX;
        int posY;

        boolean blankSameRowOrCol = false;

        //checkCols
        int i = 0;
        while((!blankSameRowOrCol) && (i<maxRows)){
            if(itemList.get(i+(piece.getIndexY() * maxRows)).getBlankImage()){
                blankSameRowOrCol = true;
            }
            i++;
        }
        if(blankSameRowOrCol){
            int blankX = i;
            int indexY = piece.getIndexY();
            if(blankX > piece.getIndexX()) {
                while (i > 1) {
                    int pos = i + (indexY * maxRows) - 1;
                    Piece pieceOneToMove = itemList.get(pos);
                    itemList.remove(pos);

                    Piece pieceTwoToMove = itemList.get(pos-1);

                    posX = pieceTwoToMove.getIndexX();
                    posY = pieceTwoToMove.getIndexY();

                    pieceTwoToMove.setIndexX(pieceOneToMove.getIndexX());
                    pieceTwoToMove.setIndexY(pieceOneToMove.getIndexY());
                    pieceOneToMove.setIndexX(posX);
                    pieceOneToMove.setIndexY(posY);

                    itemList.set(pos -1, pieceOneToMove);
                    itemList.add(pos, pieceTwoToMove);

                    i--;
                }
            }
            else{
                while (i < maxRows) {
                    int pos = i + (indexY * maxRows) -1;
                    Piece pieceOneToMove = itemList.get(pos);
                    itemList.remove(pos);

                    Piece pieceTwoToMove = itemList.get(pos);

                    posX = pieceTwoToMove.getIndexX();
                    posY = pieceTwoToMove.getIndexY();

                    pieceTwoToMove.setIndexX(pieceOneToMove.getIndexX());
                    pieceTwoToMove.setIndexY(pieceOneToMove.getIndexY());
                    pieceOneToMove.setIndexX(posX);
                    pieceOneToMove.setIndexY(posY);

                    itemList.set(pos, pieceTwoToMove);
                    itemList.add(pos+1, pieceOneToMove);

                    i++;
                }
            }
            return true;
        }else {
            //Check Rows
            i = 0;
            blankSameRowOrCol=false;

            while((!blankSameRowOrCol) && (i<maxRows)){
                int a = piece.getIndexX() +( i * maxRows);
                if(itemList.get(piece.getIndexX() +( i * maxRows)).getBlankImage()){
                    blankSameRowOrCol = true;
                    break;
                }
                i++;
            }
            if(blankSameRowOrCol) {
                int blankY = i;
                if (blankY > piece.getIndexY()) {
                    while (i > 0) {
                        int pos = piece.getIndexX() +( i * maxRows);
                        Piece pieceOneToMove = itemList.get(pos);
                        itemList.remove(pos);

                        Piece pieceTwoToMove = itemList.get(pos - maxRows);

                        posX = pieceTwoToMove.getIndexX();
                        posY = pieceTwoToMove.getIndexY();

                        pieceTwoToMove.setIndexX(pieceOneToMove.getIndexX());
                        pieceTwoToMove.setIndexY(pieceOneToMove.getIndexY());
                        pieceOneToMove.setIndexX(posX);
                        pieceOneToMove.setIndexY(posY);

                        itemList.set(pos - maxRows, pieceOneToMove);
                        itemList.add(pos, pieceTwoToMove);

                        i--;
                    }
                } else {
                    while (i < maxRows -1) {
                        int pos = piece.getIndexX() +( i * maxRows);
                        Piece pieceOneToMove = itemList.get(pos);
                        Piece pieceTwoToMove = itemList.get(pos+maxRows);

                        itemList.remove(pos+maxRows);


                        posX = pieceTwoToMove.getIndexX();
                        posY = pieceTwoToMove.getIndexY();

                        pieceTwoToMove.setIndexX(pieceOneToMove.getIndexX());
                        pieceTwoToMove.setIndexY(pieceOneToMove.getIndexY());
                        pieceOneToMove.setIndexX(posX);
                        pieceOneToMove.setIndexY(posY);

                        itemList.set(pos, pieceTwoToMove);
                        itemList.add(pos+maxRows, pieceOneToMove);

                        i++;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public class PieceViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {

        public TextView initialPosition;
        public ImageView pieceImage;
        public CardView cardView;

        public PieceViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            initialPosition = (TextView) itemView.findViewById(R.id.initial_position);
            pieceImage = (ImageView) itemView.findViewById(R.id.piece_image);
            cardView = (CardView) itemView.findViewById(R.id.piece_cv);
        }

        @Override
        public void onClick(View view) {
            boolean change = checkNextTo(getAdapterPosition());

            Piece piece = itemList.get(getAdapterPosition());

            if((!change) && (!piece.getBlankImage())){
                change = checkRowAndCol(piece);
            }

            if(change) {
                boolean finish = true;
                for(int i =0; i<itemList.size(); i++){
                    if(i != itemList.get(i).getInitialPosition() -1) {
                        finish = false;
                        break;
                    }
                }
                notifyDataSetChanged();

                if(finish)
                    Snackbar.make(((MainActivity) context).getView(), "WOW! YOU FINISHED! WELLDONE!", Snackbar.LENGTH_LONG)
                            .show();
            }

        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }
}
