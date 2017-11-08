package com.e_eduspace.sellib;

import android.support.annotation.NonNull;
import android.util.Log;

import com.e_eduspace.sellib.db.TickedDB;
import com.e_eduspace.sellib.entity.TickedPoint;
import com.e_eduspace.sellib.entity.TickedStroke;
import com.e_eduspace.sellib.entity.TickedTag;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/10/31.
 * 接受点线集合，直接操作缓存，不做本地处理
 */

public class Ticked {

    private ExecutorService mExecutor;
    private TickedDB mDB;
    //补偿点
    private float[] mCompensation = new float[2];
    //配置文件
    private List<TickedTag> mTags;
    private OnTickedListener mListener;
    //题集
    private List<TickedTag> mQues = new ArrayList<>();

    private TickedInterceptor<String, List<TickedTag>> mSaltInterceptor;
    private long mFirstTime;

    private Ticked(Builder builder) {
        mSaltInterceptor = builder.mSaltInterceptor;
        mExecutor = Executors.newSingleThreadExecutor();
        config();

    }

    /**
     * 获取平均点
     */
    private TickedPoint validPoint(List<? extends TickedPoint> notePoints) {
        if (notePoints == null || notePoints.isEmpty()) {
            return null;
        }
        TickedPoint tickedPointEntity = (TickedPoint) notePoints.get(0).newInstance();
//        notePoints = notePoints.subList(notePoints.size() / 3, notePoints.size());
        int size = notePoints.size()/* > 10 ? 10 : notePoints.size()*/;
        float[] pxs = new float[size];
        float[] pys = new float[size];
        float sumX = 0f;
        float sumY = 0f;
        for (int i = 0; i < size; i++) {
            int anInt = new Random().nextInt(notePoints.size());
            pxs[i] = notePoints.get(anInt).getPX();
            pys[i] = notePoints.get(anInt).getPY();
        }

        for (float px : pxs) {
            sumX += px;
        }
        for (float py : pys) {
            sumY += py;
        }

        tickedPointEntity.setPX(sumX / size - mCompensation[0]);
        tickedPointEntity.setPY(sumY / size - mCompensation[1]);
        return tickedPointEntity;
    }

    /**
     * 配置文件
     */
    private Ticked config(List<TickedTag> config) {
        mTags = config;
        return this;
    }

    /**
     * 配置文件
     */
    private void config() {

        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] bytes = openAssets(getClass().getResourceAsStream(Constants.CONFIG_PATH + Constants.CONFIG_JSON));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes)));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    if (mSaltInterceptor != null) {
                        //解析数据
                        config(mSaltInterceptor.salt(sb.toString().split(Constants.CONFIG_SPLIT)));
                    }

                    //配置数据库文件
                    File db = new File(Constants.CONFIG_SD_PATH);
                    if (!db.exists()) {
                        if (!db.mkdirs()) {
                            throw new FileNotFoundException(Constants.CONFIG_PATH + " create failed");
                        }
                    }
                    File file = new File(Constants.CONFIG_SD_DB);
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            throw new FileNotFoundException(Constants.CONFIG_DB + " create failed");
                        }
                    }
                    //强行更新文件
                    FileOutputStream fos = new FileOutputStream(file);
                    bytes = openAssets(getClass().getResourceAsStream(Constants.CONFIG_PATH + Constants.CONFIG_DB));
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    int len = -1;
                    byte[] temp = new byte[1024];
                    while ((len = bais.read(temp)) > -1) {
                        fos.write(temp, 0, len);
                    }
                    bais.close();
                    fos.close();
                    mDB = new TickedDB(file.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TICKED", e.getMessage());
                    if (mListener != null) {
                        mListener.onError("配置文件加载失败，"+e.getMessage());
                    }
                }
            }
        });
    }

    private byte[] openAssets(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int len = -1;
            byte[] bytes = new byte[1024];
            while ((len = is.read(bytes)) > -1) {
                baos.write(bytes, 0, len);
            }
            is.close();
            baos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Ticked dispose(@NonNull final TickedStroke stroke) {
        if (mFirstTime == 0) {
            mFirstTime = System.currentTimeMillis();
        }
        mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                List<? extends TickedPoint> points = stroke.getPointList();
                TickedPoint point = validPoint(points);
                if (mDB != null && mTags != null) {
                    TickedTag result = null;
                    for (TickedTag tag : mTags) {
                        result = mDB.query(point, tag.title, String.valueOf(tag.page));
                        if (result != null) {//匹配到结果
                            break;
                        }
                    }
                    if (result != null) {//匹配到结果
                        match(result);
                    } else {
                        if (mListener != null) {
                            mListener.onError("请在制定区域作答");
                        }
                    }
                } else {
                    if (mListener != null) {
                        mListener.onError("SD卡读写权限错误");
                    }
                }
            }
        });
        return this;
    }

    /**
     * 实时匹配
     * 10     115    41  121
     * @param result
     */
    private void match(TickedTag result) {
        if (Constants.SUBMIT_FLAG == result.loc) {
            if (mListener != null) {
                mListener.onCompleted(mQues, System.currentTimeMillis() - mFirstTime);
                clean();
            }
            return;
        }
        //查找该题目是否存在，如果存在更新答案即可
        int index = mQues.indexOf(result);
        if (index > -1) {
            mQues.get(index).loc = result.loc;
            result = mQues.get(index);
        }
        //作答
        result.reply(index > -1);
        if (index < 0) {//
            mQues.add(result);
        }
        if (mListener != null) {
            mListener.onTicked(result);
        }
    }

    public void clean() {
        mQues.clear();
        mFirstTime = 0;
    }

    public void disCharge (){
        clean();
        if (mExecutor != null && !mExecutor.isShutdown()) {
            mExecutor.shutdown();
        }
    }

    public void setOnTickedLisener(OnTickedListener listener) {
        mListener = listener;
    }

    public interface OnTickedListener {
        void onTicked(TickedTag tag);

        void onError(String msg);

        void onCompleted(List<TickedTag> tags, long l);
    }

    /**
     * 配置
     */
    public static class Builder {

        private TickedInterceptor<String, List<TickedTag>> mSaltInterceptor;

        public Builder addSalt(TickedInterceptor<String, List<TickedTag>> interceptor) {
            mSaltInterceptor = interceptor;
            return this;
        }

        public Ticked build() {
            return new Ticked(this);
        }
    }
}
