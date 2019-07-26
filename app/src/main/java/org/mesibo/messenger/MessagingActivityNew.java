package org.mesibo.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.messaging.MesiboMessagingFragment;
import com.mesibo.messaging.MesiboUI;


public class MessagingActivityNew extends AppCompatActivity implements MesiboMessagingFragment.FragmentListener {


    private Toolbar mToolbar = null;


    MessagingUIFragmentNew mFragment = null;

    private Mesibo.UIHelperListner mMesiboUIHelperlistener = null;
    private MesiboUI.Config mMesiboUIOptions = null;

    private Mesibo.UserProfile mUser = null;
    private ImageView mProfileImage = null;
    private TextView mUserStatus = null;
    private TextView mTitle = null;
    private String mProfileImagePath = null;
    private Bitmap mProfileThumbnail = null;

    private ActionMode mActionMode = null;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();

    private Mesibo.MessageParams mParameter = null;
    boolean mIsMessagingLite = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (null == args) {
            return;
        }

        //TBD, this must be fixed
        if (!Mesibo.isReady()) {
            finish();
            return;
        }

        mMesiboUIHelperlistener = Mesibo.getUIHelperListner();
        mMesiboUIOptions = MesiboUI.getConfig();

        String peer = args.getString("peer");
        long groupId = args.getLong("groupid");

        if (groupId > 0) {
            mUser = Mesibo.getUserProfile(groupId);
        } else
            mUser = Mesibo.getUserProfile(peer);

        if (null == mUser) {
            finish();
            return;
        }

        mParameter = new Mesibo.MessageParams(peer, groupId, Mesibo.FLAG_DEFAULT, 0);

        // setContentView(R.layout.activity_messaging_new);
        setContentView(R.layout.activity_chat_layout);

        mToolbar = findViewById(R.id.toolbar);
        /// Utils.setActivityStyle(this, mToolbar);


        setSupportActionBar(mToolbar);
        final ActionBar ab = getSupportActionBar();
        //getSupportActionBar().setHomeAsUpIndicator(new RoundImageDrawable(b));
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        mUserStatus = (TextView) findViewById(R.id.chat_profile_subtitle);
        // Utils.setTextViewColor(mUserStatus, TOOLBAR_TEXT_COLOR);

        mProfileImage = (ImageView) findViewById((R.id.chat_profile_pic));
        if (mProfileImage != null) {

            mProfileImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == mProfileImagePath) {
                        return;
                    }

