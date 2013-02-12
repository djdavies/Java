package examples.waterworld;
import jgame.*;
import jgame.platform.*;
public class WaterWorld extends StdGame {
	public static void main(String [] args) {
		new WaterWorld(parseSizeArgs(args,0));
	}
	public WaterWorld() { initEngineApplet(); }
	public WaterWorld(JGPoint size){ initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(20,15,32,32,null,null,null); }

	public String action_msg=null;
	public Player player;
	public int level_nr; // determines platforms and monster freqencies
	// determines certain speed and regeneration factors
	public int gamedifficulty;
	public int goodies_left;

	public class LevelDefs {
		public int multiplier=37;
		public int modulo=17;
		public int divider=5;
		public int increment=4;
		public int width=2;
		public int solid_freq=4;
		public int waterlevel;
		public int special_flags;
		public int ball_freq=20;
		public int bird_freq=30;
		public int fish_freq=50;
		public LevelDefs(int multiplier, int modulo, int divider,
		int increment, int width, int solid_freq, int waterlevel,
		int special_flags,  int ball_freq,int bird_freq,int fish_freq) {
			this.multiplier=multiplier;
			this.modulo=modulo;
			this.divider=divider;
			this.increment=increment;
			this.width=width;
			this.solid_freq=solid_freq;
			this.waterlevel=waterlevel;
			this.special_flags=special_flags;
			this.ball_freq=ball_freq;
			this.bird_freq=bird_freq;
			this.fish_freq=fish_freq;
		}
	}

	public LevelDefs lev;

	public final LevelDefs [] levels = {
		new LevelDefs(1,  4, 2,  2, 1, 2,     5,  0,  30, 50, 90),// intro level
		new LevelDefs(11,  5, 1,  5, 3, 9999, 1,  2,  25, 99999, 99999),
		
		new LevelDefs(19, 5, 1,   5, 3, 9999, 9,  1,  40, 99999, 70),

		new LevelDefs(37, 17, 5,  4, 2, 4,    6,  0,  35, 45, 50),
		new LevelDefs(1,  5, 1,   6, 1, 1,    3,  0,  99999, 30, 100),
		new LevelDefs(43,  3, 1,  5, 3, 3,    5,  4,  30, 50, 40),
	};

	public void defineLevel() {
		lev = levels[level_nr];
		removeObjects(null,0);
		fillBG(" ");
		int alter=1;
		for (int y=2; y<15; y+=2) {
			setTiles(0,y,new String[] {  "                    "  });
			for (int x = ((y*lev.multiplier)%lev.modulo)/lev.divider;
			x<20; x += lev.increment ){
				for (int w=0; w<lev.width; w++) {
					if ((alter % lev.solid_freq)==0) {
						setTile(x+w,y,"#");
					} else {
						setTile(x+w,y,"-");
					}
					if (y>2) setTile(x+w,y-1,"*");
				}
				alter++;
			}
		}
		/* fill level with water */
		for (int y=pfTilesY()-lev.waterlevel; y<pfTilesY(); y++) {
			for (int x=0; x<pfTilesX(); x++) {
				String tile = getTileStr(x,y);
				if (tile.equals(" ")) {
					setTile(x,y,"w");
				} else if (tile.equals("*")) {
					setTile(x,y,"*w");
				}
				if ( (tile.equals("-") || tile.equals("*") || tile.equals("#"))
				&&   (x<2 || x>pfTilesX()-3)) {
					setTile(x,y,"w");
				}
			}
		}
		// add special features
		if ((lev.special_flags&1)!=0) { // dividing wall in the middle
			setTiles(pfTilesX()/2-1, 0, new String[] {
				"##", "##", "##", "##", "##", "##", "##","##","##","##"
			});
		}
		if ((lev.special_flags&2)!=0) { // two walls on the left and right
			setTiles(2*pfTilesX()/3+2, 6, new String[]{"#","#","#","#","#"});
			setTiles(pfTilesX()/3-2, 6, new String[] {"#","#","#","#","#"});
		}
		if ((lev.special_flags&4)!=0) { // basins on the left and right
			setTilesMulti(pfTilesX()-6, 7, new String[] {
				"# w  w  *w w w",
				"# w  w  #  # w",
				"# *w *w w  w w",
				"# #  #  w  w w",
				"# w  w  *w w w",
				"# w  w  #  # w",
				"# *w *w *w w w",
				"# #  #  #  # w",
			});
			setTilesMulti(0, 7, new String[] {
				"w w *w w  w  #",
				"w # #  w  w  #",
				"w w w  *w *w #",
				"w w w  #  #  #",
				"w w *w w  w  #",
				"w # #  w  w  #",
				"w w *w *w *w #",
				"w # #  #  #  #",
			});
			for (int y=0; y<=lev.waterlevel; y++) {
				if (y==lev.waterlevel) {
					setTiles(5, pfTilesY()-y, new String[] {"####ww####"});
				} else if (y%2==0) {
					setTilesMulti(5, pfTilesY()-y, new String[] {
						"*w *w w w *w *w w w *w *w"});
				} else {
					setTiles(5, pfTilesY()-y, new String[] {"##ww##ww##"});
				}
			}
		}
		goodies_left = countTiles(4);
	}

