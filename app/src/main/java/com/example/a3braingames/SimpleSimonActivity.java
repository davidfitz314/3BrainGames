package com.example.a3braingames;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimpleSimonActivity extends AppCompatActivity {
    //Notification
    private static final String NOTIFICATION_CHANNEL_ID = "com.example.a3braingames.SS_NOTIFICATION_CHANNEL_ID";
    private NotificationManager mNotifyManager;
    private static final int NOTIFICATION_ID = 1;

    //Media Section
    private SoundPool soundPool;
    private float actVolume, maxVolume, volume;
    AudioManager audioManager;
    private boolean loaded = false;
    private int soundIdc;
    private int soundIdd;
    private int soundIde;
    private int soundIdf;

    //debug mode bool
    private boolean DEBUG_MODE_ON = false;

    //TODO save local game data in case of screen rotation
    //TODO show computer turn Button Clicks
    //Transition Drawable for changing button color on computer turn
    private int mTransitionLength = 500;
    private int mTransitionOffSet = 800;

    //Shared preferences
    SharedPreferences mSharedPref;
    private String sharedPrefFile;

    //list of previous highscores
    private List<Integer> mScores = new ArrayList<Integer>();

    //Int containing current score
    private int mCurrentScore;

    //Int for playerIndex
    private int mPlayerIndex;

    //Int for level
    private int mLevel;

    //Bool for player turn
    private boolean mPlayerTurn = false;

    //TextView showing current Score
    private TextView mScoreView;

    //List of current game button pattern
    private List<Integer> mPatternList = new ArrayList<Integer>();

    //Colored Buttons
    private Button mYellowButton;
    private Button mBlueButton;
    private Button mRedButton;
    private Button mGreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_simon);

        //Init the shared prefs
        sharedPrefFile = getIntent().getExtras().getString(MainActivity.mFilePath);
        mSharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        //Populate the Highscores list with the Shared Prefs saved Scores
        mScores.add(mSharedPref
                .getInt(MainActivity.SIMPLE_SIMON_SCORE_ONE, 0));
        mScores.add(mSharedPref
                .getInt(MainActivity.SIMPLE_SIMON_SCORE_TWO, 0));
        mScores.add(mSharedPref
                .getInt(MainActivity.SIMPLE_SIMON_SCORE_THREE, 0));
        mScores.add(mSharedPref
                .getInt(MainActivity.SIMPLE_SIMON_SCORE_FOUR, 0));
        mScores.add(mSharedPref
                .getInt(MainActivity.SIMPLE_SIMON_SCORE_FIVE, 0));

        //Init the current score and playerIndex and level
        mCurrentScore = 0;
        mPlayerIndex = 0;
        mLevel = 0;

        //Init the score view and set its text
        mScoreView = findViewById(R.id.simple_simon_score_view);
        mScoreView.setText(convertIntsToString(mCurrentScore));

        //Init Colored Buttons
        mYellowButton = findViewById(R.id.ss_yellow_button);
        mBlueButton = findViewById(R.id.ss_blue_button);
        mRedButton = findViewById(R.id.ss_red_button);
        mGreenButton = findViewById(R.id.ss_green_button);

        //audio manager for volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //init soundpool
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        //loading all notes music files
        soundIdc = soundPool.load(this, R.raw.c_note, 1);
        soundIdd = soundPool.load(this, R.raw.d_note, 1);
        soundIde = soundPool.load(this, R.raw.e_note, 1);
        soundIdf = soundPool.load(this, R.raw.f_note, 1);

        //Set onclick listeners for each Colored Button
        mYellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkIfCorrectPress = checkPressedButton(mPlayerIndex, v.getId());
                if (checkIfCorrectPress) {
                    UpdateScore();
                    mPlayerIndex++;
                    soundManager(0);
                    if (mPlayerIndex == mLevel && mPlayerIndex != mPatternList.size() - 1) {
                        mPlayerTurn = false;
                        beginComputerTurn();
                    }
                } else {
                    youLostAlertDialog();
                }
                if (mPlayerIndex == mPatternList.size() - 1) {
                    Toast.makeText(getApplicationContext(), "You Win!!!", Toast.LENGTH_SHORT).show();
                    youWinAlertDialog();
                }
            }
        });

        mBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkIfCorrectPress = checkPressedButton(mPlayerIndex, v.getId());
                if (checkIfCorrectPress) {
                    UpdateScore();
                    mPlayerIndex++;
                    soundManager(1);
                    if (mPlayerIndex == mLevel && mPlayerIndex != mPatternList.size() - 1) {
                        mPlayerTurn = false;
                        beginComputerTurn();
                    }
                } else {
                    youLostAlertDialog();
                }
                if (mPlayerIndex == mPatternList.size() - 1) {
                    Toast.makeText(getApplicationContext(), "You Win!!!", Toast.LENGTH_SHORT).show();
                    youWinAlertDialog();
                }
            }
        });

        mRedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkIfCorrectPress = checkPressedButton(mPlayerIndex, v.getId());
                if (checkIfCorrectPress) {
                    UpdateScore();
                    mPlayerIndex++;
                    soundManager(2);
                    if (mPlayerIndex == mLevel && mPlayerIndex != mPatternList.size() - 1) {
                        mPlayerTurn = false;
                        beginComputerTurn();
                    }
                } else {
                    youLostAlertDialog();
                }
                if (mPlayerIndex == mPatternList.size() - 1) {
                    Toast.makeText(getApplicationContext(), "You Win!!!", Toast.LENGTH_SHORT).show();
                    youWinAlertDialog();
                }
            }
        });

        mGreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkIfCorrectPress = checkPressedButton(mPlayerIndex, v.getId());
                if (checkIfCorrectPress) {
                    UpdateScore();
                    mPlayerIndex++;
                    soundManager(3);
                    if (mPlayerIndex == mLevel && mPlayerIndex != mPatternList.size() - 1) {
                        mPlayerTurn = false;
                        beginComputerTurn();
                    }
                } else {
                    youLostAlertDialog();
                }
                if (mPlayerIndex == mPatternList.size() - 1) {
                    Toast.makeText(getApplicationContext(), "You Win!!!", Toast.LENGTH_SHORT).show();
                    youWinAlertDialog();
                }
            }
        });

        generateSimonList();
        beginComputerTurn();
        createNotificationChannel();
    }

    //Sound Manager
    protected void soundManager(int id_in){
        switch (id_in){
            case 0:
                if (loaded) {
                    soundPool.play(soundIdc, volume, volume, 0, 0, 1f);
                }
                break;
            case 1:
                if (loaded) {
                    soundPool.play(soundIdd, volume, volume, 0, 0, 1f);
                }
                break;
            case 2:
                if (loaded) {
                    soundPool.play(soundIde, volume, volume, 0, 0, 1f);
                }
                break;
            case 3:
                if (loaded) {
                    soundPool.play(soundIdf, volume, volume, 0, 0, 1f);
                }
                break;
        }
    }

    public void youWinAlertDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.alert_dialog_you_won_custom_view, null))
                .setPositiveButton(R.string.yes_alert_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes Answer, restart game.
                        resetGame();
                    }
                })
                .setNegativeButton(R.string.no_alert_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the Activity
                        finish();
                    }
                })
                .show();
    }

    public void youLostAlertDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.alert_dialog_you_lost_custom_view, null))
                .setPositiveButton(R.string.yes_alert_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Yes Answer, restart game.
                        resetGame();
                    }
                })
                .setNegativeButton(R.string.no_alert_answer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the activity
                        finish();
                    }
                })
                .show();
    }

    private void resetGame() {
        saveIfHighScore();
        mPlayerIndex = 0;
        mCurrentScore = 0;
        mScoreView.setText(convertIntsToString(mCurrentScore));
        mPlayerTurn = false;
        mLevel = 0;
        generateSimonList();
        beginComputerTurn();
    }

    private String convertIntsToString(int int_in) {
        return "" + int_in;
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

    public void UpdateScore() {
        mCurrentScore++;
        mScoreView.setText(convertIntsToString(mCurrentScore));
    }

    public boolean checkPressedButton(int index_in, int button_id_in) {
        switch (button_id_in) {
            case R.id.ss_yellow_button:
                if (mPatternList.get(index_in) == button_id_in) {
                    return true;
                }
                break;
            case R.id.ss_blue_button:
                if (mPatternList.get(index_in) == button_id_in) {
                    return true;
                }
                break;
            case R.id.ss_red_button:
                if (mPatternList.get(index_in) == button_id_in) {
                    return true;
                }
                break;
            case R.id.ss_green_button:
                if (mPatternList.get(index_in) == button_id_in) {
                    return true;
                }
                break;
        }
        return false;
    }

    public void generateSimonList() {
        this.mPatternList = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            final int random = new Random().nextInt(4);
            switch (random) {
                case 0:
                    mPatternList.add(R.id.ss_yellow_button);
                    break;
                case 1:
                    mPatternList.add(R.id.ss_blue_button);
                    break;
                case 2:
                    mPatternList.add(R.id.ss_red_button);
                    break;
                case 3:
                    mPatternList.add(R.id.ss_green_button);
                    break;
            }
        }
    }

    public void beginComputerTurn() {
        //TODO loop through the list simulating button presses
        if (DEBUG_MODE_ON) {
            Log.d("LEVELCOLORS:", "" + mLevel);
        }

        AnimationSet setYellow = new AnimationSet(false);
        AnimationSet setBlue = new AnimationSet(false);
        AnimationSet setRed = new AnimationSet(false);
        AnimationSet setGreen = new AnimationSet(false);

        for (int i = 0; i <= mLevel; i++) {
            //simulate button press
            int color_id = mPatternList.get(i);
            if (color_id == R.id.ss_yellow_button) {

                int length = i+1;
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_animation);
                animation.setFillAfter(true);
                animation.reset();
                animation.setDuration(mTransitionLength);
                animation.setStartOffset(length*mTransitionOffSet);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //soundManager(0);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        soundManager(0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                setYellow.addAnimation(animation);

                if (DEBUG_MODE_ON) {
                    Log.d("LEVELCOLORS:", "YELLOW");
                    mScoreView.setBackgroundResource(R.drawable.ss_yellow_square);
                }
            } else if (color_id == R.id.ss_blue_button) {

                int length = i+1;
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_animation);
                animation.setFillAfter(true);
                animation.reset();
                animation.setDuration(mTransitionLength);
                animation.setStartOffset(length*mTransitionOffSet);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //soundManager(1);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        soundManager(1);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                setBlue.addAnimation(animation);

                if (DEBUG_MODE_ON) {
                    Log.d("LEVELCOLORS:", "BLUE");
                    mScoreView.setBackgroundResource(R.drawable.ss_blue_square);
                }
            } else if (color_id == R.id.ss_red_button) {
                int length = i+1;
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_animation);
                animation.setFillAfter(true);
                animation.reset();
                animation.setDuration(mTransitionLength);
                animation.setStartOffset(length*mTransitionOffSet);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //soundManager(2);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        soundManager(2);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                setRed.addAnimation(animation);

                if (DEBUG_MODE_ON) {
                    Log.d("LEVELCOLORS:", "RED");
                    mScoreView.setBackgroundResource(R.drawable.ss_red_square);
                }
            } else if (color_id == R.id.ss_green_button) {
                int length = i+1;
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_animation);
                animation.setFillAfter(true);
                animation.reset();
                animation.setDuration(mTransitionLength);
                animation.setStartOffset(length*mTransitionOffSet);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        //soundManager(3);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        soundManager(3);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                setGreen.addAnimation(animation);

                if (DEBUG_MODE_ON) {
                    Log.d("LEVELCOLORS:", "GREEN");
                    mScoreView.setBackgroundResource(R.drawable.ss_green_square);
                }
            } else {
                Log.d("LEVELCOLORS:", "ERROR UNKNOWN BUTTON TYPE!");
            }
        }

        mYellowButton.startAnimation(setYellow);
        mBlueButton.startAnimation(setBlue);
        mRedButton.startAnimation(setRed);
        mGreenButton.startAnimation(setGreen);

        //TODO increment the level?
        if (DEBUG_MODE_ON) {
            Log.d("LEVELCOLORS:", "PLAYERTURN");
        }
        mLevel++;
        mPlayerIndex = 0;
        mPlayerTurn = true;
    }

    public void saveIfHighScore() {
        //Update the list to have the new high score if it is greater then previous high scores
        checkIfHighScore();

        //Open the ShredPreferences Editor
        SharedPreferences.Editor prefEditor = mSharedPref.edit();

        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_ONE, mScores.get(0));
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_TWO, mScores.get(1));
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_THREE, mScores.get(2));
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_FOUR, mScores.get(3));
        prefEditor
                .putInt(MainActivity.SIMPLE_SIMON_SCORE_FIVE, mScores.get(4));

        //Apply the changes
        prefEditor.apply();
    }

    public void checkIfHighScore() {
        int score = this.mCurrentScore;
        boolean newHighScore = false;
        for (int i = 0; i < 5; i++) {
            int oldScore = mScores.get(i);
            if (score > oldScore) {
                mScores.set(i, score);
                score = oldScore;
                newHighScore = true;
            }
        }
        if (newHighScore) {
            sendScoreNotification();
        }
    }

    @Override
    protected void onStop() {
        resetGame();
        super.onStop();
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
                .setContentTitle("Simple Simon!")
                .setContentText("New High Score, Click to See!")
                .setSmallIcon(R.drawable.simple_simon_colors_logo)
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
            notificationChannel.setDescription("Simple Simon, New Best Score Notification");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}
