package examples.billiardberzerk;


import org.jbox2d.common.*;
import org.jbox2d.collision.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import org.jbox2d.dynamics.joints.*;


import jgame.*;
import jgame.platform.*;

import examples.StdPhysicsObject;

public class Main extends StdGame implements ContactListener {
	public static void main(String[]args) {
		new Main(parseSizeArgs(args,0));}
	public Main() { initEngineApplet(); }
	public Main(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(30,40,16,16,null,null,null); }

	World world;

	int stars_this_hit=0;
	int nr_aliens_this_hit=0;
	int nr_aliens_this_frame=0;
	int nr_aliens_last_frame=0;

	public static final float PXMUL=20f;
	public static final int NRROT=80;
	
	public void initGame() {
		setMsgFont(new JGFont("Sans",0,23));
		defineMedia("media.tbl");
		setFrameRate(isMidlet()? 15 : 25,1);
		key_startgame = 0;
		if (!isAndroid() && !isOpenGL()) {
			for (int i=0; i<NRROT; i++) {
				defineImageRotated("crate"+i,"-",0,"crate",i*2.0*Math.PI/NRROT);
			}
			for (int i=0; i<NRROT; i++) {
				defineImageRotated("barrel"+i,"-",0,"barrel",
					i*2.0*Math.PI/(NRROT));
			}
		}
		startgame_ticks=45;
		startgame_ingame=true;
		leveldone_ingame=true;
		lifelost_ingame=true;
		lifelost_ticks=45;
		leveldone_ticks=50;
		gameover_ticks=45;
		status_font = new JGFont("Sans",0,23);
		title_font = new JGFont("Sans",0,23);
	}
	public void incrementLevel() {
		if (level<16) level++;
		stage++;
	}
	static final int r=1; // rotating wall with motor
	static final int R=2; // 2 rotating walls w/o motor
	static final int h=3; // horizontal wall
	static final int H=4; // 2 horizontal walls
	static final int V=5; // vertical wall
	static final int C=6; // bl / tr corner walls
	//                                          |   |  1 1 1 1 1 1 1
	//                               1 2 3 4 5 6|7 8|9 0 1 2 3 4 5 6
	int [] lv_fireballs = new int[] {3,2,1,0,3,2,1,0,2,1,0,1,1,0,0,0};
	int	[] lv_crates = new int[]    {0,0,2,3,0,0,1,3,0,0,3,1,0,0,3,2};
	int [] lv_barrels = new int[]   {0,2,0,2,3,0,2,0,0,3,0,3,0,6,2,0};
	int [] lv_wall = new int[]      {0,V,0,r,0,h,0,R,0,C,0,r,0,V,0,H};
	public void defineLevel() {
		resetHitCounts();
		// 1=avoid+die 2=avoid
		setTileSettings("", 1, 0);
		fillBG("");
		setBGImage("bg"+(stage%7));
		world = StdPhysicsObject.createWorld(-100f,-100f,200f,200f);
		world.setContactListener(this);
		StdPhysicsObject.world = world;
		StdPhysicsObject.PXMUL = PXMUL;

		// floor
		StdPhysicsObject.createRect(0,pfHeight()+400,  pfWidth(),400, 0.0f,0.0f);
		// left wall
		StdPhysicsObject.createRect(-400,0,  400,pfHeight()+400, 0.0f,0.0f);
		// ceiling
		StdPhysicsObject.createRect(0,-400,  pfWidth(),400, 0.0f,0.0f);
		// right wall
		StdPhysicsObject.createRect(pfWidth()+400,0,  400,pfHeight()+400, 0.0f,0.0f);
		removeObjects(null,0);
		new Player(32,32);
		fillTileCid(0,0,48,48, 2);
		int stage8  = 8 + (stage%8);
		int stagewall = 6 + ((stage-6)%10);
		int thestage = (int)Math.min(stage8,stage);
		int thewallstage = (int)Math.min(stagewall,stage);
		int nr_fireballs = lv_fireballs[thestage];
		int nr_crates = lv_crates[thestage];
		int nr_aliens = 4 + (isMidlet() ? level/3 : level/2);
		int nr_barrels = lv_barrels[thestage];
		int walltype = lv_wall[thewallstage];
		for (int i=0; i<nr_fireballs; i++) {
			new Fireball(random(50,200,1),random(50,200,1));
		}
		for (int i=0; i<nr_crates; i++) {
			new Crate(random(80,pfWidth()-50,1),
				random(80,pfHeight()-50,1) );
		}
		for (int i=0; i<nr_barrels; i++) {
			new Barrel(random(80,pfWidth()-50,1),
				random(80,pfHeight()-50,1) );
		}
		if (walltype==V) { // vertical line
			StdPhysicsObject o = new StdPhysicsObject("crate",0, 
				pfWidth()/2,pfHeight()/2,0.0,
				16,144, 0, 0,0);
			o.setAppearance(null,null,0,0,true,1,1);
			//o.setAppearance(new JGColor(230,150,30),null,0,0,true,1,1);
			fillTileCid(pfWidth()/2 - 16, pfWidth()/2 + 16,
				pfHeight()/2 - 144, pfHeight()/2 + 144, 1);
		}
		if (walltype==h) {
			// horizontal line. hard with balls, easy with crates
			StdPhysicsObject o = new StdPhysicsObject("wall",0, 
				pfWidth()/2,pfHeight()/2,0.0,
				128,16, 0, 0,0);
			o.setAppearance(null,null,0,0,true,1,1);
			fillTileCid(pfWidth()/2 - 128, pfWidth()/2 + 128,
				pfHeight()/2 - 16, pfHeight()/2 + 16, 1);
		}
		if (walltype==H) { // 2 horiz lines
			StdPhysicsObject o = new StdPhysicsObject("wall",0, 
				pfWidth()/2,pfHeight()/4,0.0,
				128,16, 0, 0,0);
			o.setAppearance(null,null,0,0,true,1,1);
			fillTileCid(pfWidth()/2 - 128, pfWidth()/2 + 128,
				pfHeight()/4 - 16, pfHeight()/4 + 16, 1);
			o = new StdPhysicsObject("wall",0, 
				pfWidth()/2,3*pfHeight()/4,0.0,
				128,16, 0, 0,0);
			o.setAppearance(null,null,0,0,true,1,1);
			fillTileCid(pfWidth()/2 - 128, pfWidth()/2 + 128,
				3*pfHeight()/4 - 16, 3*pfHeight()/4 + 16, 1);
		}
		if (walltype==r) { // rotating line
			StdPhysicsObject o = new StdPhysicsObject("wall",0, 
				pfWidth()/2,pfHeight()/2,0.0,
				144,16, 3.0, 0.15,0.8);
			o.createRevoluteJoint(null,pfWidth()/2,pfHeight()/2,
				true, 1, 6000);
			o.setAppearance(new JGColor(230,150,30),null,0,0,true,1,1);
			fillTileCid(pfWidth()/2 - 144, pfWidth()/2 + 144,
				pfHeight()/2 - 144, pfHeight()/2 + 144, 2);
		}
		if (walltype==R) { // two rotating lines
			StdPhysicsObject o = new StdPhysicsObject("wall",0, 
				pfWidth()/2,pfHeight()/4,0.0,
				96,16, 3.0, 0.15,0.8);
			o.createRevoluteJoint(null,pfWidth()/2,pfHeight()/4,
				false, 1, 6000);
			o.setAppearance(new JGColor(230,150,30),null,0,0,true,1,1);
			fillTileCid(pfWidth()/2 - 96, pfWidth()/2 + 96,
				pfHeight()/4 - 96, pfHeight()/4 + 96, 2);
			o = new StdPhysicsObject("wall",0, 
				pfWidth()/2, 3*pfHeight()/4,0.0,
				96,16, 3.0, 0.15,0.8);
			o.createRevoluteJoint(null,pfWidth()/2,3*pfHeight()/4,
				false, 1, 6000);
			o.setAppearance(new JGColor(230,150,30),null,0,0,true,1,1);
			fillTileCid(pfWidth()/2 - 96, pfWidth()/2 + 96,
				3*pfHeight()/4 - 96, 3*pfHeight()/4 + 96, 2);
		}
		if (walltype==C) { // 2 corners
			StdPhysicsObject.createRect(pfWidth()/2, pfHeight()/4,
				96,16, 0.0f, 0.0f);
			fillTileCid(pfWidth()/2 - 96, pfWidth()/2 + 96,
				pfHeight()/4 - 16, pfHeight()/4 + 16, 1);
			StdPhysicsObject.createRect(pfWidth()/2+96+16,
				pfHeight()/4+64+16,
				16,64, 0.0f, 0.0f);
			fillTileCid(pfWidth()/2 + 96, pfWidth()/2 + 96+16+16,
				pfHeight()/4+16, pfHeight()/4 + 16+64+64, 1);
			StdPhysicsObject.createRect(pfWidth()/2, 3*pfHeight()/4,
				96,16, 0.0f, 0.0f);
			fillTileCid(pfWidth()/2 - 96, pfWidth()/2 + 96,
				3*pfHeight()/4 - 16, 3*pfHeight()/4 + 16, 1);
			StdPhysicsObject.createRect(pfWidth()/2-96-16,
				3*pfHeight()/4-64-16,
				16,64, 0.0f, 0.0f);
			fillTileCid(pfWidth()/2 - 96-16-16, pfWidth()/2 - 96,
				3*pfHeight()/4-16-64-64, 3*pfHeight()/4 - 16, 1);
		}
		for (int i=0; i<nr_aliens; i++) {
			// check for tiles
			// 5 tries before giving up
			for (int t=0; t<5; t++) {
				int xpos = random(80,pfWidth()-50,1);
				int ypos = random(80,pfHeight()-50,1);
				JGRectangle tiler = getTiles(new JGRectangle(xpos,ypos, 36,36));
				if (and(getTileCid(tiler),3)) continue;
				new Alien(xpos,ypos, 0.5+0.1*level);
				break;
			}
		}
		if (stage>0 && (stage%2)==0) {
			int radius = 70+7*level;
			int cenx = random(32+radius,pfWidth()-64-radius,1);
			int ceny = random(32+radius,pfHeight()-64-radius,1);
			double ang = random(0,Math.PI*2);
			new Star(cenx + radius*Math.sin(ang),ceny + radius*Math.cos(ang));
			new Star(cenx - radius*Math.sin(ang),ceny - radius*Math.cos(ang));
		}
	}
	void fillTileCid(int x1,int x2,int y1,int y2, int cid) {
		JGRectangle tiles = getTiles(new JGRectangle(x1,y1,x2-x1,y2-y1));
		for (int y=tiles.y; y<tiles.y+tiles.height; y++) {
			for (int x=tiles.x; x<tiles.x+tiles.width; x++) {
				if (cid==1) {
					setTile(x,y,"#");
				} else {
					setTileCid(x,y,cid);
				}
			}
		}
	}
	public void initNewLife() {
		removeObjects("player",0);
		new Player(32,32);
	}
	public void doFrameInGame() {
		nr_aliens_last_frame = nr_aliens_this_frame;
		nr_aliens_this_frame=0;
		//if (getKey('L')) levelDone();
		//world.setGravity(new Vec2(
		//	0.01f*(float)(-viewWidth()/2  + getMouseX()),
		//	0.01f*(float)(-viewHeight()/2 + getMouseY()) ));
		// Prepare for simulation. Typically we use a time step of 1/60 of a
		// second (60Hz) and 10 iterations. This provides a high quality simulation
		// in most game scenarios.
		float timeStep = 1.0f / 40.0f;
		int iterations = 2;

		// Instruct the world to perform a single step of simulation.
		// It is generally best to keep the time step and iterations fixed.
		world.step(timeStep, iterations);

		// note, we count this before updating the objects to ensure
		// leveldone does not override lifelost.
		if (countObjects("alien",0) == 0) levelDone();
		moveObjects(null,0);
		checkCollision(2,4); // objects hit alien
		checkCollision(4,1); // alien hit player
		checkCollision(1,32); // player hit star
		checkCollision(8,16);
		checkBGCollision(1,4);
		moveObjects("_shadow",0);
		/*if (getMouseButton(1) || getKey('5')) {
			clearMouseButton(1);
			clearKey('5');
			//optype = (optype+1)%11;
			new PhysicsObject(getMouseX(),getMouseY(),20,20,false);
		}*/
	}
	void setGlobalMsg(String msg,int time) {
		globalMsg = msg;
		globalMsgTime = time;
	}
	String globalMsg="";
	int globalMsgTime=0;
	JGFont globalmsg_font = new JGFont("Sans",0,30);
	public void paintFrameInGame() {
		//drawString("k="+getLastKey()+" ch="+(int)getLastKeyChar(),100,100,0);
		if (globalMsgTime > 0) {
			globalMsgTime--;
			if (globalMsg!=null) {
				setColor(JGColor.white);
				setFont(globalmsg_font);
				drawString(globalMsg, pfWidth()/2,pfHeight()/2-15, 0);
			}
		}
		//Instructions
		//Kill the robots with red balls or smash them into walls.
		//
		//Shoot the ball:
		//Mouse/touch: drag to aim, release to fire shot.
		//Keyboard: press Fire/Z, use cursor keys to aim, press Fire/Z to fire
		//
		//Move around:
		//Mouse/touch: hold until cross appears, drag to move
		//Keyboard: use cursor keys
		//
		// Star bonus:
		// Get both stars in one shot to gain an extra life
		/*drawLine(0,0,pfWidth(),0);
		drawLine(0,0,0,pfHeight());
		drawLine(0,pfHeight()-1,pfWidth(),pfHeight()-1);
		drawLine(pfWidth()-1,0,pfWidth()-1,pfHeight());*/
	}

