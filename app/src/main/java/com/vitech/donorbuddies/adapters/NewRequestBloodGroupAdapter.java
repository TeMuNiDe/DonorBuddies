package com.vitech.donorbuddies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vitech.donorbuddies.R;
import com.vitech.donorbuddies.data.BloodGroupSelector;
import com.vitech.donorbuddies.data.BloodRequest;



public class NewRequestBloodGroupAdapter extends RecyclerView.Adapter<NewRequestBloodGroupAdapter.BloodGroupHolder> {
    Context context;
   public boolean isPrimary  = false;
    int tos = 0;
  public boolean[] checked={false,false,false,false,false,false,false,false};
    String[] bloodGroups = BloodRequest.BLOOD_GROUP_ALL;
    public NewRequestBloodGroupAdapter(Context context,boolean isPrimary){
        this.context  = context;
this.isPrimary = isPrimary;
    }


    @Override
    public BloodGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new BloodGroupHolder(inflater.inflate(R.layout.group_selector,parent,false));
    }

    @Override
    public void onBindViewHolder(BloodGroupHolder holder, int position) {
holder.view.setText(bloodGroups[position]);

            holder.view.setChecked(checked[position]);


    }

    @Override
    public int getItemCount() {
        return bloodGroups.length;
    }

    class BloodGroupHolder extends RecyclerView.ViewHolder implements BloodGroupSelector.OnCheckedChangedListener{
BloodGroupSelector view;

        public BloodGroupHolder(View itemView) {
            super(itemView);
           view = (BloodGroupSelector)itemView.findViewById(R.id.blood);
view.setOnCheckedChangedListner(this);
                    }


        @Override
        public void onCheckedChanged(boolean isChecked) {
            if(isPrimary){
                for(int i=0;i<checked.length;i++){
                    checked[i]=false;
                }
                checked[getLayoutPosition()]=isChecked;
            }else{
                checked[getLayoutPosition()]=isChecked;
            }
            notifyDataSetChanged();
        }
    }
}
