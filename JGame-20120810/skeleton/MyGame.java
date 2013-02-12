package mygame;
import jgame.*;
import jgame.platform.*;

/** Minimal shooter for jgame skeletons. */
public class MyGame extends StdGame {
	public static void main(String[]args) {new MyGame(parseSizeArgs(args,0));}
	public MyGame() { initEngineApplet(); }
	public MyGame(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(32,24,8,8,null,null,null); }
	public void initGame() {
		defineMedia("media.tbl");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		setHighscores(10,new Highscore(0,"nobody"),15);
		startgame_ingame=true;
	}
	public void initNewLife() {
		removeObjects(null,0);
		new Player(pfWidth()/2,pfHeight()-32,5);
	}
	public void doFrameTitle() {
		if (getMouseButton(1)) {
			clearMouseButton(1);
			startGame();
		}
	}
	public void startGameOver() { removeObjects(null,0); }
	public void doFrameInGame() {
		moveObjects();
		checkCollision(2,1); // enemies hit player
		checkCollision(4,2); // bullets hit enemies
		if (checkTime(0,(int)(800),(int)((12-level/2))))
			new Enemy();
		if (gametime>=800 && countObjects("enemy",0)==0) levelDone();
	}
	public void incrementLevel() {
		score += 50;
		if (level<7) level++;
		stage++;
	}
	JGFont scoring_font = new JGFont("Arial",0,8);
	public class Enemy extends JGObject {
		double timer=0;
		public Enemy() {
			super("enemy",true,MyGame.this.random(32,pfWidth()-40),-8,
					2, stage%2==1 ? "block" : "ball",
					MyGame.this.random(-1,1), (1.0+level/2.0), -2 );
		}
		public void move() {
			timer += gamespeed;
			x += Math.sin(0.1*timer);
			y += Math.cos(0.1*timer);
			if (y>pfHeight()) y = -8;
		}
		public void hit(JGObject o) {
			remove();
			o.remove();
			score += 5;
		}
	}
	public class Player extends JGObject {
		int prevmousex=0;
		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"shipu", 0,0,speed,speed,-1);
		}
		public void move() {
			setDir(0,0);
			if (getKey(key_left)  && x > xspeed)               xdir=-1;
			if (getKey(key_right) && x < pfWidth()-32-yspeed)  xdir=1;
			if (getKey(key_fire) && countObjects("bullet",0) < 2) {
				new JGObject("bullet",true,x,y,4,"bary", 0.0,-5.0, -2);
				clearKey(key_fire);
			}
			if (getMouseButton(1)) {
				new JGObject("bullet",true,x,y,4,"bary", 0.0,-5.0, -2);
				clearMouseButton(1);
			}
			if (getMouseX()!=prevmousex && getMouseInside()) {
				x = getMouseX();
				prevmousex = getMouseX();
			}
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,2)) lifeLost();
			else {
				score += 5;
				obj.remove();
			}
		}
	}
}
