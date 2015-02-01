<?php
	require_once 'info.php';
	require_once 'conn.php';


	mysql_query("DROP TABLE user", $con);
	mysql_query("set character_set_server=utf8", $con);
	mysql_query("set character_set_database=utf8", $con);
	$user ="CREATE TABLE user(
		uid int PRIMARY KEY NOT NULL AUTO_INCREMENT,
		uname varchar(50),
		uphone int,
		ustuid varchar(20),
		upwd varchar(50),
		umd5 varchar(255),
		ugrade varchar(10),
		upic varchar(255),
		usex varchar(10)
	)";

	if (!mysql_query($user, $con))
		die(mysql_error());
	else
		echo 'OK';
	
	mysql_close($con);
?>
