import BuildHelper._

val ZioVersion       = "2.0.6"
val H2Version        = "2.1.210"
val ZioSchemaVersion = "0.2.1"

name := "zio-jdbc"
version := "0.0.2-snapshot"

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  List(
    organization := "dev.zio",
    homepage     := Some(url("https://zio.dev/zio-jdbc/")),
    licenses     := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers   := List(
      Developer("jdegoes", "John De Goes", "john@degoes.net", url("http://degoes.net"))
    )
  )
)

addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("fmt", "; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll")

addCommandAlias(
  "testJVM",
  ";core/test"
)

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
  )
  .aggregate(core, docs, examples)


lazy val core = project
  .in(file("core"))
  .settings(stdSettings("zio-jdbc"))
  .settings(
    assembly / assemblyJarName := "zio-jdbc-assembly-0.0.2-SNAPSHOT.jar",
    assemblyMergeStrategy := {
      case PathList("META-INF") => MergeStrategy.concat
      case _ => MergeStrategy.first
    },
    assemblyPackageScala / assembleArtifact := false,
    assembly / packageOptions ~= { pos =>
      pos.filterNot { po =>
        po.isInstanceOf[Package.MainClass]
      }
    },
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"          % ZioVersion,
      "dev.zio"       %% "zio-streams"  % ZioVersion,
      "dev.zio"       %% "zio-schema"   % ZioSchemaVersion,
      "dev.zio"       %% "zio-test"     % ZioVersion % Test,
      "dev.zio"       %% "zio-test-sbt" % ZioVersion % Test,
      "com.h2database" % "h2"           % H2Version  % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    Test / fork    := true,
    run / fork     := true
  )

lazy val docs = project
  .in(file("zio-jdbc-docs"))
  .settings(
    moduleName                                 := "zio-jdbc-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    projectName                                := "ZIO JDBC",
    mainModuleName                             := (core / moduleName).value,
    projectStage                               := ProjectStage.Research,
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects(core),
  )
  .dependsOn(core)
  .enablePlugins(WebsitePlugin)

lazy val examples = project
  .in(file("examples"))
  .dependsOn(core)
  .settings(stdSettings("zio-jdbc-examples"))
  .settings(
    publish / skip := true,
    libraryDependencies ++= Seq(
      "ch.qos.logback"       % "logback-classic"          % "1.2.6",
      "net.logstash.logback" % "logstash-logback-encoder" % "6.6"
    )
  )