	public void initGame() {
		defineMedia("water_world.tbl");
		setFrameRate(40,4);
		startgame_ticks=60;
		lifelost_ticks=100;
		leveldone_ticks=100;
		startgame_ingame=true;
		// we need to do this because otherwise doFrame will be called once
		// without lev being inited
		setGameState("Title");
	}
	public void startTitle() {
		level_nr=0;
		gamedifficulty=0;
		defineLevel();
		playAudio("music","titlemusic",true);
	}
	public void initNewGame(int level_selected) {
		level_nr=1;
		gamedifficulty=0;
		lives=3;
		stopAudio("music");
	}
	public void incrementLevel() {
		level_nr++;
		if (level_nr >= levels.length) {
			level_nr=1;
			if (gamedifficulty < 5) gamedifficulty++;
		}
		lev = levels[level_nr];
	}

	public void doFrame() {
		super.doFrame();
		moveObjects();
		checkCollision(2,1);
		checkCollision(4,2);
		checkBGCollision(3,5);
		checkBGCollision(4,1);
		checkBGCollision(11,6);
		if ((int)timer%(lev.ball_freq-3*gamedifficulty) == 0) {
			new Ball(random(-tileHeight(),pfWidth()-32),
				-tileHeight(),
				-1 + 2*(int)(Math.random()+0.5) );
		}
		if ((int)timer%(lev.bird_freq-3*gamedifficulty) == 0) {
			int x,dir,y;
			while (true) {
				if (player==null || player.x > pfWidth()/2) {
					x = -tileWidth();
					dir = 1;
				} else {
					x = pfWidth();
					dir = -1;
				}
				y=(int)random(32,(pfTilesY()-lev.waterlevel)*tileHeight(),64);
				int cid = getTileCidAtCoord(x+tileWidth()*dir,y);
				if ( (cid&1+2+8) == 0) break;
			}
			new Bird(x,y,dir);
		}
		if ((int)timer%(lev.fish_freq-2*gamedifficulty) == 0) {
			int x,dir,y;
			int timeout=8;
			while ((timeout--) > 0) {
				if (random(0,2) >= 1.0) {
					x = -tileWidth();
					dir = 1;
				} else {
					x = pfWidth();
					dir = -1;
				}
				y=(int)random(32, pfHeight(), 32);
				int cid = getTileCidAtCoord(x+tileWidth()*dir,y);
				if ( (cid&(1+2))==0 && (cid&8)!=0 ) {
					new Fish(x,y,dir);
					break;
				}
			}
		}
		if (countObjects("bubble",0) > 0) {
			playAudio("bubbling","bubbling",true);
		} else {
			stopAudio("bubbling");
		}
	}

	public void startStartGame() {
		removeObjects(null,0);
		player=new Player(pfWidth()/4,32*5);
	}

