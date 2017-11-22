package com.e_eduspace.sellib.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;

import com.e_eduspace.sellib.entity.TickedPoint;
import com.e_eduspace.sellib.entity.TickedTag;

/**
 * Created by Administrator on 2017/10/31.
 */

public abstract class DB {

    private DBHelper mHelper;
    final SQLiteDatabase mSQLiteDatabase;

    public DB(Context context){
        mHelper = new DBHelper(context);
        mSQLiteDatabase = mHelper.getWritableDatabase();
    }

    public DB(String path) {
        mSQLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     * 查询点loc
     * @param tickedPoint
     * @return
     */
    public abstract TickedTag query(TickedPoint tickedPoint, String... values);

    /**
     * 获取提交范围
     * @param pageIndex
     * @return
     */
    public abstract RectF query(int pageIndex);
}
