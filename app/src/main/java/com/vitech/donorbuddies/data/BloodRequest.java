package com.vitech.donorbuddies.data;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.crash.FirebaseCrash;
import com.vitech.donorbuddies.R;
import com.vitech.donorbuddies.adapters.NewRequestBloodGroupAdapter;

import org.json.JSONObject;

import java.util.Calendar;


public class BloodRequest implements Parcelable {

   public static final String[] BLOOD_GROUP_ALL = {BloodRequest.BLOOD_GROUP_A_POSITIVE,BloodRequest.BLOOD_GROUP_A_NEGATIVE,BloodRequest.BLOOD_GROUP_B_POSITIVE,BloodRequest
            .BLOOD_GROUP_B_NEGATIVE,BloodRequest.BLOOD_GROUP_AB_POSITIVE,BloodRequest.BLOOD_GROUP_AB_NEGATIVE,BloodRequest.BLOOD_GROUP_O_POSITIVE,BloodRequest.BLOOD_GROUP_O_NEGATIVE};
    public static final String BLOOD_GROUP_A_POSITIVE = "A+";
    public static final String BLOOD_GROUP_A_NEGATIVE = "A-";
    public static final String BLOOD_GROUP_B_POSITIVE = "B+";
    public static final String BLOOD_GROUP_B_NEGATIVE = "B-";
    public static final String BLOOD_GROUP_AB_POSITIVE = "AB+";
    public static final String BLOOD_GROUP_AB_NEGATIVE = "AB-";
    public static final String BLOOD_GROUP_O_POSITIVE = "O+";
    public static final String BLOOD_GROUP_O_NEGATIVE = "O-";

    public String bloodGroupPrimary;
    public String bloodGroupSubstitute;
    public String message;
    public String hospital;
    public String timeStamp;
    public String name;
    public String contact;
    public String sender;
    public BloodRequest(String sender,String name,String contact,String hospital,String message,String bloodGroup,String bloodGroupSubstitute,String timeStamp){
        this.name = name;
        this.contact = contact;
       this.hospital = hospital;
        this.message = message;
        this.sender = sender;
        this.bloodGroupPrimary = bloodGroup;
        this.bloodGroupSubstitute = bloodGroupSubstitute;
        this.timeStamp = timeStamp;
            }
    public String toJSON(){

        JSONObject query = new JSONObject();
JSONObject data = new JSONObject();

        try {
            data.put("sender", sender).put("name",name).put("contact",contact).put("hospital",hospital).put("message",message).put("primaryBlood",bloodGroupPrimary).put("substituteBlood",bloodGroupSubstitute).put("timestamp",timeStamp).put("operation","insert");
           query.put("to","/topics/all").put("priority","high").put("data",data);


        }catch (Exception e){

            FirebaseCrash.report(e);

        }
        return query.toString();
    }



    public static class Builder{
       View container;
     RecyclerView primaryGroup,substituteGroup;
       NewRequestBloodGroupAdapter primary,substitute;
       EditText name,contact,hospital,message;
        boolean perfect;
        Context context;
        public Builder(View container, Context context){
this.container = container;
            this.context = context;
            this.primaryGroup = (RecyclerView) container.findViewById(R.id.new_request_primary);
            this.substituteGroup = (RecyclerView)container.findViewById(R.id.new_request_substitute);
            this.primary = (NewRequestBloodGroupAdapter) primaryGroup.getAdapter();
            this.substitute = (NewRequestBloodGroupAdapter)substituteGroup.getAdapter();
            this.name = (EditText) container.findViewById(R.id.new_request_name);
            this.contact = (EditText) container.findViewById(R.id.new_request_contact);
            this.hospital =(EditText)  container.findViewById(R.id.new_request_hospital);
            this.message = (EditText) container.findViewById(R.id.new_request_message);
        }


       public BloodRequest build(){
           String primaryG = "";
           String substituteG = "";
           for(int i = 0;i<primary.checked.length;i++){
               if(primary.checked[i]){
                   primaryG = BLOOD_GROUP_ALL[i];
               }
               if(substitute.checked[i]){
                   substituteG+=BLOOD_GROUP_ALL[i]+"0";
               }

           }

           String defmessage = context.getSharedPreferences("donorpreferences",Context.MODE_PRIVATE).getString("def_message",context.getResources().getString(R.string.def_bloodrequest_message));

           if(!message.getText().toString().equals("")){
               defmessage = message.getText().toString();
           }

           return new BloodRequest(context.getSharedPreferences("donorpreferences",Context.MODE_PRIVATE).getString("senderid","self"),name.getText().toString(),contact.getText().toString(),hospital.getText().toString(),defmessage,primaryG,substituteG,Long.toString( Calendar.getInstance().getTimeInMillis()));
       }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(contact);
        dest.writeString(message);
        dest.writeString(hospital);
        dest.writeString(bloodGroupPrimary);
        dest.writeString(bloodGroupSubstitute);
        dest.writeString(timeStamp);
        dest.writeString(sender);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public BloodRequest createFromParcel(Parcel source) {
            return new BloodRequest(source);
        }

        @Override
        public BloodRequest[] newArray(int size) {
            return new BloodRequest[size];
        }
    };
    public BloodRequest(Parcel in){
        this.name = in.readString();
        this.contact = in.readString();
        this.message = in.readString();
        this.hospital = in.readString();
        this.bloodGroupPrimary = in.readString();
        this.bloodGroupSubstitute = in.readString();
        this.timeStamp = in.readString();
        this.sender = in.readString();
    }



}
