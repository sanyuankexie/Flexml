package com.guet.flexbox.mock.test


import com.guet.flexbox.mock.MockSession
import org.junit.Test
import java.io.File

class MockTestCase {
    @Test
    fun mock() {
        val root = System.getProperty("user.dir")
        val files = arrayOf(
                "xml.xml",
                "json.json"
        ).map {
            root + File.separator + it
        }
        MockSession.run(files[0], files[1]);
    }
}
