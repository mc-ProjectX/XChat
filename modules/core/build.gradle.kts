plugins {
    id("hq.shared")
}

dependencies {
    compileOnly(libs.paper.api)

    compileOnly(framework.core)
    compileOnly(framework.command)
    compileOnly(framework.database)

    compileOnly(projectX.core)
    compileOnly(project(":modules:api"))

    testImplementation(kotlin("test"))
}