	public void paintFrame() {
		drawImageString("SCORE "+score,0,0,-1,"fonts_map",32,0);
		//drawImageString("OXYGEN: ==========================",0,pfHeight()-16,"fonts_map",32,0);
		for (int i=1; i<lives; i++) drawImage(pfWidth()-32*i,0,"player_l1");
	}
	public void paintFrameStartGame() {
		drawImageString("START",pfWidth()/2,pfHeight()/3,0,"fontl_map",32,0);
	}
	public void paintFrameStartLevel() {
		drawImageString("LEVEL "+(stage+1),pfWidth()/2,2*pfHeight()/3,0,
			"fontl_map",32,0);
	}
	public void paintFrameTitle() {
		drawImageString("WATER WORLD",16*9,pfHeight()/3,-1,
				"fontl_map",32,0);
		drawImageString("PRESS " + getKeyDesc(key_startgame).toUpperCase()
			+ " TO START", 0, 2*pfHeight()/3, -1, "fontl_map",32,0);
	}
	public void paintFrameLevelDone() {
		drawImageString("LEVEL CLEAR !",16*7,pfHeight()/3,-1, "fontl_map",32,0);
	}

	public void paintFrameLifeLost() {
		drawImageString("OUCH!",pfWidth()/2,pfHeight()/3,0, "fontl_map",32,0);
	}
	public void paintFrameGameOver() {
		drawImageString("GAME OVER", pfWidth()/2,pfHeight()/3,0,
			"fontl_map",32,0);
	}

	public class Explo extends JGObject {
		public Explo(double x,double y) {
			super("explo",true,x,y,0,"explo", 0.0,0.0,16);
		}
	}

	public class Bubble extends JGObject {
		public Bubble(int x,int y,int xdir,int ydir) {
			super("bubble",true,x+8,y+8,4,"bubble", 0.0,0.0,-2);
			setDirSpeed(xdir,1, 14.0, ydir>0 ?  8  :  (ydir<0 ? -5 : 1) );
		}
		public void move() {
			if (xspeed > 0.8) {
				xspeed -= 0.8;
			} else {
				xspeed = 0.0;
			}
			if (yspeed > -3) yspeed -= 0.3;
			JGRectangle cts = getCenterTiles();
			if ( (getTileCid(cts.x, cts.y)&8)==0) {
				if (yspeed > 1) {
					yspeed =- yspeed;
				} else {
					remove();
				}
			}
		}
		public void hit() {
			remove();
		}
	}

	public class Bullet extends JGObject {
		public Bullet(int x,int y,int dir) {
			super("bullet",true,x+8,y+8,4,"bullet", dir,1, 8.0,-2.0, -2);
			playAudio("shoot");
		}
		public void move() {
			yspeed += 0.5;
		}
		public void hit() {
			remove();
		}
		public void hit_bg(int tilecid,int tx,int ty,int txsize,int tysize) {
			if ((tilecid&2) != 0) {
				/* bounce */
				int tlcid = getTileCid(tx,ty);
				int blcid = getTileCid(tx,ty+tysize-1);
				int trcid = getTileCid(tx+txsize-1,ty);
				int brcid = getTileCid(tx+txsize-1,ty+tysize-1);
				if (xdir==0) remove();
				if ((xdir==1  && ((tlcid|blcid)&2)==0)
				||  (xdir==-1 && ((trcid|brcid)&2)==0)) {
					xdir = -xdir;
				}
				if ((yspeed > 0.0 && ((blcid&2)&(brcid&2))!=0)
				||  (yspeed < 0.0 && ((tlcid&2)&(trcid&2))!=0)) {
					if (yspeed < 0.0) {
						yspeed = -yspeed;
					} else {
						yspeed = -(yspeed/2.0);
					}
				}
			}
			if ((tilecid&8) != 0) {
				yspeed = -(yspeed/2.0);
				if (yspeed > -2.0 || xdir == 0) remove();
			}
		}
	}

