---------------------------------------
JGame - a Java game engine for 2D games
---------------------------------------

----F U N D R A I S E R-------------------------------------
Want to support JGame development?  
DONATE NOW  at http://www.13thmonkey.org/~boris/jgame/
------------------------------------------------------------


About this document
-------------------

Below is a list of system requirements and features.  This should help you
decide if JGame is suitable for you.  It is followed by several sections that
explain how to run and compile applications for each of the supported
platforms.  If you got your desired platform(s) to run, MANUAL, javadoc/,
tutorial/, and the example games should provide ample material for learning
how to write your first game using the JGame API.


By: Boris van Schooten, schooten@cs.utwente.nl

Website: www.13thmonkey.org/~boris/jgame/

Version: 3.6, 10 aug 2012 (20120810),

JRE (personal computer) platforms:
    Java 1.3 or higher; 250Mhz+ with 2D graphics acceleration recommended
    for the example games.
Tested on: Linux, FreeBSD, NetBSD, Windows XP-Vista-7, MacOS X,
           Java 1.3.1, 1.4.2, 1.5.0, 1.6.0, 1.7.0
           Sun/Oracle JDK, OpenJDK (1.6)

JOGL 1.1.x (opengl) platforms:
    Java 1.4 or higher; 500Mhz+ with 32Mb 3D graphics acceleration recommended
    for the example games.
Tested on: Linux, Windows XP-Vista-7, MacOS X, NetBSD
           Java 1.4.2, 1.5.0, 1.6.0, 1.7.0
           Sun/Oracle JDK, OpenJDK (1.6)

J2ME (MIDP, mobile) platforms:
    MIDP2.0/CLDC1.1
Tested on: WTK2.5.x emulator, Sagem My401C, My411X, Sony Ericsson K320i,
           T-Mobile Vairy Touch II, Samsung GT-B7610
           (I am looking for more test platforms!)

Android platforms:
    Eclair/2.1, Froyo/2.2, Gingerbread/2.3, ICS/4.0
Tested on: emulator, Archos A28, HTC Desire, Samsung Galaxy Gio,
            Samsung Galaxy S WiFi 5.0, Arnova 9 G3 tablet

Distribution license: Revised BSD license (see LICENSE)

Summary: JGame is an open source 2D game engine that makes multiplatform
development easier. It runs on the Java JRE 1.3+ platform with optional OpenGL
(JOGL) enhancements, the J2ME (MIDP2.0/CLDC1.1) mobile platform, and the
Android (2.1+) platform. There is also a Flash (Actionscript 3) version.
JGame features sprites with automatic animation and collision detection, a
tile-based background with easy sprite-tile interaction facilities, sound,
game state, persistent storage, and game options. Games are programmed at a
fixed "virtual" resolution, but can be scaled to any screen resolution. The
engine provides an enhanced graphics API, with a fallback to simpler and more
efficient graphics on J2ME and plain JRE. A patched version of the JBox2D
physics engine that runs on all platforms has now been included.

Features:
* Multiplatform.  
  - Tested on various platforms.
  - Can easily be run as applet, from a jar, or as a webstart.
  - Games can be run on the J2ME and Android platforms without requiring
    changes in the code.
  - Graphics API consists of two levels: an enhanced API for modern graphical
	back-ends (OpenGL, Android,Flash) and (fallback to) a more basic API for
	more limited platforms like plain AWT and J2ME.
  - You can program a game at one resolution, but it can be scaled to any
    desired resolution when run.  Scaling preferences enable precise control
	of scaling under J2ME and Android.
* Highly optimised.  
  - Implementation ensures optimised and accelerated graphics for a
    variety of platforms without requiring extra packages; even works well on
    remote X11 displays.
  - Code is optimised for maximum speed and minimum object creations so it
    works well on slower machines such as mobiles.
  - Provides very efficient multidirectional scrolling on all platforms.
* Easy handling of sprites and animations.
  - Built-in sprite animation
  - Load sprites, tiles, and colour fonts directly from sprite sheets.
  - Define images, animations, and sounds in a text file.
