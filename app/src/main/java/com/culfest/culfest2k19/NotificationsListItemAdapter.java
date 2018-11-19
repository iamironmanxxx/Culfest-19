package com.culfest.culfest2k19;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pankaj Vaghela on 28-10-2018.
 */
public class NotificationsListItemAdapter extends RecyclerView.Adapter<NotificationsListItemAdapter.ViewHolder> {

    private ArrayList<CulfestNotification> mNotifs;
    private LayoutInflater mInflater;
    private NotificationItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    NotificationsListItemAdapter(Context context, ArrayList<CulfestNotification> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mNotifs = data;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_notifications_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setData(mNotifs.get(position));
    }

    public void refreshBlockOverlay(int position){
        notifyItemChanged(position);
    }


    // total number of cells
    @Override
    public int getItemCount() {
        return mNotifs.size();
    }




    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleTv, msgTv, timeTv;
        ImageView toggleIc;
        boolean toggle =  false;

        ViewHolder(View view) {
            super(view);
            titleTv = (TextView) view.findViewById(R.id.ni_titletv);
            msgTv = (TextView) view.findViewById(R.id.ni_msgtv);
            timeTv = (TextView) view.findViewById(R.id.ni_timetv);
            toggleIc = view.findViewById(R.id.ni_toggle_ic);
            toggleIc.setVisibility(View.GONE);
            view.setOnClickListener(this);
        }

        public void setData(CulfestNotification data){
            titleTv.setText(data.getTitle());
            msgTv.setText(data.getMsg());

        }

        @Override
        public void onClick(View view) {

                if (toggle) {
                    toggle = false;
//                    msgTv.setMaxLines(2);
                    msgTv.getLayoutParams().height = context.getResources().getDimensionPixelSize(R.dimen.notification_item_msg_height);
                } else {
                    toggle = true;
//                    msgTv.setMaxLines(Integer.MAX_VALUE);
                    msgTv.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                msgTv.requestLayout();
            /*
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle(mNotifs.get(getAdapterPosition()).getTitle());
            alertBuilder.setMessage(mNotifs.get(getAdapterPosition()).getMsg());
            alertBuilder.setCancelable(true);
            alertBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertBuilder.create().show();*/

            if (mClickListener != null) {
                  mClickListener.onNotificationItemClick(view, getAdapterPosition());
            }
        }
    }

    // convenience method for getting data at click position
    CulfestNotification getItem(int id) {
        return mNotifs.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(NotificationItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface NotificationItemClickListener {
        void onNotificationItemClick(View view, int position);
    }
}

