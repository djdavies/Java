<?php
include 'getuidcookie.php';
include 'nocache.php';
require 'mystdfunc.php';

//$album = $_GET['albumdirectory'];

$gamedata = parseattributefile($uid.".game");

$sprites = array(
	"white16"  => "16x16 white",
	"red16"    => "16x16 red",
	"magenta16"=> "16x16 magenta",
	"blue16"   => "16x16 blue",
	"cyan16"   => "16x16 cyan",
	"green16"  => "16x16 green",
	"yellow16" => "16x16 yellow",
	"orange16" => "16x16 orange",
	"grey16" => "16x16 grey",
	"ltred16" => "16x16 light red",
	"ltmagenta16" => "16x16 light magenta",
	"ltblue16" => "16x16 light blue",
	"dkcyan16" => "16x16 dark cyan",
	"dkgreen16" => "16x16 dark green",
	"brown16" => "16x16 brown",
	//"white8"  => "8x8 white",
	//"red8"    => "8x8 red",
	//"magenta8"=> "8x8 magenta",
	//"blue8"   => "8x8 blue",
	//"cyan8"   => "8x8 cyan",
	//"green8"  => "8x8 green",
	//"yellow8" => "8x8 yellow",
);
$tiles = array(
	"k" => "black",
	"w" => "white",
	"r" => "red",
	"m" => "magenta",
	"b" => "blue",
	"c" => "cyan",
	"g" => "green",
	"y" => "yellow",
	"or" => "orange",
	"gr" => "grey",
	"lr" => "light red",
	"lm" => "light magenta",
	"lb" => "light blue",
	"dc" => "dark cyan",
	"dg" => "dark green",
	"br" => "brown",
);
 
$types = array(
	"1" => "1",
	"2" => "2",
	"4" => "4",
	"8" => "8");
$typecombos = array(
	"none",
	"1", "2", "1+2",
	"4", "1+4", "2+4", "1+2+4",
	"8", "1+8", "2+8", "1+2+8", "4+8", "1+4+8", "2+4+8","1+2+4+8",
);

$fulltypecombos = array(
	"none",
	"1", "2", "1+2",
	"4", "1+4", "2+4", "1+2+4",
	"8", "1+8", "2+8", "1+2+8", "4+8", "1+4+8", "2+4+8","1+2+4+8",
	"player bullet",
	"bullet+1", "bullet+2", "bullet+1+2",
	"bullet+4", "bullet+1+4", "bullet+1+2", "bullet+1+2+4",
	"bullet+8", "bullet+1+8", "bullet+2+8", "bullet+1+2+8",
	"bullet+4+8", "bullet+1+4+8", "bullet+2+4+8","bullet+1+2+4+8",
);

$direction = array(
	"left"  => "left",
	"right" => "right",
	"up"    => "up",
	"down"  => "down",
);
$axis = array(
	"horizontal" => "horizontal",
	"vertical"   => "vertical",
);
$location = array(
	"random"   => "random location",
	"corner"   => "any side of screen",
	"top"      => "top",
	"bottom"   => "bottom",
	"topbottom"=> "top or bottom",
	"left"     => "left",
	"right"    => "right",
	"leftright"=> "left or right",
);

$bouncedir = array(
	"none"     => "neither side of screen",
	"any"  	   => "any side of screen",
	"top"      => "top",
	"bottom"   => "bottom",
	"topbottom"=> "top or bottom",
	"left"     => "left",
	"right"    => "right",
	"leftright"=> "left or right",
);

$motion = array(
	"center" => "towards center",
	"player" => "towards player",
	"random" => "in random direction",
	"not" => "not",
);

$shootdir = array(
	"player" => "towards player",
	"left"  => "left",
	"right" => "right",
	"up"    => "up",
	"down"  => "down",
);
$dieopts = array(
	//"not"  => "nothing happens",
	"die"  => "agent disappears",
	"create1"  => "agent is replaced by agent type 1",
	"create2"  => "agent is replaced by agent type 2",
	"create3"  => "agent is replaced by agent type 3",
	"create4"  => "agent is replaced by agent type 4",
	"create5"  => "agent is replaced by agent type 5",
);
?>

