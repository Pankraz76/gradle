/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gradlebuild.modules.extension

import gradlebuild.modules.model.License

abstract class ExternalModulesExtension(bundleGroovyMajor: Int) {

    val groovyVersion = when (bundleGroovyMajor) {
        4 -> "4.0.29"
        // This is expected to contain Groovy 5 soon, once it's released, or we need to test it.
        else -> error("Unsupported Groovy major version: $bundleGroovyMajor")
    }

    val gradleIdeStarterVersion = "0.8.2"
    val kotlinVersion = "2.2.21"

    fun futureKotlin(module: String) = "org.jetbrains.kotlin:kotlin-$module:$kotlinVersion"

    val agp = "com.android.tools.build:gradle:8.10.2"
    val ansiControlSequenceUtil = "net.rubygrapefruit:ansi-control-sequence-util:0.3"
    val ant = "org.apache.ant:ant:1.10.14"
    val antLauncher = "org.apache.ant:ant-launcher:1.10.14"
    val asm = "org.ow2.asm:asm:9.7"
    val asmAnalysis = "org.ow2.asm:asm-analysis:9.7"
    val asmCommons = "org.ow2.asm:asm-commons:9.7"
    val asmTree = "org.ow2.asm:asm-tree:9.7"
    val asmUtil = "org.ow2.asm:asm-util:9.7"
    val assertj = "org.assertj:assertj-core:3.27.3"
    val awsS3Core = "com.amazonaws:aws-java-sdk-core:1.12.771"
    val awsS3Kms = "com.amazonaws:aws-java-sdk-kms:1.12.771"
    val awsS3S3 = "com.amazonaws:aws-java-sdk-s3:1.12.771"
    val awsS3Sts = "com.amazonaws:aws-java-sdk-sts:1.12.771"
    val bouncycastlePgp = "org.bouncycastle:bcpg-jdk18on:1.78.1"
    val bouncycastlePkix = "org.bouncycastle:bcpkix-jdk18on:1.78.1"
    val bouncycastleProvider = "org.bouncycastle:bcprov-jdk18on:1.78.1"
    val bouncycastleUtil = "org.bouncycastle:bcutil-jdk18on:1.78.1"
    val bsh = "org.apache-extras.beanshell:bsh:2.1.1"
    val commonsCodec = "commons-codec:commons-codec:1.17.2"
    val commonsCompress = "org.apache.commons:commons-compress:1.28.0"
    val commonsHttpclient = "org.apache.httpcomponents:httpclient:4.5.14"
    val commonsIo = "commons-io:commons-io:2.21.0"
    val commonsLang = "org.apache.commons:commons-lang3:3.17.0"
    val commonsMath = "org.apache.commons:commons-math3:3.6.1"
    val configurationCacheReport = "org.gradle.buildtool.internal:configuration-cache-report:1.27"
    val develocityTestAnnotation = "com.gradle:develocity-testing-annotations:2.0.4"
    val eclipseSisuPlexus = "org.eclipse.sisu:org.eclipse.sisu.plexus:0.3.5"
    val errorProneAnnotations = "com.google.errorprone:error_prone_annotations:2.30.0"
    val fastutil = "it.unimi.dsi:fastutil:8.5.15"
    val gcs = "com.google.apis:google-api-services-storage:v1-rev20250122-2.0.0"
    val googleApiClient = "com.google.api-client:google-api-client:2.7.2"
    val googleHttpClient = "com.google.http-client:google-http-client:1.45.0"
    val googleHttpClientApacheV2 = "com.google.http-client:google-http-client-apache-v2:1.45.0"
    val googleHttpClientGson = "com.google.http-client:google-http-client-gson:1.45.0"
    val googleOauthClient = "com.google.oauth-client:google-oauth-client:1.35.0"
    val gradleFileEvents = "org.gradle.fileevents:gradle-fileevents:1.4.0"
    val gradleIdeStarter = "org.gradle.buildtool.internal:gradle-ide-starter:$gradleIdeStarterVersion"
    val gradleIdeStarterScenarios = "org.gradle.buildtool.internal:gradle-ide-starter-scenarios:$gradleIdeStarterVersion"
    val gradleProfiler = "org.gradle.profiler:gradle-profiler:0.25.0"
    val groovy = "org.apache.groovy:groovy:$groovyVersion"
    val groovyAnt = "org.apache.groovy:groovy-ant:$groovyVersion"
    val groovyAstbuilder = "org.apache.groovy:groovy-astbuilder:$groovyVersion"
    val groovyConsole = "org.apache.groovy:groovy-console:$groovyVersion"
    val groovyDateUtil = "org.apache.groovy:groovy-dateutil:$groovyVersion"
    val groovyDatetime = "org.apache.groovy:groovy-datetime:$groovyVersion"
    val groovyDoc = "org.apache.groovy:groovy-groovydoc:$groovyVersion"
    val groovyJson = "org.apache.groovy:groovy-json:$groovyVersion"
    val groovyNio = "org.apache.groovy:groovy-nio:$groovyVersion"
    val groovySql = "org.apache.groovy:groovy-sql:$groovyVersion"
    val groovyTemplates = "org.apache.groovy:groovy-templates:$groovyVersion"
    val groovyTest = "org.apache.groovy:groovy-test:$groovyVersion"
    val groovyXml = "org.apache.groovy:groovy-xml:$groovyVersion"
    val gson = "com.google.code.gson:gson:2.11.0"
    val guava = "com.google.guava:guava:33.3.1-jre"
    val h2Database = "com.h2database:h2:2.3.232"
    val hamcrest = "org.hamcrest:hamcrest:2.2"
    val httpcore = "org.apache.httpcomponents:httpcore:4.4.16"
    val inject = "javax.inject:javax.inject:1"
    val ivy = "org.apache.ivy:ivy:2.5.3"
    val jacksonAnnotations = "com.fasterxml.jackson.core:jackson-annotations:2.18.2"
    val jacksonCore = "com.fasterxml.jackson.core:jackson-core:2.18.2"
    val jacksonDatabind = "com.fasterxml.jackson.core:jackson-databind:2.18.2"
    val jacksonDatatypeJdk8 = "com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.18.2"
    val jacksonDatatypeJsr310 = "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2"
    val jakartaActivation = "com.sun.activation:jakarta.activation:2.0.1"
    val jakartaXmlBind = "jakarta.xml.bind:jakarta.xml.bind-api:4.0.2"
    val jansi = "org.fusesource.jansi:jansi:2.4.1"
    val jatl = "com.googlecode.jatl:jatl:0.2.3"
    val javaPoet = "com.squareup:javapoet:1.13.0"
    val jaxbCore = "com.sun.xml.bind:jaxb-core:4.0.5"
    val jaxbImpl = "com.sun.xml.bind:jaxb-impl:4.0.5"
    val jcifs = "jcifs:jcifs:1.3.17"
    val jclToSlf4j = "org.slf4j:jcl-over-slf4j:2.0.16"
    val jcommander = "com.beust:jcommander:1.82"
    val jetbrainsAnnotations = "org.jetbrains:annotations:26.0.2"
    val jgit = "org.eclipse.jgit:org.eclipse.jgit:7.0.1.202408132216-r"
    val jgitSsh = "org.eclipse.jgit:org.eclipse.jgit.ssh.apache:7.0.1.202408132216-r"
    val jgitSshAgent = "org.eclipse.jgit:org.eclipse.jgit.ssh.apache.agent:7.0.1.202408132216-r"
    val jna = "net.java.dev.jna:jna:5.14.0"
    val jnaPlatform = "net.java.dev.jna:jna-platform:5.14.0"
    val jnrConstants = "com.github.jnr:jnr-constants:0.10.4"
    val joda = "joda-time:joda-time:2.14.0"
    val jsch = "com.github.mwiede:jsch:0.2.17"
    val jspecify = "org.jspecify:jspecify:1.0.0"
    val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"
    val julToSlf4j = "org.slf4j:jul-to-slf4j:2.0.16"
    val junit = "junit:junit:4.13.2"
    val junit5JupiterApi = "org.junit.jupiter:junit-jupiter-api:5.11.4"
    val junit5Vintage = "org.junit.vintage:junit-vintage-engine:5.11.4"
    val junitJupiter = "org.junit.jupiter:junit-jupiter:5.11.4"
    val junitPlatform = "org.junit.platform:junit-platform-launcher:1.11.4"
    val junitPlatformEngine = "org.junit.platform:junit-platform-engine:1.11.4"
    val jzlib = "com.jcraft:jzlib:1.1.3"
    val kotlinBuildToolsImpl = futureKotlin("build-tools-impl")
    val kotlinCompilerEmbeddable = futureKotlin("compiler-embeddable")
    val kotlinJvmAbiGenEmbeddable = "org.jetbrains.kotlin:jvm-abi-gen-embeddable:2.0.21"
    val kotlinReflect = futureKotlin("reflect")
    val kotlinStdlib = futureKotlin("stdlib")
    val kotlinxSerializationCore = "org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.0"
    val kotlinxSerializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0"
    val kryo = "com.esotericsoftware.kryo:kryo:2.24.0"
    val log4jToSlf4j = "org.slf4j:log4j-over-slf4j:2.0.16"
    val maven3Artifact = "org.apache.maven:maven-artifact:3.9.9"
    val maven3BuilderSupport = "org.apache.maven:maven-builder-support:3.9.9"
    val maven3Core = "org.apache.maven:maven-core:3.9.9"
    val maven3Model = "org.apache.maven:maven-model:3.9.9"
    val maven3RepositoryMetadata = "org.apache.maven:maven-repository-metadata:3.9.9"
    val maven3ResolverProvider = "org.apache.maven:maven-resolver-provider:3.9.9"
    val maven3Settings = "org.apache.maven:maven-settings:3.9.9"
    val maven3SettingsBuilder = "org.apache.maven:maven-settings-builder:3.9.9"
    val mavenResolverApi = "org.apache.maven.resolver:maven-resolver-api:1.9.18"
    val mavenResolverConnectorBasic = "org.apache.maven.resolver:maven-resolver-connector-basic:1.9.18"
    val mavenResolverImpl = "org.apache.maven.resolver:maven-resolver-impl:1.9.18"
    val mavenResolverSupplier = "org.apache.maven.resolver:maven-resolver-supplier:1.9.18"
    val mavenResolverTransportFile = "org.apache.maven.resolver:maven-resolver-transport-file:1.9.18"
    val mavenResolverTransportHttp = "org.apache.maven.resolver:maven-resolver-transport-http:1.9.18"
    val minlog = "com.esotericsoftware.minlog:minlog:1.3.1"
    val nativePlatform = "net.rubygrapefruit:native-platform:1.0.1"
    val objenesis = "org.objenesis:objenesis:3.3"
    val plexusCipher = "org.sonatype.plexus:plexus-cipher:2.0"
    val plexusClassworlds = "org.codehaus.plexus:plexus-classworlds:2.7.0"
    val plexusInterpolation = "org.codehaus.plexus:plexus-interpolation:1.27"
    val plexusSecDispatcher = "org.codehaus.plexus:plexus-sec-dispatcher:2.1.1"
    val plexusUtils = "org.codehaus.plexus:plexus-utils:3.5.1"
    val plist = "com.googlecode.plist:dd-plist:1.27"
    val pmavenCommon = "org.sonatype.pmaven:pmaven-common:0.9-20100615"
    val pmavenGroovy = "org.sonatype.pmaven:pmaven-groovy:0.9-20100615"
    val slf4jApi = "org.slf4j:slf4j-api:2.0.16"
    val slf4jSimple = "org.slf4j:slf4j-simple:2.0.16"
    val snakeyaml = "org.yaml:snakeyaml:2.3"
    val testng = "org.testng:testng:7.10.2"
    val tomlj = "org.tomlj:tomlj:1.1.1"
    val trove4j = "org.jetbrains.intellij.deps:trove4j:1.0.20200330"
    val xbeanReflect = "org.apache.xbean:xbean-reflect:4.23"

