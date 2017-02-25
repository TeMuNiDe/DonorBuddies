package com.vitech.donorbuddies;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.vitech.donorbuddies.adapters.DashBoardAdapter;
import com.vitech.donorbuddies.adapters.NewRequestBloodGroupAdapter;
import com.vitech.donorbuddies.data.BloodRequest;
import com.vitech.donorbuddies.managers.Communicator;
import com.vitech.donorbuddies.managers.RefreshService;


public class DashBoard extends AppCompatActivity implements DashBoardAdapter.OnItemClickListener{

View newRequest;
    MessageReciever messageReciever;
  public   static String ACTION_MESSAGE = "MESSAGE_RECEIVED";
    View dashBoard;
    DashBoardAdapter adapter;
    TextView noRequest;
    ImageView newRequestTrigger;
    RecyclerView dashBoardList,groupSelectorPrimary,groupSelectorSecondary;
    boolean isNewRequest;
    EditText newRequestName,newRequestContact;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        dashBoardList = (RecyclerView)findViewById(R.id.dash_board_list);
        dashBoard = findViewById(R.id.activity_dash_board);
        noRequest = (TextView)findViewById(R.id.no_request_string);
        preferences = getSharedPreferences("donorpreferences",MODE_PRIVATE);
        if(preferences.getString("sender","null").equals("null")){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("sender",Long.toString(System.currentTimeMillis()));
            editor.commit();
        }
noRequest.setText(preferences.getString("no_request",getResources().getString(R.string.def_no_request_string)));

    groupSelectorPrimary = (RecyclerView) findViewById(R.id.new_request_primary);
        groupSelectorSecondary = (RecyclerView) findViewById(R.id.new_request_substitute);
        groupSelectorPrimary.setAdapter(new NewRequestBloodGroupAdapter(getApplicationContext(),true));
        groupSelectorSecondary.setAdapter(new NewRequestBloodGroupAdapter(getApplicationContext(),false));
        groupSelectorPrimary.setLayoutManager(new GridLayoutManager(getApplicationContext(),5));
        groupSelectorSecondary.setLayoutManager(new GridLayoutManager(getApplicationContext(),5));
        adapter = new DashBoardAdapter(getApplicationContext(),this);
        if(adapter.bloodRequests.size()>0){
            dashBoardList.setVisibility(View.VISIBLE);
            noRequest.setVisibility(View.GONE);
        }
        dashBoardList.setAdapter(adapter);
        dashBoardList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        newRequestTrigger = (ImageView)findViewById(R.id.new_request);
        newRequest= findViewById(R.id.new_request_layout);
        newRequestName = (EditText)findViewById(R.id.new_request_name);
        newRequestContact = (EditText)findViewById(R.id.new_request_contact);
        newRequestTrigger.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        newRequest.setVisibility(View.VISIBLE);


            newRequestName.setText(preferences.getString("name",""));
            newRequestContact.setText(preferences.getString("contact",""));
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
        animation.setDuration(500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}@Override public void onAnimationEnd(Animation animation) {dashBoard.setVisibility(View.GONE);isNewRequest=true;invalidateOptionsMenu();}@Override public void onAnimationRepeat(Animation animation) {}});
        newRequest.startAnimation(animation);

           }
});


    }


    @Override
    public void onBackPressed() {
if(newRequest.getVisibility()== View.GONE){
    startService(new Intent(getApplicationContext(), RefreshService.class));
    super.onBackPressed();
}else {
Animation slideBack = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
    slideBack.setDuration(500);
    slideBack.setInterpolator(new AccelerateInterpolator());
    slideBack.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation){

        }

        @Override
        public void onAnimationEnd(Animation animation){
newRequest.setVisibility(View.GONE);
isNewRequest = false;
            invalidateOptionsMenu();
        }

        @Override
        public void onAnimationRepeat(Animation animation){

        }
    });
    dashBoard.setVisibility(View.VISIBLE);
    newRequest.startAnimation(slideBack);

}
    }

    @Override
    public void onItemCLick(BloodRequest request) {
        startActivity(new Intent(getApplicationContext(),BloodRequestActivity.class).putExtra("BloodRequest",request));
    }

    class PostRequest extends AsyncTask<BloodRequest,Void,Boolean>{
        BloodRequest builder;
        ProgressDialog dialog;
        boolean dirty = false;
        @Override
        protected void onPreExecute() {
            builder = new BloodRequest.Builder(findViewById(R.id.new_request_parent),getApplicationContext()).build();

            if(builder.name.equals("")||builder.contact.equals("")||builder.hospital.equals("")||builder.message.equals("")|builder.bloodGroupSubstitute.equals("")||builder.bloodGroupPrimary.equals("")){
                this.dirty  = true;
            }
            if(!dirty) {
                dialog = new ProgressDialog(DashBoard.this);
                dialog.setMessage("Sending Request..");
                dialog.setIndeterminate(false);
                dialog.show();
            }
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(BloodRequest... params) {
if(!dirty) {
    try {
        new Communicator(getApplicationContext()).sendBloodRequest(builder);
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
           return true;
        }

        @Override
        protected void onPostExecute(Boolean bloodRequest) {
            if (!dirty) {
                dialog.dismiss();
                if (bloodRequest) {
                    dashBoardList.setVisibility(View.VISIBLE);
                    noRequest.setVisibility(View.GONE);

                    onBackPressed();
                    Snackbar.make(findViewById(R.id.activity_dash_board), "Sent Success", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(findViewById(R.id.activity_dash_board), "Network error", Snackbar.LENGTH_SHORT).show();
                }

            }else {
                Snackbar.make(findViewById(R.id.activity_dash_board), "Please Fill All the particulars", Snackbar.LENGTH_SHORT).show();
            }
            super.onPostExecute(bloodRequest);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.dash_board_menu, menu);
        MenuItem item = menu.findItem(R.id.send);
        if (isNewRequest) {

            item.setVisible(true);
        }
        else {
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.send:new PostRequest().execute();break;
            case R.id.mail:Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","jntuagaga@gmail.com",null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Anantapur Donnors - Reg");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "write your content here");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));break;
            case R.id.rate:Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id="+getPackageName()));
                startActivity(i);break;
            case R.id.about:startActivity(new Intent(getApplicationContext(),About.class));
        }


        return super.onOptionsItemSelected(item);
    }

    class MessageReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
BloodRequest request = intent.getParcelableExtra("bloodrequest");

adapter.bloodRequests.add(0,request);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        int ver = 0;
        try {
            ver =   getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        }catch (Exception e){
            FirebaseCrash.report(e);
        }

        if(preferences.getInt("version",0)>ver) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
            AlertDialog dialog;
            builder.setMessage("New Version Available").setPositiveButton("Udate", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("market://details?id="+getPackageName()));
                    startActivity(i);
                }
            }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                }
            });
dialog = builder.create();
            dialog.show();

        }




        if(messageReciever==null){
            messageReciever = new MessageReciever();

        }
        IntentFilter filter = new IntentFilter(ACTION_MESSAGE);
        registerReceiver(messageReciever,filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(messageReciever!=null){
            unregisterReceiver(messageReciever);
        }
        super.onPause();
    }
}
