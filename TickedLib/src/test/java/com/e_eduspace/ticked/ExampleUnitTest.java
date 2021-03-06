package com.e_eduspace.ticked;

import com.e_eduspace.ticked.entity.Question;
import com.e_eduspace.ticked.entity.TickedTag;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

//        test();

//        test1();


        int[] ints = {32,4,3,436,1,22,44,23};

        ArrayList<Integer> integers = new ArrayList<>();
        for (int anInt : ints) {
            integers.add(anInt);
        }

        System.out.println(integers);
        System.out.println("ceshi");

        Collections.sort(integers, new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {
                return t1 - integer;
            }
        });

        System.out.println(integers);
        System.out.println("ceshi");
    }

    private void test1() throws InterruptedException, java.util.concurrent.ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> submit = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (true) {
                    throw new FileNotFoundException();
                }
                return Thread.currentThread().getName();
            }
        });

        String s = submit.get();
        System.out.println(s);
    }

    private void test() {
        String[] tits = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20"};
        ArrayList<TickedTag> tags = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Question que = new Question();
            que.title = tits[i];
            que.page = 1;
            tags.add(que);
        }

        String str = "[{\"title\":\"1\",\"loc\":-1,\"page\":1},{\"title\":\"2\",\"loc\":-1,\"page\":1},{\"title\":\"3\",\"loc\":-1,\"page\":1},{\"title\":\"4\",\"loc\":-1,\"page\":1},{\"title\":\"5\",\"loc\":-1,\"page\":1},{\"title\":\"6\",\"loc\":-1,\"page\":1},{\"title\":\"7\",\"loc\":-1,\"page\":1},{\"title\":\"8\",\"loc\":-1,\"page\":1},{\"title\":\"9\",\"loc\":-1,\"page\":1},{\"title\":\"10\",\"loc\":-1,\"page\":1},{\"title\":\"11\",\"loc\":-1,\"page\":1},{\"title\":\"12\",\"loc\":-1,\"page\":1},{\"title\":\"13\",\"loc\":-1,\"page\":1},{\"title\":\"14\",\"loc\":-1,\"page\":1},{\"title\":\"15\",\"loc\":-1,\"page\":1},{\"title\":\"16\",\"loc\":-1,\"page\":1},{\"title\":\"17\",\"loc\":-1,\"page\":1},{\"title\":\"18\",\"loc\":-1,\"page\":1},{\"title\":\"19\",\"loc\":-1,\"page\":1},{\"title\":\"20\",\"loc\":-1,\"page\":1},{\"title\":\"10000\",\"loc\":-1,\"page\":1}]&&[{\"title\":\"1\",\"loc\":-1,\"page\":2},{\"title\":\"2\",\"loc\":-1,\"page\":2},{\"title\":\"3\",\"loc\":-1,\"page\":2},{\"title\":\"4\",\"loc\":-1,\"page\":2},{\"title\":\"5\",\"loc\":-1,\"page\":2},{\"title\":\"20000\",\"loc\":-1,\"page\":2}]";
        String[] split = str.split("&&");

        System.out.println(split);
    }
}