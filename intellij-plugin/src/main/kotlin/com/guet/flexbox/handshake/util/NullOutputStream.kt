package com.guet.flexbox.handshake.util

import java.io.OutputStream

object NullOutputStream : OutputStream() {

    override fun write(b: ByteArray, off: Int, len: Int) {}

    override fun write(b: Int) {}

    override fun write(b: ByteArray) {}
}