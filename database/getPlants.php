<?php
include("DataBaseClass.php");
$dataBase = new DataBase("mysql.hostinger.es", "u742800395_user", "qwerty123", "u742800395_geo");

echo $dataBase->doQuery("Select * From Plants;");
$dataBase->close();
?>