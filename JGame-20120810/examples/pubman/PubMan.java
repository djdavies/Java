package examples.pubman;
import examples.StdMazePlayer;
import examples.StdMazeMonster;
import examples.StdScoring;
import jgame.*;
import jgame.platform.*;
public class PubMan extends StdGame {
	public static void main(String [] args) {new PubMan(parseSizeArgs(args,0));}
	public PubMan() { initEngineApplet(); }
	public PubMan(JGPoint size) { initEngine(size.x,size.y);  }
	public void initCanvas() {
		setCanvasSettings(19,15,16,16,null,null,null);
		if (isMidlet()) {
			setScalingPreferences(3.0/4.0,4.0/3.0, 0,5,5,5);
		}
	}
	public void initGame() {
		defineMedia("pub_man.tbl");
		setVideoSyncedUpdate(true);
		if (isMidlet()) {
			setFrameRate(15,4);
			setGameSpeed(2.0);
		} else {
			setFrameRate(40,4);
		}
		// the in-game sequences depend on these precise timings
		startgame_ticks=80;
		leveldone_ticks=80;
		lifelost_ticks=80;
		gameover_ticks=120;
		startgame_ingame=true;
		stage=1;
		defineLevel();
	}
	Player player=null;
	JGObject ghost_house=null;
	int nr_pills;
	double powerpilltime=0,powerpillmax;
	int powerpillcaught;
	public void doFrameInGame() {
		moveObjects();
		checkBGCollision(2,1); // pills hit player
		checkBGCollision(4,1); // revolving doors hit player
		checkCollision(1+8,2); // player, ghost_house hits enemies
		checkCollision(4,1); // pills hit player
		if (powerpilltime > 0) powerpilltime -= getGameSpeed();
	}
	JGFont scorefont = new JGFont("Helvetica",0,18);
	public void paintFrame() {
		//drawImageString(""+score,0,0,-1,"font_map",32,2);
		setFont(scorefont);
		drawString(""+score,16,0,-1);
		for (int i=1; i<lives; i++) drawImage(pfWidth()-16-16*i,0,"player_d7");
	}
	public void paintFrameTitle() {
		drawImage(90,72,"splash_image");
		//drawImageString("PUB MAN",pfWidth()/2,75,0,"font_map",32,2);
		if (!isMidlet()) {
			drawImageString("PRESS " + getKeyDesc(key_startgame).toUpperCase(),
				pfWidth()/2,140,0,"font_map",32,2);
		} else {
			drawImageString("STAR TO START", pfWidth()/2,130,0,"font_map",32,2);
			drawImageString("POUND TO QUIT", pfWidth()/2,160,0,"font_map",32,2);
		}
	}
	public void paintFrameLifeLost() {
		int ypos = posWalkForwards(-16,pfHeight(),seqtimer, 80, 75, 10,30);
		drawImageString("CAUGHT!",pfWidth()/2,ypos,0,"font_map",32,2);
	}
	public void paintFrameGameOver() {
		drawImageString("GAME OVER!",pfWidth()/2,75,0,"font_map",32,2);
	}
	public void paintFrameLevelDone() {
		int ypos = posWalkForwards(-16,pfHeight(),seqtimer, 80, 75, 10,30);
		drawImageString("LEVEL CLEAR!",pfWidth()/2,ypos,0,"font_map",32,2);
	}
	public void paintFrameStartLevel() {
		int ypos = posWalkForwards(pfHeight()-8,pfHeight()-8,
			seqtimer, 80, 145-32, 55,10);
		drawImageString("LEVEL "+(stage+1),pfWidth()/2,ypos,0,"font_map",32,2);
	}
	public void paintFrameStartGame() {
		int ypos = posWalkForwards(pfHeight()+24,pfHeight()+24,
			seqtimer, 80, 145, 55,10);
		drawImageString("READY!",pfWidth()/2,ypos,0,"font_map",32,2);
	}
	public void incrementLevel() {
		level++;
		stage++;
		if (level>=9) level=6;
	}
	public void startInGame() {
		powerpillmax=250-22*level;
		powerpilltime=0;
		// replace everything except the power pills
		removeObjects("enemy",0);
		player=new Player(9*16,11*16,key_up,key_down,key_left,key_right);
		ghost_house = new JGObject("ghost_house",false,9*tileWidth(),
			5*tileHeight()-2,8,null);
		ghost_house.setBBox(0,0,16,16);
		for (int i=0; i<4+level/3; i++) new Enemy(9,5,0,-1, 1.5+i*0.1);
	}
	public void startGameOver() { removeObjects(null,0); }
	public void defineLevel() {
		removeObjects(null,0);
		fillBG(".");
		// build left half of maze
		// generate base wall matrix
		for (int y=0; y<pfTilesY(); y++) {
			for (int x=0; x<=pfTilesX()/2; x++) {
				if((!and(x,1) && !and(y,1)) ) {
					setTile(x,y,"#"+(stage%5));
				} else
				if (x==0 || x==pfTilesX()-1
				||  y==0 || y==pfTilesY()-1 ) {
					setTile(x,y,"#"+(stage%5));
				}
			}
		}
		// facilitate path for ghosts
		setTile(8,3,"f");
		setTile(9,3,"f");
		setTile(6,1,"g");
		setTile(7,3,"g");
		setTile(7,4,"f");
		setTile(7,5,"f");
		// facilitate player escape route
		for (int i=6;i<=9;i++) setTile(i,11,"f");
		setTile(9,12,"f");
		// revolving doors
		for (int i=0; i<5-(level%3); i++) {
			int xcen,ycen;
			do {
				xcen=(int)random(2,pfTilesX()/2-2,2);
				ycen=(int)random(2,pfTilesY()-4,4) + (((xcen&2)!=0) ? 0 : 2);
				// don't place when there's a wall, ghost path, door in the way
			} while (and( getTileCid(xcen-1,ycen)|getTileCid(xcen+1,ycen)
				     |getTileCid(xcen,ycen-1)|getTileCid(xcen,ycen+1),21));
			if (random(0,1) > 0.5
			|| (xcen>=4 && xcen<=6 && ycen >=4 && ycen <=6)) {
				setTile(xcen+1,ycen,"d");
				setTile(xcen-1,ycen,"d");
				setTile(xcen,ycen-1,"v");
				setTile(xcen,ycen+1,"^");
			} else {
				setTile(xcen+1,ycen,"<");
				setTile(xcen-1,ycen,">");
				setTile(xcen,ycen-1,"d");
				setTile(xcen,ycen+1,"d");
			}
		}
		// some extra wall segments
		for (int i=0; i<10; i++) {
			int wx,wy;
			// only place when there's no wall, keep-free path, or door,
			// and avoid some critical locations
			do {
				wx = (int)random(1,pfTilesX()/2,1);
				wy = (int)random(1,pfTilesY()-1,2) + ((wx&1)!=0 ? 1 : 0);
			} while (and(getTileCid(wx,wy),1+64+4+32)
			|| (wx==5 && wy==6)
			|| (wx==5 && wy==8));
			setTile(wx,wy,"#"+(stage%5));
		}
		// ghost house
		setTile(9,4,"=");
		setTile(8,5,"#"+(stage%5));
		setTile(8,6,"#"+(stage%5));
		setTile(9,6,"#"+(stage%5));
		// mirror the left half
		for (int y=0; y<pfTilesY(); y++) {
			for (int x=0; x<pfTilesX()/2; x++) {
				String tile =getTileStr(x,y);
				if (tile.equals(">")) tile="<";
				else if (tile.equals("<")) tile=">";
				setTile(pfTilesX()-1-x, y, tile);
			}
		}
		// flood fill with pills
		floodFillPills(9,11);
		// place power pills
		for (int py=1; py<pfTilesY(); py+=pfTilesY()-3) {
			for (int px=1; px<pfTilesX()/2-1; px++) {
				// find place where player can come
				if (and(getTileCid(px,py),2)) {
					new JGObject("powerpill",true,
						px*tileWidth(),py*tileHeight(),4,"powerpill");
					new JGObject("powerpill",true,
						(pfTilesX()-1-px)*tileWidth(),py*tileHeight(),4,
						"powerpill");
					break;
				}
			}
		}
		nr_pills=countTiles(2);
	}
	void floodFillPills(int x,int y) {
		if (and(getTileCid(x,y),1+2+8)) return;
		if (!and(getTileCid(x,y),4+32)) setTile(x,y,"o");
		if (x>0) floodFillPills(x-1,y);
		if (y>0) floodFillPills(x,y-1);
		if (x<pfTilesX()-1) floodFillPills(x+1,y);
		if (y<pfTilesY()-1) floodFillPills(x,y+1);
		
	}
	public class Player extends StdMazePlayer {
		public boolean is_dead=false;
		public Player(double x, double y,int k_u,int k_d,int k_l,int k_r) {
			super("player", x,y, 1, "player_", true, false, 1+8, 3.0,
			k_u, k_d, k_l, k_r);
		}
		public void move() {
			super.move();
		}
		public void hit(JGObject obj) { // hit power pill
			obj.remove();
			score += 50;
			powerpilltime=powerpillmax+1;
			powerpillcaught=0;
		}
		public void hit_bg(int tilecid,int tx,int ty, int txsize,int tysize) {
			// get pills
			for (int dy=0; dy<tysize; dy++) {
				for (int dx=0; dx<txsize; dx++) {
					if (and(getTileCid(tx+dx,ty+dy),2)) {
						setTile(tx+dx,ty+dy,".");
						nr_pills--;
						score += 5;
					}
				}
			}
			if (nr_pills <= 0) levelDone();
			// revolve doors
			JGPoint cen = getCenterTile();
			if (and(getTileCid(cen,0,0),4)) {
				// find rotation direction
				String door = getTileStr(cen,0,0);
				if (door.equals("^")) { //above
					setTile(cen.x,cen.y,".");
					setTile(cen.x,cen.y-2,".");
					setTile(cen.x-1,cen.y-1,">");
					setTile(cen.x+1,cen.y-1,"<");
				} else if (door.equals("v")) { //below 
					setTile(cen.x,cen.y,".");
					setTile(cen.x,cen.y+2,".");
					setTile(cen.x-1,cen.y+1,">");
					setTile(cen.x+1,cen.y+1,"<");
				} else if (door.equals("<")) { //left
					setTile(cen.x,cen.y,".");
					setTile(cen.x-2,cen.y,".");
					setTile(cen.x-1,cen.y-1,"v");
					setTile(cen.x-1,cen.y+1,"^");
				} else if (door.equals(">")) { //right
					setTile(cen.x,cen.y,".");
					setTile(cen.x+2,cen.y,".");
					setTile(cen.x+1,cen.y-1,"v");
					setTile(cen.x+1,cen.y+1,"^");
				}
			}
		}
	}
	public class Enemy extends StdMazeMonster {
		boolean is_eaten=false;
		boolean is_restored=false;
		public Enemy(double x, double y, int xdir, int ydir,double speed) {
			super("enemy",true,16*x,16*y,2,"enemy_",true, 1+4,xdir,ydir,
				speed, null,false, 0.4);
		}
		public void move() {
			if (is_eaten) { // return to ghost house
				avoid=false;
				home_in=ghost_house;
				gfx_prefix = "enemy_bk_";
				block_mask=1;
				random_proportion=0.25;
			} else {
				random_proportion=0.4;
				block_mask=1+4;
				if (powerpilltime>=powerpillmax) { // reverse when pill eaten
					xdir = -xdir;
					ydir = -ydir;
				}
				if (powerpilltime > 0 && !is_restored) { // flee
					home_in=player;
					avoid=true;
					if (powerpilltime<80 && ((int)powerpilltime&8)!=0) {
						gfx_prefix="enemy_w_";
					} else {
						gfx_prefix = "enemy_b_";
					}
				} else if (powerpilltime <= 0 || powerpilltime >= powerpillmax){
					// clear restored flag and home in
					is_restored=false;
					home_in=player;
					avoid=false;
					gfx_prefix = "enemy_";
				} else { // powerpilltime>0 && is_restored -> home in
					home_in=player;
					avoid=false;
					gfx_prefix = "enemy_";
				}
			}
			super.move();
		}
		public void hit(JGObject obj) {
			if (!(obj instanceof Player)) { // ghost house
				if (is_eaten) {
					is_eaten=false;
					is_restored=true;
				}
			} else { // player
				if (powerpilltime > 0 && !is_eaten && !is_restored) {
					// ghost dies
					is_eaten=true;
					xdir = -xdir;
					ydir = -ydir;
					score += 100*(1<<powerpillcaught);
					new StdScoring("Scoring",x+8,y+4,0,0, 80,
						""+(100*(1<<powerpillcaught)),
						new JGFont("Helvetica",0,10),
						new JGColor[] {JGColor.white,JGColor.green}, 16);
					powerpillcaught++;
				}
				if ((powerpilltime<=0&&!is_eaten)||is_restored) {// player dies
					// make sure player dies only once
					if (!((Player)obj).is_dead) {
						((Player)obj).is_dead=true;
						lifeLost();
					}
				}
			}
		}
	}
}
