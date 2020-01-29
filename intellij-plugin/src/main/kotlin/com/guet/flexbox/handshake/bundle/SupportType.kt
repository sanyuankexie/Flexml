package com.guet.flexbox.handshake.bundle

import com.google.gson.annotations.SerializedName

enum class SupportType {
    @SerializedName("colors")
    COLORS,
    @SerializedName("url")
    URL,
    @SerializedName("values")
    VALUES,
    @SerializedName("bool")
    BOOL
}