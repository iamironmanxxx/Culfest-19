package com.culfest.culfest2k19;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Pankaj Vaghela on 28-10-2018.
 */
public class CulfestNotification {

    String nid;
    String title;
    String msg;

    @ServerTimestamp
    Date timestamp;



    public  CulfestNotification(){}

    public String fetchNid() { return nid; }
    public void setupNid(String nid) { this.nid = nid; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CulfestNotification)) return false;
        CulfestNotification that = (CulfestNotification) o;
        return Objects.equals(nid, that.nid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nid);
    }
}
