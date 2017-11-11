package com.e_eduspace.pointgather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.newchinese.coolpensdk.constants.PointType;
import com.newchinese.coolpensdk.entity.NotePoint;
import com.newchinese.coolpensdk.entity.NoteStroke;
import com.newchinese.coolpensdk.listener.OnConnectListener;
import com.newchinese.coolpensdk.listener.OnKeyListener;
import com.newchinese.coolpensdk.listener.OnLeNotificationListener;
import com.newchinese.coolpensdk.listener.OnPointListener;
import com.newchinese.coolpensdk.manager.BluetoothLe;
import com.newchinese.coolpensdk.manager.DrawingboardAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/11/1.
 */

public abstract class TopActivity extends AppCompatActivity implements BluAction.OnItemClickListener, ActionProvider.SubUiVisibilityListener{

    private String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private String EXTERNOR_STROGE_READ = Manifest.permission.READ_EXTERNAL_STORAGE;
    private String EXTERNOR_STROGE_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String ACTION_BLU[] = {BluetoothDevice.ACTION_FOUND, BluetoothDevice.ACTION_ACL_CONNECTED, BluetoothDevice.ACTION_ACL_DISCONNECTED, BluetoothDevice.ACTION_PAIRING_REQUEST};
    private final String CONFIG_JSON = "config.json";
    protected final String CONFIG_PATH = "identify_config";

    private final int BLU_ENABLE = 0x02;
    private Toolbar mToolbar;
    private BluAction mBluAction;
    protected BluetoothLe mLe;
    protected DrawingboardAPI mAPI;
    private MenuItem mItem;
    private boolean mDown;
    protected Gson mGson;


