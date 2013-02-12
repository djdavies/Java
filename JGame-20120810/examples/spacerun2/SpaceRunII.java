package examples.spacerun2;
import examples.StdScoring;
import jgame.*;
import jgame.platform.*;
/** A more customised version of Space Run */
public class SpaceRunII extends StdGame {
	public static void main(String [] args) {
		new SpaceRunII(parseSizeArgs(args,0));
	}
	public SpaceRunII() { initEngineApplet(); }
	public SpaceRunII(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(20,15,16,16,null,null,null); }
	public void initGame() {
		defineMedia("space_run.tbl");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		lives_img="ship";
		startgame_ingame=true;
		leveldone_ingame=true;
		title_color = JGColor.yellow;
		title_bg_color = new JGColor(140,0,0);
		title_font = new JGFont("Arial",0,20);
		setHighscores(10,new Highscore(0,"nobody"),15);
		highscore_title_color = JGColor.red;
		highscore_title_font = new JGFont("Arial",0,20);
		highscore_color = JGColor.yellow;
		highscore_font = new JGFont("Arial",0,16);
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
		if (checkTime(0,(int)(800),(int)((20-level))))
			new JGObject("pod",true,pfWidth(),random(0,pfHeight()-16),
				4, "pod", 0,0,14,14,  (-3.0-level), 0.0, -2);
		if (gametime>=800 && countObjects("enemy",0)==0) levelDone();
	}
	public void incrementLevel() {
		if (level<7) level++;
		stage++;
	}
	JGFont scoring_font = new JGFont("Arial",0,8);
	public void paintFrameLifeLost() {
		setColor(title_bg_color);
		drawRect(160,50,seqtimer*7,seqtimer*5,
			true,true,false);
		int ypos = posWalkForwards(-24,viewHeight(), seqtimer,
			80, 50, 20, 10);
		drawString("You're hit !",160,ypos,0,
			getZoomingFont(title_font,seqtimer,0.2,1/40.0),
				title_color);
	}
	public void paintFrameGameOver() {
		setColor(title_bg_color);
		setStroke(1);
		drawRect(160,51,seqtimer*2,seqtimer/2,
			true,true,false);
		drawString("Game Over !",160,50,0,
			getZoomingFont(title_font,seqtimer,0.2,1/120.0),
				title_color);
	}
	public void paintFrameStartGame() {
		drawString("Get Ready!",160,50,0,
			getZoomingFont(title_font,seqtimer,0.2,1/80.0),
				title_color);
	}
	public void paintFrameStartLevel() {
		drawString("Stage "+(stage+1),160,50+seqtimer,0,
			getZoomingFont(title_font,seqtimer,0.2,1/80.0),
				title_color);
	}
	public void paintFrameLevelDone() {
		drawString("Stage "+(stage+1)+" Clear !",160,50,0,
			getZoomingFont(title_font,seqtimer+80,0.2,1/80.0),
				title_color);
	}
	public void paintFrameTitle() {
		drawString("Space Run III",160,50,0,
			getZoomingFont(title_font,seqtimer+20,0.3,0.03),
				title_color);
		drawString("Press "+getKeyDesc(key_startgame)+" to start",160,120,0,
			getZoomingFont(title_font,seqtimer+5,0.3,0.03),
				title_color);
		if (!isMidlet())
			drawString("Press "+getKeyDesc(key_gamesettings)+" for settings",
				160,160,0,getZoomingFont(title_font,seqtimer,0.3,.03),
				title_color);
	}
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
