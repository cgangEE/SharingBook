<?php

	require_once 'tool/info.php';
	require_once 'tool/conn.php';
	require_once 'tool/web.php';

	function check($ustuid, $upwd, $ugrade){
		$url = "http://jw.zzu.edu.cn/scripts/qscore.dll/search";
		$result = httpGet($ustuid, $upwd, $ugrade, $url);
		if (strstr($result, "成绩单")===false) 
			return false;
		return true;
	}

	function inDatabase($ustuid, $upwd, $ugrade){
		global $con;
		$result = mysql_query("SELECT * FROM user WHERE ustuid='$ustuid' AND upwd='$upwd' AND ugrade='$ugrade'", $con);
		$row = mysql_fetch_array($result);
		if (!$row) return false;
		return true;
	}

	function strGet($str, $begin, $end){
		$x = strstr($str, $begin);
		$result="";
		for ($i = strlen($begin); $x[$i]!=$end; $i++)
			$result = $result . $x[$i];
		return $result;
	}

	function register($ustuid, $upwd, $ugrade){
		$umd5 = md5($ustuid.$upwd);
		$url = "http://jw.zzu.edu.cn/scripts/stuinfo.dll/check";
		$result = httpGet($ustuid, $upwd, $ugrade, $url);
		$uname = strGet($result, "姓&nbsp;&nbsp;&nbsp; 名：", '<');
		$usex = strGet($result, "性&nbsp;&nbsp;&nbsp; 别：", '<');
		$upic = strGet($result, "<img src=\"", '"');
		global $con;
		$sql = "INSERT INTO user(uname, ustuid, upwd, umd5, ugrade, upic, usex, uphone) VALUES ('$uname', '$ustuid', '$upwd', '$umd5', '$ugrade', '$upic', '$usex', '$uphone')";
		$x = mysql_query($sql, $con);
	}


	$ustuid = $_GET['ustuid'];
	$upwd = $_GET['upwd'];
	$ugrade = $_GET['ugrade'];
	$uphone = $_GET['uphone'];

	if (inDatabase($ustuid, $upwd, $ugrade)==true)
		echo '0';  //echo "您的学号曾经注册过，请您直接登录";
	else if (check($ustuid, $upwd, $ugrade, $uphone)==true) {
		register($ustuid, $upwd, $ugrade);
		echo '1';	//echo '恭喜您，注册成功！';
	}
	else echo '2';	//echo '年级、学号或密码错误\n请您重新输入';
?>
