/** Copyright (c) 2019 Mesibo
 * https://mesibo.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the terms and condition mentioned on https://mesibo.com
 * as well as following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions, the following disclaimer and links to documentation and source code
 * repository.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 *
 * Neither the name of Mesibo nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Documentation
 * https://mesibo.com/documentation/
 *
 * Source Code Repository
 * https://github.com/mesibo/messenger-app-android
 *
 */

package org.mesibo.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.MesiboCall;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.messaging.MesiboUserListFragment;
import com.mesibo.messaging.MesiboMessagingFragment;

import java.util.ArrayList;
import java.util.List;

public class MesiboMainActivity extends AppCompatActivity implements MesiboUserListFragment.FragmentListener, MesiboMessagingFragment.FragmentListener, MediaPicker.ImageEditorListener, Mesibo.CallListener {


    MesiboUserListFragment mUserListFragment;
    MesiboMyCallLogsFragment mesiboMyCallLogsFragment;
    ViewPagerAdapter mAdapter;
    RelativeLayout mReturnToCallFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pager_layout);

        mReturnToCallFragment  = findViewById(R.id.returnToCallLayout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        mReturnToCallFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MesiboCall.getInstance().call(MesiboMainActivity.this, 0,null, false);
            }
        });


        Mesibo.addListener(this);

    }


    private void setupViewPager(ViewPager viewPager) {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        String myMessage = "NewContactSelector";
        bundle.putString("message", myMessage );
        // Create a new Fragment to be placed in the activity layout
        mUserListFragment = new MesiboUserListFragment();
        mUserListFragment.setListener(this);
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        mUserListFragment.setArguments(bundle);

        mesiboMyCallLogsFragment = new MesiboMyCallLogsFragment();
        mAdapter.addFragment(mUserListFragment, "Chats");
        mAdapter.addFragment(mesiboMyCallLogsFragment,"Call Logs");

        viewPager.setAdapter(mAdapter);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String filePath = MediaPicker.processOnActivityResult(this, requestCode, resultCode, data);


        if(null == filePath)
            return;

        UIManager.launchImageEditor(this, MediaPicker.TYPE_FILEIMAGE, -1, null, filePath, false, false, true, true, 600, this);

    }

    @Override
    public void onImageEdit(int i, String s, String filePath, Bitmap bitmap, int status1) {

    }

    @Override
    public void Mesibo_onUpdateTitle(String s) {

    }

    @Override
    public void Mesibo_onUpdateSubTitle(String s) {

    }

    @Override
    public boolean Mesibo_onClickUser(String s, long l, long l1) {

            Intent i = new Intent(this, MessagingActivityNew.class);
            i.putExtra("peer", s);
            i.putExtra("groupid", l);
            startActivity(i);

        //return false to load default
        return true;
    }

    @Override
    public boolean Mesibo_onUserListFilter(Mesibo.MessageParams messageParams) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void Mesibo_onUpdateUserPicture(Mesibo.UserProfile userProfile, Bitmap bitmap, String s) {

    }

    @Override
    public void Mesibo_onUpdateUserOnlineStatus(Mesibo.UserProfile userProfile, String s) {

    }

    @Override
    public void Mesibo_onShowInContextUserInterface() {

    }

    @Override
    public void Mesibo_onHideInContextUserInterface() {

    }

    @Override
    public void Mesibo_onContextUserInterfaceCount(int i) {

    }

    @Override
    public void Mesibo_onError(int i, String s, String s1) {

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean Mesibo_onCall(long l, long l1, Mesibo.UserProfile userProfile, int i) {
        return false;
    }

    @Override
    public boolean Mesibo_onCallStatus(long l, long l1, int i, int i1, String s) {

        UIManager.showOnCallProgressGreenBar(mReturnToCallFragment);
        return false;
    }

    @Override
    public void Mesibo_onCallServer(int i, String s, String s1, String s2) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
