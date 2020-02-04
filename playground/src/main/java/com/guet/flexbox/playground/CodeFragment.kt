package com.guet.flexbox.playground

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.didichuxing.doraemonkit.util.UIUtils
import com.facebook.litho.LithoView
import com.facebook.litho.Row
import com.facebook.litho.widget.HorizontalScroll
import com.facebook.litho.widget.Text
import com.facebook.litho.widget.VerticalScroll
import com.facebook.yoga.YogaEdge
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.guet.flexbox.litho.widget.CornerOutlineProvider
import com.guet.flexbox.playground.model.AppLoader

open class CodeFragment : BottomSheetDialogFragment() {

    var onDismissListener: DialogInterface.OnDismissListener? = null

    private lateinit var codeView: LithoView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
                R.layout.fragment_code,
                container,
                false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        codeView = view.findViewById(R.id.code)
        val outline = CornerOutlineProvider(
                UIUtils.dp2px(requireContext(), 15f)
        )
        codeView.apply {
            outlineProvider = outline
            clipToOutline = true
        }
        val arg = arguments ?: return
        val url = arg.getString("url") ?: return
        val code = AppLoader.findSourceCode(url)
        val c = codeView.componentContext
        codeView.setComponentAsync(Row.create(c)
                .backgroundColor(resources.getColor(R.color.code_background))
                .child(VerticalScroll.create(c)
                        .nestedScrollingEnabled(true)
                        .paddingDip(YogaEdge.VERTICAL, 10f)
                        .childComponent(HorizontalScroll.create(c)
                                .paddingDip(YogaEdge.HORIZONTAL, 10f)
                                .contentProps(Text.create(c)
                                        .textColor(resources.getColor(R.color.whitesmoke))
                                        .text(code))
                        )
                ).build())
    }

    override fun onStart() {
        super.onStart()
        requireDialog().window?.apply {
            val color = ColorDrawable(Color.TRANSPARENT)
            setBackgroundDrawable(color)
            findViewById<View>(R.id.design_bottom_sheet).apply {
                background = color
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismiss(dialog)
    }
}