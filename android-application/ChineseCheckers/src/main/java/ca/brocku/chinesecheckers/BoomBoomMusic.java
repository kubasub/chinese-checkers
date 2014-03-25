package ca.brocku.chinesecheckers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Main on 3/20/14.
 */
public class BoomBoomMusic {

    static MediaPlayer mp;
    static SoundPool sp;
    private static Boolean started = false;
    private static Boolean loaded = false;
    private static int piecepopsound;
    final static String BACKSOUNDPREF = "BackgroundSoundPref";
    final static String EFFCTSOUNDPREF = "EffectsSoundPrefs";
    private static SharedPreferences sharedPrefs;
    private static Context gContext;

    public static void start(Context c) {
        gContext = c;
        if (started && !mp.isPlaying()) {
            mp.start();
        } else if (!started) {
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
            setupMP(c);
            setupSP(c);
        }
    }

    /*
     * setupMp
     * sets up the background music for the app
     */
    private static void setupMP(Context c) {
        mp = MediaPlayer.create(c, R.raw.eisi);
        mp.setVolume(sharedPrefs.getInt(BACKSOUNDPREF, 100) / 100.0f, sharedPrefs.getInt(BACKSOUNDPREF, 100) / 100.0f);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                changeBackGroundMusic(gContext, R.raw.eisi);
            }
        });
        mp.start();
        started = true;
    }

    /*
     * konamiCodeEntered
     *
     * when Konami code is entered chooses one of three Konami game theme songs
     */
    public static void konamiCodeEntered(Context c) {
        if (mp != null) {
            switch ((int) (Math.random() * (3))) {
                case 0:
                    changeBackGroundMusic(c, R.raw.castlevania);
                    break;
                case 1:
                    changeBackGroundMusic(c, R.raw.gradius);
                    break;
                case 2:
                    changeBackGroundMusic(c, R.raw.mgs);
                    break;
            }
        }
    }

    private static void changeBackGroundMusic(Context c, int toThis) {
        try {
            AssetFileDescriptor afd = c.getResources().openRawResourceFd(toThis);
            mp.stop();
            mp.reset();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            Log.e("ERROR", "changeBackGroundMusic Failed " + e.getMessage());
        }
    }

    /*
     * setupSP
     * sets up the sound pool for the short burt sounds for the app
     */
    private static void setupSP(Context c) {
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        piecepopsound = sp.load(c, R.raw.piecepopsound, 1); //http://soundbible.com/2067-Blop.html
    }

    public static void setBackgroundVolume(int setTo) {
        if (mp != null) {
            mp.setVolume(setTo / 100.0f, setTo / 100.0f);
        }
    }

    public static void pause() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
        }
    }

    public static void onPieceTap() {
        if(loaded){
            sp.play(piecepopsound, sharedPrefs.getInt(EFFCTSOUNDPREF, 100) / 100.0f, sharedPrefs.getInt(EFFCTSOUNDPREF, 100) / 100.0f, 1, 0, 1f);
        }
    }

    public static void stop() {
        mp.stop();
        started = false;
    }
}