* Tile-based background handling, with decorative backdrop.
  - Scrolling using a virtual screen / viewport model
  - Built-in playfield wrap-around facility
  - multilayer parallax backdrop and zooming/rotating view in OpenGL and Flash
* Automatic collision detection with sprites and background tiles, and 
  easy background tile interaction.
* Channel-based sound system with sound enable/disable built in
* Can run at both fixed frame rate and variable (video synchronised) frame
  rate; built-in facilities for making variable frame rate easier to use.
* A local store API for saving user info / save games
* A settings API (currently for Android only)
* A state machine model for in-game sequences.
* a version of the JBox2D physics engine that works on all platforms including
  J2ME is included
* A standard game state machine and some standard game objects are provided.
* Standard highscore table functions are provided.
* Debugging facilities, which include: visualising bounding boxes, printing
  debug messages next to an object on the playfield.
* A web-based game generator for generating prototype games without any
  programming (under development).


-----------------------------------------
Getting started with software development
-----------------------------------------

JGame offers several platform and deployment options which are explained in
this section.  Basically, there is the JRE (Java Runtime Environment)
platform, both with and without OpenGL (JOGL) extension, meant for regular
PCs, the J2ME (Java 2 Micro Edition) for mobile phones, and Android for
smartphones.  If you want to develop in JRE, you must get the JDK (Java
Development Kit).  This includes the javac compiler, the jar tool, etc.
Apache Ant is also required for the make scripts.  You have to install the
JOGL extension separately, if you want to use it.  If you want to develop for
J2ME, you must also get the WTK (Wireless Toolkit 2.5.x), which contains the
J2ME base classes, an emulator, the preverify tool, etc.  To run JGame,
mobiles need to be CLDC1.1/MIDP2.0 capable.  If you want to develop for
Android, you need to get the Android SDK.  The next sections explain how to
work with each of the platforms.

Full API documentation is found in the javadoc/ directory; general
documentation about game development and practical issues of working with
JGame are found in the MANUAL file; a tutorial introducing the JGame features
step by step by means of example code is found in the tutorial/ directory.
The example games should also illustrate well how a game may be built with
JGame.  More general information about java, games, graphics, etc. you can
find on the JGame website.

For people who want to port their JGame 1 or 2 app to JGame 3, read the
section on porting below.

Quickstart using Gen-skeleton
-----------------------------

There is a new Ant-based platform-independent skeleton generation script that
generates a directory with a minimal JGame app in it, with a build.xml, a
media table, etc.  Run it like this:

  cd skeleton/

  ant -Dapppath=[app_dir] -Dpackagename=[package] -Dmainclass=[class]

Complete example usage:

  cd skeleton/
  ant -Dapppath=me/mygame -Dpackagename=me.mygame -Dmainclass=MyGame
  mv me ../my_build_directory
  cd ..
  # copy JGame resources to build directory
  cp -R classes-jre  my_build_directory
  cp -R classes-jogl my_build_directory
  cp -R classes-midp my_build_directory
  cp -R src-base     my_build_directory
  cp -R src-android  my_build_directory
  cd my_build_directory/me/mygame
  # build JRE version to MyGame.jar
  ant jre
  # build JRE-JOGL version to MyGameJogl.jar
  # (JOGL must be installed, see JRE-JOGL section below)
  ant jogl
  # build the MIDP version to MyGameMidlet.jar / jad
  export WTK_HOME=/path/to/WTK_root
  export PROGUARD_HOME=/path/to/Proguard_root
  ant midp
  # build the Android version to classes-examples/bin/MyGame-release.apk
  export ANDROID_HOME=/path/to/android-sdk
  ant android

This will create a skeleton and compile it for JRE, JOGL, MIDP, and Android,
respectively creating MyGame.jar, MyGameJogl.jar, MyGameMidlet.jar,
MyGame-release.apk in the jars/ directory.  Applet and Webstart are not yet
supported (instead, use the instructions below to create a HTML and JNLP
file).

Before using the generated build.xml, you must point the basedir attribute at
the first line of the file to the build_directory.  The default is "../..",
which works for the example above.  Use "../../.." if the game directory is
three levels deep, etc.  You can also set the project description and Jar
name.  In the AndroidManifest.xml, you can set the screenOrientation attribute
to portrait or landscape, as desired.

