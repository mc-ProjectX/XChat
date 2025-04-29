import kr.hqservice.resourcegenerator.bukkit.BukkitResourceGeneratorProperties

plugins {
    id("hq.shared")
    id("hq.shadow")
    id("kr.hqservice.resource-generator.bukkit")
}

bukkitResourceGenerator {
    main = "kr.hqservice.x.chat.XChatPlugin"
    name = "${extra["projectName"]}"
    apiVersion = "1.13"
    depend = listOf("HQFramework", "XCore")
    libraries = excludedRuntimeDependencies()

    permissions.apply {
        create("project_x.chat.spy") {
            description = "스파이 모드 전환 가능한 권한입니다."
            default = BukkitResourceGeneratorProperties.Permission.Default.OP
        }

        create("project_x.chat.clear") {
            description = "채팅을 청소할 수 있는 권한입니다."
            default = BukkitResourceGeneratorProperties.Permission.Default.OP
        }
    }

    commands.apply {
        create("귓").apply {
            description = "귓속말을 보낼 수 있습니다."
            usage = "/귓 <닉네임> <메시지>"
            aliases = listOf("귓속말", "귓말", "w", "rnlt", "rnltthrakf", "rnltakf")
        }

        create("답장").apply {
            description = "귓속말을 보낸 사람에게 답장합니다."
            usage = "/답장 <메시지>"
            aliases = listOf("답", "r", "ekqwkd", "ekq")
        }

        create("일반채팅").apply {
            description = "일반 채팅 모드로 전환합니다."
        }

        create("관리자채팅").apply {
            description = "관리자 채팅 모드로 전환합니다."
            aliases = listOf("opchat", "operatorchat")
            permission = "op"
        }

        create("가이드채팅").apply {
            description = "가이드 채팅 모드로 전환합니다."
            aliases = listOf("guidechat")
            permission = "project_x.group.guide"
        }

        create("스파이").apply {
            description = "스파이 모드로 전환합니다."
            aliases = listOf("spy")
            permission = "project_x.chat.spy"
        }

        create("채팅청소").apply {
            description = "채팅을 청소합니다."
            aliases = listOf("spy")
            permission = "project_x.chat.clear"
        }
    }
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(framework.core)
    runtimeOnly(project(":modules:core"))
    runtimeOnly(project(":modules:api"))
}
