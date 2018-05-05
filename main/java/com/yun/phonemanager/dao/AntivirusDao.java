package com.yun.phonemanager.dao;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class AntivirusDao {
    private static String Fullpath;
    SQLiteDatabase db;
    // 初始化
    public AntivirusDao() {
        // 数据库的位置
        String path = "data/data/com.jess.mobilesafe/files/antivirus.db";
        // 打开数据库的到数据库对象
        //db = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READONLY);
    }
    public String query(String md5, Context context){
        String desc=null;
        File f=context.getDatabasePath("antivirus.db").getParentFile();
        if(f.exists()==false)
        {
            f.mkdirs();
        }
        Fullpath=f.getPath()+"/antivirus.db";
        db = SQLiteDatabase.openDatabase(Fullpath, null,SQLiteDatabase.OPEN_READONLY);
        //？为占位符
        Cursor cursor = db.rawQuery("select desc from datable where md5 = ?", new String[]{md5});
        if (cursor.moveToNext()) {
            desc = cursor.getString(0);

        }
        return desc;
    }
    /**
     * 关闭数据库释放资源
     */
    public void close(){
        if (db!=null) {
            db.close();
        }
    }
}
