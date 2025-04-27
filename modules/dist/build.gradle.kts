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
    }
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(framework.core)
    runtimeOnly(project(":modules:core"))
    runtimeOnly(project(":modules:api"))
}
