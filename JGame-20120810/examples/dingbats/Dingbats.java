package examples.dingbats;
import jgame.*;
import jgame.platform.*;
import examples.StdScoring;

public class Dingbats extends StdGame {

	class Level {
		String bat1,bat2,bat3;
		int combo;
		String bg0,bg1;
		int bg_style;
		double bg_density;
		Level(String bat1,String bat2,String bat3,
		String bg0,String bg1,
		int bg_style, double bg_density) {
			this.bat1=bat1;
			this.bat2=bat2;
			this.bat3=bat3;
			this.bg0=bg0;
			this.bg1=bg1;
			this.bg_style=bg_style;
			this.bg_density=bg_density;
		}
	}

	//Level [] levels = new Level[]{
	//	new Level(

	public static void main(String [] args) {
		new Dingbats(StdGame.parseSizeArgs(args,0));
	}

	/** Application constructor. */
	public Dingbats(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Dingbats() { initEngineApplet(); }

	public void initCanvas() { setCanvasSettings(16,12,64,64,null,null,null); }

	public void initGame() {
		setFrameRate(50,2);
		setBlendMode(1,0);
		setVideoSyncedUpdate(true);
		defineMedia("dingbats.tbl");
		setMouseCursor(null);
		// If you want to have highscores in StdGame, add the following line.
		setHighscores(
			10, // number of highscores
			new Highscore(0,"nobody"), // default entry for highscore
			25 // max length of the player name
		);
		setPFWrap(true,true,-60,-60);
		setPFSize(23,19);
		highscore_font = new JGFont("Serif",0,32);
		highscore_title_font = new JGFont("Serif",0,32);
		status_font = new JGFont("Serif",0,32);
		title_font = new JGFont("Serif",0,32);
		leveldone_ingame=true;
		startgame_ingame=true;
		lifelost_ingame=true;
	}

	public void startTitle() {
		removeObjects(null,0);
		fillBG(" ");
		setBGImage(1,"background3",true,true);
		setBGImage(0,"background2l0",true,true);
	}

	// game state
	double zoom=1.0;

	double time_to_alarm;
	boolean chase_player=false;

	int clicks;

	// level difficulty settings
	int bg_space;
	double max_time_to_alarm;
	double dingbat_speed;

	/** Called when a new level is started. */
	public void defineLevel() {
		if (level >= 11) level = 11;
		// set difficulty
		bg_space = 2 - level/4; // [0,2] 2=easy
		max_time_to_alarm = (40-level)*50; // [28,40]
		dingbat_speed = 0.6 + 0.05*level; // [0.6, 1.15]
		// remove any remaining objects
		fillBG(" ");
		removeObjects(null,0);
		for (int i=0; i<13; i++) new Dingbat(1);
		for (int i=0; i<13; i++) new Dingbat(2);
		for (int i=0; i<13; i++) new Dingbat(3);
		new Pointer();
		time_to_alarm = max_time_to_alarm;
		switch (stage%4) {
		case 0:
			setBGImage(1,"background5",true,true);
			setBGImage(0,"background1l0",true,true);
			// randomly placed tiles
			for (int y=random(1,3,1); y<19; y+=4) {
				for (int x=random(0,5,1); x<22;
				x+=random(6+bg_space,10+bg_space,1)) {
					setTile(x,y,"2"+random(0,7,1));
				}
			}
		break;
		case 1:
			setBGImage(0,"background1l0",true,true);
			setBGImage(1,"background1",true,true);
			// tiles in vertical strips
			for (int x=2; x<21; x+=5) {
				for (int y=random(0,5,1); y<18;
				y+=random(7+bg_space,13+bg_space,1)) {
					for (int y2=0; y2<random(2,4,1); y2++)
						setTile(x,y+y2,"1"+random(0,7,1));
				}
			}
		break;
		case 2:
			setBGImage(1,"background4",true,true);
			setBGImage(0,"background2l0",true,true);
			// randomly placed tiles
			for (int y=random(1,3,1); y<18; y+=3) {
				for (int x=random(0,5,1); x<23;
				x+=random(6+bg_space,9+bg_space,1)) {
					setTile(x,y,"4"+random(0,7,1));
				}
			}
		break;
		case 3:
			setBGImage(0,"background2l0",true,true);
			setBGImage(1,"background3",true,true);
			// tiles in horizontal strips
			for (int y=1; y<18; y+=4) {
				for (int x=random(0,5,1); x<20;
				x+=random(9+bg_space,13+bg_space,1)) {
					int start = random(0,8,4);
					for (int x2=0; x2<4; x2++)
						setTile(x+x2,y,"3"+(start+x2));
				}
			}
		break;
		}
		clicks=7;
	}

	public void initNewLife() {
		new Pointer();
		clicks=9;
	}
	int mousex,mousey,prevmousex=0,prevmousey=0;
	double mousespeed = 0;

	public void paintFrameTitle() {
		drawImage(viewWidth()/2-250, viewHeight()/3,
			"logo" + (1 + ((int)(seqtimer/12))%4), false  );
		setFont(status_font);
		setColor(JGColor.white);
		drawString("Press "+getKeyDesc(key_startgame)+" to start",
			viewWidth()/2, viewHeight()*2/3, 0);
	}

	public void doFrameInGame() {
		time_to_alarm -= getGameSpeed();
		chase_player =  time_to_alarm<=0;
		if (!inGameState("LifeLost")) {
			// set zoom/rotate to default to get actual mouse coords
			setViewZoomRotate(1,0);
			prevmousex = mousex;
			prevmousey = mousey;
			mousex = getMouseX();
			mousey = getMouseY();
		}
		// update view settings
		setViewZoomRotate(zoom,0);
		setViewOffset(
			(int)(-0.15*viewWidth() + 96/2  + 1.3*mousex),
			(int)(-0.15*viewHeight() + 96/2 + 1.3*mousey),false);
		setBGImgOffset(0,mousex/1.5 + 30*zoom,mousey/1.5 + 30*zoom,false);
		setBGImgOffset(1,mousex/3.0 + 50*zoom,mousey/3.0 + 50*zoom,false);
		// update objects
		moveObjects(null,0);
		checkCollision(4,2);
		checkCollision(4,1);
		checkCollision(1,8);
		checkBGCollision(1,1);
		// generate view zoom/rotate effects
		mousespeed = 0.8*mousespeed +
			0.2*Math.sqrt((prevmousex-mousex)*(prevmousex-mousex) +
						  (prevmousey-mousey)*(prevmousey-mousey) );
		double newzoom = 1.15 - mousespeed*0.035;
		if (newzoom<1.0) newzoom=1.0;
		zoom += (newzoom-zoom)*0.035;
		if (zoom<1.0) zoom=1.0;
		// level done criterium
		if (countObjects("neon",0) == 0) {
			levelDone();
		} else {
			//if (clicks==0 && countObjects("bonus",0) == 0) lifeLost();
		}
		if (getKey('D')) levelDone();
	}

	JGColor [] timebarcol = new JGColor [] {
		JGColor.white,JGColor.orange,JGColor.red,JGColor.yellow
	};
	JGColor [] colcycle = new JGColor [] {JGColor.red,JGColor.yellow,
		JGColor.green,JGColor.cyan,JGColor.blue, JGColor.magenta };
	JGColor [] dingbatcol = new JGColor [] {
		new JGColor(1.0,1.0,1.0),
		new JGColor(1.0,1.0,0.4),
		new JGColor(1.0,0.5,0.4), 
	};		
	public void paintFrameInGame() {
		// display instructions
		setFont(status_font);
		drawString("Clicks "+clicks,viewWidth()/2, 0,0);
		//drawString("Press N for the next level, or D to lose a life.",
		//	pfWidth()/2,180,0);
		setBlendMode(1,-1);
		boolean animate = time_to_alarm <= 0;
		if (animate) animate = ( ((int)-time_to_alarm/10)%2 ) == 0;
		drawImage(0, viewHeight()-64, 
			"alarmbell" + (animate ? "2" : ""),
			false);
		if (time_to_alarm>0)
			drawRect(64,viewHeight()-40,(viewWidth()-64)*(time_to_alarm/max_time_to_alarm),
				16, true, false, false, timebarcol);
	}
	class Pointer extends JGObject {
		double mousexdir=0,mouseydir=0;
		int bullettimer=0;
		double invulnerability=100;
		Pointer () {
			super("pointer",false,mousex,mousey,1,null);
			setBBox(32,32,32,32);
		}
		public void hit(JGObject o) {
			if (invulnerability <= 0) {
				remove();
				lifeLost();
				new Explo(x,y-25);
				new Explo(x-30,y+15);
				new Explo(x+30,y+15);
			}
		}
		public void hit_bg(int tilecid) {
			if (invulnerability <= 0) {
				remove();
				lifeLost();
				new Explo(x,y-25);
				new Explo(x-30,y+15);
				new Explo(x+30,y+15);
			}
		}
		double markerx,markery;
		double prevmarkerx,prevmarkery;
		double angle,prevangle;
		public void move() {
			if (invulnerability > 0) invulnerability -= gamespeed;
			x = 2*mousex;
			y = 2*mousey;
			if (Math.abs(prevmousex-mousex) + Math.abs(prevmousey-mousey)>2) {
				mousexdir = 0.8*mousexdir + 0.2*(prevmousex - mousex);
				mouseydir = 0.8*mouseydir + 0.2*(prevmousey - mousey);
			} else {
				mousexdir += 0.1*(prevmousex - mousex);
				mouseydir += 0.1*(prevmousey - mousey);
			}
			markerx = x+150*Math.sin(angle);
			markery = y+150*Math.cos(angle);
			prevmarkerx = x+150*Math.sin(prevangle);
			prevmarkery = y+150*Math.cos(prevangle);
			angle = Math.PI+Math.atan2(mousexdir,mouseydir);
			if (angle-prevangle > Math.PI) prevangle -= Math.PI*2;
			if (angle-prevangle < - Math.PI) prevangle += Math.PI*2;
			if (Math.abs(angle-prevangle) > Math.PI*0.3) {
				prevangle = angle;
			} else {
				prevangle = 0.7*prevangle + 0.3*angle;
			}
			//if (bullettimer>0) bullettimer--;
			if ((getMouseButton(1) || getKey('Z'))
			&&  bullettimer<=0 && clicks>0) {
				clearKey('Z');
				clearMouseButton(1);
				new Bullet(markerx,markery,angle);
				//bullettimer=12;
				clicks--;
			}
		}
		public void paint() {
			setBlendMode(1,0);
			drawImage(mousex*2, mousey*2, "pointer",
				new JGColor(1.0,1.0,1.0), /* blend colour */
				0.5 - (invulnerability > 0 ?
					(0.25+0.25*Math.sin(invulnerability)) : 0), /* alpha */
				Math.atan2(mousexdir,mouseydir), /* rotation */
				1.0 + 0.5*(invulnerability/100.0), /* zoom */
				true);
			drawImage(prevmarkerx, prevmarkery, "bullet",
				new JGColor(1.0,1.0,1.0), /* blend colour */
				1.0, /* alpha */
				Math.atan2(mousexdir,mouseydir)+Math.PI*0.5, /* rotation */
				0.5,   //0.85 + 0.15*Math.sin(rot*0.3), /* zoom */
				true);
		}
	}
	// a translucent rotating zooming object
	class Bullet extends JGObject {
		int [] nr_got = new int [3];
		double rot=0, rotinc;
		Bullet(double x,double y,double ang) {
			super("bullet",true, x,y, 2, null,35);
			//setSpeed(0.0*Math.sin(ang),0.0*Math.cos(ang));
			setBBox(-24,-24,144,144);
			//setBBox(8,8,88,88);
			rot=ang-Math.PI*0.5;
		}
		public void move() {
			//rot += 0.05;
		}
		boolean pair=false,twopair=false,triplet=false,quad=false,quint=false,
				set=false,twoset=false;
		public void hit(JGObject o) {
			Dingbat od = (Dingbat) o;
			new Explo(od.x,od.y);
			nr_got[od.type-1]++;
			od.remove();
			// check for combos. Each combo is awarded only once, but they are
			// cumulative.
			//int [] nr_got2 = new int[] {nr_got[0],nr_got[1],nr_got[2]};
			// handle combos by priority: quint, quad, set, triplet, pair
			String bonusname=null;
			int bonus=5, clickbonus=0;
			double msgangle=0;
			for (int i=0; i<3; i++) {
				if (!quint && nr_got[i]>=5) {
					quint=true;
					bonusname = "quint";
					msgangle = 90;
					clickbonus += 3;
					bonus += 75;
				}
				if (!quad && nr_got[i]==4) {
					quad=true;
					bonusname = "quad";
					msgangle = 90;
					clickbonus += 5;
					bonus += 50;
				}
			}
			while (!set && nr_got[0]>0 && nr_got[1]>0 && nr_got[2]>0) {
				set=true;
				bonusname = "set";
				msgangle = -90;
				clickbonus += 5;
					bonus += 50;
			}
			while (!twoset && nr_got[0]>1 && nr_got[1]>1 && nr_got[2]>1) {
				twoset=true;
				bonusname = "two sets";
				msgangle = -45;
				clickbonus += 3;
					bonus += 75;
			}
			for (int i=0; i<3; i++) {
				if (!triplet && nr_got[i]==3) {
					triplet=true;
					bonusname = "triplet";
					msgangle = 45;
					clickbonus += 3;
					bonus += 30;
				}
				if (nr_got[i]>=2) {
					if (!pair) {
						pair=true;
						bonusname = "pair";
						msgangle = 0;
						clickbonus += 1;
						bonus += 10;
					}
					if (!twopair) { // we've got one pair, see if we got two
						for (int j=0; j<3; j++) {
							if (j==i) continue;
							if (nr_got[j]>=2) {
								twopair=true;
								bonusname = "two pairs";
								msgangle = -90;
								clickbonus += 3;
								bonus += 30;
							}
						}
					}
				}
			}
			score += bonus;
			for (int i=0; i<clickbonus; i++) new Bonus(x,y);
			if (bonusname != null) {
				Object img = getImage("bonus-"+bonusname);
				if (img==null) {
					new StdScoring("score",x,y,0,-1, 80, bonusname+"!",
						new JGFont("Serif",0,20),
							new JGColor[]
							{JGColor.red,JGColor.blue,JGColor.green},
							10);
				} else {
					msgangle = msgangle/180*Math.PI;
					JGObject player = getObject("pointer");
					if (player!=null) {
						msgangle += atan2(player.x-x, player.y-y);
					}
					new Message(x,y,"bonus-"+bonusname, msgangle);
				}
			}
		}
		public void paint() {
			setBlendMode(1,0);
			drawImage(x, y, "bullet", /* regular image parameters */
				colcycle[(int)expiry/2 % colcycle.length], /* blend colour */
				1.0,//0.5+0.5*Math.sin(expiry*0.8), /* alpha */
				rot, /* rotation */
				0.5 + (35-expiry)/35.0*1.2,   //0.85 + 0.15*Math.sin(rot*0.3), /* zoom */
				true /* relative to playfield */
			);
		}
	}
	class Message extends JGObject {
		double rot;
		String sprite;
		Message(double x,double y,String sprite, double rot) {
			super("message",true, x,y, 0, null,80);
			this.sprite = sprite;
			this.rot = rot;
		}
		public void move() {
			x += 3.0*Math.sin(rot);
			y += 3.0*Math.cos(rot);
		}
		public void paint() {
			setBlendMode(1,0);
			drawImage(x, y, sprite, /* regular image parameters */
				colcycle[(int)expiry/4 % colcycle.length],
				0.5 + 0.5*(expiry/80.0), /* alpha */
				0.0, /* rotation */
				0.5 + 0.5*(80-expiry)/80.0,   //0.85 + 0.15*Math.sin(rot*0.3), /* zoom */
				true /* relative to playfield */
			);
		}
	}
	class Explo extends JGObject {
		double rot=0, rotinc;
		String imgname="explo";
		Explo(double x,double y) {
			super("explo",true, x,y, 0, null,80);
			rotinc = 0.06;
			imgname += 1 + stage%3;
		}
		public void move() {
			rot += rotinc;
		}
		public void paint() {
			setBlendMode(1,0);
			drawImage(x, y, imgname, /* regular image parameters */
				new JGColor(1.0,1.0,1.0), /* blend colour */
				expiry/80.0, /* alpha */
				rot, /* rotation */
				1+(80-expiry)/80.0*2.5,   //0.85 + 0.15*Math.sin(rot*0.3), /* zoom */
				true /* relative to playfield */
			);
		}
	}
	class Bonus extends JGObject {
		double rot=0;
		String imgname="bonus";
		Bonus(double x,double y) {
			super("bonus",true, x,y, 8, null,500);
			setSpeed(random(-1,1),random(-1,1));
			setBBox(16,16,64,64);
			imgname += 1+stage%3;
		}
		public void move() {
			rot += 0.01;
		}
		public void hit(JGObject o) {
			remove();
			score += 15;
			clicks++;
		}
		public void paint() {
			setBlendMode(1,0);
			drawImage(x, y, imgname, /* regular image parameters */
				new JGColor(1.0,1.0,1.0), /* blend colour */
				expiry/500.0, /* alpha */
				rot, /* rotation */
				1.0,//(100-expiry)/100.0*4,   //0.85 + 0.15*Math.sin(rot*0.3), /* zoom */
				true /* relative to playfield */
			);
		}
	}
	// a translucent rotating zooming object
	class Dingbat extends JGObject {
		int type; // [1,3]
		double rot=0, rotinc;
		double scale=1.0, alpha=0.7;
		String imgname="star";
		Dingbat(int type) {
			super("neon",true,
				Dingbats.this.random(0,pfWidth()-50),
				Dingbats.this.random(0,pfHeight()-50),
				4,null);
			setSpeed(dingbat_speed*random(-1,1,2)*random(1.5,3),
					 dingbat_speed*random(-1,1,2)*random(1.5,3));
			rotinc = random(-1,1,2)*random(0.08,0.18);
			this.type = type;
			setBBox(16,16,64,64);
			approach_dir   = random(-1,1,2);
			approach_angle = random(0.6,0.9);
			imgname += type + "" + (1 + stage%3);
		}
		double approach_angle, approach_dir;
		public void move() {
			// we may be running at variable frame rate: ensure that rotation
			// speed is constant by multiplying with gamespeed.
			rot += rotinc*getGameSpeed();
			if (chase_player) {
				JGObject player = getObject("pointer");
				if (player!=null) {
					double ang = atan2(x-player.x,y-player.y);
					double dist = Math.abs(x-player.x)+Math.abs(y-player.y);
					if (dist>400) dist=400;
					ang += approach_dir*Math.PI*(approach_angle + 0.4*(400-dist)/400);
					xspeed += dingbat_speed*0.5*Math.sin(ang);
					yspeed += dingbat_speed*0.5*Math.cos(ang);
					xspeed *= 0.95;
					yspeed *= 0.95;
				}
			}
		}
		public void paint() {
			// blend mode is: source multiplier: alpha/destination multiplier: 1
			if (type==2) setBlendMode(1,0); else setBlendMode(1,0);
			// the extended openGL drawImage method
			drawImage(x, y, imgname, /* regular image parameters */
				dingbatcol[type-1], /* blend colour */
				alpha, /* alpha */
				rot, /* rotation */
				scale,   //0.85 + 0.15*Math.sin(rot*0.3), /* zoom */
				true /* relative to playfield */
			);
		}
	}

}
