package com.afrasilv.asynctask;

import android.app.Activity;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afrasilv.sliderpuzzle.MainActivity;
import com.afrasilv.utils.Solver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 21/05/16.
 */
public class GetSolution extends AsyncTask<Void, Void, ArrayList<String>> {
    private Activity activity;
    private MaterialDialog progressDialog;
    private Object[] itemList;


    public GetSolution(Activity activity, MaterialDialog prDialog, Object[] itemList) {

        this.activity = activity;
        this.progressDialog = prDialog;
        this.itemList = itemList;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {
        List<Integer> goalList = new ArrayList<>();

        for (int i = 0; i < itemList.length; i++) {
            goalList.add(i+1);
        }

        goalList.set(goalList.size()-1, 0);

        String numRowsCols = ((MainActivity) activity).getSharedPref("num_rowscols");
        int maxRows = Integer.valueOf(numRowsCols);

        ArrayList<String> solution= null;

        try{
            solution = Solver.solve(itemList, goalList.toArray(), maxRows);
        } catch(Exception e){
            this.cancel(true);
        }

        return solution;
    }

    @Override
    protected void onPostExecute(ArrayList<String> result) {
        super.onPostExecute(result);

        if (progressDialog != null)
            progressDialog.dismiss();

        ((MainActivity) activity).setSolutionList(result);

    }
}
