package com.afrasilv.sliderpuzzle;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afrasilv.dao.Piece;
import com.afrasilv.fragments.GameFragment;
import com.afrasilv.fragments.InitGameFragment;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private FragmentManager fragmentManager;

    private Fragment actualFragment;

    private ArrayList<Piece> pieceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.app_name)
                        .content("Under construction... Maybe one day.")
                        .positiveText("Ok")
                        .show();
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
    }

    public void changeFragment(int id) {
        switch(id){
            case 0:
              /*  Bundle args = new Bundle();
                args.putParcelableArrayList("listPieces", this.pieceList);

*/
                actualFragment = GameFragment.newInstance();
                //actualFragment.setArguments(args);

                break;
            case 1:
                actualFragment = InitGameFragment.newInstance();

                break;
        }

        fragmentManager.beginTransaction().replace(R.id.content_frame, actualFragment, actualFragment.getTag()).commit();
    }

    public void setPieceList(ArrayList<Piece> pieceList){
        this.pieceList = pieceList;
    }

    public ArrayList<Piece> getPieceList(){
        return this.pieceList;
    }
}
