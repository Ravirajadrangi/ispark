
import sbt._
import Keys._

object ISparkBuild extends Build {

  lazy val root = Project(id = "ispark",
                          base = file("."),
                          settings = rootSettings) aggregate(core, kernel, web)

  lazy val core = Project(id = "ispark-core",
                         base = file("core"),
                         settings = coreSettings)

  lazy val kernel = Project(id = "ispark-kernel",
                         base = file("kernel"),
                         settings = sharedSettings) dependsOn(core)

  lazy val web = Project(id = "ispark-web",
                         base = file("web"),
                         settings = webSettings) dependsOn(core) dependsOn(kernel)

  // TODO: web shouldn't directly depend on kernel, it should just know where to
  // find an installation of it (i.e., via a conf var) to execute.

  def sharedSettings = Defaults.defaultSettings ++ Seq(
    version := "0.1.0",
    organization := "io.magnify",
    scalaVersion := "2.10.3",

    fork := true, // fork new JVMs for tests

    // also check the local Maven repository ~/.m2
    resolvers ++= Seq(Resolver.file("Local Maven Repo", file(Path.userHome + "/.m2/repository"))),
    resolvers ++= Seq("snapshots"     at "http://oss.sonatype.org/content/repositories/snapshots",
                      "staging"       at "http://oss.sonatype.org/content/repositories/staging",
                      "releases"      at "http://oss.sonatype.org/content/repositories/releases"
                     ),

    scalacOptions ++= Seq("-deprecation", "-unchecked"),

    // TODO: The web component shouldn't really depend on spark; that should eventually be
    // probably restricted to kernel.
    libraryDependencies ++= {
      Seq(
        "org.apache.spark"  %% "spark-core"         % "0.9.0-incubating" % "compile",
        "org.apache.spark"  %% "spark-repl"         % "0.9.0-incubating" % "compile"
      )
    }
  )

  def rootSettings = sharedSettings ++ Seq(
    publish := {}
  )

  def coreSettings = sharedSettings ++ Seq(
    // Add thrift dependencies to the core lib,
    // as well as the Scrooge (Scala thrift compiler) configuration.

    libraryDependencies ++= {
      Seq(
        "org.apache.thrift" % "libthrift" % "0.8.0",
        "com.twitter" %% "scrooge-core" % "3.9.2",
        "com.twitter" %% "finagle-thrift" % "6.5.0"
      )
    }
  ) ++ com.twitter.scrooge.ScroogeSBT.newSettings

  def webSettings = sharedSettings ++ Seq(
    // Add web-app-specific settings for using lift.

    unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" },

    libraryDependencies ++= {
      val liftVersion = "2.6-M2"
      Seq(
        "net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
        "net.liftweb"       %% "lift-mapper"        % liftVersion        % "compile",
        "net.liftmodules"   %% "lift-jquery-module_2.6" % "2.5",
        "org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
        "ch.qos.logback"    % "logback-classic"     % "1.0.6",
        "org.specs2"        %% "specs2"             % "1.14"             % "test",
        "com.h2database"    % "h2"                  % "1.3.167"
      )
    }
  ) ++ seq(com.github.siasia.WebPlugin.webSettings :_*)
}


