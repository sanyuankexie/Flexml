package com.guet.flexbox.playground

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.guet.flexbox.AppExecutors
import com.guet.flexbox.litho.toPx
import com.vansuita.materialabout.builder.AboutBuilder
import com.vansuita.materialabout.views.AboutView

class AboutFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
                R.layout.fragment_about,
                container,
                false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.apply {
            background = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                            resources.getColor(R.color.deeppurplea200),
                            resources.getColor(R.color.purplea200)
                    )
            )
            val layout: LinearLayout = findViewById(R.id.layout)
            AppExecutors.runOnAsyncThread {
                val photo = Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.ic_photo2)
                        .submit()
                val cover = Glide.with(this)
                        .asBitmap()
                        .load(R.mipmap.profile_cover)
                        .submit()
                val appIcon = Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.ic_launcher)
                        .submit()
                val android = Glide.with(this)
                        .asBitmap()
                        .load(R.mipmap.android)
                        .submit()
                val facebook = Glide.with(this)
                        .asBitmap()
                        .load(R.mipmap.facebook)
                        .submit()
                val tomcat =
                        Glide.with(this)
                                .asBitmap()
                                .load(R.drawable.ic_tomcat)
                                .submit()
                val builder = AboutBuilder.with(context)
                        .setPhoto(photo.get())
                        .setCover(cover.get())
                        .setName("Luke")
                        .setSubTitle("Android Hacker")
                        .setBrief("😀为了更美好的明天")
                        .setAppIcon(appIcon.get())
                        .addEmailLink("imlkluo@qq.com")
                        .setAppName(R.string.app_name)
                        .addGitHubLink("LukeXeon")
                        .addLink(
                                android.get(),
                                "GCTA Android",
                                "https://github.com/sanyuankexie"
                        )
                        .addLink(
                                facebook.get(),
                                "FB litho",
                                "https://fblitho.com"
                        )
                        .addLink(
                                tomcat.get(),
                                "Tomcat EL",
                                "https://tomcat.apache.org"
                        )
                        .addFiveStarsAction()
                        .setVersionNameAsAppSubTitle()
                        .addShareAction(R.string.app_name)
                        .setWrapScrollView(true)
                        .setLinksAnimated(true)
                        .setWrapScrollView(true)
                        .setShowAsCard(true)
                AppExecutors.runOnUiThread {
                    val aboutView = builder.build().apply {
                        holder.radius = 10.toPx().toFloat()
                    }
                    val iconView = AboutView::class.java
                            .getDeclaredField("ivAppIcon")
                            .apply {
                                isAccessible = true
                            }.get(aboutView) as ImageView
                    iconView.scaleType = ImageView.ScaleType.FIT_XY
                    layout.addView(aboutView, 0)
                }
            }
            val image: ImageView = findViewById(R.id.image)
            Glide.with(image)
                    .load(R.drawable.ic_gcta)
                    .into(image)
        }
    }
}