<html><head><title>The Online (Prototype) Action Game Generator</title>
<meta HTTP-EQUIV="Pragma" CONTENT="no-cache">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body bgcolor=#444488 text=#ccccee link=#eeccff vlink=#cc88ff alink=#ffaaff>

<h1>The Online (Prototype) Action Game Generator (1.3 beta)</h1>

<P>

<table width=700><tr><td>

Just fill in the form to create an action game online!  The form will be
translated to a parameter file which is read by the JGame SimpleGeneratedGame
applet. 

<p>
<b>Notes</b><br>
Game will be 640x480, 35fps, with 40x30 tiles, 16x16 pixels each.
<br>
(this is fixed, maybe configurable in later version)
<br>
Distances are in pixels, speeds in pixels/frame.
<br>
The game has levels, but levels are not progressively difficult yet.
<br>
When you leave a field blank, a "sensible" default value will be used.
<P>

<?php 
if (isset($_GET['error'])) {
	$formerr = $_GET['error'];
	echo "<P><strong><font color=red>";
	echo "The form contains errors. ";
	if ($formerr == "badinput") {
		echo "Some fields were incorrectly filled in.";
	}
	if ($formerr == "undefinput") {
		echo "Sommige fields were not filled in.";
	}
	echo "</font></strong><P>";
}
?>

</td></tr></table>


<form action="processgameform.php" method="POST">
<?php
//echo "<input type='hidden' name='albumdirectory' value='$album'>\n";
?>

<table>
<tr><td>
Name of game:
</td><td>
<?php inserttextfield($gamedata,"gamename",30); ?>
</td></tr>
</table>

<P>

<table>
<?php

//$qa_table = readqafile();

//srand(hexdec(substr($uid,0,6)));
//shuffle($qa_table);

//foreach ($qa_table as $idx => $value) {
//	$qidx = $value[0];
//	echo "<tr><td>\n";
//	echo "<input type=checkbox name=\"q$qidx\" ";
//	insertchecked($gamedata,"q$qidx");
//	echo ">".$value[1]."<br>\n";
//	echo "</td></tr>\n";
//}

?>
</table>

<P>

<hr>
Background Base
(choose one)

<P>

<!--
[ ] empty
[ ] border
[ ] maze: from ____ segments to ____ segments,
          background type ____
          Appearance: [choose a tile]
-->
<?php insertradiobtn($gamedata, "basebg","empty"); ?>Empty<br>


<?php insertradiobtn($gamedata, "basebg","border"); ?>Border<br>


<?php insertradiobtn($gamedata, "basebg","wallhoriz"); ?>Border
	+ horizontal wall<br>


<?php insertradiobtn($gamedata, "basebg","wallvert"); ?>Border
	+ 2 vertical walls<br>


<?php insertradiobtn($gamedata, "basebg","maze"); ?>Grid,

width <?php inserttextfield($gamedata,"basebgmazexsize",5)?> segments,
height <?php inserttextfield($gamedata,"basebgmazeysize",5)?> segments,
background type <?php insertselect($gamedata,"basebgtype",$types); ?>

<p>

appearance: <?php insertselect($gamedata,"basebgtile",$tiles); ?> on
	<?php insertselect($gamedata,"basebgfill",$tiles); ?>.

<P>

<hr>
Background additional random tiles
(determine up to 3 additional types)
<P>


<?php
for ($i=1; $i<=3; $i++) {
?>

	<table><tr>
	<td><?php echo $i; insertcheckbox($gamedata,"randombg".$i); ?>:</td>
	<td>density <?php inserttextfield($gamedata,"randombg".$i."density",5)?>
	percent, background type 
	<?php insertselect($gamedata,"randombg".$i."type",$types); ?>,
	appearance <?php insertselect($gamedata,"randombg".$i."tile",$tiles); ?>.
	</td></tr></table>

<?php
}
?>

<hr>
Player
(background will be cleared where the player is created;
player is object type 32, bullet is object type 16)

<P>

