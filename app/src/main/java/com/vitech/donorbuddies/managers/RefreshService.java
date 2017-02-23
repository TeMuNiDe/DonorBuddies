package com.vitech.donorbuddies.managers;

import android.app.IntentService;
import android.content.Intent;
import com.vitech.donorbuddies.data.BloodRequest;

import java.util.Calendar;
import java.util.List;

public class RefreshService extends IntentService {

    public RefreshService() {
        super("RefreshService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        long mills = Calendar.getInstance().getTimeInMillis();
        DataBaseBridge baseBridge = new DataBaseBridge(this);
        List<BloodRequest> requestList = baseBridge.getRequests();
        for(BloodRequest request:requestList){
            if((mills-Long.parseLong(request.timeStamp))>1000*60*60*24*2){
                baseBridge.deleteRequest(request.timeStamp);
            }
        }

    }

    }