For more details on installing, running, and compiling for each of the
platforms, see the sections below.


----------------
JRE applications
----------------

This section explains in detail how JGame can be used and deployed with JRE.
Most Java developers are particularly familiar with JRE, and starting software
development should be easy.  If you are new to Java, here are some
suggestions.  If you do not need any additional libraries, you can use the
gen-skeleton example above to create a build.xml script for you.  Use 'ant
jre' to compile your game.

There are other ways of putting the jgame package in your classpath.  You can
do it by adapting the CLASSPATH variable, so that it points to the JGame
classes.  You can either point to the JGame classes directory (classes-jre),
or point to jgame-jre.jar.  For example, using BASH, while in the JGame main
directory, use:

  export CLASSPATH=$CLASSPATH:`pwd`/jgame-jre.jar

In Windows, try:

  set CLASSPATH=.;<put the path to jgame-jre.jar here>

You can also specify the classpath using the -classpath or -cp option:

  java (or javac) -classpath .:<path to jgame-jre.jar> <my classes> (unix)
  java (or javac) -classpath .;<path to jgame-jre.jar> <my classes> (dos)

Always remember to put ".", the current directory, or your project directory
if not equal to the current directory, in your classpath.  The
examples/Std*... game object classes are included in jgame-jre, but it may be
useful to copy them to your own project directory, since it's likely you're
going to adapt them some in the later stages of your game project.  You may
want to move them to another directory in your own project; remember to change
the 'package examples' lines in the java files if you do this.

JRE Webstart
------------

You can run your application using the java command, but you can also package
it into a webstart package.  This enables easier installation from the web or
from a shortcut, and automatically downloading updates.  To be able to do
this, you need to do 3 things:

* put all your classes and media files in a single Jar, including the JGame
  base classes.  There should be a manifest included so the application can be
  run with java -jar <jarfile>, such as the jgame-all.jar file.  The standard
  build.xml does this automatically. 

* sign the jar (optional).  Signing the jar enables the application to have
  the full privileges of a regular application.  If you don't sign, the
  application will not have certain abilities, such as loading/saving
  configuration or highscores, and displaying full-screen.  Signing can be
  done like this:

  keytool -genkey -validity <number of days that the generated key is valid>

  This will prompt for two passwords and some personal data.  The first
  password is the main password for the .keystore file that is generated, the
  second is the password for the generated key.  The above command generates
  the default key "mykey".  If you forgot the keystore password, simply delete
  the .keystore file and try again.  Now use:

  jarsigner <jarfile> mykey

  After typing the keystore password, the jarfile will be signed with the key
  data in mykey.  

* create a JNLP file.  This is basically a "shortcut" to the Jar file.  When
  users click on the JNLP file (or invoke it using javaws), the Jar is
  downloaded and executed.  The JNLP file also specifies the execution
  privileges.  Look at jgame.jnlp and jgame-signed.jnlp to see what a JNLP
  looks like. 

JRE applet
----------

For running games as applets, the Java plugin that comes with the JRE is used.
A HTML file specifies an <applet> element, which may directly refer to the
applet class file, or a class within a given Jar.  Applets can be put on the
web server both as a collection of files or in a Jar.   Applets always have a
fixed size within the HTML window.  Parameters may be passed using <param>
tags.  There are many examples in the html/ and tutorial/ directories.
Applets have limited privileges. In particular, they cannot access files from
the local file system.  Applets can be paused and unpaused by the web browser,
in which case start() and stop() are called.  A game may need to detect
whether it is an applet. Use the isApplet() method for this.  You can use the
appletviewer tool to test applets without requiring a web browser.

Overview of JRE files
---------------------

* jgame-jre.jar: Java 1.3 classes of JGame library 
  (you can include the jar in your classpath)

* classes-jre: the same, but as separate class files

* src-base: the architecture-independent sources

* src-jre: The JRE-specific sources

To recompile the JRE base classes and jars use:

  ant base

* javadoc/: the API documentation

