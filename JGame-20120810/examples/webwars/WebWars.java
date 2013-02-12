package examples.webwars;
import jgame.*;
import jgame.platform.*;

/*

TODO

pause mode text larger

V boss rage timer

shoot sound

sound credits

*/

//[SWF(width="640", height="480", frameRate="60", backgroundColor="#000000")]

public class WebWars extends StdGame {
	public static double particledensity=5.0;
	public static void main(String[]args) {new WebWars(parseSizeArgs(args,0));}
	public WebWars() { initEngineApplet(); }
	public WebWars(JGPoint size) { initEngine(size.x,size.y); }
	public void initGame() {
		defineMedia("media.tbl");
		//setHighscores(10,new Highscore(0,"nobody"),15);
		setFrameRate(isAndroid() ? 45 : 55,3);
		setMouseCursor(NO_CURSOR);
		//defineAnimation("missile1_u",new Array("missile1_u1","missile1_u2"),
		//	0.4,false);
		//defineImageMap("bulletredmap",new bulletredmap().bitmapData,0,0,12,12,0,0);
		//defineImageFromMap("bulletred1","-",0,"bulletredmap",0,"-");
		//setTileSettings(null,1,2);
		//[Embed(source="images/barrel.ttf",fontFamily="army2",fontWeight="normal")]
	//	public static String army2_font;
		//[Embed(source="simplemaze/luxisr.ttf", fontFamily="army",  embedAsCFF='false',fontWeight= "normal")] 
		//public static String army_font;
		//[Embed(source="data/luxisr.ttf", fontFamily="sans",  embedAsCFF='false',fontWeight= "normal")] 
		//public static String sans_font;
		startgame_ingame=true;
		leveldone_ingame=true;
		lifelost_ingame=true;
		gameover_ingame=true;
		startgame_ticks = 100;
		leveldone_ticks = 140;
		lifelost_ticks = 100;
		gameover_ticks = 140;
		audio_dialog_at_startup=false;
		dbgShowMessagesInPf(false);
		dbgShowFullStackTrace(true);
		//audioenabled=false;
	}

	public int [] colors = new int[] {
		0xFF0000,
		0xFFFF00,
		0x00FF00,
		0x00FFFF,
		0x0000FF,
		0xFF00FF 
	};
	public JGColor [] jgcolors = new JGColor[] {
		JGColor.red,
		new JGColor(255,140,0),
		JGColor.yellow,
		new JGColor(140,255,0),
		JGColor.green,
		new JGColor(0,255,140),
		JGColor.cyan,
		new JGColor(0,128,255),
		JGColor.blue,
		new JGColor(128,0,255),
		JGColor.magenta,
		new JGColor(255,0,140),
	};


	// media


	public boolean first_time = false;
	//public boolean snd_enabled = true;



	/* asteroid homer bird triangle email pear cross critter boss_ena */
	public Level [] lev = new Level [] {
		new Level(125,140,  0,  0,  0,  0,  0,  0, false),
		/* Welcome to Web 2.0 */
		new Level(  0,240, 40,  0,  0,  0,  0,  0, false),
		/* Tweet Me */
		new Level(  0,  0,  0,270, 32,  0,  0,  0, false),
		/* You've got mail */
		new Level(  0,190,  0,  0,  0, 29,  0,  0, false),
		/* iTomato */
		new Level(  0,  0, 60,  0, 52, 48,  0,  0, false),
		/* Mashup I */
		new Level(  0,  0,  0,  0,  0,  0,  0,  0, true),
		/* The Boss Button */
		new Level(160,310,  0,  0,  0,  0,280,  0, false),
		/* Hard to Kill */
		new Level(  0,265,  0,330,  0, 55,350,  0, false),
		/* Mashup II */
		new Level(  0,  0,  0,  0,  0,  0,  0, 22, false),
		/* Bugzilla */
		new Level(  0,260, 90,  0,  0,  0,  0, 57, false),
		/* Followers */
		new Level(365,375,185,590,150,110,630,155, false),
		/* Mashup */
		new Level(  0,  0,  0,  0,  0,  0,  0,  0, true)
		/* Final Boss */
	};


	public int nextlife = 0;
	public int ammo = 0;
	public int difficulty = 1;

	public int startlevel=0;
	public int maxpow=0;

	public void initCanvas() {
		setCanvasSettings(20,15,32,32,null,null,null);
		setScalingPreferences(7.0/8.0,8.0/7.0, 0,5,5,5);
	}

	public void startTitle() {
		// submit previous score
		//env.submitStat("Score",score);
		setBGImage("bg0");
		removeObjects(null,0);
		//stopAudio("music");
		new Link(2*pfWidth()/5, credpos+16*0, 75,15, null, "Web design hot",
			"http://www.webdesignhot.com/free-vector-graphics/");
		new Link(2*pfWidth()/5, credpos+16*1, 75,15, null, "Webtreats",
			"http://www.flickr.com/photos/webtreatsetc/");
		new Link(2*pfWidth()/5, credpos+16*2, 75,15, null, "Animesh Jha",
			"http://animeshjha.com/zenphoto/wallpapersanddesigns/");
		//new Link(pfWidth()/2, credpos+16*2, 75,15, null, "Devilshark",
		//	"http://devilshark.deviantart.com/");
		new Link(2*pfWidth()/5, credpos+16*3, 75,15, null, "Dark Maiden",
			"http://darkmaiden-stock.deviantart.com/art/Simple-Star-Field-44386925");


		new Link(3*pfWidth()/5, credpos+16*0, 75,15, null, "Snap2objects",
			"http://www.snap2objects.com/207/06/10/30-free-vector-rss-buttons/");
		new Link(3*pfWidth()/5, credpos+16*1, 75,15, null, "Icojoy",
			"http://www.icojoy.com/articles/47/");
		new Link(3*pfWidth()/5, credpos+16*2, 75,15, null, "Datamouse",
			"http://datamouse.deviantart.com/art/Web-2-0-RSS-Icon-2-69821640");
		new Link(3*pfWidth()/5, credpos+16*3, 75,15, null, "Iconspedia",
			"http://www.iconspedia.com/icon/music-15852.html");


		// NEO Sounds - http://www.neosounds.com/ free example
		new Link(4*pfWidth()/5, credpos+16*0, 75,15, null, "J.B.",null);
		new Link(4*pfWidth()/5, credpos+16*1, 75,15, null, "J. Fairbanks",null);


		/* Animesh Jha bgs:
		http://animeshjha.com/zenphoto/wallpapersanddesigns/
		DragonArtz - concentric circles
		http://dragonartz.wordpress.com/2009/03/page/2/
		Devilshark - floral-with rainbow bg
		http://devilshark.deviantart.com/
		starfield - dark maiden
		http://darkmaiden-stock.deviantart.com/art/Simple-Star-Field-44386925
		pear logo:
		http://bloomart.deviantart.com/art/pear-87946418?offset=10

		rss icons: snap2objects (cc attribution)
		http://www.snap2objects.com/207/06/10/30-free-vector-rss-buttons/
		twitterbird: icojoy (free)
		http://www.icojoy.com/articles/47/
		orange rss:
		http://datamouse.deviantart.com/art/Web-2-0-RSS-Icon-2-69821640
		music note: iconspedia (free)
		http://www.iconspedia.com/icon/music-15852.html

		email, @, sun, warning: not found

http://www.flickr.com/photos/webtreatsetc/4688761390/sizes/l/
purple abstract pareeerica
http://www.flickr.com/photos/8078381@N03/3259314501/
webtreats 2
http://www.brusheezy.com/textures/1957-Vibrant-Grungy-Bokeh-Texture

color design elements
http://www.webdesignhot.com/free-vector-graphics/free-vector-colorful-business-design-elements/

		*/

	}
	public void doFrameTitle() {
		moveObjects(null,0);
		if (first_time && seqtimer < 300) {
			if (getMouseButton(1)) {
				first_time=false;
				clearMouseButton(1);
			}
		}
		if (getMouseButton(1)
		&& getMouseX() >= pfWidth()/2-120 && getMouseX() <= pfWidth()/2+120
		&& getMouseY() >= ctrlpos-20&& getMouseY() <= ctrlpos+40) {
			// XXX no way to start game except from key_startgame!
			startGame();
		}
		if (getMouseButton(1)
		&& getMouseY() >= ctrlpos+40&& getMouseY() <= ctrlpos+90) {
			if (getMouseX() >= pfWidth()/2-160 && getMouseX() <= pfWidth()/2-90) {
				if (difficulty > 1) difficulty--;
			}
			if (getMouseX() >= pfWidth()/2+90 && getMouseX() <= pfWidth()/2+160) {
				if (difficulty < 36) difficulty++;
			}
			clearMouseButton(1);
		}
		if (getMouseX() >= tomatoxpos-50 && getMouseX() <= tomatoxpos+50
		&&  getMouseY() >= credpos-75 && getMouseY() <= credpos+100) {
			if (getMouseButton(1)) {
				clearMouseButton(1);
				invokeUrl("http://tmtg.net/","_blank");
			}
		}
		if (getMouseX() >= sndxpos && getMouseX() <= sndxpos+48
		&&  getMouseY() >= sndypos && getMouseY() <= sndypos+48) {
			if (getMouseButton(1)) {
				clearMouseButton(1);
				audioenabled = !audioenabled;
				//if (snd_enabled) enableAudio(); else disableAudio();
			}
		}
	}

