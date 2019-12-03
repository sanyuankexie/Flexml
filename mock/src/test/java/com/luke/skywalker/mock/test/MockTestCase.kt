package com.luke.skywalker.mock.test


import com.luke.skywalker.mock.MockSession
import org.junit.Test
import java.io.File

class MockTestCase {
    @Test
    fun mock() {
        val root = System.getProperty("user.dir")
        val files = arrayOf(
                "testcase${File.separator}feed-card.xml",
                "testcase${File.separator}data.json"
        ).map {
            root + File.separator + it
        }
        MockSession.open(files[0], files[1]);
    }
}
