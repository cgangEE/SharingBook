<?php
	require_once 'tool/info.php';
	require_once 'tool/conn.php';

	function inDatabase($ustuid, $umd5){
		global $con;
		$result = mysql_query("SELECT * FROM user WHERE umd5='$umd5' AND ustuid='$ustuid'", $con);
		$row = mysql_fetch_array($result);
		if ($row){
			echo json_encode($row);
		}
	}

	$umd5 = $_GET['umd5'];
	$ustuid = $_GET['ustuid'];

	inDatabase($ustuid, $umd5);
?>