To re-generate the javadocs, use:

  ant docs

* src-tools: miscellaneous tools:
  - jar2jad: create a jad from a jar
  - deploypackage: install and run package with native binaries (i.e. JOGL)
  - inkscape2physics: translate inkscape file to physics definion (UNFINISHED)

To recompile the tools use:

  ant tools


---------------------
JRE-JOGL applications
---------------------

If you want to use JOGL for compiling applications, you have to install it.
You need JOGL JSR 231 (version 1.x).  This is the JOGL released by Sun
(javax.media.opengl).  There is also an older JOGL (net.java.games.jogl) which
is not suitable.  Jogl has since 2010 become part of the jogamp.org project.
Jogamp's Jogl 2.0 is also incompatible with 1.x, and JGame is not yet adapted
to work with Jogl 2.0.

You can download Jogl 1.1.x binaries from this archive:

http://download.java.net/media/jogl/builds/archive/jsr-231-1.x-webstart-current/

You need the following files: 
  gluegen-rt.jar
  gluegen-rt-natives-<ARCHITECTURE>.jar 
  jogl.jar 
  jogl-natives-<ARCHITECTURE>.jar

The natives jars contains DLLs or SOs which must be placed where the system can find them.  They must be extracted first (use "jar xvf <file>").

The architecture bitness (32 ot 64) is determined by the bitness of the
JRE/JDK you use. Use the command "java -d64" to see if you have a 64 bit JRE
(you'll get an error if you don't).  For more details, see

  http://jogamp.org/jogl/doc/deployment/JOGL-DEPLOYMENT.html

Jogl has no official installer.  Unfortunately, it contains native binaries
that cannot be run from inside a Jar.  I created a native binaries installer
(JarInstaller) which can be used to deploy JOGL games as "standalone"
packages.  It is found in the src-tools/deploypackage directory.  It
automatically extracts the right files for your architecture. For example, in
Windows it extracts the files found in win32/ or win64/, plus any files in
generic/. See the docs inside JarInstaller and the JGame homepage for more
explanations, and examples of JOGL standalone packages.  After extracting,
your class needs to be run with "-Djava.library.path=.".  RunClass, also found
in src-tools/deploypackage,  is a small wrapper class that does just this.
You only need to fill in the name of your main class to make it work.

Alternatively, you can just put the appropriate jars in JAVA_HOME/jre/lib/ext,
and the native code libraries (DLL, SO, etc) in JAVA_HOME/jre/bin, though the
README discourages this (version clashes may occur).  An alternative is to set
CLASSPATH to point to the Jars, and pass
-Djava.library.path=<PATH_TO_NATIVE_LIBS> to pass the location of the native
libraries to Java.  Or:

java -cp PATH_TO_JOGL_AND_GLUEGEN_JAR -Djava.library.path=PATH_TO_NATIVE_LIBS
    YOUR_JOGL_APPLICATION

Running JOGL applications
-------------------------

If you have JOGL installed, running JOGL applications can be done by just
replacing jgame-jre.jar by jgame-jogl.jar.  Another option is to define a
Webstart, which also takes care of JOGL installation for you.

JOGL webstart
-------------

This works the same as with a JRE Webstart, only you have to specify the JOGL
resource, which is then automatically downloaded.  This means JOGL does not
have to be installed.  See jgame-jogl.jnlp to see how it's specified.

JOGL applet
-----------

JOGL applications can even be run as applets, through a special wrapper called
the JNLPAppletLauncher.  See: tutorial/applet-example9-jogl.html for an
example.  Note that the size of the applet canvas has to be passed through the
applet parameters canvaswidth / canvasheight.  This is because width and
height do not work as expected in an applet wrapped by the launcher.  For more
information about the JNLPAppletLauncher, see:

https://jogl-demos.dev.java.net/applettest.html


Overview of JOGL files
----------------------

* jgame-jogl.jar: Java 1.4 classes of JGame, using the JOGL (OpenGL) extension
  (you can include the jar in your classpath)

* classes-jogl: same as above, but as separate class files

* src-base: the architecture-independent sources

