package com.vitech.donorbuddies.managers;




import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class FireBaseToken extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseInstanceId.getInstance();


        Log.d("Token","received");
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        Log.d("Topic","subscribed");
    }
}
