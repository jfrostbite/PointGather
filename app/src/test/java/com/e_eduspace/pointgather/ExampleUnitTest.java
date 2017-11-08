package com.e_eduspace.pointgather;

import com.e_eduspace.sellib.entity.Question;
import com.e_eduspace.sellib.entity.TickedTag;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        String[] tits = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
        ArrayList<TickedTag> tags = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Question que = new Question();
            que.title = tits[i];
            que.loc = -1;
            que.page = 1;
            tags.add(que);
        }

        String json = new Gson().toJson(tags);
        System.out.println(json);

        List<TickedTag> tickedTags =  new Gson().fromJson(json, new TypeToken<List<Question>>() {
        }.getType());
        System.out.println(json);
    }

    private void test(StringBuilder str) {
        str.delete(0, str.length());
        str.append("fdsa");
    }
}