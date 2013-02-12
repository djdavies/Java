package examples.spacerun;
import examples.StdScoring;
import jgame.*;
import jgame.platform.*;
/** A minimal game using StdGame with default settings. */
public class SpaceRun extends StdGame {
	public static void main(String[]args) {new SpaceRun(parseSizeArgs(args,0));}
	public SpaceRun() { initEngineApplet(); }
	public SpaceRun(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(20,15,16,16,null,null,null); }
	public void initGame() {
		defineMedia("space_run.tbl");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		setHighscores(10,new Highscore(0,"nobody"),15);
	}
	public void initNewLife() {
		removeObjects(null,0);
		new Player(16,pfHeight()/2,5);
	}
	public void startGameOver() { removeObjects(null,0); }
	public void doFrameInGame() {
		moveObjects();
		checkCollision(2+4,1); // enemies, pods hit player
		if (checkTime(0,(int)(800),(int)((8-level/2))))
			new JGObject("enemy",true,pfWidth(),random(0,pfHeight()-16),
				2, "enemy", 0,0,16,16,
				(-3.0-level), random(-1,1), -2);
		if (checkTime(0,800,20-level))
			new JGObject("pod",true,pfWidth(),random(0,pfHeight()-16),
				4, "pod", 0,0,14,14,  (-3.0-level), 0.0, -2);
		if (gametime>=800 && countObjects("enemy",0)==0) levelDone();
	}
	public void incrementLevel() {
		if (level<7) level++;
		stage++;
	}
	JGFont scoring_font = new JGFont("Arial",0,8);
	public class Player extends JGObject {
		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"ship", 0,0,32,16,0,0,speed,speed,-1);
		}
		public void move() {
			setDir(0,0);
			if (getKey(key_up)    && y > yspeed)               ydir=-1;
			if (getKey(key_down)  && y < pfHeight()-16-yspeed) ydir=1;
			if (getKey(key_left)  && x > xspeed)               xdir=-1;
			if (getKey(key_right) && x < pfWidth()-32-yspeed)  xdir=1;
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,2)) lifeLost();
			else {
				score += 5;
				obj.remove();
				new StdScoring("pts",obj.x,obj.y,0,-1.0,40,"5 pts",scoring_font,
					new JGColor [] { JGColor.red,JGColor.yellow },2);
			}
		}
	}
}
