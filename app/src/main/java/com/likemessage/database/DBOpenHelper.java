package com.likemessage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gifisan.nio.common.Logger;
import com.gifisan.nio.common.LoggerFactory;

/**
 * Created by wangkai on 2016/4/13.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    private Logger logger = LoggerFactory.getLogger(DBOpenHelper.class);

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// 覆写onCreate方法，当数据库创建时就用SQL命令创建一个表

        String sql = "create table t_message(messageID integer primary key autoincrement,toUserID INT8,fromUserID INT8,msgDate INT8,msgType INT8,message varchar(255),isSend BOOLEAN,deleted BOOLEAN )";

        logger.info("_________________________________create table:{}",sql);

        db.execSQL(sql);

        logger.info("_________________________________created table:{}",sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }
}
