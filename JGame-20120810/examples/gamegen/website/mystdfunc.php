<?php
//20051209 adopted generalised functions from the nl-art parsefiles.php and
//commitorder.php
//20070327 adapted for jgame gamegen

// global application-specific settings

$webmaster_email = "schooten@cs.utwente.nl";

// used for reading/writing files
// must have trailing slash
$global_basedir =
"/WebSpace/Tki/parlevink/schooten/public_html/onlinegamegenerator/sessiondata/";



// Make php shut up about stat and fopen failures
error_reporting (E_ALL ^ (E_NOTICE | E_WARNING));


// present an error to the user
setlocale (LC_ALL, 'nl_NL');
function produceerror($errorcode) {
	global $webmaster_email;
	echo "<BR><h1>$errorcode.</h1><P>";
	echo "For problem and  bug reports, mail:";
	echo "<i>$webmaster_email</i>";
	exit();
}

//write access stats for this user
function writeaccesslog($basename,$userinfo) {
	global $global_basedir;
	if (!file_exists($global_basedir.$basename.".hits")) {
		$f = fopen($global_basedir.$basename.".hits","w");
	} else {
		$f = fopen($global_basedir.$basename.".hits","a+");
	}
	if (!$f) return; //exit silently on error
	fwrite($f,date("d m Y H:i:s $userinfo\n"));
	fclose($f);
}


//write http client info for this user
function saveuserinfo($userid) {
	global $_SERVER;
	if (!$userid) return;
	$userinfo = parseattributefile($userid.".clientinfo");
	$userinfo['user_agent']  = $_SERVER['HTTP_USER_AGENT'];
	$userinfo['referer']     = $_SERVER['HTTP_REFERER'];
	$userinfo['remote_addr'] = $_SERVER['REMOTE_ADDR'];
	//$userinfo['remote_host'] = $_SERVER['REMOTE_HOST'];
	$userinfo = writeattributefile($userinfo,$userid.".clientinfo");
}

//get a unique 6-digit id for later reference, such as order id
function getuniqueorderid() {
	global $global_basedir;
	while (TRUE) {
		$order = mt_rand(100000,999999);
		if (!stat($global_basedir.$order.".ordernr")) break;
	}
	$f=fopen($global_basedir.$order.".ordernr","w");
	fclose($f);
	return $order;
}


// find all files in given $dir which end with the characters $extension
// chops off extension
function myglob($dir,$extension) {
	global $global_basedir;
	$retval = array();
	if ($handle = opendir($global_basedir.$dir)) {
		while (false !== ($file = readdir($handle))) {
			if (!strcmp($extension,
			substr($file,-strlen($extension)))) {
				$retval[] = substr($file,0,strlen($file)-strlen($extension));
			}
		}
	}
	return $retval;
}

function writefile($data,$path) {
	global $global_basedir;
	$f = fopen($global_basedir.$path,"w");
	if (!$f) return; //exit silently on error
	fwrite($f,$data);
	fclose($f);
}

// Parses a text file of the format: (<id>: <value>\n)*
// Any line without `<id> :' is skipped.
// The value of a line with just `<id> :' is TRUE
// `:' can not yet be escaped by `\:'
// Returns empty array when file does not exist.
function parseattributefile($path) {
	global $global_basedir;
	$returnvalue=array();
	if (!file_exists($global_basedir.$path)) { 
		return $returnvalue;
	}
	$file = fopen($global_basedir.$path,"r");
	if (!$file) {
		produceerror("Kan file $path niet openen");
	}
	while (!feof($file)) {
		$line = fgets($file);
		if ($line) {
			$lineinfo = split(":",$line,2);
			if (count($lineinfo)>2)
				produceerror("parseererror in file $path");
			else if (count($lineinfo)==2 && strlen(trim($lineinfo[1]))==0)
				$returnvalue[trim($lineinfo[0])] = "";
			else if (count($lineinfo)==2)
				$returnvalue[trim($lineinfo[0])] = trim($lineinfo[1]);
			/* count==0 or string empty -> do nothing */
		}
	}
	fclose($file);
	return $returnvalue;
}

// Writes given array keys/values as key: value\n
// Creates new file when it does not exist.
function writeattributefile($array,$path) {
	global $global_basedir;
	$file = fopen($global_basedir.$path,"w");
	if (!$file) {
		produceerror("Kan file $path niet schrijven");
	}
	foreach ($array as $key => $value) {
		fwrite($file,$key.":".$value."\n");
	}
	fclose($file);
}



