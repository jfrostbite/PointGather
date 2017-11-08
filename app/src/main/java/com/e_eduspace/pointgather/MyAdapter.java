package com.e_eduspace.pointgather;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e_eduspace.identify.entity.PointAreaEntity;

import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

    private Context mContext;
    private List<PointAreaEntity> mDatas;

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item, parent, false);
//        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (position == 0) {
            holder.setTitle("ID", "区域", "位置", "minX", "minY", "maxX" , "maxY", "页码");
        } else {
            holder.setData(mDatas.get(position - 1));
        }
    }

    public void updateData(List<PointAreaEntity> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 1 : mDatas.size() + 1;
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private int[] mTvResId = {R.id.tv_id, R.id.tv_tag, R.id.tv_loc, R.id.tv_p1_x, R.id.tv_p1_y, R.id.tv_p2_x, R.id.tv_p2_y, R.id.tv_page};
        private TextView[] mTvs = new TextView[mTvResId.length];
        public MyHolder(View itemView) {
            super(itemView);
            for (int j = 0; j < mTvResId.length; j++) {
                mTvs[j] = itemView.findViewById(mTvResId[j]);
                mTvs[j].setGravity(Gravity.CENTER);
            }
        }

        public void setData(PointAreaEntity pae) {
            mTvs[0].setText(String.valueOf(pae.getId()));
            mTvs[1].setText(String.valueOf(pae.getTag()));
            mTvs[2].setText(String.valueOf(pae.getLoc()));
            mTvs[3].setText(String.valueOf(pae.getMinX()));
            mTvs[4].setText(String.valueOf(pae.getMinY()));
            mTvs[5].setText(String.valueOf(pae.getMaxX()));
            mTvs[6].setText(String.valueOf(pae.getMaxY()));
            mTvs[7].setText(String.valueOf(pae.getPageIndex()));
        }

        public void setTitle(String... titles) {
            for (int i = 0; i < mTvs.length; i++) {
                mTvs[i].setText(titles[i]);
            }
        }
    }
}
