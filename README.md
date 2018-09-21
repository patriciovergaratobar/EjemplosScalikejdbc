# EjemplosScalikejdbc
Ejemplo de conexión a un Mysql con Scalikejdbc

## Crea la base de datos chucrutdb
```sql
CREATE SCHEMA `chucrutdb` DEFAULT CHARACTER SET utf8 ;
```
## Importa la tablas con datos de prueba a chucrutdb
Debes importar desde Workbench el script dbMysql.sql o ejecutando el siguiente comando e una terminal

```sh
mysql --protocol=tcp --host=localhost --user=root --port=3306 --default-character-set=utf8 --comments --database=chucrutdb  < "EjemplosScalikejdbc/script/dbMysql.sql"
```
Con esto debiera bastar para que puedas probar los ejemplos que se encuentran en el código.

