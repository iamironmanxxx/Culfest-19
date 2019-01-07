package com.culfest.culfest2k19;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Date;

public class NotificationsActivity extends AppCompatActivity implements NotificationsListItemAdapter.NotificationItemClickListener{


    private static final long QUERY_LIMIT = 6;
    private String lastFetchedUID="";
    private FloatingActionButton fab;
    RecyclerView notifsListRV;
    ProgressBar progressBar;

    CollectionReference notifsRef;
    ArrayList<CulfestNotification> notifications;
    int notifications_count;
    NotificationsListItemAdapter notifListAdapter;

    boolean isNotificationSet;
    private Date lastFetchedTime;
    private int lastSize=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        notifsRef = FirebaseFirestore.getInstance().collection("notifications");

        notifsListRV = findViewById(R.id.notifications_list_rv);

        fab=findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query=getQuery();
                setupNotifsList2(query);
            }
        });

        Query query = getQuery();
        initRecycler();
        setupNotifsList2(query);
    }

    private Query getQuery() {
        Query query;
        if(!lastFetchedUID.equals(""))
            query=notifsRef.orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastFetchedTime).limit(QUERY_LIMIT);
        else
            query=notifsRef.orderBy("timestamp",Query.Direction.DESCENDING).limit(QUERY_LIMIT);
        return query;
    }
    private void initRecycler()
    {
        notifications = new ArrayList<>();
        progressBar = findViewById(R.id.nfs_progressBar);

        notifsListRV.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this));
        notifListAdapter = new NotificationsListItemAdapter(NotificationsActivity.this, notifications);
        notifListAdapter.setClickListener(this);
        notifsListRV.setAdapter(notifListAdapter);


        progressBar.setVisibility(View.VISIBLE);
    }

    public void setupNotifsList2(Query locQuery){

        locQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    fab.setVisibility(View.VISIBLE);
                    QuerySnapshot snapshots = task.getResult();
                    if(snapshots.size()>0){
                        boolean isChange = false;
                        for (QueryDocumentSnapshot document : snapshots) {
                            lastFetchedUID=document.getId();
                            Date currentTime=(Date) document.get("timestamp");
                            lastFetchedTime=currentTime;
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
                    }else{
                        if(notifications.size()==0){
                            showNoticeAlert(NotificationsActivity.this, "Some Error Occurred", "Unable to retrieve notifications",true);
                        }else{
                            isNotificationSet = true;
                            fab.setVisibility(View.GONE);
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