	public void paintFrame(){
		setFont(new JGFont("sans",0,20));
		setColor(JGColor.green);
		drawString(""+score,status_l_margin,0,-1);
		drawCount(lives<8 ? lives-1 : 7, "pointersm", viewWidth()-status_r_margin,0,
				- getImageSize("pointersm").x-2);
		//XXX draw rect with fill image not supported!
		drawRect(128,4,(ammo < 4000 ? ammo : 3999)/10,16,true,false,false,null,"powerbar");
	}

	private double ctrlpos=110;
	private double instrpos=210; //210
	private double credpos=380;
	private double tomatoxpos=110;
	private double sndxpos=570;
	private double sndypos=420;
	public void paintFrameTitle() {
		setFont(new JGFont("sans",0,32));
		double frac = (timer % 15)/15.0;
		int intp = (int)Math.floor(timer / 15);
		int col1 = colors[intp%colors.length];
		int col2 = colors[(intp+1)%colors.length];
		col1 = (col1&0xf8f8f8)/8;
		col2 = (col2&0xf8f8f8)/8;
		int w1 = (int)( (1.0-frac)*8.999999);
		int w2 = (int)( frac*8.9999 );
		if (first_time && seqtimer < 300) {
			int light1 = (int)( 255*(seqtimer < 50 ? seqtimer/50.0 : 1) );
			int light2 = (int)( 255*(seqtimer < 200 ? (seqtimer-150)/50.0 : 1));
			if (seqtimer > 275) {
				light1 = (int)( 255*((300-seqtimer)/25.0) );
				light2 = light1;
			}
			setColor(new JGColor(light1,light1,light1));
			drawString("They never warned us what ",viewWidth()/2+3,90,0);
			drawString("Web 2.0 would be like ...",viewWidth()/2+3,90+35,0);
			if (seqtimer > 150) {
				setColor(new JGColor(light2,light2,light2));
				drawString("... until it was too late.",viewWidth()/2+3,90+85,0);
			}
		} else {
			first_time=false;
			drawImage("gamelogo",pfWidth()/2 - 160,30);
			setColor(JGColor.white);
			//trace(intp+" "+w1+" "+w2);
			//setColor(col1*Math.floor(w1/2) + col2*Math.floor(w2/2));
			int col = (int)( col1*Math.floor(w1/2) + col2*Math.floor(w2/2) );
			setColor(new JGColor((col>>16)&0x7f,(col>>8)&0x7f,col&0x7f));
			drawString(isAndroid()? "Touch here to start!"
				: "Click here to start!",viewWidth()/2+3,ctrlpos+3,0);
			//setColor(col1*w1 + col2*w2);
			col = col1*w1 + col2*w2;
			setColor(new JGColor((col>>16)&0xff,(col>>8)&0xff,col&0xff));
			drawString(isAndroid()? "Touch here to start!"
				: "Click here to start!",viewWidth()/2,ctrlpos,0);
			setColor(JGColor.white);
			setFont(new JGFont("sans",0,22));
			drawString(
				"Start at level: "+difficulty,viewWidth()/2,ctrlpos+48,0);
			drawImage("leftarrow",viewWidth()/2-125-64/2, ctrlpos+45);
			drawImage("rightarrow",viewWidth()/2+125-64/2, ctrlpos+45);
			setFont(new JGFont("sans",0,18));
			drawString(difficulty > 12 ? (difficulty > 24 ? 
				"(Hard)" : "(Medium)" ) : "(Easy)",
				viewWidth()/2,ctrlpos+66,0);
			setFont(new JGFont("sans",0,30));
			drawString("Instructions",viewWidth()/2,instrpos,0);
			setFont(new JGFont("sans",0,20));
			//drawString("Move your pointer with the mouse.",
			//	viewWidth()/2,instrpos+40,0);
			if (!isAndroid()) {
				drawString("Mouse button aims.",
					viewWidth()/2,instrpos+40+0,0);
			} else {
				drawString("Move with accelerometer. Touch to aim.",
					viewWidth()/2,instrpos+40+0,0);
			}
			drawString("Pick up orange things to gain firepower.",
				viewWidth()/2,instrpos+40+25,0);
			drawString("Press 'P' to pause.",
				viewWidth()/2,instrpos+40+50,0);
			setFont(new JGFont("sans",0,20));
			drawString("by",tomatoxpos,credpos-78,0);
			drawImage("logo",tomatoxpos-96/2, credpos-56);
			setFont(new JGFont("sans",0,18));
			drawString("Available for Android!",tomatoxpos,credpos+48,0);
			drawString("Get it at tmtg.net",tomatoxpos,credpos+72,0);
			//setFont(new JGFont("sans",0,16));
			//drawString("djgm.net/tomato",130,credpos+95,0);
			setFont(new JGFont("sans",0,15));
			drawString("Featuring creative commons content by",400,credpos-40,0);
			drawString("graphics",pfWidth()/2,credpos-20,0);
			drawString("sound",4*pfWidth()/5,credpos-20,0);
			drawImage("speakericon",sndxpos,sndypos);
			if (!audioenabled) drawImage("officon",sndxpos-12,sndypos-18);
			if (!isAndroid()) {
				drawImage("pointer",getMouseX()-24,getMouseY()-24,true,null,1.0,-Math.PI*1.25,0.5);
			}
		}
	}
	public void doFrameInGame() {
		if (getKey('N')) levelDone();
		// XXX hack! fix this before using VideoSynced
		gametime = Math.floor(gametime);
		//long t1=System.nanoTime();
		moveObjects(null,0);
		//long t2=System.nanoTime();
		//System.out.println("moveObjects:" + (t2-t1)/1000000.0);
		checkCollision(2+4,1);
		//t1=System.nanoTime();
		//System.out.println("check1:" + (t1-t2)/1000000.0);
		checkCollision(8,2+4);
		//t2=System.nanoTime();
		//System.out.println("check2:" + (t2-t1)/1000000.0);
		if (ammo < 0) ammo=0;
		int pow=0;
		if (ammo > 250) pow = 1;
		if (ammo > 1000) pow = 2;
		if (ammo > 2000) pow = 3;
		if (ammo > 3000) pow = 4;
		if (pow > maxpow) {
			maxpow = pow;
			//env.submitStat("Weapon",pow+1);
		}
		if (score >= nextlife) {
			lives++;
			playAudio("extralife");
			nextlife += 25000;
			if (getObject("player")!=null) {
				((Player)getObject("player")).newlifeanim=100;
			}
		}
		//checkBGCollision(1+2+4,1);
		int nr_objects = countObjects("E",0);
		// phase 1 (level1-6) or phase 2 (level 7-12)
		int levphase = ((int)Math.floor(level/6))%2;
		// difficulty multiplier
		double diff = level >= 12 ? (level >= 24 ? 0.75 : 1.0 ) : 2.0;
		if (isAndroid()) diff *= 1.25;
		Level lvl = lev[level%12];
		lvl = new Level(
			(int)( diff*lvl.asteroid_freq),
			(int)( diff*lvl.homer_freq),
			(int)( diff*lvl.bird_freq),
			(int)( diff*lvl.triangle_freq),
			(int)( diff*lvl.email_freq),
			(int)( diff*lvl.pear_freq),
			(int)( diff*lvl.cross_freq),
			(int)( diff*lvl.critter_freq),
			lvl.boss_ena);
		if (getObject("boss")!=null) nr_objects++;
		/*if (!lvl.boss_ena && gametime == 2000)  {
			stopAudio("music");
			playAudio("music","gamemusicmedium",true);
		}
		if (!lvl.boss_ena && gametime == 3000)  {
			stopAudio("music");
			playAudio("music","gamemusicfast",true);
		}
		if (!lvl.boss_ena && gametime == 3600)  {
			stopAudio("music");
		}*/
		if (gametime > 3600 || (lvl.boss_ena && gametime>100)   ) {
			if (nr_objects == 0) {
				levelDone();
			}
			return;
		}
		// don't create objects if there are too many already
		if (nr_objects >= 22) return;
		if (lvl.asteroid_freq!=0 && (gametime % lvl.asteroid_freq) == 0) {
			new Asteroid(random(0,pfWidth()-32),pfHeight(),64,levphase);
		}
		int xpos,ypos,i;
		if (lvl.homer_freq!=0 &&  (gametime % lvl.homer_freq) == 0) {
			xpos = (int)random(0,pfWidth()-5*60);
			ypos = -64 + random(0,1,1) * (pfHeight() + 64);
			for (i=0; i<5; i++) {
				new Homer(xpos + i * 60, ypos,
					level < 6 ? 0.045*(1.0 - 0.3*Math.cos(i-1.5))
					: (levphase==0 ? 0.07*(1.0 - 0.3*Math.cos(i-1.5))
					               : 0.15*(1.0 - 0.3*Math.cos(i-1.5)) )
					,levphase==0 ? "rss_blue" : "rss_green" );
			}
		}
		if (lvl.bird_freq!=0 &&  (gametime % lvl.bird_freq) == 0) {
			new Bird(pfWidth(),random(0,pfHeight()-64),0.05);
		}
		if (lvl.triangle_freq!=0 &&  (gametime % lvl.triangle_freq) == 0) {
			xpos = (int)random(64,pfWidth()-96);
			if (levphase==1 && random(0,1) > 0.5) {
					// start at top
				for (i=0; i<4; i++)
					new Triangle(xpos,-65-96*i,2.0,"warning"+(levphase+1));
			} else {
					// start at bottom
				for (i=0; i<4; i++)
					new Triangle(xpos,pfHeight()+96*i,-2.0,"warning"+(levphase+1));
			}
		}
		if (lvl.email_freq!=0 &&  (gametime % lvl.email_freq) == 0) {
			new Email(-48 + random(0,1,1)*(pfWidth()+48),random(0,pfHeight()-48));
		}
		if (lvl.pear_freq!=0 &&  (gametime % lvl.pear_freq) == 0) {
			new Pear(-48 + random(0,1,1)*(pfWidth()+48),random(0,pfHeight()-48),
				2.0,"pear");
		}
		if (lvl.cross_freq!=0 &&  (gametime % lvl.cross_freq) == 0) {
			new Cross(random(0,pfWidth()-64),
					  -128 + random(0,1,1) * (pfHeight() + 128+128),
					  null, 64, 0.0 );
		}
		if (lvl.critter_freq!=0 &&  (gametime % lvl.critter_freq) == 0) {
			new Critter(random(0,pfWidth()-64),
					  -64 + random(0,1,1) * (pfHeight() + 64),3.0,"jay" );
		}
		if (lvl.boss_ena && gametime == 10) {
			new Boss(-256+random(0,1,1)*(pfWidth()+256),random(64,pfHeight()-112-64), levphase,diff);
		}
	}