* src-jre: the JRE-specific sources

* src-jogl: the JRE/JOGL-specific sources

To recompile the JRE/JOGL base classes backend (you must have JOGL installed)
use:

  ant base-jogl


The JRE and JOGL example games
------------------------------

* jars/<Game>.jar: the example games are found in the jars directory.
  Click on them or use java -jar <Game>.jar to launch a game.

* jars/<Game>Jogl.jar: the JOGL versions of the example games end with Jogl.
  When you have JOGL installed, you can just click on the jars to start.
  Or use the java invocation as explained above to start them. If you
  point to JOGL using CLASSPATH, you need to specify the CLASSPATH
  explicitly, like this:

  java -cp $CLASSPATH:jgame-jogl-all.jar examples.Launcher  (unix)
  java -cp %CLASSPATH%;jgame-jogl-all.jar examples.Launcher  (dos)

To recompile the JRE examples, tutorials, gamegen, and generate the example
jars:

  ant base
  ant apps         (creates the JRE version)

  ant base-jogl
  ant apps-jogl    (creates the JOGL version)

p
To recompile an individual example, go to its directory and use:

  ant jre
  and jogl

Here's an overview of the individual example game classes:

x examples.nebulaalpha.NebulaAlpha - minimal game using base classes only
  examples.Insecticide     - example tile-based game using base classes only
  examples.ChainReaction   - example mouse-based game using base classes
  examples.SpaceRun        - minimal game using StdGame framework w/ defaults
  examples.SpaceRunII      - minimal game using StdGame with customisation
  examples.SpaceRunIII     - minimal game illustrating scrolling
x examples.Munchies                - example game using StdGame framework
x examples.waterworld.WaterWorld   - example game using StdGame framework
x examples.Ramjet                  - example game using StdGame framework
x examples.packetstorm.PacketStorm - example game using StdGame framework
x examples.ogrotron.Ogrotron       - example game using StdGame framework
x examples.cavernsoffire.CavernsOfFire - example game using StdGame, scrolling
x examples.pacmanandzombies.PacmanAndZombies - demonstrates StdVirtualKeyboard
x examples.matrixminer.MatrixMiner - uses StdGame and Std... objects
  examples.PubMan                  - uses StdGame and Std... objects
  examples.DungeonsOfHack          - uses StdGame, Std... objects, scrolling
  examples.guardian.Guardian       - uses StdGame, scrolling
  examples.dingbats.Dingbats       - JOGL game
x examples.webwars.WebWars         - JOGL + Flash game
x examples.billiardberzerk.Main    - demonstrates jbox2d

(x) - game has (some) sound

The desired screen size can be supplied as command line parameters to most of
the applications.  If no size is specified, full-screen is assumed.  All
applications can be quit by pressing Shift-Esc (this is a standard JGame
feature).


* html/: HTML for running the games as applets.  Try:

  appletviewer html/applet-<THE_GAME>-<YOUR_RESOLUTION>.html


* tutorial/: a HTML tutorial in 9 simple example applications, illustrating
  the different features of the game engine.  The code is self-documenting.


------------
J2ME midlets
------------

J2ME (aka MIDP) applications (aka midlets) are much like applets.  They have a
fixed screen size, and can be paused and unpaused like applets.  If you want a
single game to make use of both PC and mobile optimally, you typically want to
set some of its parameters according to whether it's a midlet or not.  Use
isMidlet() to detect this.  Midlets have to be compiled and packaged in a
special way (see also the midp make scripts).  You need WTK (Wireless Toolkit)
to compile them.  After installing WTK, make sure WTK_HOME points to your WTK
root directory.  

When compiling MIDlets, you need to specify that special base classes (classes
different from the JRE base classes) are to be used, and that bytecode for
Java 1.3 has to be generated.  Basically:

  javac -bootclasspath ${CLDCAPI}:${MIDPAPI} -source 1.3 -target 1.3 <javafiles>

Then you have to preverify the classes (basically this involves doing some of
the class linking normally done at runtime but not supported by mobiles):

  ${WTK_HOME}/bin/preverify -d <dest-directory> <source-directory>

