package com.e_eduspace.identify;

import com.e_eduspace.identify.entity.FormPage;
import com.e_eduspace.identify.entity.LineBean;
import com.e_eduspace.identify.entity.PageBean;
import com.e_eduspace.identify.entity.PointAreaEntity;
import com.e_eduspace.identify.entity.StrokeEntity;

import java.util.List;

/**
 * Created by Administrator on 2017-08-08.
 */

public interface IDentify {
    IDentify initialize(FormPage formPage);
    IDentify setOnRecognitionListener(OnRecognitionListener<PageBean> listener);
    void convert(List<? extends StrokeEntity> strokes);
    void check();
    void recogn(LineBean line);
    void clean(boolean recog);
    void recover (int counter);
    PointAreaEntity queryLast(boolean regon);

    List<PointAreaEntity> query(boolean regon);
}
