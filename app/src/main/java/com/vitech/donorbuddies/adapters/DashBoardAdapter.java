package com.vitech.donorbuddies.adapters;

import android.app.Activity;
import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vitech.donorbuddies.R;
import com.vitech.donorbuddies.data.BloodRequest;
import com.vitech.donorbuddies.managers.DataBaseBridge;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.RequestHolder> {
    public Context context;
   public List<BloodRequest> bloodRequests;
OnItemClickListener listener;
    public DashBoardAdapter(Context context,Activity parent){
        this.context =context;
        this.listener = (OnItemClickListener)parent;
        this.bloodRequests = new DataBaseBridge(context).getRequests();
    }

    @Override
    public RequestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new RequestHolder(inflater.inflate(R.layout.request_item,parent,false));
    }


    @Override
    public void onBindViewHolder(RequestHolder holder, int position) {
holder.request = bloodRequests.get(position);
        holder.bloodGroupPrimary.setText(holder.request.bloodGroupPrimary);
        holder.bloodGroupSubstitute.setText(holder.request.bloodGroupSubstitute.replace("0","   "));
        holder.requestInfo.setText(holder.request.name+","+holder.request.hospital);
        Calendar time = Calendar.getInstance();
                time.setTimeInMillis(Long.parseLong(holder.request.timeStamp));
        holder.requestTime.setText(SimpleDateFormat.getInstance().format(time.getTime()));

    }

    @Override
    public int getItemCount() {

        return bloodRequests.size();
    }

    class RequestHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{
        public TextView requestInfo;
        public  TextView bloodGroupPrimary;
        public TextView bloodGroupSubstitute;
        public  TextView requestTime;
public BloodRequest request;
        public RequestHolder(View itemView) {
            super(itemView);
            requestInfo =(TextView)  itemView.findViewById(R.id.request_info);
            bloodGroupPrimary = (TextView) itemView.findViewById(R.id.blood_group_primary_text);
            bloodGroupSubstitute = (TextView) itemView.findViewById(R.id.blood_group_substitute_text);
            requestTime  = (TextView)itemView.findViewById(R.id.request_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
listener.onItemCLick(request);        }
    }
   public  interface OnItemClickListener{
       void onItemCLick(BloodRequest request);
   }

}
