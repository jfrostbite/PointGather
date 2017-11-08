package com.e_eduspace.pointgather;

import com.e_eduspace.identify.entity.PointEntity;
import com.newchinese.coolpensdk.entity.NotePoint;

/**
 * Created by Administrator on 2017/10/24.
 */

public class GatherPoint extends NotePoint implements PointEntity {
    private Long id;
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
        return null;
    }

    @Override
    public void setSid(Long sid) {

    }

    @Override
    public Object newInstance() {
        return new GatherPoint();
    }

    public static GatherPoint getGatherPoint(NotePoint notePoint){
        GatherPoint gatherPoint = new GatherPoint();
        gatherPoint.setId(null);
        gatherPoint.setPX(notePoint.getPX());
        gatherPoint.setPY(notePoint.getPY());
        gatherPoint.setFirstPress(notePoint.getFirstPress());
        gatherPoint.setTestTime(notePoint.getTestTime());
        gatherPoint.setPageIndex(notePoint.getPageIndex());
        gatherPoint.setPointType(notePoint.getPointType());
        gatherPoint.setPress(notePoint.getPress());
        return gatherPoint;
    }
}
