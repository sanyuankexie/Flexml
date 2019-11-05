package com.guet.flexbox.mock.test


import com.guet.flexbox.mock.MockSession
import org.junit.Test
import java.io.File

class MockTestCase {
    @Test
    fun mock() {
        val root = System.getProperty("user.dir")
        val files = arrayOf(
                "testcase${File.separator}test-frame.xml",
                "testcase${File.separator}data.json"
        ).map {
            root + File.separator + it
        }
        MockSession.open(files[0], files[1]);
    }
}
