package com.likemessage.database;

/**
 * Created by wangkai on 2016/4/13.
 */
public class LMessage {

    private int id = 0;

    private String toNo = null;

    private String fromNo = null;

    private long msgDate = 0;

    private String message = null;

    private int isSend = 0;

    private String lmID = null;

    private int deleted = 0;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToNo() {
        return toNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public String getFromNo() {
        return fromNo;
    }

    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    public long getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(long msgDate) {
        this.msgDate = msgDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int isSend() {
        return isSend;
    }

    public void setSend(int isSend) {
        this.isSend = isSend;
    }

    public String getLmID() {
        return lmID;
    }

    public void setLmID(String lmID) {
        this.lmID = lmID;
    }

    public int isDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
