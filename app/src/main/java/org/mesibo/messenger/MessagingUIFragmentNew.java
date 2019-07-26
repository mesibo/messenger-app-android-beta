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

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mesibo.api.Mesibo;
import com.mesibo.calls.MesiboCall;
import com.mesibo.messaging.MesiboMessagingFragment;
import com.mesibo.messaging.MesiboRecycleViewHolder;
import org.mesibo.messenger.Utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


public class MessagingUIFragmentNew extends MesiboMessagingFragment implements MesiboRecycleViewHolder.Listener, Mesibo.FileTransferListener/*, MesiboUserListFragment.FragmentListener*/ {


    public static final int TYPE_SEND_CONATCT = 113;
    public static final int TYPE_RECEIVE_CONTACT = 114;
    public static boolean mDefaultLayout = true;

    boolean doubleBackToExitPressedOnce = false;
    public int mTYPE = 0;
    private ProgressBar mProgressBar;
    long mMid;
    Mesibo.MesiboMessage mMesiboMessage;
    MesiboRecycleViewHolder mMesiborecyclerView;
    boolean mMessageTypeIncoming, mMessageSelected = false;
    ArrayList<MesiboRecycleViewHolder> mesiboRecycleViewHolderArrayList = new ArrayList<>();
    ArrayList<Long> mMidToDeleteList = new ArrayList<>();
    boolean replyEnabled = false;
    int mHideRepy = 1;
    private Mesibo.UserProfile mUser = null;

    ArrayList<Integer> mRecyclerPosArrayList = new ArrayList<>();
    private Integer images[] = {R.drawable.ic_av_timer_black_18dp, R.drawable.message_got_receipt_from_server,
            R.drawable.message_got_receipt_from_target, R.drawable.message_got_read_receipt_from_target, R.drawable.ic_action_block};
    private Integer mAudioMissedImage = R.drawable.baseline_call_missed_black_24;
    private Integer mVideoMissedImage = R.drawable.baseline_missed_video_call_black_24;

    int mBlackTintColor = Color.parseColor("#858586");


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

    public class IncomingMessgaeViewHolder extends MesiboRecycleViewHolder {


        RelativeLayout mRootLayout, mAudioLayout, mOthersLayout, mProgressLayout, mPlayButtonLayout;
        LinearLayout mMessageLayout;
        TextView mCaptionTv, mAudioduration, mSenderName, mMessageTV, mChatTimeTv;
        ImageView mAudioPlayButton, mAudioPauseButton, mImageContainerView, mPlayButton;
        ProgressBar mMediaProgress;
        LinearLayout mLinkPreviewLayout, mLinkImageLayout, mLinkCancelLayout;
        ImageView mLinkImage;
        TextView mLinkTitle, mLinkDesc, mLinkUrl;
        boolean mLinkActive = true;


        Boolean mLiked = false;


        View mViewIncomingMessage;

