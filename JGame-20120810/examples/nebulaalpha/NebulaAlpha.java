package examples.nebulaalpha;
import jgame.*;
import jgame.platform.*;
/** A minimal game using nothing but the JGame base classes. */
public class NebulaAlpha extends JGEngine {
	public static void main(String [] args) { new NebulaAlpha(0,0); }
	public NebulaAlpha() { initEngineApplet(); }
	public NebulaAlpha(int x,int y){initEngine(x,y);}
	public void initCanvas() { setCanvasSettings(20,15,32,32,null,null,null); }
	public void initGame() {
		defineMedia("nebula_alpha.tbl");
		setBGImage("bgimg");
		setFrameRate(40,4);
		setGameState("Title");
		// doesn't work in openjdk
		//playAudio("music","mainmusic",true);
	}
	int timer=0, score=0;
	double gamespeed=1.0;
	public void doFrameTitle() {
		if (getKey(' ')) {
			new Player(pfWidth()/2-25,pfHeight()-113);
			setGameState("InGame");
			score=0;
			gamespeed=1.0;
		}
	}
	public void doFrameInGame() {
		moveObjects();
		checkCollision(2,1); // robots hit player
		checkCollision(4,2); // bullets hit robots
		timer++;
		if (gamespeed<2) gamespeed += 0.0001;
		if (timer%(int)(20/gamespeed) == 0) {
			new Robot(random(0,pfWidth()-64), -90, (int)random(-1,1,2) );
		}
	}
	public void paintFrame() {
		drawImageString("SCORE "+score,150,0,-1,"font_map",32,0);
	}
	public void paintFrameGameOver() {
		drawImageString("GAME OVER",165,200,-1,"font_map",32,0);
	}
	public void paintFrameTitle() {
		drawImageString("NEBULA ALPHA",120,200,-1,"font_map",32,0);
	}
	public class Robot extends JGObject {
		public Robot(double x, double y, int dir) {
			super("robot",true,x,y,2,null);
			setSpeedAbs(dir*2.0*gamespeed,0);
		}
		public void move() {
			if (y < pfHeight()-200) {
				y += 2.0 * gamespeed; 
			} else {
				y += ( 2.0 - (y-(pfHeight()-200))/200.0*3.5 )*gamespeed;
			}
			if (xdir<0) { setAnim("robot_l"); } else { setAnim("robot_r"); }
			if (x < 0) xdir = 1;
			if (x > pfWidth()-64)  xdir=-1;
		}
		public void hit(JGObject obj) {
			new JGObject("explo",true,x,y,0,"explo", 0.0,0.0, 32);
			playAudio("explo");
			remove();
			obj.remove();
			score += 5;
		}
	}
	public class Player extends JGObject {
		public Player(double x, double y) {
			super("player",false,x,y,1,"player"); 
		}
		public void move() {
			if (getKey(KeyLeft)  && x > 14)              x -= 14*gamespeed;
			if (getKey(KeyRight) && x < pfWidth()-51-14) x += 14*gamespeed;
			if (getKey(' ')) {
				if (countObjects("bullet",0) < 2) {
					new JGObject("bullet",true,x-8,y-8,4,"bullet",0.0,-14.0,-2);
					playAudio("shoot");
					clearKey(' ');
				}
			}
		}
		public void hit(JGObject obj) {
			new JGObject("explo",true,x,y,0,"explo", 0.0,0.0, 32);
			remove();
			addGameState("GameOver");
			new JGTimer(100,true) { public void alarm() {
					setGameState("Title"); removeObjects(null,0);  } };
		}
	}
}