	public void paintFrameStartGame() {
		int size = 5 + (int)seqtimer;
		if (size > 50) size = 50;
		setColor(jgcolors[ (int)Math.floor(seqtimer/1) % 12 ]);
		setFont(new JGFont("sans",0, size));
		drawString("Get Ready!",viewWidth()/2,130-size/2.0, 0);
	}
	public void paintFrameStartLevel() {
		if (seqtimer<20) {
			int alpha = (int)(255*(20-seqtimer)/20);
			setColor(new JGColor(255,255,255,alpha));
			drawRect(0,0,pfWidth(),pfHeight(),true,false);
		}
		int size = 5 + (int)(seqtimer*1.3) - 40;
		if (size > 50) size = 50;
		setColor(jgcolors[ (int)Math.floor(seqtimer/1) % 12 ]);
		if (seqtimer > 40) {
			setFont(new JGFont("sans",0,size));
			drawString("Wave "+(stage+1),viewWidth()/2,270-size/2.0, 0);
		}
	}
	public void paintFrameLevelDone() {
		// switch sequence
		int phase = 0;
		setBlendMode(1,0);
		int colnr=(int)(seqtimer/4);
		double offset=seqtimer/70;
		double scale = seqtimer/70;
		double bright = seqtimer < 90 ? 0 : (seqtimer-90)/50;
		double circsize = seqtimer < 120 ? 1.0 : 1.0 + (seqtimer-120)/45;
		for (double i=150*seqtimer/50; i>=8/(scale+5); i/=1.5) {
			if (i<25) {
				scale *= 1.5;
				i *= 0.7;
			}
			JGColor col = jgcolors[colnr%12];
			col = new JGColor(
				(int)(col.r + 255*bright),
				(int)(col.g + 255*bright),
				(int)(col.b + 255*bright) );
			if (col.r < 0) col.r = 0;
			if (col.g < 0) col.g = 0;
			if (col.b < 0) col.b = 0;
			if (col.r > 255) col.r = 255;
			if (col.g > 255) col.g = 255;
			if (col.b > 255) col.b = 255;
			//setColor(col);
			for (double a=0; a<2; a += (2.01/16)) {
				double ang =  + offset + (phase*0.125*0.333333 + a) * Math.PI;
				double x = pfWidth()/2 + i*scale*Math.sin(ang);
				double y = pfHeight()/2 + i*scale*Math.cos(ang);
				drawImage("particlewhitehard", x-16,y-16, true, col, 1.0, 0.0,
					0.012*i*scale*circsize);
				//drawOval(x,y,0.4*i*scale,0.4*i*scale,true,true);
			}
			phase = (phase+1)%2;
			colnr++;
		}
		setBlendMode(1,-1);
		// paint message
		int size = 5 + (int)seqtimer;
		if (size > 50) size = 50;
		setColor(jgcolors[ (int)Math.floor(seqtimer/1) % 12 ]);
		setFont(new JGFont("sans",0,size));
		drawString("Wave "+(stage+1),viewWidth()/2,130-size/2.0, 0);
		drawString("Cleared!",viewWidth()/2,270-size/2.0, 0);
	}
	public void paintFrameLifeLost() {
	}
	public void paintFrameGameOver() {
		int size = 5 + (int)seqtimer;
		if (size > 50) size = 50;
		setColor(jgcolors[ (int)Math.floor(seqtimer/1) % 12 ]);
		setFont(new JGFont("sans",0, size));
		drawString("Game Over!",viewWidth()/2,130-size/2.0, 0);
	}
	public void paintFramePaused(){
		setColor(JGColor.white);
		setFont(new JGFont("sans",0, 30));
		// XXX getFontHeight was used, not implemented!
//		drawRect(viewWidth()/20,15*viewHeight()/36,18*viewWidth()/20,
//			5*viewHeight()/36+title_font.size, true,false,false);
		drawString("Paused",viewWidth()/2,13*viewHeight()/36,0);
		drawString("Press P to continue",
			viewWidth()/2,20*viewHeight()/36,0);
	}
	public void initNewGame(int level_selected) {
		setMouseCursor(NO_CURSOR);
		score=0;
		lives=4;
		nextlife = 25000;
		level=difficulty-1;
		stage=level;
		startlevel = level;
		ammo=0;
		maxpow = 0;
		clearMouseButton(1);
	}
	public void initNewLife() {
		ammo /= 2;
		removeObjects("E",0);
		new Player();
		//new Player(true,false);
	}
	public void defineLevel() {
		//env.submitStat("LevelsCompleted",stage-startlevel);
		setBGImage("bg"+(1+(stage%6)));
		//stopAudio("music");
		//playAudio("music","gamemusic",true);
	}
	public void incrementLevel() {
		//env.submitStat("HighestLevelCompleted",stage+1);
		//ammo = 0;
		level++;
		stage++;
	}
	public void startStartGame() {
		playAudio("ready");
	}
	public void startLevelDone() {
		playAudio("excellent");
	}
	//public void paintFrame() {
	//	super.paintFrame();
	//	setColor(JGColor.white);
	//	setFont(new JGFont("sans",0,20));
	//	drawString("Score "+score,0,0,-1);
	//	drawString("Lives "+lives,viewWidth()-2,0,1);
	//	drawString("Air "+air,viewWidth()/2,0,1);
	//}