    protected ExecutorService mExecutor;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                String msg = "";
                String action = intent.getAction();
                switch (action) {
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (mBluAction != null) {
                            mBluAction.add(device);
                        }
                        return;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        msg = "设备已连接";
                        if (mItem != null) {
                            mItem.setIcon(R.mipmap.pen_succ_nor);
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        msg = "设备已断开";
                        if (mItem != null) {
                            mItem.setIcon(R.mipmap.pen_break_nor);
                        }
                        break;
                    case BluetoothDevice.ACTION_PAIRING_REQUEST:
                        msg = "正在连接...";
                        break;
                    default:
                        return;
                }
                Toast.makeText(TopActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbar = findViewById(R.id.my_toolbar);

        setSupportActionBar(mToolbar);

        mGson = new GsonBuilder().serializeNulls().create();

        mExecutor = Executors.newSingleThreadExecutor();

        checkPer();

        registBroadcast();

        initSDK();
    }

    private void config() {
        File path = new File(Environment.getExternalStorageDirectory(), CONFIG_PATH);
        if (!path.exists()) {
            if (!path.mkdirs()) {
                throw new RuntimeException("初始化失败，请检查目录或手动创建后运行程序。");
            }
        }
        File file = new File(path, CONFIG_JSON);

        if (!file.exists()) {
//            throw new RuntimeException("配置文件不存在，请手动创建配置文件后运行程序");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            //解析数据
            analysis(mGson, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected abstract void analysis(Gson gson, String str);

    private void initSDK() {
        //蓝牙相关
        mLe = BluetoothLe.getDefault();
        mLe.init(this);
        mLe.setOnLeNotificationListener(mNotification);
        mLe.setOnConnectListener(mConnectListener);
        mLe.setOnKeyListener(mKeyListener);

        //书写相关
        mAPI = DrawingboardAPI.getInstance();
        mAPI.init(this, "1308e911d0841bf20922d075dfaab229");
        mAPI.setBookSize(155, 215);
//        mAPI.setBaseOffset(0, -432);
    }

    private void registBroadcast() {
        IntentFilter filter = new IntentFilter();
        for (String action : ACTION_BLU) {
            filter.addAction(action);
        }
        registerReceiver(mReceiver, filter);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        MenuItem item = menu.findItem(R.id.item_blu);
        mBluAction = (BluAction) MenuItemCompat.getActionProvider(item);
        mBluAction.setSubMenuVisibility(true)
                .setOnItemClickListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_blu:
                mItem = item;
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {//蓝牙打开
                    BluetoothAdapter.getDefaultAdapter().startDiscovery();
                } else {//蓝牙未打开i啊
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLU_ENABLE);
                }
                break;
        }
        return true;
    }

    /**
     * 检查权限
     */
    private void checkPer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//未授权
                //检查是否拒绝权限
                //true 申请过权限但是用户拒绝,这里重新申请权限
                //false 申请过权限，但是用户拒绝，并且勾选不再提示，源码显示，系统版本错误反悔false，此处提示用户进入设置界面设置权限
                Toast.makeText(this, ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION) ? "可申请权限" : "权限被禁止", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, EXTERNOR_STROGE_READ, EXTERNOR_STROGE_WRITE}, 0x01);
            } else {
                config();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0x01) {
            int denied = PackageManager.PERMISSION_DENIED;
            boolean failure = false;
            for (int grantResult : grantResults) {
                if (denied == grantResult) {
                    failure = true;
                }
            }
            Toast.makeText(this, !failure ? "授权成功" : "未授权，系统运行状态异常", Toast.LENGTH_SHORT).show();
            if (!failure) {
                config();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClick(BluetoothDevice device) {
        Toast.makeText(this, "连接设备:" + device.getAddress(), Toast.LENGTH_LONG).show();
        //连接设备，首先停止扫描
        if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }
        mLe.connectBleDevice(device);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLU_ENABLE) {
            Toast.makeText(this, resultCode == RESULT_OK ? "蓝牙已开启" : "蓝牙状态异常", Toast.LENGTH_SHORT).show();
            BluetoothAdapter.getDefaultAdapter().startDiscovery();
        }
    }

    private AlertDialog mAlertDialog;
    /**
     * sdk监听器
     */
    private OnLeNotificationListener mNotification = new OnLeNotificationListener() {
        @Override
        public void onReadHistroyInfo() {

        }

        @Override
        public void onHistroyInfoDetected() {
            //有历史信息 删除历史信息
            ProgressDialog.Builder builder = new ProgressDialog.Builder(TopActivity.this);
            mAlertDialog = builder.setMessage("History information is detected, deleting...").create();
            mAlertDialog.show();
            Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                @Override
                public void run() {
                    System.out.println("发送清空电量监听");
                    mLe.sendBleInstruct(BluetoothLe.EMPTY_STORAGE_DATA);
                }
            }, 3000, TimeUnit.MILLISECONDS);
        }

        @Override
        public void onHistroyInfoDeleted() {
            //历史信息情况完毕
            mAlertDialog.dismiss();
            mAlertDialog = null;
            openWritedChannel();
        }
    };

    protected void openWritedChannel(){}

    private OnConnectListener mConnectListener = new OnConnectListener() {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected() {
        }

        @Override
        public void onFailed(int i) {

        }

        @Override
        public void isConnecting() {

        }
    };
    private OnKeyListener mKeyListener = new OnKeyListener() {
        @Override
        public void onKeyGenerated(String s) {
            mLe.setKey(s);
        }

        @Override
        public void onSetLocalKey() {

        }
    };

    protected OnPointListener mPointListener = new OnPointListener() {
        @Override
        public void onStrokeCached(int i, NoteStroke noteStroke) {

        }

        @Override
        public void onPointCatched(int i, final NotePoint notePoint) {
            if (notePoint.getPointType() == PointType.TYPE_DOWN) {
                onPointDown(notePoint);
                mDown = true;
            } else if (notePoint.getPointType() == PointType.TYPE_MOVE) {
                onPointMove(notePoint);
            } else if (mDown && notePoint.getPointType() == PointType.TYPE_UP) {
                onPointUp(notePoint);
                mDown = false;
            }
        }

        /**
         * 采集点系统自动化思想：
         * 改变当前页棉猴检测是否影响当前正在操作的页面，如果出现跳页，提示用户该操作是否为系统bug导致
         * 该操作交给用户进行决定
         * @param i
         * @param notePoint
         */
        @Override
        public void onPageIndexChanged(int i, final NotePoint notePoint) {

        }
    };

    protected abstract void onPointDown(NotePoint notePoint);
    protected abstract void onPointMove(NotePoint notePoint);
    protected abstract void onPointUp(NotePoint notePoint);


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    /**
     * 扫描蓝牙设备列表显示隐藏监听
     *
     * @param isVisible
     */
    @Override
    public void onSubUiVisibilityChanged(boolean isVisible) {
        if (isVisible) {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {//蓝牙打开
                BluetoothAdapter.getDefaultAdapter().startDiscovery();
            } else {//蓝牙未打开i啊
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLU_ENABLE);
            }
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isDiscovering()) {
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            }
        }
    }
}
