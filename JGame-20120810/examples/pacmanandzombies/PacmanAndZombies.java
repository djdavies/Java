package examples.pacmanandzombies;
import jgame.*;
import jgame.platform.*;
import examples.StdMazePlayer;
import examples.StdMazeMonster;
import examples.StdDungeonMonster;
import examples.StdScoring;
import examples.StdVirtualKeyboard;
public class PacmanAndZombies extends StdGame {
	public static void main(String [] args) {new PacmanAndZombies(parseSizeArgs(args,0));}
	public PacmanAndZombies() { initEngineApplet(); }
	public PacmanAndZombies(JGPoint size) { initEngine(size.x,size.y);  }
	public void initCanvas() {
		setCanvasSettings(19,15,32,32,null,null,null);
		if (isMidlet()) {
			setScalingPreferences(3.0/4.0,4.0/3.0, 0,5,5,5);
		}
	}
	public void initGame() {
		if (isAndroid()) {
			defineMedia("media-android.tbl");
			optsAddEnum("controls","Controls","",
				new String[] { "Accelerometer",
					"Virtual keys", "Swipe" }, 2);
			optsAddBoolean("visualise_dir","Visualise direction",
				"Show virtual keyboard and accelerometer direction as overlay",
					true);
			//optsAddString("username","Your Name", "", 20,false, "Player");
			//optsAddNumber("volume","Volume","", 1, 8.5,30,0.5, 12.5);
		} else {
			defineMedia("media.tbl");
		}
		setVideoSyncedUpdate(true);
		if (isMidlet()) {
			setFrameRate(15,4);
			setGameSpeed(2.0);
		} else if (isAndroid()) {
			setFrameRate(30,4);
			virtkey = new StdVirtualKeyboard(this);
			virtkey.paintVirtualKeyboardInit(0, 15*32, 0,19*32, 0.82, 0.82,
					0.5, 0.5);
		} else {
			setFrameRate(40,4);
		}
		// the in-game sequences depend on these precise timings
		startgame_ticks=120;
		leveldone_ticks=120;
		lifelost_ticks=300;
		gameover_ticks=160;
		startgame_ingame=true;
		lifelost_ingame=true;
		accel_set_zero_menu=true;
		stage=1;
		//defineLevel();
		//playAudio("intro");
		setTextOutline(1,JGColor.black);
	}
	Player player=null;
	JGObject ghost_house=null;
	int nr_pills;
	double powerpilltime=0,powerpillmax;
	int powerpillcaught;
	int moantimer=2000;
	JGObject newpill=null;
	int newpillanimtmr=0;
	double newzombietime=10;
	int nr_extra_pills_left=0;

	StdVirtualKeyboard virtkey=null;

