package com.showdy.partial


object PartialFunctionExecutor {


    private val functionsList = mutableListOf<PartialFunction<*,*>>()


    fun <P1, R> add(function: PartialFunction<P1, R>) {
        functionsList.add(function)
    }

    fun <P1,R> execute(p: P1):R {
        if (functionsList.size < 2) throw IllegalArgumentException("at least two partial function")
        val subList = functionsList.subList(1, functionsList.size)
        val functionCall =   subList.fold(functionsList[0]) { p1, r ->
                p1 orElse r
        }
        return (functionCall as PartialFunction<P1,R>).invoke(p)

    }
}

infix fun <P1, R> PartialFunction<P1, R>.orElse(that: PartialFunction<P1, R>): PartialFunction<P1, R> {
    return PartialFunction({ this.isMatch(it) || that.isMatch(it) }) {
        when {
            this.isMatch(it) -> this(it)
            else -> that(it)
        }
    }
}