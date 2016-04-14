package com.likemessage.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.likemessage.common.LConstants;

import java.util.ArrayList;
import java.util.List;

import in.co.madhur.chatbubblesdemo.model.ChatMessage;

/**
 * Created by wangkai on 2016/4/13.
 */
public class DBUtil {

    private static DBUtil dbUtil = null;

    public static DBUtil getDbUtil(){
        return dbUtil;
    }

    public static void init(Context context){
        dbUtil = new DBUtil(context);
    }

    private DBOpenHelper dbOpenHelper;

    public DBUtil(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context, "dbtest.db", null, 1);
    }

    public void save(LMessage message) {// 插入记录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_message (fromNo,toNo,msgDate,message,lmID,isSend,deleted) values(?,?,?,?,?,?,?)",
                new Object[] { message.getFromNo(), message.getToNo(),message.getMsgDate(),message.getMessage(),message.getLmID(),message.isSend(),0 });
        db.close();// 记得关闭数据库操作
    }

    public void delete(Integer id) {// 删除纪录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from t_message where id=?", new Object[] { id });
        db.close();
    }

    public void update(LMessage message) {// 修改纪录
//        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//        db.execSQL("update t_message set username=?,pass=? where" + " id=?", new Object[] { message.getUsername(), message.getPass(), message.getId() });
//        db.close();
        throw new RuntimeException("update");
    }

    public LMessage find(Integer id) {// 根据ID查找纪录
        LMessage message = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_message where id=?", new String[] { id.toString() });
        if (cursor.moveToFirst()) {// 依次取出数据
            message = createLMessage(cursor);
        }
        db.close();
        return message;
    }

    public List<LMessage> findAll() {// 查询所有记录
        List<LMessage> lists = new ArrayList<LMessage>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // Cursor cursor=db.rawQuery("select * from t_users limit ?,?", new
        // String[]{offset.toString(),maxLength.toString()});
        // //这里支持类型MYSQL的limit分页操作

        Cursor cursor = db.rawQuery("select * from t_message ", null);
        while (cursor.moveToNext()) {
            LMessage message = createLMessage(cursor);
            lists.add(message);
        }
        db.close();
        return lists;
    }

    private LMessage createLMessage(Cursor cursor){
        LMessage message = new LMessage();
        message.setId(cursor.getInt(cursor.getColumnIndex("id")));
        message.setFromNo(cursor.getString(cursor.getColumnIndex("fromNo")));
        message.setLmID(cursor.getString(cursor.getColumnIndex("lmID")));
        message.setMessage(cursor.getString(cursor.getColumnIndex("message")));
        message.setMsgDate(cursor.getLong(cursor.getColumnIndex("msgDate")));
        message.setSend(cursor.getInt(cursor.getColumnIndex("isSend")));
        message.setToNo(cursor.getString(cursor.getColumnIndex("toNo")));
        message.setDeleted(cursor.getInt(cursor.getColumnIndex("deleted")));
        return message;
    }

    public long getCount() {//统计所有记录数
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from t_message ", null);
        cursor.moveToFirst();
        db.close();
        return cursor.getLong(0);
    }


    public void saveMsg(ChatMessage message,String fromNo, String toNo){

        LMessage lMessage = new LMessage();
        lMessage.setFromNo(fromNo);
        lMessage.setToNo(toNo);
        lMessage.setSend(message.isSend() ? 1 : 0);
        lMessage.setMsgDate(message.getMessageTime());
        lMessage.setMessage(message.getMessageText());
        lMessage.setLmID("none");
        save(lMessage);

    }


    public List<LMessage> findTop(Integer limit) {// 根据ID查找纪录
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<LMessage> lists = new ArrayList<LMessage>();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_message order by msgDate desc limit 0,?", new String[] { limit.toString() });
        while (cursor.moveToNext()) {
            LMessage message = createLMessage(cursor);
            lists.add(message);
        }
        db.close();
        return lists;
    }

    public List<LMessage> findChat(String toNo){

        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<LMessage> lists = new ArrayList<LMessage>();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_message where toNo = ? or fromNo = ?", new String[] { toNo,toNo });
        while (cursor.moveToNext()) {
            LMessage message = createLMessage(cursor);
            lists.add(message);
        }
        db.close();
        return lists;
    }



}