	public class Player extends JGObject {
		double speed=6;
		int jumptime=0;
		int falltime=0;
		int bullettime=0;
		int dir=1;
		boolean jumping_up=false,jumping_down=false, swimming=false;
		public Player (double x,double y) { 
			super("player",false, x,y, 1, "player_l1",0,0,32,32);
		}
		public void move() {
			if (!swimming && (checkBGCollision(getTileBBox())&8)!=0) {
				swimming=true;
				playAudio("splash","splash",false);
				jumptime=0;
				setAnim("player_swd");
				falltime = (int)(tileHeight()/speed);
			}
			if (!swimming) {
				moveNorm();
			} else {
				moveSwim();
			}
			if (x >= pfWidth()-32)  x = pfWidth()-32;
			if (y >= pfHeight()-32) y = pfHeight()-32;
			if (x < 0) x=0;
			if (y < 0) y=0;
		}
		void moveSwim() {
			int cid =checkBGCollision(getTileBBox()); 
			stopAnim();
			if (falltime > 0) {
				y += speed;
				falltime--;
				return;
			}
			if (bullettime > 0) {
				bullettime--;
			} else {
				if (getKey(key_fireleft)) {
					new Bubble((int)x,(int)y,-1,0);
					bullettime=5;
				} else if (getKey(key_fireright)) {
					new Bubble((int)x,(int)y,1,0);
					bullettime=5;
				} else if (getKey(key_firedown)) {
					new Bubble((int)x,(int)y,0,1);
					bullettime=5;
				} else if (getKey(key_fireup)) {
					new Bubble((int)x,(int)y,0,-1);
					bullettime=5;
				}
			}
			if (getKey(key_left)) {
				setAnim("player_swl");
				startAnim();
				x-=speed;
				snapToGrid(0,16);
				dir = -1;
			} else if (getKey(key_right)) {
				setAnim("player_swr");
				startAnim();
				x+=speed;
				snapToGrid(0,16);
				dir = 1;
			} else if (getKey(key_up)) {
				setAnim("player_swu");
				startAnim();
				y-=speed;
				snapToGrid(16,0);
				dir = 0;
				if ((cid&8)==0) {
					jumptime=22;
					swimming=false;
					return;
				}
			} else if (getKey(key_down)) {
				setAnim("player_swd");
				startAnim();
				y+=speed;
				snapToGrid(16,0);
				dir = 0;
			}
			if ((cid&8)==0) {
				swimming=false;
			}
		}
		public void moveNorm() {
			snapToGrid(speed/2,0); // ensure we can fall through small holes
			JGRectangle ts = getTiles();
			JGRectangle cts = getCenterTiles();
			int cid=0;
			for (int tdx=0; tdx<ts.width; tdx++) {
				cid |= getTileCid(ts.x+tdx, cts.y+1);
			}
			if (bullettime > 0) {
				bullettime--;
			} else {
				if (getKey(key_fireleft)) {
					new Bullet((int)x,(int)y,-1);
					bullettime=12;
				} else if (getKey(key_fireright)) {
					new Bullet((int)x,(int)y,1);
					bullettime=12;
				}
			}
			stopAnim();
			if (jumptime<=0) {
				jumping_up=false;
				jumping_down=false;
				if (isYAligned(speed)) {
					if ((cid&3)==0) {
						/* no support -> fall */
						y += speed;
						// make sure the player is tile aligned when it falls
						// off a tile, or it might find support when it should
						// fall through a hole.
						snapToGrid(speed/2.0,0);
					} else {
						/* stand on ground */
						snapToGrid(0,speed);
						if (getKey(key_left)) {
							setAnim("player_l");
							startAnim();
							dir = -1;
							x-=speed;
						}
						if (getKey(key_right)) {
							setAnim("player_r");
							startAnim();
							dir = 1;
							x+=speed;
						}
						if (getKey(key_up)) {
							jumptime=22;
						}
					}
				} else {
					/* fall until aligned */
					y += speed;
				}
			} else { /* jumping */
				if (jumptime>11) { /* up */
					y -= speed;
					jumping_up=true;
					jumping_down=false;
				} else { /* down */
					y += speed;
					jumping_up=false;
					jumping_down=true;
					/* see if we hit the ground */
					if (isYAligned(speed)) {
						cid=0;
						for (int tx=0; tx<ts.width;tx++) {
							cid |= getTileCid(ts.x+tx, cts.y+1);
						}
						if ((cid&3)!=0) jumptime=0;
					}
				}
				if (getKey(key_left)) {
					setAnim("player_l");
					startAnim();
					x-=speed;
					dir = -1;
				}
				if (getKey(key_right)) {
					setAnim("player_r");
					startAnim();
					x+=speed;
					dir = 1;
				}
				jumptime--;
			}
		}
		public void hit(JGObject obj) {
			remove();
			new Explo(x,y);
			lifeLost();
		}
		public void hit_bg(int tilecid,int tx,int ty,int txsize,int tysize) {
			if ((tilecid&2) != 0) {
				/* what we should do here is complex and depends on our state.
				 * But, we only need to handle bumping into things here.
				 * Support is handled by the regular move routine.  We need
				 * concern ourselves only with type 2 material.
				 *
				 * if we are jumping up, we should be blocked by type 2
				 * material.  If we bump our head on type 2 material the jump
				 * should be aborted.
				 *
				 * if we are jumping down or are not jumping, we should be
				 * blocked on our sides by type 2 material.
				 */
				if (jumping_up) {
					if (isYAligned(speed*2)) {
						boolean bump_head=false;
						JGRectangle cts = getCenterTiles();
						for (int tdx=0; tdx<txsize; tdx++) {
							boolean topwall =
								(getTileCid(tx+tdx, cts.y-1)&2) != 0;
							boolean botwall =
								(getTileCid(tx+tdx, cts.y  )&2) != 0;
							if (topwall && !botwall) {
								bump_head=true;
								break;
							}
						}
						if (bump_head) {
							jumptime=0;
							snapToGrid(speed,speed);
						} else {
							snapToGrid(speed,0);
						}
					} else {
						snapToGrid(speed,0);
					}
				} else if (!swimming) {
					snapToGrid(speed,0);
				} else {
					snapToGrid(speed,speed);
				}
				/*for (int tdx=0; tdx<txsize; tdx++) {
					cid |= canvas.getTileCid(tx+tdx, ty);
				}*/
			}
			if ((tilecid&1) != 0 && swimming) {
				snapToGrid(speed,speed);
			}
			if ((tilecid&4) != 0) {
				playAudio("pickup");
				for (int x=0; x<txsize;x++) {
					for (int y=0; y<tysize;y++) {
						if ((getTileCid(tx+x,ty+y)&4)!=0) {
							goodies_left--;
							score += 25;
							if ((getTileCid(tx+x,ty+y)&8)!=0) {
								setTile(tx+x,ty+y,"w");
							} else {
								setTile(tx+x,ty+y," ");
							}
						}
					}
				}
			}
			// check if level finished
			if (goodies_left<=0) {
				remove(); // delete myself until the next level starts
				levelDone();
			}
		}
	}

