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