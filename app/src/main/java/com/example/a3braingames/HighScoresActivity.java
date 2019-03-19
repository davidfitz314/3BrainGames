package com.example.a3braingames;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import android.content.BroadcastReceiver;

public class HighScoresActivity extends AppCompatActivity {
    //Shared preferences
    SharedPreferences mSharedPref;
    private String sharedPrefFile;

    //Simple Simon TextViews
    private TextView mSimonScoreOne;
    private TextView mSimonScoreTwo;
    private TextView mSimonScoreThree;
    private TextView mSimonScoreFour;
    private TextView mSimonScoreFive;
    //Simple Simon Score Ints
    private int mSSScoreOne;
    private int mSSScoreTwo;
    private int mSSScoreThree;
    private int mSSScoreFour;
    private int mSSScoreFive;

    //Towers of Hanoi TextViews
    private TextView mToHanoiScoreOne;
    private TextView mToHanoiScoreTwo;
    private TextView mToHanoiScoreThree;
    private TextView mToHanoiScoreFour;
    private TextView mToHanoiScoreFive;
    //Towers of Hanoi Score Ints
    private int mToHScoreOne;
    private int mToHScoreTwo;
    private int mToHScoreThree;
    private int mToHScoreFour;
    private int mToHScoreFive;

    //Piano TextView
    private TextView mPianoNotesCountedView;
    //Piano counter
    private int mNoteCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        //Init the shared prefs
        sharedPrefFile = getIntent().getExtras().getString(MainActivity.mFilePath);
        mSharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        //Init the views
        mSimonScoreOne          = findViewById(R.id.ss_hs_one_view);
        mSimonScoreTwo          = findViewById(R.id.ss_hs_two_view);
        mSimonScoreThree        = findViewById(R.id.ss_hs_three_view);
        mSimonScoreFour         = findViewById(R.id.ss_hs_four_view);
        mSimonScoreFive         = findViewById(R.id.ss_hs_five_view);
        mToHanoiScoreOne        = findViewById(R.id.toh_hs_one_view);
        mToHanoiScoreTwo        = findViewById(R.id.toh_hs_two_view);
        mToHanoiScoreThree      = findViewById(R.id.toh_hs_three_view);
        mToHanoiScoreFour       = findViewById(R.id.toh_hs_four_view);
        mToHanoiScoreFive       = findViewById(R.id.toh_hs_five_view);
        mPianoNotesCountedView  = findViewById(R.id.piano_player_notes_played_view);

        // Set Simple Simon views data, check the shared pref file for data and update
        mSSScoreOne     = mSharedPref.getInt(MainActivity.SIMPLE_SIMON_SCORE_ONE, 0);
        mSSScoreTwo     = mSharedPref.getInt(MainActivity.SIMPLE_SIMON_SCORE_TWO, 0);
        mSSScoreThree   = mSharedPref.getInt(MainActivity.SIMPLE_SIMON_SCORE_THREE, 0);
        mSSScoreFour    = mSharedPref.getInt(MainActivity.SIMPLE_SIMON_SCORE_FOUR, 0);
        mSSScoreFive    = mSharedPref.getInt(MainActivity.SIMPLE_SIMON_SCORE_FIVE, 0);
        mSimonScoreOne
                .setText(convertIntstoString(mSSScoreOne));
        mSimonScoreTwo
                .setText(convertIntstoString(mSSScoreTwo));
        mSimonScoreThree
                .setText(convertIntstoString(mSSScoreThree));
        mSimonScoreFour
                .setText(convertIntstoString(mSSScoreFour));
        mSimonScoreFive
                .setText(convertIntstoString(mSSScoreFive));

