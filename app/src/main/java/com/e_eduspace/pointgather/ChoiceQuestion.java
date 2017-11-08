package com.e_eduspace.pointgather;

import com.e_eduspace.sellib.entity.TickedStroke;
import com.e_eduspace.sellib.entity.TickedTag;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/11/1.
 */

public class ChoiceQuestion extends TickedTag {
    private String[] answers = {"A","B","C","D","E"};
    public List<String> answerList;
    public List<TickedStroke> strokes;

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChoiceQuestion) {
            ChoiceQuestion question = (ChoiceQuestion) obj;
            return title.equals(question.title);
        }
        return false;
    }

    /**
     * 更新loc确定答案
     * @param append 是否添加答案，判定多选
     */
    @Override
    public void reply(boolean append) {
        if (!append) {
            answerList = Collections.emptyList();
            answerList.add(answers[loc]);
        }
        if (answerList != null) {
            answerList.add(answers[loc]);
        }
    }

    @Override
    public List<String> answer() {
        return null;
    }
}
