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

import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.messaging.MesiboMessagingFragment;
import com.mesibo.messaging.MesiboRecycleViewHolder;

import org.mesibo.messenger.R;

import java.util.Calendar;
import java.util.Locale;


public class MessagingUIFragmentLite extends MesiboMessagingFragment implements MesiboRecycleViewHolder.Listener, Mesibo.FileTransferListener {


    public static final int TYPE_SEND_CONATCT = 113;
    public static final int TYPE_RECEIVE_CONTACT = 114;
    public int mTYPE = 0;




    @Override
    public int Mesibo_onGetItemViewType(Mesibo.MessageParams messageParams, String s) {


        if (messageParams.isIncoming()) {
            return MesiboRecycleViewHolder.TYPE_INCOMING;
        }

        if (messageParams.getStatus() == MesiboRecycleViewHolder.TYPE_HEADER) {
            return MesiboRecycleViewHolder.TYPE_HEADER;
        }

        if (messageParams.isSavedMessage()) {
            return MesiboRecycleViewHolder.TYPE_CUSTOM;
        }

        if (messageParams.isMissedCall()) {
            return MesiboRecycleViewHolder.TYPE_MISSEDCALL;
        }

        return MesiboRecycleViewHolder.TYPE_OUTGOING;


        // return 0;
    }


    public class HeaderViewHolder extends MesiboRecycleViewHolder {

        View mViewHeader;

        public HeaderViewHolder(View v) {
            super(v);
            mViewHeader = v;


        }
    }

    public class SendContactViewHolder extends MesiboRecycleViewHolder {

        TextView mContactName, mMessageContact;
        ImageView mContactImage;
        View mViewSendContact;

        public SendContactViewHolder(View v) {
            super(v);
            mContactName = v.findViewById(R.id.contactName);
            mMessageContact = v.findViewById(R.id.messageContact);
            mContactImage = v.findViewById(R.id.contactImage);
            mViewSendContact = v;


        }
    }


    public class ReceiveContactViewHolder extends MesiboRecycleViewHolder {

        TextView mContactName, mMessageContact, mAddToContact;
        ImageView mContactImage;
        View mViewReceiveContact;

        public ReceiveContactViewHolder(View v) {
            super(v);
            mContactName = v.findViewById(R.id.contactName);
            mMessageContact = v.findViewById(R.id.messageTV);
            mAddToContact = v.findViewById(R.id.addToContactTV);
            mContactImage = v.findViewById(R.id.contactImage);
            mViewReceiveContact = v;


        }
    }


    public class MissedCallViewHolder extends MesiboRecycleViewHolder {

        TextView missedTS, callTypeText, customMessageTV;
        ImageView missedCallImage;
        LinearLayout mMissedCallLayout, mCustomMessageLayout;
        View mMissedViewHeader;

        public MissedCallViewHolder(View v) {
            super(v);


            mMissedCallLayout = v.findViewById(R.id.missedCallLayout);
            mCustomMessageLayout = v.findViewById(R.id.customeMessageLayout);
            customMessageTV = v.findViewById(R.id.customeMessage);
            missedTS = v.findViewById(R.id.missedcallTS);
            callTypeText = v.findViewById(R.id.callTypeMsg);
            missedCallImage = v.findViewById(R.id.missedCallImage);
            mMissedViewHeader = v;


        }
    }




    @Override
    public MesiboRecycleViewHolder Mesibo_onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {


        if (TYPE_SEND_CONATCT == type) {


            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.send_contact_row_item, viewGroup, false);
            return new SendContactViewHolder(v);

        } else if (TYPE_RECEIVE_CONTACT == type) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receive_contact_row_item, viewGroup, false);
            return new ReceiveContactViewHolder(v);

        } else if (MesiboRecycleViewHolder.TYPE_HEADER == type) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_header_view, viewGroup, false);
            return new HeaderViewHolder(v);

        } else if (MesiboRecycleViewHolder.TYPE_MISSEDCALL == type) {

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_missedcall_view, viewGroup, false);
            return new MissedCallViewHolder(v);

        }


        return null;
    }


    @Override
    public void Mesibo_onBindViewHolder(MesiboRecycleViewHolder mesiboRecycleViewHolder, int type, boolean b, Mesibo.MessageParams messageParams, Mesibo.MesiboMessage mesiboMessage) {


        if (type == MesiboRecycleViewHolder.TYPE_HEADER) {

            mTYPE = MesiboRecycleViewHolder.TYPE_HEADER;
            HeaderViewHolder HeaderView = (HeaderViewHolder) mesiboRecycleViewHolder;

        } else if (type == MesiboRecycleViewHolder.TYPE_MISSEDCALL) {
            mTYPE = type;
            MissedCallViewHolder missedCallViewHolder = (MissedCallViewHolder) mesiboRecycleViewHolder;
            missedCallViewHolder.mCustomMessageLayout.setVisibility(View.GONE);
            missedCallViewHolder.mMissedCallLayout.setVisibility(View.VISIBLE);
            missedCallViewHolder.missedTS.setText(getTIME(mesiboMessage.ts));

            if (messageParams.isVoiceCall()) {
                missedCallViewHolder.callTypeText.setText("Missed audio call at");
                Integer mAudioMissedImage = R.drawable.baseline_call_missed_black_24;
                missedCallViewHolder.missedCallImage.setImageResource(mAudioMissedImage);
            } else {
                missedCallViewHolder.callTypeText.setText("Missed video call at");
                Integer mVideoMissedImage = R.drawable.baseline_missed_video_call_black_24;
                missedCallViewHolder.missedCallImage.setImageResource(mVideoMissedImage);
            }

        } else if (type == MesiboRecycleViewHolder.TYPE_CUSTOM) {

            MissedCallViewHolder customMessage = (MissedCallViewHolder) mesiboRecycleViewHolder;
            customMessage.mCustomMessageLayout.setVisibility(View.VISIBLE);
            customMessage.mMissedCallLayout.setVisibility(View.GONE);
            customMessage.customMessageTV.setText(mesiboMessage.message);

        }




    }


    @Override
    public void Mesibo_onViewRecycled(MesiboRecycleViewHolder mesiboRecycleViewHolder) {

    }

    @Override
    public void Mesibo_oUpdateViewHolder(MesiboRecycleViewHolder mesiboRecycleViewHolder, Mesibo.MesiboMessage mesiboMessage) {

    }

    private String getTIME(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("hh:mm a", cal).toString();
    }




}