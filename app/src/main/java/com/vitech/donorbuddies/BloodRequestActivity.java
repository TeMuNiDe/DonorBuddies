package com.vitech.donorbuddies;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.vitech.donorbuddies.data.BloodRequest;
import com.vitech.donorbuddies.managers.Communicator;


import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.EXTRA_TEXT;

public class BloodRequestActivity extends AppCompatActivity {
BloodRequest request;
    TextView message;
    Button delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_request);
delete = (Button)findViewById(R.id.delete);

        request = getIntent().getParcelableExtra("BloodRequest");
        if(request.sender.equals(getSharedPreferences("donorpreferences",MODE_PRIVATE).getString("sender","null"))){
            delete.setVisibility(View.VISIBLE);

        }
        setTitle(request.name);
        message = (TextView)findViewById(R.id.message);
        message.setText(request.hospital+"\n\n"+request.message);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
new Deletor().execute();
            }
        });
    }

    public void Call(View v){
        Intent caller  = new Intent(Intent.ACTION_DIAL);
        caller.setData(Uri.parse("tel:"+request.contact));
        startActivity(Intent.createChooser(caller,"Call Using..."));
    }
    public void Share(View v){
Intent share = new Intent(ACTION_SEND);
       String rqst = "Urgently Required "+request.bloodGroupPrimary+ " blood\n\n "+request.message+"\n\n Contact:\n\n"+request.name+"\n"+request.contact+
               "\n\n Location :"+request.hospital;
        rqst   = rqst+" \n\n The Following blood groups can also be substituted \n"+request.bloodGroupSubstitute.replace("0","  ");
        share.putExtra(EXTRA_TEXT,rqst);
        share.setType("text/plain");
        startActivity(Intent.createChooser(share,"Share to.."));
    }
    public void Locate(View v){
String query = "geo:0,0?q="+ Uri.encode(request.hospital+",Anantapur");
        Intent locate = new Intent(ACTION_VIEW,Uri.parse(query));
       startActivity(Intent.createChooser(locate,"View in..."));

    }

  class Deletor extends AsyncTask<Void,Boolean,Boolean> {
      ProgressDialog dialog;
      AlertDialog.Builder builder;

      @Override
      protected void onPreExecute() {

          dialog = new ProgressDialog(BloodRequestActivity.this);
          dialog.setMessage("Sending Deletion Commands..");
          dialog.setIndeterminate(false);
dialog.show();
          super.onPreExecute();
      }

      @Override
      protected Boolean doInBackground(Void... params) {
       return  new Communicator(getApplicationContext()).delete(request.timeStamp);


      }


      @Override
      protected void onPostExecute(Boolean aBoolean) {
          dialog.dismiss();
          builder = new AlertDialog.Builder(BloodRequestActivity.this);
                    if (aBoolean) {

builder.setMessage("Request will be deleted shortly...").setPositiveButton("ok", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        onBackPressed();
    }
}).show();
                    } else {

                        builder.setMessage("Unknown error occured").setPositiveButton("retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              new Deletor().execute();
                            }
                        }).show();
          }
          super.onPostExecute(aBoolean);
      }
  }

}
