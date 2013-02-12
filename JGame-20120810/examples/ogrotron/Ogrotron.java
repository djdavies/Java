package examples.ogrotron;
import jgame.*;
import jgame.platform.*;

/** Simple robotron clone, made to work well with all platforms:
* midp, jre, and jogl. Supports touch screen. */
public class Ogrotron extends StdGame {
	public static void main(String[]args) {new Ogrotron(parseSizeArgs(args,0));}
	public Ogrotron() { initEngineApplet(); }
	public Ogrotron(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(15,20,16,16,null,null,null); }
	public void initGame() {
		setProgressMessage("");
		defineMedia("ogrotron.tbl");
		setSmoothing(false);
		setVideoSyncedUpdate(true);
		if (isMidlet()) {
			setFrameRate(25,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(50,1);
			title_font = new JGFont("Arial",0,11);
			highscore_title_font = new JGFont("Arial",0,11);
			highscore_font = new JGFont("Arial",0,11);
		}
		setScalingPreferences(0.9,1.12,5,5,5,5);
		leveldone_ingame=true;
		startgame_ingame=true;
		lifelost_ingame=true;
		accel_set_zero_menu=true;
		//setHighscores(10,new Highscore(0,"nobody"),15);
		setPFSize(17,22);
		status_font = new JGFont("Arial",0,16);
		title_font = new JGFont("Arial",0,16);
	}
	public void startTitle() {
		show_man_accel=0;
		show_man_touch=0;
		fillBG("");
		setBGImage("introscreen");
		setViewOffset(0,0,false);
	}
	public void paintFrameTitle() {}
	public void doFrameTitle() {
		if (getMouseButton(1)) {
			clearMouseButton(1);
			if (getMouseY() > 3*viewHeight()/4) {
				invokeUrl("http://tmtg.net/","_blank");
			} else {
				startGame();
			}
		}
	}
	Player player=null;
	public void defineLevel() {
		removeObjects(null,0);
		fillBG("");
		setBGImage("bg"+(stage%3));
		player=new Player(pfWidth()/2-8,pfHeight()/2-8);
		for (int i=0; i<8+level*2; i++) {
			double x,y;
			while (true) {
				x = random(0,pfWidth()-16);
				y = random(0,pfHeight()-16);
				if (x < pfWidth()/2-24  || x > pfWidth()/2+24
				||  y < pfHeight()/2-24 || y > pfHeight()/2+24) break;
			}
			new Ogre(x,y,random(0.45,0.8) );
		}
		for (int i=0; i<8+2*level; i++)
			setTile(random(0,pfTilesX(),1),random(0,pfTilesY(),1),
				""+(stage%5));
			//new JGObject("bonus",true, random(0,pfWidth()-8),
			//	random(0,pfWidth()-8), 8, "bonus",-1);
	}
	public void initNewLife() {
		player=new Player(pfWidth()/2-8,pfHeight()/2-8);
	}
	public void startGameOver() {
		removeObjects(null,0);
		first_time=false;
	}
	public void doFrameInGame() {
		moveObjects();
		checkCollision(4,1); // ogre hit player
		checkBGCollision(1,1+4); // bonus hits player, ogre
		checkCollision(2,4);   // bullet hits ogre
		if (countObjects(null,4)==0) levelDone();
		//if (checkTime(0,800,20-level))
		//	new JGObject("pod",true,pfWidth(),random(0,pfHeight()-16),
		//		4, "pod", 0,0,14,14,  (-3.0-level), 0.0, -2);
	}
	boolean first_time=true;
	boolean has_fired=false;
	int show_man_accel=0;
	int show_man_touch=0;
	public void paintFrameInGame() {
		if (getMouseButton(1) && (isAndroid() || isMidlet())) {
			drawImage(getMouseX(),getMouseY(),"crosshairs"+(((int)seqtimer/3)%3));
		}
		// device is being held vertically in first 4 seconds
		// -> display instructions
		double [] accel = getAccelZeroCorrected();
		if (level == 0 && first_time && gametime < 50*4
		&& show_man_accel==0
		&& (Math.abs(accel[2]) 
			< Math.max(Math.abs(accel[0]),Math.abs(accel[1]))) 
		) {
			show_man_accel=4*50;
		}
		// not aimed for first 5 seconds -> display instructions
		if (level == 0 && first_time && gametime > 50*5 && gametime < 52*5
		&& show_man_touch==0
		&& !has_fired) {
			show_man_touch=4*50;
		}
		if (level==0) {
		//if (!inGameState("LifeLost") && !inGameState("LevelDone")
		//&& !inGameState("StartGame")) {
			if (show_man_accel>0) {
				show_man_accel--;
				double alpha = show_man_accel > 50 ? 1.0 : 
					show_man_accel / 50.0;
				double scale = 0.7 - 0.07*(show_man_accel/4.0/50.0);
				drawImage(-40,25,"manual_accel", null, alpha, 0.0,scale, false);
			} else if (show_man_touch > 0) {
				show_man_touch--;
				double alpha = show_man_touch > 50 ? 1.0 : 
					show_man_touch / 50.0;
				double scale = 0.7 - 0.07*(show_man_touch/4.0/50.0);
				drawImage(-40,25,"manual_touch", null, alpha, 0.0,scale, false);
			}
		}
	}
	public void incrementLevel() {
		if (level<8) level++;
		stage++;
	}
	void explode(double x,double y) {
		playAudio("explo");
		if (isOpenGL() || isAndroid()) {
			double baseang=random(0.0,3.14);
			double phase = random(0.0,6.28);
			new Explo(x,y, baseang             , 2.1, phase);
			new Explo(x,y, baseang+0.8*Math.PI, 2.0, phase);
			new Explo(x,y, baseang+1.6*Math.PI, 1.9, phase);
			new Explo(x,y, baseang+0.4*Math.PI, 1.8, phase);
			new Explo(x,y, baseang+1.2*Math.PI, 1.7, phase);
			//new Explo(x,y, baseang+1.66*Math.PI, 1.4, phase);
		} else {
			new JGObject("explo",true, x,y, 0,"explo_u",0.0,-4.0,
				JGObject.expire_off_pf);
			new JGObject("explo",true, x,y, 0,"explo_d",0.0,4.0,
				JGObject.expire_off_pf);
		}
	}
	JGFont scoring_font = new JGFont("Arial",0,8);
	class Ogre extends JGObject {
		double speed;
		Ogre(double x,double y, double speed) {
			super("ogre",true,x,y,4,"ogre"+(stage%3));
			this.speed=speed;
		}
		double newdirtimer=0;
		public void move() {
			if (newdirtimer<=0) {
				newdirtimer=random(15,35);
				double ang=random(0.0,Math.PI*2);
				if (random(0,1) > 0.5 && player!=null) {
					ang = atan2(player.x-x,player.y-y);
				}
				setSpeedAbs(speed*Math.sin(ang),speed*Math.cos(ang));
				if (x<0 && xdir<=0) xdir=1;
				if (x>pfwidth && xdir>=0) xdir=-1;
				if (y<0 && ydir<=0) ydir=1;
				if (y>pfheight && ydir>=0) ydir=-1;
			} else {
				newdirtimer -= gamespeed;
			}
		}
		public void hit(JGObject obj) {
			score += 10;
			remove();
			obj.remove();
			explode(x,y);
		}
		public void hit_bg(int tilecid,int tilex,int tiley) {
			setTile(tilex,tiley,"");
			new Spawned(tilex*tilewidth,tiley*tileheight);
			//new Spawned(tilex*tilewidth-4,tiley*tileheight+random(-4,4));
			//new Spawned(tilex*tilewidth+4,tiley*tileheight+random(-4,4));
		}
	}
	class Spawned extends JGObject {
		double sleeptmr=random(150.0,200.0);
		double speed=random(1.0,2.0);
		double circleang=random(0.1,0.5);
		Spawned(double x,double y) {
			super("spawn",true,x,y,4,"egg");
		}
		double newdirtimer=0;
		public void move() {
			if (sleeptmr > 0) {
				sleeptmr -= gamespeed;
				return;
			}
			setGraphic("spawn");
			if (newdirtimer<=0) {
				newdirtimer=random(15,35);
				if (player!=null) {
					double ang = atan2(player.x-x,player.y-y) + circleang;
					setSpeed(speed*Math.sin(ang),speed*Math.cos(ang));
				} else {
					setSpeed(0,0);
				}
			} else {
				newdirtimer -= gamespeed;
			}
		}
		public void hit(JGObject obj) {
			score += 5;
			remove();
			obj.remove();
			explode(x,y);
		}
	}
	public class Explo extends JGObject {
		double ang;
		double cenx,ceny, speed, phase;
		public Explo(double x,double y,double ang,double speed,double phase) {
			super("explo",true,x,y, 0,null,70);
			setBBox(0,0,16,16);
			cenx = x;
			ceny = y;
			this.speed = speed;
			this.phase = phase;
			this.ang=ang;
		}
		public void move() {
			x = cenx + (70-expiry)*speed*Math.sin(ang);
			y = ceny + (70-expiry)*speed*Math.cos(ang);
			ang += 0.01*Math.sin(0.001*expiry+phase);
		}
		public void paint() {
			double alpha = expiry/70.0;
			int anim = 1 + (((int)expiry/2)&1);
			setBlendMode(1,0);
			drawImage(x, y, "explo_d"+anim, null, alpha,ang,
				2.0-1.2*expiry/70.0, true);
			setBlendMode(1,-1);
		}
	}
	JGColor[] cyclecol = new JGColor[] {
		JGColor.yellow,JGColor.green,JGColor.cyan,JGColor.blue,
		JGColor.magenta,JGColor.red };

	public class Pts extends JGObject {
		int cyc=0;
		public Pts(double x,double y) {
			super("pts",true,x-8,y-8, 0,null,70);
			setBBox(0,0,32,32);
		}
		public void paint() {
			double alpha = expiry/70.0;
			cyc = (cyc+1)%(2*cyclecol.length);
			int anim = cyc/2;
			setBlendMode(1,0);
			drawImage(x, y, "pts_large",
				cyclecol[anim], alpha, 0.0, 2.0-1.5*expiry/70.0, true);
			setBlendMode(1,-1);
		}
	}
	public class Player extends JGObject {
		public Player(double x,double y) {
			super("player",false,x,y,1,"player", 1.0,1.0, -1);
			setBBox(4,4,8,8);
		}
		double bullettimer=0;
		double invultimer=100;
		double bulxspeed=0,bulyspeed=0;
		public void move() {
			setViewOffset((int)((x/pfwidth)*32),(int)((y/pfheight)*32),false);
			if (getMouseButton(1)) {
				double angle = atan2(viewXOfs()+getMouseX() - x,
									 viewYOfs()+getMouseY() - y);
				xspeed = 3.0*Math.sin(angle);
				yspeed = 3.0*Math.cos(angle);
				xdir = 1;
				ydir = 1;
				bulxspeed = -xspeed*xdir;
				bulyspeed = -yspeed*ydir;
				if (xspeed < 0) setGraphic("player_l");
				else setGraphic("player_r");
			} else {
				xspeed = 3.0;
				yspeed = 3.0;
				setDir(0,0);
				if (getKey(key_up)    && y > yspeed)       ydir=-1;
				if (getKey(key_down)  && y < pfHeight()-16) ydir=1;
				if (getKey(key_left)  && x > xspeed) {
					xdir=-1;
					setGraphic("player_l");
				}
				if (getKey(key_right) && x < pfWidth()-16) {
					xdir=1;
					setGraphic("player_r");
				}
				if (xdir!=0||ydir!=0) {
					bulxspeed = xspeed*xdir;
					bulyspeed = yspeed*ydir;
				}
			}
			if (bullettimer<=0) {
				if (bulxspeed!=0 || bulyspeed!=0) {
					bullettimer=8;
					new JGObject("bullet",true, x,y, 2, "bullet",
						-2.0*bulxspeed, -2.0*bulyspeed, expire_off_pf);
					has_fired=true;
				}
			} else {
				bullettimer -= gamespeed;
			}
			if (invultimer >= 0) invultimer -= gamespeed;
			double [] accel = getAccelZeroCorrected();
			if (hasAccelerometer() && (accel[0]!=0 || accel[1]!=0)) {
				double angle = atan2(accel[0],accel[1]);
				xspeed = 2.0*accel[0];
				yspeed = 2.0*accel[1];
				xdir = 1;
				ydir = 1;
			}
			if (x < 8) {
				x = 8;
				if (xspeed < 0) xspeed=0;
			}
			if (x > pfwidth-24) {
				x = pfwidth-24;
				if (xspeed > 0) xspeed=0;
			}
			if (y < 8) {
				y = 8;
				if (yspeed < 0) yspeed=0;
			}
			if (y > pfheight-24) {
				y = pfheight-24;
				if (yspeed > 0) yspeed=0;
			}
		}
		public void paint() {
			if (invultimer >= 0 && ((int)invultimer%4 < 2)) {
				setColor(JGColor.white);
				drawOval(x+9,y+9,19,19,true,true);
			}
		}
		public void hit_bg(int tilecid,int tilex,int tiley) {
			setTile(tilex,tiley,"");
			score+=25;
			playAudio("bonus");
			if (isAndroid()||isOpenGL()) {
				new Pts(tilex*tilewidth,tiley*tileheight);
			} else {
				new JGObject("pts",true,tilex*tilewidth,tiley*tileheight,0,
					"pts", 0.0, -0.25, 80);
			}
		}
		public void hit(JGObject obj) {
			if (invultimer<0) {
				remove();
				obj.remove();
				explode(obj.x,obj.y);
				lifeLost();
				//Display.getDisplay(Ogrotron.this).vibrate(100);  
				playAudio("death");
				explode(x,y);
			}
			//new StdScoring("pts",obj.x,obj.y,0,-1.0,40,"5 pts",scoring_font,
			//	new JGColor [] { JGColor.red,JGColor.yellow },2);
		}
	}
}
