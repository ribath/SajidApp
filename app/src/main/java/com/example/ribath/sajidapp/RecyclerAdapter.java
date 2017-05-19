package com.example.ribath.sajidapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ribath on 5/6/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    List<MessageData> messageDatas;

    public RecyclerAdapter(List<MessageData> messageDatas) {
        this.messageDatas = messageDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_list_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageData messageData = messageDatas.get(position);
        ////
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(messageData.getDate()));
        String date = formatter.format(calendar.getTime());
        ////
        holder.number.setText(messageData.getNumber());
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        if(messageDatas  != null) {
            return messageDatas.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView number;
        public TextView date;

        public ViewHolder(View v) {
            super(v);
            number = (TextView) v.findViewById(R.id.number);
            date = (TextView) v.findViewById(R.id.date);
        }
    }
}
