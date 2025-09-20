plugins {
    id("hq.shared")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.placeholder)
    compileOnly(libs.vault)

    compileOnly(framework.core)
    compileOnly(framework.command)
    compileOnly(framework.database)
    compileOnly(framework.inventory)

    compileOnly(projectX.core)
    compileOnly(projectX.core.prefix)
    compileOnly(project(":modules:api"))

    testImplementation(kotlin("test"))
}