        // Set Towers of Hanoi views data, check the shared pref file for data and update
        mToHScoreOne    = mSharedPref.getInt(MainActivity.TOWERS_OF_HANOI_SCORE_ONE, 99999);
        mToHScoreTwo    = mSharedPref.getInt(MainActivity.TOWERS_OF_HANOI_SCORE_TWO, 99999);
        mToHScoreThree  = mSharedPref.getInt(MainActivity.TOWERS_OF_HANOI_SCORE_THREE, 99999);
        mToHScoreFour   = mSharedPref.getInt(MainActivity.TOWERS_OF_HANOI_SCORE_FOUR, 99999);
        mToHScoreFive   = mSharedPref.getInt(MainActivity.TOWERS_OF_HANOI_SCORE_FIVE, 99999);
        mToHanoiScoreOne
                .setText(convertIntstoString(mToHScoreOne));
        mToHanoiScoreTwo
                .setText(convertIntstoString(mToHScoreTwo));
        mToHanoiScoreThree
                .setText(convertIntstoString(mToHScoreThree));
        mToHanoiScoreFour
                .setText(convertIntstoString(mToHScoreFour));
        mToHanoiScoreFive
                .setText(convertIntstoString(mToHScoreFive));

        // Set the piano view data check the shared pref for a prev note count and update our current note counter
        mNoteCounter    = mSharedPref.getInt(MainActivity.PIANO_NOTE_COUNTER_NAME, 0);
        mPianoNotesCountedView.setText(convertIntstoString(mNoteCounter));

    }

    private String convertIntstoString(int int_in){
        String temp = "" + int_in;
        return temp;
    }

    public void resetPianoHighScore(View view) {
        //Open a shared pref editor
        SharedPreferences.Editor prefEditor = mSharedPref.edit();

        //Reset the counters both local and sharedpref
        mNoteCounter = 0;
        prefEditor.putInt(MainActivity.PIANO_NOTE_COUNTER_NAME, mNoteCounter);

        //Apply the changes
        prefEditor.apply();

        //Reset the textviews to reflect the changes
        mPianoNotesCountedView.setText(""+mNoteCounter);
    }

    public void resetSimpleSimonScores(View view) {
        //Open a shared pref editor
        SharedPreferences.Editor prefEditor = mSharedPref.edit();
        //Reset the counters both local and sharedpref
        mSSScoreOne     = 0;
        mSSScoreTwo     = 0;
        mSSScoreThree   = 0;
        mSSScoreFour    = 0;
        mSSScoreFive    = 0;
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_ONE, mSSScoreOne);
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_TWO, mSSScoreTwo);
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_THREE, mSSScoreThree);
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_FOUR, mSSScoreFour);
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_FIVE, mSSScoreFive);

        //Apply the changes
        prefEditor.apply();

        //Reset the textviews to reflect the changes
        mSimonScoreOne
                .setText(convertIntstoString(mSSScoreOne));
        mSimonScoreTwo
                .setText(convertIntstoString(mSSScoreTwo));
        mSimonScoreThree
                .setText(convertIntstoString(mSSScoreThree));
        mSimonScoreFour
                .setText(convertIntstoString(mSSScoreFour));
        mSimonScoreFive
                .setText(convertIntstoString(mSSScoreFive));
    }

    public void resetTowersOfHanoiScores(View view) {
        //Open a shared pref editor
        SharedPreferences.Editor prefEditor = mSharedPref.edit();

        //Reset the counters both local and sharedpref
        mToHScoreOne    = 99999;
        mToHScoreTwo    = 99999;
        mToHScoreThree  = 99999;
        mToHScoreFour   = 99999;
        mToHScoreFive   = 99999;
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_ONE, mToHScoreOne);
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_TWO, mToHScoreTwo);
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_THREE, mToHScoreThree);
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_FOUR, mToHScoreFour);
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_FIVE, mToHScoreFive);

        //Apply the changes
        prefEditor.apply();

        //Reset the textviews to reflect the changes
        mToHanoiScoreOne
                .setText(convertIntstoString(mToHScoreOne));
        mToHanoiScoreTwo
                .setText(convertIntstoString(mToHScoreTwo));
        mToHanoiScoreThree
                .setText(convertIntstoString(mToHScoreThree));
        mToHanoiScoreFour
                .setText(convertIntstoString(mToHScoreFour));
        mToHanoiScoreFive
                .setText(convertIntstoString(mToHScoreFive));
    }
}
