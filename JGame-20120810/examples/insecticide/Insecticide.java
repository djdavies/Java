package examples.insecticide;
import jgame.*;
import jgame.platform.*;

public class Insecticide extends JGEngine {
	int timer=0;
	int score=0;
	int lives=3;
	int level=0,stage=0;
	JGColor bg_color = new JGColor(255,240,0);
	JGColor fg_color = new JGColor(100,50,0);
	public static void main(String [] args) { new Insecticide(0,0); }
	public Insecticide(int w,int h) { initEngine(w,h); }
	public Insecticide() { initEngineApplet(); }

	public void initCanvas(){
		setCanvasSettings(24,18,32,32,fg_color,bg_color,null);
	}

	public void initGame() {
		defineMedia("insecticide.tbl");
		setFrameRate(40,4);
		setGameState("Title");
		createBackground();
		setMsgFont(new JGFont("Helvetica",0,32));
	}
	public void doFrameTitle() {
		if (getKey('Z')) {
			score=0;
			timer=0;
			level=0;
			stage=0;
			lives=3;
			createBackground();
			setGameState("InGame");
		}
	}
	public void doFrameInGame() {
		if ((timer%220-10*level)==0 && timer<1500
		&& existsObject("player")) {
			int xpos = (getObject("player").x < pfWidth()/2) ?
					pfWidth() : -tileWidth();
			new Spider(xpos, random(pfHeight()/2,pfHeight()-tileHeight()),
				random(-1,1,2)*random(2,4), random(-1,1,2)*random(2,4) );
		}
		if ((timer%380-15*level)==20 && timer<1500) {
			int xdir = random(-1,1,2);
			double xpos = (xdir < 0) ? random(32*10,pfWidth()-32)
			                         : random(0,pfWidth()-32*11);
			for (int i=0; i<15; i++) {
				new Scorpion(xpos,0,1);
				xpos += 32*xdir;
			}
		}
		if (stage>2&& (timer%250-5*level - 70*(stage%3))==20 && timer<1500
		&& existsObject("player")) {
			int xdir = (getObject("player").x < pfWidth()/2) ? -1 : 1;
			int xpos = xdir < 0 ? pfWidth() : -tileWidth();
			new Beetle(xpos, random(0,pfHeight()-3*tileHeight()),
				random(1,2)*xdir, random(0.05,0.2));
		}
		if (stage>1&&(timer%250-5*level - 90*(stage%2))==30 && timer<1500
		&& existsObject("player")) {
			int xdir = (getObject("player").x < pfWidth()/2) ? -1 : 1;
			int xpos = xdir < 0 ? pfWidth() : -tileWidth();
			new Worm(xpos, random(0,pfHeight()-2*tileHeight()),xdir*4.0);
		}
		moveObjects();
		checkCollision(2,1);
		checkCollision(1,4);
		checkBGCollision(15,1|2|4);
		timer++;
		if (timer > 1500 && countObjects(null,1)==0) { // next level
			if (!inGameState("EndLevel")) {
				addGameState("EndLevel");
				new JGTimer(100,true,"EndLevel") { public void alarm() {
					setGameState("InGame");
					if (level<10) level++;
					stage++;
					timer=0;
					createBackground();
				} };
			}
		}
	}
	void createBackground() {
		new Player();
		setBGImage("empty_"+(stage%3));
		fillBG("");
		for (int i=0; i<15+level; i++) {
			setTile(
				(int)(random(0,pfTilesX()  )),
				(int)(random(0,pfTilesY()-1)), "m");
		}
	}

	public void paintFrame() {
		setColor(new JGColor(0,0,0));
		drawString("Score:"+score,0,0,-1);
		drawString("Level:"+(stage+1),pfWidth()/2,0,0);
		drawString("Lives:"+lives,pfWidth(),0,1);
	}
	public void paintFrameTitle() {
		setColor(new JGColor(0,0,0));
		drawString("Insecticide",pfWidth()/2,pfHeight()/3,0);
		drawString("Press Z to start",pfWidth()/2,2*pfHeight()/3,0);
	}
	public void paintFrameDead() {
		if (lives==1) drawString("Game Over",pfWidth()/2,pfHeight()/3,0);
	}

