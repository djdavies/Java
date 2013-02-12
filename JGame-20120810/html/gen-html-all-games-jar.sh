#!/bin/sh

# generate the HTML for the example games

./gen-jgame-html.pl examples.insecticide.Insecticide '' \
	'archive="../jars/Insecticide.jar">' \
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.nebulaalpha.NebulaAlpha '' \
	'archive="../jars/NebulaAlpha.jar">' \
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.matrixminer.MatrixMiner '' \
	'archive="../jars/MatrixMiner.jar">' \
	320x240 640x480 800x600 1024x768

./gen-jgame-html.pl examples.pubman.PubMan '' 'archive="../jars/PubMan.jar">' \
	320x240 640x480 800x600 1024x768

./gen-jgame-html.pl examples.munchies.Munchies '' \
	'archive="../jars/Munchies.jar">' \
	320x240 640x480 800x600 1024x768

./gen-jgame-html.pl examples.spacerun.SpaceRun '' \
	'archive="../jars/SpaceRun.jar">' \
	320x240 640x480 800x600 1024x768

./gen-jgame-html.pl examples.spacerun2.SpaceRunII '' \
	'archive="../jars/SpaceRun2.jar">' \
	320x240 640x480 800x600 1024x768

./gen-jgame-html.pl examples.spacerun3.SpaceRunIII '' \
	'archive="../jars/SpaceRun3.jar">' \
	320x240 640x480 800x600 1024x768

./gen-jgame-html.pl examples.waterworld.WaterWorld '' \
	'archive="../jars/WaterWorld.jar">' \
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.chainreaction.ChainReaction '' \
	'archive="../jars/ChainReaction.jar">' \
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.dungeonsofhack.DungeonsOfHack '' \
	'archive="../jars/DungeonsOfHack.jar">' \
	600x400 1020x680

./gen-jgame-html.pl examples.dungeonsofhack.DungeonsOfHack '-scroll' \
	'archive="../jars/DungeonsOfHack.jar"> <param name="scrolling" value="yes">' \
	600x400 1020x680

./gen-jgame-html.pl examples.cavernsoffire.CavernsOfFire '' \
	'archive="../jars/CavernsOfFire.jar">' \
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.cavernsoffire.CavernsOfFire '-scroll' \
	'archive="../jars/CavernsOfFire.jar"> <param name="scrolling" value="yes">'\
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.ramjet.Ramjet '' 'archive="../jars/Ramjet.jar">' \
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.packetstorm.PacketStorm '' \
	'archive="../jars/PacketStorm.jar">' \
	320x240 640x480 800x600 1024x768

./gen-jgame-html.pl examples.guardian.Guardian '' \
	'archive="../jars/Guardian.jar">' \
	640x480 800x600 1024x768

./gen-jgame-html.pl examples.ogrotron.Ogrotron '' \
	'archive="../jars/Ogrotron.jar">' \
	240x320 480x640 600x800

./gen-jgame-html.pl examples.billiardberzerk.Main '' \
	'archive="../jars/BilliardBerzerk.jar">' \
	240x320 480x640 600x800 768x1024

./gen-jgame-html.pl examples.pacmanandzombies.PacmanAndZombies '' \
	'archive="../jars/PacmanAndZombies.jar">' \
	640x480 800x600 1024x768


