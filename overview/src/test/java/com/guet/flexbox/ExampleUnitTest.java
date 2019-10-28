package com.guet.flexbox;

import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        new Test3();
        assertEquals(4, 2 + 2);
    }
}

class Test2 {
    Test2(Object o)
    {

    }
}

class Test3 extends Test2{

    Test3() {
        super(((Function<Void, Void>) aVoid -> {
            System.out.println(
                    Thread.currentThread().getStackTrace()[2].getClassName());
            return null;
        }).apply(null));
    }
}