This generates the preverified classes in dest-directory.   Now, you can jar
these plus the media files your game requires into a single jar.  This jar
must include a special manifest that specifies the necessary MIDlet
information.  Look at the manifest_midp_...  files for examples.  In some
cases, you also need to create a "shortcut" file called a Jad.  The Jad is
virtually identical to the manifest file, except that it must contain a link
to the Jar, and the correct size of the Jar file must be included in the
MIDlet-Jar-Size attribute.  There is now a tool that creates a Jad from a
MIDlet Jar:

  java -jar jars/jar2jad.jar <jarfile> <destination-jadfile>

The Ant scripts create the Jads automatically.

Since Jar size on mobiles is critical, it may be worth squeezing it to as
small a size as possible.  There exist Jar shrinkers which shorten symbol
names, optimise bytecode, and remove unused methods (which may help a lot when
you're using a generic library like JGame).  The Jars can be shrunk by almost
a factor 2 by using Proguard.  The Ant scripts use Proguard automatically
(tested with version 3.9 or 4.1).  You have to have proguard installed, and
specify the path to proguard in the shell variable PROGUARD_HOME.  

To use Proguard for MIDP, You have to specify the cldcapi11.jar and
mdipapi20.jar to Proguard as library jars, and don't forget to turn off mixed
case class name generation (since midp cannot handle separate classes with
only case differences, like 'a.class' and 'A.class'!).  The shrunk jar has to
be re-preverified.  

You can run the midlets in the WTK emulator (called runmidlet or emulator), or
download the game to your mobile.  Runmidlet is a wrapper around emulator.  I
recommend emulator because it shows System.out and System.err.  Run it using:

  emulator -Xdescriptor:YourMidlet.jad

The easiest way to download the jar to your phone is usually downloading from
the internet, but this usually costs money.   Most mobiles also enable
applications to be downloaded directly from the PC.  However, in some cases
this may be something of a challenge (this may involve, for example, writing
the jars into a hidden directory).  Some mobiles use a data cable for this,
some use bluetooth or IRda to transfer files.  My own mobile uses a regular
USB mass storage interface.  When downloading from the internet, you typically
get the Jad, which then automatically starts download of the Jar.  When
downloading from your PC you may require either Jad+Jar or just the Jar.


Blackberry
----------

JGame MIDP games also run on newer Blackberry devices, which are J2ME
compatible.  Some Blackberry users are used to making JARs/JADs run on their
devices, but to make your users' life easier, you should convert them to COD
and ALX, which are Blackberry's native equivalents of JAR and JAD
respectively.  I do not have access to a Blackberry device, so I have not been
able to test this properly.

To create a COD from a JAR, use rapc.  This comes with the free Blackberry
JDE.  First, install the JDE, then put the JDE bin directory in your PATH.
Since rapc is Java, you can also use it in Linux.  You will still need to
install JDE using Wine to get the jar file.  In Linux, point JDE_HOME to the
JDE root directory, then use:

  java -jar $JDE_HOME/bin/rapc.jar import "$JDE_HOME/lib/net_rim_api.jar" \
      codename=MIDLet_NAME  -midlet jad=JAD_NAME.jad  jar=JAR_NAME.jar

ALX is just an XML shortcut format. Use something like this:

  <loader version="1.0">
    <application id="Application Name">
      <name>Application Name</name>
      <description>Description of your application</description>
      <version>Desired Application Version</version>
      <vendor>Vendor Information</vendor>
      <fileset Java="1.0">
        <files>
        COD_NAME.cod
        </files>
      </fileset>
    </application>
  </loader>


J2ME files overview
-------------------

* classes-midp:  java 1.3 classes of the JGame API for MIDP.

* src-base: the architecture-independent sources

* src-midp: the J2ME specific sources

To recompile the MIDP base classes, you must set the WTK_HOME variable to
point to your WTK install.  Then you can use:

  ant base-midp

* jars/<appname>Midlet.jar and .jad are the Midlet versions of some of the
  games.

To recompile the MIDP examples and generate the JARs and JADs:

  ant apps-midp

Or to recompile an individual game:

  cd examples/<Game>
  ant midp


------------
Android apps
------------

Compiling for Android requires the free Android SDK.  You should ensure the
android-8 package (Android 2.1) is installed.  To use Android command-line
tools directly, point your PATH to the tools directories inside the
android-sdk root. These are:

ANDROID_SDK_ROOT/platform-tools
ANDROID_SDK_ROOT/platforms/android-8/tools
ANDROID_SDK_ROOT/tools

Android also requires a recent version of Apache Ant.

Four example games contain make scripts to compile the games for Android
(make-android).  The make script calls the "android" tool to create a package
skeleton for the game. Then the game-specific files are copied into the
package, and ant is used to automagically compile everything into an APK file.
AndroidManifest.xml and build.properties files are required to create the APK.
build.properties contains the passwords for the keystore that is used to sign
the game.  In this distribution, jgame-examples-android.keystore is used to
sign the apps. This is OK if you want to run the APKs on your own device, but
if you want to publish them in the android market, sign them using your own
keystore.  Create a keystore like this:

keytool -genkey -v -keystore my-release-key.keystore  \
        -alias alias_name -keyalg RSA -keysize 2048 -validity 10000

You will then be prompted for credentials and passwords for keystore and
alias.

If you leave out the -alias option, the default alias "mykey" is used.  Please
DON'T lose the keystore or the password, otherwise the market does not allow
you to publish new APKs for your app!

Once signed, you can upload the apk to your device or to the market.
Connecting your device as an USB drive is not the fastest option, use ADB
(android debug bridge) instead.  ADB enables direct access to your tethered
device.  First, you need to enable USB debugging in your device settings.
Also, some devices may require a device-specific installation process to make
ADB work.  If you've set up your device, connect it using an USB cable.  Use
this to see if your device is seen by ADB:

  adb devices

ADB will try to start an adb server if not already running.  The server
listens to a specific port to detect devices.  Now, invoke the following
command:

  adb install -r classes-android/bin/<YourGame>-release.apk

The -r switch enables replacing an old version of the package. See also the
ADB documentation:

http://developer.android.com/guide/developing/tools/adb.html


Overview of Android files
-------------------------

* src-base: the architecture-independent sources

* src-android: the Android-specific sources

* jars/<appname>-release.apk are the Android versions of some of the games.

Android makefiles are available for Caverns of Fire, Ogrotron, Web Wars, and
Pacman and Zombies.


-------------
Flash applets
-------------

You can port a JGame app to Flash by means of the JGame Flash package.  This
is a separate package that can be downloaded from the JGame homepage, which
contains a Flash version of the JGame API.  It also contains scripts to
convert Java to Actionscript 3 and vice versa, so you can also choose to start
with Flash and port to Java afterwards.  The two APIs are almost 1-to-1
mappable now, and I already succeeded in automatically converting one game
from Java to Flash.  In the future, JGame Flash will be integrated with JGame,
and java-to-flash scripts added to the standard make structure.



-----------------------------------
Porting from one version to another
-----------------------------------

Throughout its lifetime, JGame has undergone several API changes that require
changing older code in order to compile on the newer versions.  They are
listed in this section.

1.x -> 2.x
----------

Due to the distinction between playfield and viewport, the methods for
querying the screen dimensions have been renamed:

screenWidth/Height -> pfWidth/Height or viewWidth/Height

pfTileWidth/Height -> tileWidth/Height

2.0 -> 2.1
----------

The initialisation sequence has been changed.  Instead of passing the
playfield dimensions to the initEngine method, the init code calls initCanvas
at some later point, so that playfield dimensions can be determined after
reading applet parameters.  Inside initCanvas, the playfield dimensions (and
scaling preferences in 3.x) can be set using setCanvasSettings (and
setScalingPreferences in 3.x).  The old initialisation code would be a couple
of constructors like this:

public void MyGame (int w,int h) { initEngine(w,h,30,20,16,16,null,null,null); }
public void MyGame () { initEngineApplet(30,20,16,16,null,null,null); }

In the new initialisation API this reads:

public void MyGame (int w,int h) { initEngine(w,h); }
public void MyGame () { initEngineApplet(); }

public void initCanvas () {setCanvasSettings(30,20,16,16,null,null,null); }

2.x -> 3.0
----------

In spite of the major structural changes in JGame3, the API changes are minor.
Nevertheless, a number of things have to be changed before you can compile a
JGame 1 or 2 application to JGame 3.  Making the changes should usually be as
easy as an couple of automatic search/replaces.  These are the following:

Change all references to Point, Rectangle, Color, and Font to JGPoint,
JGRectangle, JGColor, JGFont.  These are platform-independent minimal versions
of the AWT classes, and only have the most basic methods.  This means you may
have to rewrite code that refers to special methods in the AWT classes.

All references to Dimension have been replaced by JGPoint.  In particular the
parseSizeArgs now returns a JGPoint.  

Replace any calls to setCursor(Cursor) by setMouseCursor(int) or
setMouseCursor(Object).  The int version is used for setting a default cursor,
the latter for removing the cursor or setting your own cursor.  If you want to
set your own cursor, you must pass a java.awt.Cursor to setMouseCursor, which
makes your application JRE-only. 

After these changes, you can probably remove the java.awt.* import, because
all AWT classes have been replaced.  If not, then you have to remove any
remaining dependencies before you can compile your application for MIDP.  Note
also that MIDP has only minimal implementations of the base classes, and has
other limitations.

Include one extra import, jgame.platform.*, so you get:

import jgame.*;
import jgame.platform.*;

To be portable to MIDP, remove or rename any game states with names other than
those used in StdGame. These are "StartGame", "LevelDone", "InGame", "Title",
"Highscore", "EnterHighscore", "LifeLost", "GameOver", "Paused", "Loader",
"SelectLevel".  Because MIDP does not support reflection, the MIDP version
only supports states with these names.

MIDP does not have a Math.atan2 method, but JGame now provides a replacement in
JGEngine.

3.0 -> 3.1
----------

A gamespeed variable has been introduced to make changing the game speed
easier.  This does mean, however, that some things, in particular the StdGame
and JGTimer timers, are no longer ints but are now floats.  Usually this
causes no problems, but it does in some particular cases.  For example,
doing a particular action periodically with a statement like this:

  if (gametime%triggerperiod == 0) { do_something; }

no longer works, as the float version of '%' will be used!  This can be
remedied in a quick and dirty way like this:

  if ((int)gametime % triggerperiod == 0) { do_something; }

But if you want this code to work properly for different game speeds, however,
use StdGame.checkTime, which uses the correct logic for this:

  if (checkTime(triggerperiod)) { do_something; }

3.1 -> 3.2
----------

JGObject's finalize() has been renamed to destroy() to prevent name clash
problems.

3.3 -> 3.4
----------

StdGame's initNewGame now takes an int parameter which represents the level
selected.

3.4 -> 3.5 -> 3.6
-----------------

drawImage has new versions where the image name is the first parameter, so as
to be consistent with drawString and Java's own drawImage.  It is recommended
to use these because JGame Flash will only have these versions of drawImage.

There are new JGObject constructors compatible with JGame Flash.  In some
cases these cause ambiguous constructor calls, when integers are used when
either doubles or integers are expected.  This can be fixed by explicitly
passing doubles where doubles are expected.


--------------
Game generator
--------------

JGame now comes with a (prototype) game generator (Gamegen).  This is a tool
for generating a prototype game without any programming.  Basically you fill
in a set of parameters, which together determine the dynamics of the game.  It
is based on the JGame basic concepts, such as tiles and object types, which
means you may need to read the docs to understand precisely what every
parameter does.  Gamegen is meant for web-based deployment.  There is PHP code
for filling in the parameters in a neat form, but it is also possible (but not
as easy) to use it without PHP or a web server.  This tool is still in
development stages, as it isn't very robust or polished, and should in the
future generate neat Java code, which can be used to generate quick templates
to jump start game development.  It is available online on the JGame website;
check out the website if you want to take a look at this tool.  If you want to
use it in any other way, see the examples/gamegen/README file.  A generalised
non-PHP version is work in progress.

