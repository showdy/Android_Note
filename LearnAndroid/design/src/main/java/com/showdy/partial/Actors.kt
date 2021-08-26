package com.showdy.partial

data class ApplyEvent(val money: Int, val title: String)

val groupLeader = {
    val matcher: (ApplyEvent) -> Boolean = { it.money <= 200 }
    val handler: (ApplyEvent) -> Unit = { println("GroupLeader handle:${it.title}") }
    PartialFunction(matcher, handler)
}()


val president = {
    val matcher: (ApplyEvent) -> Boolean = { it.money < 500 }
    val handler: (ApplyEvent) -> Unit = { println("president handle:${it.title}") }
    PartialFunction(matcher, handler)
}()

val college = {
    val matcher: (ApplyEvent) -> Boolean = { true }
    val handler: (ApplyEvent) -> Unit = {
        when {
            it.money > 1000 -> println("college refused :${it.title}")
            else -> println("college handle:${it.title}")
        }
    }
    PartialFunction(matcher, handler)
}()