	private boolean firsttime=true;

	public void paintFrameInGame() {
		if (firsttime && gametime > 200 && gametime < 500) {
			double alpha = 1.0;
			if (gametime < 250) alpha = (gametime-200)/50.0;
			if (gametime > 450) alpha = (500-gametime)/50.0;
			setColor(new JGColor((int)(alpha*255),(int)(alpha*255),(int)(alpha*255)));
			setFont(new JGFont("sans",0,25));
			if (!isAndroid()) {
				drawString("Hold mouse button to aim!",viewWidth()/2,100,0);
			} else {
				drawString("Move with accelerometer. Touch to aim."
					,viewWidth()/2,100,0);
			}
		}
		if (gametime >= 500) firsttime=false;
	}

	/*public void paintFrameLevelDone() {
		setFont(new JGFont("sans",0,25));
		drawString("Wave "+(stage)+" finished !",viewWidth()/2,130,0);
		if (seqtimer > 60) {
			drawString("Bonus "+air+"x1: "+(air*1),
				viewWidth()/2,270,0);
		}
	}*/

	// methods for objects


	/** x, y = center position */
	public void genExplo(double x,double y,int size) {
		new ParticleBig(x,y);
		for (int i=0; i < (int)(size*WebWars.particledensity); i++)
			new Particle(x,y);
	}

}



class Level {
	public int asteroid_freq;
	public int homer_freq;
	public int bird_freq;
	public int triangle_freq;
	public int email_freq;
	public int pear_freq;
	public int cross_freq;
	public int critter_freq;
	public boolean boss_ena;
	public Level(int asteroid_freq,int homer_freq,int bird_freq,int triangle_freq,int email_freq,int pear_freq,int cross_freq,int critter_freq,boolean boss_ena
	) {
		this.asteroid_freq  = asteroid_freq;
		this.homer_freq     = homer_freq;
		this.bird_freq      = bird_freq;
		this.triangle_freq  = triangle_freq;
		this.email_freq     = email_freq;
		this.pear_freq      = pear_freq;
		this.cross_freq     = cross_freq;
		this.critter_freq   = critter_freq;
		this.boss_ena       = boss_ena;
	}
}

class Asteroid extends JGObject {
	private int iconsize;
	private int type;
	private int tmr=0;
	private double rot=0.0,rotspeed;
	public Asteroid(double x,double y,int size,int type) {
		super("Easteroid",true,x,y,2,null);
		this.iconsize = size;
		this.type = type;
		setSpeed(random(-1,1,2)*random(0.5,1.5),random(-1,1,2)*random(0.5,1.5));
		rotspeed = random(-0.02,0.02);
		setBBox(32-size/2,32-size/2,size,size);
	}
	public void move() {
		if (x > pfwidth) x = -64;
		if (y > pfheight) y = -64;
		if (x < -64) x = pfwidth;
		if (y < -64) y = pfheight;
		rot += rotspeed;
		tmr += 1;
		//iconsize = 32*Math.cos(tmr/10.0);
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+32,y+32,iconsize/4);
			if (iconsize > 30.0) {
				new Asteroid(x,y,iconsize/2,type);
				new Asteroid(x,y,iconsize/2,type);
				if (type==1) new Asteroid(x,y,iconsize/2,type);
				((WebWars)eng).score += 15;
			} else {
				if (random(0,1) > 0.6) new Bonus(x,y);
				((WebWars)eng).score += 25;
			}
			remove();
		}
	}
	public void paint() {
		//eng.drawImage("crosshair",x,y,true,null,1.0,0,0.5);
		eng.drawImage(type==0 ? "play_blue" : "play_red",
			x,y,true,null,1.0,-rot,iconsize/64.0);
	}
}

