package com.guet.flexbox.build

import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaFlexDirection
import com.facebook.yoga.YogaJustify
import com.facebook.yoga.YogaWrap

object Flex : Declaration(Common) {
    override val attributeSet: AttributeSet by create {
        enum("flexWrap", mapOf(
                "wrap" to YogaWrap.WRAP,
                "noWrap" to YogaWrap.NO_WRAP,
                "wrapReverse" to YogaWrap.WRAP_REVERSE
        ))
        enum("justifyContent", mapOf(
                "flexStart" to YogaJustify.FLEX_START,
                "flexEnd" to YogaJustify.FLEX_END,
                "center" to YogaJustify.CENTER,
                "spaceBetween" to YogaJustify.SPACE_BETWEEN,
                "spaceAround" to YogaJustify.SPACE_AROUND
        ))
        enum("alignItems", mapOf(
                "auto" to YogaAlign.AUTO,
                "flexStart" to YogaAlign.FLEX_START,
                "flexEnd" to YogaAlign.FLEX_END,
                "center" to YogaAlign.CENTER,
                "baseline" to YogaAlign.BASELINE,
                "stretch" to YogaAlign.STRETCH
        ))
        enum("alignContent", mapOf(
                "auto" to YogaAlign.AUTO,
                "flexStart" to YogaAlign.FLEX_START,
                "flexEnd" to YogaAlign.FLEX_END,
                "center" to YogaAlign.CENTER,
                "baseline" to YogaAlign.BASELINE,
                "stretch" to YogaAlign.STRETCH
        ))
        enum("flexDirection", mapOf(
                "row" to YogaFlexDirection.ROW,
                "column" to YogaFlexDirection.COLUMN,
                "rowReverse" to YogaFlexDirection.ROW_REVERSE,
                "columnReverse" to YogaFlexDirection.COLUMN_REVERSE
        ))
    }
}