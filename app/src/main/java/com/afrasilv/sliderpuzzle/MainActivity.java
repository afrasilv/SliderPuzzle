package com.afrasilv.sliderpuzzle;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afrasilv.asynctask.GetSolution;
import com.afrasilv.dao.Piece;
import com.afrasilv.fragments.GameFragment;
import com.afrasilv.fragments.InitGameFragment;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Stack<Fragment> fragmentStack;
    private FragmentManager fragmentManager;

    private Fragment actualFragment;

    private ArrayList<Piece> pieceList;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentStack = new Stack<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        changeFabImage(0);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsFabButton();
            }
        });

        actualFragment = InitGameFragment.newInstance();
        fragmentManager = getSupportFragmentManager();

        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) finish();
            }
        });

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.content_frame, actualFragment, actualFragment.getTag());
        fragmentTransaction.commit();

        fragmentStack.push(actualFragment);
    }

    public void settingsFabButton(){
        MaterialDialog md = new MaterialDialog.Builder(MainActivity.this)
                .title(R.string.app_name)
                .customView(R.layout.settings_dialog_layout, false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        CheckBox cbhints = (CheckBox) dialog.findViewById(R.id.checkbos_hints);

                        addSharedPref("show_hints", String.valueOf(cbhints.isChecked()));

                        EditText editText = (EditText) dialog.findViewById(R.id.input_rowcols);

                        addSharedPref("num_rowscols", editText.getText().toString());

                    }
                })
                .build();

        CheckBox cbhints = (CheckBox) md.findViewById(R.id.checkbos_hints);

        String checkValue = getSharedPref("show_hints");
        if(!checkValue.isEmpty()) {
            boolean checkValueFromPref = Boolean.valueOf(checkValue);
            cbhints.setChecked(checkValueFromPref);
        }

        EditText editText = (EditText) md.findViewById(R.id.input_rowcols);

        String numberRowCols = getSharedPref("num_rowscols");
        if(!numberRowCols.isEmpty())
            editText.setText(numberRowCols);

        md.show();
    }

    public void addSharedPref(String name, String value) {
        SharedPreferences pref = this.getSharedPreferences("settingspref", MODE_PRIVATE);

        // We need an editor object to make changes
        SharedPreferences.Editor edit = pref.edit();

        edit.putString(name, value);

        // Commit the changes
        edit.apply();
    }

    public String getSharedPref(String name){
        SharedPreferences pref = getSharedPreferences("settingspref", MODE_PRIVATE);

        if(name.equals("num_rowscols"))
            return pref.getString(name, "4");
        else
            return pref.getString(name, "true");
    }

    public void changeFragment(int id) {
        switch(id){
            case 0:
                actualFragment = GameFragment.newInstance();

                fab.hide();

                changeFabImage(1);

                fab.show();

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callToSolvePuzzle();
                    }
                });

                break;
            case 1:
                actualFragment = InitGameFragment.newInstance();

                fab.hide();

                changeFabImage(0);

                fab.show();

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settingsFabButton();
                    }
                });

                break;
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, actualFragment, actualFragment.getTag()).commit();

        fragmentStack.push(actualFragment);
    }

    public void changeFabImage(int mode){
        Bitmap fabImage;
        if(mode ==0) {
            fabImage = BitmapFactory.decodeResource(getResources(), R.drawable.sett);
            fab.setColorNormal(getResources().getColor(R.color.blue));
            fab.setColorPressed(getResources().getColor(R.color.blue_pressed));
            fab.setColorRipple(getResources().getColor(R.color.blue_pressed));
        }
        else {
            fabImage = BitmapFactory.decodeResource(getResources(), R.drawable.sos);
            fab.setColorNormal(getResources().getColor(R.color.red));
            fab.setColorPressed(getResources().getColor(R.color.red_pressed));
            fab.setColorRipple(getResources().getColor(R.color.red_pressed));
        }

        fabImage = Bitmap.createScaledBitmap(fabImage, 100, 100, true);

        fab.setImageBitmap(fabImage);
    }

    public void callToSolvePuzzle(){
        try {
            final GetSolution getSolution = new GetSolution(MainActivity.this, progressDialog, ((GameFragment) actualFragment).getItemList());
            getSolution.execute();

            progressDialog = new MaterialDialog.Builder(MainActivity.this)
                    .title("We're thinking in a solution")
                    .content("Miau miau miau miau miau miau miau miau miau")
                    .progress(true, 0)
                    .negativeText("Cancel")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            getSolution.cancel(true);
                        }
                    })
                    .show();

        }
        catch(Exception e){
            Snackbar.make(getView(), "No solution", Snackbar.LENGTH_LONG)
                    .show();
        }


    }

    public void setPieceList(ArrayList<Piece> pieceList){
        this.pieceList = pieceList;
    }

    public ArrayList<Piece> getPieceList(){
        return this.pieceList;
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.size() > 1) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(fragmentStack.pop());

            Fragment fragment = fragmentStack.lastElement();

            if(fragment instanceof InitGameFragment) {
                fab.hide();

                changeFabImage(0);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        settingsFabButton();
                    }
                });
                fab.show();
            }

            ft.replace(R.id.content_frame, fragment, fragment.getTag()).commit();

        } else {
            finish();
        }
    }

    public void setSolutionList(ArrayList<String> solutionList){
        if(progressDialog!=null)
            progressDialog.dismiss();

        if((solutionList !=null) && (!solutionList.isEmpty())) {
            Snackbar.make(getView(), "We have a solution! :D", Snackbar.LENGTH_LONG)
                    .show();

            ((GameFragment) actualFragment).setSolutionList(solutionList);
        }
        else
            Snackbar.make(getView(), "No solution", Snackbar.LENGTH_LONG)
                    .show();
    }

    public View getView(){
        View v = getWindow().getDecorView().getRootView();

        if(actualFragment.getView() != null)
            v = actualFragment.getView();

        return v;
    }
}
