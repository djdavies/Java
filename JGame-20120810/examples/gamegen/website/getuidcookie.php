<?php
/* generate id cookie */
if (!$HTTP_COOKIE_VARS['uid']) {
	$uid = uniqid(rand());
	setcookie("uid",$uid);
	//saveuserinfo($uid);
} else {
	$uid = $HTTP_COOKIE_VARS['uid'];
}
?>