appearance: <?php insertselect($gamedata,"playersprite",$sprites); ?>
<P>

initial position: x=<?php inserttextfield($gamedata,"playerx",5)?>,
                  y=<?php inserttextfield($gamedata,"playery",5)?>.

<P>
horizontal motion:
<br>
<?php insertradiobtn($gamedata, "playerxmove","off"); ?>off<br>
<?php insertradiobtn($gamedata, "playerxmove","linear"); ?>linear,
	speed <?php inserttextfield($gamedata,"playerxspeed",5)?><br>
<?php insertradiobtn($gamedata, "playerxmove","accel"); ?>inertial,
	acceleration <?php inserttextfield($gamedata,"playerxaccel",5)?><br>
<P>
vertical motion:<br>

<?php insertradiobtn($gamedata, "playerymove","off"); ?>off<br>
<?php insertradiobtn($gamedata, "playerymove","linear"); ?>linear,
	speed <?php inserttextfield($gamedata,"playeryspeed",5)?><br>
<?php insertradiobtn($gamedata, "playerymove","accel"); ?>inertial,
	acceleration <?php inserttextfield($gamedata,"playeryaccel",5)?><br>
<P>


background interaction:
<br>
bump into background type
	<?php insertselect($gamedata,"playerbumpbgtype",$typecombos); ?> <br>
die when touching background type
	<?php insertselect($gamedata,"playerdiebgtype",$typecombos); ?>
<P>

object interaction:
<br>
die when touching object type
	<?php insertselect($gamedata,"playerdieobjtype",$typecombos); ?> <br>
pick up object type 
	<?php insertselect($gamedata,"playergetobjtype",$typecombos); ?>,
	scoring <?php inserttextfield($gamedata,"playergetscore",5)?>
<P>

shooting:
<br>
<?php insertradiobtn($gamedata, "playershoot","off"); ?>off<br>
<?php insertradiobtn($gamedata, "playershoot","1dir"); ?>1 direction,
	<?php insertselect($gamedata,"playershootdir",$direction); ?><br>
<?php insertradiobtn($gamedata, "playershoot","2dir"); ?>2 directions,
	<?php insertselect($gamedata,"playershootaxis",$axis); ?><br>
<?php insertradiobtn($gamedata, "playershoot","4dir"); ?>4 directions<br>
<?php insertradiobtn($gamedata, "playershoot","alldir"); ?>any direction,
	following player movement direction<br>
<P>

bullet speed: <?php inserttextfield($gamedata,"playerbulletspeed",5)?>,
maximum number of bullets:
	<?php inserttextfield($gamedata,"playermaxbullets",5)?>,
bullet disappears when hitting background type
	<?php insertselect($gamedata,"playerbulletdiebgtype",$typecombos); ?>.

<P><hr>

Level completion
(choose zero or more of the following, zero means no levels)

<P>
<?php insertcheckbox($gamedata, "endleveltimeout"); ?>timeout:
	<?php inserttextfield($gamedata,"endleveltimeoutlen",5)?> seconds
<br>
<?php insertcheckbox($gamedata, "endlevelobjcount"); ?>object type removed:
	<?php insertselect($gamedata,"endlevelobjtype",$typecombos); ?>
<br>
score <?php inserttextfield($gamedata,"endlevelscore",5)?> points.

<P>

<input type='submit' name="submit" value='Generate game!'>

<input type="submit" name="clear" value="Erase form">

<P><hr>

Agents 
(will disappear when they move off screen; will not start at the same row or
column as the player or inside background)
(determine up to 5 agent types)
<P>