class Bird extends JGObject {
	private int tmr=0;
	private double accel=0;
	private double rot=0.0;
	private String img="birdl";
	public Bird(double x,double y,double accel) {
		super("Ebird",true,x,y,2,null);
		xspeed = random(-1,1,2)*35*accel;
		this.accel=accel;
		setBBox(16,16,32,32);
	}
	public void move() {
		if (x > pfwidth) x = -64;
		if (y > pfheight) y = -64;
		if (x < -64) x = pfwidth;
		if (y < -64) y = pfheight;
		tmr ++;
		//iconsize = 32*Math.cos(tmr/10.0);
		JGObject player = eng.getObject("player");
		if (player!=null) {
			if (y>player.y) {
				yspeed -= accel;
			} else {
				yspeed += accel;
			}
			if (x>player.x) img="birdl"; else img="birdr";
			rot = Math.PI/2 - eng.atan2(player.x-x,player.y-y);
			if (tmr % 70 == 0) {
				eng.playAudio("zap");
				double bulrot = eng.atan2(player.x-x,player.y-y);
				new BirdBullet(x,y,4*Math.sin(bulrot),4*Math.cos(bulrot),rot,img);
			}
		}
		yspeed -= 0.02*yspeed;
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("screech"+random(1,2,1));
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+32,y+32,6);
			((WebWars)eng).score += 20;
			remove();
			if (random(0,1) > 0.4) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage(img,x,y,true,null,1.0,-rot,1.0);
	}
}
class BirdBullet extends JGObject {
	private double rot=0.0;
	private String img="birdl";
	public BirdBullet(double x,double y,double xspd,double yspd,double rot,String img) {
		super("Ebirdbullet",true,x,y,2,null,expire_off_view);
		setSpeed(xspd,yspd);
		setBBox(32-8,32-8,16,16);
		this.rot = rot;
		this.img = img;
	}
	public void move() {
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("screech"+random(1,2,1));
			((WebWars)eng).genExplo(x+32,y+32,2);
			((WebWars)eng).score += 5;
			remove();
			if (random(0,1) > 0.9) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage(img,x,y,true,null,1.0,-rot,24.0/64.0);
	}
}

class Email extends JGObject {
	public Email(double x,double y) {
		super("Eemail",true,x,y,2,null,290);
		double ang = eng.atan2(pfwidth/2-x,pfheight/2-y);
		setSpeed(1.5*Math.sin(ang),1.5*Math.cos(ang));
		setBBox(0,0,53,48);
	}
	public void move() {
		if (expiry < 4) {
			remove();
			eng.playAudio("eject");
			for (double a=Math.PI/12; a<Math.PI*2-0.001; a+=Math.PI/3)
				new Spam(x,y,a);
		}
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+26,y+24,6);
			((WebWars)eng).score += 20;
			remove();
			if (random(0,1) > 0.35) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage("email",x,y);
	}
}

class Spam extends JGObject {
	private double rot=0.0;
	public Spam(double x,double y,double rot) {
		super("Espam",true,x,y,2,null,expire_off_view);
		setBBox(0,0,35,32);
		setSpeed(2.5*Math.sin(rot), 2.5*Math.cos(rot));
	}
	public void move() {
		if (y > pfheight/2) {
			yspeed -= 0.02;
		} else {
			yspeed += 0.02;
		}
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+17,y+16,3);
			((WebWars)eng).score += 5;
			remove();
			if (random(0,1) > 0.8) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage("spam",x,y);
	}
}


class Homer extends JGObject {
	private int tmr=0;
	private double accel=0;
	private double rot=0.0,rotspeed;
	private String img;
	public Homer(double x,double y,double accel,String sprite) {
		super("Ehomer",true,x,y,2,null);
		rotspeed = 0.5*accel;
		this.accel = accel;
		img = sprite;
		xspeed = ((x - pfwidth/2)/pfwidth) * 16;
		setBBox(0,0,32,32);
	}
	public void move() {
		if (x > pfwidth) x = -64;
		if (y > pfheight) y = -64;
		if (x < -64) x = pfwidth;
		if (y < -64) y = pfheight;
		rot += rotspeed;
		tmr += 1;
		//iconsize = 32*Math.cos(tmr/10.0);
		JGObject player = eng.getObject("player");
		if (player!=null) {
			if (x>player.x) {
				xspeed -= accel;
			} else {
				xspeed += accel;
			}
			if (y>player.y) {
				yspeed -= accel;
			} else {
				yspeed += accel;
			}
		}
		//xspeed -= 0.02*xspeed;
		xspeed -= (0.2*accel)*xspeed;
		yspeed -= (0.2*accel)*yspeed;
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+16,y+16,6);
			((WebWars)eng).score += 15;
			remove();
			if (random(0,1) > 0.6) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage(img,x,y,true,null,1.0,-rot,1.0);
	}
}

class Triangle extends JGObject {
	private int tmr=0;
	private double center;
	private String img;
	private double speed;
	private double imgsize=1.0;
	public Triangle(double x,double y,double speed,String img) {
		super("Etriangle",true,x,y,2,null);
		this.speed = speed;
		this.img = img;
		center = x;
		tmr = (int)( y-pfheight );
	}
	public void move() {
		if (y > pfheight && speed > 0) y = -64;
		if (y < -64 && speed < 0) y = pfheight;
		tmr ++;
		//iconsize = 32*Math.cos(tmr/10.0);
		JGObject player = eng.getObject("player");
		y += speed;
		x = center + 64*Math.sin(tmr/20.0);
		//setBBox(8,16,48,44); // upright triangle
		int size = (int)(imgsize*65);
		setBBox(32-size/2,32-size/2,size,size);
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			int i;
			obj.remove();
			((WebWars)eng).genExplo(x+32,y+32,2);
			imgsize -= 0.3;
			if (imgsize<0.3) {
				eng.playAudio("explo"+random(1,3,1));
				((WebWars)eng).genExplo(x+32,y+32,4);
				remove();
				((WebWars)eng).score += 35;
				if (random(0,1) > 0.25) new Bonus(x,y);
			}
		}
	}
	public void paint() {
		eng.drawImage(img,x,y,true,null,1.0,0.0,imgsize);
	}
}

class Pear extends JGObject {
	private int tmr=0;
	private double oldx,oldy;
	private double newx,newy;
	private String img;
	private double speed;
	private double imgsize=1.0;
	public Pear(double x,double y,double speed,String img) {
		super("Epear",true,x,y,2,null);
		this.speed = speed;
		this.img = img;
		oldx = x;
		oldy = y;
	}
	public void move() {
		/*if (x > pfwidth) x = -64;
		if (y > pfheight) y = -64;
		if (x < -64) x = pfwidth;
		if (y < -64) y = pfheight;*/
		tmr ++;
		if (tmr < 75) {
			x = oldx;
			y = oldy;
			imgsize = 1.0;
		} else if (tmr==75) {
			do {
				newx = random(32,pfwidth-64);
				newy = random(32,pfheight-96);
			} while (Math.abs(oldx-newx) > pfwidth*0.6
			||       Math.abs(oldy-newy) > pfheight*0.6
			||       Math.abs(oldx-newx) < pfwidth*0.25
			||       Math.abs(oldy-newy) < pfheight*0.25);
		} else if (tmr<175) {
			double pos = (tmr-75.0)/100.0;
			imgsize = 1.0 - 0.5*Math.sin(pos*Math.PI);
			pos = 0.5 - 0.5*Math.cos(pos*Math.PI);
			x = (1.0-pos)*oldx + pos*newx;
			y = (1.0-pos)*oldy + pos*newy;
		} else if (tmr==175) {
			oldx = newx;
			oldy = newy;
			tmr=0;
		}
		//iconsize = 32*Math.cos(tmr/10.0);
		JGObject player = eng.getObject("player");
		setBBox((int)(25-imgsize*50/2),(int)(25-imgsize*50/2),
			(int)(imgsize*50),(int)(imgsize*50) );
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			((WebWars)eng).genExplo(x+26,y+26,5);
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).score += 25;
			remove();
			if (random(0,1) > 0.4) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage(img,x,y,true,null,1.0,0.0,imgsize);
	}
}