	// -------------------------
	// TITLE
	// -------------------------
	int startlevel=1;
	public void startTitle() {
		setBGImage(null);
		fillBG("");
		removeObjects(null,0);
	}
	public void doFrameTitle() {
		if (getKey(KeyMouse1)) {
			clearMouseButton(1);
			clearKey(KeyMouse1);
			if (getMouseY() > pfHeight()/2-100-50
			&&  getMouseY() < pfHeight()/2-100+50) {
				invokeUrl("http://tmtg.net/","_blank");
			}
			if (getMouseY() > pfHeight()/2-50
			&&  getMouseY() < pfHeight()/2+50) {
				startlevel += 4;
				if (startlevel >= 16) startlevel=1;
			}
			if (getMouseY() > pfHeight()/2+100-50
			&&  getMouseY() < pfHeight()/2+100+50) {
				startGame(startlevel-1);
			}
		}
		if (getKey(key_fire)) {
			startGame(startlevel-1);
		}
		if (getKey(key_left)) {
			if (startlevel>0) startlevel -= 4;
			clearKey(key_left);
		}
		if (getKey(key_right)) {
			if (startlevel<14) startlevel += 4;
			clearKey(key_right);
		}
	}
	public void paintFrameTitle() {
		drawImage("splash_image",pfWidth()/2-80,pfHeight()/7);
		setFont(new JGFont("Sans",0,23));
		drawString("More games on tmtg.net",
			pfWidth()/2,pfHeight()/2-100,0);
		drawString("Start on level: "+startlevel,
			pfWidth()/2,pfHeight()/2,0);
		drawString("Touch here or",
			pfWidth()/2,pfHeight()/2+85,0);
		drawString("press fire to start",
			pfWidth()/2,pfHeight()/2+115,0);
		setColor(JGColor.orange);
		setFont(new JGFont("Sans",0,21));
		drawString("Credits:",
			pfWidth()/2,pfHeight()/2+160,0);
		drawString("Some gfx by: webtreats",
			pfWidth()/2,pfHeight()/2+195,0);
		drawString("Some sounds by:",
			pfWidth()/2,pfHeight()/2+230,0);
		drawString("mwmarsh,justkiddink,bunyi",
			pfWidth()/2,pfHeight()/2+265,0);
	}


