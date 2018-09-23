package com.pvergara.lab.mysql

import com.pvergara.lab.mysql.model._
import scalikejdbc._
import scalikejdbc.config._

/**
  * Ejemplos con scalikejdbc
  */
object AppClient extends App {

  //Se abre la conexion tomando los parametros  que se encuentran en application.conf
  DBs.setupAll()

  //Este valor implicito con tiene la session y es necesario para que se ejecute una query.
  implicit val session = AutoSession

  //Ejecutando una query simple y pasando el resultado a una lista con map.
  val entities: List[Map[String, Any]] = sql"select * from user".map(_.toMap).list.apply()
  //Se recorre la lista de WrappedResultSet
  for {
    e <- entities
    //Se lee el valor de la columna username
    username = e("username").toString
  } {
    //Se lee el valor de la columna id_user
    val id = e("user_id").toString.toInt
    //Se imprime El id_user y el username por cada registro obtenido.
    println(s"ID ${id}  ${username}")
  }

  //Se ejecuta el mismo select pero esta vez se transforma cada resultado a un UserModel y luego se imprimen.
  // Este ejemplo funciona cuan el apply espera como parametro WrappedResultSet en el mapper
  //val users: List[UserModel] = sql"select * from user".map(rs => UserModel(rs)).list.apply()
  //users.foreach(println)

  //Obteniendo
  val u = UserModel.syntax("uu")

  val usersList2: List[UserModel] = withSQL { select.from(UserModel as u) }.map(UserModel(u.resultName)).list.apply()
  usersList2.foreach(println)


  //Buscar un usuario
  val name = "dante"
  println(s"Buscando a ${name}...")
  val dante: Option[UserModel] = withSQL { select.from(UserModel as u).where.eq(u.userName, name)}.map(
    UserModel(u.resultName)).single.apply()

  if (dante.nonEmpty) {
    println(dante.get)
  }


  //Insert Datos
  println("INSERT")
  val usersListInsert : List[UserModel] = Seq(
    UserModel(0, "cvergara", "Camila", "Vergara", 27, "cver@hhh.cl", "8726637", "user"),
    UserModel(0, "ruben", "Ruben", "Lucero Alf", 27, "ruben@chucrut.es", "5678273636", "user")
  ).toList

  usersListInsert foreach {
    us =>
      sql"insert into user (username, password, email, name, last_name, age, phono, perfil) values (${us.userName}, ${us.userName},  ${us.mail}, ${us.name}, ${us.lastName}, ${us.age}, ${us.phono}, ${us.perfil})"
        .update.apply()
  }
  val usersList3: List[UserModel] = withSQL { select.from(UserModel as u) }.map(UserModel(u.resultName)).list.apply()
  println("\nREGISTRO ACTUALES")
  usersList3 foreach println

  //DELETE
  //DELETE FROM `chucrutdb`.`user` WHERE user_id =
  println("\nELIMINAR REGISTROS")
  val nameRuben = "ruben"
  val nameCvergara = "cvergara"
  val usersList4: List[UserModel] = withSQL { select.from(UserModel as u).where.eq(u.userName, nameRuben).or.eq(u.userName, nameCvergara) }.map(UserModel(u.resultName)).list.apply()
  println("REGISTRO PARA ELIMINAR")
  usersList4 foreach println
  usersList4 foreach { us => sql"delete from user WHERE user_id = ${us.id}".update.apply() }


  //SELECT TABLA VEHICLE
  println("\nJugando con los vehiculos...")
  val v = VehicleModel.syntax("v")
  val vehiclesList: List[VehicleModel] = withSQL { select.from(VehicleModel as v) }.map(VehicleModel(v.resultName)).list.apply()
  vehiclesList foreach println

  //Join entre user y vehicle

  val (uv, vv) = (UserVehiclesModel.syntax, VehicleModel.syntax)

  //INNER JOIN
  val userVehiclesList: Seq[UserVehiclesModel] = withSQL {
    select.from(UserVehiclesModel as uv).innerJoin(VehicleModel as vv).on(uv.id, vv.userId)
  }.map(UserVehiclesModel(uv, vv)).list.apply()

  println("\nResultado INNER JOIN")
  userVehiclesList foreach println

  DBs.closeAll()
}