	public void paintFrameEndLevel() {
		drawString("Insects eradicated!",pfWidth()/2,pfHeight()/3,0);
	}

	public class Player extends JGObject {
		public Player () { 
			super("player",false,pfWidth()/2,pfHeight()-tileWidth(),4,"ship");
		}
		public void move() {
			JGPoint ct = getCenterTile();
			if (getKey(KeyUp)
			&& (!isYAligned(8) || (getTileCid(ct.x,ct.y-1) & 7) == 0))
				y-=8;
			if (getKey(KeyDown)
			&& (!isYAligned(8) || (getTileCid(ct.x,ct.y+1) & 7) == 0))
				y+=8;
			if (getKey(KeyLeft)
			&& (!isXAligned(8) || (getTileCid(ct.x-1,ct.y) & 7) == 0))
				x-=8;
			if (getKey(KeyRight)
			&& (!isXAligned(8) || (getTileCid(ct.x+1,ct.y) & 7) == 0))
				x+=8;
			if (x<0 || x>pfWidth()-32) snapToGrid(16,0);
			if (y>pfHeight()-32 || y<=pfHeight()*0.45) snapToGrid(0,16);
			if (getKey('Z') && !existsObject("bullet")) {
				new Bullet("bullet",x,y-16);
			}
		}
		public void hit(JGObject obj) {
			remove();
			createExplo(x,y,32);
			addGameState("Dead");
			new JGTimer(100,true,"Dead") { public void alarm() {
				removeObjects(null,0);
				if (--lives <= 0) {
					// game over
					setGameState("Title");
				} else {
					// continue level
					new Player();
					setGameState("InGame");
				}
			} };
		}
		public void hit_bg(int tilecid) {
			snapToGrid(16,16);
		}
	}

	public class Bullet extends JGObject {
		public Bullet (String name,
		double x,double y) { 
			super(name,false, x,y, 2, "bullet");
		}
		public void move() {
			y -= 32;
			if (y<-16) remove();
		}
		public void hit(JGObject obj) {
			remove();
		}
		public void hit_bg(int tilecid) {
			remove();
		}
		public void hit_bg(int tilecid,int tx,int ty, int txsize,int tysize) {
			for (int x=0; x<txsize; x++) {
				for (int y=0; y<tysize; y++) {
					int thiscid = getTileCid(tx+x,ty+y);
					setTile(tx+x,ty+y, "");
					if (thiscid >=1 && thiscid < 7) {
						setTile(tx+x,ty+y, "m"+(thiscid+1));
					}
				}
			}
		}
	}

