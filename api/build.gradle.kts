import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.4.0-M3"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  id("org.jetbrains.dokka") version "0.10.1"
  kotlin("jvm") version "1.4.10"
  kotlin("plugin.spring") version "1.4.10"
  kotlin("plugin.jpa") version "1.4.10"
}

group = "com.posts"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
  maven { url = uri("https://repo.spring.io/milestone") }
  jcenter()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")

  implementation("io.jsonwebtoken:jjwt-api:0.11.1")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.1")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.1")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("com.h2database:h2")

  implementation("it.ozimov:embedded-redis:0.7.2")

  testCompileOnly("org.springframework.security:spring-security-test")
  testRuntimeOnly("org.apache.httpcomponents:httpclient")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }

  implementation("io.springfox:springfox-swagger-ui:2.9.2")
  implementation("io.springfox:springfox-swagger2:2.9.2")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.dokka {
  outputFormat = "html"
  outputDirectory = "$buildDir/javadoc"
}