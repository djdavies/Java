package examples.spacerun3;
import examples.StdScoring;
import jgame.*;
import jgame.platform.*;
/** Space Run III, a variant on Space Run, illustrating scrolling and wrapping
 * playfield. */
public class SpaceRunIII extends StdGame {
	public static void main(String [] args) {
		new SpaceRunIII(parseSizeArgs(args,0));
	}
	public SpaceRunIII() { initEngineApplet(); }
	public SpaceRunIII(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() {
		setCanvasSettings(20,15,16,16,null,null,null);
		if (isMidlet())
			setScalingPreferences(3.0/4.0,4.0/3.0, 0,7,0,7);
	}
	public void initGame() {
		defineMedia("space_run.tbl");
		if (isMidlet()) {
			setFrameRate(18,1);
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
	public void defineLevel() {
		removeObjects(null,0);
		switch (stage%2) {
		case 0:
			leveldone_ingame=true;
			setPFSize(150,40);
			setPFWrap(false,false,0,0);
			int tunnelheight = 11-level/2;
			int tunnelpos = pfTilesY()/2 - tunnelheight/2;
			fillBG("#");
			int firstpart=15;
			int oldpos=0;
			for (int x=0; x<pfTilesX(); x++) {
				for (int y=tunnelpos; y<tunnelpos+tunnelheight; y++) {
					setTile(x,y,"");
				}
				if (firstpart>0) {
					firstpart--;
				} else {
					if (random(0,5) < 1)
						new JGObject("enemy",true,tileWidth()*x,
							tileHeight()*(oldpos+tunnelheight/2),
							2, "enemy", 0,0,16,16,
							-1,0, JGObject.suspend_off_view);
					if (random(0,5) < 1)
						new JGObject("pod",true,tileWidth()*x,
							tileHeight()*(oldpos+random(2,tunnelheight-3,1)),
							4, "pod", 0,0,14,14, 0,0, JGObject.suspend_off_view);
					oldpos = tunnelpos;
					tunnelpos += random(-1,1,2);
					if (tunnelpos < 1) tunnelpos = 1;
					if (tunnelpos + tunnelheight >= pfTilesY()-1)
						tunnelpos = pfTilesY()-tunnelheight-1;
				}
			}
		break;
		case 1:
			leveldone_ingame=false;
			setPFSize(21,16);
			setPFWrap(true,true,-8,-8);
			fillBG("");
			for (int i=0; i<5+level/2; i++) {
				new BombDropper();
				new JGObject("pod",true,random(pfWidth()/2,pfWidth()),
					random(0,pfHeight()),
					4, "pod", 0,0,14,14, 0,random(0.5,1.2), -1);
			}
		break; }
		new Player(0,pfHeight()/2,3);
	}
	public void initNewLife() {
		defineLevel();
	}
	public void startGameOver() { removeObjects(null,0); }
	public void doFrameInGame() {
		moveObjects();
		checkCollision(2+4,1); // enemies, pods hit player
		checkBGCollision(1,1); // bg hits player
		if (stage%2==1 && countObjects("pod",0)==0) levelDone();
		setViewOffset((int)getObject("player").x+100,
			(int)getObject("player").y,true);
	}
	public void incrementLevel() {
		if (level<10) level++;
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
			super("player",false,x,y,1,"ship",0,0,speed,speed,-1);
		}
		public void move() {
			setDir(0,0);
			if (getKey(key_up))   ydir=-1;
			if (getKey(key_down)) ydir=1;
			if (getKey(key_right)) { x += getGameSpeed()*3*xspeed/2; }
			else                        { x += getGameSpeed()*xspeed;   }
			if (!isOnPF(0,0)) levelDone();
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
		public void hit_bg(int tilecid) { lifeLost(); }
	}
	class BombDropper extends JGObject {
		public BombDropper() {
			super("enemy",true,SpaceRunIII.this.random(pfWidth()/3,pfWidth()),
				SpaceRunIII.this.random(0,pfHeight()), 2, "enemy");
				setSpeed(random(-0.7,-0.3),random(-0.5,0.5));
		}
		public void move() {
			if (random(0,1) < 0.005) {
				JGPoint cen = getCenterTile();
				setTile(cen.x,cen.y,"#");
			}
		}
	}
}
