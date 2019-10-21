package com.guet.flexbox.mock.test


import com.guet.flexbox.mock.MockServer

import org.junit.Test

import java.io.File

class MockTestCase {
    @Test
    fun mock() {
        val args = arrayOf("\\testcase\\xml.xml", "\\testcase\\json.json")
        val path = File("..\\").absolutePath
        for (i in args.indices) {
            args[i] = path + args[i]
        }
        MockServer.main(args)
    }

}
