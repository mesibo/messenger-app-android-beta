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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesibo.api.MesiboUtils;
import com.mesibo.contactutils.MesiboContactsReader;
import com.mesibo.messaging.MesiboUI;


import org.mesibo.messenger.R;

import java.util.ArrayList;

public class ContactDetailsActivity extends AppCompatActivity implements MesiboContactsReader.ContactsReaderListener {

    private static final String TAG = "ContactDetailsActivity";
    ImageView userImage;
    String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);

        MesiboUI.Config opt = MesiboUI.getConfig();

        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(opt.mToolbarColor));
        ab.setTitle("Contact Details");

        Intent intent = getIntent();

        assert intent != null;
         mName = intent.getStringExtra("name");
        Bitmap bitmap = intent.getParcelableExtra("BitmapImage");
        ArrayList<String> numbersList = intent.getStringArrayListExtra("numbers");
        String photoURI = intent.getStringExtra("PhotoURI");

        userImage = findViewById(R.id.user_image);


        if(null == photoURI ){
            userImage.setImageDrawable(MesiboUtils.getRoundImageDrawable(bitmap));
        }else{
            setProfilePic(photoURI);
        }




        Log.d(TAG, "Numbers : " + mName + " - " + numbersList.size());

        TextView userName = findViewById(R.id.user_name);
        userName.setText(mName);





        LinearLayout numbersListLayout = findViewById(R.id.numberListlinearLayout);


        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(5, 5, 5, 5);


        numbersListLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int j = 0; j < numbersList.size(); j++) {

            View numberLayout = inflater.inflate(R.layout.phone_number_row_item, null, false);

            TextView number = numberLayout.findViewById(R.id.number);
            TextView type = numberLayout.findViewById(R.id.numberType);

            LinearLayout inviteLayout = numberLayout.findViewById(R.id.inviteLayout);

            inviteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, "Mesibo");
                        String sAux = "\nI found Mesibo app really cool.\n Check it Out \nhttps://play.google.com/store/apps/details?id=com.mesibo.mesiboapplication\n";
                        //sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n";
                        i.putExtra(Intent.EXTRA_TEXT, sAux);
                        startActivity(Intent.createChooser(i, "Select One"));

                    } catch (Exception e) {
                        //e.toString();

                    }
                }
            });


            String Numbers[] = numbersList.get(j).split("-");

            number.setText(Numbers[0]);

            String typeN = Numbers[1];

            if (typeN.matches("0") || typeN.matches("2")) {

                type.setText("Mobile");
            } else {
                type.setText("Others");
            }

            numbersListLayout.addView(numberLayout);
            Log.d(TAG, "Numbers List: " + numbersList.get(j));
        }


    }


    public void setProfilePic(String photoUri) {

        MesiboContactsReader mesiboContactsReader = new MesiboContactsReader(this, null, this);
        Bitmap bitmap = mesiboContactsReader.loadPhoto(mName, photoUri, 500, false);
        userImage.setImageDrawable(MesiboUtils.getRoundImageDrawable(bitmap));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 1)

            getSupportFragmentManager().popBackStackImmediate();
        else
            finish();
    }

    @Override
    public boolean ContactsReader_onContact(MesiboContactsReader.Contact contact) {
        return false;
    }
}
