package com.e_eduspace.pointgather;

import com.e_eduspace.sellib.entity.TickedPoint;
import com.newchinese.coolpensdk.entity.NotePoint;

/**
 * Created by Administrator on 2017/11/1.
 */

public class PointBean extends NotePoint implements TickedPoint {
    private Long id;
    private Long sid;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getSid() {
        return sid;
    }

    @Override
    public void setSid(Long sid) {
        this.sid = sid;
    }

    @Override
    public Object newInstance() {
        return new PointBean();
    }

    public static TickedPoint getTickedPoint(NotePoint point) {
        TickedPoint ticked = new PointBean();
        ticked.setFirstPress(point.getFirstPress());
        ticked.setPageIndex(point.getPageIndex());
        ticked.setPointType(point.getPointType());
        ticked.setPress(point.getPress());
        ticked.setPX(point.getPX());
        ticked.setPY(point.getPY());
        return ticked;
    }
}