class Boss extends JGObject {
	private int tmr=0;
	private int hp=250;
	private int is_hit=0;
	private double rot=0;
	private double diff=0;
	private int type;
	public Boss(double x,double y,int type,double diff) {
		super("boss",false,x,y,2,null);
		if (x < pfwidth/2) xspeed = 0.8; else xspeed = -0.8;
		this.diff=diff;
		this.type = type;
		if (type==0) {
			setBBox(0,0,256,112); 
		} else {
			setBBox(24,24,192-48,192-48);
		}
	}
	public void move() {
		//JGObject player = eng.getObject("player");
		if (x < -256) x = pfwidth;
		if (x > pfwidth) x = -256;
		tmr++;
		// rage
		if (tmr == 3600*2) diff /= 3;
		if (tmr == 3600*3) diff /= 3;
		is_hit=0;
		//JGRectangle bb = getBBox();
		//int midx = (bb.x + bb.width)/2;
		//int midy = (bb.y + bb.height)/2;
		if (type==0) {
			if (tmr%(int)(Math.floor(130*diff)) < 50 && tmr%10 == 0) {
				if (Math.floor(tmr/200)%4 == 0) {
					new BossButton(x+128-22,y+112-22,0,1, 0);
				} else if (Math.floor(tmr/200)%4 == 1) {
					new BossButton(x,y+(112-22)/2,-1,0, 0);
				} else if (Math.floor(tmr/200)%4 == 2) {
					new BossButton(x+256-44,y+(112-22)/2,1,0, 0);
				} else {
					new BossButton(x+128-22,y,0,-1, 0);
				}
			}
		} else {
			if (tmr%(int)(Math.floor(30*diff)) == 0) {
				new BossButton(x+96-20,y+96-20, Math.sin(rot),Math.cos(rot), 1);
			}
		}
		if (type==1) rot += 0.03;
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			hp--;
			is_hit=1;
			/*if (hp == 100) {
				eng.stopAudio("music");
				eng.playAudio("music","gamemusicmedium",true);
			}
			if (hp == 50) {
				eng.stopAudio("music");
				eng.playAudio("music","gamemusicfast",true);
			}*/
			if (hp <= 0) {
				int i;
				//eng.stopAudio("music");
				eng.playAudio("bossexplo");
				for (i=0; i<(int)(35*WebWars.particledensity); i++) {
					new Particle(x+random(0,256-32),y+random(0,112-32),
						(int)random(0,50));
				}
				((WebWars)eng).score += 20;
				remove();
				for (i=0; i<12; i++)
					new Bonus(x+random(0,256-32),y+random(0,112-32));
			}
		}
	}
	public void paint() {
		eng.drawImage("boss"+(type+1),x,y,true,null,is_hit!=0? 0.7 : 1.0, -rot,
			1.0);
	}
}

class BossButton extends JGObject {
	private int tmr=0;
	private int anim=1;
	private int type=1;
	private double accel=0;
	private String img;
	private double rot=0;
	public BossButton(double x,double y,double speedx,double speedy,int type) {
		super("Ebossbutton",true,x,y,2,null);
		this.accel = random(0.1,0.15);
		this.xspeed = speedx*random(4.0,6.0);
		this.yspeed = speedy*random(4.0,6.0);
		this.type = type;
		if (type==0) {
			setBBox(0,0,44,20);
			img = "bossbut";
		} else {
			setBBox(4,4,32,32);
			img = "bossstar";
		}
	}
	public void move() {
		if (x > pfwidth) x = -64;
		if (y > pfheight) y = -64;
		if (x < -64) x = pfwidth;
		if (y < -64) y = pfheight;
		tmr += 1;
		//iconsize = 32*Math.cos(tmr/10.0);
		JGObject player = eng.getObject("player");
		if (player!=null) {
			if (x>player.x) {
				xspeed -= accel;
			} else {
				xspeed += accel;
			}
			if (y>player.y-8) {
				yspeed -= accel;
			} else {
				yspeed += accel;
			}
		}
		xspeed -= 0.02*xspeed;
		yspeed -= 0.02*yspeed;
		anim = 1 + (tmr/5)%3;
		if (type==1) rot += accel;
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+18,y+13,5);
			((WebWars)eng).score += 15;
			remove();
			if (random(0,1) > 0.6) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage(img+anim,x,y,true,null,1.0,-rot,1.0);
	}
}

class Cross extends JGObject {
	private int tmr=0;
	private double rot=0.0;
	private Cross parent;
	private int imgsize;
	private int has_children=3;
	public Cross(double x,double y,Cross parent,int size,double rot) {
		super("Ecross",true,x,y,2,null);
		this.parent = parent;
		imgsize = size;
		this.rot = rot;
		setBBox(32-size/2,32-size/2,size,size);
		if (size > 32) {
			for (int i=0; i<8; i++) {
				new Cross(x,y,this,size/2,i*Math.PI/4);
			}
		}
		setSpeed(random(-1,1,2)*random(1.0,1.5),random(-1,1,2)*random(1.0,1.5));
	}
	public void move() {
		if (parent!=null) parent.has_children=3;
		if (has_children>0) has_children--;
		/*if (parent!=null && !parent.isAlive()) {
			parent=null;
			setSpeed(random(-1,1,2)*random(1.5,4),random(-1,1,2)*random(1.5,4));
		}*/
		rot += 0.01;
		tmr += 1;
		if (parent!=null) {
			x = parent.x + 1.5*imgsize*Math.sin(rot);
			y = parent.y + 1.5*imgsize*Math.cos(rot);
		} else {
			/*if (x > pfwidth+64) x = -128;
			if (y > pfheight+64) y = -128;
			if (x < -128) x = pfwidth+64;
			if (y < -128) y = pfheight+64;*/
			if (x < 64 && xspeed < 0) xspeed = -xspeed;
			if (y < 64 && yspeed < 0) yspeed = -yspeed;
			if (x >pfwidth - 128 && xspeed > 0) xspeed = -xspeed;
			if (y >pfheight - 128 && yspeed > 0) yspeed = -yspeed;
		}
		//iconsize = 32*Math.cos(tmr/10.0);
		JGObject player = eng.getObject("player");
		/*if (player!=null) {
			if (x>player.x) {
				xspeed -= accel;
			} else {
				xspeed += accel;
			}
			if (y>player.y) {
				yspeed -= accel;
			} else {
				yspeed += accel;
			}
		}
		xspeed -= 0.02*xspeed;
		yspeed -= 0.02*yspeed;*/
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			if (has_children!=0) return;
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+32,y+32,5);
			((WebWars)eng).score += 15;
			remove();
			if (parent==null) new Bonus(x,y);
			if (random(0,1) > 0.7) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage("cross",x,y,true,null,1.0,rot,imgsize/64.0);
	}
}

class Critter extends JGObject {
	private double speed=0;
	private int dir=0; /* up right down left */
	private int phase=0;
	private String img;
	public Critter(double x,double y,double speed,String sprite) {
		super("Ecritter",true,x,y,2,null);
		this.speed = speed;
		img = sprite;
		setBBox(8,8,32,32);
	}
	public void move() {
		JGObject player = eng.getObject("player");
		phase--;
		if ( (phase&7) == 7) {
			xdir = 1;
			ydir = 1;
		} else {
			xdir = 0;
			ydir = 0;
		}
		if (phase <= 0) {
			phase = random(40,90,1);
			if (player!=null) {
				if (dir==0 || dir==2) { // vert -> horiz
					yspeed = 0;
					dir = x < player.x ? 1 : 3;
					xspeed = 8*speed * ( dir==3 ? -1 : 1);

				} else { // horiz -> vert
					dir = y < player.y ? 2 : 0;
					yspeed = 8*speed * ( dir==0 ? -1 : 1);
					xspeed = 0;
				}
			}
		}
	}
	public void hit(JGObject obj) {
		if (!isAlive()) return;
		if (obj.colid==8) {
			obj.remove();
			eng.playAudio("explo"+random(1,3,1));
			((WebWars)eng).genExplo(x+24,y+24,6);
			((WebWars)eng).score += 20;
			remove();
			if (random(0,1) > 0.5) new Bonus(x,y);
		}
	}
	public void paint() {
		eng.drawImage(img + (((phase&8)==0) ? "1" : "2"), x,y,true,null,1.0,
			-dir*Math.PI/2, 1.0);
	}
}