                    // MesiboUIManager.launchPictureActivity(MessagingActivityNew.this, mUser.name, mProfileImagePath);
                }
            });
        }

        RelativeLayout nameLayout = (RelativeLayout) findViewById(R.id.name_tite_layout);
        mTitle = (TextView) findViewById(R.id.chat_profile_title);
        mTitle.setText(mUser.name);
        // Utils.setTextViewColor(mTitle, TOOLBAR_TEXT_COLOR);

        if (mTitle != null) {
            nameLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //MesiboUIManager.launchUserProfile(MessagingActivityNew.this, mParameter.peer, mParameter.groupid, v);
                    if (null != mMesiboUIHelperlistener)
                        mMesiboUIHelperlistener.Mesibo_onShowProfile(MessagingActivityNew.this, mUser);
                }
            });
        }

        startFragment(savedInstanceState);

    }

    private void startFragment(Bundle savedInstanceState) {
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (findViewById(R.id.fragment_container) == null || savedInstanceState != null) {
            return;
        }

        // Create a new Fragment to be placed in the activity layout
        //if (mIsMessagingLite)
            //mFragment = new MessagingUIFragmentLite();
       // else
            mFragment = new MessagingUIFragmentNew();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        mFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragment).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (null == mMesiboUIHelperlistener)
            return true;

        //int menuId = mMesiboUIHelperlistener.Mesibo_onGetMenuResourceId(FROM_MESSAGING_ACTIVITY);
        //getMenuInflater().inflate(menuId, menu);

        mMesiboUIHelperlistener.Mesibo_onGetMenuResourceId(this, FROM_MESSAGING_ACTIVITY, mParameter, menu);

        return true;
    }

    static int FROM_MESSAGING_ACTIVITY = 1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        } else {
            mMesiboUIHelperlistener.Mesibo_onMenuItemSelected(this, FROM_MESSAGING_ACTIVITY, mParameter, id);
        }
        return super.onOptionsItemSelected(item);
    }


    //TBD, note this requires API level 10
    /*
    private Bitmap createThumbnailAtTime(String filePath, int timeInSeconds){
        MediaMetadataRetriever mMMR = new MediaMetadataRetriever();
        mMMR.setDataSource(filePath);
        //api time unit is microseconds
        return mMMR.getFrameAtTime(timeInSeconds*1000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
    }
    */


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mFragment.Mesibo_onBackPressed()) {
            return;
        }

        super.onBackPressed(); // allows standard use of backbutton for page 1

    }

    @Override
    public void Mesibo_onUpdateUserPicture(Mesibo.UserProfile profile, Bitmap thumbnail, String picturePath) {
        mProfileThumbnail = thumbnail;
        mProfileImagePath = picturePath;
        mProfileImage.setImageDrawable(MesiboUtils.getRoundImageDrawable(mProfileThumbnail));
    }

    @Override
    public void Mesibo_onUpdateUserOnlineStatus(Mesibo.UserProfile profile, String status) {
        if (null == status) {
            mUserStatus.setVisibility(View.GONE);
            return;
        }

        mUserStatus.setVisibility(View.VISIBLE);
        mUserStatus.setText(status);
        return;
    }

    @Override
    public void Mesibo_onShowInContextUserInterface() {
        mActionMode = startSupportActionMode(mActionModeCallback);
    }

    @Override
    public void Mesibo_onHideInContextUserInterface() {
        if (null == mActionMode)
            return;
        mActionMode.finish();
    }

    @Override
    public void Mesibo_onContextUserInterfaceCount(int count) {
        if (null == mActionMode)
            return;

        mActionMode.setTitle(String.valueOf(count));
        mActionMode.invalidate();
    }

    @Override
    public void Mesibo_onError(int type, String title, String message) {
        //  Utils.showAlert(this, title, message);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mFragment.Mesibo_onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        mFragment.Mesibo_onActivityResult(requestCode, resultCode, data);
    }