	public class Spider extends JGObject {
		int frames_collided=0;
		public Spider (double x,double y,double xdir,double ydir) { 
			super("spider",true, x,y, 1, "spider",0,0,32,32,xdir,ydir);
		}
		public void move() {
			frames_collided--;
			//x+=xdir; y+=ydir;
			if (x<0 && xdir<0) xdir=-xdir;
			if (y<pfHeight()*0.5 && ydir<0) ydir=-ydir;
			if (x>pfWidth()-32 && xdir>0) xdir=-xdir;
			if (y>pfHeight()-32 && ydir>0) ydir=-ydir;
		}
		public void hit(JGObject obj) {
			createExplo(x,y,8);
			remove();
			obj.remove();
			score += 25;
		}
		public void hit_bg(int tilecid) {
			frames_collided += 3;
			if (frames_collided >3) return;
			JGRectangle rthis = getTileBBox();
			if (rthis==null) return;
			// try different directions from the previous positions to see
			// in which direction there is an obstacle
			JGRectangle r = new JGRectangle(rthis);
			r.x += getLastX()-x; // bbox previous x position
			int cidydir=checkBGCollision(r);
			r.x = rthis.x;
			r.y += getLastY()-y; // bbox previous y position
			int cidxdir=checkBGCollision(r);
			if (cidxdir!=0) {
				xdir = -xdir;
				snapToGrid(5,0);
			}
			if (cidydir!=0) {
				ydir = -ydir;
				snapToGrid(0,5);
			}
		}
	}
	public class Scorpion extends JGObject {
		boolean descending=false;
		int dir=1;
		public Scorpion (double x,double y, int dir) { 
			super("scorpion",true, x,y, 1, "scorpion_r",0,0,32,32,
				0,0, 5.0, 5.0*1.9, -1);
		}
		public void move() {
			if (dir > 0) setImage("scorpion_r"); else setImage("scorpion_l");
			if (descending) {
				y += yspeed;
				if (isYAligned()) {
					descending=false;
					snapToGrid(); // originally x only
				}
			} else {
				x += dir*xspeed;
				if ((x<0 && dir<0) || (x>640-32 && dir>0)) {
					descending=true;
					dir =- dir;
				}
				snapToGrid(); // originally x only
			}
			if (y >= pfHeight()) {
				y = pfHeight()/2;
				snapToGrid(0,32);
			}
			//if (!isOnScreen(8,8)) remove();
			//if (y>480-32 && dy>0) dy=-dy;
		}
		public void hit(JGObject obj) {
			createExplo(x,y,8);
			JGRectangle r=getCenterTiles();
			setTile(r.x+dir,r.y,"m");
			remove();
			obj.remove();
			score += 5;
		}
		public void hit_bg(int tilecid) {
			snapToGrid(16,yspeed/2.0);
			if (!descending) dir =- dir;
			descending=true;
		}
	}
	public class Beetle extends JGObject {
		int hibernating=-1;
		public Beetle (double x,double y,double xspeed,double yspeed) { 
			super("beetle",true, x,y, 1,null,xspeed,yspeed);
		}
		public void move() {
			if (xdir < 0) setGraphic("beetle_l");
			if (xdir > 0) setGraphic("beetle_r");
			if (x<0 && xdir<0) xdir=-xdir;
			if (y<0 && ydir<0) ydir=-ydir;
			if (x>pfWidth()-16 && xdir>0) xdir=-xdir;
			if (y>pfHeight()-16 && ydir>0) ydir=-ydir;
			if (hibernating == 0) {
				if (countObjects("beetle",0) < 25) {
					remove();
					new Beetle(x-8, y-8, -xspeed,yspeed-0.05);
					new Beetle(x-8, y+8, -xspeed,yspeed+0.1);
					new Beetle(x+8, y-8, xspeed,yspeed-0.05);
					new Beetle(x+8, y+8, xspeed,yspeed+0.1);
				}
			} else hibernating--;
		}
		public void hit(JGObject obj) {
			createExplo(x,y,8);
			remove();
			obj.remove();
			score += 15;
		}
		public void hit_bg(int tilecid) {
			if (hibernating < 0) {
				hibernating=(int)random(100,140);
				setGraphic("mushroom_gr");
				setDir(0,0);
			}
		}
		public void hit_bg(int tilecid,int tx,int ty) {
			setTile(tx,ty,"");
			setPos(tx*tileWidth(),ty*tileHeight());
		}
	}
	public class Worm extends JGObject {
		int hibernating=-1;
		public Worm (double x,double y,double xspeed) { 
			super("worm",true, x,y, 1,null,0,0,32,32,xspeed,0);
			snapToGrid(0,32);
		}
		public void move() {
			if (random(0,1) > 0.96) {
				setTile(getCenterTile(),"m");
			}
			if (xdir < 0) setGraphic("worm_l");
			if (xdir > 0) setGraphic("worm_r");
			if (!isOnPF(16,16)) remove();
		}
		public void hit(JGObject obj) {
			createExplo(x,y,8);
			remove();
			obj.remove();
			score += 50;
		}
	}
	public void createExplo(double x,double y, int intensity) {
		for (int i=0; i<intensity; i++) {
			new JGObject("explo",true, x,y, 0, "explo",
				random(-5,5), random(-5,5), (int)random(10,10+intensity) );
		}
	}

}
