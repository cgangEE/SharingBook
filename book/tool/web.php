<?php
	function httpGet($ustuid, $upwd, $ugrade, $url){
		$data = array(
				'nianji'=>$ugrade,
				'xuehao'=>$ustuid,
				'mima'=>$upwd
				);
		$data = http_build_query($data);

		$opts = array(
			'http' => array(
				'method' => 'POST',
				'header'=> 'Content-type:application/x-www-form-urlencoded',
				'content' => $data
				)
		);
		
		$context = stream_context_create($opts);
		$result = file_get_contents($url, false, $context);

		$result = mb_convert_encoding($result, 'utf-8', 'gbk');
		return $result;
	}
?>
