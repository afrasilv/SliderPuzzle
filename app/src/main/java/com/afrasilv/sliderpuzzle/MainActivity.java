package com.afrasilv.sliderpuzzle;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afrasilv.dao.Piece;
import com.afrasilv.fragments.GameFragment;
import com.afrasilv.fragments.InitGameFragment;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Stack<Fragment> fragmentStack;
    private FragmentManager fragmentManager;

    private Fragment actualFragment;

    private ArrayList<Piece> pieceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentStack = new Stack<>();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        return pref.getString(name, "");
    }

    public void changeFragment(int id) {
        switch(id){
            case 0:
              /*  Bundle args = new Bundle();
                args.putParcelableArrayList("listPieces", this.pieceList);

*/
                actualFragment = GameFragment.newInstance();

                hideFab();

                //actualFragment.setArguments(args);

                break;
            case 1:
                actualFragment = InitGameFragment.newInstance();

                break;
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, actualFragment, actualFragment.getTag()).commit();

        fragmentStack.push(actualFragment);
    }

    public void hideFab() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fab.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            fab.animate().translationY(fab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    public void showFab() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
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

            // fragmentManager.popBackStack();

            Fragment fragment = fragmentStack.lastElement();

            if(fragment instanceof InitGameFragment)
                showFab();

            ft.replace(R.id.content_frame, fragment, fragment.getTag()).commit();

        } else {
            finish();
        }
    }
}
