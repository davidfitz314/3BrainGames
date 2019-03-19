package com.example.a3braingames;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PianoPlayerActivity extends AppCompatActivity {
    //Buttons
    private FloatingActionButton mCnote;
    private FloatingActionButton mCsharpNote;
    private FloatingActionButton mDnote;
    private FloatingActionButton mDsharpNote;
    private FloatingActionButton mEnote;
    private FloatingActionButton mFnote;
    private FloatingActionButton mFsharpNote;
    private FloatingActionButton mGnote;
    private FloatingActionButton mGsharpNote;
    private FloatingActionButton mAnote;
    private FloatingActionButton mAsharpNote;
    private FloatingActionButton mBnote;

    //Shared preferences
    SharedPreferences mSharedPref;
    private String sharedPrefFile;
    private int mNoteCounter = 0;

    //Media Section
    private SoundPool soundPool;
    private float actVolume, maxVolume, volume;
    AudioManager audioManager;
    private boolean loaded = false;
    private int soundIdc;
    private int soundIdcSharp;
    private int soundIdd;
    private int soundIddSharp;
    private int soundIde;
    private int soundIdf;
    private int soundIdfSharp;
    private int soundIdg;
    private int soundIdgSharp;
    private int soundIda;
    private int soundIdaSharp;
    private int soundIdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piano_player);

        //shared pref init
        sharedPrefFile = getIntent().getExtras().getString(MainActivity.mFilePath);
        mSharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        //check the shared pref for a prev note count and update our current note counter
        mNoteCounter = mSharedPref.getInt(MainActivity.PIANO_NOTE_COUNTER_NAME, 0);

        //audio manager for volume
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        //Hardware buttons setting to adjust the media sound
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //init soundpool
        soundPool = new SoundPool(12, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });

        //loading all notes music files
        soundIdc = soundPool.load(this, R.raw.c_note, 1);
        soundIdcSharp = soundPool.load(this, R.raw.c_sharp_note, 1);
        soundIdd = soundPool.load(this, R.raw.d_note, 1);
        soundIddSharp = soundPool.load(this, R.raw.d_sharp_note, 1);
        soundIde = soundPool.load(this, R.raw.e_note, 1);
        soundIdf = soundPool.load(this, R.raw.f_note, 1);
        soundIdfSharp = soundPool.load(this, R.raw.f_sharp_note, 1);
        soundIdg = soundPool.load(this, R.raw.g_note, 1);
        soundIdgSharp = soundPool.load(this, R.raw.g_sharp_note, 1);
        soundIda = soundPool.load(this, R.raw.a_note, 1);
        soundIdaSharp = soundPool.load(this, R.raw.a_sharp_note, 1);
        soundIdb = soundPool.load(this, R.raw.b_note, 1);

        mCnote = (FloatingActionButton) findViewById(R.id.c_note_button);
        mCnote.setSoundEffectsEnabled(false);
        mCnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(0);
                mNoteCounter++;
            }
        });

        mCsharpNote = findViewById(R.id.c_sharp_note_button);
        mCsharpNote.setSoundEffectsEnabled(false);
        mCsharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(1);
                mNoteCounter ++;
            }
        });

        mDnote = findViewById(R.id.d_note_button);
        mDnote.setSoundEffectsEnabled(false);
        mDnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(2);
                mNoteCounter ++;
            }
        });

        mDsharpNote = findViewById(R.id.d_sharp_note_button);
        mDsharpNote.setSoundEffectsEnabled(false);
        mDsharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(3);
                mNoteCounter ++;
            }
        });

        mEnote = findViewById(R.id.e_note_button);
        mEnote.setSoundEffectsEnabled(false);
        mEnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(4);
                mNoteCounter ++;
            }
        });

        mFnote = findViewById(R.id.f_note_button);
        mFnote.setSoundEffectsEnabled(false);
        mFnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(5);
                mNoteCounter ++;
            }
        });

        mFsharpNote = findViewById(R.id.f_sharp_note_button);
        mFsharpNote.setSoundEffectsEnabled(false);
        mFsharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(6);
                mNoteCounter ++;
            }
        });

        mGnote = findViewById(R.id.g_note_button);
        mGnote.setSoundEffectsEnabled(false);
        mGnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(7);
                mNoteCounter ++;
            }
        });

        mGsharpNote = findViewById(R.id.g_sharp_note_button);
        mGsharpNote.setSoundEffectsEnabled(false);
        mGsharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(8);
                mNoteCounter ++;
            }
        });

        mAnote = findViewById(R.id.a_note_button);
        mAnote.setSoundEffectsEnabled(false);
        mAnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(9);
                mNoteCounter ++;
            }
        });

        mAsharpNote = findViewById(R.id.a_sharp_note_button);
        mAsharpNote.setSoundEffectsEnabled(false);
        mAsharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(10);
                mNoteCounter ++;
            }
        });

        mBnote = findViewById(R.id.b_note_button);
        mBnote.setSoundEffectsEnabled(false);
        mBnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager(11);
                mNoteCounter ++;
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        soundPool.release();
        soundPool=null;
    }

    protected void soundManager(int id_in){
        switch (id_in){
            case 0:
                if (loaded) {
                    soundPool.play(soundIdc, volume, volume, 0, 0, 1f);
                }
                break;
            case 1:
                if (loaded) {
                    soundPool.play(soundIdcSharp, volume, volume, 0, 0, 1f);
                }
                break;
            case 2:
                if (loaded) {
                    soundPool.play(soundIdd, volume, volume, 0, 0, 1f);
                }
                break;
            case 3:
                if (loaded) {
                    soundPool.play(soundIddSharp, volume, volume, 0, 0, 1f);
                }
                break;
            case 4:
                if (loaded) {
                    soundPool.play(soundIde, volume, volume, 0, 0, 1f);
                }
                break;
            case 5:
                if (loaded) {
                    soundPool.play(soundIdf, volume, volume, 0, 0, 1f);
                }
                break;
            case 6:
                if (loaded) {
                    soundPool.play(soundIdfSharp, volume, volume, 0, 0, 1f);
                }
                break;
            case 7:
                if (loaded) {
                    soundPool.play(soundIdg, volume, volume, 0, 0, 1f);
                }
                break;
            case 8:
                if (loaded) {
                    soundPool.play(soundIdgSharp, volume, volume, 0, 0, 1f);
                }
                break;
            case 9:
                if (loaded) {
                    soundPool.play(soundIda, volume, volume, 0, 0, 1f);
                }
                break;
            case 10:
                if (loaded) {
                    soundPool.play(soundIdaSharp, volume, volume, 0, 0, 1f);
                }
                break;
            case 11:
                if (loaded) {
                    soundPool.play(soundIdb, volume, volume, 0, 0, 1f);
                }
                break;

        }
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

    @Override
    protected void onPause() {
        super.onPause();
        //Open SharedPreferences editor
        SharedPreferences.Editor prefEditor = mSharedPref.edit();
        //Save the note counter int into the shared pref file
        prefEditor.putInt(MainActivity.PIANO_NOTE_COUNTER_NAME, mNoteCounter);
        prefEditor.apply();

    }
}