class Player extends JGObject {
	private double rot=Math.PI*1.25;
	private int bulletdir=1;
	private double crossx=1,crossy=1;
	public int newlifeanim=0;
	public Player() {
		super("player",false,pfwidth/2,pfheight/2,1,null);
		setBBox(8,8,32,32);
	}
	private int tmr=0;
	public void move() {
		tmr++;
		if (newlifeanim > 0) newlifeanim--;
		if (eng.hasAccelerometer()) {
			// use accelerometer controls
			if (eng.getMouseInside() && eng.getMouseButton(1)) {
				rot = eng.atan2(eng.getMouseY()-y,eng.getMouseX()-x);
			}
			x += 3.0*eng.getAccelX();
			y += 3.0*eng.getAccelY();
			if (x < 0) x = 0;
			if (y < 0) y = 0;
			if (x > pfwidth-48) x = pfwidth-48;
			if (y > pfheight-48) y = pfheight-48;
		} else if (eng.getMouseInside()) {
			// use mouse controls
			if (!eng.getMouseButton(1)) {
			} else {
				double newcrossx = eng.getMouseX()-x;
				double newcrossy = eng.getMouseY()-y;
				// weigh new position more if it is >90 degrees from cross
				double w = 0.05;
				if (newcrossx*newcrossx + newcrossy*newcrossy >= 4) {
					double newrot = Math.PI + eng.atan2(newcrossy,newcrossx);
					double ang = newrot - rot;
					if (ang > Math.PI) ang -= 2*Math.PI;
					if (ang < -Math.PI) ang += 2*Math.PI;
					if (ang > Math.PI/3) w += 0.14*(ang-Math.PI/3)/(Math.PI/1.5);
				}
				crossx = (1.0-w)*crossx + w*newcrossx;
				crossy = (1.0-w)*crossy + w*newcrossy;
				rot = Math.PI + eng.atan2(crossy,crossx);
				crossx = -Math.cos(rot);
				crossy = -Math.sin(rot);
			}
			x = eng.getMouseX();
			y = eng.getMouseY();
			if (x<0) x = 0;
			if (y<0) y = 0;
			if (x>pfwidth-48) x = pfwidth-48;
			if (y>pfheight-48) y = pfheight-48;
		}
		int ammo =  ((WebWars)eng).ammo;
		if (ammo > 250 && ammo<=1000) {
			if ( tmr%5 == 0) {
				new Bullet(x+14,y+14,9,rot);
			}
			// ammo decreases slightly slower so it's easier to maintain
			if ( tmr%6 == 0) {
				ammo--;
			}
		} else if (ammo > 1000 && ammo<=2000) {
			if ( tmr%8 == 0) {
				new Bullet(x+14,y+14,9,rot-0.2);
				new Bullet(x+14,y+14,9,rot);
				new Bullet(x+14,y+14,9,rot+0.2);
			}
			// ammo decreases slightly slower so it's easier to maintain
			if ( tmr%3 == 0) {
				ammo--;
			}
		} else if (ammo > 2000 && ammo<=3000) {
			if ( tmr%8 == 0) {
				new Bullet(x+14,y+14,9,rot-0.2);
				new Bullet(x+14,y+14,9,rot);
				new Bullet(x+14,y+14,9,rot+0.2);
				ammo-=3;
			} else if ( tmr%8 == 4) {
				new Bullet(x+14,y+14,9,rot);
				ammo-=1;
			}
		} else if (ammo > 3000) {
			if ( tmr%8 == 0) {
				new Bullet(x+14,y+14,9,rot-0.4);
				new Bullet(x+14,y+14,9,rot-0.2);
				new Bullet(x+14,y+14,9,rot);
				new Bullet(x+14,y+14,9,rot+0.2);
				new Bullet(x+14,y+14,9,rot+0.4);
				ammo-=5;
			}
		} else {
			if ( tmr%8 == 0) {
				new Bullet(x+14,y+14,9,rot);
				ammo--;
			}
		}
		((WebWars)eng).ammo = ammo;
	}
	public void paint() {
		double scale = 0.5;
		double imgx = x, imgy = y;
		/*//start game effect
		if (eng.inGameState("StartLevel")) {
			double phase = ((WebWars)eng).seqtimer/100.0;
			scale = 18.5 - phase*18;
			imgx = phase*x + (1.0-phase)*(pfwidth/2);
			imgy = phase*y + (1.0-phase)*(pfheight/2);
		}*/
		eng.drawImage("pointer",imgx-24,imgy-24,true,null,1.0,-rot,scale);
		if (newlifeanim > 0) {
			eng.drawImage("pointer",imgx-24,imgy-24,true,null,
				1.0 - newlifeanim/100.0,
				-rot,
				0.5 + 10.0*(newlifeanim/100.0));
		}
		//eng.drawImage("crosshair",
		//	8 + x - 32*crossx,
		//	8 + y - 32*crossy,
		//	true,null,1.0,Math.PI/2+rot,1.0);
		//eng.drawImage("crosshair",
		//	x + (48-32)/2 + 160*Math.cos(rot),
		//	y + (48-32)/2 + 160*Math.sin(rot),
		//	true,null,1.0,Math.PI/2+rot,1.0);
	}
	public void hit(JGObject obj) {
		if (obj.colid==2 && tmr > 150) {
			remove();
			((StdGame)eng).lifeLost();
			//obj.remove();
			((WebWars)eng).genExplo(x+24,y+24,10);
			for (double ang=0; ang<Math.PI*2; ang += 0.17) {
				new Bullet(x+14,y+14,9,ang);
			}
			eng.playAudio("death");
		}
		if (obj.colid==4) {
			((WebWars)eng).ammo += eng.isAndroid() ? (int)(45*1.25) : 45;
			((WebWars)eng).score += 40;
			obj.remove();
			eng.playAudio("bonus"+random(1,3,1));
			new Message(obj.x+16-226/2,obj.y+16-71/2,"pow");
		}
	}
	public void hit_bg(int tilecid) {
		((StdGame)eng).lifeLost();
	}
}
class Bonus extends JGObject {
	private double rot=0.0;
	private double rotspeed;
	public Bonus(double x,double y) {
		super("bonus",true,x,y,4,null,400);
		xspeed = random(-0.5,0.5);
		yspeed = random(-0.5,0.5);
		setBBox(-8,-8,48,48);
		rotspeed = random(-0.02,0.02);
	}
	public void move() {
		rot += rotspeed;
	}
	public void hit_bg(int tilecid) {
	}
	public void paint() {
		eng.drawImage("bonus" + (1 + ((StdGame)eng).stage%6),
			x,y,true,null,
			expiry >= 40 ? 0.8 : 0.8*(expiry/40.0), 
			-rot, 1.0 );
	}
}

class Bullet extends JGObject {
	private double rot;
	private double timer=0.0;
	public Bullet(double x,double y,double speed,double rot) {
		super("bullet",true,x,y,8,null,expire_off_view);
		xspeed = speed*Math.cos(rot);
		yspeed = speed*Math.sin(rot);
		this.rot = rot;
		setBBox(5,5,10,10);
	}
	public void move() {
		timer += gamespeed;
		if ( random(0.0, 3.0) > 2.0)
			new SmokeParticle(x+8,y+8,xspeed,yspeed);
	}
	public void hit_bg(int tilecid) {
	}
	public void paint() {
		eng.drawImage("bullet",x,y,true,null,1.0,-rot,1.0);
	}
}

