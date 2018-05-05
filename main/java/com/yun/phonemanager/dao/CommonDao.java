package com.yun.phonemanager.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

public class CommonDao {
    private SharedPreferences sp;
    private static String Fullpath;
    private SQLiteDatabase db;
    // 初始化
    public CommonDao(Context context) {

        // 数据库的位置
        //String path = "data/data/com.jess.mobilesafe/files/commonnum.db";
        //String path = "com.xiaobin.myapplication2/com.jess.mobilesafe/files/commonnum.db";
        // 打开数据库的到数据库对象
        try{
            File file = new File(context.getFilesDir(),"commonnum.db");
            String path = file.getAbsolutePath();
            db = SQLiteDatabase.openDatabase(path, null,
                    SQLiteDatabase.OPEN_READONLY);
        }catch(Exception e){
            e.printStackTrace();
            //  File file = new File(context.getFilesDir(),"commonnum.db");
            // sp.edit().putString("path",file.getAbsolutePath()).commit();
        }




		/*
		File file = new File(context.getFilesDir(), "commonnum.db");

		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
*/

	/*	File f=context.getDatabasePath("commonnum.db").getParentFile();
		if(f.exists()==false)
		{
			f.mkdirs();
		}
		Fullpath=f.getPath()+"/commonnum.db";
		db = SQLiteDatabase.openDatabase(Fullpath, null,SQLiteDatabase.OPEN_READONLY);
		*/

    }
    /**
     * 查询一共有多少组
     * @return 返回组的总数
     */
    public int getGroupCount() {
        //查询一共有多少组
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from classlist",null);
        if (cursor.moveToFirst()) {
            //结果
            count = cursor.getInt(0);
        }
        //关闭游标
        cursor.close();
        //返回结果，没查出来就是0
        return count;
    }
    /**
     * 查询每组由多少个分组
     * @param groupPosition 组的位置
     * @return 每组的分组的总个数
     */
    public int getChildrenCount(int groupPosition){
        //每组一共有多少组
        int count = 0;
        int newgroupPosition = groupPosition+1;
        Cursor cursor = db.rawQuery("select count(*) from table"+newgroupPosition,null);
        if (cursor.moveToFirst()) {
            //结果
            count = cursor.getInt(0);
        }
        //关闭游标
        cursor.close();
        //返回结果，没查出来就是0
        return count;
    }
    /**
     * 查询每个组名
     * @param groupPosition 组的位置
     * @return 返回组名
     */
    public String getGroupName(int groupPosition){
        String name = "";
        int newgroupPosition = groupPosition+1;
        Cursor cursor = db.rawQuery("select name from classlist where idx = ?;",new String[]{newgroupPosition+""});
        if (cursor.moveToFirst()) {
            //结果
            name = cursor.getString(0);
        }
        //关闭游标
        cursor.close();
        //返回结果，没查出来就是空
        return name;
    }
    /**
     * 返回每个分组的内容
     * @param groupPosition 组的位置
     * @param childPosition 分组的位置
     * @return 返回分组内容
     */
    public String getChildContent(int groupPosition,int childPosition){
        String content = "";
        int newgroupPosition = groupPosition+1;
        int newchildPosition = childPosition+1;
        Cursor cursor = db.rawQuery("select name,number from table"+newgroupPosition+" where _id = ?;",new String[]{newchildPosition+""});
        if (cursor.moveToFirst()) {
            //结果
            content = "       "+cursor.getString(0)+"\n       "+cursor.getString(1);
        }
        //关闭游标
        cursor.close();
        //返回结果，没查出来就是空
        return content;
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
