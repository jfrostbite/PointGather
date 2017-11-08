package com.e_eduspace.pointgather;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.e_eduspace.identify.Constants;
import com.e_eduspace.identify.IDentifyMulti;
import com.e_eduspace.identify.entity.FormPage;
import com.e_eduspace.identify.entity.PointAreaEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.manager.BluetoothLe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends TopActivity {

    private int mRecog = -1;//采集点类型 0，不参与识别；1，参与识别
    //当前任务标记（单位：页）
    private int mTaskPage = -1;

    private IDentifyMulti mBuild;
    private List<FormPage> mFormPage;
    private RecyclerView mRv;
    private MyAdapter mAdapter;
    private List<PointAreaEntity> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        initView();
        super.onCreate(savedInstanceState);
        mAPI.setBookSize(109, 153);
        initIdentify();
        registObserver();
    }

    private void initView() {
        mRv = findViewById(R.id.rv_list);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MyAdapter();
        mRv.setAdapter(mAdapter);
    }

    private void initIdentify() {
        mBuild = new IDentifyMulti.Builder(this, null, true).build();
        //初始化最后录入状态
        mExecutor = Executors.newSingleThreadExecutor();
    }

    private void registObserver() {
        getContentResolver().registerContentObserver(Constants.POINT_URI, true, new ContentObserver(new Handler()) {
            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (Constants.POINT_URI_ADD.equals(uri)) {
                    PointAreaEntity pae = mBuild.queryLast(mRecog == 1);
                    if (pae != null) {
                        mList.add(pae);
                    } else {
                        mList.clear();
                    }
                    mAdapter.notifyDataSetChanged();
                    mRv.smoothScrollToPosition(mList.size());
                } else if (Constants.POINT_URI_NEW.equals(uri)) {
                    Toast.makeText(MainActivity.this, "当前页面已采集完毕", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 重置数据，切换采集类型，翻页
     */
    private void recover() {
        PointAreaEntity pae = mBuild.initialize(mFormPage.get(mTaskPage - 1)).queryLast(mRecog == 1);
        mBuild.recover(pae != null ? pae.getId() * 2 : 0);
    }

    public void gather(View view) {
        if (mFormPage == null) {
            Toast.makeText(this, "采集配置文件加载失败，请检查权限", Toast.LENGTH_LONG).show();
            return;
        }
        if (!mLe.getConnected()) {
            Toast.makeText(this, "设备未连接", Toast.LENGTH_LONG).show();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请选择采集类型：")
                .setMultiChoiceItems(getResources().getStringArray(R.array.type_item), new boolean[]{mRecog == 0, mRecog == 1}, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        mRecog = which;
                        if (mRecog != -1 && isChecked) {
                            //打开书写通道
                            mLe.sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
                            mAPI.setOnPointListener(mPointListener);
                            mList = mBuild.query(mRecog == 1);
                            mAdapter.updateData(mList);
                            dialog.dismiss();
                            recover();
                        } else {
                            Toast.makeText(MainActivity.this, "请选择采集类型", Toast.LENGTH_LONG).show();
                        }
                    }
                }).create();
        builder.show();
    }

    public void saveAs(View view) {
        File srcDB = getDatabasePath(Constants.POINT_DB_NAME);
        File savePath = new File(Environment.getExternalStorageDirectory(), CONFIG_PATH);
        if (!savePath.exists()) {
            if (!savePath.mkdirs()) {
                Toast.makeText(this, "创建数据失败", Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (srcDB != null && srcDB.exists()) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                File tarDB = new File(savePath, Constants.POINT_DB_NAME);
                if (!tarDB.exists()) {
                    if (!tarDB.createNewFile()) {
                        Toast.makeText(this, "数据创建失败", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                fis = new FileInputStream(srcDB);
                fos = new FileOutputStream(tarDB);
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = fis.read(bytes)) > -1) {
                    fos.write(bytes, 0, len);
                }
                fis.close();
                fos.close();
                Toast.makeText(this, "导出成功", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "导出失败", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "未检测到采集数据", Toast.LENGTH_LONG).show();
        }
    }

    public void clean(View view) {
        if (mBuild != null) {
            if (mRecog != -1) {
                mBuild.clean(mRecog == 1);
            } else {
                mBuild.clean(true);
                mBuild.clean(false);
            }
        }
    }

    @Override
    protected void analysis(Gson gson, String str) {
        mFormPage = gson.fromJson(str, new TypeToken<List<FormPage>>() {
        }.getType());
        Log.e("TAG", str);
    }

    @Override
    protected void onPointDown(final NotePoint notePoint) {
        if (notePoint.getPageIndex() != mTaskPage) {//换页
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("警告：")
                    .setMessage("正在改变当前采集页面：\n\n系统记录页：第" + mTaskPage + "页，预改变页：第" + notePoint.getPageIndex() + "页？")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    //如果是系统bug导致的跳页，那么修复该bug点
                                    mBuild.addPoint(mRecog == 1, GatherPoint.getGatherPoint(new NotePoint(notePoint.getPX(), notePoint.getPY(), notePoint.getTestTime(), notePoint.getFirstPress(), notePoint.getPress(), mTaskPage, notePoint.getPointType())));
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mTaskPage = notePoint.getPageIndex();
                            mExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    recover();
                                }
                            });
                        }
                    })
                    .create()
                    .show();
        } else {
            mExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mBuild.addPoint(mRecog == 1, GatherPoint.getGatherPoint(notePoint));
                }
            });
        }
    }

    @Override
    protected void onPointMove(NotePoint notePoint) {

    }

    @Override
    protected void onPointUp(NotePoint notePoint) {

    }
}
