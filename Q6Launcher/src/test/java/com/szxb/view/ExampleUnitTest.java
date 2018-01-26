package com.szxb.view;

import org.junit.Test;

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
    }



    @Test
    public void tests() {
        String str = "ssd";
        byte[] by = str.getBytes();
        for (int i = 0; i < by.length; i++) {
            System.out.println("by[i]:"+by[i]);
        }
        System.out.println("by ="+by);

    }

}