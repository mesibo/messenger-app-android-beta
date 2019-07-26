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



import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.MesiboVideoCallFragment;
import com.mesibo.messaging.MessagingActivity;

import org.webrtc.RendererCommon.ScalingType;

/**
 * Fragment for call control.
 */
public class VideoCallFragment extends MesiboVideoCallFragment implements Mesibo.CallListener, View.OnTouchListener {

    private LinearLayout mUpArrowDecline, mUpArrowAccept, mUpArrowMessage;
    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private ImageButton toggleCameraButton;
    private ImageButton toggleMuteButton;
    private ImageButton toggleSpeakerButton;
    ImageView imageView;
    private  MesiboVideoCallFragment.OnCallEvents callEvents;
    private TextView mSwipeTextDecline;
    private TextView mSwipeTextAccept;
    private TextView mSwipeTextMessage;
    Animation shake;
     private ScalingType scalingType;
    boolean firstTouch = false;
    float dX, dY;
    float m_screen_width, m_screen_height;
    float m_VcentreX, m_VcentreY, m_ViewStartingY;
    Mesibo.UserProfile mProfile;
    private View mIncomingView, mInProgressView, mIncomingAudioAcceptLayout;




    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View controlView = inflater.inflate(R.layout.fragment_videocall_new, container, false);

