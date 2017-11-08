package com.e_eduspace.pointgather;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ActionProvider;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/10/19.
 */

public class BluAction extends ActionProvider implements MenuItem.OnMenuItemClickListener {

    private TextView mView;
    private final String BLU_DEV = "bluDev";
    private SubMenu mSubMenu;
    private OnItemClickListener mListener;
    private boolean mVisibility;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public BluAction(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public boolean hasSubMenu() {
        return mVisibility;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        mSubMenu = subMenu;
        subMenu.clear();
        subMenu.add("设备列表").setEnabled(false);
    }

    /**
     * 添加项目
     * 利用Intent 传递蓝牙设备
     * @param device
     * @return
     */
    public void add(BluetoothDevice device) {
        if (device == null) {
            throw new NullPointerException("device is null");
        }
        if (mSubMenu != null) {
            Intent intent = new Intent();
            intent.putExtra(BLU_DEV, device);
            mSubMenu.add(TextUtils.isEmpty(device.getName()) ? "Unknown" : device.getName()).setIntent(intent).setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (mListener != null) {
            mListener.onItemClick((BluetoothDevice) item.getIntent().getParcelableExtra(BLU_DEV));
        }
        return true;
    }

    public BluAction setSubMenuVisibility(boolean visibility) {

        mVisibility = visibility;
        return this;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice device);
    }
}
