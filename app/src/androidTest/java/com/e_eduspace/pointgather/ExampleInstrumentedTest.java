package com.e_eduspace.pointgather;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private final String CONFIG_JSON = "config.json";
    private final String CONFIG_PATH = "identify_config";
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.e_eduspace.pointgather", appContext.getPackageName());
        config();
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
            throw new RuntimeException("配置文件不存在，请手动创建配置文件后运行程序");
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Log.e("TAG", sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
