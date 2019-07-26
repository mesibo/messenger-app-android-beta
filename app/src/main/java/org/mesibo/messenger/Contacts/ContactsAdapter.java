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

package org.mesibo.messenger.Contacts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.contactutils.MesiboContactsReader;


import org.mesibo.messenger.R;

import java.util.ArrayList;
import java.util.List;


public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "ContactsAdapter";
    private Context context;
    private List<ContactsItem> mContactList;
    private List<ContactsItem> ContactsItemList;
    int mDefaultImage;
    MesiboContactsReader mContactsReader;

    public static final int SECTION_VIEW = 0;
    public static final int CONTENT_VIEW = 1;


    private Mesibo.UserProfile mUser = null;


    @Override
    public int getItemViewType(int position) {
        if (ContactsItemList.get(position).isSection()) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView thumbnail, audioCall, videoCall;
        public LinearLayout rootLayout;


        public ContactsViewHolder(View view, final Context context) {
            super(view);
            name = view.findViewById(R.id.name);
            thumbnail = view.findViewById(R.id.thumbnail);
            rootLayout = view.findViewById(R.id.contactsItemLayout);
            audioCall = view.findViewById(R.id.audioCall);
            videoCall = view.findViewById(R.id.videoCall);


        }
    }

    public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitleTextview;

        public SectionHeaderViewHolder(View itemView) {
            super(itemView);
            headerTitleTextview = itemView.findViewById(R.id.headerTitleTextview);
        }
    }


    public ContactsAdapter(Context context, ArrayList<ContactsItem> callLogsList, MesiboContactsReader ContactsReader) {
        this.context = context;
        this.mContactList = callLogsList;
        this.ContactsItemList = callLogsList;
        this.mContactsReader = ContactsReader;

        mDefaultImage = com.mesibo.messaging.R.drawable.default_user_image;
        Log.d(TAG, "ArrayList size in Adapter: " + ContactsItemList.size());
    }
//
//    @Override
//    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.contacts_row_item, parent, false);
//
//        return new ContactsViewHolder(itemView);
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == SECTION_VIEW) {
            return new SectionHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_title, parent, false));
        }
        return new ContactsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row_item, parent, false), context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ContactsItem contactsList = ContactsItemList.get(position);


        if (SECTION_VIEW == getItemViewType(position)) {

            SectionHeaderViewHolder sectionHeaderViewHolder = (SectionHeaderViewHolder) holder;
            ContactsItem sectionItem = ContactsItemList.get(position);
            sectionHeaderViewHolder.headerTitleTextview.setText(sectionItem.getSectionHeaderTitle());
            return;

        }

        ContactsViewHolder itemViewHolder = (ContactsViewHolder) holder;
        String name = contactsList.getContact().Name;
        itemViewHolder.name.setText(name);

        List<MesiboContactsReader.PhoneNumber> Numbers;
        ArrayList<String> numbersList = new ArrayList<>();


        Numbers = contactsList.getContact().Numbers;
        Log.d("numberSize", "" + name + " : " + Numbers.size());


        if (0 < Numbers.size()) {
            for (int i = 0; i < Numbers.size(); i++) {

                numbersList.add(Numbers.get(i).Phone + "-" + Numbers.get(i).type);

            }
        }

        Log.d(TAG, "NumberList : " + numbersList.size());


        Bitmap bmp = mContactsReader.loadPhoto(contactsList.getContact(), 50, true);



        itemViewHolder.thumbnail.setImageDrawable(MesiboUtils.getRoundImageDrawable(bmp));


        itemViewHolder.audioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        itemViewHolder.videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        itemViewHolder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(context, ContactDetailsActivity.class);
                i.putExtra("name", name);
                i.putExtra("BitmapImage", bmp);
                i.putStringArrayListExtra("numbers", numbersList);
                i.putExtra("PhotoURI", contactsList.getContact().PhotoUri);

                context.startActivity(i);

            }
        });

//        Bitmap b;
//        if (null != filePath) {
//            b = BitmapFactory.decodeFile(filePath);
//            if (null != b) {
//                holder.thumbnail.setImageDrawable(new RoundImageDrawable(b));
//            }else {
//
//                mContactsReader.loadPhoto(contactsList,50,true);
//                //TBD, getActivity.getresource crashes sometime if activity is closing
//                holder.thumbnail.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeResource(context.getResources(), mDefaultImage)));
//            }
//        } else {
//            mContactsReader.loadPhoto(contactsList,50,true);
//            //TBD, getActivity.getresource crashes sometime if activity is closing
//            holder.thumbnail.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeResource(context.getResources(), mDefaultImage)));
//        }




    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "Item Count : " + ContactsItemList.size());
        return ContactsItemList.size();

    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    ContactsItemList = mContactList;
                } else {
                    List<ContactsItem> filteredList = new ArrayList<>();
                    for (ContactsItem row : mContactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or peer match
                        if (row.getContact().Name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    ContactsItemList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = ContactsItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                ContactsItemList = (ArrayList<ContactsItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
