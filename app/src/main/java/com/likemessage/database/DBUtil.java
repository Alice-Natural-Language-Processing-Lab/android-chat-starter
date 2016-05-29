package com.likemessage.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.likemessage.bean.T_MESSAGE;

import java.util.ArrayList;
import java.util.List;

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

    public void save(T_MESSAGE message) {// 插入记录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作
        db.execSQL("insert into t_message (toUserID,fromUserID,msgDate,msgType,message,isSend,deleted) values(?,?,?,?,?,?,?)",
                new Object[] { message.getToUserID(), message.getFromUserID(),message.getMsgDate(),message.getMsgType(),message.getMessage(),message.isSend() ? 1 : 0,0 });
        db.close();// 记得关闭数据库操作
    }

    public void delete(Integer id) {// 删除纪录
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.execSQL("delete from t_message where id=?", new Object[] { id });
        db.close();
    }

    public void update(T_MESSAGE message) {// 修改纪录
//        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//        db.execSQL("update t_message set username=?,pass=? where" + " id=?", new Object[] { message.getUsername(), message.getPass(), message.getId() });
//        db.close();
        throw new RuntimeException("update");
    }

    public T_MESSAGE find(Integer id) {// 根据ID查找纪录
        T_MESSAGE message = null;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_message where id=?", new String[] { id.toString() });
        if (cursor.moveToFirst()) {// 依次取出数据
            message = createLMessage(cursor);
        }
        db.close();
        return message;
    }

    public List<T_MESSAGE> findAll() {// 查询所有记录
        List<T_MESSAGE> lists = new ArrayList<T_MESSAGE>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        // Cursor cursor=db.rawQuery("select * from t_users limit ?,?", new
        // String[]{offset.toString(),maxLength.toString()});
        // //这里支持类型MYSQL的limit分页操作

        Cursor cursor = db.rawQuery("select * from t_message ", null);
        while (cursor.moveToNext()) {
            T_MESSAGE message = createLMessage(cursor);
            lists.add(message);
        }
        db.close();
        return lists;
    }

    private T_MESSAGE createLMessage(Cursor cursor){
        T_MESSAGE message = new T_MESSAGE();
        message.setMessageID(cursor.getInt(cursor.getColumnIndex("messageID")));
        message.setToUserID(cursor.getInt(cursor.getColumnIndex("toUserID")));
        message.setFromUserID(cursor.getInt(cursor.getColumnIndex("fromUserID")));
        message.setMsgDate(cursor.getLong(cursor.getColumnIndex("msgDate")));
        message.setMsgType(cursor.getInt(cursor.getColumnIndex("msgType")));
        message.setMessage(cursor.getString(cursor.getColumnIndex("message")));
        message.setSend(cursor.getInt(cursor.getColumnIndex("isSend")) == 1);
        message.setDeleted(cursor.getInt(cursor.getColumnIndex("deleted")) == 1);
        return message;
    }

    public long getCount() {//统计所有记录数
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from t_message ", null);
        cursor.moveToFirst();
        db.close();
        return cursor.getLong(0);
    }


    public void saveMsg(T_MESSAGE message){
        save(message);
    }


    public List<T_MESSAGE> findTop() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<T_MESSAGE> lists = new ArrayList<T_MESSAGE>();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select t.* from (" +
                "select t1.messageID,t1.toUserID,t1.fromUserID,t1.msgDate,t1.msgType,t1.message,t1.isSend,t1.deleted from t_message t1 where t1.isSend = 1 " +
                "union all " +
                "select t2.messageID,t2.fromUserID as toUserID,t2.toUserID as fromUserID,t2.msgDate,t2.msgType,t2.message,t2.isSend,t2.deleted from t_message t2 where t2.isSend = 0 " +
                ")t group by t.toUserID order by t.msgDate desc",null);
        while (cursor.moveToNext()) {
            T_MESSAGE message = createLMessage(cursor);
            lists.add(message);
        }
        db.close();
        return lists;
    }

    public List<T_MESSAGE> findChat(Integer chatUserID){

        String _chatUserID = String.valueOf(chatUserID);

        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<T_MESSAGE> lists = new ArrayList<T_MESSAGE>();
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery("select * from t_message where toUserID = ? or fromUserID = ?", new String[] { _chatUserID,_chatUserID });
        while (cursor.moveToNext()) {
            T_MESSAGE message = createLMessage(cursor);
            lists.add(message);
        }
        db.close();
        return lists;
    }



}