    // Compile only dependencies (dynamically downloaded if needed)
    val maven3Compat = "org.apache.maven:maven-compat:3.9.9"
    val maven3PluginApi = "org.apache.maven:maven-plugin-api:3.9.9"
    val zinc = "org.scala-sbt:zinc_2.13:1.9.3"

    // Test classpath only libraries
    val aircompressor = "io.airlift:aircompressor:0.27"
    val archunit = "com.tngtech.archunit:archunit:1.3.0"
    val archunitJunit5 = "com.tngtech.archunit:archunit-junit5:1.3.0"
    val archunitJunit5Api = "com.tngtech.archunit:archunit-junit5-api:1.3.0"
    val awaitility = "org.awaitility:awaitility-kotlin:4.2.2"
    val bytebuddy = "net.bytebuddy:byte-buddy:1.15.7"
    val bytebuddyAgent = "net.bytebuddy:byte-buddy-agent:1.15.7"
    val cglib = "cglib:cglib:3.3.0"
    val compileTesting = "com.google.testing.compile:compile-testing:0.21.0"
    val dockerJavaApi = "com.github.docker-java:docker-java-api:3.3.7"
    val equalsverifier = "nl.jqno.equalsverifier:equalsverifier:3.17.5"
    val guice = "com.google.inject:guice:7.0.0"
    val hikariCP = "com.zaxxer:HikariCP:6.2.1"
    val httpmime = "org.apache.httpcomponents:httpmime:4.5.14"
    val jacksonKotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2"
    val jetty = "org.eclipse.jetty:jetty-http:9.4.56.v20240815"
    val jettySecurity = "org.eclipse.jetty:jetty-security:9.4.56.v20240815"
    val jettyServer = "org.eclipse.jetty:jetty-server:9.4.56.v20240815"
    val jettyServlet = "org.eclipse.jetty:jetty-servlet:9.4.56.v20240815"
    val jettyUtil = "org.eclipse.jetty:jetty-util:9.4.56.v20240815"
    val jettyWebApp = "org.eclipse.jetty:jetty-webapp:9.4.56.v20240815"
    val joptSimple = "net.sf.jopt-simple:jopt-simple:5.0.4"
    val jsoup = "org.jsoup:jsoup:1.18.1"
    val jtar = "org.kamranzafar:jtar:2.3"
    val kotlinCoroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0"
    val kotlinCoroutinesDebug = "org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.9.0"
    val littleproxy = "xyz.rogfam:littleproxy:2.0.20"
    val mockitoCore = "org.mockito:mockito-core:5.15.2"
    val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:5.3.2"
    val mockwebserver = "com.squareup.okhttp3:mockwebserver:4.12.0"
    val mySqlConnector = "com.mysql:mysql-connector-j:9.2.0"
    val netty = "io.netty:netty-all:4.1.116.Final"
    val opentest4j = "org.opentest4j:opentest4j:1.3.0"
    val samplesCheck = "org.gradle.exemplar:samples-check:1.0.0"
    val samplesDiscovery = "org.gradle.exemplar:samples-discovery:1.0.0"
    val servletApi = "javax.servlet:javax.servlet-api:3.1.0"
    val snappy = "org.iq80.snappy:snappy:0.4"
    val socksProxy = "com.github.bbottema:java-socks-proxy-server:2.0.0"
    val spock = "org.spockframework:spock-core:2.4-M1-groovy-4.0"
    val spockJUnit4 = "org.spockframework:spock-junit4:2.4-M1-groovy-4.0"
    val sshdCore = "org.apache.sshd:sshd-core:2.14.0"
    val sshdOsgi = "org.apache.sshd:sshd-osgi:2.14.0"
    val sshdScp = "org.apache.sshd:sshd-scp:2.14.0"
    val sshdSftp = "org.apache.sshd:sshd-sftp:2.14.0"
    val testcontainers = "org.testcontainers:testcontainers:1.20.4"
    val testcontainersSpock = "org.testcontainers:spock:1.20.4"
    val xerces = "xerces:xercesImpl:2.12.2"
    val xmlunit = "xmlunit:xmlunit:1.6"

