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

package org.mesibo.messenger.Contacts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.contactutils.MesiboContactsReader;
import com.mesibo.messaging.MessagingActivity;


import org.mesibo.messenger.R;

import java.util.ArrayList;
import java.util.Objects;


public class ContactsFragment extends Fragment implements MesiboContactsReader.ContactsReaderListener {


    public static final String TAG = "ContactsFragment";
    RecyclerView mRecyclerView;
    RecyclerView mContactsLinkedList;
    TextView mNoContactsText;
    private ContactsAdapter mAdapter;
    ArrayList<ContactsItem> mContactsArrayList = new ArrayList<>();
    ArrayList<ContactsItem> mContactsNewList = new ArrayList<>();
    private Mesibo.UserProfile mUser = null;
    MesiboContactsReader mContactsReader;
    MesiboContactsReader.Contact contact;
    Boolean mIsLoaded = false;
    String mPreviousName = "";
    int mCount = 20;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setNestedScrollingEnabled(false);
        mContactsLinkedList = view.findViewById(R.id.ContactsRecyclerList);
        mContactsLinkedList.setNestedScrollingEnabled(false);
        mNoContactsText = view.findViewById(R.id.no_contacts_tv);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        setHasOptionsMenu(true);
        RelaodList();

        return view;


    }


    public void startReadingContacts() {

        mContactsReader.read(mCount);


    }


    public void setContactList(ArrayList<ContactsItem> arrayList) {

        mNoContactsText.setVisibility(arrayList.size() > 0 ? View.GONE : View.VISIBLE);
        mAdapter = new ContactsAdapter(getActivity(), arrayList, mContactsReader);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 70));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView mRecyclerView, int newState) {
                super.onScrollStateChanged(mRecyclerView, newState);

                if (!mRecyclerView.canScrollVertically(1)) {
                    mCount = mCount + 10;
                    mIsLoaded = true;
                    startReadingContacts();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.messaging_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem actionSetting = menu.findItem(R.id.action_settings);
        actionSetting.setVisible(false);
        MenuItemCompat.setShowAsAction(actionSetting, MenuItemCompat.SHOW_AS_ACTION_NEVER);


        MenuItem actionMessgae = menu.findItem(R.id.mesibo_contacts);
        actionMessgae.setVisible(false);
        MenuItemCompat.setShowAsAction(actionMessgae, MenuItemCompat.SHOW_AS_ACTION_NEVER);


        MenuItem myActionMenuItem = menu.findItem(R.id.mesibo_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                while (mContactsReader.read(mCount)) {

                }
            }
        });

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mesibo_search) {


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);

        mContactsReader = new MesiboContactsReader(getActivity(), null, this);
        mContactsArrayList.clear();

        startReadingContacts();


    }


    @Override
    public void onPause() {
        super.onPause();


        mContactsArrayList.clear();
        mIsLoaded = false;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactsReader.close();

    }

    @Override
    public void onStop() {
        super.onStop();
        mContactsReader.close();
    }

    @Override
    public boolean ContactsReader_onContact(MesiboContactsReader.Contact contact) {

        ContactsItem contactsItem = new ContactsItem();


        if (null != contact) {

            Log.d(TAG, "Contacts : " + contact.Name + " --- " + contact.PhotoUri + "---" + contact.Numbers);


            contactsItem.setContact(contact);

            String currentName = String.valueOf(contact.Name.charAt(0)).toUpperCase();

            if (!TextUtils.equals(mPreviousName, currentName)) {

                mPreviousName = currentName;
                ContactsItem contactsItem1 = new ContactsItem();


                contactsItem1.setContact(contact);
                contactsItem1.setSection(true);
                contactsItem1.setSectionHeaderTitle(currentName);
                mContactsArrayList.add(contactsItem1);

            }

            mContactsArrayList.add(contactsItem);


        } else {

            if (mIsLoaded) {

                mAdapter.notifyDataSetChanged();


            } else {
                setContactList(mContactsArrayList);


            }


        }

        return false;
    }


    public void RelaodList() {
        ArrayList<Mesibo.UserProfile> usersList = Mesibo.getSortedUserProfiles();


        ArrayList<Mesibo.UserProfile> newBlockedList = new ArrayList<>();


        for (int i = 0; i < usersList.size(); i++) {

            if (usersList.get(i).groupid <= 0) {

                Mesibo.UserProfile userProfile = usersList.get(i);
                newBlockedList.add(userProfile);


            }

        }

        setBlockedmUserProfileList(newBlockedList);

    }

    public void setBlockedmUserProfileList(ArrayList<Mesibo.UserProfile> arrayList) {

        BlockedContactsAdapter mAdapter = new BlockedContactsAdapter(this, arrayList);

        //mBlockedList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mContactsLinkedList.setLayoutManager(layoutManager);
        //mBlockedList.getRecycledViewPool().setMaxRecycledViews(0, 0);
        //mBlockedList.setItemAnimator(new DefaultItemAnimator());
        //mBlockedList.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 70));
        mContactsLinkedList.setAdapter(mAdapter);

    }


    ///Adapter

    public class BlockedContactsAdapter extends RecyclerView.Adapter<BlockedContactsAdapter.MyViewHolder> {
        private ContactsFragment context;
        private ArrayList<Mesibo.UserProfile> mUserProfileList;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView name, status;
            public ImageView thumbnail, callStatus, callVideo;
            LinearLayout mTimeStatusLayut;

            public MyViewHolder(View view) {
                super(view);

                name = view.findViewById(R.id.name);
                status = view.findViewById(R.id.status);
                thumbnail = view.findViewById(R.id.thumbnail);

            }
        }


        public BlockedContactsAdapter(ContactsFragment context, ArrayList<Mesibo.UserProfile> UserProfileList) {
            this.context = context;
//        this.listener = listener;
            this.mUserProfileList = UserProfileList;


        }

        @Override
        public BlockedContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.blocked_user_row_item, parent, false);

            return new BlockedContactsAdapter.MyViewHolder(itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(BlockedContactsAdapter.MyViewHolder holder, final int position) {
            final Mesibo.UserProfile mUserProfile = mUserProfileList.get(position);


            int pos = holder.getAdapterPosition();

            holder.name.setText(mUserProfileList.get(pos).name);

            if (null != mUserProfileList.get(pos).status) {
                holder.status.setVisibility(View.VISIBLE);

                if (!mUserProfileList.get(pos).status.isEmpty()) {
                    holder.status.setText(mUserProfileList.get(pos).status);
                } else {
                    holder.status.setText("Hey there! I am using MESIBO");
                }
            } else {
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText("Hey there! I am using MESIBO");
            }


            String imagePath = Mesibo.getUserProfilePicturePath(mUserProfileList.get(pos), Mesibo.FileInfo.TYPE_AUTO);
            if (null != imagePath) {
                Bitmap b = BitmapFactory.decodeFile(imagePath);
                if (null != b)
                    holder.thumbnail.setImageDrawable(MesiboUtils.getRoundImageDrawable(b));
            } else {
                holder.thumbnail.setImageDrawable(MesiboUtils.getRoundImageDrawable(BitmapFactory.decodeResource(Objects.requireNonNull(getActivity()).getResources(), R.drawable.default_user_image)));
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(getActivity(), MessagingActivity.class);
                    intent.putExtra("peer", mUserProfileList.get(pos).address);
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return mUserProfileList.size();
        }

    }


}