/* text functions */

function euroformat($eurocents) {
	return str_replace(".",",",sprintf("EUR %01.2f",$eurocents/100));
}


/* $attdata is hashtable, key is the name of the key. Outputs in html: form
 * value (assumes form name is already output), and a td with error
 * information if present.  Error information is represented as '#<text>'
 * after the field data. This means '#' may not be used in the client field.
 */
function insertformfield($attdata,$key) {
	if (isset($attdata[$key])) {
		$value = $attdata[$key];
	} else {
		$value = "#";
	}
	$value = explode("#",$value);
	print "value='".$value[0]."'>";
	if (count($value) == 2) {
		echo "</td><td><font color=red>".$value[1]."</font>";
	}
}

/* $attdata is hashtable, key is the name of the key. Outputs in html:
 * "checked" if $key exists in $attdata. */
function insertchecked($attdata,$key) {
	if (isset($attdata[$key])) {
		echo " checked ";
	}
}


/* new functions that insert the entire fields */

/* fill in $attdata[$name] if it exists. Does not handle error messages yet */
function inserttextfield($attdata,$name,$size) {
	echo "<input type=\"text\" name=\"".$name."\" size=".$size;
	if (isset($attdata[$name])) {
		echo " value=\"".$attdata[$name]."\"";
	}
	echo ">";
}

/* set checked if $attdata contains key $name */
function insertcheckbox($attdata,$name) {
	echo "<input type=\"checkbox\" name=\"".$name."\"";
	if (isset($attdata[$name])) {
		echo " checked";
	}
	echo ">";
}

/* $attdata is hashtable, name is the name of the name, $value is the value
 * that the name should have when button is checked. Outputs in html:
 * '<input type="radio" name="$name" value = "$value">',
 * insert "checked" if $name == $value in $attdata. */
function insertradiobtn($attdata,$name,$value) {
	echo "<input type=\"radio\" name=\"".$name."\" value=\"".$value."\"";
	if (isset($attdata[$name])
	&&  strcmp($attdata[$name],$value)==0) {
		echo " checked ";
	}
	echo ">";
}

/* $values is a hashtable of options, selected is set if $values[$val] ==
 * $attdata[$name] */
function insertselect($attdata,$name,$values) {
	echo "<select name=\"".$name."\">\n";
	foreach ($values as $val => $desc) {
		echo "<option value=\"".$val."\"";
		if (isset($attdata[$name])
		&&  strcmp($attdata[$name],$val)==0) {
			echo " selected";
		}
		echo ">$desc\n";
	}
	echo"</select>\n";
}


/* text field normalisation and syntax checks */

/* chop whitespaces at beginning and end, and dangerous chars `#' `<' `>' */
function chopdelimiters($str) {
	$str = str_replace("#"," ",$str);
	$str = str_replace("'","`",$str);
	$str = str_replace("\\"," ",$str);
	$str = str_replace(">"," ",$str);
	$str = str_replace("<"," ",$str);
	$str = str_replace("\t"," ",$str);
	$str = str_replace("\n"," ",$str);
	$str = trim($str);
	return $str;
}

// for first names, surnames
function checkifpersonname($str) {
	for ($i=0; $i<strlen($str); $i++) {
		$chr = $str[$i];
		if ($chr >= 'a' && $chr <='z') continue;
		if ($chr >= 'A' && $chr <='Z') continue;
		if ($chr == ' ') continue;
		if ($chr == '-' || $chr == '/' || $chr=='\'' || $chr=='`') continue;
		if ($chr == '.' || $chr == ',') continue;
		if ($chr == '(' || $chr == ')') continue;
		return FALSE;
	}
	return TRUE;
}

// for phone numbers
function checkifphonenumber($str) {
	for ($i=0; $i<strlen($str); $i++) {
		$chr = $str[$i];
		if ($chr >= '0' && $chr <='9') continue;
		if ($chr == '-' || $chr == '+') continue;
		if ($chr == ' ') continue;
		return FALSE;
	}
	return TRUE;
}

// check if Dutch zip code
function checkifpostcode($str) {
	if (strlen($str) < 6 || strlen($str) > 8) return FALSE;
	for ($i=0; $i<4; $i++) {
		$chr = $str[$i];
		if ($chr >= '0' && $chr <='9') continue;
		return FALSE;
	}
	for ($i=strlen($str) - 2; $i<strlen($str); $i++) {
		$chr = $str[$i];
		if ($chr >= 'a' && $chr <='z') continue;
		if ($chr >= 'A' && $chr <='Z') continue;
		return FALSE;
	}
	return TRUE;
}

