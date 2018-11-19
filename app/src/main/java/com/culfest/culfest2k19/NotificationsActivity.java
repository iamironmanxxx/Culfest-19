package com.culfest.culfest2k19;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity implements NotificationsListItemAdapter.NotificationItemClickListener{

    RecyclerView notifsListRV;
    ProgressBar progressBar;

    CollectionReference notifsRef;
    ArrayList<CulfestNotification> notifications;
    int notifications_count;
    NotificationsListItemAdapter notifListAdapter;

    boolean isNotificationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notifsRef = FirebaseFirestore.getInstance().collection("notifications");

        notifsListRV = findViewById(R.id.notifications_list_rv);

        Query query = notifsRef.orderBy("timestamp",Query.Direction.DESCENDING);
        setUpNotifsList(query);
    }

    private void setUpNotifsList(Query pQuery){

        notifications = new ArrayList<>();
        progressBar = findViewById(R.id.nfs_progressBar);

        notifsListRV.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
        notifListAdapter = new NotificationsListItemAdapter(NotificationsActivity.this, notifications);
        notifListAdapter.setClickListener(this);
        notifsListRV.setAdapter(notifListAdapter);


        progressBar.setVisibility(View.VISIBLE);
        setUpProductListIni(pQuery, null,1, 10);
    }


    private void setUpProductListIni(final Query query, @Nullable final DocumentSnapshot startAfterSnapshot, @Nullable final int recursiveDepth, final int page_limit){

        Query locQuery = query;
        if(startAfterSnapshot!=null) {
            locQuery = locQuery.startAfter(startAfterSnapshot);
        }
        if(page_limit>0){
            locQuery = locQuery.limit(page_limit);
        }

        locQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                     QuerySnapshot snapshots = task.getResult();
                     if(snapshots.size()>0){
                         boolean isChange = false;
                         for (QueryDocumentSnapshot document : snapshots) {
                             CulfestNotification cur = document.toObject(CulfestNotification.class);
                             cur.setupNid(document.getId());
                             if(notifications.contains(cur)) {
                                 isChange = true;
                                 notifications.set(notifications.indexOf(cur), cur);
                             }else {
                                 notifications.add(cur);
                             }
                             notifListAdapter.notifyDataSetChanged();
                             progressBar.setVisibility(View.GONE);
                         }
                         if(snapshots.size()==page_limit&&!isChange){
                             if(recursiveDepth<50) {
                                 setUpProductListIni(query, snapshots.getDocuments().get(page_limit - 1), recursiveDepth + 1, page_limit);
                             }else{
//                                 setUpProductListIni(query, snapshots.getDocuments().get(page_limit - 1), recursiveDepth + 1, 0);
                             }
                         }else{
                             isNotificationSet = true;
                             notifications_count = notifications.size();
                         }
                     }else{
                         if(notifications.size()==0){
                             showNoticeAlert(NotificationsActivity.this, "Some Error Occurred", "Unable to retrieve notifications",true);
                         }else{
                             isNotificationSet = true;
                             notifications_count = notifications.size();
                         }
                     }
                }else{
                    showNoticeAlert(NotificationsActivity.this,"Error Occurred","Please try again later",true);
                }
            }
        });


    }

    @Override
    public void onNotificationItemClick(View view, int position) {

    }


    public static void showNoticeAlert(final Context context, String title, @javax.annotation.Nullable String msg, final boolean exit){
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title);
        if(msg!=null)builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
                if(exit){
                    if(context instanceof Activity){
                        ((Activity)context).finish();
                    }
                }
            }
        }).create().show();
    }
}
