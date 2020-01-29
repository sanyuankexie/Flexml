package com.guet.flexbox.el

internal open class ScopeELContext(
        internal val target: ELContext
) : ELContext() {

    override fun isLambdaArgument(name: String?): Boolean {
        return super.isLambdaArgument(name) || target.isLambdaArgument(name)
    }

    override fun getLambdaArgument(name: String?): Any {
        return if (super.isLambdaArgument(name)) {
            super.getLambdaArgument(name)
        } else {
            target.getLambdaArgument(name)
        }
    }

    override fun getFunctionMapper(): FunctionMapper = target.functionMapper

    override fun getVariableMapper(): VariableMapper = target.variableMapper

    override fun getELResolver(): ELResolver = target.elResolver

}