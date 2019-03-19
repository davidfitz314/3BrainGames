package com.example.a3braingames;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //Strings for pref data names and filepath
    private static final String sharedPrefFile              = "com.example.a3braingames.sharedPrefs";
    public static final String SIMPLE_SIMON_SCORE_ONE       = "com.example.a3braingames.ss_score_one";
    public static final String SIMPLE_SIMON_SCORE_TWO       = "com.example.a3braingames.ss_score_two";
    public static final String SIMPLE_SIMON_SCORE_THREE     = "com.example.a3braingames.ss_score_three";
    public static final String SIMPLE_SIMON_SCORE_FOUR      = "com.example.a3braingames.ss_score_four";
    public static final String SIMPLE_SIMON_SCORE_FIVE      = "com.example.a3braingames.ss_score_five";
    public static final String TOWERS_OF_HANOI_SCORE_ONE    = "com.example.a3braingames.toh_score_one";
    public static final String TOWERS_OF_HANOI_SCORE_TWO    = "com.example.a3braingames.toh_score_two";
    public static final String TOWERS_OF_HANOI_SCORE_THREE  = "com.example.a3braingames.toh_score_three";
    public static final String TOWERS_OF_HANOI_SCORE_FOUR   = "com.example.a3braingames.toh_score_four";
    public static final String TOWERS_OF_HANOI_SCORE_FIVE   = "com.example.a3braingames.toh_score_five";
    public static final String PIANO_NOTE_COUNTER_NAME      = "com.example.a3braingames.piano_note_counter_name";
    public static final String mFilePath                    = "com.example.a3braingames.FILE_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startSimpleSimonGame(View view) {
        Intent simonIntent = new Intent(MainActivity.this, SimpleSimonActivity.class);
        simonIntent.putExtra("FILE_PATH", sharedPrefFile);
        startActivity(simonIntent);
    }

    public void startTowersOfHanoiGame(View view) {
        Intent towersIntent = new Intent(MainActivity.this, TowersOfHanoiActivity.class);
        towersIntent.putExtra("FILE_PATH", sharedPrefFile);
        startActivity(towersIntent);
    }

    public void startPianoPlayerGame(View view) {
        Intent pianoIntent = new Intent(MainActivity.this, PianoPlayerActivity.class);
        pianoIntent.putExtra("FILE_PATH", sharedPrefFile);
        startActivity(pianoIntent);
    }

    public void goToHighScoresActivity(View view) {
        Intent highscoresIntent = new Intent(MainActivity.this, HighScoresActivity.class);
        highscoresIntent.putExtra("FILE_PATH", sharedPrefFile);
        startActivity(highscoresIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_3_brain_games, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id_in = item.getItemId();
        if (id_in == R.id.settings_3_brain_games){
            Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
