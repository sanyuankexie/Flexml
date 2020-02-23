package com.guet.flexbox.litho.load

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.module.GlideModule

@Deprecated("")
class DrawableLoaderGlideModule : GlideModule {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
    }

    override fun registerComponents(
            context: Context,
            glide: Glide,
            registry: Registry
    ) {
        DrawableLoaderModule.init(registry)
    }
}