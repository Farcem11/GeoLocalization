<?php
include("DataBaseClass.php");
$dataBase = new DataBase("mysql.hostinger.es", "u742800395_user", "qwerty123", "u742800395_geo");

$Name = $_POST["Name"];
$Image = base64_decode($_POST["Image"]);
$Image = mysqli_real_escape_string($Image);
$Latitude = (double)$_POST["Latitude"];
$Longitude = (double)$_POST["Longitude"];
$Planter = $_POST["Planter"];
$Donor = $_POST["Donor"];

echo $dataBase->doQuery("insert into Plants(Name, Image, Latitude, Longitude, Planter, Donor)values('$Name', '$Image', $Latitude, $Longitude, '$Planter', '$Donor');");
$dataBase->close();
?>