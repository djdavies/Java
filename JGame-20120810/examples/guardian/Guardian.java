package examples.guardian;
import jgame.*;
import jgame.platform.*;
import java.util.*;
import examples.StdScoring;
/** A minimal game using StdGame with default settings. */
public class Guardian extends StdGame {
	public static void main(String[]args) {new Guardian(parseSizeArgs(args,0));}
	public Guardian() { initEngineApplet(); }
	public Guardian(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(32,24,16,16,null,null,null); }
	public void initGame() {
		defineMedia("guardian.tbl");
		// define hill and base tiles
		for (int i=0; i<20*5; i++)
			defineImage("hills"+i,"h"+i,0,"hills_map",i,"-");
		for (int i=0; i<4*8; i++)
			defineImage("base"+i,"b"+i,0,"hq_map",i,"-");
		setPFSize(224,24);
		setPFWrap(true,false,0,0);
		setFrameRate(45,1);
		setBGImage("starbg");
		setHighscores(10,new Highscore(0,"nobody"),15);
		startgame_ingame=true;
		leveldone_ingame=true;
		leveldone_ticks = 200;
	}
	public void initNewLife() {
		new Player(0,pfHeight()/2,5);
	}
	public void defineLevel() {
		removeObjects(null,0);
		new Player(0,pfHeight()/2,5);
		for (int x=0; x<pfTilesX(); x++) {
			for (int y=0; y<5; y++) {
				setTile(x,pfTilesY()-5+y, "h"+(20*y + (x%20)));
			//for (int y=(int)(pfTilesY() - 3 - 2*Math.sin(x));y<pfTilesY();y++){
			//	setTile(x,y,"#");
			//}
			}
		}
		for (int x=0; x<4; x++) {
			for (int y=0; y<8; y++) {
				setTile(8+x,pfTilesY()-8+y, "b"+(4*y + (x%4)));
			}
		}
		for (int x=0; x<9; x++) {
			new Humanoid(random(0,pfWidth()));
		}
		new HomeBase(16*8,16*(pfTilesY()-8));
	}
	public void startGameOver() { removeObjects(null,0); }
	public void doFrameInGame() {
		//if (getKey('L')) levelDone();
		if (gametime >= 800 && countObjects(null,4) == 0) levelDone();
		moveObjects();
		checkCollision(4+8+32+64,1); //enemies,homebase,mines,bullets hit player
		checkCollision(4+32,2); // enemies, mines hit laser
		checkCollision(16,4+1); // humanoids hit enemies, player
		if (checkTime(0,800,100-(level*5)))
			new UFO(random(0,pfWidth()),random(16,pfHeight()-32));
		if (checkTime(0,800,220-(level*12)))
			new Bomber(random(0,pfWidth()),random(16,pfHeight()-32));
		if (checkTime(0,800,200-(level*10)))
			new Splitter(random(0,pfWidth()),random(16,pfHeight()-32));
		if (checkTime(1200,2000,250-(level*10)))
			new Baiter(random(0,pfWidth()),random(16,pfHeight()-32));
		//	new JGObject("enemy",true,pfWidth()/2,random(0,pfHeight()-16),
		//		4, "enemy", 0,0,16,16, random(-1,1,2)*3.0, random(-1,1), -2);
		//if (checkTime(0,800,20-level))
		//	new JGObject("pod",true,pfWidth()/2,random(0,pfHeight()-16),
		//		8, "pod", 0,0,14,14, -3.0-level, 0.0, -2);
		setViewOffset((int)(panx+panxofs),0,true);
		setBGImgOffset(0, (int)( (panx+panxofs)/3.0 ),0,true);
	}
	public void incrementLevel() {
		if (level<9) level++;
		stage++;
	}
	public void startLevelDone() {
		score += 50*countObjects("humanoid",0);
	}
	public void paintFrameLevelDone() {
		super.paintFrameLevelDone();
		setFont(title_font);
		drawString("Humanoids on surface x 50",
			viewWidth()/2,viewHeight()*2/3-30, 0);
		drawCount(countObjects("humanoid",0), "human_r2",
			viewWidth()/2-20*5, viewHeight()*2/3, 20);
	}
	double panxofs=0;
	double panx=0;
	JGFont scoring_font = new JGFont("Arial",0,16);
	class Player extends JGObject {
		public Player(double x,double y,double speed) {
			super("player",false,x,y,1,"shipr");
		}
		int invulnerability=70;
		int dir=1;
		int bullettime=0;
		Humanoid pickedup=null;
		int thrustdir=0;  //
		public void move() {
			if (invulnerability>0) invulnerability--;
			if (getKey(key_up) && y>0) {
				if (yspeed>-2.5) {
					yspeed = -2.5;
				} else if (getKey(key_fire)) {
					if (yspeed>-8) yspeed -= 0.15;
				} else {
					if (yspeed>-8) yspeed -= 0.5;
				}
			} else if (getKey(key_down) && y<pfHeight()-24) {
				if (yspeed<2.5) {
					yspeed = 2.5;
				} else if (getKey(key_fire)) {
					if (yspeed<8) yspeed += 0.15;
				} else {
					if (yspeed<8) yspeed += 0.5;
				}
			} else {
				yspeed=0;
			}
			if (y<0) {
				y=0;
				yspeed=0;
			}
			if (y>pfHeight()-24) {
				y = pfHeight()-24;
				yspeed=0;
			}
			thrustdir=0;
			if (getKey(key_left)) {
				if (xspeed>0) xspeed-=2;
				if (xspeed>-30) xspeed -= 0.9;
				dir=-1;
				thrustdir=-1;
				setGraphic("shipl");
			} else if (getKey(key_right)) {
				if (xspeed<0) xspeed+=2;
				if (xspeed< 30) xspeed += 0.9;
				dir=1;
				thrustdir=1;
				setGraphic("shipr");
			} else {
				if (xspeed>0) xspeed -= 1;
				if (xspeed<0) xspeed += 1;
				if (xspeed>-1 && xspeed<1) xspeed=0;
			}
			if (dir>0) {
				if (panxofs < viewWidth()/4) {
					panxofs += 15;
					if (panxofs > viewWidth()/4) panxofs = viewWidth()/4;
				}
			} else {
				if (panxofs > -viewWidth()/4) {
					panxofs -= 15;
					if (panxofs < -viewWidth()/4) panxofs = -viewWidth()/4;
				}
			}
			panx = x+xspeed +16;
			if (bullettime>0) {
				bullettime--;
			} else {
				if (getKey(key_fire)) {
					bullettime=2;
					new Laser(x+16,y+8,dir,xspeed);
				}
			}
			if (pickedup!=null) {
				pickedup.x = x+xspeed*xdir + 8;
				pickedup.y = y+yspeed*ydir + 16;
			}
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,4+32+64) && invulnerability<=0) {
				lifeLost();
			} else if (and(obj.colid,16)) { // humanoid
				Humanoid h = (Humanoid)obj;
				if (!h.isFollowing() && pickedup==null) {
					h.setFollowing(this);
					pickedup = h;
				}
			} else { // homebase
				if (pickedup!=null) {
					new StdScoring("pts",obj.x+32,obj.y,0,-1.0,40,"100 pts",
						scoring_font,
						new JGColor [] { JGColor.blue,JGColor.cyan },2);
					score += 100;
					pickedup.remove();
					pickedup=null;
				}
			}
		}
		public void paint() {
			setColor(JGColor.white);
			if (invulnerability>0) {
				if (invulnerability%3==0)
					drawOval(x-4,y-4,40,24,true,false);
			}
			if (thrustdir==1) {
				setColor(JGColor.yellow);
				if (gametime%4<=1) {
					drawOval(x-6,y+10,12,6,true,true);
				} else {
					drawOval(x-4,y+10,8,6,true,true);
				}
			}
			if (thrustdir==-1) {
				setColor(JGColor.yellow);
				if (gametime%4<=1) {
					drawOval(x+32+6,y+10,12,6,true,true);
				} else {
					drawOval(x+32+4,y+10,8,6,true,true);
				}
			}
		}
	}
	JGColor[] cyclecol = new JGColor[] {
		JGColor.yellow,JGColor.green,JGColor.cyan,JGColor.blue,
		JGColor.magenta,JGColor.red };
	class Laser extends JGObject {
		public Laser(double x,double y,int dir,double speedofs) {
			super("laser",true,x,y,2,null,18);
			backx = x;
			this.dir = dir;
			this.speedofs = speedofs;
		}
		double backx;
		int dir;
		double speedofs;
		int colcycle=0;
		public void move() {
			x += 30*dir+speedofs;
			backx += 10*dir+speedofs;
			if (backx<x) {
				setBBox((int)-(x-backx),0,(int)(x-backx),1);
			} else {
				setBBox(0,0,(int)(backx-x),1);
			}
		}
		public void hit(JGObject obj) {
			if (!obj.isAlive()) return; // already hit by another laser
			remove();
			if (and(obj.colid,32)) return; // mine absorbs laser
			obj.remove();
			score += 5;
			if (obj instanceof Splitter) {
				score += 5;
				// generate globes
				for (double i=0; i<6.28; i+=1.05*0.5)
					new Globe(obj.x,obj.y, 6*Math.sin(i),6*Math.cos(i));
			}
			if (obj instanceof Baiter) {
				score += 20;
			}
			new Explo(obj.x+8,obj.y+8,0);
			new Explo(obj.x+8,obj.y+8,1);
			new Explo(obj.x+8,obj.y+8,2);
			new Explo(obj.x+8,obj.y+8,3);
		}
		public void paint() {
			setStroke(2);
			colcycle = (colcycle+1)%cyclecol.length;
			setColor(cyclecol[colcycle]);
			drawLine(x,y,backx,y);
		}
	}
	class Explo extends JGObject {
		public Explo(double x,double y,int wait) {
			super("explo",true,x,y,0,null,30+wait);
			phase1=random(0, 3.14);
			phase2=random(0, 3.14);
		}
		int colcycle;
		double phase1,phase2;
		public void paint() {
			if (expiry>=30) return;
			colcycle = (colcycle+1)%(cyclecol.length*2);
			setColor(cyclecol[colcycle/2]);
			for (int i=0; i<expiry/2; i++) {
				double expiry_fac = 0.3*Math.sin(phase2+(expiry-i)/4.0);
				drawOval(
					x + (i/2.0+1)*(30-expiry)*Math.sin(phase1+i+expiry_fac),
					y + (i/2.0+1)*(30-expiry)*Math.cos(phase1+i+expiry_fac),
					1+expiry/3,1+expiry/3,
					true,true);
			}
		}
	}
	JGColor[] flashcol = new JGColor[] {
		JGColor.red,JGColor.yellow,JGColor.white,JGColor.yellow,JGColor.red,
		JGColor.black,JGColor.black,JGColor.black};
	class HomeBase extends JGObject {
		public HomeBase(double x,double y) {
			super("homebase",false,x,y,8,null);
			setBBox(0,-16,64,16);
		}
		int colcycle1=0;
		int colcycle2=0;
		int colcycle3=6;
		public void paint() {
			colcycle1 = (colcycle1+1)%(flashcol.length*2);
			colcycle2 = (colcycle2+1)%(flashcol.length*4);
			colcycle3 = (colcycle3+1)%(flashcol.length*4);
			setColor(flashcol[colcycle1/2]);
			drawOval(x+34,y+3,4,4,true,true);
			setColor(flashcol[colcycle2/4]);
			drawOval(x+14,y+8,2,2,true,true);
			setColor(flashcol[colcycle3/4]);
			drawOval(x+54,y+8,2,2,true,true);
		}
	}
	class Humanoid extends JGObject {
		JGObject following=null;
		public Humanoid(double x) {
			super("humanoid",true,x,pfHeight()-23,16,null);
			setSpeed(random(0.6,0.8), 0.0);
			xdir=0;
		}
		public void move() {
			if (following==null) {
				if (y < pfHeight()-23) {
					stopAnim();
					xdir=0;
					y += 2;
					if (y >= pfHeight()-23) {
						y = pfHeight()-23;
						startAnim();
					}
				} else {
					if (xdir<0) {
						setGraphic("human_l");
					} else if (xdir>0) {
						setGraphic("human_r");
					} else {
						xdir = random(-1,1,2);
					}
				}
				setAnimSpeed(xspeed/0.7 * 0.3);
			} else {
				stopAnim();
				xdir=0;
				if (!following.isAlive()) {
					following=null;
				}
			}
		}
		public boolean isFollowing() { return following!=null; } 
		public void setFollowing(JGObject obj) { following=obj; }
	}
	class Enemy extends JGObject {
		public Enemy(String name,double x,double y,int type,String graphic) {
			super(name,true,x,y,type,null);
			this.graphic=graphic;
		}
		String graphic;
		int materialisetimer=90;
		int colcycle=0;
		public void move() {
			if (materialisetimer>0) {
				materialisetimer--;
				if (materialisetimer==0) {
					setGraphic(graphic);
					//default size
					x -= 8;
					y -= 8;
					materialise();
				}
			}
		}
		public void materialise() {}
		public boolean isMaterialised() { return materialisetimer<=0; }
		public void paint() {
			if (materialisetimer>0) {	
				colcycle = (colcycle+1)%(cyclecol.length*2);
				setColor(cyclecol[colcycle/2]);
				for (double i=0; i<6.28; i+=1.05) {
					drawOval(
						x + materialisetimer*Math.sin(i+materialisetimer/20.0),
						y + materialisetimer*Math.cos(i+materialisetimer/20.0),
						10-materialisetimer/10,10-materialisetimer/10,
						true,true);
				}
			}
		}
	}
	class Bomber extends Enemy {
		public Bomber(double x, double y) {
			super("bomber",x,y,4,"bomber");
		}
		int bombtimer=50;
		public void materialise() {
			setSpeed(random(-1,1,2)*2,random(-1,1,2)*2);
		}
		public void move() {
			super.move();
			if (!isMaterialised()) return;
			if (y < -21) y = pfHeight();
			if (y > pfHeight()+1) y = -20;
			if (bombtimer<=0) {
				bombtimer = random(100,200,1);
				JGObject mine=new JGObject("mine",true, x,y, 32,
					"mine" + (random(0,1) > 0.5 ? "" : "r"), 350);
				mine.setAnimSpeed(random(-1,1,2)*random(0.2,0.5));
			} else {
				bombtimer--;
			}
		}
	}
	class Splitter extends Enemy {
		public Splitter(double x, double y) {
			super("splitter",x,y,4,"splitter");
		}
		public void materialise() {
			setSpeed(random(-1,1,2)*1,0);
		}
		public void move() {
			super.move();
			if (!isMaterialised()) return;
			if (y < -21) y = pfHeight();
			if (y > pfHeight()+1) y = -20;
		}
	}
	class Baiter extends Enemy {
		public Baiter(double x, double y) {
			super("baiter",x,y,4,"baiter");
		}
		int bullettimer=20;
		public void move() {
			super.move();
			if (!isMaterialised()) return;
			JGObject player = getObject("player");
			if (player!=null) {
				if (x>player.x) xspeed -= 0.2;
				if (x<player.x) xspeed += 0.2;
				if (y>player.y) y -= 0.2;
				if (y<player.y) y += 0.2;
				xspeed *= 0.99;
				yspeed *= 0.99;
			}
			bullettimer--;
			if (bullettimer==0) {
				shootPlayer(x,y,4);
				bullettimer = random(15,35,1);
			}
		}
	}
	class UFO extends Enemy {
		public UFO(double x,double y) {
			super("ufo",x,y,4,"ufo");
		}
		int mode=0; // 1=homing in on human;  2=abducting human
		int homeintimer=200;
		Humanoid abduct=null;
		public void materialise() {
			setSpeed(random(-1,1,2)*random(2,3),random(-1,1,2)*random(0.5,1.5));
		}
		int bullettimer=100;
		public void move() {
			super.move();
			if (!isMaterialised()) return;
			bullettimer--;
			if (bullettimer==0) {
				shootPlayer(x,y,4);
				bullettimer = random(80,150,1);
			}
			if (mode==0) {
				if (y>pfHeight()-16 && yspeed>0) yspeed = -yspeed;
				if (y<0 && yspeed<0) yspeed = -yspeed;
				if (homeintimer==0) {
					// find humanoid
					Vector obj = getObjects("humanoid", 0, true,
						new JGRectangle((int)x-64, (int)y, 128+16, pfHeight()));
					for (Iterator obji=obj.iterator(); obji.hasNext();) {
						Humanoid h = (Humanoid)obji.next();
						if (h.isFollowing()) continue;
						mode=1;
						abduct = h;
						break;
					}
					homeintimer = random (100,200,1);
				} else {
					homeintimer--;
				}
			} else if (mode==1) {
				setSpeed(0,0);
				if (x > abduct.x+1) x -= 1.2;
				if (x < abduct.x-1) x += 1.2;
				y += 0.5;
				if (abduct.isFollowing() || !abduct.isAlive() || y>=pfHeight()){
					// abort, start moving again
					mode=0;
					setSpeed(random(-1,1,2)*random(2,3),
						random(-1,1,2)*random(0.5,1.5));
				}
			} else { // mode 2
				setSpeed(0,0);
				y -= 0.5;
				abduct.x = x+2;
				abduct.y = y+16;
				if (y<=0) {
					remove();
					abduct.remove();
					new Mutant(x,y);
				}
			}
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,16) && mode==1) {
				mode=2;
				abduct.setFollowing(this);
			}
		}
		public void paint() {
			super.paint();
			if (mode==1) drawImage((int)x+2,(int)y+16,"tractor");
		}
	}
	class Mutant extends JGObject {
		Mutant(double x,double y) {
			super("mutant",true, x,y, 4, "mutant");
		}
		int bullettimer=20;
		public void move() {
			bullettimer--;
			if (bullettimer==0) {
				shootPlayer(x,y,4);
				bullettimer = random(7,22,1);
			}
			JGObject player = getObject("player");
			if (player!=null) {
				if (x>player.x) xspeed -= 0.2;
				if (x<player.x) xspeed += 0.2;
				// we aim above player to evade lasers
				if (y>player.y-50) yspeed -= 0.2;
				if (y<player.y-50) yspeed += 0.2;
				xspeed *= 0.98;
				yspeed *= 0.98;
				//if (xspeed>0) {xspeed -= 0.05;} else {xspeed += 0.05; }
				//if (yspeed>0) {yspeed -= 0.05; } else {yspeed += 0.05; }
			}
		}
	}
	class Globe extends JGObject {
		Globe(double x,double y, double xspeed,double yspeed) {
			super("globe",true, x,y, 4, "globe");
			setSpeed(xspeed,yspeed);
		}
		public void move() {
			JGObject player = getObject("player");
			if (player!=null) {
				if (x>player.x) xspeed -= 0.2;
				if (x<player.x) xspeed += 0.2;
				if (y>player.y) yspeed -= 0.2;
				if (y<player.y) yspeed += 0.2;
				xspeed *= 0.95;
				yspeed *= 0.95;
			}
		}
	}
	public void shootPlayer(double x, double y, double bulspeed) {
		JGObject player = getObject("player");
		if (player!=null) {
			double angle = Math.atan2(player.x-x, player.y-y);
			new JGObject("bullet",true, x+2,y+2, 64, "bullet",
					bulspeed*Math.sin(angle),bulspeed*Math.cos(angle),
					JGObject.expire_off_view );
		}
	}
}
