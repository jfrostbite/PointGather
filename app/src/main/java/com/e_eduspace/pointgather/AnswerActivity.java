package com.e_eduspace.pointgather;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.e_eduspace.sellib.Ticked;
import com.e_eduspace.sellib.TickedInterceptor;
import com.e_eduspace.sellib.entity.Question;
import com.e_eduspace.sellib.entity.TickedPoint;
import com.e_eduspace.sellib.entity.TickedStroke;
import com.e_eduspace.sellib.entity.TickedTag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.coolpensdk.manager.DrawingBoardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 解决up点的问题，计时器超时判断
 */
public class AnswerActivity extends TopActivity implements Ticked.OnTickedListener {

    private DrawingBoardView mBoardView;

    //题集
    private List<ChoiceQuestion> mQues = Collections.emptyList();
    //当前书写线
    private TickedStroke currentStroke;
    //当前点集
    private List<TickedPoint> currentPoints;
    private Ticked mTicked;
    //重选标记
    private final int REELECT = 5;
    private TickedTag mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_answer);
        initView();
        super.onCreate(savedInstanceState);
        mAPI.setBookSize(155, 210);
        //打开书写通道
        mAPI.setOnPointListener(mPointListener);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

            }
        });

        initTicked();
    }

    private void initTicked() {
        Ticked.Builder builder = new Ticked.Builder();
        mTicked = builder.addSalt(new TickedInterceptor<String, List<TickedTag>>() {
            @Override
            public List<TickedTag> salt(String... conStr) {
                List<TickedTag> tickedTags = mGson.fromJson(conStr[1], new TypeToken<List<Question>>() {
                }.getType());
                return tickedTags;
            }
        }).addListener(this).build();
    }

    private void initView() {
        mBoardView = findViewById(R.id.dbv);
    }

    @Override
    protected void analysis(Gson gson, String str) {
    }

    @Override
    protected void onPointDown(final NotePoint notePoint) {
        mBoardView.drawLine(notePoint);
        mExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {

                return null;
            }
        });
        currentStroke = new StrokeBean();
        currentStroke.setPageIndex(notePoint.getPageIndex());

        currentPoints = new ArrayList<>();
        currentPoints.add(PointBean.getTickedPoint(notePoint));
    }

    @Override
    protected void onPointMove(final NotePoint notePoint) {
        mBoardView.drawLine(notePoint);
        mExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        });
        currentPoints.add(PointBean.getTickedPoint(notePoint));
    }

    @Override
    protected void onPointUp(NotePoint notePoint) {
        Toast.makeText(this, "抬起点", Toast.LENGTH_SHORT).show();
        mExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return null;
            }
        });
        currentStroke.setPointList(currentPoints);
        mTicked.dispose(currentStroke);

    }

    @Override
    protected void openWritedChannel() {
        super.openWritedChannel();
        Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("发送书写通道");
                mLe.sendBleInstruct(BluetoothLe.OPEN_WRITE_CHANNEL);
            }
        }, 3000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onTicked(TickedTag tag) {
        mTag = tag;
        List<String> answer = mTag.answer();
        Log.e("AnswerActivity", new GsonBuilder().serializeNulls().create().toJson(answer));
    }

    @Override
    public void onError(String msg) {
        Log.e("AnswerActivity", msg);
    }

    @Override
    public void onCompleted(List<TickedTag> tags, long l) {
        for (TickedTag tag : tags) {
            for (String s : tag.answer()) {
                Log.e("答案", s);
            }
        }
//        Log.e("AnswerActivity", new GsonBuilder().serializeNulls().create().toJson(mTag));
        Log.e("AnswerActivity", new GsonBuilder().serializeNulls().create().toJson(tags));
        Log.e("AnswerActivity", "答题时间："+l);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTicked != null) {
            mTicked.disCharge();
        }
    }
}
