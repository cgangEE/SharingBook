<?php
	$con=mysql_connect($host, $user, $pwd);
	if (!$con)
		die(mysql_error());
	mysql_query("DEFAULT CHARACTER SET utf8;", $con);
	$x = mysql_select_db($db_name, $con);
	if (!$x){
		if (!mysql_query("CREATE DATABASE $db_name", $con))
			die(mysql_error());
	}
//	mysql_query("SET CHARACTER SET UFT8");
?>
