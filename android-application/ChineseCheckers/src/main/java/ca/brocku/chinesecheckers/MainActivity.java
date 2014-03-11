package ca.brocku.chinesecheckers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.UUID;

import ca.brocku.chinesecheckers.gamestate.GameStateManager;

/** This is the activity for the home screen of Chinese Checkers.
 *
 * It controls navigation to the different parts of the application including the different game
 * modes and user settings.
 *
 */
@SuppressLint("all")
public class MainActivity extends Activity {
    private Button offlineActivityButton;
    private Button onlineActivityButton;
    private Button helpActivityButton;
    private Button settingsActivityButton;

    public static final String PREF_DONE_INITIAL_SETUP = "DONE_INITIAL_SETUP";
    public static final String PREF_SHOW_MOVES = "SHOW_MOVES";
    public static final String PREF_USER_ID = "USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInitialPreferences(); //only sets the prefs on first launch

        //Bind Controls
        offlineActivityButton = (Button)findViewById(R.id.offlineConfigurationActivityButton);
        onlineActivityButton = (Button)findViewById(R.id.onlineListActivityButton);
        helpActivityButton = (Button)findViewById(R.id.helpActivityButton);
        settingsActivityButton = (Button)findViewById(R.id.settingsActivityButton);

        //Bind Handlers
        offlineActivityButton.setOnClickListener(new OfflineActivityButtonHandler());
        onlineActivityButton.setOnClickListener(new OnlineActivityButtonHandler());
        helpActivityButton.setOnClickListener(new HelpActivityButtonHandler());
        settingsActivityButton.setOnClickListener(new SettingsActivityButtonHandler());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_help) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
        } else if(id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /** Sets initial preferences if it is the first time the application is launched.
     *
     * The preferences set are:
     *      Show possible moves to true
     *      user's ID to a generated UUID
     *
     */
    private void setInitialPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Gets boolean to determine if this is the first time app is launched
        Boolean isInitialSetupDone = sharedPref.getBoolean(PREF_DONE_INITIAL_SETUP, false);

        //Sets the default preferences. This is only ran the first time application is launched.
        if(!isInitialSetupDone) {
            SharedPreferences.Editor editor = sharedPref.edit(); //editor for the prefs

            editor
                .putBoolean(PREF_DONE_INITIAL_SETUP, true)
                .putBoolean(PREF_SHOW_MOVES, true)
                .putString(PREF_USER_ID, UUID.randomUUID().toString())
                .commit();
        }
    }

    /** Handles clicking on the "Offline" game button.
     *
     */
    private class OfflineActivityButtonHandler implements Button.OnClickListener {

        /** OnClick event which starts the OfflineConfigurationActivity activity.
         *
         * @param view the Offline button
         */
        @Override
        public void onClick(View view) {
            File savedOfflineGame = getFileStreamPath(GameStateManager.SERIALIZED_FILENAME); //get the serialized file

            if(savedOfflineGame.exists()) { //if there is a saved game file
                try {
                    //Load the GameStateManager from storage
                    FileInputStream fis = openFileInput(GameStateManager.SERIALIZED_FILENAME);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    GameStateManager gameStateManager = (GameStateManager)ois.readObject();
                    ois.close();
                    fis.close();

                    //Bundle information and start the OfflineGameActivity
                    Intent intent = new Intent(MainActivity.this, OfflineGameActivity.class);
                    intent.putExtra("GAME_STATE_MANAGER", (Parcelable) gameStateManager); //Store GameStateManager
                    intent.putExtra("SAVED_GAME", true); //Store flag that this is a saved game
                    startActivity(intent);

                } catch (Exception e) {
                    savedOfflineGame.delete(); //delete the saved game as it couldn't be loaded
                    e.printStackTrace();
                }
            } else { //there is no saved game, go to configuration for the offline game
                startActivity(new Intent(MainActivity.this, OfflineConfigurationActivity.class));
            }
        }
    }

    private class OnlineActivityButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, OnlineListActivity.class));
        }
    }

    private class HelpActivityButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
        }
    }

    private class SettingsActivityButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
    }
}