<?php
for ($i=1; $i<=5; $i++) {
?>

<table><tr>
<td valign=top><?php echo $i.":"; insertcheckbox($gamedata,"agent$i"); ?></td><td>
object type: <?php insertselect($gamedata,"agent$i"."type",$types);?>
	<P>
appearance: <?php insertselect($gamedata,"agent$i"."sprite",$sprites);?>
	<P>
create: at <?php insertselect($gamedata,"agent$i"."createloc",$location);?>,
	<?php inserttextfield($gamedata,"agent$i"."createinit",5)?>
	objects at beginning of level,
	and every <?php inserttextfield($gamedata,"agent$i"."createinterval",5)?>
	seconds
	starting from <?php inserttextfield($gamedata,"agent$i"."createbegin",5)?>
	seconds 
	until <?php inserttextfield($gamedata,"agent$i"."createend",5)?> seconds
	<P>
move horizontal:
	speed <?php inserttextfield($gamedata,"agent$i"."xspeed",5)?>, initially
	move <?php insertselect($gamedata,"agent$i"."movexinit",$motion);?>.<br>
	<?php insertcheckbox($gamedata,"agent$i"."movexrandom");?>move randomly,
		change direction every 
		<?php inserttextfield($gamedata,"agent$i"."movexrandomchg",5)?> seconds	
		<br>
	<?php insertcheckbox($gamedata,"agent$i"."movextoplayer");?>move towards
		player, when distance 
		between <?php inserttextfield($gamedata,"agent$i"."movextoplayermin",5)?>
		and <?php inserttextfield($gamedata,"agent$i"."movextoplayermax",5)?>
		percent of screen<br>
	<?php insertcheckbox($gamedata,"agent$i"."movexfrplayer");?>move away from
		player, when distance 
		between <?php inserttextfield($gamedata,"agent$i"."movexfrplayermin",5)?>
		and <?php inserttextfield($gamedata,"agent$i"."movexfrplayermax",5)?>
		percent of screen<br>
	<P>
move vertical:
	speed <?php inserttextfield($gamedata,"agent$i"."yspeed",5)?>, initially
	move <?php insertselect($gamedata,"agent$i"."moveyinit",$motion);?>.<br>
	<?php insertcheckbox($gamedata,"agent$i"."moveyrandom");?>move randomly,
		change direction every 
		<?php inserttextfield($gamedata,"agent$i"."moveyrandomchg",5)?> seconds	
		<br>
	<?php insertcheckbox($gamedata,"agent$i"."moveytoplayer");?>move towards
		player, when distance 
		between <?php inserttextfield($gamedata,"agent$i"."moveytoplayermin",5)?>
		and <?php inserttextfield($gamedata,"agent$i"."moveytoplayermax",5)?>
		percent of screen<br>
	<?php insertcheckbox($gamedata,"agent$i"."moveyfrplayer");?>move away from
		player, when distance 
		between <?php inserttextfield($gamedata,"agent$i"."moveyfrplayermin",5)?>
		and <?php inserttextfield($gamedata,"agent$i"."moveyfrplayermax",5)?>
		percent of screen<br>

	<P>
<?php insertcheckbox($gamedata,"agent$i"."shoot");?> shoot
	every <?php inserttextfield($gamedata,"agent$i"."shootfreq",5)?>
	seconds in direction
	<?php insertselect($gamedata,"agent$i"."shootdir",$shootdir);?>
	with bullet speed
	<?php inserttextfield($gamedata,"agent$i"."shootspeed",5)?>
	and bullet type
	<?php insertselect($gamedata,"agent$i"."bullettype",$types);?>
	<P>
interaction:<br>
	bounce off 
	<?php insertselect($gamedata,"agent$i"."bouncesides",$bouncedir);?>.<br>
	die when hitting object of type
	<?php insertselect($gamedata,"agent$i"."dieobjtype",$fulltypecombos);?>.<br>
	bump into background type 
	<?php insertselect($gamedata,"agent$i"."blockbgtype",$typecombos);?>.<br>
	die when hitting background type 
	<?php insertselect($gamedata,"agent$i"."diebgtype",$typecombos);?>.<br>
	die action: 
	<?php insertselect($gamedata,"agent$i"."dieaction",$dieopts);?>,
	scoring
	<?php inserttextfield($gamedata,"agent$i"."diescore",5)?> points.<br>

</td></tr></table>
<?php
	if ($i < 5) echo "<hr>\n";
}
?>

<input type='submit' name="submit" value='Generate game!'>

<input type="submit" name="clear" value="Erase form">

</form>

<P>

</center>
</body></html>