class Message extends JGObject {
	private double scale=0.2;
	private String img;
	private int tmr=0;
	public Message(double x,double y,String image) {
		super("message",true,x,y,8,null,100);
		img = image;
	}
	public void move() {
		scale *= 1.025;
		tmr++;
	}
	public void paint() {
		int col = (int)(1 + ((((StdGame)eng).gametime + random(0,2)) / 4) % 6);
		eng.setBlendMode(1,0);
		eng.drawImage(img+col,x,y,true,null, expiry/100.0, 0.0, scale);
		eng.setBlendMode(1,-1);
	}
}

class ParticleBig extends JGObject {
	private double size=1.0;
	private double z_dir;
	private int delay;
	private double ang=0.0;
	private double col;
	public ParticleBig(double x,double y) {
		this(x,y,0);
	}
	public ParticleBig(double x,double y,int delay) {
		super("particle",true,x-16,y-16,0,null);
		expiry = delay+random(60,75,1);
		z_dir = random(0.05,0.09);
		this.delay = delay;
		col = ((WebWars)eng).gametime + random(-1.5,1.5);
		//col = random(0.0, 5.9);
	}
	public void move() {
		col += 0.1;
		if (delay==0) {
			double iang = random(0,Math.PI*2);
			double ispd = random(-1.0,0.0);
			setSpeed(ispd*Math.sin(iang),ispd*Math.cos(iang));
			//z_pos = 40.0*ispd;
			//ang = eng.atan2(xspeed,yspeed);
		} else if (delay < 0) {
			size += z_dir;
		}
		delay--;
	}
	public void paint() {
		if (delay > 0) return;
		//int col = (int)( 1 + ((((StdGame)eng).gametime + random(0,2)) / 3) % 6);
		eng.setBlendMode(1,0);
		JGColor color = ((WebWars)eng).jgcolors[(int)(col%12)];
		eng.drawImage("particlewhite",x,y,true,color, 
			expiry > 40 ? 0.8 : 0.8*(expiry/40.0), 
			-Math.PI/2 + ang, 2.0*size);
		eng.setBlendMode(1,-1);
	}
}

class Particle extends JGObject {
	private double size=1.0;
	private double z_dir;
	private int delay;
	private double ang=0.0;
	private double col;
	public Particle(double x,double y) {
		this(x,y,0);
	}
	public Particle(double x,double y,int delay) {
		super("particle",true,x-16,y-16,0,null);
		expiry = delay+random(25,75,1);
		z_dir = random(-4,4);
		this.delay = delay;
		col = ((WebWars)eng).gametime + random(-1.5,1.5);
		//col = random(0.0, 5.9);
	}
	public void move() {
		col += 0.1;
		if (delay==0) {
			double iang = random(0,Math.PI*2);
			double ispd = random(-4.0,-0.5);
			setSpeed(ispd*Math.sin(iang),ispd*Math.cos(iang));
			size = 2.0 + 0.33*ispd;
			ang = eng.atan2(xspeed,yspeed);
		} else if (delay < 0) {
			//size += z_dir;
		}
		delay--;
	}
	public void paint() {
		if (delay > 0) return;
		//int col = (int)( 1 + ((((StdGame)eng).gametime + random(0,2)) / 3) % 6);
		eng.setBlendMode(1,0);
		JGColor color = ((WebWars)eng).jgcolors[(int)(col%12)];
		eng.drawImage("dropparticle",x,y,true,color, 
			expiry > 20 ? 0.5 : 0.5*(expiry/20.0), 
			-Math.PI/2 + ang, 1.0*size );//,
			//eng.BlendAdd);
		eng.setBlendMode(1,-1);
	}
}

class SmokeParticle extends JGObject {
	private double size=1.0;
	private double size_speed=0.0;
	private int delay=0;
	private double ang=0.0;
	private double ang_speed=0.0;
	private double col;
	public SmokeParticle(double x,double y,double xspeed, double yspeed) {
		super("particle",true,x-8,y-8,0,null);
		expiry = delay+random(11,19,1);
		size_speed = random(0.1,0.2);
		ang_speed = random(-0.2,0.2);
		this.xspeed = xspeed*0.6;
		this.yspeed = yspeed*0.6;
		//col = ((WebWars)eng).gametime + random(-1.5,1.5);
		//col = random(0.0, 5.9);
	}
	public void move() {
		//col += 0.1;
		if (delay==0) {
			double iang = random(0,Math.PI*2);
			double ispd = random(0.0,0.5);
			//setSpeed(ispd*Math.sin(iang),ispd*Math.cos(iang));
			size = 0.5;
			ang = random(0,Math.PI*2);
		} else if (delay < 0) {
			size += size_speed;
			ang += ang_speed;
		}
		delay--;
	}
	public void paint() {
		if (delay > 0) return;
		//int col = (int)( 1 + ((((StdGame)eng).gametime + random(0,2)) / 3) % 6);
		eng.setBlendMode(1,0);
		//JGColor color = ((WebWars)eng).jgcolors[(int)(col%12)];
		eng.drawImage("smokeparticle",x,y,true, null, 
			expiry > 7 ? 0.6 : 0.6*(expiry/7.0), 
			ang, 1.0*size );
		eng.setBlendMode(1,-1);
	}
}
class ParticleOld extends JGObject {
	private double z_pos=0.0;
	private double z_dir;
	private int delay;
	public ParticleOld(double x,double y) {
		this(x,y,0);
	}
	public ParticleOld(double x,double y,int delay) {
		super("particle",true,x,y,0,null);
		expiry = delay+random(25,50,1);
		z_dir = random(-4,4);
		this.delay = delay;
	}
	public void move() {
		if (delay==0) {
			setSpeed(random(-3.5,3.5),random(-3.5,3.5));
		} else if (delay < 0) {
			z_pos += z_dir;
		}
		delay--;
	}
	public void paint() {
		if (delay > 0) return;
		int col = (int)( 1 + ((((StdGame)eng).gametime + random(0,2)) / 3) % 6);
		eng.setBlendMode(1,0);
		eng.drawImage("particle"+col,x,y,true,null, 
			expiry > 20 ? 1.0 : (expiry/20.0), 
			0.0, 300.0 / (200+z_pos));//,
			//eng.BlendAdd);
		eng.setBlendMode(1,-1);
	}
}

class Link extends JGObject {
	private String text;
	private String url;
	private boolean hlt=false;
	private double w,h;
	public Link(double x,double y,double width,double height,String img,String text,String url) {
		super("Elink",true,x,y,0,img);
		this.text = text;
		this.url = url;
		this.w = width;
		this.h = height;
	}
	public void move() {
		hlt=false;
		if (url==null) return;
		if (eng.getMouseX() >= x - w/2 && eng.getMouseY() >= y + 6
		&&  eng.getMouseX() <= x + w/2 && eng.getMouseY() <= y + 6 + h) {
			hlt=true;
			if (eng.getMouseButton(1)) {
				eng.clearMouseButton(1);
				eng.invokeUrl(url,"_blank");
			}
		}
	}
	public void paint() {
		if (text!=null) {
			eng.setColor(hlt ? JGColor.white : new JGColor(160,160,160));
			eng.setFont(new JGFont("sans",0,13));
			eng.drawString(text,x,y+8,0);
		}
	}
}