	public class Ball extends JGObject {
		double speed=1.7 + 0.3*gamedifficulty;
		double dir=1;
		boolean was_falling=false;
		boolean floating=false;
		int float_timeout=100;
		public Ball (double x,double y,int dir) { 
			super("ball",true, x,y, 2, null,0,0,32,32);
			this.dir=dir;
		}
		public void move() {
			JGRectangle ts = getTiles();
			JGRectangle cts = getCenterTiles();
			if (!floating) {
				snapToGrid(speed/2,0); // ensure we can fall through holes
				if (isYAligned(speed)) {
					int cid=0;
					for (int tdx=0; tdx<ts.width; tdx++) {
						cid |= getTileCid(ts.x+tdx, cts.y+1);
					}
					if ((cid&3)==0) {
						/* no support -> fall */
						y += speed;
						was_falling=true;
					} else {
						/* move to dir */
						snapToGrid(0,speed);
						x += dir*speed;
						was_falling=false;
					}
				} else {
					/* fall until aligned */
					y += speed;
				}
			} else {
				x += dir*1.0;
				if ((float_timeout--) <= 0) {
					new Explo(x,y); 
					remove();
				}
			}
			if (dir >= 0) { setAnim("ball_r"); }
			else          { setAnim("ball_l"); }
			if (!isOnPF(32,32)) remove(); 
		}
		public void hit(JGObject obj) {
			remove();
			obj.remove();
			score += 10;
			new Explo(x,y);
		}
		public void hit_bg(int tilecid,int tx,int ty,int txsize,int tysize) {
			if ((tilecid&2) != 0) {
				if (!floating) {
					snapToGrid(speed,speed);
				} else {
					snapToGrid(0,speed);
				}
				dir = -dir;
			}
			if ((tilecid&1) != 0 && floating) {
				snapToGrid(0,speed);
				dir = -dir;
			}
			if ((tilecid&8) != 0) {
				floating=true;
				setAnim("ball_water");
				int tlcid = getTileCid(tx,ty);
				int trcid = getTileCid(tx+txsize-1,ty);
				if (((tlcid|trcid)&8)!=0) { /* we're below the surface */
					y--;
				} else { /* we're near the surface */
					if (y > 0 && (int)y % tileHeight() > tileHeight()*0.7) {
						y--;
					} else {
						y+=2;
					}
				}
			}
		}
	}

