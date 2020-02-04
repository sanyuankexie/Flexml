package com.guet.flexbox.playground

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.didichuxing.doraemonkit.util.UIUtils
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
        val c = inflater.context
        return ViewPager2(c).apply {
            background = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                            resources.getColor(R.color.deeppurplea200),
                            resources.getColor(R.color.purplea200)
                    )
            )
            orientation = ViewPager2.ORIENTATION_VERTICAL
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager2 = view as ViewPager2
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
                    setPadding(
                            paddingLeft,
                            getStatusBarHeight(),
                            paddingRight,
                            paddingBottom
                    )
                    holder.radius = 10.toPx().toFloat()
                }
                val iconView = AboutView::class.java
                        .getDeclaredField("ivAppIcon")
                        .apply {
                            isAccessible = true
                        }.get(aboutView) as ImageView
                iconView.scaleType = ImageView.ScaleType.FIT_XY
                val c = requireContext()
                val view2 = LinearLayout(c).apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    gravity = Gravity.CENTER
                    orientation = LinearLayout.VERTICAL
                    setBackgroundColor(Color.TRANSPARENT)
                    layoutParams = ViewGroup.LayoutParams(-1, -1)
                    addView(AppCompatImageView(c).apply {
                        val px = UIUtils.dp2px(c, 120f)
                        layoutParams = LinearLayout.LayoutParams(px, px)
                        Glide.with(this)
                                .load(R.drawable.ic_gcta)
                                .into(this)
                    })
                    addView(AppCompatTextView(c).apply {
                        layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
                            topMargin = UIUtils.dp2px(c, 15f)
                        }
                        typeface = Typeface.DEFAULT_BOLD
                        text = "G C T A"
                        setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                UIUtils.dp2px(c, 40f).toFloat()
                        )
                        setTextColor(Color.WHITE)
                        gravity = Gravity.CENTER
                    })
                }
                val holders = arrayOf(aboutView, view2).map {
                    object : RecyclerView.ViewHolder(it) {}
                }
                viewPager2.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

                    override fun onBindViewHolder(
                            holder: RecyclerView.ViewHolder,
                            position: Int
                    ) {
                    }

                    override fun getItemViewType(
                            position: Int
                    ): Int {
                        return position
                    }

                    override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                    ): RecyclerView.ViewHolder {
                        return holders[viewType]
                    }

                    override fun getItemCount(): Int = 2
                }
            }
        }
    }
}