        public IncomingMessgaeViewHolder(View v) {
            super(v);
            mRootLayout = v.findViewById(R.id.rootLayout);
            mMessageLayout = v.findViewById(R.id.messageLayout);
            mAudioLayout = v.findViewById(R.id.audioLayout);
            mOthersLayout = v.findViewById(R.id.otherLayout);
            mProgressLayout = v.findViewById(R.id.progressLayout);
            mPlayButtonLayout = v.findViewById(R.id.playButtonLayout);
            mCaptionTv = v.findViewById(R.id.captionTV);
            mAudioduration = v.findViewById(R.id.audioDuration);
            mSenderName = v.findViewById(R.id.senderName);
            mMessageTV = v.findViewById(R.id.messageText);
            mChatTimeTv = v.findViewById(R.id.chatTimeTv);
            mPlayButton = v.findViewById(R.id.mediaPlayButton);
            mAudioPlayButton = v.findViewById(R.id.audioPlayButton);
            mAudioPauseButton = v.findViewById(R.id.audioPauseButton);
            mImageContainerView = v.findViewById(R.id.ImageContainerView);
            mMediaProgress = v.findViewById(R.id.progressCircularBar);

            mChatTimeTv.setTextAppearance(R.style.chat_timings_params);

            mLinkPreviewLayout = v.findViewById(R.id.linkPreviewLayout);
            mLinkImageLayout = v.findViewById(R.id.linkImageLayout);
            mLinkCancelLayout = v.findViewById(R.id.linkPreviewCancel);
            mLinkTitle = v.findViewById(R.id.linkTitle);
            mLinkDesc = v.findViewById(R.id.linkDescription);
            mLinkUrl = v.findViewById(R.id.linkUrl);
            mLinkImage = v.findViewById(R.id.linkImage);
            mLinkCancelLayout.setVisibility(GONE);
            mLinkImageLayout.setVisibility(GONE);

            mLinkCancelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLinkPreviewLayout.setVisibility(GONE);
                }
            });

            mLinkPreviewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLinkUrl.getText().toString().trim()));
                    startActivity(browserIntent);
                }
            });

            mViewIncomingMessage = v;
        }
    }


    public class OutgoingMessgaeViewHolder extends MesiboRecycleViewHolder {

        RelativeLayout mRootLayout, mAudioLayout, mOthersLayout, mProgressLayout, mPlayButtonLayout;
        LinearLayout mMessageLayout;
        TextView mCaptionTv, mAudioduration, mMessageTV, mChatTimeTv;
        ImageView mAudioPlayButton, mAudioPauseButton, mImageContainerView, mPlayButton, mOutMsgStatusTickIV;
        ProgressBar mMediaProgress;
        LinearLayout mLinkPreviewLayout, mLinkImageLayout, mLinkCancelLayout;
        ImageView mLinkImage;
        TextView mLinkTitle, mLinkDesc, mLinkUrl;
        boolean mLinkActive = true;


        Boolean mLiked = false;
        View mViewOutgoingMessage;

        public OutgoingMessgaeViewHolder(View v) {
            super(v);

            mRootLayout = v.findViewById(R.id.rootLayout);
            mMessageLayout = v.findViewById(R.id.messageLayout);
            mAudioLayout = v.findViewById(R.id.audioLayout);
            mOthersLayout = v.findViewById(R.id.otherLayout);
            mProgressLayout = v.findViewById(R.id.progressLayout);
            mPlayButtonLayout = v.findViewById(R.id.playButtonLayout);
            mCaptionTv = v.findViewById(R.id.captionTV);
            mAudioduration = v.findViewById(R.id.audioDuration);
            mMessageTV = v.findViewById(R.id.messageText);
            mChatTimeTv = v.findViewById(R.id.chatTimeTv);
            mPlayButton = v.findViewById(R.id.mediaPlayButton);
            mAudioPlayButton = v.findViewById(R.id.audioPlayButton);
            mAudioPauseButton = v.findViewById(R.id.audioPauseButton);
            mImageContainerView = v.findViewById(R.id.ImageContainerView);
            mMediaProgress = v.findViewById(R.id.progressCircularBar);
            mOutMsgStatusTickIV = v.findViewById(R.id.outMsgStatusIV);

            mChatTimeTv.setTextAppearance(R.style.chat_timings_params_outgoing);


            mLinkPreviewLayout = v.findViewById(R.id.linkPreviewLayout);
            mLinkImageLayout = v.findViewById(R.id.linkImageLayout);
            mLinkCancelLayout = v.findViewById(R.id.linkPreviewCancel);
            mLinkTitle = v.findViewById(R.id.linkTitle);
            mLinkDesc = v.findViewById(R.id.linkDescription);
            mLinkUrl = v.findViewById(R.id.linkUrl);
            mLinkImage = v.findViewById(R.id.linkImage);
            mLinkCancelLayout.setVisibility(GONE);
            mLinkImageLayout.setVisibility(GONE);

            mLinkCancelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLinkPreviewLayout.setVisibility(GONE);
                }
            });

            mLinkPreviewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLinkUrl.getText().toString().trim()));
                    startActivity(browserIntent);
                }
            });


            mViewOutgoingMessage = v;

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

        } else if (MesiboRecycleViewHolder.TYPE_OUTGOING == type) {

//            if (mDefaultLayout)
//
//                return null;

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.outgoing_chat_layout, viewGroup, false);
            return new OutgoingMessgaeViewHolder(v);

        } else if (MesiboRecycleViewHolder.TYPE_INCOMING == type) {

//            if (mDefaultLayout)
//
//                return null;

            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.incoming_chat_layout, viewGroup, false);
            return new IncomingMessgaeViewHolder(v);

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
                missedCallViewHolder.missedCallImage.setImageResource(mAudioMissedImage);
            } else {
                missedCallViewHolder.callTypeText.setText("Missed video call at");
                missedCallViewHolder.missedCallImage.setImageResource(mVideoMissedImage);
            }

        } else if (type == MesiboRecycleViewHolder.TYPE_CUSTOM) {

            MissedCallViewHolder customMessage = (MissedCallViewHolder) mesiboRecycleViewHolder;
            customMessage.mCustomMessageLayout.setVisibility(View.VISIBLE);
            customMessage.mMissedCallLayout.setVisibility(View.GONE);
            customMessage.customMessageTV.setText(mesiboMessage.message);

        } else if (type == MesiboRecycleViewHolder.TYPE_INCOMING) {

            mTYPE = MesiboRecycleViewHolder.TYPE_INCOMING;
            IncomingMessgaeViewHolder IncomingView = (IncomingMessgaeViewHolder) mesiboRecycleViewHolder;

            if (messageParams.isDeleted()) {

                String time = getTIME(mesiboMessage.ts);
                IncomingView.mLinkPreviewLayout.setVisibility(GONE);
                IncomingView.mMessageTV.setText(time);
                IncomingView.mMessageTV.setTextColor(Color.LTGRAY);
                IncomingView.mMessageTV.setText(Html.fromHtml("&#216 " + "This message was deleted"
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

            } else {

                String time = getTIME(mesiboMessage.ts);
                IncomingView.mChatTimeTv.setText(time);
                Mesibo.UserProfile respondersProfile = messageParams.profile;

                if (null == messageParams.groupProfile && 0 == messageParams.groupid) {
                    IncomingView.mSenderName.setVisibility(View.GONE);
                } else {
                    String nameM = respondersProfile.name;
                    IncomingView.mSenderName.setText(nameM);
                }


                if (null != mesiboMessage.file  /*|| null != mesiboMessage.location*/) {
                    IncomingView.mLinkPreviewLayout.setVisibility(GONE);
                    IncomingView.mAudioLayout.setVisibility(View.GONE);
                    IncomingView.mProgressLayout.setVisibility(View.VISIBLE);
                    IncomingView.mMessageLayout.setVisibility(View.GONE);

                    mProgressBar = IncomingView.mMediaProgress;

                    if (mesiboMessage.file.isTransferred()) {
                        IncomingView.mProgressLayout.setVisibility(View.GONE);
                    }

                    //Check group message, if yes show senders name
                    if (null != messageParams.groupProfile || 0 < messageParams.groupid) {
                        IncomingView.mSenderName.setVisibility(View.VISIBLE);
                    }

                    //Check for caption , if there make caption visible
                    if (!mesiboMessage.message.trim().isEmpty()) {
                        IncomingView.mCaptionTv.setVisibility(View.VISIBLE);
                        IncomingView.mCaptionTv.setText(mesiboMessage.message);
                    }


                    //Image or Video
                    if (mesiboMessage.file.type == Mesibo.FileInfo.TYPE_IMAGE || mesiboMessage.file.type == Mesibo.FileInfo.TYPE_VIDEO /*|| null != mesiboMessage.location*/) {
                        IncomingView.mAudioLayout.setVisibility(View.GONE);
                        IncomingView.mOthersLayout.setVisibility(View.VISIBLE);
                        File imgFile = new File(mesiboMessage.file.getPath());

                        // set Image
                        IncomingView.mImageContainerView.setImageBitmap(mesiboMessage.file.image);


                        if(mesiboMessage.file.type == Mesibo.FileInfo.TYPE_IMAGE){
                            IncomingView.mImageContainerView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UIManager.launchImageViewer(getActivity(),mesiboMessage.file.getPath());

                                }
                            });
                        }
                        //set play button for Video
                        if (mesiboMessage.file.type == Mesibo.FileInfo.TYPE_VIDEO) {

                            IncomingView.mPlayButtonLayout.setVisibility(View.VISIBLE);
                            IncomingView.mPlayButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    File file = new File(mesiboMessage.file.getPath());

//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                    intent.setDataAndType(Uri.parse(mesiboMessage.file.getUrl()), "video/mp4");
//                                    startActivity(intent);
                                    UIManager.openMedia(getActivity(),mesiboMessage.file.getUrl(),mesiboMessage.file.getPath());

                                }
                            });


                        }else{
                            IncomingView.mPlayButtonLayout.setVisibility(View.GONE);
                        }

                        // location

