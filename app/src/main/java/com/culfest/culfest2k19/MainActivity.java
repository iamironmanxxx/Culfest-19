package com.culfest.culfest2k19;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_GENERAL = "notification_general";
    TextInputEditText titleEt, msgEt;
    Button sendBtn;

    SharedPreferences sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titleEt = findViewById(R.id.notif_title_et);
        msgEt = findViewById(R.id.notif_msg_et);
        sendBtn = findViewById(R.id.notif_send_btn);

        sPrefs =  this.getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
        
        if(!sPrefs.getBoolean(NOTIFICATION_GENERAL,false)){
            FirebaseMessaging.getInstance().subscribeToTopic("general")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                SharedPreferences.Editor editor;

                                editor = sPrefs.edit();
                                editor.putBoolean(NOTIFICATION_GENERAL, true);

                                editor.apply();
                            }
                        }
                    });

        }


    }

    public void sendNotifBtnOnClick(View view) {
        CollectionReference notifsRef = FirebaseFirestore.getInstance().collection("notifications");


        if(titleEt.getText().length()==0) titleEt.setError("This is required");
        else if(msgEt.getText().length()==0) msgEt.setError("This is required");
        else{
            CulfestNotification notif = new CulfestNotification();

            notif.setMsg(msgEt.getText().toString());
            notif.setTitle(titleEt.getText().toString());

            notifsRef.add(notif).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Toast.makeText(MainActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_notifs:
                startActivity(new Intent(MainActivity.this,NotificationsActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

}
