package ca.brocku.chinesecheckers.tests;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.brocku.chinesecheckers.HelpActivity;
import ca.brocku.chinesecheckers.OfflineGameActivity;
import ca.brocku.chinesecheckers.R;

/**
 * Created by Main on 2/18/14.
 */
public class OfflineGameActivityUnitTest extends ActivityInstrumentationTestCase2<OfflineGameActivity> {

    private TestHelpers testHelper;

    private Activity curAct;
    private Instrumentation curInstruments;
    private Instrumentation.ActivityMonitor monitor;
    private String[] players;

    public OfflineGameActivityUnitTest() {
        super(OfflineGameActivity.class);
        testHelper = new TestHelpers();
    }

    public OfflineGameActivityUnitTest(Activity curAct,Instrumentation curInstruments){
        super(OfflineGameActivity.class);
        this.curAct = curAct;
        this.curInstruments = curInstruments;
        testHelper = new TestHelpers();
    }

    public void setUp() throws Exception {
        super.setUp();
        testHelper = new TestHelpers();
        Intent intent = new Intent();
        String[] players = new String[]{
                "Red Bob",
                "Orange Bob",
                "Yellow Bob",
                "Green Bob",
                "Blue Bob",
                "Purple Bob"};
        intent.putExtra("PLAYER_NAMES", players);
        setActivityIntent(intent);
        setActivityInitialTouchMode(false);
        curAct = getActivity();
        curInstruments = getInstrumentation();
    }

    public void testActivity() throws Exception {
        activityTestHelper();
        helpActivityTransitionTest();
        activityTestHelper();
    }

    public void helpActivityTransitionTest(){
        monitor = curInstruments.addMonitor(HelpActivity.class.getName(), null, false);

        curInstruments.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        curInstruments.invokeMenuActionSync(curAct, R.id.action_help, 0);

//
//        MenuItem item = new MenuItem() {
//            @Override
//            public int getItemId() {
//                return R.id.action_help;
//            }
//
//            @Override
//            public int getGroupId() {
//                return 0;
//            }
//
//            @Override
//            public int getOrder() {
//                return 0;
//            }
//
//            @Override
//            public MenuItem setTitle(CharSequence charSequence) {
//                return null;
//            }
//
//            @Override
//            public MenuItem setTitle(int i) {
//                return null;
//            }
//
//            @Override
//            public CharSequence getTitle() {
//                return null;
//            }
//
//            @Override
//            public MenuItem setTitleCondensed(CharSequence charSequence) {
//                return null;
//            }
//
//            @Override
//            public CharSequence getTitleCondensed() {
//                return null;
//            }
//
//            @Override
//            public MenuItem setIcon(Drawable drawable) {
//                return null;
//            }
//
//            @Override
//            public MenuItem setIcon(int i) {
//                return null;
//            }
//
//            @Override
//            public Drawable getIcon() {
//                return null;
//            }
//
//            @Override
//            public MenuItem setIntent(Intent intent) {
//                return null;
//            }
//
//            @Override
//            public Intent getIntent() {
//                return null;
//            }
//
//            @Override
//            public MenuItem setShortcut(char c, char c2) {
//                return null;
//            }
//
//            @Override
//            public MenuItem setNumericShortcut(char c) {
//                return null;
//            }
//
//            @Override
//            public char getNumericShortcut() {
//                return 0;
//            }
//
//            @Override
//            public MenuItem setAlphabeticShortcut(char c) {
//                return null;
//            }
//
//            @Override
//            public char getAlphabeticShortcut() {
//                return 0;
//            }
//
//            @Override
//            public MenuItem setCheckable(boolean b) {
//                return null;
//            }
//
//            @Override
//            public boolean isCheckable() {
//                return false;
//            }
//
//            @Override
//            public MenuItem setChecked(boolean b) {
//                return null;
//            }
//
//            @Override
//            public boolean isChecked() {
//                return false;
//            }
//
//            @Override
//            public MenuItem setVisible(boolean b) {
//                return null;
//            }
//
//            @Override
//            public boolean isVisible() {
//                return false;
//            }
//
//            @Override
//            public MenuItem setEnabled(boolean b) {
//                return null;
//            }
//
//            @Override
//            public boolean isEnabled() {
//                return false;
//            }
//
//            @Override
//            public boolean hasSubMenu() {
//                return false;
//            }
//
//            @Override
//            public SubMenu getSubMenu() {
//                return null;
//            }
//
//            @Override
//            public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
//                return null;
//            }
//
//            @Override
//            public ContextMenu.ContextMenuInfo getMenuInfo() {
//                return null;
//            }
//
//            @Override
//            public void setShowAsAction(int i) {
//
//            }
//
//            @Override
//            public MenuItem setShowAsActionFlags(int i) {
//                return null;
//            }
//
//            @Override
//            public MenuItem setActionView(View view) {
//                return null;
//            }
//
//            @Override
//            public MenuItem setActionView(int i) {
//                return null;
//            }
//
//            @Override
//            public View getActionView() {
//                return null;
//            }
//
//            @Override
//            public MenuItem setActionProvider(ActionProvider actionProvider) {
//                return null;
//            }
//
//            @Override
//            public ActionProvider getActionProvider() {
//                return null;
//            }
//
//            @Override
//            public boolean expandActionView() {
//                return false;
//            }
//
//            @Override
//            public boolean collapseActionView() {
//                return false;
//            }
//
//            @Override
//            public boolean isActionViewExpanded() {
//                return false;
//            }
//
//            @Override
//            public MenuItem setOnActionExpandListener(OnActionExpandListener onActionExpandListener) {
//                return null;
//            }
//        };
//        curAct.onOptionsItemSelected(item);

        curAct = curInstruments.waitForMonitorWithTimeout(monitor, testHelper.timeoutForActivityTransition);
        assertNotNull("Transition to HelpActivity Failed", curAct);

        curInstruments.removeMonitor(monitor);
        monitor = curInstruments.addMonitor(OfflineGameActivity.class.getName(),null,false);

        new HelpActivityUnitTest(curAct,curInstruments).activityTest();

        curAct = curInstruments.waitForMonitorWithTimeout(monitor,testHelper.timeoutForActivityTransition);
        assertNotNull("Transition Back to OfflineGameActivity Failed",curAct);
    }

    public void activityTestHelper(){
        TextView offlineCurrentPlayerTextView = (TextView) curAct.findViewById(R.id.offlineCurrentPlayerTextView);
        testHelper.TextViewTest(this,offlineCurrentPlayerTextView,true,"Red Bob");

        Button offlineMoveResetButton = (Button) curAct.findViewById(R.id.offlineMoveResetButton);
        testHelper.ButtonTest(this,offlineMoveResetButton,true);

        Button offlineMoveDoneButton = (Button) curAct.findViewById(R.id.offlineMoveDoneButton);
        testHelper.ButtonTest(this,offlineMoveDoneButton,true);
    }

}
