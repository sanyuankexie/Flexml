package com.guet.flexbox.playground

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.guet.flexbox.litho.toPx
import com.vansuita.materialabout.builder.AboutBuilder
import com.vansuita.materialabout.views.AboutView

class AboutFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val aboutView = AboutBuilder.with(requireContext())
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
        return FrameLayout(inflater.context).apply {
            background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                            resources.getColor(R.color.deeppurplea200),
                            resources.getColor(R.color.purplea700)
                    )
            )
            addView(aboutView)
        }
    }
}
