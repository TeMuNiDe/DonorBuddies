package com.vitech.donorbuddies.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.vitech.donorbuddies.data.BloodRequest;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Communicator{


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    Context context;

    public Communicator(Context context){
this.context = context;
    }


    public void sendBloodRequest(BloodRequest request) throws Exception{
        Response response;
        SharedPreferences preferences = context.getSharedPreferences("donorpreferences",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name",request.name);
        editor.putString("contact",request.contact);
        editor.commit();
request.sender = preferences.getString("sender","random");


        RequestBody body = RequestBody.create(JSON,request.toJSON());

            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();

            Request callBuilder = new Request.Builder().addHeader("Authorization", "key=AAAAt6PYfCM:APA91bGRygFw0YaSiVRWbvWDGdq-eSuk-nH8DNUcEf4A4WSvV4S1tt9kSgXAGqafChufTszfW-rpzfwny0jvdDYu8AITTbDLeRnyX0C8ixxoYfgWOYKchD7wwzgOSrYWiPwwCGv0Lsby").addHeader("Content-Type", "application/json").url("https://fcm.googleapis.com/fcm/send").post(body).build();

           response = client.newCall(callBuilder).execute();


        if(response.isSuccessful()) {
            return ;
        }
       throw new Exception(new Throwable("network error"));
    }
    public boolean delete(String timeStamp){
        JSONObject object = new JSONObject();
try {
    object.put("to", "/topics/all").put("priority","high").put("data", new JSONObject().put("operation", "delete").put("timestamp", timeStamp));
    //Log.d("JSON",object.toString());
    RequestBody body = RequestBody.create(JSON, object.toString());

    OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();

    Request callBuilder = new Request.Builder().addHeader("Authorization", "key=AAAAt6PYfCM:APA91bGRygFw0YaSiVRWbvWDGdq-eSuk-nH8DNUcEf4A4WSvV4S1tt9kSgXAGqafChufTszfW-rpzfwny0jvdDYu8AITTbDLeRnyX0C8ixxoYfgWOYKchD7wwzgOSrYWiPwwCGv0Lsby").addHeader("Content-Type", "application/json").url("https://fcm.googleapis.com/fcm/send").post(body).build();
Response re = client.newCall(callBuilder).execute();
    if(!re.isSuccessful()){


       return false;
    }
    //Log.d("resp",rep);

}catch (Exception e){
    e.printStackTrace();
    return false;
}
        return true;
    }
    }
