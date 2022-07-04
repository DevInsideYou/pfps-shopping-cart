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
    // c
    `json-circe-util`,
    `retries-cats-retry`,
    // i
    `delivery-http-http4s`,
    // o
    `auth-jwt-pdi`,
    `cache-redis-redis4cats`,
    `client-http-http4s`,
    `config-env-ciris`,
    `core-adapters`,
    `cryptography-jsr105-api`,
    `persistence-db-postgres-skunk`,
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

lazy val core =
  project
    .in(file("01-core"))
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.cats,
        Libraries.derevoCats,
        Libraries.derevoCore,
        Libraries.monocleCore,
        Libraries.newtype,
        Libraries.refinedCats,
        Libraries.refinedCore,
        Libraries.squants
      )
    )

lazy val `retries-cats-retry` =
  project
    .in(file("02-c-retries-cats-retry"))
    .dependsOn(
      Seq(
        core
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.catsRetry
      )
    )

lazy val `json-circe-util` =
  project
    .in(file("02-c-json-circe-util"))
    .dependsOn(
      Seq(
        core
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.circeCore,
        Libraries.circeGeneric,
        Libraries.circeParser,
        Libraries.circeRefined,
        Libraries.derevoCirce
      )
    )

lazy val `delivery-http-http4s` =
  project
    .in(file("02-i-delivery-http-http4s"))
    .dependsOn(
      Seq(
        `json-circe-util`
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.http4sCirce,
        Libraries.http4sDsl,
        Libraries.http4sJwtAuth,
        Libraries.http4sServer
      )
    )

lazy val `auth-jwt-pdi` =
  project
    .in(file("02-o-auth-jwt-pdi"))
    .dependsOn(
      Seq(
        `json-circe-util`
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.http4sJwtAuth // this is a wrapper around http4s and jwt-scala
      )
    )

lazy val `cache-redis-redis4cats` =
  project
    .in(file("02-o-cache-redis-redis4cats"))
    .dependsOn(
      Seq(
        `json-circe-util`
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.redis4catsEffects,
        Libraries.redis4catsLog4cats
      )
    )

lazy val `client-http-http4s` =
  project
    .in(file("02-o-client-http-http4s"))
    .dependsOn(
      Seq(
        `json-circe-util`
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.http4sDsl,
        Libraries.http4sClient,
        Libraries.http4sCirce
      )
    )

lazy val `config-env-ciris` =
  project
    .in(file("02-o-config-env-ciris"))
    .dependsOn(
      Seq(
        core
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
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

lazy val `core-adapters` =
  project
    .in(file("02-o-core-adapters"))
    .dependsOn(
      Seq(
        core
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB
      )
    )

lazy val `cryptography-jsr105-api` =
  project
    .in(file("02-o-cryptography-jsr105-api"))
    .dependsOn(
      Seq(
        core
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.javaxCrypto
      )
    )

lazy val `persistence-db-postgres-skunk` =
  project
    .in(file("02-o-persistence-db-postgres-skunk"))
    .dependsOn(
      Seq(
        `json-circe-util`,
        `retries-cats-retry`
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.catsEffect,
        Libraries.fs2,
        Libraries.skunkCirce,
        Libraries.skunkCore
      )
    )

lazy val main =
  project
    .in(file("03-main"))
    .dependsOn(
      Seq(
        // i
        `delivery-http-http4s`,
        // o
        `auth-jwt-pdi`,
        `cache-redis-redis4cats`,
        `client-http-http4s`,
        `config-env-ciris`,
        `core-adapters`,
        `cryptography-jsr105-api`,
        `persistence-db-postgres-skunk`
      ).map(_ % Cctt): _*
    )
    .settings(
      scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      libraryDependencies ++= Seq(
        CompilerPlugin.kindProjector,
        CompilerPlugin.betterMonadicFor,
        CompilerPlugin.semanticDB,
        Libraries.log4cats,
        Libraries.logback % Runtime
      )
    )

lazy val `big-ball-of-mud` = (project in file("modules/core"))
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)
  // .dependsOn(main % Cctt)
  .settings(
    name := "shopping-cart-core",
    Docker / packageName := "shopping-cart",
    scalacOptions ++= List("-Ymacro-annotations", "-Yrangepos", "-Wconf:cat=unused:info"),
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
