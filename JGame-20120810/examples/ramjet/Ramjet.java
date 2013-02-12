package examples.ramjet;
import examples.StdScoring;
import jgame.*;
import jgame.platform.*;
/** A minimal game using StdGame with default settings. */
public class Ramjet extends StdGame {
	public static void main(String [] args) {new Ramjet(parseSizeArgs(args,0));}
	public Ramjet() { initEngineApplet(); }
	public Ramjet(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(80,60,8,8,null,null,null); }
	public void initGame() {
		setFrameRate(45,1);
		defineImage("brown","#",1,"empty-brown8.gif","-");
		defineImage("nullimg",".",0,"null","-");
		setTileSettings("#", 1, 0);
		defineMedia("ramjet.tbl");
		//defineAudioClip("titlemusic","music/t/51 - Chronomaster.mid");
		//defineAudioClip("titlemusic","music/t/BARYON04.MID");
		//defineAudioClip("music1","music/Adlbtekk.mid");
		//defineAudioClip("music2","music/trutopiaca.mid");
		//defineAudioClip("shoot","sounds/shoot.wav");
		//defineAudioClip("explo","sounds/explo.wav");
		//defineAudioClip("thrust","sounds/thrust.wav");
		leveldone_ingame=true;
		startgame_ingame=true;
		lifelost_ingame=true;
		gameover_ingame=true;
	}
	public void startTitle() {
		// doesn't seem to work in openjdk
		//playAudio("music","titlemusic",true);
		new JGObject("_title",false,187,84,0,"splash_image");
	}
	public void paintFrameTitle() {
		drawString("Press "+getKeyDesc(key_startgame)+" to start",
			pfWidth()/2, 250, 0, title_font,title_color);
		drawString("Press "+getKeyDesc(key_gamesettings)+" for settings",
			pfWidth()/2, 350, 0, title_font,title_color);
	}
	public void doFrameTitle() {
		new ShortExplo(random(180,460,1),random(80,150,1),2,6,false,false);
		moveObjects();
	}
	Player player=null;
	public void initNewLife() {
		player=new Player(pfWidth()/2,pfHeight()/2);
	}
	public void initNewGame(int level_selected) {
		super.initNewGame(level_selected);
		removeObjects("_title",0);
		stopAudio();
		lives=4;
		player=new Player(pfWidth()/2,pfHeight()/2);
	}
	public void defineLevel() {
		removeObjects(null,4|8|16);
		for (int i=0; i<3+level; i++) {
			int xpos;
			while (true) {
				xpos = random(0,pfWidth(),1);
				if  (player==null) break;
				if (xpos < player.x-100 || xpos > player.x+100) break;
			}
			new Boulder(xpos,random(0,pfHeight()),
				random(-1,1,2)*random(0.5,1.5),random(-1,1,2)*random(0.5,1.5),
				50);
		}
		for (int i=0; i<pfTilesX()/2; i++) {
			//setTile(i,10,"#");
			//setTile(i,50,"#");
		}
		for (int i=pfTilesX()/2; i<pfTilesX(); i++) {
			//setTile(i,20,"#");
			//setTile(i,40,"#");
		}
		//if (level==0) playAudio("music","music1",true);
		//if (level==1) playAudio("music","music2",true);
		//if (level==1) playAudio("music","music2",false);
	}
	public void startGameOver() {
		removeObjects(null,0); 
		stopAudio("music");
	}
	public void doFrameInGame() {
		moveObjects();
		if (countObjects("boulder",0) == 0) levelDone();
		checkCollision(2,4|8); // bullets hit enemies
		checkCollision(4|8|16,1); // enemies hit player
		checkCollision(4,8); // rocks hit ufos
		checkCollision(1,32); // player hits bonus
		checkBGCollision(1,1);
		if (checkTime(100,100000,500-level*2)) {
			if (random(0,1,1) == 0) {
				new UFO(pfWidth(),random(16,pfHeight()-32),-2,random(-.8,.8));
			} else {
				new UFO(0,random(16,pfHeight()-32),2,random(-.8,.8));
			}
		}
		if (countObjects("ufo",0) == 0) {
			stopAudio("ufo");
		} else {
			playAudio("ufo","ufo",true);
		}
	}
	public void incrementLevel() {
		if (level<9) level++;
		stage++;
	}
	JGFont scoring_font = new JGFont("Arial",0,8);
	public class Boulder extends JGObject {
		int size;
		public Boulder(double x,double y,double xspeed,double yspeed,int size) {
			super("boulder",true,x,y,4,null);
			setSpeed(xspeed,yspeed);
			setBBox(-size/2,-size/2,size,size);
			this.size=size;
		}
		public void move() {
			if (x < size && xspeed < 0) xspeed = -xspeed;
			if (y < size && yspeed < 0) yspeed = -yspeed;
			if (x > pfWidth()-size  && xspeed > 0) xspeed = -xspeed;
			if (y > pfHeight()-size && yspeed > 0) yspeed = -yspeed;
		}
		public void paint() {
			setColor(JGColor.white);
			drawOval((int)x,(int)y,size,size,false,true);
		}
		public void hit(JGObject obj) {
			remove();
			obj.remove();
			if (size > 18) {
				for (int i=0; i<3; i++) {
					new Boulder(x,y,
						random(-1,1,2)*random(0.5,1.5),
						random(-1,1,2)*random(0.5,1.5),
						size/2);
				}
				new ShortExplo(x,y,size,6,true,false);
				score += 5;
			} else {
				//new Explo(x,y,size);
				new ShortExplo(x,y,size,6,true,true);
				new Bonus(x,y);
				score += 10;
			}
		}
	}
	public class UFO extends JGObject {
		int timer=30;
		public UFO(double x, double y, double xspeed, double yspeed) {
			super("ufo",true, x,y, 8,null, xspeed,yspeed,-2);
			setBBox(-5,-5,10,10);
		}
		public void move() {
			if (timer==0) {
				timer=100;
				if (player!=null) {
					double angle = Math.atan2(player.x-x,player.y-y);
					new UfoBullet(x,y,3.0*Math.sin(angle), 3.0*Math.cos(angle));
					playAudio("ufoshoot");
				}
			} else {
				timer--;
			}
		}
		public void paint() {
			setColor(JGColor.pink);
			drawLine(x,y-6, x-6,y+6);
			drawLine(x,y-6, x+6,y+6);
			drawLine(x-6,y+6, x+6,y+6);
		}
		public void hit(JGObject obj) {
			remove();
			obj.remove();
			score += 200;
			new ShortExplo(x,y,20,10,true,true);
			new StdScoring("Pts", x,y, 0,-.5,
				50, "200", scoringfontl,scoringcolors,4);
		}
	}
	public class UfoBullet extends JGObject {
		public UfoBullet(double x,double y,double xspeed,double yspeed) {
			super("bullet",true,x,y,16,null,xspeed,yspeed,-2);
			setBBox(-3,-3,6,6);
		}
		public void paint() {
			setColor(JGColor.yellow);
			drawOval((int)x,(int)y,
				5+3*Math.sin(gametime),5+3*Math.sin(gametime),true,true);
		}
	}
	public class Bullet extends JGObject {
		double orient=0; // 0...2*PI
		public Bullet(double x,double y,double xspeed,double yspeed,
		double orient) {
			super("bullet",true,x,y,2,null, -2);
			setBBox(-3,-3,6,6);
			setSpeed(xspeed,yspeed);
			this.orient=orient;
			playAudio("shoot");
		}
		public void paint() {
			setColor(JGColor.orange);
			drawLine(
				(x-5*Math.sin(orient)),
				(y-5*Math.cos(orient)),
				(x+5*Math.sin(orient)),
				(y+5*Math.cos(orient)) );
		}
	}
	JGColor [] explo_col = new JGColor[] { JGColor.red,JGColor.yellow,JGColor.green,
	JGColor.cyan,JGColor.blue,JGColor.magenta};
	public class ShortExplo extends JGObject {
		int size;
		boolean spawn;
		public ShortExplo(double x,double y, int size, int expiry,
		boolean sound,boolean dospawn) {
			super("explo",true,x,y,0,null,6);
			if (sound) playAudio("explo");
			this.size=size;
			this.expiry=expiry+random(0,5,1);
			spawn=dospawn;
		}
		public void move() {
			if (spawn && (int)expiry==0 && size>2) {
				for (double r=random(0,0.6); r<Math.PI*2; r+=1.2) {
					ShortExplo newexp = new ShortExplo(
							x+(int)(size*Math.sin(r))+random(-3,3),
							y+(int)(size*Math.cos(r))+random(-3,3),
							size/2,6, false,true );
					newexp.xspeed=Math.sin(r);//+random(-1,1);
					newexp.yspeed=Math.cos(r);//+random(-1,1);
				}
			}
		}
		public void paint() {
			setColor(explo_col[(int)expiry%6]);
			if (size>=3) {
				drawOval(x,y,size,size,true,true);
			} else {
				drawLine(x,y,x+1,y);
			}
		}
	}
	JGFont scoringfont = new JGFont("Helvetica",0,12);
	JGFont scoringfontl = new JGFont("Helvetica",0,20);
	JGColor [] scoringcolors = new JGColor[] { JGColor.blue,JGColor.red };
	public class Bonus extends JGObject {
		public Bonus(double x,double y) {
			super("bonus",true,x,y,32,null);
			setBBox(-6,-6,12,12);
		}
		int timer=300;
		public void move() {
			if (player!=null) {
				if (x<player.x) xspeed += 0.01; else xspeed -= 0.01;
				if (y<player.y) yspeed += 0.01; else yspeed -= 0.01;
			}
			if (xspeed>0) xspeed -= 0.005; else xspeed+=0.005;
			if (yspeed>0) yspeed -= 0.005; else yspeed+=0.005;
			if ((timer--) <= 0) remove();
		}
		public void hit(JGObject obj) {
			remove(); 
			score += 25;
			new StdScoring("Pts", x,y, xspeed,yspeed,
				50, "25", scoringfont,scoringcolors,4);
			playAudio("pickup");
		}
		public void paint() {
			setColor(JGColor.magenta);
			drawOval((int)x,(int)y,
				5+3*Math.sin(gametime),5+3*Math.cos(gametime),false,true);
		}
	}
	public class Player extends JGObject {
		double orient=0; // 0...400
		int bullettime=0;
		boolean togglefire=true;
		int thrusting=0;
		int invulnerability=45*4;
		public Player(double x,double y) {
			super("player",false,x,y,1,null);
			setBBox(-6,-6,12,12);
		}
		public void move() {
			// bounce
			if (x < 10&& xspeed < 0) xspeed = -xspeed;
			if (y < 10 && yspeed < 0) yspeed = -yspeed;
			if (x > pfWidth()-10  && xspeed > 0) xspeed = -xspeed;
			if (y > pfHeight()-10 && yspeed > 0) yspeed = -yspeed;
			// rotate
			if (getKey(key_right)) orient -= 5;
			if (getKey(key_left))  orient += 5;
			//if (getKey(key_down)) {
			//	orient += 200;
			//	clearKey(key_down);
			//}
			if (orient < 0) orient += 400;
			if (orient >= 400) orient -= 400;
			// move
			double orient_rad = (orient/200)*Math.PI;
			thrusting=0;
			if (getKey(key_up)) {
				xspeed -= 0.3*Math.sin(orient_rad);
				yspeed -= 0.3*Math.cos(orient_rad);
				thrusting=1;
			}
			if (getKey(key_fireleft)) {
				//xspeed -= 0.3*Math.cos(orient_rad);
				//yspeed += 0.3*Math.sin(orient_rad);
				//thrusting=true;
			}
			if (getKey(key_fireright)) {
				//xspeed += 0.3*Math.cos(orient_rad);
				//yspeed -= 0.3*Math.sin(orient_rad);
				//thrusting=true;
			}
			if (getKey(key_down)) {
				xspeed *= 0.93;
				yspeed *= 0.93;
				if (Math.abs(xspeed) > 0.1 && Math.abs(yspeed) > 0.1)
					thrusting=2;
			}
			if (thrusting!=0) {
				playAudio("thrust","thrust",true);
			} else {
				stopAudio("thrust");
			}
			//fire
			togglefire=false;
			if (getKey(key_fire)) {
				togglefire=true;
				//togglefire = !togglefire;
				//clearKey(key_up);
			}
			if (togglefire && bullettime<=0) {
				new Bullet(x-8*Math.sin(orient_rad),y-8*Math.cos(orient_rad),
					xspeed-10*Math.sin(orient_rad),
					yspeed-10*Math.cos(orient_rad), orient_rad );
				bullettime=8;
			}
			if (bullettime > 0) bullettime--;
			if (invulnerability>0) invulnerability--;
		}
		public void paint() {
			double orient_rad = (orient/200)*Math.PI;
			setColor(JGColor.yellow);
			if (invulnerability == 0 || gametime%8 < 4) {
				drawLine(
					(int)(x-10*Math.sin(orient_rad)),
					(int)(y-10*Math.cos(orient_rad)),
					(int)(x+10*Math.sin(orient_rad-0.5)),
					(int)(y+10*Math.cos(orient_rad-0.5)) );
				drawLine(
					(int)(x-10*Math.sin(orient_rad)),
					(int)(y-10*Math.cos(orient_rad)),
					(int)(x+10*Math.sin(orient_rad+0.5)),
					(int)(y+10*Math.cos(orient_rad+0.5)) );
			}
			if (thrusting==1) {
				setColor(JGColor.red);
				drawLine(
					(int)(x+15*Math.sin(orient_rad)),
					(int)(y+15*Math.cos(orient_rad)),
					(int)(x+8*Math.sin(orient_rad-0.5)),
					(int)(y+8*Math.cos(orient_rad-0.5)) );
				drawLine(
					(int)(x+15*Math.sin(orient_rad)),
					(int)(y+15*Math.cos(orient_rad)),
					(int)(x+8*Math.sin(orient_rad+0.5)),
					(int)(y+8*Math.cos(orient_rad+0.5)) );
			}
		}
		public void hit(JGObject obj) {
			if (invulnerability == 0) {
				new ShortExplo(x,y,25,6,true,true);
				remove();
				lifeLost();
			}
		}
		public void hit_bg(int tilecid,int tx,int ty) {
			//x = getLastX();
			//y = getLastY();
			//int dirs = 
			//tryAllDirs(x,y);
			/*JGRectangle oldtile = getTiles();
			if (oldtile.x+oldtile.width < tx  && xspeed > 0) xspeed = -xspeed;
			if (oldtile.y+oldtile.height < ty && yspeed > 0) yspeed = -yspeed;
			if (oldtile.x > tx                && xspeed < 0) xspeed = -xspeed;
			if (oldtile.y > ty                && yspeed < 0) yspeed = -yspeed;
			*/
			//if (and(getTileCid(
			//if (and(getTileCid(tx,
			//xspeed = -xspeed;
			//yspeed = -yspeed;
		}
		public void tryAllDirs(double x,double y) {
			if (checkBGCollision(xspeed,yspeed)==0) return;
			if (checkBGCollision(xspeed,-yspeed)==0) {
				yspeed = -yspeed;
				return;
			}
			if (checkBGCollision(-xspeed,-yspeed)==0) {
				xspeed = -xspeed;
				yspeed = -yspeed;
				return;
			}
			if (checkBGCollision(-xspeed,yspeed)==0) {
				xspeed = -xspeed;
			}
		}
	}
}
