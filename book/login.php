<?php
	require_once 'tool/info.php';
	require_once 'tool/conn.php';

	function inDatabase($ustuid, $umd5){
		global $con;
		$result = mysql_query("SELECT * FROM user WHERE umd5='$umd5' AND ustuid='$ustuid'", $con);
		$row = mysql_fetch_array($result);
		if (!$row) return false;
		return true;
	}

	$umd5 = $_GET['umd5'];
	$ustuid = $_GET['ustuid'];

	if (inDatabase($ustuid, $umd5)==true)
		echo '1'; 
	else echo '0';
?>