//                        if (null != mesiboMessage.location) {
//
//
//
//
//                            IncomingView.mImageContainerView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                }
//                            });
//
//
//                        }


                    }


                    //Audio

                    if (mesiboMessage.file.type == Mesibo.FileInfo.TYPE_AUDIO) {


                        IncomingView.mAudioLayout.setVisibility(View.VISIBLE);
                        IncomingView.mOthersLayout.setVisibility(View.GONE);
                        IncomingView.mCaptionTv.setVisibility(View.GONE);
                        IncomingView.mPlayButtonLayout.setVisibility(View.GONE);
                        IncomingView.mMessageLayout.setVisibility(View.GONE);

                        if (mesiboMessage.file.isTransferred()) {

                            IncomingView.mProgressLayout.setVisibility(View.GONE);
                            IncomingView.mAudioPlayButton.setVisibility(View.VISIBLE);

                        } else {

                            IncomingView.mProgressLayout.setVisibility(View.VISIBLE);
                            IncomingView.mAudioPlayButton.setVisibility(View.GONE);
                        }

                        //set up MediaPlayer
                        MediaPlayer mp = new MediaPlayer();
                        try {
                            String path = mesiboMessage.file.getPath();
                            mp.setDataSource(path);
                            mp.prepare();

                            float duration = mp.getDuration();
                            double durationLong = duration / 60000;
                            IncomingView.mAudioduration.setText(String.format("%.2f", durationLong));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        IncomingView.mAudioPlayButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UIManager.openMedia(getActivity(),mesiboMessage.file.getUrl(),mesiboMessage.file.getPath());
//                                IncomingView.mAudioPlayButton.setVisibility(View.GONE);
//                                IncomingView.mAudioPauseButton.setVisibility(View.VISIBLE);
//                                mp.start();

                            }
                        });

                        IncomingView.mAudioPauseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                IncomingView.mAudioPlayButton.setVisibility(View.VISIBLE);
                                IncomingView.mAudioPauseButton.setVisibility(View.GONE);
                                mp.pause();
                            }
                        });


                    }

                    if(mesiboMessage.file.type == Mesibo.FileInfo.TYPE_OTHER){
                        IncomingView.mAudioLayout.setVisibility(View.VISIBLE);
                        IncomingView.mOthersLayout.setVisibility(View.GONE);
                        IncomingView.mCaptionTv.setVisibility(View.GONE);
                        IncomingView.mPlayButtonLayout.setVisibility(View.GONE);
                        IncomingView.mMessageLayout.setVisibility(View.GONE);

                        // set Image
                        IncomingView.mImageContainerView.setImageBitmap(mesiboMessage.file.image);

                        IncomingView.mImageContainerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UIManager.openMedia(getActivity(),mesiboMessage.file.getUrl(),mesiboMessage.file.getPath());
                            }
                        });
                    }

                } else {

                    IncomingView.mLinkPreviewLayout.setVisibility(GONE);
                    IncomingView.mAudioLayout.setVisibility(View.GONE);
                    IncomingView.mOthersLayout.setVisibility(View.GONE);
                    IncomingView.mCaptionTv.setVisibility(View.GONE);
                    IncomingView.mProgressLayout.setVisibility(View.GONE);
                    IncomingView.mPlayButtonLayout.setVisibility(View.GONE);
                    IncomingView.mMessageLayout.setVisibility(View.VISIBLE);

                    if (null != messageParams.groupProfile && 0 < messageParams.groupid) {
                        IncomingView.mSenderName.setVisibility(View.VISIBLE);
                    }


                    IncomingView.mMessageTV.setText(Html.fromHtml(mesiboMessage.message +
                            " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

                    String[] linksInText = AppUtils.extractLinks(mesiboMessage.message);

                    if (linksInText.length >= 1) {
                        // Toast.makeText(ActivityPostCreate.this, "" + linksInText[0], Toast.LENGTH_SHORT).show();


                        if (linksInText[0].contains(".com") && IncomingView.mLinkActive) {

                            SampleAPI.getLinkPreview(linksInText[0], new SampleAPI.ApiResponseHandler() {
                                @Override
                                public void onApiResponse(boolean result, String response) {

                                    if (result) {

                                        try {
                                            JSONObject jObject = new JSONObject(response);


                                            String title = jObject.getString("title");
                                            String description = jObject.getString("description");
                                            String image = jObject.getString("image");
                                            String url = jObject.getString("url");

                                            if (null != title && !title.isEmpty()) {
                                                IncomingView.mLinkActive = true;

                                                IncomingView.mLinkTitle.setText(title);
                                                IncomingView.mLinkDesc.setText(description);
                                                IncomingView.mLinkUrl.setText(url);

                                                if (null != image && !image.isEmpty()) {

                                                    IncomingView.mLinkImageLayout.setVisibility(VISIBLE);
                                                    String mTempFilePath = Mesibo.getFilePath(Mesibo.FileInfo.TYPE_PROFILETHUMBNAIL) +title+ "_image.jpg";

                                                    File imgFile1 = new File(mTempFilePath);
                                                    if (imgFile1.exists()) {
                                                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                                                        IncomingView.mLinkImage.setImageBitmap(myBitmap);
                                                    }else {
                                                        downloadFile(image, IncomingView.mLinkImage, mTempFilePath);
                                                    }

                                                } else {
                                                    IncomingView.mLinkImageLayout.setVisibility(GONE);
                                                }

                                                IncomingView.mLinkPreviewLayout.setVisibility(VISIBLE);


                                            } else {
                                                IncomingView.mLinkActive = false;
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                }

                            });
                        } else {
                            IncomingView.mLinkPreviewLayout.setVisibility(GONE);
                        }

                    }




                }


            }

            IncomingView.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = IncomingView.getAdapterPosition();


                    mMessageTypeIncoming = true;

                    if (mRecyclerPosArrayList.size() > 0) {
                        if (!mRecyclerPosArrayList.contains(pos)) {
                            mRecyclerPosArrayList.add(pos);
                            mMidToDeleteList.add(mMid);
                            mesiboRecycleViewHolderArrayList.add(IncomingView);
                            IncomingView.mRootLayout.setBackgroundColor(AppConfig.Grey_color);
                        } else {
                            mRecyclerPosArrayList.remove(Integer.valueOf(pos));
                            mMidToDeleteList.remove(mMid);
                            mesiboRecycleViewHolderArrayList.remove(IncomingView);
                            IncomingView.mRootLayout.setBackgroundColor(AppConfig.Transparent);

                            if (mRecyclerPosArrayList.size() == 0) {
                                ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onHideInContextUserInterface();
                                mHideRepy = 1;

                            }
                        }

                    }

                    if (mRecyclerPosArrayList.size() > 1) {

                        mHideRepy = 0;
                        ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onShowInContextUserInterface();
                        ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onContextUserInterfaceCount(mRecyclerPosArrayList.size());
                    }


                }
            });

            IncomingView.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    mMessageSelected = true;

                    int pos = IncomingView.getAdapterPosition();


                    mMid = mesiboMessage.mid;


                    if (!mRecyclerPosArrayList.contains(pos)) {
                        mRecyclerPosArrayList.add(pos);
                        IncomingView.mRootLayout.setBackgroundColor(AppConfig.Grey_color);
                        mMidToDeleteList.add(mMid);
                        mesiboRecycleViewHolderArrayList.add(IncomingView);
                    } else {
                        mRecyclerPosArrayList.remove(Integer.valueOf(pos));
                        mMidToDeleteList.remove(mMid);
                        mesiboRecycleViewHolderArrayList.remove(IncomingView);
                        IncomingView.mRootLayout.setBackgroundColor(AppConfig.Transparent);
                        if (mRecyclerPosArrayList.size() == 0) {
                            ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onHideInContextUserInterface();
                            mHideRepy = 1;
                        }
                    }

                    if (mRecyclerPosArrayList.size() > 1) {
                        ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onContextUserInterfaceCount(mRecyclerPosArrayList.size());
                        mHideRepy = 0;
                    }


//                        mMessageSelected = !mMessageSelected;
//
//                        if(mMessageSelected){
                    IncomingView.mRootLayout.setBackgroundColor(AppConfig.Grey_color);
                    mMesiboMessage = mesiboMessage;
                    mMesiborecyclerView = IncomingView;
                    mMessageTypeIncoming = true;
                    // mMid = mesiboMessage.mid;
                    //mMidToDeleteList.add(mMid);
                    //mesiboRecycleViewHolderArrayList.add(IncomingView);


                    ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onShowInContextUserInterface();
//                        }else{
//                            IncomingView.mRootLayout.setBackgroundColor(AppConfig.Transparent);
//                            ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onHideInContextUserInterface();
//                        }


                    return true;
                }
            });


            IncomingView.mSenderName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // setup the alert builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    String[] actions = {"Message", "Video Call", "Audio Call"};
                    builder.setItems(actions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // Message

                                    Intent i = new Intent(getActivity(), MessagingActivityNew.class);
                                    i.putExtra("peer", messageParams.peer);
                                    startActivity(i);
                                    dialog.dismiss();

                                    break;

                                case 1: // Video Call
                                    Mesibo.MessageParams mParameter = new Mesibo.MessageParams(messageParams.peer, 0, Mesibo.FLAG_DEFAULT, 0);
                                    MesiboCall.getInstance().call(getActivity(), Mesibo.random(), mParameter.profile, true);
                                    dialog.dismiss();
                                    break;
                                case 2: // Audio Call
                                    Mesibo.MessageParams mParameter1 = new Mesibo.MessageParams(messageParams.peer, 0, Mesibo.FLAG_DEFAULT, 0);
                                    MesiboCall.getInstance().call(getActivity(), Mesibo.random(), mParameter1.profile, false);
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


        } else if (type == MesiboRecycleViewHolder.TYPE_OUTGOING) {

            mTYPE = MesiboRecycleViewHolder.TYPE_OUTGOING;


            OutgoingMessgaeViewHolder OutView = (OutgoingMessgaeViewHolder) mesiboRecycleViewHolder;
            String time = getTIME(mesiboMessage.ts);

            if (messageParams.isDeleted()) {
                OutView.mLinkPreviewLayout.setVisibility(GONE);

                OutView.mMessageTV.setText(time);
                OutView.mMessageTV.setTextColor(Color.LTGRAY);
                OutView.mMessageTV.setText(Html.fromHtml("&#216 " + "You deleted this message."
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                        "&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
                OutView.mOutMsgStatusTickIV.setVisibility(View.GONE);


            } else {
                String ts = getTIME(mesiboMessage.ts);
                OutView.mChatTimeTv.setText(ts);
                Mesibo.UserProfile respondersProfile = messageParams.profile;


                if (null != mesiboMessage.file  /*|| null != mesiboMessage.location*/) {

                    OutView.mLinkPreviewLayout.setVisibility(GONE);
                    OutView.mProgressLayout.setVisibility(View.VISIBLE);
                    OutView.mMessageLayout.setVisibility(View.GONE);


                    mProgressBar = OutView.mMediaProgress;

                    if (mesiboMessage.file.isTransferred()) {
                        OutView.mProgressLayout.setVisibility(View.GONE);
                    }

                    //Check for caption , if there make caption visible
                    if (!mesiboMessage.message.trim().isEmpty()) {
                        OutView.mCaptionTv.setVisibility(View.VISIBLE);
                        OutView.mCaptionTv.setText(mesiboMessage.message);
                    }


                    //Image or Video
                    if (mesiboMessage.file.type == Mesibo.FileInfo.TYPE_IMAGE || mesiboMessage.file.type == Mesibo.FileInfo.TYPE_VIDEO /*|| null != mesiboMessage.location*/) {
                        OutView.mAudioLayout.setVisibility(View.GONE);
                        OutView.mOthersLayout.setVisibility(View.VISIBLE);
                        File imgFile = new File(mesiboMessage.file.getPath());

                        // set Image
                        OutView.mImageContainerView.setImageBitmap(mesiboMessage.file.image);
//                        if (imgFile.exists()) {
//                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                            OutView.mImageContainerView.setImageBitmap(myBitmap);
//                        }


                        if(mesiboMessage.file.type == Mesibo.FileInfo.TYPE_IMAGE){
                            OutView.mImageContainerView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UIManager.launchImageViewer(getActivity(),mesiboMessage.file.getPath());

                                }
                            });
                        }
                        //set play button for Video

                        if (mesiboMessage.file.type == Mesibo.FileInfo.TYPE_VIDEO) {

                            OutView.mPlayButtonLayout.setVisibility(View.VISIBLE);
                            OutView.mPlayButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    File file = new File(mesiboMessage.file.getPath());

//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                    intent.setDataAndType(Uri.parse(mesiboMessage.file.getUrl()), "video/mp4");
//                                    startActivity(intent);


//                                    if(Mesibo.fileExists(mesiboMessage.file.getPath())){
//                                        MimeTypeMap myMime = MimeTypeMap.getSingleton();
//                                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
//                                        String mimeType = myMime.getMimeTypeFromExtension(fileExt(mesiboMessage.file.getUrl()).substring(1));
//                                        newIntent.setDataAndType(Uri.fromFile(file),mimeType);
//                                        newIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                                        try {
//                                            getActivity().startActivity(newIntent);
//                                        } catch (ActivityNotFoundException e) {
//                                            Toast.makeText(getActivity(), "No handler for this type of file.", Toast.LENGTH_LONG).show();
//                                        }
//
//                                    }else {

                                       UIManager.openMedia(getActivity(),mesiboMessage.file.getUrl(),mesiboMessage.file.getPath());

                                   // }

                                }
                            });




                        }else{
                            OutView.mPlayButtonLayout.setVisibility(View.GONE);
                        }

                        // location

