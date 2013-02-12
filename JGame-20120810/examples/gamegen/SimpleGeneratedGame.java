package examples.gamegen;
import jgame.*;
import jgame.platform.*;
import examples.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/** The class that goes with the fillgameform.php form.  The form is converted
 * by php to an AppConfig file, which is read into this object. */
public class SimpleGeneratedGame extends StdGame {
	public static void main(String [] args) {
		new SimpleGeneratedGame(parseSizeArgs(args,0));
	}
	public SimpleGeneratedGame() { initEngineApplet(); }
	public SimpleGeneratedGame(JGPoint size) {
		initEngine(size.x,size.y);
	}
	public void initCanvas() { setCanvasSettings(40,30,16,16,null,null,null); }
	static final int PLAYERTYPE=32;
	static final int BULLETTYPE=16;
	AppConfig gamecfg;
	public void initGame() {
		String cfgname=null;
		if (isApplet()) {
			cfgname = getParameter("configfile");
		} else {
			JFileChooser chooser = new JFileChooser();
			chooser.setCurrentDirectory(new File(
				System.getProperty("user.dir")));
			chooser.setDialogTitle("Choose a config file");
			int returnVal = chooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				cfgname = chooser.getSelectedFile().getAbsolutePath();
			} else {
				System.exit(0);
			}
			// XXX read this as resource!
			//cfgname = "mygeneratedgame.appconfig";
		}
		gamecfg = new AppConfig("Game parameters",this,cfgname);
		gamecfg.loadFromFile();
		gamecfg.defineFields("gp_","","","");
		gamecfg.saveToObject();
		initMotionPars();
		// unpause and copy settingswhen config window is closed
		gamecfg.setListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				start();
				requestGameFocus();
				initMotionPars();
			}
		} );
		defineMedia("simplegeneratedgame.tbl");
		setFrameRate(35,1);
	}
	public void initNewLife() {
		player = new Player();
	}
	public void startGameOver() {
		removeObjects(null,0);
	}
	public void incrementLevel() {
		level++;
		stage++;
	}
	JGFont scoring_font = new JGFont("Arial",0,16);

	public void doFrameTitle() {
		if (getKey('G')) {
			clearKey('G');
			gamecfg.openGui();
			// pause until window closed
			stop();
		}
	}

	public void paintFrameTitle() {
		drawString(gp_gamename,
			pfWidth()/2,pfHeight()/3,0,title_font,title_color);
		drawString("Press "+getKeyDesc(key_startgame)+" to start",
			pfWidth()/2,6*pfHeight()/10,0,title_font,title_color);
		drawString("Press "+getKeyDesc(key_gamesettings)+" for settings",
			pfWidth()/2,7*pfHeight()/10,0,title_font,title_color);
	}

	/* game configuration */

	public String gp_gamename="MyGame";

	Player player=null;

	public void defineLevel() {
		// base BG
		fillBG(gp_basebgfill);
		if (gp_basebg.equals("empty")) {
		} else if (gp_basebg.equals("border")) {
			drawBGBorder(gp_basebgtile,gp_basebgtype);
		} else if (gp_basebg.equals("wallhoriz")) {
			drawBGBorder(gp_basebgtile,gp_basebgtype);
			for (int x=6; x<pfTilesX()-6; x++) {
				setTile(x,pfTilesY()/2,gp_basebgtile);
				setTileCid(x,pfTilesY()/2,gp_basebgtype);
			}
		} else if (gp_basebg.equals("wallvert")) {
			drawBGBorder(gp_basebgtile,gp_basebgtype);
			for (int y=6; y<pfTilesY()-6; y++) {
				setTile(pfTilesX()/3, y, gp_basebgtile);
				setTileCid(pfTilesX()/3, y, gp_basebgtype);
				setTile(2*pfTilesX()/3, y, gp_basebgtile);
				setTileCid(2*pfTilesX()/3, y, gp_basebgtype);
			}
		} else if (gp_basebg.equals("maze")) {
			drawBGBorder(gp_basebgtile,gp_basebgtype);
			double xstep=pfTilesX()/gp_basebgmazexsize;
			double ystep=pfTilesY()/gp_basebgmazeysize;
			for (double x=0; x<pfTilesX()-1.5; x+=xstep) {
				for (double y=0; y<pfTilesY()-1.5; y+=ystep) {
					drawBGCross(x,y,gp_basebgtile,gp_basebgtype);
				}
			}
			for (double x=0; x<pfTilesX()+xstep; x+=xstep)
				drawBGCross(x,pfTilesY()-1,gp_basebgtile,gp_basebgtype);
			for (double y=0; y<pfTilesY()+ystep; y+=ystep)
				drawBGCross(pfTilesX()-1,y,gp_basebgtile,gp_basebgtype);
			drawBGCross(pfTilesX()-1,pfTilesY()-1,gp_basebgtile,gp_basebgtype);
		}
		// extra BG
		if (gp_randombg1) 
			drawRandomBG(gp_randombg1density,gp_randombg1type,gp_randombg1tile);
		if (gp_randombg2) 
			drawRandomBG(gp_randombg2density,gp_randombg2type,gp_randombg2tile);
		if (gp_randombg3) 
			drawRandomBG(gp_randombg3density,gp_randombg3type,gp_randombg3tile);
		// clear objects
		removeObjects(null,0);
		// set player
		player = new Player();
		// clear area around player
		JGRectangle clr_t = getTiles(new JGRectangle(
			(int)player.x-16, (int)player.y-16, 48, 48));
		for (int x=0; x<clr_t.width; x++) {
			for (int y=0; y<clr_t.height; y++) {
				setTile(clr_t.x+x, clr_t.y+y, gp_basebgfill);
			}
		}
		// place agents
		if (gp_agent1)
			for (int i=0; i<gp_agent1createinit; i++) {
				newAgent(1);
			}
		if (gp_agent2)
			for (int i=0; i<gp_agent2createinit; i++) {
				newAgent(2);
			}
		if (gp_agent3)
			for (int i=0; i<gp_agent3createinit; i++) {
				newAgent(3);
			}
		if (gp_agent4)
			for (int i=0; i<gp_agent4createinit; i++) {
				newAgent(4);
			}
		if (gp_agent5)
			for (int i=0; i<gp_agent5createinit; i++) {
				newAgent(5);
			}
	}
	void drawRandomBG(double density,int type,String tile) {
		int nrtiles = (int)((pfTilesX()*pfTilesY()*density) / 100.0);
		for (int i=0; i<nrtiles; i++) {
			int xpos,ypos;
			int nrtries=20;
			do {
				xpos = random(0,pfTilesX(),1);
				ypos = random(0,pfTilesY(),1);
				// abort if we had to try 20 times
				if (--nrtries <= 0) return;
			} while (getTileCid(xpos,ypos)!=0
			||       getTileCid(xpos-1,ypos-1)!=0
			||       getTileCid(xpos+1,ypos-1)!=0
			||       getTileCid(xpos-1,ypos+1)!=0
			||       getTileCid(xpos+1,ypos+1)!=0);
			setTile(xpos,ypos,tile);
			setTileCid(xpos,ypos,type);
		}
	}
	void drawBGBorder(String tile,int type) {
		for (int x=0; x<pfTilesX(); x++) {
			setTile(x,0,tile);
			setTileCid(x,0,type);
			setTile(x,pfTilesY()-1,tile);
			setTileCid(x,pfTilesY()-1,type);
		}
		for (int y=0; y<pfTilesY(); y++) {
			setTile(0,y,tile);
			setTileCid(0,y,type);
			setTile(pfTilesX()-1,y,tile);
			setTileCid(pfTilesX()-1,y,type);
		}
	}
	public void drawBGCross(double xd,double yd,String tile,int type) {
		int x = (int)xd;
		int y = (int)yd;
		setTile(x,y,tile);
		setTileCid(x,y,type);
		setTile(x-1,y,tile);
		setTileCid(x-1,y,type);
		setTile(x+1,y,tile);
		setTileCid(x+1,y,type);
		setTile(x,y-1,tile);
		setTileCid(x,y-1,type);
		setTile(x,y+1,tile);
		setTileCid(x,y+1,type);
	}
	boolean checkForTiles(double xpos,double ypos) {
		return checkBGCollision(new JGRectangle(
			(int)xpos,(int)ypos,(int)16,(int)16) )!=0;
	}
	public String gp_basebg="empty"; /* empty, border, maze */
	public double gp_basebgmazeysize=1;
	public double gp_basebgmazexsize=5;
	public int gp_basebgtype=0;
	public String gp_basebgtile="w";
	public String gp_basebgfill="k";

	public boolean gp_randombg1=false;
	public double gp_randombg1density=0;
	public int gp_randombg1type=0;
	public String gp_randombg1tile="w";

	public boolean gp_randombg2=false;
	public double gp_randombg2density=0;
	public int gp_randombg2type=0;
	public String gp_randombg2tile="w";

	public boolean gp_randombg3=false;
	public double gp_randombg3density=0;
	public int gp_randombg3type=0;
	public String gp_randombg3tile="w";

	public class Player extends JGObject {
		int invulnerability=35*3;
		double bulxdir=0,bulydir=0;
		public Player() {
			super("player",false,0,0,PLAYERTYPE,null);
				setPos(gp_playerx,gp_playery);
				setImage(gp_playersprite);
		}
		public void move() {
			// invulnerability
			if (invulnerability > 0) {
				invulnerability--;
				if ( (invulnerability&3) >= 2) {
					setImage(null);
				} else {
					setImage(gp_playersprite);
				}
			}
			// move
			if (gp_playerxmove.equals("linear")) {
				if (getKey(key_left))  x -= gp_playerxspeed;
				if (getKey(key_right)) x += gp_playerxspeed;
			}else if (gp_playerxmove.equals("accel")) {
				if (getKey(key_left))  xspeed -= gp_playerxaccel;
				if (getKey(key_right)) xspeed += gp_playerxaccel;
			}
			if (gp_playerymove.equals("linear")) {
				if (getKey(key_up))   y -= gp_playeryspeed;
				if (getKey(key_down)) y += gp_playeryspeed;
			}else if (gp_playerymove.equals("accel")) {
				if (getKey(key_up))   yspeed -= gp_playeryaccel;
				if (getKey(key_down)) yspeed += gp_playeryaccel;
			}
			if (x<0) x=0;
			if (x>pfWidth()-16) x = pfWidth()-16;
			if (y<0) y=0;
			if (y>pfHeight()-16) y = pfHeight()-16;
			// the rest of the method is shoot code.
			// check direction for 8-directional shooting
			double newbulxdir = x + xspeed - getLastX();
			double newbulydir = y + yspeed - getLastY();
			if (newbulxdir!=0 || newbulydir!=0) {
				bulxdir = newbulxdir;
				bulydir = newbulydir;
			}
			// check if we may shoot first
			if (countObjects(null,BULLETTYPE) > gp_playermaxbullets-0.01)
				return;
			if (gp_playershoot.equals("1dir")) {
				if (getKey(key_fire)) {
					if (gp_playershootdir.equals("left"))
						new PlayerBullet(x,y,-gp_playerbulletspeed,0);
					if (gp_playershootdir.equals("right"))
						new PlayerBullet(x,y,gp_playerbulletspeed,0);
					if (gp_playershootdir.equals("up"))
						new PlayerBullet(x,y,0,-gp_playerbulletspeed);
					if (gp_playershootdir.equals("down"))
						new PlayerBullet(x,y,0,gp_playerbulletspeed);
				}
			} else if (gp_playershoot.equals("2dir")) {
				if (gp_playershootaxis.equals("horizontal")) {
					if (getKey(key_fireleft))
						new PlayerBullet(x,y,-gp_playerbulletspeed,0);
					if (getKey(key_fireright))
						new PlayerBullet(x,y,gp_playerbulletspeed,0);
				}
				if (gp_playershootaxis.equals("vertical")) {
					if (getKey(key_fireup))
						new PlayerBullet(x,y,0,-gp_playerbulletspeed);
					if (getKey(key_firedown))
						new PlayerBullet(x,y,0,gp_playerbulletspeed);
				}
			} else if (gp_playershoot.equals("4dir")) {
				if (getKey(key_fireleft))
					new PlayerBullet(x,y,-gp_playerbulletspeed,0);
				if (getKey(key_fireright))
					new PlayerBullet(x,y,gp_playerbulletspeed,0);
				if (getKey(key_fireup))
					new PlayerBullet(x,y,0,-gp_playerbulletspeed);
				if (getKey(key_firedown))
					new PlayerBullet(x,y,0,gp_playerbulletspeed);
			} else if (gp_playershoot.equals("alldir")) {
				if (getKey(key_fire)) {
					if (bulxdir!=0 || bulydir!=0)
						new PlayerBullet(x,y, bulxdir*gp_playerbulletspeed,
							bulydir*gp_playerbulletspeed);
				}
			}
			clearKey(key_fire);
			clearKey(key_fireup);
			clearKey(key_firedown);
			clearKey(key_fireleft);
			clearKey(key_fireright);
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,gp_playerdieobjtype) && invulnerability==0)
				lifeLost();
			if (and(obj.colid,gp_playergetobjtype)) {
				obj.remove();
				score += gp_playergetscore;
				new StdScoring("pts",obj.x,obj.y,0,-1.0,40,
					(int)gp_playergetscore+" pts",scoring_font,
					new JGColor [] { JGColor.red,JGColor.yellow },2);
			}
		}
		public void hit_bg(int tilecid) {
			if (and(tilecid,gp_playerbumpbgtype)) {
				x = getLastX();
				y = getLastY();
				snapToGrid(8,8);
			}
			if (and(tilecid,gp_playerdiebgtype) && invulnerability==0) {
				lifeLost();
			}
		}
	}
	public class PlayerBullet extends JGObject {
		public PlayerBullet(double x,double y,double xspeed,double yspeed) {
			super("playerbullet",true,x+4,y+4,BULLETTYPE,
				null,xspeed,yspeed,-2);
			setImage(gp_playersprite+"bul");
		}
		public void hit_bg(int tilecid) {
			if (and(tilecid,gp_playerbulletdiebgtype)) remove();
		}
	}

	public String gp_playersprite="white16";
	public double gp_playerx=312;
	public double gp_playery=232;
	public String gp_playerxmove="linear"; /* off, linear, accel */
	public double gp_playerxspeed=8;
	public double gp_playerxaccel=1;
	public String gp_playerymove="linear"; /* off, linear, accel */
	public double gp_playeryspeed=8;
	public double gp_playeryaccel=1;
	public int gp_playerbumpbgtype=0;
	public int gp_playerdiebgtype=0;
	public int gp_playerdieobjtype=0;
	public int gp_playergetobjtype=0;
	public double gp_playergetscore=10;
	public String gp_playershoot="off"; /* off, 1dir, 2dir, 4dir, alldir */
	public String gp_playershootdir="left"; /* left right up down */
	public String gp_playershootaxis="horizontal"; /* horizontal vertical */
	public double gp_playerbulletspeed=10;
	public double gp_playermaxbullets=1;
	public int gp_playerbulletdiebgtype=0;

	public void doFrameInGame() {
		moveObjects();
		checkCollision(1+2+4+8,BULLETTYPE+PLAYERTYPE);
		checkBGCollision(1+2+4+8,BULLETTYPE+PLAYERTYPE);
		checkCollision(BULLETTYPE,1+2+4+8);
		checkBGCollision(1+2+4+8,1+2+4+8); // bg hits agents
		// create agents
		if (gp_agent1 && gp_agent1createinterval>0.0
		&& checkTime((int)(35*gp_agent1createbegin),
		(int)(35*gp_agent1createend),
		(int)(35*gp_agent1createinterval)))
			newAgent(1);
		if (gp_agent2 && gp_agent2createinterval>0.0
		&& checkTime((int)(35*gp_agent2createbegin),
		(int)(35*gp_agent2createend),
		(int)(35*gp_agent2createinterval)))
			newAgent(2);
		if (gp_agent3 && gp_agent3createinterval>0.0
		&& checkTime((int)(35*gp_agent3createbegin),
		(int)(35*gp_agent3createend),
		(int)(35*gp_agent3createinterval)))
			newAgent(3);
		if (gp_agent4 && gp_agent4createinterval>0.0
		&& checkTime((int)(35*gp_agent4createbegin),
		(int)(35*gp_agent4createend),
		(int)(35*gp_agent4createinterval)))
			newAgent(4);
		if (gp_agent5 && gp_agent5createinterval>0.0
		&& checkTime((int)(35*gp_agent5createbegin),
		(int)(35*gp_agent5createend),
		(int)(35*gp_agent5createinterval)))
			newAgent(5);
		// check level end
		if ((gp_endleveltimeout  && gametime >= 35*gp_endleveltimeoutlen)
		||  (gp_endlevelobjcount && countObjects(null,gp_endlevelobjtype)==0)) {
			score += (int)gp_endlevelscore;
			levelDone();
		}
	}

	public boolean gp_endleveltimeout=false;
	public double gp_endleveltimeoutlen=50;
	public boolean gp_endlevelobjcount=false;
	public int gp_endlevelobjtype=0;
	public double gp_endlevelscore=100;

	class Agent extends JGObject {
		int objtimer=0;
		int    type;
		String sprite;
		String bouncesides;
		int    dieobjtype;
		int    blockbgtype,diebgtype;
		String dieaction;
		boolean shoot;
		double  shootfreq;
		String  shootdir;
		double  shootspeed;
		int     bullettype;
		double  diescore;
		AgentMotion xmot,ymot;
		public Agent(int type,String sprite,
		String bouncesides, String createloc,int dieobjtype,
		int blockbgtype, int diebgtype, String dieaction,
		boolean shoot, double shootfreq, String shootdir,
		double shootspeed, int bullettype,
		double diescore,
		AgentMotion xmot, AgentMotion ymot) {
		/* left right up down player */
			super("agent",true,100,100,type,sprite);
			this.type       =type;
			this.sprite     =sprite;
			this.bouncesides=bouncesides;
			this.dieobjtype =dieobjtype;
			this.blockbgtype=blockbgtype;
			this.diebgtype  =diebgtype;
			this.dieaction  =dieaction;
			this.shoot      =shoot;
			this.shootfreq  =shootfreq;
			this.shootdir   =shootdir;
			this.shootspeed =shootspeed;
			this.bullettype =bullettype;
			this.diescore   =diescore;
			int dirs=0;
			int tries=12;
			while (true) {
				if (createloc.equals("random")) {
					setPos(random(0,pfWidth(),1),random(0,pfHeight(),1));
				} else if (createloc.equals("corner")) {
					dirs|=15;
				} else if (createloc.equals("top")) {
					dirs|=1;
				} else if (createloc.equals("bottom")) {
					dirs|=2;
				} else if (createloc.equals("topbottom")) {
					dirs|=3;
				} else if (createloc.equals("left")) {
					dirs|=4;
				} else if (createloc.equals("right")) {
					dirs|=8;
				} else if (createloc.equals("leftright")) {
					dirs|=12;
				}
				if (dirs!=0) {
					int dir;
					do {
						dir = 1<<(random(0,3,1));
					} while ( (dirs&dir) == 0);
					if (dir==1) y = -16;
					if (dir==2) y = pfHeight();
					if (dir==1 || dir==2) x = random(0,pfWidth()-16);
					if (dir==4) x = -16;
					if (dir==8) x = pfWidth();
					if (dir==4 || dir==8) y = random(0,pfHeight()-16);
				}
				if (tries-- <= 0) break;
				if (checkForTiles(x,y)) continue;
				if (player==null) break;
				// don't start in the same row or column as player
				if ( (x > player.x-16 && x < player.x+32)
				||   (y > player.y-16 && y < player.y+32) ) continue;
				// don't start near player
				if ( (x > player.x-48 && x < player.x+64)
				&&   (y > player.y-48 && y < player.y+64) ) continue;
				break;
			}
			this.xmot = xmot;
			this.ymot = ymot;
			if (xmot.init.equals("center")
			||  (xmot.init.equals("player") && player==null) ) {
				xspeed = ((x > pfWidth()/2) ? -1 : 1) * xmot.speed;
			} else if (xmot.init.equals("player")) {
				xspeed = ((x > player.x) ? -1 : 1) * xmot.speed;
			} else if (xmot.init.equals("random")) {
				xspeed = xmot.speed*random(-1,1,2);
			} else if (xmot.init.equals("not")) {
			}
			if (ymot.init.equals("center")
			||  (ymot.init.equals("player") && player==null) ) {
				yspeed = ((y > pfHeight()/2) ? -1 : 1) * ymot.speed;
			} else if (ymot.init.equals("player")) {
				yspeed = ((y > player.y) ? -1 : 1) * ymot.speed;
			} else if (ymot.init.equals("random")) {
				yspeed = ymot.speed*random(-1,1,2);
			} else if (ymot.init.equals("not")) {
			}
		}
		public void move() {
			objtimer++;
			// move
			if (xmot.random && objtimer%(int)(35*xmot.randomchg)==0) {
				xspeed = xmot.speed*random(-1,1,2);
			}
			if (ymot.random && objtimer%(int)(35*ymot.randomchg)==0) {
				yspeed = ymot.speed*random(-1,1,2);
			}
			if (player!=null) {
				double playerdist = Math.sqrt(
					(player.x-x)*(player.x-x) + (player.y-y)*(player.y-y) )
					* 100.0 / (pfWidth());
				if (xmot.toplayer
				&& playerdist>xmot.toplayermin && playerdist<xmot.toplayermax) {
					xspeed = 0;
					x += xmot.speed * (player.x>x ? 1 : -1);
				} else if (xmot.frplayer
				&& playerdist>xmot.frplayermin && playerdist<xmot.frplayermax) {
					xspeed = 0;
					x += xmot.speed * (player.x<x ? 1 : -1);
				}
				if (ymot.toplayer
				&& playerdist>ymot.toplayermin && playerdist<ymot.toplayermax) {
					yspeed = 0;
					y += ymot.speed * (player.y>y ? 1 : -1);
				} else if (ymot.frplayer
				&& playerdist>ymot.frplayermin && playerdist<ymot.frplayermax) {
					yspeed = 0;
					y += ymot.speed * (player.y<y ? 1 : -1);
				}
			}
			// react to background
			int bounces=0;
			if (bouncesides.equals("any")) {
				bounces|=15;
			} else if (bouncesides.equals("top")) {
				bounces|=1;
			} else if (bouncesides.equals("bottom")) {
				bounces|=2;
			} else if (bouncesides.equals("topbottom")) {
				bounces|=3;
			} else if (bouncesides.equals("left")) {
				bounces|=4;
			} else if (bouncesides.equals("right")) {
				bounces|=8;
			} else if (bouncesides.equals("leftright")) {
				bounces|=12;
			}
			if ((bounces&1)!=0 && y < 0) {
				y = 0;
				if (yspeed<0) yspeed = -yspeed;
			}
			if ((bounces&2)!=0 && y > pfHeight()-16) {
				y = pfHeight()-16;
				if (yspeed>0) yspeed = -yspeed;
			}
			if ((bounces&4)!=0 && x < 0) {
				x = 0;
				if (xspeed<0) xspeed = -xspeed;
			}
			if ((bounces&8)!=0 && x > pfWidth()-16) {
				x = pfWidth()-16;
				if (xspeed>0) xspeed = -xspeed;
			}
			// shoot
			if (shoot && shootfreq > 0.0 && objtimer % (int)(shootfreq*35)==0) {
				if (shootdir.equals("left")) {
					new AgentBullet(x,y,bullettype,sprite,
						diebgtype|blockbgtype,-shootspeed,0);
				} else if (shootdir.equals("right")) {
					new AgentBullet(x,y,bullettype,sprite,
						diebgtype|blockbgtype,shootspeed,0);
				} else if (shootdir.equals("up")) {
					new AgentBullet(x,y,bullettype,sprite,
						diebgtype|blockbgtype,0,-shootspeed);
				} else if (shootdir.equals("down")) {
					new AgentBullet(x,y,bullettype,sprite,
						diebgtype|blockbgtype,0,shootspeed);
				} else if (shootdir.equals("player") && player!=null) {
					double angle = Math.atan2(player.x-x,player.y-y);
					new AgentBullet(x,y,bullettype,sprite,
						diebgtype|blockbgtype,
						shootspeed*Math.sin(angle),shootspeed*Math.cos(angle));
				}
				
			}
		}
		public void hit(JGObject obj) {
			if (and(obj.colid,dieobjtype)) {
				die();
				if ((obj.colid&BULLETTYPE)!=0) obj.remove();
			}
		}
		public void hit_bg(int tilecid) {
			if (and(tilecid,diebgtype)) {
				die();
			}
			if (and(tilecid,blockbgtype)) {
				x = getLastX();
				y = getLastY();
				//snapToGrid(xmot.speed,ymot.speed);
				// XXX insert nice bouncing algorithm here
				xspeed = -xspeed;
				yspeed = -yspeed;
			}
		}
		public void die() {
			score += (int)diescore;
			remove();
			if (dieaction.equals("die")) {
			} else if (dieaction.equals("create1")) {
				Agent agt=newAgent(1);
				agt.setPos(x,y);
			} else if (dieaction.equals("create2")) {
				Agent agt=newAgent(2);
				agt.setPos(x,y);
			} else if (dieaction.equals("create3")) {
				Agent agt=newAgent(3);
				agt.setPos(x,y);
			} else if (dieaction.equals("create4")) {
				Agent agt=newAgent(4);
				agt.setPos(x,y);
			} else if (dieaction.equals("create5")) {
				Agent agt=newAgent(5);
				agt.setPos(x,y);
			}
		}
	}
	public class AgentBullet extends JGObject {
		int diebgtype;
		public AgentBullet(double x,double y,int type,String basesprite,
		int diebgtype,double xspeed,double yspeed) {
			super("agentbullet",true,x+4,y+4,type,
				basesprite+"bul",xspeed,yspeed,-2);
			this.diebgtype = diebgtype;
		}
		public void hit_bg(int tilecid) {
			if (and(tilecid,diebgtype)) remove();
		}
	}

	// appconfig and our pho scripts do not handle structured parameters,
	// so we include a flat list of all agent parameters.
	// The code below handles this. It's butt ugly but it works for now

	public Agent newAgent(int anr) {
		Agent agt;
		switch (anr) {
		case 1:
			agt = new Agent(gp_agent1type,gp_agent1sprite,
			gp_agent1bouncesides,gp_agent1createloc,gp_agent1dieobjtype,
			gp_agent1blockbgtype, gp_agent1diebgtype, gp_agent1dieaction,
			gp_agent1shoot, gp_agent1shootfreq, gp_agent1shootdir,
			gp_agent1shootspeed, gp_agent1bullettype,
			gp_agent1diescore,
			xmots[0],ymots[0]);
			return agt;
		case 2:
			agt = new Agent(gp_agent2type,gp_agent2sprite,
			gp_agent2bouncesides,gp_agent2createloc,gp_agent2dieobjtype,
			gp_agent2blockbgtype, gp_agent2diebgtype, gp_agent2dieaction,
			gp_agent2shoot, gp_agent2shootfreq, gp_agent2shootdir,
			gp_agent2shootspeed, gp_agent2bullettype,
			gp_agent2diescore,
			xmots[1],ymots[1]);
			return agt;
		case 3:
			agt = new Agent(gp_agent3type,gp_agent3sprite,
			gp_agent3bouncesides,gp_agent3createloc,gp_agent3dieobjtype,
			gp_agent3blockbgtype, gp_agent3diebgtype, gp_agent3dieaction,
			gp_agent3shoot, gp_agent3shootfreq, gp_agent3shootdir,
			gp_agent3shootspeed, gp_agent3bullettype,
			gp_agent3diescore,
			xmots[2],ymots[2]);
			return agt;
		case 4:
			agt = new Agent(gp_agent4type,gp_agent4sprite,
			gp_agent4bouncesides,gp_agent4createloc,gp_agent4dieobjtype,
			gp_agent4blockbgtype, gp_agent4diebgtype, gp_agent4dieaction,
			gp_agent4shoot, gp_agent4shootfreq, gp_agent4shootdir,
			gp_agent4shootspeed, gp_agent4bullettype,
			gp_agent4diescore,
			xmots[3],ymots[3]);
			return agt;
		case 5:
			agt = new Agent(gp_agent5type,gp_agent5sprite,
			gp_agent5bouncesides,gp_agent5createloc,gp_agent5dieobjtype,
			gp_agent5blockbgtype, gp_agent5diebgtype, gp_agent5dieaction,
			gp_agent5shoot, gp_agent5shootfreq, gp_agent5shootdir,
			gp_agent5shootspeed, gp_agent5bullettype,
			gp_agent5diescore,
			xmots[4],ymots[4]);
			return agt;
		}
		return null;
	}

	class AgentMotion {
		public double speed=5;
		public String init="center"; /* center player random */
		public boolean random=false;
		public double randomchg=1;
		public boolean toplayer=false;
		public double toplayermin=0;
		public double toplayermax=100;
		public boolean frplayer=false;
		public double frplayermin=0;
		public double frplayermax=100;
	
		public AgentMotion(double speed,String init,
		boolean random,double randomchg,
		boolean toplayer,double toplayermin,double toplayermax,
		boolean frplayer,double frplayermin,double frplayermax) {
			this.speed      =speed;
			this.init       =init;
			this.random     =random;
			this.randomchg  =randomchg;
			this.toplayer   =toplayer;
			this.toplayermin=toplayermin;
			this.toplayermax=toplayermax;
			this.frplayer   =frplayer;
			this.frplayermin=frplayermin;
			this.frplayermax=frplayermax;
		}
		
	}

	AgentMotion xmots[] = new AgentMotion[5],ymots[]=new AgentMotion[5];

	void initMotionPars() {
		xmots[0] = new AgentMotion(gp_agent1xspeed, gp_agent1movexinit,
		gp_agent1movexrandom,gp_agent1movexrandomchg,gp_agent1movextoplayer,
		gp_agent1movextoplayermin,gp_agent1movextoplayermax,gp_agent1movexfrplayer,
		gp_agent1movexfrplayermin,gp_agent1movexfrplayermax);
		ymots[0] = new AgentMotion(gp_agent1yspeed, gp_agent1moveyinit,
		gp_agent1moveyrandom,gp_agent1moveyrandomchg,gp_agent1moveytoplayer,
		gp_agent1moveytoplayermin,gp_agent1moveytoplayermax,gp_agent1moveyfrplayer,
		gp_agent1moveyfrplayermin,gp_agent1moveyfrplayermax);

		xmots[1] = new AgentMotion(gp_agent2xspeed, gp_agent2movexinit,
		gp_agent2movexrandom,gp_agent2movexrandomchg,gp_agent2movextoplayer,
		gp_agent2movextoplayermin,gp_agent2movextoplayermax,gp_agent2movexfrplayer,
		gp_agent2movexfrplayermin,gp_agent2movexfrplayermax);
		ymots[1] = new AgentMotion(gp_agent2yspeed, gp_agent2moveyinit,
		gp_agent2moveyrandom,gp_agent2moveyrandomchg,gp_agent2moveytoplayer,
		gp_agent2moveytoplayermin,gp_agent2moveytoplayermax,gp_agent2moveyfrplayer,
		gp_agent2moveyfrplayermin,gp_agent2moveyfrplayermax);

		xmots[2] = new AgentMotion(gp_agent3xspeed, gp_agent3movexinit,
		gp_agent3movexrandom,gp_agent3movexrandomchg,gp_agent3movextoplayer,
		gp_agent3movextoplayermin,gp_agent3movextoplayermax,gp_agent3movexfrplayer,
		gp_agent3movexfrplayermin,gp_agent3movexfrplayermax);
		ymots[2] = new AgentMotion(gp_agent3yspeed, gp_agent3moveyinit,
		gp_agent3moveyrandom,gp_agent3moveyrandomchg,gp_agent3moveytoplayer,
		gp_agent3moveytoplayermin,gp_agent3moveytoplayermax,gp_agent3moveyfrplayer,
		gp_agent3moveyfrplayermin,gp_agent3moveyfrplayermax);

		xmots[3] = new AgentMotion(gp_agent4xspeed, gp_agent4movexinit,
		gp_agent4movexrandom,gp_agent4movexrandomchg,gp_agent4movextoplayer,
		gp_agent4movextoplayermin,gp_agent4movextoplayermax,gp_agent4movexfrplayer,
		gp_agent4movexfrplayermin,gp_agent4movexfrplayermax);
		ymots[3] = new AgentMotion(gp_agent4yspeed, gp_agent4moveyinit,
		gp_agent4moveyrandom,gp_agent4moveyrandomchg,gp_agent4moveytoplayer,
		gp_agent4moveytoplayermin,gp_agent4moveytoplayermax,gp_agent4moveyfrplayer,
		gp_agent4moveyfrplayermin,gp_agent4moveyfrplayermax);

		xmots[4] = new AgentMotion(gp_agent5xspeed, gp_agent5movexinit,
		gp_agent5movexrandom,gp_agent5movexrandomchg,gp_agent5movextoplayer,
		gp_agent5movextoplayermin,gp_agent5movextoplayermax,gp_agent5movexfrplayer,
		gp_agent5movexfrplayermin,gp_agent5movexfrplayermax);
		ymots[4] = new AgentMotion(gp_agent5yspeed, gp_agent5moveyinit,
		gp_agent5moveyrandom,gp_agent5moveyrandomchg,gp_agent5moveytoplayer,
		gp_agent5moveytoplayermin,gp_agent5moveytoplayermax,gp_agent5moveyfrplayer,
		gp_agent5moveyfrplayermin,gp_agent5moveyfrplayermax);
	}

	public boolean gp_agent1=false;
	public int gp_agent1type=0;
	public String gp_agent1sprite="white16";
	public String gp_agent1createloc="random";
		/*random corner top bottom topbottom left right leftright */
	public double gp_agent1createinit=1;
	public double gp_agent1createinterval=0;
	public double gp_agent1createbegin=0;
	public double gp_agent1createend=999999;
	public double gp_agent1xspeed=5;
	public String gp_agent1movexinit="center"; /* center player random not */
	public boolean gp_agent1movexrandom=false;
	public double gp_agent1movexrandomchg=1;
	public boolean gp_agent1movextoplayer=false;
	public double gp_agent1movextoplayermin=0;
	public double gp_agent1movextoplayermax=200;
	public boolean gp_agent1movexfrplayer=false;
	public double gp_agent1movexfrplayermin=0;
	public double gp_agent1movexfrplayermax=200;
	public double gp_agent1yspeed=5;
	public String gp_agent1moveyinit="center"; /* center player random not */
	public boolean gp_agent1moveyrandom=false;
	public double gp_agent1moveyrandomchg=1;
	public boolean gp_agent1moveytoplayer=false;
	public double gp_agent1moveytoplayermin=0;
	public double gp_agent1moveytoplayermax=200;
	public boolean gp_agent1moveyfrplayer=false;
	public double gp_agent1moveyfrplayermin=0;
	public double gp_agent1moveyfrplayermax=200;
	public boolean gp_agent1shoot=false;
	public double gp_agent1shootfreq=5;
	public String gp_agent1shootdir="player"; /* left right up down player */
	public double gp_agent1shootspeed=10;
	public int gp_agent1bullettype=0;
	public String gp_agent1bouncesides="none";
	public int gp_agent1dieobjtype=0;
	public int gp_agent1blockbgtype=0;
	public int gp_agent1diebgtype=0;
	public String gp_agent1dieaction="not";
		/* not die create1 create2 create3 create4 create5 */
	public double gp_agent1diescore=5;

	public boolean gp_agent2=false;
	public int gp_agent2type=0;
	public String gp_agent2sprite="white16";
	public String gp_agent2createloc="random";
		/*random corner top bottom topbottom left right leftright */
	public double gp_agent2createinit=1;
	public double gp_agent2createinterval=0;
	public double gp_agent2createbegin=0;
	public double gp_agent2createend=999999;
	public double gp_agent2xspeed=5;
	public String gp_agent2movexinit="center"; /* center player random not */
	public boolean gp_agent2movexrandom=false;
	public double gp_agent2movexrandomchg=1;
	public boolean gp_agent2movextoplayer=false;
	public double gp_agent2movextoplayermin=0;
	public double gp_agent2movextoplayermax=200;
	public boolean gp_agent2movexfrplayer=false;
	public double gp_agent2movexfrplayermin=0;
	public double gp_agent2movexfrplayermax=200;
	public double gp_agent2yspeed=5;
	public String gp_agent2moveyinit="center"; /* center player random not */
	public boolean gp_agent2moveyrandom=false;
	public double gp_agent2moveyrandomchg=1;
	public boolean gp_agent2moveytoplayer=false;
	public double gp_agent2moveytoplayermin=0;
	public double gp_agent2moveytoplayermax=200;
	public boolean gp_agent2moveyfrplayer=false;
	public double gp_agent2moveyfrplayermin=0;
	public double gp_agent2moveyfrplayermax=200;
	public boolean gp_agent2shoot=false;
	public double gp_agent2shootfreq=5;
	public String gp_agent2shootdir="player"; /* left right up down player */
	public double gp_agent2shootspeed=10;
	public int gp_agent2bullettype=0;
	public String gp_agent2bouncesides="none";
	public int gp_agent2dieobjtype=0;
	public int gp_agent2blockbgtype=0;
	public int gp_agent2diebgtype=0;
	public String gp_agent2dieaction="not";
		/* not die create1 create2 create3 create4 create5 */
	public double gp_agent2diescore=5;

	public boolean gp_agent3=false;
	public int gp_agent3type=0;
	public String gp_agent3sprite="white16";
	public String gp_agent3createloc="random";
		/*random corner top bottom topbottom left right leftright */
	public double gp_agent3createinit=1;
	public double gp_agent3createinterval=0;
	public double gp_agent3createbegin=0;
	public double gp_agent3createend=999999;
	public double gp_agent3xspeed=5;
	public String gp_agent3movexinit="center"; /* center player random not */
	public boolean gp_agent3movexrandom=false;
	public double gp_agent3movexrandomchg=1;
	public boolean gp_agent3movextoplayer=false;
	public double gp_agent3movextoplayermin=0;
	public double gp_agent3movextoplayermax=200;
	public boolean gp_agent3movexfrplayer=false;
	public double gp_agent3movexfrplayermin=0;
	public double gp_agent3movexfrplayermax=200;
	public double gp_agent3yspeed=5;
	public String gp_agent3moveyinit="center"; /* center player random not */
	public boolean gp_agent3moveyrandom=false;
	public double gp_agent3moveyrandomchg=1;
	public boolean gp_agent3moveytoplayer=false;
	public double gp_agent3moveytoplayermin=0;
	public double gp_agent3moveytoplayermax=200;
	public boolean gp_agent3moveyfrplayer=false;
	public double gp_agent3moveyfrplayermin=0;
	public double gp_agent3moveyfrplayermax=200;
	public boolean gp_agent3shoot=false;
	public double gp_agent3shootfreq=5;
	public String gp_agent3shootdir="player"; /* left right up down player */
	public double gp_agent3shootspeed=10;
	public int gp_agent3bullettype=0;
	public String gp_agent3bouncesides="none";
	public int gp_agent3dieobjtype=0;
	public int gp_agent3blockbgtype=1;
	public int gp_agent3diebgtype=1;
	public String gp_agent3dieaction="not";
		/* not die create1 create2 create3 create4 create5 */
	public double gp_agent3diescore=5;

	public boolean gp_agent4=false;
	public int gp_agent4type=0;
	public String gp_agent4sprite="white16";
	public String gp_agent4createloc="random";
		/*random corner top bottom topbottom left right leftright */
	public double gp_agent4createinit=1;
	public double gp_agent4createinterval=0;
	public double gp_agent4createbegin=0;
	public double gp_agent4createend=999999;
	public double gp_agent4xspeed=5;
	public String gp_agent4movexinit="center"; /* center player random not */
	public boolean gp_agent4movexrandom=false;
	public double gp_agent4movexrandomchg=1;
	public boolean gp_agent4movextoplayer=false;
	public double gp_agent4movextoplayermin=0;
	public double gp_agent4movextoplayermax=200;
	public boolean gp_agent4movexfrplayer=false;
	public double gp_agent4movexfrplayermin=0;
	public double gp_agent4movexfrplayermax=200;
	public double gp_agent4yspeed=5;
	public String gp_agent4moveyinit="center"; /* center player random not */
	public boolean gp_agent4moveyrandom=false;
	public double gp_agent4moveyrandomchg=1;
	public boolean gp_agent4moveytoplayer=false;
	public double gp_agent4moveytoplayermin=0;
	public double gp_agent4moveytoplayermax=200;
	public boolean gp_agent4moveyfrplayer=false;
	public double gp_agent4moveyfrplayermin=0;
	public double gp_agent4moveyfrplayermax=200;
	public boolean gp_agent4shoot=false;
	public double gp_agent4shootfreq=5;
	public String gp_agent4shootdir="player"; /* left right up down player */
	public double gp_agent4shootspeed=10;
	public int gp_agent4bullettype=0;
	public String gp_agent4bouncesides="none";
	public int gp_agent4dieobjtype=0;
	public int gp_agent4blockbgtype=0;
	public int gp_agent4diebgtype=0;
	public String gp_agent4dieaction="not";
		/* not die create1 create2 create3 create4 create5 */
	public double gp_agent4diescore=5;

	public boolean gp_agent5=false;
	public int gp_agent5type=0;
	public String gp_agent5sprite="white16";
	public String gp_agent5createloc="random";
		/*random corner top bottom topbottom left right leftright */
	public double gp_agent5createinit=1;
	public double gp_agent5createinterval=0;
	public double gp_agent5createbegin=0;
	public double gp_agent5createend=999999;
	public double gp_agent5xspeed=5;
	public String gp_agent5movexinit="center"; /* center player random not */
	public boolean gp_agent5movexrandom=false;
	public double gp_agent5movexrandomchg=1;
	public boolean gp_agent5movextoplayer=false;
	public double gp_agent5movextoplayermin=0;
	public double gp_agent5movextoplayermax=200;
	public boolean gp_agent5movexfrplayer=false;
	public double gp_agent5movexfrplayermin=0;
	public double gp_agent5movexfrplayermax=200;
	public double gp_agent5yspeed=5;
	public String gp_agent5moveyinit="center"; /* center player random not */
	public boolean gp_agent5moveyrandom=false;
	public double gp_agent5moveyrandomchg=1;
	public boolean gp_agent5moveytoplayer=false;
	public double gp_agent5moveytoplayermin=0;
	public double gp_agent5moveytoplayermax=200;
	public boolean gp_agent5moveyfrplayer=false;
	public double gp_agent5moveyfrplayermin=0;
	public double gp_agent5moveyfrplayermax=200;
	public boolean gp_agent5shoot=false;
	public double gp_agent5shootfreq=5;
	public String gp_agent5shootdir="player"; /* left right up down player */
	public double gp_agent5shootspeed=10;
	public int gp_agent5bullettype=0;
	public String gp_agent5bouncesides="none";
	public int gp_agent5dieobjtype=0;
	public int gp_agent5blockbgtype=0;
	public int gp_agent5diebgtype=0;
	public String gp_agent5dieaction="not";
		/* not die create1 create2 create3 create4 create5 */
	public double gp_agent5diescore=5;

}