    val licenses = mapOf(
        ansiControlSequenceUtil to License.Apache2,
        ant to License.Apache2,
        antLauncher to License.Apache2,
        asm to License.BSD3,
        asmAnalysis to License.BSD3,
        asmCommons to License.BSD3,
        asmTree to License.BSD3,
        asmUtil to License.BSD3,
        assertj to License.Apache2,
        awsS3Core to License.Apache2,
        awsS3Kms to License.Apache2,
        awsS3S3 to License.Apache2,
        awsS3Sts to License.Apache2,
        bouncycastlePgp to License.MIT,
        bouncycastleProvider to License.MIT,
        bouncycastleUtil to License.MIT,
        bsh to License.Apache2,
        commonsCodec to License.Apache2,
        commonsCompress to License.Apache2,
        commonsHttpclient to License.Apache2,
        commonsIo to License.Apache2,
        commonsLang to License.Apache2,
        commonsMath to License.Apache2,
        compileTesting to License.Apache2,
        configurationCacheReport to License.Apache2,
        fastutil to License.Apache2,
        gcs to License.Apache2,
        googleApiClient to License.Apache2,
        googleHttpClient to License.Apache2,
        googleHttpClientApacheV2 to License.Apache2,
        googleHttpClientGson to License.Apache2,
        googleOauthClient to License.Apache2,
        gradleFileEvents to License.Apache2,
        gradleIdeStarter to License.Apache2,
        gradleProfiler to License.Apache2,
        groovy to License.Apache2,
        gson to License.Apache2,
        guava to License.Apache2,
        guice to License.Apache2,
        h2Database to License.EPL,
        hamcrest to License.BSD3,
        hikariCP to License.Apache2,
        httpcore to License.Apache2,
        inject to License.Apache2,
        ivy to License.Apache2,
        jacksonAnnotations to License.Apache2,
        jacksonCore to License.Apache2,
        jacksonDatabind to License.Apache2,
        jacksonDatatypeJdk8 to License.Apache2,
        jacksonDatatypeJsr310 to License.Apache2,
        jakartaActivation to License.EDL,
        jakartaXmlBind to License.EDL,
        jansi to License.Apache2,
        jatl to License.Apache2,
        javaPoet to License.Apache2,
        jaxbCore to License.EDL,
        jaxbImpl to License.EDL,
        jcifs to License.LGPL21,
        jclToSlf4j to License.MIT,
        jcommander to License.Apache2,
        jetbrainsAnnotations to License.Apache2,
        jgit to License.EDL,
        jnrConstants to License.Apache2,
        joda to License.Apache2,
        jsch to License.BSDStyle,
        jsr305 to License.BSD3,
        julToSlf4j to License.MIT,
        junit to License.EPL,
        junit5JupiterApi to License.EPL,
        junit5Vintage to License.EPL,
        junitPlatform to License.EPL,
        junitPlatformEngine to License.EPL,
        jzlib to License.BSDStyle,
        kryo to License.BSD3,
        log4jToSlf4j to License.MIT,
        maven3BuilderSupport to License.Apache2,
        maven3Model to License.Apache2,
        maven3RepositoryMetadata to License.Apache2,
        maven3ResolverProvider to License.Apache2,
        maven3Settings to License.Apache2,
        maven3SettingsBuilder to License.Apache2,
        mavenResolverApi to License.Apache2,
        mavenResolverConnectorBasic to License.Apache2,
        mavenResolverImpl to License.Apache2,
        mavenResolverSupplier to License.Apache2,
        mavenResolverTransportFile to License.Apache2,
        mavenResolverTransportHttp to License.Apache2,
        minlog to License.BSD3,
        nativePlatform to License.Apache2,
        objenesis to License.Apache2,
        plexusCipher to License.Apache2,
        plexusInterpolation to License.Apache2,
        plexusSecDispatcher to License.Apache2,
        plexusUtils to License.Apache2,
        plist to License.MIT,
        pmavenCommon to License.Apache2,
        pmavenGroovy to License.Apache2,
        slf4jApi to License.MIT,
        snakeyaml to License.Apache2,
        testng to License.Apache2,
        tomlj to License.Apache2,
        trove4j to License.LGPL21,
        xbeanReflect to License.Apache2,
        zinc to License.Apache2
    )

}