//                        if (null != mesiboMessage.location) {
//
//
//
//
//                            IncomingView.mImageContainerView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//
//                                }
//                            });
//
//
//                        }


                    }


                    //Audio

                    if (mesiboMessage.file.type == Mesibo.FileInfo.TYPE_AUDIO) {

                        OutView.mAudioLayout.setVisibility(View.VISIBLE);
                        OutView.mOthersLayout.setVisibility(View.GONE);
                        OutView.mCaptionTv.setVisibility(View.GONE);
                        OutView.mPlayButtonLayout.setVisibility(View.GONE);
                        OutView.mMessageLayout.setVisibility(View.GONE);


                        if (mesiboMessage.file.isTransferred()) {

                            OutView.mProgressLayout.setVisibility(View.GONE);
                            OutView.mAudioPlayButton.setVisibility(View.VISIBLE);

                        } else {

                            OutView.mProgressLayout.setVisibility(View.VISIBLE);
                            OutView.mAudioPlayButton.setVisibility(View.GONE);
                        }

                        //set up MediaPlayer
                        MediaPlayer mp = new MediaPlayer();
                        try {
                            String path = mesiboMessage.file.getPath();
                            mp.setDataSource(path);
                            mp.prepare();

                            float duration = mp.getDuration();
                            double durationLong = duration / 60000;
                            OutView.mAudioduration.setText(String.format("%.2f", durationLong));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        OutView.mAudioPlayButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                OutView.mAudioPlayButton.setVisibility(View.GONE);
//                                OutView.mAudioPauseButton.setVisibility(View.VISIBLE);
//                                mp.start();

                                UIManager.openMedia(getActivity(),mesiboMessage.file.getUrl(),mesiboMessage.file.getPath());

                            }
                        });

                        OutView.mAudioPauseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                OutView.mAudioPlayButton.setVisibility(View.VISIBLE);
                                OutView.mAudioPauseButton.setVisibility(View.GONE);
                                mp.pause();
                            }
                        });


                    }


                    if(mesiboMessage.file.type == Mesibo.FileInfo.TYPE_OTHER){
                        OutView.mAudioLayout.setVisibility(View.GONE);
                        OutView.mOthersLayout.setVisibility(View.VISIBLE);
                        File imgFile = new File(mesiboMessage.file.getPath());

                        // set Image
                        OutView.mImageContainerView.setImageBitmap(mesiboMessage.file.image);

                        OutView.mImageContainerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UIManager.openMedia(getActivity(),mesiboMessage.file.getUrl(),mesiboMessage.file.getPath());
                            }
                        });
                    }

                } else {// message

                    OutView.mLinkPreviewLayout.setVisibility(GONE);
                    OutView.mAudioLayout.setVisibility(View.GONE);
                    OutView.mOthersLayout.setVisibility(View.GONE);
                    OutView.mCaptionTv.setVisibility(View.GONE);
                    OutView.mProgressLayout.setVisibility(View.GONE);
                    OutView.mPlayButtonLayout.setVisibility(View.GONE);
                    OutView.mMessageLayout.setVisibility(View.VISIBLE);


                    OutView.mMessageTV.setText(Html.fromHtml(mesiboMessage.message
                            + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;" +
                            "&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));


                    String[] linksInText = AppUtils.extractLinks(mesiboMessage.message);

                    if (linksInText.length >= 1) {
                        // Toast.makeText(ActivityPostCreate.this, "" + linksInText[0], Toast.LENGTH_SHORT).show();


                        if (linksInText[0].contains(".com") && OutView.mLinkActive) {

                            SampleAPI.getLinkPreview(linksInText[0], new SampleAPI.ApiResponseHandler() {
                                @Override
                                public void onApiResponse(boolean result, String response) {

                                    if (result) {

                                        try {
                                            JSONObject jObject = new JSONObject(response);
                                            String title = jObject.getString("title");
                                            String description = jObject.getString("description");
                                            String image = jObject.getString("image");
                                            String url = jObject.getString("url");
                                            if (null != title && !title.isEmpty()) {
                                                OutView.mLinkActive = true;

                                                OutView.mLinkTitle.setText(title);
                                                OutView.mLinkDesc.setText(description);
                                                OutView.mLinkUrl.setText(url);

                                                if (null != image && !image.isEmpty()) {

                                                    OutView.mLinkImageLayout.setVisibility(VISIBLE);
                                                    String mTempFilePath = Mesibo.getFilePath(Mesibo.FileInfo.TYPE_PROFILETHUMBNAIL) +title+ "_image.jpg";

                                                    File imgFile1 = new File(mTempFilePath);
                                                    if (imgFile1.exists()) {
                                                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
                                                        OutView.mLinkImage.setImageBitmap(myBitmap);
                                                    }else {
                                                        downloadFile(image, OutView.mLinkImage, mTempFilePath);
                                                    }

                                                } else {
                                                    OutView.mLinkImageLayout.setVisibility(GONE);
                                                }



                                                OutView.mLinkPreviewLayout.setVisibility(VISIBLE);


                                            } else {
                                                OutView.mLinkActive = false;
                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                    }

                                }

                            });
                        } else {
                            OutView.mLinkPreviewLayout.setVisibility(GONE);
                        }

                    }


                }

            }

            if (mesiboMessage.status == Mesibo.MSGSTATUS_OUTBOX) {
                OutView.mOutMsgStatusTickIV.setImageResource(images[0]);


            } else if (mesiboMessage.status == Mesibo.MSGSTATUS_SENT) {
                OutView.mOutMsgStatusTickIV.setImageResource(images[1]);

            } else if (mesiboMessage.status == Mesibo.MSGSTATUS_DELIVERED) {

                OutView.mOutMsgStatusTickIV.setImageResource(images[2]);

            } else if (mesiboMessage.status == Mesibo.MSGSTATUS_READ) {
                OutView.mOutMsgStatusTickIV.setImageResource(images[3]);

            } else if (mesiboMessage.status == Mesibo.MSGSTATUS_BLOCKED) {




            }


            OutView.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = OutView.getAdapterPosition();


                    mMessageTypeIncoming = true;

                    if (mRecyclerPosArrayList.size() > 0) {
                        if (!mRecyclerPosArrayList.contains(pos)) {
                            mRecyclerPosArrayList.add(pos);
                            mMidToDeleteList.add(mMid);
                            mesiboRecycleViewHolderArrayList.add(OutView);
                            OutView.mRootLayout.setBackgroundColor(AppConfig.Grey_color);
                        } else {
                            mRecyclerPosArrayList.remove(Integer.valueOf(pos));
                            mMidToDeleteList.remove(mMid);
                            mesiboRecycleViewHolderArrayList.remove(OutView);
                            OutView.mRootLayout.setBackgroundColor(AppConfig.Transparent);

                            if (mRecyclerPosArrayList.size() == 0) {
                                ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onHideInContextUserInterface();
                                mHideRepy = 1;

                            }
                        }

                    }

                    if (mRecyclerPosArrayList.size() > 1) {

                        mHideRepy = 0;
                        ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onShowInContextUserInterface();
                        ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onContextUserInterfaceCount(mRecyclerPosArrayList.size());
                    }


                }
            });

            OutView.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    mMessageSelected = true;

                    int pos = OutView.getAdapterPosition();


                    mMid = mesiboMessage.mid;


                    if (!mRecyclerPosArrayList.contains(pos)) {
                        mRecyclerPosArrayList.add(pos);
                        OutView.mRootLayout.setBackgroundColor(AppConfig.Grey_color);
                        mMidToDeleteList.add(mMid);
                        mesiboRecycleViewHolderArrayList.add(OutView);
                    } else {
                        mRecyclerPosArrayList.remove(Integer.valueOf(pos));
                        mMidToDeleteList.remove(mMid);
                        mesiboRecycleViewHolderArrayList.remove(OutView);
                        OutView.mRootLayout.setBackgroundColor(AppConfig.Transparent);
                        if (mRecyclerPosArrayList.size() == 0) {
                            ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onHideInContextUserInterface();
                            mHideRepy = 1;
                        }
                    }

                    if (mRecyclerPosArrayList.size() > 1) {
                        ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onContextUserInterfaceCount(mRecyclerPosArrayList.size());
                        mHideRepy = 0;
                    }


