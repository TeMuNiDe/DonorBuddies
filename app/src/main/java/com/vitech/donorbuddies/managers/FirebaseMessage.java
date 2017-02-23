package com.vitech.donorbuddies.managers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vitech.donorbuddies.BloodRequestActivity;
import com.vitech.donorbuddies.DashBoard;
import com.vitech.donorbuddies.R;
import com.vitech.donorbuddies.data.BloodRequest;
import java.util.Map;

public class FirebaseMessage extends FirebaseMessagingService{
Map<String,String> map ;

    SharedPreferences preferences;
    boolean self = false;
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        map =  remoteMessage.getData();
        preferences = getSharedPreferences("donorpreferences",MODE_PRIVATE);
        if(map.get("operation").equals("insert")) {
            String mapp = map.get("sender");
            String pref = preferences.getString("sender","null");
            BloodRequest request = new BloodRequest(mapp,map.get("name"), map.get("contact"), map.get("hospital"), map.get("message"), map.get("primaryBlood"), map.get("substituteBlood"), map.get("timestamp"));

            if(mapp.equals(pref) ){
                self = true;
sendBroadcast(new Intent(DashBoard.ACTION_MESSAGE).putExtra("bloodrequest",request).putExtra("insert",true));
            }

            Intent notifier = new Intent(getApplicationContext(), BloodRequestActivity.class);
            notifier.putExtra("BloodRequest", request);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

            stackBuilder.addParentStack(BloodRequestActivity.class);
            stackBuilder.addNextIntent(notifier);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(request.bloodGroupPrimary + " blood Required").setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).setContentText(request.name + "," + request.hospital).setContentIntent(pendingIntent).build();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            new DataBaseBridge(getApplicationContext()).insertRequest(request);
            if(!self) {
                sendBroadcast(new Intent(DashBoard.ACTION_MESSAGE).putExtra("bloodrequest",request).putExtra("insert",true));

                manager.notify(0, notification);
            }
        }
        else if(map.get("operation").equals("delete")){
            new DataBaseBridge(this).deleteRequest(map.get("timestamp"));

        }else if(map.get("operation").equals("val_update")){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(map.get("key"),map.get("value"));
            editor.commit();
        }else if(map.get("operation").equals("version_update")){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("version",Integer.parseInt(map.get("version")));
editor.commit();
        }





    }
}
