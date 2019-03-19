package com.example.a3braingames;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TowersOfHanoiActivity extends AppCompatActivity {
    //Notifcation Channels and IDs
    private static final String NOTIFICATION_CHANNEL_ID = "com.example.a3braingames.TOH_NOTIFICATION_CHANNEL_ID";
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 2;

    //Media Section
    private SoundPool soundPool;
    private float actVolume, maxVolume, volume;
    AudioManager audioManager;
    private boolean loaded = false;
    private int soundIdc;

    //Debug Boolean
    private boolean DEBUG_MODE = true;

    //Shared preferences
    SharedPreferences mSharedPref;
    private String sharedPrefFile;

    //list of previous highscores
    private List<Integer> mScores = new ArrayList<Integer>();

    //int containing current score
    private int mCurrentScore;

    //TextView showing current Score
    private TextView mScoreView;

    //ImageButtons for gameplay
    private ImageButton mLeftButton;
    private ImageButton mMiddleButton;
    private ImageButton mRightButton;

    //Last Selected Button id
    private int mLastButton;

    //Lists for Tower moves
    private List<Integer> mLeftTowerList    = new ArrayList<>(3);
    private List<Integer> mMiddleTowerList  = new ArrayList<>(3);
    private List<Integer> mRightTowerList   = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_towers_of_hanoi);

        //init the shared prefs
        sharedPrefFile = getIntent().getExtras().getString(MainActivity.mFilePath);
        mSharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        //Populate the Highscores list with the Shared Prefs saved Scores
        mScores.add(mSharedPref
                .getInt(MainActivity.TOWERS_OF_HANOI_SCORE_ONE, 99999));
        mScores.add(mSharedPref
                .getInt(MainActivity.TOWERS_OF_HANOI_SCORE_TWO, 99999));
        mScores.add(mSharedPref
                .getInt(MainActivity.TOWERS_OF_HANOI_SCORE_THREE, 99999));
        mScores.add(mSharedPref
                .getInt(MainActivity.TOWERS_OF_HANOI_SCORE_FOUR, 99999));
        mScores.add(mSharedPref
                .getInt(MainActivity.TOWERS_OF_HANOI_SCORE_FIVE, 99999));

        //Init the current score
        mCurrentScore = 0;

        //Init the score view and set its text
        mScoreView = findViewById(R.id.towers_of_hanoi_score_view);
        mScoreView.setText(convertIntsToString(mCurrentScore));

        //audio manager for volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //init soundpool
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        //loading all notes music files
        soundIdc = soundPool.load(this, R.raw.button_a, 1);

        //Init all Tower Lists to default
        setTowersList();

        //Init the ImageViews
        mLeftButton     = findViewById(R.id.lefttohview);
        mMiddleButton   = findViewById(R.id.middletohview);
        mRightButton    = findViewById(R.id.righttohview);

        //SELECT the left tower as the last button
        mLastButton = mLeftButton.getId();
        mLeftButton.setBackgroundResource(R.drawable.toh_selected_button_border);

        //Create OnClick Handlers for Each ImageButton
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasWonGame()){
                    youWinAlertDialog();
                } else {
                    if (mLastButton != v.getId()) {
                        //Play a sound for every click
                        soundPool.play(soundIdc, volume, volume, 0, 0, 1f);
                        //Places a border around the currently pressed ImageButton
                        mLeftButton.setBackgroundResource(R.drawable.toh_selected_button_border);

                        //Removes the border from the previously selected ImageButton
                        if (mLastButton == mMiddleButton.getId()) {
                            if (ifMiddleTowerEmpty()){
                                mMiddleButton.setBackgroundResource(0);
                            } else {
                                mMiddleButton.setBackgroundResource(0);
                                boolean canTransfer = transferTopTowerNumber(mMiddleTowerList, mLeftTowerList);
                                if (canTransfer){
                                    addPointMove();
                                    mMiddleButton.setImageResource(getTowerImage(mMiddleTowerList));
                                    mLeftButton.setImageResource(getTowerImage(mLeftTowerList));
                                    mLastButton = 0;
                                    mLeftButton.setBackgroundResource(0);
                                    return;
                                } else {
                                    //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                                    Drawable errorBGs[] = new Drawable[2];
                                    Resources res = getResources();
                                    errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                                    errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                                    TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                                    mLeftButton.setBackground(crossFader);
                                    crossFader.startTransition(2000);
                                    crossFader.reverseTransition(2000);
                                }
                            }

                        } else if (mLastButton == mRightButton.getId()) {
                            if (ifRightTowerEmpty()) {
                                mRightButton.setBackgroundResource(0);
                            } else {
                                mRightButton.setBackgroundResource(0);
                                boolean canTransfer = transferTopTowerNumber(mRightTowerList, mLeftTowerList);
                                if (canTransfer){
                                    addPointMove();
                                    mRightButton.setImageResource(getTowerImage(mRightTowerList));
                                    mLeftButton.setImageResource(getTowerImage(mLeftTowerList));
                                    mLastButton = 0;
                                    mLeftButton.setBackgroundResource(0);
                                    return;
                                } else {
                                    //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                                    Drawable errorBGs[] = new Drawable[2];
                                    Resources res = getResources();
                                    errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                                    errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                                    TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                                    mLeftButton.setBackground(crossFader);
                                    crossFader.startTransition(2000);
                                    crossFader.reverseTransition(2000);
                                }
                            }
                        }
                        mLastButton = v.getId();
                    } else {
                        //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                        Drawable errorBGs[] = new Drawable[2];
                        Resources res = getResources();
                        errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                        errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                        TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                        mLeftButton.setBackground(crossFader);
                        crossFader.startTransition(2000);
                        crossFader.reverseTransition(2000);
                        mLastButton = 0;
                        mLeftButton.setBackgroundResource(0);
                    }
                }
            }
        });

        mMiddleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasWonGame()){
                    youWinAlertDialog();
                } else {
                    if (mLastButton != v.getId()) {
                        //Play a sound for every click
                        soundPool.play(soundIdc, volume, volume, 0, 0, 1f);
                        //Places a border around the currently pressed ImageButton
                        mMiddleButton.setBackgroundResource(R.drawable.toh_selected_button_border);

                        //Removes the border from the previously selected ImageButton
                        if (mLastButton == mLeftButton.getId()) {
                            if (ifLeftTowerEmpty()) {
                                mLeftButton.setBackgroundResource(0);
                            } else {
                                mLeftButton.setBackgroundResource(0);
                                boolean canTransfer = transferTopTowerNumber(mLeftTowerList, mMiddleTowerList);
                                if (canTransfer){
                                    addPointMove();
                                    mLeftButton.setImageResource(getTowerImage(mLeftTowerList));
                                    mMiddleButton.setImageResource(getTowerImage(mMiddleTowerList));
                                    mLastButton = 0;
                                    mMiddleButton.setBackgroundResource(0);
                                    return;
                                } else {
                                    //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                                    Drawable errorBGs[] = new Drawable[2];
                                    Resources res = getResources();
                                    errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                                    errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                                    TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                                    mMiddleButton.setBackground(crossFader);
                                    crossFader.startTransition(2000);
                                    crossFader.reverseTransition(2000);
                                }
                            }
                        } else if (mLastButton == mRightButton.getId()) {
                            if (ifRightTowerEmpty()) {
                                mRightButton.setBackgroundResource(0);
                            } else {
                                mRightButton.setBackgroundResource(0);
                                boolean canTransfer = transferTopTowerNumber(mRightTowerList, mMiddleTowerList);
                                if (canTransfer){
                                    addPointMove();
                                    mRightButton.setImageResource(getTowerImage(mRightTowerList));
                                    mMiddleButton.setImageResource(getTowerImage(mMiddleTowerList));
                                    mLastButton = 0;
                                    mMiddleButton.setBackgroundResource(0);
                                    return;
                                } else {
                                    //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                                    Drawable errorBGs[] = new Drawable[2];
                                    Resources res = getResources();
                                    errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                                    errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                                    TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                                    mMiddleButton.setBackground(crossFader);
                                    crossFader.startTransition(2000);
                                    crossFader.reverseTransition(2000);
                                }
                            }
                        }
                        mLastButton = v.getId();
                    } else {
                        //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                        Drawable errorBGs[] = new Drawable[2];
                        Resources res = getResources();
                        errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                        errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                        TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                        mMiddleButton.setBackground(crossFader);
                        crossFader.startTransition(2000);
                        crossFader.reverseTransition(2000);
                        mLastButton = 0;
                        mMiddleButton.setBackgroundResource(0);
                    }
                }
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasWonGame()){
                    youWinAlertDialog();
                } else {
                    if (mLastButton != v.getId()) {
                        //Play a sound for every click
                        soundPool.play(soundIdc, volume, volume, 0, 0, 1f);
                        //Places a border around the currently pressed ImageButton
                        mRightButton.setBackgroundResource(R.drawable.toh_selected_button_border);

                        //Removes the border from the previously selected ImageButton
                        if (mLastButton == mLeftButton.getId()) {
                            if (ifLeftTowerEmpty()) {
                                mLeftButton.setBackgroundResource(0);
                            } else {
                                mLeftButton.setBackgroundResource(0);
                                boolean canTransfer = transferTopTowerNumber(mLeftTowerList, mRightTowerList);
                                if (canTransfer){
                                    addPointMove();
                                    mLeftButton.setImageResource(getTowerImage(mLeftTowerList));
                                    mRightButton.setImageResource(getTowerImage(mRightTowerList));
                                    mLastButton = 0;
                                    mRightButton.setBackgroundResource(0);
                                    if (hasWonGame()){
                                        youWinAlertDialog();
                                    }
                                    return;
                                } else {
                                    //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                                    Drawable errorBGs[] = new Drawable[2];
                                    Resources res = getResources();
                                    errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                                    errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                                    TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                                    mRightButton.setBackground(crossFader);
                                    crossFader.startTransition(2000);
                                    crossFader.reverseTransition(2000);
                                }
                            }
                        } else if (mLastButton == mMiddleButton.getId()) {
                            if (ifMiddleTowerEmpty()) {
                                mMiddleButton.setBackgroundResource(0);
                            } else {
                                mMiddleButton.setBackgroundResource(0);
                                boolean canTransfer = transferTopTowerNumber(mMiddleTowerList, mRightTowerList);
                                if (canTransfer){
                                    addPointMove();
                                    mMiddleButton.setImageResource(getTowerImage(mMiddleTowerList));
                                    mRightButton.setImageResource(getTowerImage(mRightTowerList));
                                    mLastButton = 0;
                                    mRightButton.setBackgroundResource(0);
                                    if (hasWonGame()){
                                        youWinAlertDialog();
                                    }
                                    return;
                                } else {
                                    //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                                    Drawable errorBGs[] = new Drawable[2];
                                    Resources res = getResources();
                                    errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                                    errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                                    TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                                    mRightButton.setBackground(crossFader);
                                    crossFader.startTransition(2000);
                                    crossFader.reverseTransition(2000);
                                }
                            }
                        }
                        mLastButton = v.getId();
                    } else {
                        //If already selected ImageButton background radius goes to Red and then fades Back to selected Blue
                        Drawable errorBGs[] = new Drawable[2];
                        Resources res = getResources();
                        errorBGs[0] = res.getDrawable(R.drawable.toh_selected_button_border);
                        errorBGs[1] = res.getDrawable(R.drawable.toh_error_button_border);
                        TransitionDrawable crossFader = new TransitionDrawable(errorBGs);
                        mRightButton.setBackground(crossFader);
                        crossFader.startTransition(2000);
                        crossFader.reverseTransition(2000);
                        mLastButton = 0;
                        mRightButton.setBackgroundResource(0);
                    }
                }
            }
        });

        //Create the Notification Channel
        createNotificationChannel();
    }

    //Return the towers new Image
    public int getTowerImage(List<Integer> towerPic){
        if (towerPic.get(0) == 3 && towerPic.get(1) == 2 && towerPic.get(2) == 1){
            //All Blocks On
            return R.drawable.toh_all_blocks;
        } else if (towerPic.get(0) == 0 && towerPic.get(1) == 0 && towerPic.get(2) == 0){
            //No Blocks On
            return R.drawable.toh_no_blocks;
        } else if (towerPic.get(0) == 2 && towerPic.get(1) == 1 && towerPic.get(2) == 0){
            //Blocks med and small on
            return R.drawable.toh_top_two;
        } else if (towerPic.get(0) == 3 && towerPic.get(1) == 2 && towerPic.get(2) == 0){
            //Blocks Large and Med on
            return R.drawable.toh_bottom_two;
        } else if (towerPic.get(0) == 3 && towerPic.get(1) == 1 && towerPic.get(2) == 0){
            //Blocks Large and Small on
            return R.drawable.toh_no_medium_block;
        } else if (towerPic.get(0) == 3 && towerPic.get(1) == 0 && towerPic.get(2) == 0){
            //Blocks Large Only on
            return R.drawable.toh_bottom_big;
        } else if (towerPic.get(0) == 2 && towerPic.get(1) == 0 && towerPic.get(2) == 0){
            //Blocks Med Only on
            return R.drawable.toh_bottom_med;
        } else if (towerPic.get(0) == 1 && towerPic.get(1) == 0 && towerPic.get(2) == 0){
            //Blcoks Small Only on
            return R.drawable.toh_bottom_small;
        } else {
            //Block Setting Error
            return R.drawable.toh_error_button_border;
        }
    }

    //Reset Game if user Won
    public void youWinAlertDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.alert_dialog_you_won_toh, null))
                .setPositiveButton(R.string.yes_alert_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes Answer, restart game.
                        startOverAndSaveScoreHanoi();
                        resetTowersList();
                        mLeftButton.setImageResource(getTowerImage(mLeftTowerList));
                        mMiddleButton.setImageResource(getTowerImage(mMiddleTowerList));
                        mRightButton.setImageResource(getTowerImage(mRightTowerList));
                        mLastButton = mLeftButton.getId();
                        mLeftButton.setBackgroundResource(R.drawable.toh_selected_button_border);
                    }
                })
                .setNegativeButton(R.string.no_alert_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Save and Reset the Old Score
                        startOverAndSaveScoreHanoi();
                        finish();
                    }
                })
                .show();
    }

    //Check if Tower Can Transfer Number
    public Boolean transferTopTowerNumber(List<Integer> tower_out, List<Integer> tower_in){
        if (tower_out.get(2) != 0){
            //Assumes index being at the top of tower, that no other towers have a Number value greater then 0
            tower_in.set(0, tower_out.get(2));
            tower_out.set(2, 0);
            return true;
        }
        if (tower_out.get(1) != 0){
            //check Tower Numbers to verify no higher number is put on a lower one.
            if (tower_in.get(0) == 0){
                tower_in.set(0, tower_out.get(1));
                tower_out.set(1, 0);
                return true;
            } else if(tower_in.get(0) < tower_out.get(1)){
                return false;
            } else if (tower_in.get(1) == 0){
                tower_in.set(1, tower_out.get(1));
                tower_out.set(1, 0);
                return true;
            } else if (tower_in.get(1) < tower_out.get(1)){
                return false;
            } else if (tower_in.get(2) == 0){
                tower_in.set(2, tower_out.get(1));
                tower_out.set(1, 0);
                return true;
            } else {
                return false;
            }
        }
        if (tower_out.get(0) != 0){
            //check Tower Numbers to verify no higher number is put on a lower one.
            if (tower_in.get(0) == 0){
                tower_in.set(0, tower_out.get(0));
                tower_out.set(0, 0);
                return true;
            } else if(tower_in.get(0) < tower_out.get(0)){
                return false;
            } else if (tower_in.get(1) == 0){
                tower_in.set(1, tower_out.get(0));
                tower_out.set(0, 0);
                return true;
            } else if (tower_in.get(1) < tower_out.get(0)){
                return false;
            } else if (tower_in.get(2) == 0){
                tower_in.set(2, tower_out.get(0));
                tower_out.set(0, 0);
                return true;
            } else  {
                return false;
            }
        }
        return false;
    }

    //Check if Victory Conditions have been met
    public boolean hasWonGame(){
        return (mRightTowerList.get(0) == 3) && (mRightTowerList.get(1) == 2) && (mRightTowerList.get(2) == 1);
    }

    //Check if List Tower is already Full
    public boolean isTowerFull(List<Integer> tower_in){
        return !tower_in.contains(0);
    }

    //Check if Left Tower is Empty
    public boolean ifLeftTowerEmpty(){
        if (mLeftTowerList.contains(1)){
            return false;
        }
        if (mLeftTowerList.contains(2)){
            return false;
        }
        if (mLeftTowerList.contains(3)){
            return false;
        }
        return true;
    }

    //Check if Middle Tower is Empty
    public boolean ifMiddleTowerEmpty(){
        if (mMiddleTowerList.contains(1)){
            return false;
        }
        if (mMiddleTowerList.contains(2)){
            return false;
        }
        if (mMiddleTowerList.contains(3)){
            return false;
        }
        return true;
    }

    //Check if Right Tower is Empty
    public boolean ifRightTowerEmpty(){
        if (mRightTowerList.contains(1)){
            return false;
        }
        if (mRightTowerList.contains(2)){
            return false;
        }
        if (mRightTowerList.contains(3)){
            return false;
        }
        return true;
    }

    //Sets the Towers Lists to their default Starting left [3,2,1] middle [0,0,0] right [0,0,0]
    public void setTowersList(){

        mLeftTowerList.add(3);
        mLeftTowerList.add(2);
        mLeftTowerList.add(1);
        mMiddleTowerList.add(0);
        mMiddleTowerList.add(0);
        mMiddleTowerList.add(0);
        mRightTowerList.add(0);
        mRightTowerList.add(0);
        mRightTowerList.add(0);
    }

    //Resets the Towers Lists to their default Starting left [3,2,1] middle [0,0,0] right [0,0,0]
    public void resetTowersList(){

        mLeftTowerList.set(0,3);
        mLeftTowerList.set(1,2);
        mLeftTowerList.set(2,1);
        mMiddleTowerList.set(0,0);
        mMiddleTowerList.set(1,0);
        mMiddleTowerList.set(2,0);
        mRightTowerList.set(0,0);
        mRightTowerList.set(1,0);
        mRightTowerList.set(2,0);
    }

    private String convertIntsToString(int int_in){
        return ""+int_in;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                //| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    //Add point to Moves View for every successful move
    public void addPointMove(){
        mCurrentScore++;
        mScoreView.setText(convertIntsToString(mCurrentScore));
    }


    //TODO delete this segment after deciding on and implementing new save game method
    public void startOverAndSaveScoreHanoi() {
        //Update the list to have the new high score if it is greater then previous high scores
        checkIfBetterScore();

        //Open the ShredPreferences Editor
        SharedPreferences.Editor prefEditor = mSharedPref.edit();

        //TODO save new list of highscores to the shared preferences file
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_ONE, mScores.get(0));
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_TWO, mScores.get(1));
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_THREE, mScores.get(2));
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_FOUR, mScores.get(3));
        prefEditor
                .putInt(MainActivity.TOWERS_OF_HANOI_SCORE_FIVE, mScores.get(4));

        //Apply the changes
        prefEditor.apply();

        //Reset the current score and score textview
        mCurrentScore = 0;
        mScoreView.setText(convertIntsToString(mCurrentScore));
    }

    //Test the Preferences with the current best moves scores and compares them to the new one.
    public void checkIfBetterScore() {
        int score = this.mCurrentScore;
        boolean betterScore = false;
        for (int i = 0; i<5; i++){
            int oldScore = mScores.get(i);
            if (score < oldScore) {
                mScores.set(i, score);
                score = oldScore;
                betterScore = true;
            }
        }
        if (betterScore){
            //Launch Notification For Scores
            sendScoreNotification();
        }
    }

    //Send Notification if new Better Score
    public void sendScoreNotification(){
        Intent notificationIntent = new Intent(getApplicationContext(), HighScoresActivity.class);
        notificationIntent.putExtra("FILE_PATH", sharedPrefFile);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        PendingIntent notificationContentIntent = stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Towers of Hanoi!")
                .setContentText("New Best Score, Click to See!")
                .setSmallIcon(R.drawable.toh_all_blocks)
                .setContentIntent(notificationContentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        if (notifyBuilder != null) {
            mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());
        }

    }

    //Creates the Notification Manager
    public void createNotificationChannel(){
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Towers of Hanoi Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Towers of Hanoi New Best Score Notification");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}
