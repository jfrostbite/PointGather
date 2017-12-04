package com.e_eduspace.pointgather;

import com.e_eduspace.ticked.entity.TickedPoint;
import com.e_eduspace.ticked.entity.TickedStroke;
import com.newchinese.coolpensdk.entity.NoteStroke;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/1.
 */

public class StrokeBean extends NoteStroke implements TickedStroke {

    private Long id;
    private Integer pageIndex;
    private List<? extends TickedPoint> pointList;
    private List<? extends TickedPoint> mPoints;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getPageIndex() {
        return pageIndex;
    }

    @Override
    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    @Override
    public List<? extends TickedPoint> getPointList() {
        return mPoints;
    }

    @Override
    public void setPointList(List<? extends TickedPoint> points) {
        mPoints = points;
    }

    @Override
    public void resetPointList() {
        pointList = new ArrayList<>();
    }

    @Override
    public void delete() {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void update() {

    }
}
