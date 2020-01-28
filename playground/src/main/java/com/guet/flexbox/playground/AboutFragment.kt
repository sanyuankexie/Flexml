package com.guet.flexbox.playground

import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
        ).apply {
            background = GradientDrawable(
                    TOP_BOTTOM,
                    intArrayOf(
                            resources.getColor(R.color.deeppurplea200),
                            resources.getColor(R.color.purplea200)
                    )
            )
            val layout: LinearLayout = findViewById(R.id.layout)
            val aboutView = AboutBuilder.with(context)
                    .setPhoto(R.drawable.ic_photo2)
                    .setCover(R.mipmap.profile_cover)
                    .setName("Luke")
                    .setSubTitle("Android Hacker")
                    .setBrief("😀为了更美好的明天")
                    .setAppIcon(R.drawable.ic_launcher)
                    .addEmailLink("imlkluo@qq.com")
                    .setAppName(R.string.app_name)
                    .addGitHubLink("LukeXeon")
                    .addLink(R.mipmap.android, "GCTA Android", "https://github.com/sanyuankexie")
                    .addLink(R.mipmap.facebook, "FB litho", "https://fblitho.com")
                    .addLink(R.drawable.ic_tomcat, "Tomcat EL", "https://tomcat.apache.org")
                    .addFiveStarsAction()
                    .setVersionNameAsAppSubTitle()
                    .addShareAction(R.string.app_name)
                    .setWrapScrollView(true)
                    .setLinksAnimated(true)
                    .setWrapScrollView(true)
                    .setShowAsCard(true)
                    .build().apply {
                        holder.radius = 10.toPx().toFloat()
                    }
            val iconView = AboutView::class.java
                    .getDeclaredField("ivAppIcon")
                    .apply {
                        isAccessible = true
                    }.get(aboutView) as ImageView
            iconView.scaleType = ImageView.ScaleType.FIT_XY
            val image: ImageView = findViewById(R.id.image)
            Glide.with(image)
                    .load(R.drawable.ic_launcher2)
                    .into(image)
            layout.addView(aboutView, 0)
        }
    }
}