	public class Crab extends JGObject {
		double speed=1.5 + 0.2*gamedifficulty;
		double dir=1;
		char orient='u';
		boolean was_falling=true;
		public Crab (double x,double y,int dir) { 
			super("crab",true, x,y, 2, "crab_u");
			this.dir=dir;
		}
		public void move() {
			//System.out.println(name+" "+dir+" "+orient);
			JGRectangle ts = getTiles();
			if (orient=='u') {
				if (isBottomAligned(speed*.5)) {
					int cid = getTileCid(ts.x, ts.y+1) 
					    | getTileCid(ts.x+ts.width-1, ts.y+1);
					if ((cid&3)==0) {
						/* no support */
						if (!was_falling) { // try to move along wall
							if (dir>0) {
								orient = 'r';
								snapBBoxToGrid(speed,speed,false,true);
								y += 2*speed;
							} else {
								orient = 'l';
								snapBBoxToGrid(speed,speed,true,true);
								y += 2*speed;
							}
						} else {
							y += speed;
						}
						was_falling=true;
					} else {
						/* move to dir */
						//snapBBoxToGrid(0,speed*2,false,true);
						x += dir*speed;
						//if (x >= pfWidth()-16 || x <= 0) dir = -dir;
						was_falling=false;
					}
				} else {
					/* fall until aligned */
					y += speed;
					was_falling=true;
				}
			} else if (orient=='r') {
				int cid = getTileCid(ts.x-1, ts.y) 
				    | getTileCid(ts.x-1, ts.y+ts.height-1);
				if ((cid&3)==0) {
					/* no support */
					if (!was_falling) { // try to move along wall
						if (dir>0) {
							orient = 'd';
							snapBBoxToGrid(speed,speed,false,false);
							x -= speed*2;
						} else {
							orient = 'u';
							snapBBoxToGrid(speed,speed,false,true);
							x -= speed*2;
						}
					} else {
						orient = 'u';
						y += speed;
					}
					was_falling=true;
				} else {
					/* move to dir */
					//snapBBoxToGrid(speed*.5,0,false,false);
					y += dir*speed;
					if (y >= pfHeight()-16 || y <= 0) dir = -dir;
					was_falling=false;
				}
			} else if (orient=='l') {
				int cid = getTileCid(ts.x+1, ts.y) 
				    | getTileCid(ts.x+1, ts.y+ts.height-1);
				if ((cid&3)==0) {
					/* no support */
					if (!was_falling) { // try to move along wall
						if (dir>0) {
							orient = 'u';
							snapBBoxToGrid(speed,speed,true,true);
							x += speed*2;
						} else {
							orient = 'd';
							snapBBoxToGrid(speed,speed,true,false);
							x += speed*2;
						}
					} else {
						orient = 'u';
						y += speed;
					}
					was_falling=true;
				} else {
					/* move to dir */
					//snapBBoxToGrid(speed*.5,0,false,false);
					y -= dir*speed;
					if (y >= pfHeight()-16 || y <= 0) dir = -dir;
					was_falling=false;
				}
			} else if (orient=='d') {
				int cid = getTileCid(ts.x, ts.y-1) 
				    | getTileCid(ts.x+ts.width-1, ts.y-1);
				if ((cid&3)==0) {
					/* no support */
					if (!was_falling) {
						if (dir>0) {
							orient = 'l';
							snapBBoxToGrid(speed,speed,true,false);
							y -= speed*2;
						} else {
							orient = 'r';
							snapBBoxToGrid(speed,speed,false,false);
							y -= speed*2;
						}
						//snapBBoxToGrid(speed*.5,0,false,false);
					} else {
						orient = 'u';
						y += speed;
					}
					was_falling=true;
				} else {
					/* move to dir */
					//snapBBoxToGrid(0,speed*.5,false,false);
					x -= dir*speed;
					//if (x >= pfWidth()-16 || x <= 0) dir = -dir;
					was_falling=false;
				}
			}
			setAnim("crab_"+orient);
			if (!isOnPF((int)-speed,(int)-speed)) remove(); 
		}
		public void hit(JGObject obj) {
			remove();
			obj.remove();
			score += 5;
			new Explo(x,y);
		}
		public void hit_bg(int tilecid,int tx,int ty,int txsize,int tysize) {
			if ((tilecid&3) != 0) {
				// snap in case an object was in free fall
				// not when it bumps into a wall while walking
				snapBBoxToGrid(0,speed,false,true);
				dir = -dir;
			}
		}
	}

