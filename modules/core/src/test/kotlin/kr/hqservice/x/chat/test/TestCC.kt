package kr.hqservice.x.chat.test

import kotlin.test.Test

class TestCC {
    @Test
    fun test() {
        val text = "[][][][][][][][] [/ㅁㄴㅇㄴ] ㅁㄴㅇㅁㄴㅇㅁㄴㅇ ㅁㄴㅇㅇㅇㅇ"
        val regex = Regex("(\\[/[^\\]]*])|([^\\[]+|\\[(?!/)|])")
        //val regex = Regex("(\\[/[^\\]]*])|([^\\[]+)")

        regex.findAll(text).forEach { match ->
            when {
                match.groups[1] != null -> println("명령 그룹: ${match.groups[1]!!.value}")
                match.groups[2] != null -> println("일반 그룹: ${match.groups[2]!!.value}")
            }
        }
    }
}