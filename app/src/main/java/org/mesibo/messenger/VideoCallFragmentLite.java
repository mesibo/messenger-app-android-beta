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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.MesiboVideoCallFragment;
import com.mesibo.messaging.MessagingActivity;

import org.webrtc.RendererCommon.ScalingType;

/**
 * Fragment for call control.
 */
public class VideoCallFragmentLite extends MesiboVideoCallFragment implements Mesibo.CallListener, View.OnTouchListener {

    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private ImageButton toggleCameraButton;
    private ImageButton toggleMuteButton;
    private ImageButton toggleSpeakerButton;
    ImageView imageView, mDeclineViewButton, mAcceptViewButton, mDefaultMessageButton;
    private OnCallEvents callEvents;
    private ScalingType scalingType;
    Mesibo.UserProfile mProfile;
    private View mIncomingView, mInProgressView;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View controlView = inflater.inflate(R.layout.fragment_videocall_new_lite, container, false);

        TextView contactView = controlView.findViewById(R.id.call_name);
        imageView = controlView.findViewById(R.id.photo_image);
        ImageButton disconnectButton = controlView.findViewById(R.id.button_call_disconnect);
        cameraSwitchButton = controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton = controlView.findViewById(R.id.button_call_scaling_mode);
        toggleSpeakerButton = controlView.findViewById(R.id.button_call_toggle_speaker);
        toggleCameraButton = controlView.findViewById(R.id.button_call_toggle_camera);
        toggleMuteButton = controlView.findViewById(R.id.button_call_toggle_mic);
        mIncomingView = controlView.findViewById(R.id.incoming_call_container);
        mInProgressView = controlView.findViewById(R.id.outgoing_call_container);
        mDeclineViewButton = controlView.findViewById(R.id.declineButton);
        mAcceptViewButton = controlView.findViewById(R.id.accept_button);
        mDefaultMessageButton = controlView.findViewById(R.id.custom_message_button);

        // Add buttons click events.
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hangup();


            }
        });

        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchCamera();
            }
        });

        videoScalingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
                    videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_white_48dp);
                    scalingType = ScalingType.SCALE_ASPECT_FIT;
                } else {
                    videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_exit_white_48dp);
                    scalingType = ScalingType.SCALE_ASPECT_FILL;
                }
                ///callEvents.onVideoScalingSwitch(scalingType);
                scaleVideo(true);
            }
        });
        scalingType = ScalingType.SCALE_ASPECT_FILL;

        toggleSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSpeaker();
                boolean enabled = callEvents.onToggleSpeaker();
                toggleSpeakerButton.setAlpha(enabled ? 1.0f : 0.3f);
                callEvents.onToggleSpeaker();


            }
        });

        toggleMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMic();
                boolean enabled = callEvents.onToggleMic();
                toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
                callEvents.onToggleMic();

            }
        });

        toggleCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCamera();
                boolean enabled = callEvents.onToggleCamera();
                //setButton(toggleCameraButton, enabled);
                toggleCameraButton.setAlpha(enabled ? 1.0f : 0.3f);
                callEvents.onToggleCamera();


            }
        });

        mDeclineViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangup();
            }
        });

        mAcceptViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer(true);
                setDisplayMode();
            }
        });

        mDefaultMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                String[] DefaultTextMessages = {
                        "Can't talk now. What's up?",
                        "I'll call you right back.",
                        "I'll call you later.",
                        "Can't talk now. Call me later.",
                        "Custom message..."};
                builder.setItems(DefaultTextMessages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String peer = mProfile.address;

                        switch (which) {
                            case 0: //
                                sendCustomeMessage(peer, "Can't talk now. What's up?");
                                break;
                            case 1: //
                                sendCustomeMessage(peer, "I'll call you right back.");
                                break;
                            case 2: //
                                sendCustomeMessage(peer, "I'll call you later.");
                                break;
                            case 3: //
                                sendCustomeMessage(peer, "Can't talk now. Call me later.");
                                break;
                            case 4: //Custom message
                                Intent i = new Intent(getActivity(), MessagingActivity.class);
                                i.putExtra("peer", peer);
                                startActivity(i);
                                hangup();
                                break;
                        }
                    }
                });

                AlertDialog dialog_cust = builder.create();
                dialog_cust.show();
            }
        });


        contactView.setText(mProfile.name);
        setUserPicture();
        Chronometer statusView = (Chronometer) controlView.findViewById(R.id.call_status);
        setStatusView(statusView);
        setDisplayMode();

        return controlView;
    }


    private void setDisplayMode() {

        boolean incoming = (isIncoming() && !isAnswered());
        mIncomingView.setVisibility(incoming ? View.VISIBLE : View.GONE);
        mInProgressView.setVisibility(incoming ? View.GONE : View.VISIBLE);

    }


    public void setProfile(Mesibo.UserProfile profile) {

        mProfile = profile;

    }

    void setUserPicture() {
        String filePath = Mesibo.getUserProfilePicturePath(mProfile, Mesibo.FileInfo.TYPE_AUTO);

        Bitmap b;
        if (Mesibo.fileExists(filePath)) {
            b = BitmapFactory.decodeFile(filePath);
            if (null != b) {
                imageView.setImageDrawable(MesiboUtils.getRoundImageDrawable(b));
            }
        } else {
            //TBD, getActivity.getresource crashes sometime if activity is closing
            imageView.setImageDrawable(MesiboUtils.getRoundImageDrawable(BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(), R.drawable.default_user_image)));
        }
    }


    private void sendCustomeMessage(String peer, String message) {

        Mesibo.MessageParams messageParams = new Mesibo.MessageParams();
        messageParams.setPeer(peer);
        Mesibo.sendMessage(messageParams, Mesibo.random(), message);
        hangup();

    }


    @Override
    public void onStart() {
        super.onStart();

        boolean captureSliderEnabled = false;
        Bundle args = getArguments();
        if (args != null) {
        }
        boolean videoCallEnabled = true;
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Mesibo.addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Mesibo.removeListener(this);
    }

    // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

    @Override
    public boolean Mesibo_onCall(long peerid, long callid, Mesibo.UserProfile userProfile, int i) {
        return false;
    }

    @Override
    public boolean Mesibo_onCallStatus(long peerid, long callid, int status, int flags, String desc) {

        return false;
    }

    @Override
    public void Mesibo_onCallServer(int type, String url, String username, String credential) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