	public class Bird extends JGObject {
		double speed=3+0.4*gamedifficulty;
		double dir;
		boolean was_falling=false;
		public Bird (double x,double y,int dir) { 
			super("bird",true, x,y, 2, null,0,0,32,32);
			setAnim("vogel_l");
			if (dir >= 0) setAnim("vogel_r");
			this.dir=dir;
		}
		public void move() {
			x += speed*dir;
			if (!isOnPF(32,32)) remove(); 
		}
		public void hit(JGObject obj) {
			remove();
			obj.remove();
			score += 10;
			new Explo(x,y);
		}
		public void hit_bg(int tilecid,int tx,int ty,int txsize,int tysize) {
			if ((tilecid&3) != 0) {
				snapToGrid(speed,speed);
				dir = -dir;
				setAnim("vogel_l");
				if (dir >= 0) setAnim("vogel_r");
			}
			if ((tilecid&8) != 0) {
				remove();
				new Explo(x,y);
			}
		}
	}

	public class Fish extends JGObject {
		double speed=1.7+0.3*gamedifficulty;
		int dir;
		double ydir=0;
		boolean was_falling=false;
		int crabtimer=24, crabmax=32-2*gamedifficulty;
		public Fish (double x,double y,int dir) { 
			super("fish",true, x,y, 2, "fish_r",0,0,32,32);
			if (dir < 0) setImage("fish_l");
			this.dir=dir;
		}
		public void move() {
			if (isXAligned(speed)) {
				JGPoint ct = getCenterTile();
				int front1cid = getTileCid(ct, dir, 0);
				int front2cid = getTileCid(ct, dir*2, 0);
				int upcid   = getTileCid(ct, dir*2, -1)
							& getTileCid(ct, dir, -1)
							& getTileCid(ct, 0, -1);
				int downcid = getTileCid(ct, dir*2, 1)
							& getTileCid(ct, dir, 1)
							& getTileCid(ct, 0, 1);
				/* move up or down is there is an obstacle ahead, but not right
				* in front of us */
				if ( and(front1cid,8) && !and(front2cid,8)) {
					/* see if there's enough room upwards or downwards */
					if (and(upcid,8)) {
						ydir = -1;
					} else if (and(downcid,8)) {
						ydir=1;
					}
				}
			}
			y += ydir*speed*2;
			if (isYAligned(speed)) {
				ydir = 0.0;
				snapToGrid(0,speed);
			}
			x += speed*dir;
			if (!isOnPF(32,32)) remove();
			if ((crabtimer--) <= 0 && x >= 0 && x < pfWidth()-32) {
				if (countObjects("crab",0) < 20)
					new Crab(x+8,y+7, (int)random(-1,1,2));
				crabtimer=crabmax;
			}
		}
		public void hit(JGObject obj) {
			remove();
			obj.remove();
			score += 25;
			new Explo(x,y);
		}
		public void hit_bg(int tilecid,int tx,int ty,int txsize,int tysize) {
			if ((tilecid&3) != 0) {
				snapToGrid(speed,speed);
				dir = -dir;
				setImage("fish_r");
				if (dir < 0) setImage("fish_l");
			}
		}
	}
}
