package ca.brocku.chinesecheckers;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.brocku.chinesecheckers.gameboard.Piece;
import ca.brocku.chinesecheckers.gameboard.Position;
import ca.brocku.chinesecheckers.gamestate.Player;
import ca.brocku.chinesecheckers.uiengine.BoardUiEngine;

public class OfflineGameActivity extends Activity {
    private String[] players; //an array of the players' names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_game);

        // Make sure variables are setup before creating fragment
        players = getIntent().getExtras().getStringArray("PLAYER_NAMES");

        //passes player array to the offline game fragment
        Fragment offlineGameFragment = new OfflineGameFragment();
        Bundle offlineGameFragmentBundle = new Bundle();
        offlineGameFragmentBundle.putStringArray("PLAYER_NAMES", players);
        offlineGameFragment.setArguments(offlineGameFragmentBundle);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, offlineGameFragment)
                    .commit();
        }
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
            startActivity(new Intent(OfflineGameActivity.this, HelpActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class OfflineGameFragment extends Fragment {
        private BoardUiEngine boardUiEngine;
        private TextView currentPlayerName;
        private Button resetMove;
        private Button doneMove;
        private String[] playerNames;

        public OfflineGameFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_offline_game, container, false);

            playerNames = getArguments().getStringArray("PLAYER_NAMES");

            // Setup Game Board
            BoardUiEngine gameBoardUi = (BoardUiEngine)
                    rootView.findViewById(R.id.gameBoardSurface);

            this.boardUiEngine = gameBoardUi;

            gameBoardUi.setPlayerCount(playerNames.length);


            gameBoardUi.setBoardEventsHandler(new BoardEventsHandler());


            //Bind Controls
            currentPlayerName = (TextView)rootView.findViewById(R.id.offlineCurrentPlayerTextView);
            resetMove = (Button)rootView.findViewById(R.id.offlineMoveResetButton);
            doneMove = (Button)rootView.findViewById(R.id.offlineMoveDoneButton);

            //Bind Handlers
            resetMove.setOnClickListener(new ResetMoveHandler());
            doneMove.setOnClickListener(new DoneMoveHandler());

            currentPlayerName.setText(playerNames[0]);

            if(false) { //TODO change to: if there is a saved game using passed info from main or something local
                new OfflineGameResumeDialog().show(getFragmentManager(), "resumeDialog");
            }

            return rootView;
        }

        public Boolean onEndGame() {
            new OfflineGameEndDialog().show(getFragmentManager(), "endDialog");

            return true;
        }


        private class ResetMoveHandler implements View.OnClickListener {
            @Override
            public void onClick(View view) {

            }
        }

        private class DoneMoveHandler implements View.OnClickListener {

            @Override
            public void onClick(View view) {

            }
        }

        private class BoardEventsHandler implements BoardUiEngine.BoardUiEventsHandler {
            // TODO: This class needs to link with the grid
            Position lastPosition = null;

            @Override
            public void positionTouched(final Position position) {
                Log.d("TOUCHED", "(" + position.getRow() + ", " + position.getIndex() + ")");

                if(lastPosition != null) {
                    OfflineGameFragment.this.boardUiEngine.movePiece(new Piece() {
                        @Override
                        public Position getPosition() {
                            return lastPosition;
                        }

                        @Override
                        public int getPlayerNumber() {
                            return 0;
                        }
                    }, position, null);

                    lastPosition = null;
                } else {
                    lastPosition = position;
                }

            }
        }
    }
}
