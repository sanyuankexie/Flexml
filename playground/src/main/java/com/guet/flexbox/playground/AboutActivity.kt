package com.guet.flexbox.playground

import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vansuita.materialabout.builder.AboutBuilder
import com.vansuita.materialabout.views.AboutView


class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = Glide.with(this)
                .asBitmap()
                .load(R.mipmap.profile_cover)
                .submit()
        val photo = Glide.with(this)
                .asBitmap()
                .load(R.drawable.ic_photo2)
                .submit()
        val icon = Glide.with(this)
                .asBitmap()
                .load(R.drawable.ic_launcher)
                .submit()
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            val v1 = background.get()
            val v2 = photo.get()
            val v3 = icon.get()
            runOnUiThread {
                val view = AboutBuilder.with(this)
                        .setPhoto(v2)
                        .setCover(v1)
                        .setName("Luke")
                        .setSubTitle("Android Hacker")
                        .setBrief("😀为了更美好的明天")
                        .setAppIcon(v3)
                        .addEmailLink("imlkluo@qq.com")
                        .setAppName(R.string.app_name)
                        .addGitHubLink("https://github.com/LukeXeon")
                        .addFiveStarsAction()
                        .setVersionNameAsAppSubTitle()
                        .addShareAction(R.string.app_name)
                        .setWrapScrollView(true)
                        .setLinksAnimated(true)
                        .setShowAsCard(true)
                        .build()
                val iconView = AboutView::class.java
                        .getDeclaredField("ivAppIcon")
                        .apply {
                            isAccessible = true
                        }.get(view) as ImageView
                iconView.scaleType = ImageView.ScaleType.FIT_XY
                setContentView(view)
            }
        }
    }
}