// check if valid email address
function checkifemail($str) {
	$nrats=0;
	$last_at=0;
	for ($i=0; $i<strlen($str); $i++) {
		if ($str[$i] == '@') {
			$nrats++;
			$lastat = $i;
		}
	}
	if ($nrats != 1) return FALSE;
	if ($lastat <= 1 || $lastat >= strlen($str)-2) return FALSE;
	return TRUE;
}


/* old stuff which may be reused later */


/* client address data */

/* the client address/data file has format:
** <id> `:' <value> [ `#' <errorcode> ]
**
** the <id>s are:
** voornaam, achternaam, straat, postcode, woonplaats, telefoon, email
*/




/* albums */


function getpicinfo($picandformatspec) {
/* $picandformatspec is a string of the format <album>/<picname>#<formatspec>*/
/* Returns array with the following keys:
 * `name', `basedir', `format', `title', `formattitle', `price' */
	$arr = explode("#",$picandformatspec);
	if (count($arr)!=2)
		produceerror("plaatjesspecificatie $key klopt niet");
	$picname   = $arr[0];
	$picformat = $arr[1];
	$picattrs = parseattributefile("$picname.picinfo");
	$picformatnameprice = explode("#",$picattrs[$picformat]);
	if (count($picformatnameprice)!=2)
		produceerror("artikeltype/prijs $picattrs[$picformat] klopt niet");
	$retval['name'] = $picname;
	if (isset($picattrs['basedir'])) {
		$retval['basedir'] = $picattrs['basedir'];
	} else {
		$retval['basedir'] = "";
	}
	$retval['format'] = $picformat;
	$retval['title'] = $picattrs['title'];
	$retval['author'] = $picattrs['author'];
	$retval['formattitle']= $picformatnameprice[0];
	$retval['price'] = $picformatnameprice[1];
	return $retval;
}

function getpicalbum($picspec) {
	$arr = explode("#",$picspec);
	$arr2 = explode("/",$arr[0]);
	if (count($arr2) != 2)
		produceerror("plaatjesspecificatie $picspec fout");
	return $arr2[0];
}

function getpicbasename($picspec) {
	// output: filename w/o directory and w/o extension
	$arr = explode("#",$picspec);
	$arr2 = explode("/",$arr[0]);
	if (count($arr2) != 2)
		produceerror("plaatjesspecificatie $picspec fout");
	$arr3 = explode(".",$arr2[1]);
	return $arr3[0];
}


function getpicurl($albumname,$picname) {
//$picname may be either basename or with suffix .<a>
//returns url including given suffix
	list($picbase,$picext) = explode(".",$picname);
	$picattrs = parseattributefile("$albumname/$picbase.picinfo");
	if (isset($picattrs['basedir'])) {
		return $picattrs['basedir'].$picname;
	} else {
		return $albumname."/".$picname;
	}
}


/* order/shopping cart */

function orderfile_exists($orderid) {
	if (!file_exists("$orderid.order")) return FALSE;
	$items = readorderfile($orderid);
	return (count($items) != 0);
}

function readorderfile($orderid) {
/* returns a hashtable of orders, consisting of
 * key=<album>/<pic>#<format> value=<quantity>
 * If there's no order file, returns empty array */
	$retval = array();
	$file=fopen("$orderid.order","r");
	if (!$file) return $retval;
	while (!feof($file)) {
		$line = fgets($file);
		$line = trim($line);
		if (!$line) break;
		$arr = explode("#",$line);
		if (count($arr) != 3) break;
		$retval[$arr[0]."#".$arr[1]] = $arr[2];
	}
	fclose($file);
	return $retval;
}

function writeorderfile($orderid,$items) {
/* items is a hashtable key=<album>/<pic>#<format> value=<quantity> */
	$file = fopen("$orderid.order","w");
	if (!$file) produceerror("Kan order niet aanmaken");
	foreach ($items as $key => $value) {
		if (!fwrite($file, $key."#".$value."\n")) {
			fclose($file);
			produceerror("Kan order niet wegschrijven");
		}
	}
}

function addtoorder(&$items,$item,$quantity) {
	if (!array_key_exists($item, $items)) {
		$items[$item] = $quantity;
	} else {
		$items[$item] += $quantity;
	}
}


?>
