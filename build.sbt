import Dependencies._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "2.0.0"
ThisBuild / organization := "dev.profunktor"
ThisBuild / organizationName := "ProfunKtor"

ThisBuild / evictionErrorLevel := Level.Warn
ThisBuild / scalafixDependencies += Libraries.organizeImports

resolvers += Resolver.sonatypeRepo("snapshots")

val scalafixCommonSettings = inConfig(IntegrationTest)(scalafixConfigSettings(IntegrationTest))

lazy val root = (project in file("."))
  .settings(name := "shopping-cart")
  .aggregate(
    core,
    `cats-effect-util`,
    `delivery-http-http4s`,
    `config-file-ciris`,
    `cryptography-jsr105-api`,
    `persistence-db-postgres-skunk`,
    `tokens-jwt-pdi`,
    // old stuff
    `big-ball-of-mud`,
    tests
  )

lazy val tests = (project in file("modules/tests"))
  .configs(IntegrationTest)
  .settings(
    name := "shopping-cart-test-suite",
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    Defaults.itSettings,
    scalafixCommonSettings,
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
      Libraries.catsLaws,
      Libraries.log4catsNoOp,
      Libraries.monocleLaw,
      Libraries.refinedScalacheck,
      Libraries.weaverCats,
      Libraries.weaverDiscipline,
      Libraries.weaverScalaCheck
    )
  )
  .dependsOn(`big-ball-of-mud`)

// add scalafixCommon settings (removed because they refer to Integration)
lazy val core =
  project
    .in(file("01-core"))
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.cats,
        Libraries.derevoCore,
        Libraries.derevoCats,
        Libraries.monocleCore,
        Libraries.newtype,
        Libraries.refinedCore,
        Libraries.refinedCats,
        Libraries.squants
      )
    )

lazy val `cats-effect-util` =
  project
    .in(file("02-c-cats-effect-util"))
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect
      )
    )

lazy val `delivery-http-http4s` =
  project
    .in(file("02-i-delivery-http-http4s"))
    .dependsOn(core % Cctt)
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        //
        Libraries.catsEffect,
        Libraries.catsRetry, // TODO check if we need this
        Libraries.circeCore,
        Libraries.circeGeneric,
        Libraries.circeParser,
        Libraries.circeRefined,
        Libraries.derevoCirce,
        Libraries.fs2, // TODO check if we need this
        Libraries.http4sDsl,
        Libraries.http4sServer,
        Libraries.http4sClient,
        Libraries.http4sCirce,
        Libraries.http4sJwtAuth,
        Libraries.javaxCrypto, // TODO check if we need this
        Libraries.log4cats     // TODO check if we need this
        // Libraries.redis4catsEffects,
        // Libraries.redis4catsLog4cats,
      )
    )

lazy val `persistence-db-postgres-skunk` =
  project
    .in(file("02-o-persistence-db-postgres-skunk"))
    .dependsOn(
      Seq(
        core,
        `cats-effect-util`
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.catsRetry,
        Libraries.fs2,
        Libraries.javaxCrypto, // TODO, ensure that we actually need it
        Libraries.log4cats,
        Libraries.logback % Runtime, // TODO, ensure that we actually need it
        Libraries.skunkCore,
        Libraries.skunkCirce
      )
    )

lazy val `config-file-ciris` =
  project
    .in(file("02-o-config-file-ciris"))
    .dependsOn(core % Cctt)
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.cirisCore,
        Libraries.cirisEnum,
        Libraries.cirisRefined
      )
    )

lazy val `cryptography-jsr105-api` =
  project
    .in(file("02-o-cryptography-jsr105-api"))
    .dependsOn(core % Cctt)
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.javaxCrypto
      )
    )

lazy val `tokens-jwt-pdi` =
  project
    .in(file("02-o-tokens-jwt-pdi"))
    .dependsOn(
      core               % Cctt,
      `cats-effect-util` % Cctt
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        //
        Libraries.circeCore,
        Libraries.circeGeneric,
        Libraries.circeParser,
        Libraries.circeRefined,
        Libraries.derevoCirce,
        Libraries.http4sJwtAuth
      )
    )

lazy val `main` =
  project
    .in(file("03-main"))
    .dependsOn(
      Seq(
        `delivery-http-http4s`,
        `config-file-ciris`,
        `cryptography-jsr105-api`,
        `persistence-db-postgres-skunk`,
        `tokens-jwt-pdi`
        // TODO redis stuff
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      scalafmtOnCompile := true,
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB
      )
    )

lazy val `big-ball-of-mud` = (project in file("modules/core"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  .settings(
    name := "shopping-cart-core",
    Docker / packageName := "shopping-cart",
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
    scalafmtOnCompile := true,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    Defaults.itSettings,
    scalafixCommonSettings,
    dockerBaseImage := "openjdk:11-jre-slim-buster",
    dockerExposedPorts ++= Seq(8080),
    makeBatScripts := Seq(),
    dockerUpdateLatest := true,
    libraryDependencies ++= Seq(
      CompilerPlugin.kindProjector,
      CompilerPlugin.betterMonadicFor,
      CompilerPlugin.semanticDB,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.catsRetry,
      Libraries.circeCore,
      Libraries.circeGeneric,
      Libraries.circeParser,
      Libraries.circeRefined,
      Libraries.cirisCore,
      Libraries.cirisEnum,
      Libraries.cirisRefined,
      Libraries.derevoCore,
      Libraries.derevoCats,
      Libraries.derevoCirce,
      Libraries.fs2,
      Libraries.http4sDsl,
      Libraries.http4sServer,
      Libraries.http4sClient,
      Libraries.http4sCirce,
      Libraries.http4sJwtAuth,
      Libraries.javaxCrypto,
      Libraries.log4cats,
      Libraries.logback % Runtime,
      Libraries.monocleCore,
      Libraries.newtype,
      Libraries.redis4catsEffects,
      Libraries.redis4catsLog4cats,
      Libraries.refinedCore,
      Libraries.refinedCats,
      Libraries.skunkCore,
      Libraries.skunkCirce,
      Libraries.squants
    )
  )

addCommandAlias("runLinter", ";scalafixAll --rules OrganizeImports")

lazy val Cctt: String =
  "compile->compile;test->test"
