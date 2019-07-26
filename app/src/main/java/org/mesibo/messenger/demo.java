//package org.mesibo.messenger;
//
///*
// *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
// *
// *  Use of this source code is governed by a BSD-style license
// *  that can be found in the LICENSE file in the root of the source
// *  tree. An additional intellectual property rights grant can be found
// *  in the file PATENTS.  All contributing project authors may
// *  be found in the AUTHORS file in the root of the source tree.
// */
//
//
//
//import android.app.Activity;
//import android.app.Fragment;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Chronometer;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import com.mesibo.api.Mesibo;
//
//import org.webrtc.RendererCommon.ScalingType;
//
//import static com.mesibo.api.Mesibo.CALLSTATUS_ANSWER;
//
///**
// * Fragment for call control.
// */
//public class demo extends Fragment implements Mesibo.CallListener {
//    private TextView contactView;
//    private ImageButton cameraSwitchButton;
//    private ImageButton videoScalingButton;
//    private ImageButton toggleCameraButton;
//    private ImageButton toggleMuteButton;
//    private ImageButton toggleSpeakerButton;
//    private ImageButton acceptButton, acceptAudioButton;
//    private ImageButton declineButton;
//    private TextView captureFormatText;
//    private SeekBar captureFormatSlider;
//    private OnCallEvents callEvents;
//    private ScalingType scalingType;
//    private boolean videoCallEnabled = true;
//    private View mIncomingView, mInProgressView, mIncomingAudioAcceptLayout;
//
//    private CallManager.CallContext mCall = CallManager.getInstance().getCallContext();
//    private MesiboCallConfig mCallConf = CallManager.getConfig();
//
//    /**
//     * Call control interface for container activity.
//     */
//    public interface OnCallEvents {
//        void onCallAnswered(boolean video);
//        void onCallHangUp();
//        void onCameraSwitch();
//        void onVideoScalingSwitch(ScalingType scalingType);
//        void onCaptureFormatChange(int width, int height, int framerate);
//        boolean onToggleSpeaker();
//        boolean onToggleMic();
//        boolean onToggleCamera();
//    }
//
//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View controlView = inflater.inflate(R.layout.fragment_videocall_new, container, false);
//
//        // Create UI controls.
//        contactView = controlView.findViewById(R.id.call_name);
//        ImageButton disconnectButton = controlView.findViewById(R.id.button_call_disconnect);
//        cameraSwitchButton = controlView.findViewById(R.id.button_call_switch_camera);
//        videoScalingButton = controlView.findViewById(R.id.button_call_scaling_mode);
//        toggleSpeakerButton = controlView.findViewById(R.id.button_call_toggle_speaker);
//        toggleCameraButton = controlView.findViewById(R.id.button_call_toggle_camera);
//        toggleMuteButton = controlView.findViewById(R.id.button_call_toggle_mic);
//        captureFormatText = controlView.findViewById(R.id.capture_format_text_call);
//        captureFormatSlider = controlView.findViewById(R.id.capture_format_slider_call);
//
//        acceptButton = controlView.findViewById(R.id.incoming_call_connect);
//        acceptAudioButton = controlView.findViewById(R.id.incoming_audio_call_connect);
//        declineButton = controlView.findViewById(R.id.incoming_call_disconnect);
//
//        mIncomingView = controlView.findViewById(R.id.incoming_call_container);
//        mInProgressView = controlView.findViewById(R.id.outgoing_call_container);
//        mIncomingAudioAcceptLayout = controlView.findViewById(R.id.incoming_audio_accept_container);
//
//
//        // Add buttons click events.
//        disconnectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                callEvents.onCallHangUp();
//            }
//        });
//
//        declineButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                callEvents.onCallHangUp();
//            }
//        });
//
//        acceptButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                callEvents.onCallAnswered(true);
//                setDisplayMode();
//            }
//        });
//
//        acceptAudioButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setButton(toggleCameraButton, true);
//                callEvents.onCallAnswered(false);
//                setDisplayMode();
//            }
//        });
//
//
//
//        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                callEvents.onCameraSwitch();
//            }
//        });
//
//        videoScalingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
//                    videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_white_48dp);
//                    scalingType = ScalingType.SCALE_ASPECT_FIT;
//                } else {
//                    videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_exit_white_48dp);
//                    scalingType = ScalingType.SCALE_ASPECT_FILL;
//                }
//                callEvents.onVideoScalingSwitch(scalingType);
//            }
//        });
//        scalingType = ScalingType.SCALE_ASPECT_FILL;
//
//        toggleSpeakerButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean enabled = callEvents.onToggleSpeaker();
//                //toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
//                setButton(toggleSpeakerButton, enabled);
//                //CallManager.getInstance().mute(true, false, enabled);
//            }
//        });
//
//        toggleMuteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean enabled = callEvents.onToggleMic();
//                //toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
//                setButton(toggleMuteButton, enabled);
//                //CallManager.getInstance().mute(true, false, enabled);
//            }
//        });
//
//        toggleCameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean enabled = callEvents.onToggleCamera();
//                setButton(toggleCameraButton, enabled);
//                //CallManager.getInstance().mute(false, true, enabled);
//            }
//        });
//
//
//
//
//        TextView nameView = (TextView)controlView.findViewById(R.id.call_name);
//        //TextView addrView = (TextView)controlView.findViewById(R.id.call_address);
//        ImageView imageView = controlView.findViewById(R.id.photo_image);
//
//        CallManager.CallUserInterface ui = mCall.ui.get();
//        ui.statusView = (Chronometer) controlView.findViewById(R.id.call_status);
//
//        CallManager.getInstance().setUserDetails(nameView, imageView);
//        CallManager.getInstance().Mesibo_onCallStatus(0, 0, mCall.status, 0, null);
//
//        setDisplayMode();
//
//        return controlView;
//    }
//
//
//
//    private void setDisplayMode() {
//        boolean incoming = (mCall.incoming && !mCall.answered);
//
//        mIncomingView.setVisibility(incoming?View.VISIBLE:View.GONE);
//        mInProgressView.setVisibility(incoming?View.GONE:View.VISIBLE);
//
//        if(incoming) {
//            mIncomingAudioAcceptLayout.setVisibility(mCallConf.enableAudioOnlyOptionInVideoCall?View.VISIBLE:View.INVISIBLE);
//        }
//    }
//
//    private void setButton(ImageButton v, boolean enable) {
//        v.setAlpha((float)(enable?mCallConf.buttonAlphaOn:mCallConf.buttonAlphaOff)/255.0f);
//        return;
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        boolean captureSliderEnabled = false;
//        Bundle args = getArguments();
//        if (args != null) {
//            contactView.setText(mCall.profile.name);
//            videoCallEnabled = mCallConf.videoCallEnabled;
//            captureSliderEnabled = videoCallEnabled && mCallConf.captureQualitySlider;
//        }
//        if (!videoCallEnabled) {
//            cameraSwitchButton.setVisibility(View.INVISIBLE);
//        }
//        if (captureSliderEnabled) {
//            captureFormatSlider.setOnSeekBarChangeListener(
//                    new CaptureQualityController(captureFormatText, callEvents));
//        } else {
//            captureFormatText.setVisibility(View.GONE);
//            captureFormatSlider.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        setButton(toggleMuteButton, mCall.audioMute);
//        setButton(toggleCameraButton, mCall.videoMute);
//        Mesibo.addListener(this);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Mesibo.removeListener(this);
//    }
//
//    // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
//    @SuppressWarnings("deprecation")
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        callEvents = (OnCallEvents) activity;
//    }
//
//    @Override
//    public boolean Mesibo_onCall(long peerid, long callid, Mesibo.UserProfile userProfile, int i) {
//        return false;
//    }
//
//    @Override
//    public boolean Mesibo_onCallStatus(long peerid, long callid, int status, int flags, String desc) {
//        //Log.d(TAG, "Mesibo_onCallStatus: status: " + status + " flags: " + flags);
//
//        if(null == mCall)
//            return true;
//
//        boolean video = ((flags&Mesibo.CALLFLAG_VIDEO) > 0);
//        if(CALLSTATUS_ANSWER == status && !video) {
//            setButton(toggleCameraButton, true);
//        }
//        return false;
//    }
//
//    @Override
//    public void Mesibo_onCallServer(int type, String url, String username, String credential) {
//
//    }
//}