package com.pvergara.lab.mysql.model

import scalikejdbc._

// Modelo de datos que representan la tabla user
case class UserModel(id: Int, userName: String, name: String, lastName: String, age: Int, mail: String, phono: String, perfil: String)
// Modelo de datos que representan la tabla vehicle
case class VehicleModel(vehicleId: Int, plate: String, imeiGps: String, lastLongitud: Double, lastLatitude: Double, userId: Int)
case class UserVehiclesModel(id: Int, userName: String, name: String, lastName: String, age: Int, mail: String, phono: String, perfil: String, vehicles: Option[VehicleModel] = None)


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
