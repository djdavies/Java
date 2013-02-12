<?php
include 'getuidcookie.php';
include 'nocache.php';
require 'mystdfunc.php';

$gamedata = parseattributefile($uid.".game");
?>

<html><head><title><?php echo $gamedata['gamename']; ?></title></head>
<body bgcolor=#444488 text=#ccccee link=#eeccff vlink=#cc88ff alink=#ffaaff>
<center>
<applet code="gamegen.SimpleGeneratedGame" width="640" height="480"
archive="jgame-gamegen.jar" >
<param name="configfile" value=
"http://wwwhome.cs.utwente.nl/~schooten/onlinegamegenerator/sessiondata/<?php
echo $uid.".appconfig"; ?>">
<?php echo $gamedata['gamename']; ?>
</applet>
<p>

Controls: cursor keys=move,<br>
W,S,A,D=shoot in specific direction,<br>
Z=shoot in case direction is not applicable.

<P>

Click inside the game window to get focus.
<P>

<a href="sessiondata/<?php echo $uid.".appconfig"; ?>">Download appconfig
file here<br> (these can be loaded by the SimpleGeneratedGame class.</a>

<P>

<a href="fillgameform.php">Adjust the game parametera</a>
</center>
</body></html>


