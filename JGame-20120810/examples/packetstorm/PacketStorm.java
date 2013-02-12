package examples.packetstorm;
import jgame.*;
import jgame.platform.*;
/** A minimal game using StdGame with default settings. */
public class PacketStorm extends StdGame {
	public static void main(String[]args) {new PacketStorm(parseSizeArgs(args,0));}
	public PacketStorm() { initEngineApplet(); }
	public PacketStorm(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(36,29,16,16,null,null,null); }
	public void initGame() {
		defineMedia("packet_storm.tbl");
		setFrameRate(35,1);
		key_startgame=KeyMouse1;
		key_continuegame=KeyMouse1;
		setHighscores(10,new Highscore(0,"nobody"),15);
	}
	public void initNewLife() {
		removeObjects(null,0);
		new Cursor();
	}
	public void incrementLevel() {
		if (level<5) level++;
		stage++;
	}
	final static int nrxsegs=7;
	final static int xsegsize=5;
	final static int nrysegs=7;
	final static int ysegsize=4;
	int prevxseg=-1,prevyseg=-1;
	public void defineLevel() {
		removeObjects(null,0);
		fillBG("lb");
		for (int x=0; x<pfTilesX(); x+=nrxsegs*xsegsize) {
			for (int y=0; y<pfTilesY(); y++) setTile(x,y,"k");
		}
		for (int y=0; y<pfTilesY(); y+=nrysegs*ysegsize) {
			for (int x=0; x<pfTilesX(); x++) setTile(x,y,"k");
		}
		for (int x=0; x<pfTilesX(); x+=xsegsize) {
			for (int y=0; y<pfTilesY(); y+=ysegsize) {
				setTile(x,y,"k");
			}
		}
	}
	public void startGameOver() { removeObjects(null,0); }
	public void startInGame() { clearMouseButton(1);}
	public void startLifeLost() {
		playAudio("die");
		clearKey(key_continuegame);
	}
	public void doFrameLifeLost() {
		moveObjects(null,2);
	}
	public void doFrameInGame() {
		moveObjects();
		checkCollision(1,2);
		if (checkTime(0,2500,210-20*stage)) {
			for (int i=0; i<2; i++) {
				int xpos,ypos,xdir,ydir;
				do {
					if (random(0,1) > 0.5) {
						xpos = random(xsegsize,(nrxsegs-1)*xsegsize,xsegsize);
						ypos = random(0,nrysegs*ysegsize,nrysegs*ysegsize);
						xdir = 0;
						ydir = (ypos==0) ? 1 : -1;
					} else {
						xpos = random(0,nrxsegs*xsegsize,nrxsegs*xsegsize);
						ypos = random(ysegsize,(nrysegs-1)*ysegsize,ysegsize);
						xdir = (xpos==0) ? 1 : -1;
						ydir = 0;
					}
				} while (getTileCid(xpos,ypos)!=0);
				if (i==0)
					new Packet(xpos*tileWidth(),ypos*tileHeight(),xdir,ydir);
				else
					new Sink(xpos*tileWidth(),ypos*tileHeight());
			}
		}
		if (gametime >= 2500 && countObjects("packet",0) == 0) levelDone();
		if (getMouseButton(1)) {
			int xseg = (getMouseX()/(tileWidth()*xsegsize));
			int yseg = (getMouseY()/(tileHeight()*ysegsize));
			if (prevxseg!=xseg || prevyseg!=yseg) {
				prevxseg = xseg;
				prevyseg = yseg;
				flipSegment(xseg,yseg);
			}
		} else {
			prevxseg = -1;
			prevyseg = -1;
		}
	}
	void flipSegment(int xseg, int yseg) {
		if (xseg<0 || xseg>=nrxsegs || yseg<0 || yseg>=nrysegs) return;
		int left   = xseg*xsegsize;
		int top    = yseg*ysegsize;
		int right  = (xseg+1)*xsegsize;
		int bottom = (yseg+1)*ysegsize;
		int oncount=0;
		// toggle left side
		for (int x=0; x<=xsegsize; x+=xsegsize) {
			for (int yi=1; yi<ysegsize; yi++) {
				if (left+x<=0 || left+x>=nrxsegs*xsegsize) continue;
				if (and(getTileCid(left+x,top+yi),1)) oncount++;
				setTile(left+x,top+yi,
					and(getTileCid(left+x,top+yi),1) ? "k" : "lb" );
			}
		}
		for (int y=0; y<=ysegsize; y+=ysegsize) {
			for (int xi=1; xi<xsegsize; xi++) {
				if (top+y<=0 || top+y>=nrysegs*ysegsize) continue;
				if (and(getTileCid(left+xi,top+y),1)) oncount++;
				setTile(left+xi,top+y,
					and(getTileCid(left+xi,top+y),1) ? "k" : "lb" );
			}
		}
		if (oncount>8) {
			playAudio("toggle1");
		} else {
			playAudio("toggle2");
		}
	}
	JGFont scoring_font = new JGFont("Arial",0,8);
	public class Cursor extends JGObject {
		public Cursor() {
			super("cursor",false,16,16,1,null);
		}
		public void move() {
			setDir(0,0);
			if (getKey(key_up)    && y > yspeed)               ydir=-1;
			if (getKey(key_down)  && y < pfHeight()-16-yspeed) ydir=1;
			if (getKey(key_left)  && x > xspeed)               xdir=-1;
			if (getKey(key_right) && x < pfWidth()-32-yspeed)  xdir=1;
		}
	}
	public class Packet extends JGObject {
		int waittime=150;
		JGPoint [] trail = new JGPoint[100];
		int traillen=0;
		public Packet(double x,double y, int xdir, int ydir) {
			super("packet",true,x,y,1,"packet_wait");
			setDir(xdir,ydir);
			JGPoint cen = getCenterTile();
			setTileCid(cen.x,cen.y,1);
			playAudio("newpacket");
			for (int i=0; i<100; i++) trail[i] = new JGPoint(0,0);
		}
		void clearTrail() {
			for (int i=0; i<traillen; i++) {
				//andTileCid(trail[i].x,trail[i].y,255-4);
			}
			traillen=0;
		}
		/** Check if path leads to sink; recursive function */
		boolean checkAhead(int tx,int ty,int xdir,int ydir) {
			if (and(getTileCid(tx, ty),1)) return false;
			if (and(getTileCid(tx, ty),2)) return true;
			if (and(getTileCid(tx, ty),4)) return false;
			if (tx<=0 || tx>=nrxsegs*xsegsize) return false;
			if (ty<=0 || ty>=nrysegs*ysegsize) return false;
			if (traillen==100) return false;
			//trail[traillen].x=tx;
			//trail[traillen].y=ty;
			traillen++;
			//orTileCid(tx,ty,4);
			if (and(getTileCid(tx+xdir, ty+ydir),1)) {
				if (getTileCid(tx+ydir,ty+xdir)==0) {
					return checkAhead(tx+ydir,ty+xdir,ydir,xdir);
				} else if (getTileCid(tx-ydir,ty-xdir)==0) {
					return checkAhead(tx-ydir,ty-xdir,-ydir,-xdir);
				} else {
					return false;
				}
			} else {
				return checkAhead(tx+xdir,ty+ydir,xdir,ydir);
			}
		}
		public void move() {
			JGPoint cen = getCenterTile();
			if (waittime>0) {
				waittime--;
				if (waittime==0) {
					setTileCid(cen.x,cen.y,0);
					setSpeed(0.8,0.8);
					x += xdir*xspeed;
					y += ydir*yspeed;
					setGraphic("packet_move");
					playAudio("release");
					if (and(getTileCid(getCenterTile(),xdir,ydir),1)) {
						setGraphic("packet_die");
						setSpeed(0,0);
						lifeLost();
					}
				}
			}
			if (waittime<=0) {
				if (isXAligned()&&isYAligned()) {
					if (cen.x<=0 || cen.x>=nrxsegs*xsegsize
					||  cen.y<=0 || cen.y>=nrysegs*ysegsize) lifeLost();
					snapToGrid();
					if (and(getTileCid(cen,xdir,ydir),1)) {
						if (getTileCid(cen,ydir,xdir)==0) {
							int t=xdir;
							xdir=ydir;
							ydir=t;
						} else if (getTileCid(cen,-ydir,-xdir)==0) {
							int t=xdir;
							xdir=-ydir;
							ydir=-t;
						} else {
							setGraphic("packet_die");
							setSpeed(0,0);
							lifeLost();
						}
					}
				}
				if (checkAhead(cen.x,cen.y,xdir,ydir)) {
					setGraphic("packet_die");
					setSpeed(3.0,3.0);
				} else {
					setGraphic("packet_move");
					setSpeed(0.8,0.8);
				}
				clearTrail();
			}
		}
		public void destroy() {
			clearTrail();
		}
	}
	public class Sink extends JGObject {
		public Sink(double x,double y) {
			super("sink",true,x,y,2,"white16");
			JGPoint cen = getCenterTile();
			setTileCid(cen.x,cen.y,2);
		}
		public void hit(JGObject obj) {
			JGPoint cen = getCenterTile();
			remove();
			obj.remove();
			playAudio("packetdone");
		}
		public void destroy() {
			JGPoint cen = getCenterTile();
			setTileCid(cen.x,cen.y,0);
		}
	}
}
