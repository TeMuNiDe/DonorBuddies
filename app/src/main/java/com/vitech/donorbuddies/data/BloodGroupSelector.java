package com.vitech.donorbuddies.data;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckedTextView;

import com.vitech.donorbuddies.R;


public class BloodGroupSelector extends CheckedTextView {
    OnCheckedChangedListener listener;
    Context context;
    public BloodGroupSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }

  public void setOnCheckedChangedListner(OnCheckedChangedListener listtener){
      this.listener = listtener;
      this.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              toggle();
              listener.onCheckedChanged(isChecked());

          }
      });
    }


    @Override
    public void setChecked(boolean checked) {
        if(checked){
            this.setBackgroundResource(R.drawable.red_circle);
            this.setTextColor(Color.WHITE);

        }
        else{
            this.setBackgroundResource(R.drawable.red_ring);
            this.setTextColor(getResources().getColor(R.color.colorPrimary));
        }


        super.setChecked(checked);
    }


    public interface OnCheckedChangedListener{
        void onCheckedChanged(boolean isChecked);
    }

}