	public void doFrameInGame() {
		if (getKey('L')) levelDone();
		if (virtkey!=null) {
			switch (storeReadInt("controls",0)) {
			case 0:
				virtkey.accelerometerToKeys(getAccelZeroCorrected(), 1.0);
			break;
			case 1:
				virtkey.handleVirtualKeyboard(-10,15*32+10,-10,19*32+10);
			break;
			case 2:
				virtkey.swipeToKeys4Dir(24);
			break;
			}
		}
		moveObjects();
		checkBGCollision(2,1); // pills hit player
		checkBGCollision(4,1); // revolving doors hit player
		//checkCollision(1+8,2); // player, ghost_house hits enemies
		checkCollision(4+2,1); // pills, ghosts hit player
		if (powerpilltime > 0) powerpilltime -= getGameSpeed();
		if (newzombietime > 0) {
			newzombietime -= getGameSpeed();
		} else {
			newzombietime = 70 - level*(40.0/7.0);
			newzombietime = random(newzombietime-8,newzombietime+8);
			if (countObjects("zombie",0) < 25 && player!=null) {
				int tx,ty;
				while (true) {
					tx = random(1,18,1);
					ty = random(1,14,1);
					double dx = tx*32 - player.x;
					double dy = ty*32 - player.y;
					if (dx*dx + dy*dy < 32*32*7*7) continue;
					if (and(getTileCid(tx,ty),1+8)) continue;
					break;
				}
				playAudio("spawn"+random(1,2,1));
				new Zombie(tx,ty);
			}
		}
		moantimer -= countObjects("zombie",0);
		if (player!=null && player.is_dead) moantimer -= 20;
		if (moantimer < 0) {
			playAudio("moan"+random(1,3,1));
			moantimer=random(2000,2300,1);
		}
	}
	JGColor keyReleasedCol = new JGColor(255,255,255,60);
	JGColor keyPressedCol = new JGColor(255,255,255,135);
	public void paintFrameInGame() {
		if (virtkey!=null && storeReadInt("controls",0) == 1) {
			virtkey.paintVirtualKeyboard(keyPressedCol,keyReleasedCol);
		}
		if (newpillanimtmr>0) {
			newpillanimtmr -= 2.0*getGameSpeed();
			if ( (seqtimer%4) < 2) {
				setColor(JGColor.orange);
			} else {
				setColor(JGColor.red);
			}
			setStroke(3.0);
			drawOval(newpill.x+16,newpill.y+16,
				4*(newpillanimtmr+8),4*(newpillanimtmr+8), false,true);
		}
	}
	JGFont scorefont = new JGFont("Helvetica",0,18);
	public void paintFrame() {
		//drawImageString(""+score,0,0,-1,"font_map",32,2);
		setFont(scorefont);
		drawString(""+score,16,0,-1);
		for (int i=1; i<lives; i++) drawImage(pfWidth()-32-32*i,0,"pacman_r2");
	}
	public void startTitle() {
		//removeObjects("__joystick",256);
		stopAudio("ambient");
		fillBG(".");
	}
	public void doFrameTitle() {
		if (getMouseButton(1)) {
			startGame(0);
		}
	}
	public void paintFrameTitle() {
		drawImage(150,40,"splash_image");
		//drawString("Settings: username="+storeReadString("username","")
		//	+" controls="+storeReadInt("controls",0)
		//	+" bool="+storeReadInt("bool",-1)
		//	+" volume="+storeReadDouble("volume",0.0), 10,40 , -1);
		//drawImageString("PUB MAN",pfWidth()/2,75,0,"font_map",32,2);
		setFont(new JGFont("Helvetica",0,25));
		setColor(JGColor.white);
		if (!isMidlet()) {
			drawString("Press " + getKeyDesc(key_startgame)+" or tap to start",
				pfWidth()/2,420,0);
		} else {
			drawImageString("STAR TO START", pfWidth()/2,130,0,"font_map",32,2);
			drawImageString("POUND TO QUIT", pfWidth()/2,160,0,"font_map",32,2);
		}
		drawString("by Boris van Schooten",pfWidth()/2,260,0);
		drawString("Visit tmtg.net for more games",pfWidth()/2,290,0);
		//drawString("Flick in a direction to move.  Tap screen to stop.", pfWidth()/2, 330,0);
		setFont(new JGFont("Helvetica",0,14));
		drawString("Credits:", pfWidth()/2, 360,0);
		drawString("Zombie sprite by Tsuyoshi."
			+" Zombie sounds by Kathol. Backgrounds by freestockimages.org",
			pfWidth()/2, 380,0);
		//"crackman" font by typodermicfonts.com
		// zombie font by Sinister Visions
	}
	public void paintFrameLifeLost() {
		setColor(JGColor.white);
		setFont(new JGFont("Helvetica",0,30));
		int ypos = posWalkForwards(-32,pfHeight(),seqtimer, 300, 75, 10,160);
		drawString("Zombies ate your brain!",pfWidth()/2,ypos,0);
	}
	public void paintFrameGameOver() {
		setColor(JGColor.white);
		setFont(new JGFont("Helvetica",0,30));
		drawString("Game Over!",pfWidth()/2,150,0);
	}
	public void paintFrameLevelDone() {
		setColor(JGColor.white);
		setFont(new JGFont("Helvetica",0,30));
		int ypos = posWalkForwards(-32,pfHeight(),seqtimer, 120, 150, 10,70);
		drawString("Level Clear!",pfWidth()/2,ypos,0);
	}
	public void paintFrameStartLevel() {
		setColor(JGColor.white);
		setFont(new JGFont("Helvetica",0,30));
		int ypos = posWalkForwards(pfHeight()-8,pfHeight()-8,
			seqtimer, 120, 150-40, 55,50);
		drawString("Level "+(stage+1),pfWidth()/2,ypos,0);
	}
	public void paintFrameStartGame() {
		setColor(JGColor.white);
		setFont(new JGFont("Helvetica",0,30));
		int ypos = posWalkForwards(pfHeight()+24,pfHeight()+24,
			seqtimer, 100, 150, 55,10);
		drawString("Ready!",pfWidth()/2,ypos,0);
	}
	public void incrementLevel() {
		if (level<7) level++;
		stage++;
	}
	//StdTouchJoystick joy = null;
	public void startInGame() {
		powerpillmax=300;
		powerpilltime=0;
		// replace everything except the power pills
		removeObjects("zombie",0);
		player=new Player(9*32,11*32,key_up,key_down,key_left,key_right);
		stopAudio("ambient");
		ghost_house = new JGObject("ghost_house",false,9*tileWidth(),
			5*tileHeight()-2,8,null);
		ghost_house.setBBox(0,0,32,32);
		for (int i=0; i<1; i++) {
			//new Enemy(9,5,0,-1, 1.5+i*0.1);
		}
		//removeObjects("__joystick",256);
		//joy = new StdTouchJoystick(256,pfWidth()-100,pfHeight()-100,100);
		setGameSpeed(1.0);
	}
	public void startGameOver() { removeObjects(null,0); }
	public void defineLevel() {
		nr_extra_pills_left = 4;
		setBGImage("bg"+(stage%4));
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
		//setTile(8,3,"f");
		//setTile(9,3,"f");
		//setTile(6,1,"g");
		//setTile(7,3,"g");
		//setTile(7,4,"f");
		//setTile(7,5,"f");
		// facilitate player escape route
		for (int i=6;i<=9;i++) setTileCid(i,11,64);
		setTileCid(9,12,64);
		setTileCid(2,1,64);
		setTileCid(17,1,64);
		// revolving doors
		/*for (int i=0; i<5-(level%3); i++) {
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
		}*/
		// some extra wall segments
		for (int i=0; i<6+level; i++) {
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
		//setTile(9,4,"=");
		//setTile(8,5,"#"+(stage%5));
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
		placePowerPill();
		placePowerPill();
		placePowerPill();
		//if (level<2) placePowerPill();
		/*// place power pills
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
		}*/
		nr_pills=countTiles(2);
		// dig side tunnels
		int tunnelpos = random(5,9,2);
		setTile(0,tunnelpos,".");
		setTile(18,tunnelpos,".");
	}
	boolean place_left=true;
	JGObject placePowerPill() {
		int tx,ty;
		while (true) {
			tx = place_left ? random(1,8,1) : random(10,18,1);
			ty = random(1,14,1);
			if (player!=null) {
				double dx = tx*32 - player.x;
				double dy = ty*32 - player.y;
				if (dx*dx + dy*dy < 32*32*6*6) continue;
			}
			if (and(getTileCid(tx,ty),1)) continue;
			break;
		}
		place_left = !place_left;
		return new JGObject("powerpill",true,
			tx*tileWidth(),ty*tileHeight(),4,"powerpill");
	}
	void floodFillPills(int x,int y) {
		if (and(getTileCid(x,y),1+2+8)) return;
		if (!and(getTileCid(x,y),4+32)) setTile(x,y,"o");
		if (x>0) floodFillPills(x-1,y);
		if (y>0) floodFillPills(x,y-1);
		if (x<pfTilesX()-1) floodFillPills(x+1,y);
		if (y<pfTilesY()-1) floodFillPills(x,y+1);
		
	}
	public class Zombie extends JGObject {
		public Zombie(int tx,int ty) {
			super("appear",true,tx*32,ty*32,0,"zombie_appear",24);
		}
		public void move() {
			if ((int)expiry == 1) {
				new StdDungeonMonster(
				"zombie",true,x,y, 2, "zombie_",true,false,
				/*MONSTERBLOCK_T*/1+8,/*MONSTER_T*/8, 
				0.5 + 0.75*level/7.0,
				player,false,0.0);
			}
		}
	}
	int pacmaneattimer=0;
	public class Player extends StdMazePlayer {
		public boolean is_dead=false;
		public Player(double x, double y,int k_u,int k_d,int k_l,int k_r) {
			super("player", x,y, 1, "pacman_", true, true, 1, 5.0,
			k_u, k_d, k_l, k_r);
		}
		public void move() {
			if (is_dead) {
				setSpeed(0,0);
				stopAnim();
				return;
			}
			if (powerpilltime > 80) {
				gfx_prefix = "pacman_p_";
			} else if (powerpilltime > 0) {
				if ((powerpilltime%16) < 8) {
					gfx_prefix = "pacman_p_";
				} else {
					gfx_prefix = "pacman_";
				}
			} else {
				gfx_prefix = "pacman_";
			}
			setGraphic(gfx_prefix+cur_gfx_suffix);
			if (x < -16) x = pfWidth()-16;
			if (x>pfWidth()-16) x = -16;
			super.move();
		}
		public void hit(JGObject obj) {
			if (obj.colid==4) {
				// hit power pill
				obj.remove();
				score += 50;
				if (powerpilltime<=0) powerpillcaught=0;
				powerpilltime=powerpillmax+1;
				playAudio("eatpill");
			} else if (obj.colid==2) {
				// zombie
				if (powerpilltime > 0) {
					// zombie dies
					score += 1*(1<<powerpillcaught);
					new StdScoring("Scoring",x+16,y+8,0,0, 80,
						""+(1*(1<<powerpillcaught)),
						new JGFont("Helvetica",0,20),
						new JGColor[] {JGColor.white,JGColor.green}, 16);
					if (powerpillcaught==11 && nr_extra_pills_left>0) {
						newpill = placePowerPill();
						playAudio("newpill");
						newpillanimtmr=200;
						nr_extra_pills_left--;
					}
					if (powerpillcaught < 12) powerpillcaught++;
					obj.remove();
					JGPoint cen = obj.getCenterTile();
					String cen_t = getTileStr(cen.x,cen.y);
					if (cen_t.equals("b1")) {
						setTile(cen.x,cen.y,"b2");
					} else if (cen_t.equals("b2")) {
						setTile(cen.x,cen.y,"b3");
					} else if (cen_t.equals("c1")) {
						setTile(cen.x,cen.y,"c2");
					} else if (cen_t.equals("c2")) {
						setTile(cen.x,cen.y,"c3");
					} else if (cen_t.equals(".")) {
						setTile(cen.x,cen.y, random(1,2,1)==1 ? "b1":"c1");
					}
					playAudio("eatzombie");
				} else {
					// player dies
					// make sure player dies only once
					playAudio("ambient","zombieeating",true);
					if (!is_dead) {
						is_dead=true;
						lifeLost();
						setGameSpeed(2.0);
					}
					// zombie eat animation
					String anim = ((StdDungeonMonster)obj).gfx_prefix;
					anim += ((StdDungeonMonster)obj).cur_gfx_suffix;
					JGObject zombieeat = 
						new JGObject("zombie_eat",true,obj.x,obj.y,0,anim,300);
					zombieeat.setSpeed(obj.xdir*obj.xspeed/20,
						obj.ydir*obj.yspeed/20);
					obj.remove();
				}
			}		
		}
		public void hit_bg(int tilecid,int tx,int ty, int txsize,int tysize) {
			// get pills
			for (int dy=0; dy<tysize; dy++) {
				for (int dx=0; dx<txsize; dx++) {
					if (and(getTileCid(tx+dx,ty+dy),2)) {
						setTile(tx+dx,ty+dy,".");
						nr_pills--;
						score += 5;
						playAudio("pacmaneat"+pacmaneattimer);
						pacmaneattimer = (pacmaneattimer+1)%2;
					}
				}
			}
			if (nr_pills <= 0) {
				levelDone();
				//playAudio("leveldone");
			}
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
		public void paint() {
			double [] ci = null;
			if (virtkey!=null) ci = virtkey.getCompassInfo();
			if (ci!=null) {
				setColor(new JGColor(255,255,255,128));
				//drawOval(16+x+joy.xdist*100,16+y+joy.ydist*100, 16,16,true,true);
				drawOval(12+x+ci[0]*20,12+y+ci[1]*20, 24,24,true,true);
			}
		}
	}
	public class Enemy extends StdMazeMonster {
		boolean is_eaten=false;
		boolean is_restored=false;
		public Enemy(double x, double y, int xdir, int ydir,double speed) {
			super("enemy",true,32*x,32*y,2,"enemy_",true, 1+4,xdir,ydir,
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