//    private class ActionModeCallback implements ActionMode.Callback {
//        @SuppressWarnings("unused")
//        private final String TAG = ActionModeCallback.class.getSimpleName();
//
//
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            menu.clear();
//            mode.getMenuInflater().inflate(com.mesibo.messaging.R.menu.selected_menu, menu);
//
//            menu.findItem(com.mesibo.messaging.R.id.menu_reply).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(com.mesibo.messaging.R.id.menu_star).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(com.mesibo.messaging.R.id.menu_resend).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(com.mesibo.messaging.R.id.menu_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(com.mesibo.messaging.R.id.menu_forward).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(com.mesibo.messaging.R.id.menu_forward).setVisible(mMesiboUIOptions.enableForward);
//            menu.findItem(com.mesibo.messaging.R.id.menu_forward).setEnabled(mMesiboUIOptions.enableForward);
//            menu.findItem(com.mesibo.messaging.R.id.menu_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//
//            int enabled = mFragment.Mesibo_onGetEnabledActionItems();
//
//
//            menu.findItem(com.mesibo.messaging.R.id.menu_resend).setVisible((enabled&MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_RESEND) > 0);
//
//            //menu.findItem(R.id.menu_forward).setVisible(selection.size() == 1);
//            menu.findItem(com.mesibo.messaging.R.id.menu_copy).setVisible((enabled&MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_COPY) > 0);
//            menu.findItem(com.mesibo.messaging.R.id.menu_copy).setVisible((enabled&MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_REPLY) > 0);
//            return true;
//        }
//
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//
//            int mesiboItemId = 0;
//
//            if (item.getItemId() == com.mesibo.messaging.R.id.menu_remove) {
//                mesiboItemId = MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_DELETE;
//            } else if (item.getItemId() == com.mesibo.messaging.R.id.menu_copy) {
//
//                mesiboItemId = MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_COPY;
//
//            } else if (item.getItemId() == com.mesibo.messaging.R.id.menu_resend) {
//                mesiboItemId = MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_RESEND;
//            } else if (item.getItemId() == com.mesibo.messaging.R.id.menu_forward) {
//                mesiboItemId = MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_FORWARD;
//            } else if (item.getItemId() == com.mesibo.messaging.R.id.menu_star) {
//                mesiboItemId = MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_FAVORITE;
//            } else if (item.getItemId() == com.mesibo.messaging.R.id.menu_reply) {
//                mesiboItemId = MesiboMessagingFragment.MESIBO_MESSAGECONTEXTACTION_REPLY;
//            }
//
//            if(mesiboItemId > 0) {
//                mFragment.Mesibo_onActionItemClicked(mesiboItemId);
//                mode.finish();
//                mFragment.Mesibo_onInContextUserInterfaceClosed();
//                return true;
//            }
//
//            return false;
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            mFragment.Mesibo_onInContextUserInterfaceClosed();
//            mActionMode = null;
//        }
//    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = MessagingActivityNew.ActionModeCallback.class.getSimpleName();


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            mode.getMenuInflater().inflate(R.menu.selected_menu_app, menu);
            menu.findItem(R.id.menu_reply).setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.menu_star).setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.menu_resend).setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.menu_copy).setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.menu_forward).setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menu.findItem(R.id.menu_remove).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.menu_share).setVisible(false).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            int enabled = mFragment.Mesibo_onGetEnabledActionItems();


            menu.findItem(R.id.menu_reply).setVisible((enabled & MessagingUIFragmentNew.MESIBO_MESSAGECONTEXTACTION_REPLY) > 0);
            menu.findItem(R.id.menu_copy).setVisible((enabled & MessagingUIFragmentNew.MESIBO_MESSAGECONTEXTACTION_COPY) > 0);
            menu.findItem(R.id.menu_share).setVisible((enabled & MessagingUIFragmentNew.MESIBO_MESSAGECONTEXTACTION_COPY) > 0);

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int mesiboItemId = 0;

            if (item.getItemId() == R.id.menu_remove) {
                mesiboItemId = AppConfig.MESSAGECONTEXTACTION_DELETE;
            } else if (item.getItemId() == R.id.menu_copy) {
                mesiboItemId = AppConfig.MESSAGECONTEXTACTION_COPY;
            } else if (item.getItemId() == R.id.menu_resend) {
                mesiboItemId = AppConfig.MESSAGECONTEXTACTION_RESEND;
            } else if (item.getItemId() == R.id.menu_forward) {
                mesiboItemId = AppConfig.MESSAGECONTEXTACTION_FORWARD;
            } else if (item.getItemId() == R.id.menu_star) {
                mesiboItemId = AppConfig.MESSAGECONTEXTACTION_FAVORITE;
            } else if (item.getItemId() == R.id.menu_reply) {
                mesiboItemId = AppConfig.MESSAGECONTEXTACTION_REPLY;
            } else if (item.getItemId() == R.id.menu_share) {
                mesiboItemId = AppConfig.MESSAGECONTEXTACTION_SHARE;
            }

            if (mesiboItemId > 0) {
                mFragment.onActionItemClicked(mesiboItemId);
                mode.finish();
                mFragment.Mesibo_onInContextUserInterfaceClosed();
                return true;

            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFragment.Mesibo_onInContextUserInterfaceClosed();
            mActionMode = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MesiboUIManager.setMessagingActivityNew(this);
    }
}

