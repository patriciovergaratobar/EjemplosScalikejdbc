package com.pvergara.lab.mysql
import com.pvergara.lab.mysql
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

// Modelo de datos que representan la tabla user
case class UserModel(id: Int, userName: String, name: String, lastName: String, age: Int, mail: String, phono: String, perfil: String)
//Mapper de UserModel con Tabla user
object UserModel extends SQLSyntaxSupport[UserModel] {

  //Nombre de la tabla
  override val tableName = "user"

  //Columnas de la tabla
  override val columns = Seq("user_id", "username", "name", "last_name", "age", "email", "phono", "perfil")

  //Converters de los nombre de atributos del modelo en scala con los nombres de las columnas
  //Solo se indican los que son distintos entre "^NOMBRE_ATRIBUTO_DE_LA_CLASE$" -> "NOMBRE_COLUMNA"
  override val nameConverters = Map(
    "^id$" -> "user_id",
    "^userName$" -> "username",
    "^lastName$" -> "last_name",
    "^mail$" -> "email"
  )

  def apply(u: SyntaxProvider[UserModel])(rs: WrappedResultSet): UserModel = apply(u.resultName)(rs)

  def apply(u: ResultName[UserModel])(rs: WrappedResultSet) = new UserModel(

    rs.int(u.id),
    rs.nString(u.userName), rs.nString(u.name), rs.nString(u.lastName), rs.int(u.age),
    rs.nString(u.mail), rs.nString(u.phono), rs.nString(u.perfil)
  )

}

// Modelo de datos que representan la tabla vehicle
case class VehicleModel(vehicleId: Int, plate: String, imeiGps: String, lastLongitud: Double, lastLatitude: Double, userId: Int)
object VehicleModel extends SQLSyntaxSupport[VehicleModel] {

  override val tableName = "vehicle"
  override val columns = Seq( "vehicle_id","plate", "imei_gps", "last_longitud", "last_latitude", "user_user_id")

  override val nameConverters = Map(
    "^vehicleId$" -> "vehicle_id",
    "^imeiGps$" -> "imei_gps",
    "^lastLongitud$" -> "last_longitud",
    "^lastLatitude$" -> "last_latitude",
    "^userId$" -> "user_user_id"
  )



  def apply(v: SyntaxProvider[VehicleModel])(rs: WrappedResultSet): VehicleModel = apply(v.resultName)(rs)

  def apply(v: ResultName[VehicleModel])(rs: WrappedResultSet) = new VehicleModel(
    rs.int(v.vehicleId),
    rs.nString(v.plate),
    rs.nString(v.imeiGps),
    rs.double(v.lastLongitud),
    rs.double(v.lastLatitude),
    rs.int(v.userId)
  )
  /*def opt(v: SyntaxProvider[VehicleModel])(rs: WrappedResultSet): Option[VehicleModel] =
    rs.longOpt(v.resultName.id).map(_ => VehicleModel(v)(rs))
*/
}

case class UserVehiclesModel(id: Int, userName: String, name: String, lastName: String, age: Int, mail: String, phono: String, perfil: String, vehicles: Option[VehicleModel] = None)

object UserVehiclesModel extends SQLSyntaxSupport[UserVehiclesModel] {
  //Nombre de la tabla
  override val tableName = "user"
  //Columnas de la tabla
  override val columns = Seq("user_id", "username", "name", "last_name", "age", "email", "phono", "perfil")
  override val nameConverters = Map(
    "^id$" -> "user_id",
    "^userName$" -> "username",
    "^lastName$" -> "last_name",
    "^mail$" -> "email"
  )
  def apply(u: SyntaxProvider[UserVehiclesModel])(rs: WrappedResultSet): UserVehiclesModel = apply(u.resultName)(rs)
  def apply(u: ResultName[UserVehiclesModel])(rs: WrappedResultSet) = new UserVehiclesModel(
    rs.get(u.id),
    rs.get(u.userName), rs.get(u.name), rs.get(u.lastName), rs.get(u.age),
    rs.get(u.mail), rs.get(u.phono), rs.get(u.perfil)
  )

  def apply(u: SyntaxProvider[UserVehiclesModel],  v: SyntaxProvider[VehicleModel])(rs: WrappedResultSet):
  UserVehiclesModel = (apply(u)(rs)).copy(vehicles = Some(VehicleModel(v)(rs)))

}