//                        mMessageSelected = !mMessageSelected;
//
//                        if(mMessageSelected){
                    OutView.mRootLayout.setBackgroundColor(AppConfig.Grey_color);
                    mMesiboMessage = mesiboMessage;
                    mMesiborecyclerView = OutView;
                    mMessageTypeIncoming = true;
                     mMid = mesiboMessage.mid;
                    mMidToDeleteList.add(mMid);
                    //mesiboRecycleViewHolderArrayList.add(IncomingView);


                    ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onShowInContextUserInterface();
//                        }else{
//                            IncomingView.mRootLayout.setBackgroundColor(AppConfig.Transparent);
//                            ((MessagingActivityNew) Objects.requireNonNull(getActivity())).Mesibo_onHideInContextUserInterface();
//                        }


                    return true;
                }
            });


        }

    }


    @Override
    public void Mesibo_onViewRecycled(MesiboRecycleViewHolder mesiboRecycleViewHolder) {

    }


    @Override
    public void Mesibo_oUpdateViewHolder(MesiboRecycleViewHolder mesiboRecycleViewHolder, Mesibo.MesiboMessage mesiboMessage) {

    }


    @Override
    public void Mesibo_onFile(Mesibo.MessageParams messageParams, Mesibo.FileInfo fileInfo) {
        super.Mesibo_onFile(messageParams, fileInfo);


    }


    @Override
    public boolean Mesibo_onFileTransferProgress(Mesibo.FileInfo file) {
        if (100 == file.getProgress()) {

            if (null != mProgressBar) {
                mProgressBar.setVisibility(View.GONE);
            }
        } else {

            if (null != mProgressBar) {
                mProgressBar.setProgress(file.getProgress());
            }
        }
        Log.d("*************Progress", String.valueOf(file.getProgress()));

        return true;
    }


    public int Mesibo_onGetEnabledActionItems() {


        return mHideRepy;
    }

    public void setPicture(ImageView view) {


    }

    public void onActionItemClicked(int ItemId) {

        if (ItemId == AppConfig.MESSAGECONTEXTACTION_COPY) {

            ClipboardManager var17 = (ClipboardManager) this.myActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData var18 = ClipData.newPlainText("Copy", mMesiboMessage.message);
            var17.setPrimaryClip(var18);

            Toast.makeText(mActivity, "Copied", Toast.LENGTH_SHORT).show();

        } else if (ItemId == AppConfig.MESSAGECONTEXTACTION_REPLY) {
            replyEnabled = true;

            Toast.makeText(mActivity, "Reply", Toast.LENGTH_SHORT).show();
        } else if (ItemId == AppConfig.MESSAGECONTEXTACTION_DELETE) {

            Dialog dialog = new Dialog(getActivity());
            dialog.setCancelable(true);


            dialog.setContentView(R.layout.delete_message_row_item);

            TextView deleteForME = dialog.findViewById(R.id.deleteForMe);
            TextView cancel = dialog.findViewById(R.id.cancelDelete);
            TextView deleteForEveryOne = dialog.findViewById(R.id.deleteForEveryone);

            if (mMessageTypeIncoming) {
                deleteForEveryOne.setVisibility(View.GONE);
            }


            deleteForME.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();
                    deleteMessage(Mesibo.MESIBO_DELETE_DEFAULT);


                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Do something


                    dialog.dismiss();


                }
            });


            deleteForEveryOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    deleteMessage(Mesibo.MESIBO_DELETE_RECALL);


                }
            });

            dialog.show();


        } else if (ItemId == AppConfig.MESSAGECONTEXTACTION_FORWARD) {

            //MesiboUI.launchForwardActivity(Objects.requireNonNull(getActivity()), mMesiboMessage.message, true);


        } else if (ItemId == AppConfig.MESSAGECONTEXTACTION_SHARE) {
            try {


                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                //i.putExtra(Intent.EXTRA_SUBJECT, "WyngIt");
                String sAux = mMesiboMessage.message;
                //sAux = sAux + "https://play.google.com/store/apps/details?id=the.package.id \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                getActivity().startActivity(Intent.createChooser(i, "Select One"));
            } catch (Exception e) {
                //e.toString();
            }

        }


    }


    public void deleteMessage(int Type) {

        if (null != mMesiborecyclerView) {
            try {


                for (int i = 0; i < mesiboRecycleViewHolderArrayList.size(); i++) {

                    mesiboRecycleViewHolderArrayList.get(i).delete(Type);
                    mMesiborecyclerView.delete(1);
                }

                for (int j = 0; j < mMidToDeleteList.size(); j++) {
                    Mesibo.deleteMessage(mMidToDeleteList.get(j), Type);
                }

                mesiboRecycleViewHolderArrayList.clear();
                mRecyclerPosArrayList.clear();
                mMidToDeleteList.clear();

                mMesiborecyclerView.refresh();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();

            }

        }


    }


    public static boolean saveBitmpToFilePath(Bitmap bmp, String filePath) {
        File file = new File(filePath);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (null != bmp) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, fOut);

            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams messageParams, byte[] bytes) {


        return super.Mesibo_onMessage(messageParams, bytes);
    }


    @Override
    public void Mesibo_onMessageStatus(Mesibo.MessageParams messageParams) {
        super.Mesibo_onMessageStatus(messageParams);
    }


    private String getTIME(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("hh:mm a", cal).toString();
    }





    public boolean downloadFile(String url, final ImageView UserImage, String filePath) {
        Mesibo.Http http = new Mesibo.Http();
        http.url = url;
        http.downloadFile = filePath;
        http.resume = true;
        http.maxRetries = 10;
        //http.other = myObject;
        //file.setFileTransferContext(http);
        http.onMainThread = true;
        http.listener = new Mesibo.HttpListener() {
            @Override
            public boolean Mesibo_onHttpProgress(Mesibo.Http http, int state, int percent) {
                if (100 == percent && Mesibo.Http.STATE_DOWNLOAD == state) {
                    // download complete
                    File imgFile = new File(filePath);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        if(saveBitmpToFilePath(myBitmap,filePath)){
                        }
                        UserImage.setImageBitmap(myBitmap);
                    }

                    return true;
                }
                return true; // return false to cancel
            }
        };

        if (http.execute()) {
        }

        return true;
    }

}