/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mesibo.messenger.fcm;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.mesibo.messenger.MainApplication;

import org.json.JSONObject;

public class MesiboGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "FcmListenerService";

    /**
     * Called when message is received.
     *
   //  * @param from SenderID of the sender.
    // * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
//    @Override
//    public void onMessageReceived(String from, Bundle data) {
//
//        //Note if we send 'notification' instead of 'data', it will be under notification key
//        //https://developers.google.com/cloud-messaging/concept-options#notifications_and_data_messages
//
//        /*
//        String message = data.getString("message");
//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "Message: " + message);
//
//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }
//        */
//
//        MesiboRegistrationIntentService.sendMessageToListener(data, false);
//
//        Intent intent = new Intent("com.mesibo.someintent");
//        intent.putExtras(data);
//        MesiboJobIntentService.enqueueWork(MainApplication.getAppContext(), intent);
//
//    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String msg = remoteMessage.getNotification().getBody();
            Log.e(TAG, "Notification Body: " + msg);
           // handleNotification(remoteMessage.getNotification().getBody());

            Bundle data = new Bundle();
            data.putString("body",msg);

            //Note if we send 'notification' instead of 'data', it will be under notification key
            //https://developers.google.com/cloud-messaging/concept-options#notifications_and_data_messages

        /*
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        */

            // String data = Objects.requireNonNull(remoteMessage.getNotification()).getBody();

            MesiboRegistrationIntentService.sendMessageToListener(data, false);

            Intent intent = new Intent("org.mesibo.messenger.someintent");
            intent.putExtras(data);
            MesiboJobIntentService.enqueueWork(MainApplication.getAppContext(), intent);
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }

//        Map<String, String> params = remoteMessage.getData();
//        JSONObject object = new JSONObject(params);
//        Log.e("JSON_OBJECT", object.toString());






    }
}
