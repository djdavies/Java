<?php
include 'getuidcookie.php';
include 'nocache.php';
require 'mystdfunc.php';

//$uid = $HTTP_COOKIE_VARS['uid'];

//$album = $_GET['albumdirectory'];


$clearform = isset($_POST['clear']);

if ($clearform) {
	$gamedata = array();
} else {
	// copy form data
	foreach ($_POST as $key => $val) {
		$gamedata[$key] = $val;
	}
}



$config = "";
$typedef = "";
if (!$clearform) {

	foreach ($gamedata as $key => $val) {
		if (isset($val) && strcmp($val,"")!=0) {
			if (strcmp($key,"submit")==0) {
				// skip submit field
			} else if(is_numeric($val) && strcmp(substr($key,-4),"type")==0){
				// tile or object type -> integer
				$config .= "gp_$key\t$key\tint\t$val\n";
				$typedef .= "\tint gp_$key=$val\n";
			} else if (is_numeric($val)) {
				// other numerical -> double
				$config .= "gp_$key\t$key\tdouble\t$val\n";
				$typedef .= "\tdouble gp_$key=$val\n";
			} else if (strcmp($val,"on")==0) {
				// "on" -> boolean
				// XXX we assume there is no string called "on"
				$config .= "gp_$key\t$key\tboolean\ttrue\n";
				$typedef .= "\tboolean gp_$key=false\n";
			} else {
				// string otherwise
				$config .= "gp_$key\t$key\tString\t$val\n";
				$typedef .= "\tString gp_$key=\"$val\"\n";
			}
		}
	}
	#echo $typedef;
	writefile($config,$uid.".appconfig");


}

writeattributefile($gamedata,$uid.".game");

if ($clearform) {
	header("Location: fillgameform.php#form"); 
} else if ($badinput) {
	header("Location: fillgameform.php?error=badinput#form"); 
} else if ($undefinput) {
	header("Location: fillgameform.php?error=undefinput#form"); 
} else if ($counterror) {
	header("Location: fillgameform.php?error=counterror#form"); 
} else {
	header("Location: playgame.php"); 
}
?>
