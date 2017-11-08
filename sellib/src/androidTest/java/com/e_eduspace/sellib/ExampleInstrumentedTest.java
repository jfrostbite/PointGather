package com.e_eduspace.sellib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.e_eduspace.sellib.test", appContext.getPackageName());

//        asset();


    }

    private void asset() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/assets/" + Constants.CONFIG_PATH + Constants.CONFIG_DB);
        Log.e("TEST", String.valueOf(stream));
        String msg1 = String.valueOf(new File(String.valueOf(stream)).exists());
        Log.e("TEST", msg1);
        String msg = new String(InputStreamToByte(stream));
        Log.e("TEST", msg);
        boolean exists = new File("file:///android_asset/" + Constants.CONFIG_PATH).exists();
        Log.e("TEST", String.valueOf(exists));
    }

    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;
    }
}
