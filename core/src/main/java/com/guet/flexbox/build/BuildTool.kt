package com.guet.flexbox.build

import com.guet.flexbox.HostContext
import com.guet.flexbox.TemplateNode
import com.guet.flexbox.el.ELContext

// 并发优化这条路线已经尝试过，效果不好（forkJoin）
// 数据绑定属于计算密集型任务，线程切换的的代价远远大于计算单个节点的代价
abstract class BuildTool {

    fun build(
            templateNode: TemplateNode,
            pageContext: HostContext,
            data: ELContext,
            upperVisibility: Boolean,
            c: Any
    ): List<Child> {
        val type = templateNode.type
        val toWidget: ToWidget = widgets[type] ?: default
        return toWidget.toWidget(
                this,
                templateNode,
                pageContext,
                data,
                upperVisibility,
                c
        )
    }

    protected abstract val widgets: Map<String, ToWidget>

    companion object {
        private val default: ToWidget = Common to null
    }
}