	/**
	 * Called when a contact point is added. This includes the geometry
	 * and the forces.
	 */
	public void add(ContactPoint point) {
		StdPhysicsObject o1=(StdPhysicsObject)(point.shape1.m_body.m_userData);
		StdPhysicsObject o2=(StdPhysicsObject)(point.shape2.m_body.m_userData);
		int stone=0,wood=0;
		if (o1 instanceof Player||o1 instanceof Fireball) stone++;
		if (o2 instanceof Player||o2 instanceof Fireball) stone++;
		if (o1 instanceof Crate||o1 instanceof Barrel) wood++;
		if (o2 instanceof Crate||o2 instanceof Barrel) wood++;
		if (stone==2) {
			playAudio("ballhit");
		} else if (stone==1 && wood==1) {
			playAudio("woodhit");
		}
	}

	public void persist(ContactPoint point) {}
	public void remove(ContactPoint point) {}
	public void result(ContactResult point) {}


	public void resetHitCounts() {
		stars_this_hit=0;
		nr_aliens_this_hit=0;
		// if one star left, destroy it
		if (countObjects("star",0) == 1) {
			removeObjects("star",0);
		}
	}

	class Player extends StdPhysicsObject {
		Player(int x,int y) {
			super("player",1,x,y,0.0,20.0,3.0,0.15,0.8);
			setAppearance(null,"ball1",-22,-22,false,1,1);
			new BallShadow(this);
			// set more lenient bounding box
			setBBox(-(int)(0.8*20), -(int)(0.8*20),
				2*(int)(0.8*20), 2*(int)(0.8*20));

		}
		int immunity=20;
		boolean drag=false;
		boolean drag_kb=false;
		int holding=0;
		boolean holding_inc=false;
		boolean continuous=false;
		boolean continuous_kb=false;
		int startx,starty;
		int dx_kb=0,dy_kb=0;
		public void move() {
			super.move();
			immunity--;
			holding_inc=false;
			applyBackgroundFriction(3.0f,0.99f);
			if (!drag) {
				if (getMouseButton(1)) {
					drag=true;
					holding=0;
					continuous=false;
					startx=getMouseX();
					starty=getMouseY();
				}
			} else {
				int fx = -startx+getMouseX();
				int fy = -starty+getMouseY();
				if (fx*fx+fy*fy <= 256) {
					holding++;
					holding_inc=true;
					if (holding > 9) continuous=true;
				}
				if (continuous) {
					resetHitCounts();
					body.applyForce(new Vec2(100f*fx,100f*fy),
						body.getWorldCenter() );
					startx = getMouseX();
					starty = getMouseY();
					if (!getMouseButton(1)) {
						drag=false;
						continuous=false;
					}
					
				} else if (!getMouseButton(1)) {
					drag=false;
					resetHitCounts();
					playAudio("polehit");
					body.applyForce(new Vec2(80f*fx,80f*fy),
						body.getWorldCenter() );
						//new Vec2(0.1f*(float)x/PXMUL,
						//	0.1f*(float)y/PXMUL) );
				}
			}
			continuous_kb=false;
			if (!drag_kb)  {
				if (getKey(key_fire)) {
					drag_kb = true;
					dx_kb=0;
					dy_kb=0;
					clearKey(key_fire);
				}
				if (getKey(key_up)) {
					continuous_kb=true;
					resetHitCounts();
					body.applyForce(new Vec2(600f*0,600f*-1),
						body.getWorldCenter() );
				}
				if (getKey(key_down)) {
					continuous_kb=true;
					resetHitCounts();
					body.applyForce(new Vec2(600f*0,600f*1),
						body.getWorldCenter() );
				}
				if (getKey(key_left)) {
					continuous_kb=true;
					resetHitCounts();
					body.applyForce(new Vec2(600f*-1,600f*0),
						body.getWorldCenter() );
				}
				if (getKey(key_right)) {
					continuous_kb=true;
					resetHitCounts();
					body.applyForce(new Vec2(600f*1,600f*0),
						body.getWorldCenter() );
				}
			} else {
				if (getKey(key_fire)) {
					drag_kb = false;
					clearKey(key_fire);
					resetHitCounts();
					playAudio("polehit");
					body.applyForce(new Vec2(80f*dx_kb,80f*dy_kb),
						body.getWorldCenter() );
				}
				if (getKey(key_up)) {
					dy_kb -= 8;
				}
				if (getKey(key_down)) {
					dy_kb += 8;
				}
				if (getKey(key_left)) {
					dx_kb -= 8;
				}
				if (getKey(key_right)) {
					dx_kb += 8;
				}
			}
		}
		public void hit(JGObject obj) {
			if (immunity>0) {
				obj.remove();
			} else {
				obj.remove();
				genExplo(obj.x+18,obj.y+18,5,20,20,false);
				remove();
				lifeLost();
				genExplo(x,y,5,10,20,false);
			}
		}
		public void paint() {
			super.paint();
			setColor(JGColor.white);
			setStroke(4);
			if (drag) { // mouse control indicators
				if (holding_inc && !continuous) {
					drawOval(x,y,40,40,false,true);
				}
				if (!continuous) {
					drawLine(x,y,
						x+getMouseX()-startx,
						y+getMouseY()-starty );
				} else {
					drawLine(x-10,y,x+10,y);
					drawLine(x,y-10,x,y+10);
				}
			} else if (drag_kb) { // kb control indicators
				if (!continuous) {
					drawLine(x,y,
						x+dx_kb,
						y+dy_kb );
				}
			}
		}
	}
	class BallShadow extends JGObject {
		JGObject obj;
		BallShadow(JGObject o) {
			super("_shadow",true,o.x,o.y,0,"ballshadow");
			obj = o;
		}
		public void move() {
			if (!obj.isAlive()) remove();
			x = obj.x-15;
			y = obj.y-15;
		}
		/*public void paint() {
			setColor(new JGColor(0,0,0, 0.3));
			drawOval(x+10,y+10,40,40,true,true);
		}*/
	}
	class AlienShadow extends JGObject {
		JGObject obj;
		AlienShadow(JGObject o) {
			super("_shadow",true,o.x,o.y,0,"alienshadow");
			obj = o;
		}
		public void move() {
			if (!obj.isAlive()) {
				remove();
			}
			x = obj.x;
			y = obj.y;
		}
	}
	class Fireball extends StdPhysicsObject {
		Fireball(int x,int y) {
			super("fireball",2+8,x,y,0.0,20.0,3.0,0.15,0.8);
			setAppearance(null,"ball2",-22,-22,false,1,1);
			new BallShadow(this);
		}
		public void move() {
			super.move();
			applyBackgroundFriction(3.0f,0.99f);
		}
	}
	class Crate extends StdPhysicsObject {
		Crate(int x,int y) {
			super("crate",2,x,y,0.0,40,40,1.5, 0.25,0.6);
			setAppearance(null,"crate",-40,-40,true,NRROT,1);
			new BallShadow(this);
		}
		public void move() {
			super.move();
			applyBackgroundFriction(60.0f,0.965f);
		}
	}
	class Barrel extends StdPhysicsObject {
		Barrel(int x,int y) {
			super("barrel",2+16,x,y,0.0,20,3.0, 0.15,0.5);
			setAppearance(null,"barrel",-20,-20,true,NRROT,1);
			new BallShadow(this);
		}
		int expires=0;
		public void move() {
			super.move();
			applyBackgroundFriction(5.0f,0.99f);
			if (expires>0) {
				if ((expires%5)==0) {
					new Fire(x+random(-10,10),y+random(-10,10),0,15,false);
				}
				if (expires==1) {
					remove();
					genExplo(x,y,25,110,20,true);
				}
				expires--;
			}
		}
		public void hit(JGObject obj) {
			// hit by fire or fireball
			if (expires==0) expires=100;
		}
	}
	class Star extends JGObject {
		Star(double x,double y) {
			super("star",true,x,y,32,"star");
		}
		public void hit(JGObject obj) {
			remove();
			stars_this_hit++;
			if (stars_this_hit >= 2) {
				lives++;
				score += 50;
				playAudio("bonus");
			}
		}
	}
	class Fire extends JGObject {
		double waittime;
		int expires;
		Fire(double xcen,double ycen,int waittime, int expires,
		boolean deadly) {
			super("fire",true,xcen-24,ycen-24,deadly?2+8:8,null);
			this.waittime=waittime;
			this.expires = expires;
		}
		public void move() {
			if (waittime >= 0) {
				waittime -= gamespeed;
				if (waittime < 0) {
					setGraphic("fire");
					expiry = expires;
				}
			}
		}
	}
	public void genExplo(double x,double y, int nr, int radius, int time,
	boolean deadly) {
		for (int i=0; i<nr; i++) {
			new Fire(random(x-radius,x+radius),random(y-radius,y+radius),
				random(0,time,1), random(time/2,time,1), deadly );
		}
		playAudio("explo");
	}
	class Alien extends JGObject {
		double speed;
		Alien(double x,double y, double speed) {
			super("alien",true,x,y,4,"alien"+(stage%8));
			this.speed=speed;
			new AlienShadow(this);
		}
		double newdirtimer=0;
		public void move() {
			/*if (x<15 && xdir<=0) xdir=1;
			if (x>pfwidth-36-15 && xdir>=0) xdir=-1;
			if (y<15 && ydir<=0) ydir=1;
			if (y>pfheight-36-15 && ydir>=0) ydir=-1;*/
			if (newdirtimer<=0) {
				newdirtimer=random(15,35);
				double ang=random(0.0,Math.PI*2);
				//if (random(0,1) > 0.5 && player!=null) {
				//	ang = atan2(player.x-x,player.y-y);
				//}
				setSpeedAbs(speed*Math.sin(ang),speed*Math.cos(ang));
			} else {
				newdirtimer -= gamespeed;
			}
			JGRectangle tiles = getTiles();
			int t = tiles.x;
			int l = tiles.y;
			int b = t + tiles.height;
			int r = l + tiles.width;
			int oldxdir=xdir,oldydir=ydir;
			if (and(getTileCid(t-1, l  ),3)) xdir =  1;
			if (and(getTileCid(b+1, r  ),3)) xdir = -1;
			if (and(getTileCid(t,   l-1),3)) ydir =  1;
			if (and(getTileCid(b,   r+1),3)) ydir = -1;
			if (and(getTileCid(t-1, l-1),3)) {xdir =  1; ydir =  1;}
			if (and(getTileCid(b+1, l-1),3)) {xdir = -1; ydir =  1;}
			if (and(getTileCid(t-1, r+1),3)) {xdir =  1; ydir = -1;}
			if (and(getTileCid(b+1, r+1),3)) {xdir = -1; ydir = -1;}
			//if (xdir!=oldxdir || ydir!=oldydir) dbgPrint("plop");
		}
		void die() {
			nr_aliens_this_hit++;
			nr_aliens_this_frame++;
			int nr = Math.max(nr_aliens_this_hit,
				nr_aliens_this_frame+nr_aliens_last_frame);
			score += 10*nr;
			if (nr==3) {
				score += 100;
				setGlobalMsg("3 hits! +100",60);
			} else if (nr==5) {
				score += 250;
				setGlobalMsg("5 hits! +250",60);
			}
			genExplo(x+18,y+18,5,20,10,false);
			remove();
		}
		public void hit(JGObject obj) {
			if (obj instanceof Fire) {
				die();
				return;
			}
			StdPhysicsObject o = (StdPhysicsObject)obj;
			Vec2 ospeed = o.getSpeedPixelsPerFrame();
			if (!(o instanceof Fireball)
			|| ospeed.x*ospeed.x + ospeed.y*ospeed.y < 9) {
				// slow collision -> push away
				double dx = (x-18) - o.x;
				double dy = (y-18) - o.y;
				double atan = atan2(dx,dy);
				x += ospeed.x;
				y += ospeed.y;
				x += 2*Math.sin(atan);
				y += 2*Math.cos(atan);
				if (x<0 || x>pfwidth-36
				||  y<0 || y>pfheight-36) {
					die();
				}
			} else {
				// fast speed -> die
				die();
			}
		}
		public void hit_bg(int tilecid,int tilex,int tiley) {
			die();
		}
	}

}

