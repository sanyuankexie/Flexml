package com.guet.flexbox.playground.model

import com.guet.flexbox.TemplateNode
import com.guet.flexbox.compiler.Compiler
import com.guet.flexbox.compiler.NodeFactory
import java.util.*

object JitCompiler : Compiler<TemplateNode>(object : NodeFactory<TemplateNode> {
    override fun createNode(
            type: String,
            attrs: Map<String, String>,
            children: List<TemplateNode>
    ): TemplateNode {
        return TemplateNode(
                type,
                Collections.unmodifiableMap(attrs),
                Collections.unmodifiableList(children)
        )
    }
})