        // Create UI controls.

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
        RelativeLayout mDeclineViewButton = controlView.findViewById(R.id.decline_view_layout);
        RelativeLayout mAcceptViewButton = controlView.findViewById(R.id.accept_view_layout);
        RelativeLayout mDefaultMessageButton = controlView.findViewById(R.id.custom_message_view_layout);
        mSwipeTextDecline = controlView.findViewById(R.id.decline_swipe_tv);
        mSwipeTextAccept = controlView.findViewById(R.id.accept_swipe_tv);
        mSwipeTextMessage = controlView.findViewById(R.id.message_swipe_tv);
        mUpArrowDecline = controlView.findViewById(R.id.decline_up_arrow);
        mUpArrowAccept = controlView.findViewById(R.id.accept_up_arrow);
        mUpArrowMessage = controlView.findViewById(R.id.message_up_arrow);

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
                boolean enabled =  callEvents.onToggleCamera();
                //setButton(toggleCameraButton, enabled);
                toggleCameraButton.setAlpha(enabled ? 1.0f : 0.3f);
                callEvents.onToggleCamera();


            }
        });

       contactView.setText(mProfile.name);
       setUserPicture();

        //CallManager.CallUserInterface ui = mCall.ui.get();
       Chronometer statusView = (Chronometer) controlView.findViewById(R.id.call_status);
       setStatusView(statusView);
       setDisplayMode();

        ///to know the size of screen
        RelativeLayout mRootLayout = controlView.findViewById(R.id.rootView);

        ViewTreeObserver vto = mRootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mRootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                m_screen_width = mRootLayout.getMeasuredWidth();
                m_screen_height = mRootLayout.getMeasuredHeight();

            }
        });

        mAcceptViewButton.setOnTouchListener(new MyTouchListener());
        mDeclineViewButton.setOnTouchListener(new MyTouchListener());
        mDefaultMessageButton.setOnTouchListener(new MyTouchListener());

        //start shake animation
        shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake_wibble);
        mAcceptViewButton.startAnimation(shake);

        //Start Animation
        MoveUpAnimation(mUpArrowAccept);
        MoveUpAnimation(mUpArrowDecline);
        MoveUpAnimation(mUpArrowMessage);
        return controlView;
    }


    private void setDisplayMode() {

        boolean incoming = (isIncoming() && !isAnswered());
        mIncomingView.setVisibility(incoming ?View.VISIBLE:View.GONE);
        mInProgressView.setVisibility(incoming ?View.GONE:View.VISIBLE);

    }


    public void setProfile(Mesibo.UserProfile profile){

        mProfile = profile;

    }

    void setUserPicture() {
        String filePath = Mesibo.getUserProfilePicturePath(mProfile, Mesibo.FileInfo.TYPE_AUTO);

        Bitmap b;
        if(Mesibo.fileExists(filePath)) {
            b = BitmapFactory.decodeFile(filePath);
            if(null != b) {
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

    private void MoveUpAnimation(View v) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationY", 100f);
        animation.setDuration(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.start();
    }


    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {

            float x_cord = 0, y_cord, triggerRangeLimit, triggerRangeDownValue;
            int ItemClicked = 0;


            if (isAdded() && getActivity() != null) {


                //initial coordinates of the selected view
                x_cord = view.getX();
                y_cord = view.getY();

                // coordinates of view keeps changing on motion, save it in starting and animate view to statrting position of tge view after release
                if (!firstTouch) {

                    m_VcentreX = view.getX();
                    m_VcentreY = view.getY();
                    firstTouch = true;
                    m_ViewStartingY = motionEvent.getRawY();


                }


                triggerRangeLimit = m_ViewStartingY - m_screen_height / 5; //based on screen sizes , range of the button action should be decided, here we are taking 5th part of the screen height
                triggerRangeDownValue = (float) (m_ViewStartingY - m_screen_height / 4.5);// lower range value action


                //ItemClicked is to know which button(Decline/Accept/Message) is pressed
                if (view instanceof RelativeLayout) {
                    if (view.getId() == R.id.decline_view_layout) {
                        ItemClicked = 1;  //Decline

                    } else if (view.getId() == R.id.custom_message_view_layout) {

                        ItemClicked = 3;  //Message

                    } else {
                        ItemClicked = 2;  //Accept
                        view.clearAnimation();// stop shaky animation on Accept button when button is pressed
                    }
                }


                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN: //when view is pressed

//                        dX = x_cord - motionEvent.getRawX();
                        dY = y_cord - motionEvent.getRawY();


                        if (ItemClicked == 1) {// Decline
                            mSwipeTextDecline.setVisibility(View.VISIBLE);
                            mSwipeTextAccept.setVisibility(View.INVISIBLE);
                            mSwipeTextMessage.setVisibility(View.INVISIBLE);
                            mUpArrowDecline.setVisibility(View.VISIBLE);
                            mUpArrowAccept.setVisibility(View.INVISIBLE);
                            mUpArrowMessage.setVisibility(View.INVISIBLE);
                        } else if (ItemClicked == 3) {//Message
                            mSwipeTextDecline.setVisibility(View.INVISIBLE);
                            mSwipeTextAccept.setVisibility(View.INVISIBLE);
                            mSwipeTextMessage.setVisibility(View.VISIBLE);
                            mUpArrowDecline.setVisibility(View.INVISIBLE);
                            mUpArrowAccept.setVisibility(View.INVISIBLE);
                            mUpArrowMessage.setVisibility(View.VISIBLE);
                        } else {//accept
                            mSwipeTextDecline.setVisibility(View.INVISIBLE);
                            mSwipeTextAccept.setVisibility(View.VISIBLE);
                            mSwipeTextMessage.setVisibility(View.INVISIBLE);
                            mUpArrowDecline.setVisibility(View.INVISIBLE);
                            mUpArrowAccept.setVisibility(View.VISIBLE);
                            mUpArrowMessage.setVisibility(View.INVISIBLE);
                        }


                        break;

                    case MotionEvent.ACTION_MOVE: //view is moving


                        float movingY = motionEvent.getRawY();

                        // limit the moving range of view
                        if (movingY <= m_ViewStartingY && movingY > triggerRangeLimit) {
                            view.animate()
                                    .x(m_VcentreX)
                                    .y(motionEvent.getRawY() + dY)
                                    .setDuration(0)
                                    .start();
                        }


                        break;


                    case MotionEvent.ACTION_UP: // view is released


                        float relesed_view_Y = motionEvent.getRawY();// Y axis of the view when released

                        //Check if the Y value of view when released lies under the range of action || or || it is beyond the limit, in both the cases we need to trigger action of the view
                        if ((relesed_view_Y < triggerRangeLimit && relesed_view_Y > triggerRangeDownValue) || relesed_view_Y < triggerRangeDownValue) {


                            if (ItemClicked == 3) {//Message button

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

                                        String peer =mProfile.address;

                                        switch (which) {
                                            case 0: //
                                                sendCustomeMessage(peer,"Can't talk now. What's up?");
                                                break;
                                            case 1: //
                                                sendCustomeMessage(peer,"I'll call you right back.");
                                                break;
                                            case 2: //
                                                sendCustomeMessage(peer,"I'll call you later.");
                                                break;
                                            case 3: //
                                                sendCustomeMessage(peer,"Can't talk now. Call me later.");
                                                break;
                                            case 4: //Custom message
                                               Intent i = new Intent(getActivity(), MessagingActivity.class);
                                               i.putExtra("peer",peer);
                                               startActivity(i);
                                               hangup();
                                                break;
                                        }
                                    }
                                });


                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else if (ItemClicked == 2) {//Accept Button
                                answer(true);
                                setDisplayMode();
                            } else {// Decline Button
                                hangup();
                            }


                        }


                        view.animate()
                                .x(m_VcentreX)
                                .y(m_VcentreY)
                                .setDuration(0)
                                .start();

                        if (ItemClicked == 2) { // start shaky animation on accept button again when button is released
                            view.startAnimation(shake);
                        }


                        mSwipeTextDecline.setVisibility(View.INVISIBLE);
                        mSwipeTextAccept.setVisibility(View.VISIBLE);
                        mSwipeTextMessage.setVisibility(View.INVISIBLE);
                        mUpArrowDecline.setVisibility(View.INVISIBLE);
                        mUpArrowAccept.setVisibility(View.VISIBLE);
                        mUpArrowMessage.setVisibility(View.INVISIBLE);


                        firstTouch = false;

                        break;
                    default:
                        return false;
                }


            }
            return true;
        }
    }



    private void setButton(ImageButton v, boolean enable) {
        //v.setAlpha((float)(enable?mCallConf.buttonAlphaOn:mCallConf.buttonAlphaOff)/255.0f);

    }

    @Override
    public void onStart() {
        super.onStart();

        boolean captureSliderEnabled = false;
        Bundle args = getArguments();
        if (args != null) {
//            contactView.setText(mCall.profile.name);
//            videoCallEnabled = mCallConf.videoCallEnabled;
//            captureSliderEnabled = videoCallEnabled && mCallConf.captureQualitySlider;
        }
        boolean videoCallEnabled = true;
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
//        if (captureSliderEnabled) {
//            captureFormatSlider.setOnSeekBarChangeListener(
////                    new CaptureQualityController(captureFormatText, callEvents));
//       // } else {
//            captureFormatText.setVisibility(View.GONE);
//            captureFormatSlider.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        setButton(toggleMuteButton, mCall.audioMute);
//        setButton(toggleCameraButton, mCall.videoMute);
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
        callEvents = (MesiboVideoCallFragment.OnCallEvents) activity;
    }

    @Override
    public boolean Mesibo_onCall(long peerid, long callid, Mesibo.UserProfile userProfile, int i) {
        return false;
    }

    @Override
    public boolean Mesibo_onCallStatus(long peerid, long callid, int status, int flags, String desc) {
        //Log.d(TAG, "Mesibo_onCallStatus: status: " + status + " flags: " + flags);

//        if(null == mCall)
//            return true;

//        boolean video = ((flags & Mesibo.CALLFLAG_VIDEO) > 0);
//        if (CALLSTATUS_ANSWER == status && !video) {
//            setButton(toggleCameraButton, true);
//        }
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
