package org.mesibo.messenger;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboUtils;
import com.mesibo.calls.ImageTouchSlider;


import com.mesibo.calls.MesiboIncomingAudioCallFragment;
import com.mesibo.messaging.MessagingActivity;

import static com.mesibo.api.Mesibo.CALLSTATUS_COMPLETE;

/**
 * Created by mesibo on 6/5/17.
 */

public class AudioIncomingFragment extends MesiboIncomingAudioCallFragment implements
        View.OnClickListener, Mesibo.CallListener, View.OnKeyListener {

    private LinearLayout mUpArrowDecline, mUpArrowAccept, mUpArrowMessage;
    private TextView contactView, mSwipeTextDecline, mSwipeTextAccept, mSwipeTextMessage;
    Animation shake;
    float dX, dY;
    float m_screen_width, m_screen_height;
    float m_VcentreX, m_VcentreY, m_ViewStartingY;
    ImageView photoImage;
    ImageView photoImageDown;
    boolean firstTouch = false;
    Mesibo.UserProfile mProfile;
    TextView mName, mLocation;
    Context mContext;
    ImageTouchSlider slider;
    GestureDetector myGestDetector;
    LinearLayout mLocked, mUnLocked;
    public static final String TAG = "AudioIncomingFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(com.mesibo.calls.R.layout.incoming_fragment_new, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        mName = view.findViewById(com.mesibo.calls.R.id.incoming_name);
        mLocation = view.findViewById(com.mesibo.calls.R.id.incoming_location);
        slider = view.findViewById(com.mesibo.calls.R.id.slider);
        mUnLocked = view.findViewById(com.mesibo.calls.R.id.phone_unlocked);
        mLocked = view.findViewById(com.mesibo.calls.R.id.phone_locked);
        photoImage = view.findViewById(com.mesibo.calls.R.id.photo_image);
        photoImageDown = view.findViewById(com.mesibo.calls.R.id.photo_image_down);

        RelativeLayout mDeclineViewButton = view.findViewById(R.id.decline_view_layout);
        RelativeLayout mAcceptViewButton = view.findViewById(R.id.accept_view_layout);
        RelativeLayout mDefaultMessageButton = view.findViewById(R.id.custom_message_view_layout);

        mSwipeTextDecline = view.findViewById(R.id.decline_swipe_tv);
        mSwipeTextAccept = view.findViewById(R.id.accept_swipe_tv);
        mSwipeTextMessage = view.findViewById(R.id.message_swipe_tv);

        mUpArrowDecline = view.findViewById(R.id.decline_up_arrow);
        mUpArrowAccept = view.findViewById(R.id.accept_up_arrow);
        mUpArrowMessage = view.findViewById(R.id.message_up_arrow);


        ///to know the size of screen
        LinearLayout mRootLayout = view.findViewById(R.id.incoming_fragment_view);

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

        mName.setText(mProfile.name);
        setUserPicture();
        setValues();

    }

    private void setValues() {
        myGestDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            boolean swipePerformed = true;

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!swipePerformed) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e1) {
                swipePerformed = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        KeyguardManager myKeyManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);

        //Yusuf, TBD fix lock screen
        if (false && myKeyManager.inKeyguardRestrictedInputMode()) {

            //screen is locked
            mLocked.setVisibility(View.VISIBLE);
            mUnLocked.setVisibility(View.GONE);

        } else {
            mLocked.setVisibility(View.GONE);
            mUnLocked.setVisibility(View.VISIBLE);
            //screen is not locked

        }

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
                photoImage.setImageDrawable(MesiboUtils.getRoundImageDrawable(b));
                photoImageDown.setImageBitmap(b);
            }
        } else {
            //TBD, getActivity.getresource crashes sometime if activity is closing
            photoImage.setImageDrawable(MesiboUtils.getRoundImageDrawable(BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(), R.drawable.default_user_image)));
            photoImageDown.setImageDrawable(MesiboUtils.getRoundImageDrawable(BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(), R.drawable.default_user_image)));
        }
    }


    private void sendCustomeMessage(String peer, String message) {

        Mesibo.MessageParams messageParams = new Mesibo.MessageParams();
        messageParams.setPeer(peer);
        Mesibo.sendMessage(messageParams, Mesibo.random(), message);
        callHangup();


    }

    private void MoveUpAnimation(View v) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(v, "translationY", 100f);
        animation.setDuration(1000);
        animation.setRepeatCount(ValueAnimator.INFINITE);
        animation.setRepeatMode(ValueAnimator.REVERSE);
        animation.start();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            //Do something
            // Toast.makeText(mContext, "vol", Toast.LENGTH_SHORT).show();
        }
        return true;
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

                                sendCustomMessageOnCall();

                            } else if (ItemClicked == 2) {//Accept Button

                                callAnswer();

                            } else {// Decline Button

                                callHangup();

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


    public void callHangup() {
        hangup();
        getActivity().finish();
    }


    public void callAnswer() {

        answer();


    }

    public void sendCustomMessageOnCall() {
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

                        break;
                }
                callHangup();
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public void onClick(View v) {
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
        stopIncomingNotification();

    }


    @Override
    public boolean Mesibo_onCall(long peerid, long callid, Mesibo.UserProfile profile, int flags) {
        return true;
    }

    @Override
    public boolean Mesibo_onCallStatus(long peerid, long callid, int status, int flags, String desc) {
        if ((status & CALLSTATUS_COMPLETE) > 0) {
            getActivity().finish();
        }
        return true;
    }

    @Override
    public void Mesibo_onCallServer(int type, String url, String username, String credential) {

    }
}
