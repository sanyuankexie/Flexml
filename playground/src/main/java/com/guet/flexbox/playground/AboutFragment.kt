package com.guet.flexbox.playground

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.guet.flexbox.ConcurrentUtils
import com.guet.flexbox.litho.toPx
import com.vansuita.materialabout.builder.AboutBuilder
import com.vansuita.materialabout.views.AboutView


class AboutFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return FrameLayout(inflater.context).apply {
            background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                            resources.getColor(R.color.deeppurplea200),
                            resources.getColor(R.color.purplea700)
                    )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        ConcurrentUtils.threadPool.execute {
            val v1 = background.get()
            val v2 = photo.get()
            val v3 = icon.get()
            requireActivity().runOnUiThread {
                val aboutView = AboutBuilder.with(requireContext())
                        .setPhoto(v2)
                        .setCover(v1)
                        .setName("Luke")
                        .setSubTitle("Android Hacker")
                        .setBrief("😀为了更美好的明天")
                        .setAppIcon(v3)
                        .addEmailLink("imlkluo@qq.com")
                        .setAppName(R.string.app_name)
                        .addGitHubLink("LukeXeon")
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
                if (view is FrameLayout) {
                    view.addView(aboutView)
                }
            }
        }
    }
}
