package examples.chainreaction;
import jgame.*;
import jgame.platform.*;
import examples.StdScoring;
public class ChainReaction extends JGEngine {
	public static void main(String [] args) { new ChainReaction(0,0); }
	public ChainReaction() { initEngineApplet(); }
	public ChainReaction(int x,int y) {initEngine(x,y); }
	public void initCanvas() { setCanvasSettings(20,15,32,32,null,null,null); }
	public void initGame() {
		defineMedia("chain_reaction.tbl");
		setFrameRate(40,4);
		setGameState("Title");
		setMouseCursor(CROSSHAIR_CURSOR);
		setBGImage("sky");
	}
	int timer=0, score=0, bullets=0, level=1;
	public class Count { public int value=-1; }
	public void doFrameTitle() {
		if (getMouseButton(1)) { // start game
			clearMouseButton(1);
			setGameState("InGame");
			score=0;
			bullets=15;
			timer=0;
			level=1;
		}
	}
	public void doFrameNextLevel() { moveObjects(); }
	public void doFrameInGame() {
		moveObjects();
		checkCollision(2,1); // explosions hit enemies
		if (getMouseButton(1) && bullets>0) {
			new Explo(getMouseX(),getMouseY(),new Count());
			bullets--;
			clearMouseButton(1);
		}
		if (timer%(int)(20) == 0) {
			if (countObjects("plane",0) < 20) {
				new Enemy(-32 + random(0,1,1)*(pfWidth()+32),
					random(0,pfHeight()-48),
					random(1.5+level/10.0, 3+level/7.0), 0, "plane" );
			}
			if (timer < 2000 && countObjects("paratrooper",0)<25) {
			// stop generating troopers after 50 sec
				new Enemy(random(0,pfWidth()-32), -32, random(-0.7,0.7),
				random(0.2+level/50.0, 0.8+level/12.0), "paratrooper" );
			}
		}
		timer++;
		if (timer >= 2000 && countObjects("paratrooper",0) == 0) {
			setGameState("NextLevel");
			new JGTimer(160,true) {public void alarm() { // start next level
				timer=0;
				removeObjects(null,0);
				score += bullets*150;
				if (level<12) level++;
				bullets=15;
				setGameState("InGame");
			}};
		}
	}
	public void paintFrame() {
		setColor(JGColor.white);
		drawString("Score "+score,0,0,-1);
		drawString("Bullets "+bullets,pfWidth()-8,0,1);
	}
	public void paintFrameNextLevel() {
		drawImageString("WAVE "+level+" FINISHED !",48,130,-1,"font_map",32,0);
		drawImageString("BONUS "+bullets+"X150: "+(bullets*150),32,270,-1,
			"font_map",32,0);
	}
	public void paintFrameGameOver() {
		drawImageString("GAME OVER",190,200,-1,"font_map",32,0);
	}
	public void paintFrameTitle() {
		drawImageString("CHAIN REACTION",100,130,-1,"font_map",32,0);
		drawImageString("MOUSE BUTTON TO START",0,270,-1,"font_map",32,-2);
	}
	JGFont scoring_font = new JGFont("Helvetica",0,12);
	public class Scoring extends JGObject {
		String msg;
		JGColor [] cols;
		public Scoring(String message,double x,double y,JGColor [] colors) {
			super("Scoring",true,x,y,0,null,0.0,-1.0,80);
			msg=message;
			cols=colors;
		}
		public void paint() {
			setFont(scoring_font);
			setColor(cols[(timer/2)%cols.length]);
			drawString(msg,(int)x,(int)y,0);
		}
	}
	public class Enemy extends JGObject {
		String graphic;
		public Enemy(double x, double y, double xspeed, double yspeed,
		String graphic) {
			super(graphic,true,x,y,1,null,xspeed,yspeed);
			this.graphic=graphic;
		}
		public void move() {
			if (x < 0            && xdir < 0) xdir = 1;
			if (x > pfWidth()-32 && xdir > 0) xdir = -1;
			if (y >= pfHeight()-32) { // trooper lands
				setSpeed(0,0);
				colid = 0;
				setGraphic("trooper_land");
				if (!inGameState("GameOver")) {
					addGameState("GameOver");
					new JGTimer(250,true) { public void alarm() {
						removeObjects(null,0);
						setGameState("Title");
					} };
				}
			} else {
				if (xdir<0) setGraphic(graphic+"_l");
				else        setGraphic(graphic+"_r");
			}
		}
		public void hit(JGObject obj) {
			Count count = ((Explo)obj).count; // length of chain reaction count
			score += 10*count.value;
			if (count.value > 4 && count.value < 10) {
				bullets++;
				new StdScoring("Scoring",x+16,y,0,-1,80,
					"+1 bullet",scoring_font,
					new JGColor [] {JGColor.yellow,new JGColor(128,64,0)},2);
			}
			new Explo(x+16,y+16, count);
			new StdScoring("Scoring",x+16,y+16,0,-1,80, count.value+"0 pts",
				scoring_font,new JGColor[] {JGColor.blue,JGColor.blue,JGColor.cyan},2);
			remove();
		}
	}
	JGColor [] explo_col = new JGColor[] { JGColor.red,JGColor.yellow,JGColor.green,
	JGColor.cyan,JGColor.blue,JGColor.magenta};
	public class Explo extends JGObject {
		double size=16, size_inc=10;
		public Count count;
		public Explo(double x, double y, Count count) {
			super("explo",true,x,y,2,null,0.0,0.0,32);
			this.count=count;
			count.value++;
		}
		public void move() {
			size += size_inc;
			size_inc -= 0.8;
			setBBox( (int)(-size/2.4), (int)(-size/2.4),
				(int)(size/1.2), (int)(size/1.2) );
		}
		public void paint() {
			setColor(explo_col[(timer/4)%explo_col.length]);
			drawOval((int)x,(int)y,(int)size,(int)size,true,true);
		}
	}
}
