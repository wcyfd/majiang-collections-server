package com.randioo;

import java.text.MessageFormat;

import org.junit.Test;

public class LogTest {

    @Test
    public void test() {
        String format = "gameName={0}&key={1}&userId={2}&logInfo={3}";
        format = MessageFormat.format(format, "tst", "t");
        System.out.println(format);
    }
}
