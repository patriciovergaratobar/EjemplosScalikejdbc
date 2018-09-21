name := "EjemplosScalikejdbc"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.scalikejdbc" %% "scalikejdbc"          % "3.3.0",
  "org.scalikejdbc" %% "scalikejdbc-config"   % "3.3.0",
  "ch.qos.logback"  %  "logback-classic"      % "1.2.3",
  "mysql"           %   "mysql-connector-java" % "8.0.12"

)
