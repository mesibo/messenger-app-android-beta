/**
 * Copyright (c) 2019 Mesibo
 * https://mesibo.com
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the terms and condition mentioned on https://mesibo.com
 * as well as following conditions are met:
 * <p>
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions, the following disclaimer and links to documentation and source code
 * repository.
 * <p>
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * <p>
 * Neither the name of Mesibo nor the names of its contributors may be used to endorse
 * or promote products derived from this software without specific prior written
 * permission.
 * <p>
 * <p>
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
 * <p>
 * Documentation
 * https://mesibo.com/documentation/
 * <p>
 * Source Code Repository
 * https://github.com/mesibo/messenger-app-android
 */


package org.mesibo.messenger;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;

import java.util.ArrayList;


public class BlockedSettingsFragment extends Fragment {

    RecyclerView mBlockedList;
    LinearLayout mBlockedLayout;
    TextView mBlockedContacts;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_blocked_users, container, false);


        Toolbar mToolbar = v.findViewById(R.id.toolbar);


        mBlockedList = v.findViewById(R.id.blockedRecyclerList);

        mBlockedLayout = v.findViewById(R.id.blockedLayout);
        mBlockedContacts = v.findViewById(R.id.blockedContactTV);

        RelaodList();

        return v;

    }


    public void RelaodList() {
        ArrayList<Mesibo.UserProfile> usersList = Mesibo.getSortedUserProfiles();


        ArrayList<Mesibo.UserProfile> newBlockedList = new ArrayList<>();


        for (int i = 0; i < usersList.size(); i++) {

            if (usersList.get(i).groupid <= 0 && usersList.get(i).isBlocked()) {

                Mesibo.UserProfile userProfile = usersList.get(i);
                newBlockedList.add(userProfile);


            }

        }

        setBlockedmUserProfileList(newBlockedList);
        mBlockedContacts.setText("Blocked users : " + newBlockedList.size());
    }

    public void setBlockedmUserProfileList(ArrayList<Mesibo.UserProfile> arrayList) {


        BlockedContactsAdapter mAdapter = new BlockedContactsAdapter(this, arrayList);

        //mBlockedList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mBlockedList.setLayoutManager(layoutManager);
        //mBlockedList.getRecycledViewPool().setMaxRecycledViews(0, 0);
        //mBlockedList.setItemAnimator(new DefaultItemAnimator());
        //mBlockedList.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 70));
        mBlockedList.setAdapter(mAdapter);

    }


    ///Adapter

    public class BlockedContactsAdapter extends RecyclerView.Adapter<BlockedContactsAdapter.MyViewHolder> {
        private BlockedSettingsFragment context;
        private ArrayList<Mesibo.UserProfile> mUserProfileList;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public ImageView thumbnail, callStatus, callVideo;
            LinearLayout mTimeStatusLayut;

            public MyViewHolder(View view) {
                super(view);
                name = view.findViewById(R.id.name);
                thumbnail = view.findViewById(R.id.thumbnail);


            }
        }


        public BlockedContactsAdapter(BlockedSettingsFragment context, ArrayList<Mesibo.UserProfile> UserProfileList) {
            this.context = context;
//        this.listener = listener;
            this.mUserProfileList = UserProfileList;


        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.blocked_user_row_item, parent, false);

            return new MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final Mesibo.UserProfile mUserProfile = mUserProfileList.get(position);


            int pos = holder.getAdapterPosition();

            holder.name.setText(mUserProfileList.get(pos).name);


            String imagePath = Mesibo.getUserProfilePicturePath(mUserProfileList.get(pos), Mesibo.FileInfo.TYPE_AUTO);
            if (null != imagePath) {
                Bitmap b = BitmapFactory.decodeFile(imagePath);
                if (null != b)
                    holder.thumbnail.setImageDrawable(MesiboUtils.getRoundImageDrawable(b));
            }


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    Dialog dialog = new Dialog(getActivity());
                    dialog.setCancelable(true);


                    dialog.setContentView(R.layout.unblock_row_item);

                    TextView unblock = dialog.findViewById(R.id.unblockTV);

                    unblock.setText("Unblock " + mUserProfileList.get(pos).name + "");


                    unblock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Do something

                            Mesibo.UserProfile mUser1 = Mesibo.getUserProfile(mUserProfileList.get(pos).address);
                            mUser1.blockMessages(false);
                            mUser1.blockGroupMessages(false);

                            Mesibo.setUserProfile(mUser1, false);

                            Toast.makeText(getActivity(), "Unblocked", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            context.RelaodList();


                        }
                    });


                    dialog.show();

                }
            });


        }

        @Override
        public int getItemCount() {
            return mUserProfileList.size();
        }


    }


}
