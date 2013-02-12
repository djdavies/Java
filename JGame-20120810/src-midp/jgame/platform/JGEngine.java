package jgame.platform;

import jgame.*;
import jgame.impl.*;

import java.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Timer;
import java.util.Random;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.game.LayerManager;

import javax.microedition.rms.*;

import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;

public abstract class JGEngine extends MIDlet implements JGEngineInterface {

	/*=== main objects ===*/

	MIDPImage imageutil = new MIDPImage();

	EngineLogic el = new EngineLogic(imageutil,true,true);

	Thread gamethread=null;


	/*=== canvas ===*/

	JGCanvas canvas = new JGCanvas();

	public void setProgressBar(double pos) {
		// XXX check out if the load screen gets updated properly if we don't
		// use defineMedia
		canvas.progress_bar=pos;
		canvas.drawAll();
	}

	public void setProgressMessage(String msg) {
		canvas.progress_message=msg;
		canvas.drawAll();
	}

	public void setAuthorMessage(String msg) {
		canvas.author_message=msg;
		canvas.drawAll();
	}


	Image background=null;
	Graphics bgg=null;

	class JGCanvas extends GameCanvas {
		/** for displaying progress info */
		String progress_message="Loading files ...";
		/** for displaying progress bar, value between 0.0 - 1.0 */
		public double progress_bar=0.0;

		String author_message="JGame "+JGameVersionString;

		public JGCanvas () {
			super(false); 
			setFullScreenMode(true);
		}

		boolean is_initialised=false;
		void setInitialised() {
			is_initialised=true; 
			//initpainter=null;
		}

		public void keyPressed(int keyCode) {
			switch (keyCode) {
			case KEY_NUM0 : keymap['0'] = true; break;
			case KEY_NUM1 : keymap['1'] = true; break;
			case KEY_NUM2 : keymap['2'] = true; break;
			case KEY_NUM3 : keymap['3'] = true; break;
			case KEY_NUM4 : keymap['4'] = true; break;
			case KEY_NUM5 : keymap['5'] = true; break;
			case KEY_NUM6 : keymap['6'] = true; break;
			case KEY_NUM7 : keymap['7'] = true; break;
			case KEY_NUM8 : keymap['8'] = true; break;
			case KEY_NUM9 : keymap['9'] = true; break;
			case KEY_POUND: keymap[' '] = true;
			                keymap['#'] = true; break;
			case KEY_STAR : keymap[KeyEnter] = true;
			                keymap['*'] = true; break;
			default:
				// only parse game key when no other key was identified.
				// because sagem my401c maps numeric keys to game keys as well
				int gameAct = getGameAction(keyCode);
				switch (gameAct) {
				case UP   : keymap[KeyUp] = true; break;
				case LEFT : keymap[KeyLeft] = true; break;
				case RIGHT: keymap[KeyRight] = true; break;
				case DOWN : keymap[KeyDown] = true; break;
				case FIRE : keymap[KeyShift] = true;
				// support GAME_A ... GAME-D
				}
			}
			if (wakeup_key==-1 || keymap[wakeup_key]) {
				if (!running) {
					start();
					// key is cleared when it is used as wakeup key
					if (wakeup_key!=-1) keymap[wakeup_key]=false;
				}
			}

		}

		public void keyReleased(int keyCode) {
			switch (keyCode) {
			case KEY_NUM0 : keymap['0'] = false; break;
			case KEY_NUM1 : keymap['1'] = false; break;
			case KEY_NUM2 : keymap['2'] = false; break;
			case KEY_NUM3 : keymap['3'] = false; break;
			case KEY_NUM4 : keymap['4'] = false; break;
			case KEY_NUM5 : keymap['5'] = false; break;
			case KEY_NUM6 : keymap['6'] = false; break;
			case KEY_NUM7 : keymap['7'] = false; break;
			case KEY_NUM8 : keymap['8'] = false; break;
			case KEY_NUM9 : keymap['9'] = false; break;
			case KEY_POUND: keymap[' '] = false;
			                keymap['#'] = false; break;
			case KEY_STAR : keymap[KeyEnter] = false;
			                keymap['*'] = false;
			}
			int gameAct = getGameAction(keyCode);
			switch (gameAct) {
			case UP   : keymap[KeyUp] = false; break;
			case LEFT : keymap[KeyLeft] = false; break;
			case RIGHT: keymap[KeyRight] = false; break;
			case DOWN : keymap[KeyDown] = false; break;
			case FIRE : keymap[KeyFire] = false;
			// support GAME_A ... GAME-D
			}
		}
		public void pointerPressed(int x, int y) {
			mousepos.x = (int)((x-el.canvas_xofs)/el.x_scale_fac);
			mousepos.y = (int)((y-el.canvas_yofs)/el.y_scale_fac);
			mousebutton[1] = true;
			keymap[256]=true;
			mouseinside=true;
		}

		public void pointerReleased(int x, int y) {
			mousepos.x = (int)((x-el.canvas_xofs)/el.x_scale_fac);
			mousepos.y = (int)((y-el.canvas_yofs)/el.y_scale_fac);
			mousebutton[1] = false;
			keymap[256]=false;
			mouseinside=false;
		}

		public void pointerDragged(int x, int y) {
			mousepos.x = (int)((x-el.canvas_xofs)/el.x_scale_fac);
			mousepos.y = (int)((y-el.canvas_yofs)/el.y_scale_fac);
			mouseinside=true;
		}


		public void clearCanvas() {
			// fill canvas with black
			if (bufg==null) bufg = getGraphics();
			bufg.setClip(0,0,getWidth(),getHeight());
			setColor(bufg,JGColor.black);
			bufg.fillRect(0,0,getWidth(),getHeight());
		}

		long last_update_time = 0;
		Graphics bufg=null;
		public void drawAll() { try {
			if (el.is_exited) {
				//paintExitMessage(g);
				return;
			}
			// get graphics of built-in off-screen buffer.
			// nokia developer's guide says that the same graphics can be used
			// throughout the lifecycle of the canvas.
			if (bufg==null) bufg = getGraphics();
			if (!is_initialised) {
				long time = System.currentTimeMillis();
				if (time < last_update_time + 200) return;
				last_update_time = time;
				setFont(bufg,el.msg_font);
				setColor(bufg,el.fg_color);
				JGImage splash = el.existsImage("splash_image") ?
						el.getImage("splash_image")  :  null;
				if (splash!=null) {
					JGPoint splash_size=getImageSize("splash_image");
					drawImage(bufg,viewWidth()/2-splash_size.x/2,
						Math.max(0,viewHeight()/4-splash_size.y/2),
						"splash_image",
						false);
				}
				drawString(bufg,progress_message,
					viewWidth()/2,3*viewHeight()/5,0,false);
				//if (canvas.progress_message!=null) {
					//drawString(bufg,canvas.progress_message,
					//		viewWidth()/2,2*viewHeight()/3,0);
				//}
				// paint the right hand side black in case the bar decreases
				setColor(bufg,el.bg_color);
				drawRect(bufg,(int)(viewWidth()*(0.1+0.8*progress_bar)),
						(int)(viewHeight()*0.75),
						(int)(viewWidth()*0.8*(1.0-progress_bar)),
						(int)(viewHeight()*0.05), true,false, false);
				// left hand side of bar
				setColor(bufg,el.fg_color);
				drawRect(bufg,(int)(viewWidth()*0.1), (int)(viewHeight()*0.75),
						(int)(viewWidth()*0.8*progress_bar),
						(int)(viewHeight()*0.05), true,false, false);
				// length stripes
				/*drawRect(bufg,(int)(viewWidth()*0.1), (int)(viewHeight()*0.6),
						(int)(viewWidth()*0.8),
						(int)(viewHeight()*0.008), true,false, false);
				drawRect(bufg,(int)(viewWidth()*0.1),
						(int)(viewHeight()*(0.6+0.046)),
						(int)(viewWidth()*0.8),
						(int)(viewHeight()*0.008), true,false, false);*/
				drawString(bufg,author_message,
					viewWidth()-16,viewHeight()-getFontHeight(el.msg_font)-10,
					1,false);
				flushGraphics();
				return;
			}
			bufg.setClip(Math.max(0,el.canvas_xofs),Math.max(0,el.canvas_yofs),
				Math.min(el.width,el.winwidth),
				Math.min(el.height,el.winheight) );
			//bufg.setClip(0,0,120,120);
			// block update thread
			synchronized (el.objects) {
				// paint any part of bg which is not yet defined
				el.repaintBG(JGEngine.this);
				/* clear buffer */
				buf_gfx = bufg; // enable objects to draw on buffer gfx.
				// Draw background to buffer.
				// this part is the same across jre and midp.  Move it to
				// EngineLogic?
				//bufg.drawImage(background,-scaledtilex,-scaledtiley,this);
				int tilexshift=el.moduloFloor(el.tilexofs+1,el.viewnrtilesx+3);
				int tileyshift=el.moduloFloor(el.tileyofs+1,el.viewnrtilesy+3);
				int sx1 = tilexshift+1;
				int sy1 = tileyshift+1;
				int sx2 = el.viewnrtilesx+3;
				int sy2 = el.viewnrtilesy+3;
				if (sx2-sx1 > el.viewnrtilesx) sx2 = sx1 + el.viewnrtilesx;
				if (sy2-sy1 > el.viewnrtilesy) sy2 = sy1 + el.viewnrtilesy;
				int bufmidx = sx2-sx1;
				int bufmidy = sy2-sy1;
				copyBGToBuf(bufg,sx1,sy1, sx2,sy2, 0,0);
				sx1 = 0;
				sy1 = 0;
				sx2 = tilexshift-1;
				sy2 = tileyshift-1;
				copyBGToBuf(bufg,sx1,sy1, sx2,sy2, bufmidx,bufmidy);
				sx1 = 0;
				sy1 = tileyshift+1;
				sx2 = tilexshift-1;
				sy2 = el.viewnrtilesy+3;
				if (sy2-sy1 > el.viewnrtilesy) sy2 = sy1 + el.viewnrtilesy;
				copyBGToBuf(bufg,sx1,sy1, sx2,sy2, bufmidx,0);
				sx1 = tilexshift+1;
				sy1 = 0;
				sx2 = el.viewnrtilesx+3;
				sy2 = tileyshift-1;
				if (sx2-sx1 > el.viewnrtilesx) sx2 = sx1 + el.viewnrtilesx;
				copyBGToBuf(bufg,sx1,sy1, sx2,sy2, 0,bufmidy);
				for (int i=0; i<el.objects.size; i++) {
					drawObject(bufg, (JGObject)el.objects.values[i]);
				}
				buf_gfx = null; // we're finished with the object drawing
				/* draw status */
				paintFrame(bufg);
				/* draw buffer */
				flushGraphics();
			}
		} catch (JGameError e) {
			e.printStackTrace();
			exitEngine("Error during paint:\n"
					+dbgExceptionToString(e) );
		} }
	}

	/*=== jre applet emulation ===*/

	/** In future implementations, we may use this method to get a jad field.
	 */
	public String getParameter(String name) { return null; }

	/*=== midlet lifecycle ===*/

	/** Constructor called by application manager. Call initEngineApplet
	* here. */
	public JGEngine() { }


	/** Called when midlet is first initialised, or unpaused. */
	public void startApp() {
		Display display = Display.getDisplay(this);
		display.setCurrent(canvas);
		if (!el.is_inited) {
			// XXX define this in argument to EngineLogic constructor.
			el.is_resizeable=false;
			init();
		} else {
			start();
		}
		canvas.clearCanvas();
	}


	/** Wait for the unpause signal before initing canvas. */
	public void initEngineApplet() { }

	/** This method is not used for midlets, since the window size is
	 * determined by the environment. */
	public void initEngine(int width,int height) {
		exitEngine("Use initEngineApplet");
	}
	/** This method is not used for midlets, since in MIDP there is no
	* such thing as window layout. */
	public void initEngineComponent(int width,int height) {
		exitEngine("Use initEngineApplet");
	}

	/** Call setCanvasSettings here. */
	public abstract void initCanvas();

	/** Called by the application manager to pause app. Just calls stop(). */
	public void pauseApp() {
		stop();
	}

	/** Called by the application manager to exit app. Just calls destroy(). */
	public void destroyApp(boolean unconditional) {
		destroy();
	}


	/** initialise engine */
	void init() {
		storeInit();
		// canvas size might change, we don't support this yet
		el.winwidth = canvas.getWidth();
		el.winheight= canvas.getHeight();
		// get all the dimensions
		initCanvas();
		if (!el.view_initialised) {
			exitEngine("Canvas settings not initialised, use setCanvasSettings().");
		}
		// init vars
		//canvas = new JGCanvas(el.winwidth,el.winheight);
		el.initPF();
		clearKeymap();
		// set canvas padding color  (probably we need to draw an el.bg_color
		// rectangle)
		// determine default font size (unscaled)
		el.msg_font = new JGFont("Helvetica",0,
			(int)(16.0/(640.0/(el.tilex * el.nrtilesx))));
		if (!JGObject.setEngine(this)) {
			exitEngine("Another JGEngine is already running!");
		}
		el.is_inited=true;
		// do something like setInitPainter here to init the loading screen
		// create background (note: buffer is included in GameCanvas)
		background=Image.createImage(el.width+3*el.scaledtilex,
				el.height+3*el.scaledtiley );
		el.invalidateBGTiles();
		gamethread = new Thread(new JGEngineThread());
		gamethread.start();
	}

	private void clearKeymap() {
		for (int i=0; i<256+3; i++) keymap[i]=false;
	}


	abstract public void initGame();

	public void start() { running=true; }

	public void stop() { running=false; }

	public boolean isRunning() { return running; }

	public void wakeUpOnKey(int key) { wakeup_key=key; }

	public void destroy() {
		// kill game thread
		el.is_exited=true;
		if (gamethread!=null) {
			gamethread.interrupt();
			//try {
			//	gamethread.join(2000); // give up after 2 sec
			//} catch (InterruptedException e) {
			//	e.printStackTrace();
			//	// give up
			//}
		}
		// close files?? that appears to be unnecessary
		// reset global variables
		if (el.is_inited) {
			JGObject.setEngine(null);
		}
		// stop all samples (audio not implemented yet)
		disableAudio();
		System.out.println("JGame engine disposed.");
	}



	public void exitEngine(String msg) {
		if (msg!=null) {
			System.err.println(msg);
			el.exit_message=msg;
			// display error to user
			Display display = Display.getDisplay(this);
			Alert alert = new Alert("Application Error");
			alert.setType(AlertType.ERROR);
			alert.setTimeout(Alert.FOREVER);
			alert.setString(msg);
			display.setCurrent(alert);
			// somehow the thread will continue when displaying the alert
			while (true) {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
		} else {
			System.err.println("Exiting JGEngine.");
			destroy();
			notifyDestroyed();
		}
	}



	public void setCanvasSettings(int nrtilesx,int nrtilesy,int tilex,int tiley,
	JGColor fgcolor, JGColor bgcolor, JGFont msgfont) {
		el.nrtilesx=nrtilesx;
		el.nrtilesy=nrtilesy;
		el.viewnrtilesx=nrtilesx;
		el.viewnrtilesy=nrtilesy;
		el.tilex=tilex;
		el.tiley=tiley;
		setColorsFont(fgcolor,bgcolor,msgfont);
		el.view_initialised=true;
	}

	public void setScalingPreferences(double min_aspect_ratio, double
	max_aspect_ratio,int crop_top,int crop_left,int crop_bottom,int crop_right){
		el.min_aspect = min_aspect_ratio;
		el.max_aspect = max_aspect_ratio;
		el.crop_top = crop_top;
		el.crop_left= crop_left;
		el.crop_bottom = crop_bottom;
		el.crop_right = crop_right;
	}

	public void setSmoothing(boolean smooth_magnify) {
		el.smooth_magnify = smooth_magnify;
	}


	/** Engine thread, executing game action. */
	class JGEngineThread implements Runnable {
		private long target_time=0; /* time at which next frame should start */
		private int frames_skipped=0;
		public JGEngineThread () {}
		public void run() { try {
			try {
				initGame();
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
				throw new JGameError("Exception during initGame(): "+e);
			}
			canvas.setInitialised();
			canvas.clearCanvas();
			target_time = System.currentTimeMillis()+(long)(1000.0/el.fps);
			while (!el.is_exited) {
				//canvas.updateKeyState();
				//if ((debugflags&MSGSINPF_DEBUG)!=0) refreshDbgFrameLogs();
				long cur_time = System.currentTimeMillis();
				if (!running) {
					// wait in portions of 1/2 sec until running is set;
					// reset target time
					Thread.sleep(500);
					target_time = cur_time+(long)(1000.0/el.fps);
				} else if (cur_time < target_time + 900.0/el.fps) {
					// we lag behind less than 0.9 frame
					// -> do full frame.
					synchronized (el.objects) {
						doFrameAll();
						el.updateViewOffset();
					}
					canvas.drawAll();
					frames_skipped=0;
					if (cur_time+3 < target_time) {
						//we even have some time left -> sleep it away
						Thread.sleep(target_time-cur_time);
					} else {
						// we don't, just yield to give input handler and
						// painter some time
						Thread.yield();
					}
					target_time += (1000.0/el.fps);
				//} else if (cur_time >
				//target_time + (long)(1000.0*el.maxframeskip/el.fps)) {
				//	// we lag behind more than the max # frames ->
				//	// draw full frame and reset target time
				//	synchronized (el.objects) {
				//		doFrameAll();
				//		el.updateViewOffset();
				//	}
				//	canvas.drawAll();
				//	frames_skipped=0;
				//	// yield to give input handler + painter some time
				//	Thread.yield();
				//	target_time=cur_time + (long)(1000.0/el.fps);
				} else {
					// we lag behind a little -> frame skip
					synchronized (el.objects) {
						doFrameAll();
						el.updateViewOffset();
					}
					// if we skip too many frames in succession, draw a frame
					if ((++frames_skipped) > el.maxframeskip) {
						//canvas.repaint();
						canvas.drawAll();
						frames_skipped=0;
						target_time=cur_time + (long)(1000.0/el.fps);
					} else {
						target_time += (long)(1000.0/el.fps);
					}
					// yield to give input handler some time
					Thread.yield();
				}
			}
		} catch (InterruptedException e) {
			/* exit thread when interrupted */
			System.out.println("JGame thread exited.");
		} catch (Exception e) {
			dbgShowException("MAIN",e);
		} catch (JGameError e) {
			e.printStackTrace();
			exitEngine("Error in main:\n"+dbgExceptionToString(e));
		} }
	}




	/*====== variables from engine ======*/


	/** Should engine thread run or halt? Set by start() / stop()*/
	boolean running=true;


	Graphics buf_gfx=null;


	/*====== platform-dependent variables ======*/


	/* keyboard */

	/** The codes 256-258 are the mouse buttons */
	boolean [] keymap = new boolean [256+3];
	int lastkey=0;
	char lastkeychar=0;
	int wakeup_key=0;



	/*====== images ======*/



	public JGImage getImage(String imgname) {
		return el.getImage(imgname);
	}


	public JGPoint getImageSize(String imgname) {
		return el.getImageSize(imgname);
	}

	public void defineMedia(String filename) {
		el.defineMedia(this,filename);
	}

	public void defineImage(String name, String tilename, int collisionid,
	String imgfile, String img_op,
	int top,int left, int width,int height) {
		el.defineImage(this,name,tilename,collisionid,imgfile,img_op,
			top,left, width,height);
	}

	public void defineImage(String imgname, String tilename, int collisionid,
	String imgfile, String img_op) {
		el.defineImage(this,imgname,tilename,collisionid,imgfile, img_op);
	}

	public void defineImage(String imgname, String tilename, int collisionid,
	String imgmap, int mapidx, String img_op,
	int top,int left, int width,int height) {
		el.defineImage(imgname,tilename,collisionid,  imgmap, mapidx,
			img_op, top,left,width,height );
	}

	public void defineImage(String imgname, String tilename, int collisionid,
	String imgmap, int mapidx, String img_op) {
		el.defineImage(imgname,tilename,collisionid, imgmap, mapidx, img_op);
	}

	public void defineImageRotated(String name, String tilename,
	int collisionid, String srcname, double angle) {
		el.defineImageRotated(this,name,tilename,collisionid, srcname, angle);
	}


	public void defineImageMap(String mapname, String imgfile,
	int xofs,int yofs, int tilex,int tiley, int skipx,int skipy) {
		el.defineImageMap(this,mapname,imgfile, xofs,yofs, tilex,tiley,
			skipx,skipy);
	}

	public JGRectangle getImageBBox(String imgname) {
		return el.getImageBBox(imgname);
	}


	/*====== PF/view ======*/


	/*====== objects from canvas ======*/

	public void markAddObject(JGObject obj) {
		el.markAddObject(obj);
	}

	public boolean existsObject(String index) {
		return el.existsObject(index);
	}

	public JGObject getObject(String index) {
		return el.getObject(index);
	}

	public void moveObjects(String prefix, int cidmask) {
		el.moveObjects(this,prefix, cidmask);
	}

	public void moveObjects() {
		el.moveObjects(this);
	}

	public void checkCollision(int srccid,int dstcid) {
		el.checkCollision(this,srccid,dstcid);
	}

	public int checkCollision(int cidmask, JGObject obj) {
		return el.checkCollision(cidmask,obj);
	}

	public int checkBGCollision(JGRectangle r) {
		return el.checkBGCollision(r);
	}

	public void checkBGCollision(int tilecid,int objcid) {
		el.checkBGCollision(this,tilecid,objcid);
	}

	/* objects from engine */

	public Vector getObjects(String prefix,int cidmask,boolean suspended_obj,
	JGRectangle bbox) {
		return el.getObjects(prefix,cidmask,suspended_obj,
			bbox);
	}

	public void removeObject(JGObject obj) {
		el.removeObject(obj);
	}

	public void removeObjects(String prefix,int cidmask) {
		el.removeObjects(prefix,cidmask);
	}

	public void removeObjects(String prefix,int cidmask,boolean suspended_obj) {
		el.removeObjects(prefix,cidmask,suspended_obj);
	}
	public int countObjects(String prefix,int cidmask) {
		return el.countObjects(prefix,cidmask);
	}

	public int countObjects(String prefix,int cidmask,boolean suspended_obj) {
		return el.countObjects(prefix,cidmask,suspended_obj);
	}


	void drawObject(Graphics g, JGObject o) {
		if (!o.is_suspended) {
			drawImage(g,(int)o.x,(int)o.y,o.getImageName(),true);
			try {
				o.paint();
			} catch (JGameError ex) {
				ex.printStackTrace();
				exitEngine(dbgExceptionToString(ex));
			} catch (Exception e) {
				dbgShowException(o.getName(),e);
			}
		}
		// debug functionality not implemented in midp
	}



	/*====== BG/tiles ======*/

	public void setBGImage(String bgimg) {
		el.setBGImage(bgimg,0,true,true);
	}

	public void setBGImage(int depth, String bgimg,boolean wrapx,boolean wrapy){
		el.setBGImage(bgimg,depth,wrapx,wrapy);
	}


	public void setTileSettings(String out_of_bounds_tile,
	int out_of_bounds_cid,int preserve_cids) {
		el.setTileSettings(out_of_bounds_tile,out_of_bounds_cid,preserve_cids);
	}

	public void fillBG(String filltile) {
		el.fillBG(filltile);
	}

	public void setTileCid(int x,int y,int and_mask,int or_mask) {
		el.setTileCid(x,y,and_mask,or_mask);
	}

	public void setTile(int x,int y,String tilestr) {
		el.setTile(x,y,tilestr);
	}

	void setColor(Graphics g,JGColor col) {
		g.setColor(col.r,col.g,col.b);
	}


	/** xi,yi are tile indexes relative to the tileofs, that is, the top left
	 * of the bg, + 1. They must be within both the tilemap and the view. */
	public void drawTile(int xi,int yi,int tileid) {
		if (background == null) return;
		// determine position within bg
		int x = el.moduloFloor(xi+1,el.viewnrtilesx+3) * el.scaledtilex;
		int y = el.moduloFloor(yi+1,el.viewnrtilesy+3) * el.scaledtiley;
		// draw
		if (bgg==null) bgg = background.getGraphics();
		Integer tileid_obj = new Integer(tileid);
		MIDPImage img = (MIDPImage)el.getTileImage(tileid_obj);
		// define background behind tile in case the tile is null or
		// transparent.
		if (img==null||el.images_transp.containsKey(tileid_obj)) {
			EngineLogic.BGImage bg_image = (EngineLogic.BGImage)
				el.bg_images.elementAt(0);
			if (bg_image==null) {
				setColor(bgg,el.bg_color);
				bgg.fillRect(x,y,el.scaledtilex,el.scaledtiley);
			} else {
				int xtile = el.moduloFloor(xi,bg_image.tiles.x);
				int ytile = el.moduloFloor(yi,bg_image.tiles.y);
				// we use quick getimage here, unlike the jre version
				bgg.drawRegion(((MIDPImage)el.getImage(bg_image.imgname)).img,
					xtile*el.scaledtilex, ytile*el.scaledtiley, 
					el.scaledtilex, el.scaledtiley,
					Sprite.TRANS_NONE,
					x,y, Graphics.TOP|Graphics.LEFT);
			}
		}
		if (img!=null) {
			bgg.drawImage(img.img,x,y,Graphics.TOP|Graphics.LEFT);
		}
		//System.out.println("Drawn tile"+tileid);
	}



	public int countTiles(int tilecidmask) {
		return el.countTiles(tilecidmask);
	}

	public int getTileCid(int xidx,int yidx) {
		return el.getTileCid(xidx,yidx);
	}

	public String getTileStr(int xidx,int yidx) {
		return el.getTileStr(xidx,yidx);
	}

	public int getTileCid(JGRectangle tiler) {
		return el.getTileCid(tiler);
	}

	public JGRectangle getTiles(JGRectangle r) {
		return el.getTiles(r);
	}

	public boolean getTiles(JGRectangle dest,JGRectangle r) {
		return el.getTiles(dest,r);
	}



	public void setTileCid(int x,int y,int value) {
		el.setTileCid(x,y,value);
	}

	public void orTileCid(int x,int y,int or_mask) {
		el.orTileCid(x,y,or_mask);
	}

	public void andTileCid(int x,int y,int and_mask) {
		el.andTileCid(x,y,and_mask);
	}

	public void setTile(JGPoint tileidx,String tilename) {
		el.setTile(tileidx,tilename);
	}

	public void setTiles(int xofs,int yofs,String [] tilemap) {
		el.setTiles(xofs,yofs,tilemap);
	}

	public void setTilesMulti(int xofs,int yofs,String [] tilemap) {
		el.setTilesMulti(xofs,yofs,tilemap);
	}

	public int getTileCidAtCoord(double x,double y) {
		return el.getTileCidAtCoord(x,y);
	}
	public int getTileCid(JGPoint center, int xofs, int yofs) {
		return el.getTileCid(center, xofs, yofs);
	}

	public String getTileStrAtCoord(double x,double y) {
		return el.getTileStrAtCoord(x,y);
	}

	public String getTileStr(JGPoint center, int xofs, int yofs) {
		return el.getTileStr(center, xofs,yofs);
	}

	public int tileStrToID(String tilestr) {
		return el.tileStrToID(tilestr);
	}

	public String tileIDToStr(int tileid) {
		return el.tileIDToStr(tileid);
	}




	/** dx1 and dy1 are coordinates on canvas buffer, without canvas_ofs. */
	void copyBGToBuf(Graphics bufg, int sx1,int sy1,int sx2,int sy2,
	int dx1,int dy1) {
		//dx1 += el.canvas_xofs;
		//dy1 += el.canvas_yofs;
		//System.out.println("("+sx1+","+sy1+")-("+sx2+","+sy2+")");
		if (sx2<=sx1 || sy2<=sy1) return;
		int barrelx = el.scaleXPos(el.moduloFloor(el.xofs,el.tilex),false);
		int barrely = el.scaleYPos(el.moduloFloor(el.yofs,el.tiley),false);
		int barreldx = (sx1==0) ? barrelx : 0;
		int barreldy = (sy1==0) ? barrely : 0;
		barrelx = (sx1==0) ? 0 : barrelx;
		barrely = (sy1==0) ? 0 : barrely;
		int dx2 = dx1 + sx2-sx1;
		int dy2 = dy1 + sy2-sy1;
		// ensure source coordinates are not out of the bounds of the source
		// image
		int sx1e = barrelx+sx1*el.scaledtilex;
		int sy1e = barrely+sy1*el.scaledtiley;
		int sx2e = barrelx+sx2*el.scaledtilex;
		int sx2max = (el.viewnrtilesx+3)*el.scaledtilex;
		if (sx2e > sx2max) sx2e = sx2max;
		int sy2e = barrely+sy2*el.scaledtiley;
		int sy2max = (el.viewnrtilesy+3)*el.scaledtiley;
		if (sy2e > sy2max) sy2e = sy2max;
		//void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest, int anchor)
		bufg.drawRegion(background,
			sx1e,sy1e, sx2e-sx1e, sy2e-sy1e,
			//barrelx+sx1*el.scaledtilex, barrely+sy1*el.scaledtiley,
			//(sx2-sx1)*el.scaledtilex, (sy2-sy1)*el.scaledtiley,
			Sprite.TRANS_NONE,
			dx1*el.scaledtilex-barreldx + el.canvas_xofs,
			dy1*el.scaledtiley-barreldy + el.canvas_yofs,
			Graphics.TOP|Graphics.LEFT);
		//bufg.drawImage(background,
		//	dx1*el.scaledtilex-barreldx, dy1*el.scaledtiley-barreldy,
		//	dx2*el.scaledtilex-barreldx, dy2*el.scaledtiley-barreldy,
		//	barrelx+sx1*el.scaledtilex, barrely+sy1*el.scaledtiley,
		//	barrelx+sx2*el.scaledtilex, barrely+sy2*el.scaledtiley,
		//	this);
	}



	/*====== math ======*/


	public double moduloXPos(double x) {
		return el.moduloXPos(x);
	}

	public double moduloYPos(double y) {
		return el.moduloYPos(y);
	}


	/*====== debug ======*/

	public void dbgShowBoundingBox(boolean enabled) {}

	public void dbgShowGameState(boolean enabled) {}

	public void dbgShowFullStackTrace(boolean enabled) {}

	public void dbgShowMessagesInPf(boolean enabled) {}

	public void dbgSetMessageExpiry(int ticks) {}

	public void dbgSetMessageFont(JGFont font) { }

	public void dbgSetDebugColor1(JGColor col) { }

	public void dbgSetDebugColor2(JGColor col) { }

	public void dbgPrint(String msg) { dbgPrint("MAIN",msg); }

	public void dbgPrint(String source,String msg) {
		System.out.println(source+": "+msg);
	}

	public void dbgShowException(String source, Throwable e) {
		e.printStackTrace();
		//dbgPrint(source,st.toString());
	}

	public String dbgExceptionToString(Throwable e) {
		return e.toString();
	}


	//public void setCanvasSettings(int nrtilesx,int nrtilesy,int tilex,int tiley,
	//Color fgcolor, Color bgcolor, Font msgfont) {
	//	el.setCanvasSettings(nrtilesx,nrtilesy,tilex,tiley,
	//		fgcolor, bgcolor, msgfont);
	//}

	public void requestGameFocus() { }

	// note: these get and set methods do not delegate calls

	public boolean isApplet() { return false; }

	public boolean isMidlet() { return true; }

	public boolean isOpenGL() { return false; }

	public boolean isAndroid() { return false; }

	public int viewWidth() { return el.viewnrtilesx*el.tilex; }
	public int viewHeight() { return el.viewnrtilesy*el.tiley; }

	public int viewTilesX() { return el.viewnrtilesx; }
	public int viewTilesY() { return el.viewnrtilesy; }

	public int viewXOfs() { return el.pendingxofs; }
	public int viewYOfs() { return el.pendingyofs; }

	//public int viewTileXOfs() { return canvas.tilexofs; }
	//public int viewTileYOfs() { return canvas.tileyofs; }

	public int pfWidth() { return el.nrtilesx*el.tilex; }
	public int pfHeight() { return el.nrtilesy*el.tiley; }

	public int pfTilesX() { return el.nrtilesx; }
	public int pfTilesY() { return el.nrtilesy; }

	public boolean pfWrapX() { return el.pf_wrapx; }
	public boolean pfWrapY() { return el.pf_wrapy; }

	public int tileWidth()  { return el.tilex; }
	public int tileHeight() { return el.tiley; }

	public int displayWidth() { return el.winwidth; }
	public int displayHeight() { return el.winheight; }

	public double getFrameRate() { return el.fps; }

	public double getGameSpeed() { return el.gamespeed; }

	public double getFrameSkip() { return el.maxframeskip; }

	public boolean getVideoSyncedUpdate() { return false; }

	public int getOffscreenMarginX() { return el.offscreen_margin_x; }
	public int getOffscreenMarginY() { return el.offscreen_margin_y; }

	public double getXScaleFactor() { return el.x_scale_fac; }
	public double getYScaleFactor() { return el.y_scale_fac; }
	public double getMinScaleFactor() { return el.min_scale_fac; }



	public void setViewOffset(int xofs,int yofs,boolean centered) {
		el.setViewOffset(xofs,yofs,centered);
	}

	public void setBGImgOffset(int depth, double xofs, double yofs,
	boolean centered) { }

	public void setViewZoomRotate(double zoom, double rotate) { }

	public void setPFSize(int nrtilesx,int nrtilesy) {
		el.setPFSize(nrtilesx,nrtilesy);
	}

	public void setPFWrap(boolean wrapx,boolean wrapy,int shiftx,int shifty) {
		el.setPFWrap(wrapx,wrapy,shiftx,shifty);
	}


	public void setFrameRate(double fps, double maxframeskip) {
		el.setFrameRate(fps, maxframeskip);
	}

	public void setVideoSyncedUpdate(boolean value) {}

	public void setGameSpeed(double gamespeed) {
		el.setGameSpeed(gamespeed);
	}

	public void setRenderSettings(int alpha_thresh,JGColor render_bg_col) {
		el.setRenderSettings(alpha_thresh,render_bg_col);
	}

	public void setOffscreenMargin(int xmargin,int ymargin) {
		el.setOffscreenMargin(xmargin,ymargin);
	}


	/** Set global background colour, which is displayed in borders, and behind
	* transparent tiles if no BGImage is defined. */
	public void setBGColor(JGColor bgcolor) {
		el.bg_color=bgcolor;
	}

	/** Set global foreground colour, used for printing text and status
	 * messages.  It is also the default colour for painting */
	public void setFGColor(JGColor fgcolor) { el.fg_color=fgcolor;  }

	/** Set the (unscaled) message font, used for displaying status messages.
	* It is also the default font for painting.  */
	public void setMsgFont(JGFont msgfont) { el.msg_font = msgfont; }

	/** Set foreground and background colour, and message font in one go;
	* passing a null means ignore that argument. */
	public void setColorsFont(JGColor fgcolor,JGColor bgcolor,JGFont msgfont) {
		if (msgfont!=null) el.msg_font = msgfont;
		if (fgcolor!=null) el.fg_color = fgcolor;
		if (bgcolor!=null) setBGColor(bgcolor);
	}

	/** Set parameters of outline surrounding text (for example, used to
	 *  increase contrast).
	 * @param thickness 0 = turn off outline */
	public void setTextOutline(int thickness,JGColor colour) {
		// curiously, I've seen the init screen draw in-between these two
		// statements.  Check of if that's what really happened
		el.outline_colour=colour;
		el.outline_thickness=thickness;
	}

	/** Unimplemented, does nothing. */
	public void setMouseCursor(int cursor) {}

	/** Unimplemented, does nothing. */
	public void setMouseCursor(Object cursor) {}


	/* timers */

	public void removeAllTimers() {
		el.removeAllTimers();
	}

	public void registerTimer(JGTimer timer) {
		el.registerTimer(timer);
	}

	/* game state */

	public void setGameState(String state) {
		el.setGameState(state);
	}

	public void addGameState(String state) {
		el.addGameState(state);
	}

	public void removeGameState(String state) {
		el.removeGameState(state);
	}

	public void clearGameState() {
		el.clearGameState();
	}


	public boolean inGameState(String state) {
		return el.inGameState(state);
	}

	public boolean inGameStateNextFrame(String state) {
		return el.inGameStateNextFrame(state);
	}

	/** Do some administration, call doFrame. */
	private void doFrameAll() {
		//audioNewFrame();
		// the first flush is needed to remove any objects that were created
		// in the main routine after the last moveObjects or checkCollision
		el.flushRemoveList();
		el.flushAddList();
		// tick timers before doing state transitions, because timers may
		// initiate new transitions.
		el.tickTimers();
		el.flushRemoveList();
		el.flushAddList();
		// the game state transition starts here
		el.gamestate.removeAllElements();
		int maxi = el.gamestate_nextframe.size();
		for (int i=0; i<maxi; i++) {
			el.gamestate.addElement(el.gamestate_nextframe.elementAt(i));
		}
		// we assume that state transitions will not initiate new state
		// transitions!
		invokeGameStateMethods("start",el.gamestate_new);
		el.gamestate_new.removeAllElements();
		el.flushRemoveList();
		el.flushAddList();
		try {
			doFrame();
		} catch (JGameError ex) {
			ex.printStackTrace();
			exitEngine(dbgExceptionToString(ex));
		} catch (Exception ex) {
			dbgShowException("MAIN",ex);
		}
		invokeGameStateMethods("doFrame",el.gamestate);
		el.frameFinished();
	}

	private void invokeGameStateMethods(String prefix,Vector states) {
		int maxi = states.size();
		for (int i=0; i<maxi; i++) {
			String state = (String) states.elementAt(i);
			tryMethod(prefix,state);
		}
	}

	Hashtable methodidx=null;

	String [] stateprefixes = new String [] {
		"start", "doFrame", "paintFrame"
	};

	String [] statesuffixes = new String [] {
		"Loader",
		"Title",
		"SelectLevel",
		"Highscores",
		"InGame",
		"StartLevel",
		"StartGame",
		"LevelDone",
		"LifeLost",
		"GameOver",
		"EnterHighscore",
		"Paused",
	};


	/** Try to invoke parameterless method in this object, used for game state
	 * methods.  In MIDP, we don't have
	 * reflection, so we only support a fixed number of methods.  Override
	 * this to add new methods. */
	public void tryMethod(String prefix,String suffix) {
		int prefidx,sufidx;
		for (prefidx=0; prefidx<stateprefixes.length; prefidx++) {
			if (stateprefixes[prefidx].equals(prefix)) break;
		}
		for (sufidx=0; sufidx<statesuffixes.length; sufidx++) {
			if (statesuffixes[sufidx].equals(suffix)) break;
		}
		if (sufidx>=statesuffixes.length)
			exitEngine("Game state "+suffix+" not supported!");
		int idx = statesuffixes.length*prefidx + sufidx;
		//if (methodidx==null) {
		//	methodidx = new Hashtable();
		//	for (int i=0; i<statemethods.length; i++) {
		//		methodidx.put(statemethods[i],new Integer(i));
		//	}
		//}
		//Integer idx_int = (Integer)methodidx.get(name);
		//if (idx_int==null)
		//	exitEngine("Game state method "+name+" not supported!");
		switch (idx) {
		case 0: startLoader(); break;
		case 1: startTitle(); break;
		case 2: startSelectLevel(); break;
		case 3: startHighscores(); break;
		case 4: startInGame(); break;
		case 5: startStartLevel(); break;
		case 6: startStartGame(); break;
		case 7: startLevelDone(); break;
		case 8: startLifeLost(); break;
		case 9: startGameOver(); break;
		case 10: startEnterHighscore(); break;
		case 11: startEnterHighscore(); break;
		case 12: doFrameLoader(); break;
		case 13: doFrameTitle(); break;
		case 14: doFrameSelectLevel(); break;
		case 15: doFrameHighscores(); break;
		case 16: doFrameInGame(); break;
		case 17: doFrameStartLevel(); break;
		case 18: doFrameStartGame(); break;
		case 19: doFrameLevelDone(); break;
		case 20: doFrameLifeLost(); break;
		case 21: doFrameGameOver(); break;
		case 22: doFrameEnterHighscore(); break;
		case 23: doFramePaused(); break;
		case 24: paintFrameLoader(); break;
		case 25: paintFrameTitle(); break;
		case 26: paintFrameSelectLevel(); break;
		case 27: paintFrameHighscores(); break;
		case 28: paintFrameInGame(); break;
		case 29: paintFrameStartLevel(); break;
		case 30: paintFrameStartGame(); break;
		case 31: paintFrameLevelDone(); break;
		case 32: paintFrameLifeLost(); break;
		case 33: paintFrameGameOver(); break;
		case 34: paintFrameEnterHighscore(); break;
		case 35: paintFramePaused(); break;
		default:exitEngine("Game state method "+prefix+suffix+" not supported");
		}
	}

	/** Predefined game state method, implementation is empty. */
	public void startLoader() {}
	/** Predefined game state method, implementation is empty. */
	public void startTitle() {}
	/** Predefined game state method, implementation is empty. */
	public void startSelectLevel() {}
	/** Predefined game state method, implementation is empty. */
	public void startHighscores() {}
	/** Predefined game state method, implementation is empty. */
	public void startInGame() {}
	/** Predefined game state method, implementation is empty. */
	public void startStartLevel() {}
	/** Predefined game state method, implementation is empty. */
	public void startStartGame() {}
	/** Predefined game state method, implementation is empty. */
	public void startLevelDone() {}
	/** Predefined game state method, implementation is empty. */
	public void startLifeLost() {}
	/** Predefined game state method, implementation is empty. */
	public void startGameOver() {}
	/** Predefined game state method, implementation is empty. */
	public void startEnterHighscore() {}
	/** Predefined game state method, implementation is empty. */
	public void startPaused() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameLoader() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameTitle() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameSelectLevel() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameHighscores() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameInGame() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameStartLevel() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameStartGame() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameLevelDone() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameLifeLost() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameGameOver() {}
	/** Predefined game state method, implementation is empty. */
	public void doFrameEnterHighscore() {}
	/** Predefined game state method, implementation is empty. */
	public void doFramePaused() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameLoader() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameTitle() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameSelectLevel() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameHighscores() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameInGame() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameStartLevel() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameStartGame() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameLevelDone() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameLifeLost() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameGameOver() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFrameEnterHighscore() {}
	/** Predefined game state method, implementation is empty. */
	public void paintFramePaused() {}
	


	public void doFrame() {}

	void paintFrame(Graphics g) {
		buf_gfx=g;
		setColor(g,el.fg_color);
		setFont(el.msg_font);
		try {
			paintFrame();
		} catch (JGameError ex) {
			ex.printStackTrace();
			exitEngine(dbgExceptionToString(ex));
		} catch (Exception ex) {
			dbgShowException("MAIN",ex);
		}
		invokeGameStateMethods("paintFrame",el.gamestate);
		//if ((debugflags&GAMESTATE_DEBUG)!=0) {
		//	String state="{";
		//	for (Enumeration e=el.gamestate.elements(); e.hasMoreElements(); ) {
		//		state += (String)e.nextElement();
		//		if (e.hasMoreElements()) state +=",";
		//	}
		//	state += "}";
		//	setFont(el.msg_font);
		//	setColor(g,el.fg_color);
		//	drawString(state,el.viewWidth(),
		//			el.viewHeight()-(int)getFontHeight(g,el.msg_font), 1);
		//}
		//if ((debugflags&MSGSINPF_DEBUG)!=0) paintDbgFrameLogs(buf_gfx);
		buf_gfx=null;
	}

	public void paintFrame() {}

	public Graphics getBufferGraphics() { return buf_gfx; }

	/* some convenience functions for drawing during repaint and paintFrame()*/

	public void setColor(JGColor col) {
		if (buf_gfx!=null) buf_gfx.setColor(col.r,col.g,col.b);
	}

	public void setFont(JGFont font) { setFont(buf_gfx,font); }

	Font smallfont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN,
						Font.SIZE_SMALL);
	Font mediumfont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN,
						Font.SIZE_MEDIUM);
	Font largefont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN,
						Font.SIZE_LARGE);
	Font getFont(JGFont jgfont) {
		// XXX see if we can use fontsize to determine this
		double fontsize = jgfont.size*el.min_scale_fac;
		if (fontsize <= 12) return smallfont;
		if (fontsize <= 18) return mediumfont;
		return largefont;
	}

	public void setFont(Graphics g,JGFont jgfont) {
		g.setFont(getFont(jgfont));
		// not implemented
		//Font font = new Font(jgfont.name,jgfont.style,(int)jgfont.size);
		//if (g!=null) {
		//	double origsize = font.getSize2D();
		//	font=font.deriveFont((float)(origsize*el.min_scale_fac));
		//	g.setFont(font);
		//}
	}

	public void setStroke(double thickness) {
		// thickness not supported in midp
		//Graphics2D g = (Graphics2D) buf_gfx;
		//g.setStroke(new BasicStroke((float)(thickness*el.min_scale_fac)));
	}

	public void setBlendMode(int src_func, int dst_func) { }

	public double getFontHeight(JGFont jgfont) {
		return getFont(jgfont).getHeight();
	}

	void drawImage(Graphics g,double x,double y,String imgname,
	boolean pf_relative) {
		if (imgname==null) return;
		x = el.scaleXPos(x,pf_relative) + el.canvas_xofs;
		y = el.scaleYPos(y,pf_relative) + el.canvas_yofs;
		// we use the quick version unlike the jre engine
		MIDPImage img = (MIDPImage)el.getImage(imgname);
		if (img!=null)
			g.drawImage(img.img,(int)x,(int)y,Graphics.TOP|Graphics.LEFT);
	}


	public void drawLine(double x1,double y1,double x2,double y2,
	double thickness, JGColor color) {
		if (color!=null) setColor(color);
		setStroke(thickness);
		drawLine(x1,y1,x2,y2,true);
	}
	public void drawLine(double x1,double y1,double x2,double y2) {
		drawLine(x1,y1,x2,y2,true);
	}

	public void drawLine(double x1,double y1,double x2,double y2,
	boolean pf_relative) {
		if (buf_gfx==null) return;
		buf_gfx.drawLine(
			el.scaleXPos(x1,pf_relative) + el.canvas_xofs,
			el.scaleYPos(y1,pf_relative) + el.canvas_yofs,
			el.scaleXPos(x2,pf_relative) + el.canvas_xofs,
			el.scaleYPos(y2,pf_relative) + el.canvas_yofs );
	}

	private int [] xpos = new int[3];
	private int [] ypos = new int[3];
	public void drawPolygon(double [] x,double [] y, JGColor [] col,int len,
	boolean filled, boolean pf_relative) {
		if (buf_gfx==null) return;
		xpos[0] = el.scaleXPos(x[0],pf_relative) + el.canvas_xofs;
		ypos[0] = el.scaleYPos(y[0],pf_relative) + el.canvas_yofs;
		xpos[1] = el.scaleXPos(x[1],pf_relative) + el.canvas_xofs;
		ypos[1] = el.scaleYPos(y[1],pf_relative) + el.canvas_yofs;
		if (!filled) {
			// draw first and last line segment
			xpos[2] = el.scaleXPos(x[len-1],pf_relative) + el.canvas_xofs;
			ypos[2] = el.scaleYPos(y[len-1],pf_relative) + el.canvas_yofs;
			if (col!=null) setColor(buf_gfx,col[1]);
			buf_gfx.drawLine(xpos[0],ypos[0],xpos[1],ypos[1]);
			if (col!=null) setColor(buf_gfx,col[0]);
			buf_gfx.drawLine(xpos[2],ypos[2],xpos[0],ypos[0]);
		}
		for (int i=2; i<len; i++) {
			xpos[2] = el.scaleXPos(x[i],pf_relative) + el.canvas_xofs;
			ypos[2] = el.scaleYPos(y[i],pf_relative) + el.canvas_yofs;
			if (col!=null) setColor(buf_gfx,col[i]);
			if (filled) {
				buf_gfx.fillTriangle(xpos[0],ypos[0],xpos[1],ypos[1],xpos[2],ypos[2]);
			} else {
				buf_gfx.drawLine(xpos[1],ypos[1],xpos[2],ypos[2]);
			}
			xpos[1] = xpos[2];
			ypos[1] = ypos[2];
		}
	}

	public void drawRect(double x,double y,double width,double height, boolean filled,
	boolean centered, double thickness, JGColor color) {
		if (color!=null) setColor(color);
		setStroke(thickness);
		drawRect(x,y,width,height,filled,centered,true);
	}

	public void drawRect(double x,double y,double width,double height, boolean filled,
	boolean centered) {
		drawRect(x,y,width,height,filled,centered,true);
	}

	public void drawRect(double x,double y,double width,double height, boolean filled,
	boolean centered, boolean pf_relative) {
		if (buf_gfx==null) return;
		//if (pf_relative) {
		//	x -= canvas.xofs;
		//	y -= canvas.yofs;
		//}
		drawRect(buf_gfx,x,y,width,height,filled,centered,pf_relative);
	}

	public void drawRect(double x,double y,double width,double height,
	boolean filled, boolean centered,boolean pf_relative,
	JGColor [] shadecol) {
		drawRect(buf_gfx,x,y,width,height,filled,centered,pf_relative);
	}

	public void drawRect(double x,double y,double width,double height,
	boolean filled, boolean centered,boolean pf_relative,
	JGColor [] shadecol,String tileimage) {
		drawRect(buf_gfx,x,y,width,height,filled,centered,pf_relative);
	}

	void drawRect(Graphics g,double x,double y,double width,double height,
	boolean filled, boolean centered,boolean pf_relative) {
		if (centered) {
			x -= (width/2);
			y -= (height/2);
		}
		JGRectangle r = el.scalePos(x, y, width, height, pf_relative);
		r.x += el.canvas_xofs;
		r.y += el.canvas_yofs;
		if (filled) {
			g.fillRect(r.x,r.y,r.width,r.height);
		} else {
			g.drawRect(r.x,r.y,r.width,r.height);
		}
	}

	public void drawOval(double x,double y,double width,double height, boolean filled,
	boolean centered, double thickness, JGColor color) {
		if (color!=null) setColor(color);
		setStroke(thickness);
		drawOval(x,y,width,height,filled,centered,true);
	}

	public void drawOval(double x,double y, double width,double height,boolean filled,
	boolean centered) {
		drawOval(x,y,width,height,filled,centered,true);
	}

	public void drawOval(double x,double y, double width,double height,boolean filled,
	boolean centered, boolean pf_relative) {
		if (buf_gfx==null) return;
		x = el.scaleXPos(x,pf_relative) + el.canvas_xofs;
		y = el.scaleYPos(y,pf_relative) + el.canvas_yofs;
		width  = el.scaleXPos(width,false);
		height = el.scaleYPos(height,false);
		if (centered) {
			x -= (width/2);
			y -= (height/2);
		}
		if (filled) {
			buf_gfx.fillArc((int)x,(int)y,(int)width,(int)height,0,360);
		} else {
			buf_gfx.drawArc((int)x,(int)y,(int)width,(int)height,0,360);
		}
	}

	public void drawImage(double x,double y,String imgname) {
		if (buf_gfx==null) return;
		drawImage(buf_gfx,x,y,imgname,true);
	}

	public void drawImage(double x,double y,String imgname,boolean pf_relative) {
		if (buf_gfx==null) return;
		drawImage(buf_gfx,x,y,imgname,pf_relative);
	}

	public void drawImage(double x,double y,String imgname, JGColor blend_col,
	double alpha, double rot, double scale, boolean pf_relative) {
		if (buf_gfx==null) return;
		drawImage(buf_gfx,x,y,imgname,pf_relative);
	}

	/* new versions of drawImage */

	public void drawImage(String imgname,double x,double y) {
		drawImage(x,y,imgname);
	}

	public void drawImage(String imgname,double x,double y,boolean pf_relative){
		drawImage(x,y,imgname,pf_relative);
	}

	public void drawImage(String imgname, double x,double y,
	boolean pf_relative,JGColor blend_col,
	double alpha, double rot, double scale) {
		drawImage(x,y,imgname,blend_col,alpha,rot,scale,pf_relative);
	}


	public void drawString(String str, double x, double y, int align,
	JGFont font, JGColor color) {
		if (font!=null) setFont(font);
		if (color!=null) setColor(color);
		drawString(buf_gfx, str, x,y, align, false);
	}

	public void drawString(String str, double x, double y, int align) {
		drawString(buf_gfx, str, x,y, align, false);
	}

	public void drawString(String str, double x, double y, int align,
	boolean pf_relative) {
		drawString(buf_gfx, str, x,y, align, pf_relative);
	}

	/** Internal function for writing on both buffer and screen.  Coordinates
	 * are always relative to view. */
	void drawString(Graphics g, String str, double x, double y, int align,
	boolean pf_relative) {
		if (g==null) return;
		if (str.equals("")) return;
		x = el.scaleXPos(x,pf_relative) + el.canvas_xofs;
		y = el.scaleYPos(y,pf_relative) + el.canvas_yofs;
		int anchor = Graphics.TOP;
		if (align==-1) anchor |= Graphics.LEFT;
		if (align== 1) anchor |= Graphics.RIGHT;
		if (align== 0) anchor |= Graphics.HCENTER;
		if (el.outline_thickness>0) {
			int origcol = g.getColor();
			setColor(el.outline_colour);
			int real_thickness=Math.max(
				el.scaleXPos(el.outline_thickness,false),1 );
			for (int i=-real_thickness; i<=real_thickness; i++) {
				if (i==0) continue;
				g.drawString(str,(int)x+i,(int)y,anchor);
			}
			for (int i=-real_thickness; i<=real_thickness; i++) {
				if (i==0) continue;
				g.drawString(str,(int)x,(int)y+i,anchor);
			}
			g.setColor(origcol);
		}
		g.drawString(str,(int)x,(int)y,anchor);
	}

	public void drawImageString(String string, double x, double y, int align,
	String imgmap, int char_offset, int spacing) {
		el.drawImageString(this,string,x,y,align,imgmap,char_offset,spacing,
			false);
	}

	public void drawImageString(String string, double x, double y, int align,
	String imgmap, int char_offset, int spacing,boolean pf_relative) {
		el.drawImageString(this,string,x,y,align,imgmap,char_offset,spacing,
		pf_relative);
	}

	/* input */

	/** MIDP has different mouse semantics, based on touch screen. Touch
	 * screens have only press, drag, and release (no mouse move without
	 * button press).  There is only one "button", button 1.  Mouseinside is
	 * set to false when finger/stylus is not on the touch screen.
	 */

	JGPoint mousepos = new JGPoint(0,0);
	boolean [] mousebutton = new boolean[] {false,false,false,false};
	boolean mouseinside=false;

	/** XXX: does not produce a clone of mousepos, for efficiency reasons! */
	public JGPoint getMousePos() { return mousepos; }
	public int getMouseX() { return mousepos.x; }
	public int getMouseY() { return mousepos.y; }

	public boolean getMouseButton(int nr) { return mousebutton[nr]; }
	public void clearMouseButton(int nr) { mousebutton[nr]=false; }
	public void setMouseButton(int nr) { mousebutton[nr]=true; }
	public boolean getMouseInside() { return mouseinside; }

	public boolean getKey(int key) { return keymap[key]; }
	public void clearKey(int key) { keymap[key]=false; }
	public void setKey(int key) { keymap[key]=true; }

	public int getLastKey() { return lastkey; }
	public char getLastKeyChar() { return lastkeychar; }
	public void clearLastKey() {
		lastkey=0;
		lastkeychar=0;
	}

	/* maybe move the translations? Should they be system dependent? */

	/** Get a printable string describing the key. Non-static version for the
	 * sake of the interface. */
	public String getKeyDesc(int key) { return getKeyDescStatic(key); }
	/** Get a printable string describing the key. */
	public static String getKeyDescStatic(int key) {
		if (key==32) return "#";
		if (key==0) return "(none)";
		if (key==KeyEnter) return "*";
		if (key==KeyStar) return "*";
		if (key==' ') return "#";
		if (key==KeyPound) return "#";
		//if (key==KeyEsc) return "escape";
		if (key==KeyUp) return "cursor up";
		if (key==KeyDown) return "cursor down";
		if (key==KeyLeft) return "cursor left";
		if (key==KeyRight) return "cursor right";
		if (key==KeyShift) return "fire";
		//if (key==KeyAlt) return "alt";
		//if (key==KeyCtrl) return "control";
		//if (key==KeyMouse1) return "left mouse button";
		//if (key==KeyMouse2) return "middle mouse button";
		//if (key==KeyMouse3) return "right mouse button";
		//if (key==27) return "escape";
		if (key >= 33 && key <= 95)
			return new String(new char[] {(char)key});
		return "keycode "+key;
	}

	/** Obtain key code from printable string describing the key, the inverse
	 * of getKeyDesc. The string is trimmed and lowercased. Non-static version
	 * for the sake of the interface. */
	public int getKeyCode(String keydesc) { return getKeyCodeStatic(keydesc); }
	/** Obtain key code from printable string describing the key, the inverse
	 * of getKeyDesc. The string is trimmed and lowercased. */
	public static int getKeyCodeStatic(String keydesc) {
		// tab, enter, backspace, insert, delete, home, end, pageup, pagedown
		// escape
		keydesc = keydesc.toLowerCase().trim();
		//if (keydesc.equals("space")) {
		//	return 32;
		//} else if (keydesc.equals("escape")) {
		//	return KeyEsc;
		//} else 
		if (keydesc.equals("(none)")) {
			return 0;
		//} else if (keydesc.equals("enter")) {
		//	return KeyEnter;
		} else if (keydesc.equals("cursor up")) {
			return KeyUp;
		} else if (keydesc.equals("cursor down")) {
			return KeyDown;
		} else if (keydesc.equals("cursor left")) {
			return KeyLeft;
		} else if (keydesc.equals("cursor right")) {
			return KeyRight;
		//} else if (keydesc.equals("shift")) {
		//	return KeyShift;
		} else if (keydesc.equals("fire")) {
			return KeyFire;
		} else if (keydesc.equals("star")) {
			return '*';
		} else if (keydesc.equals("pound")) {
			return '#';
		//} else if (keydesc.equals("alt")) {
		//	return KeyAlt;
		//} else if (keydesc.equals("control")) {
		//	return KeyCtrl;
		//} else if (keydesc.equals("left mouse button")) {
		//	return KeyMouse1;
		//} else if (keydesc.equals("middle mouse button")) {
		//	return KeyMouse2;
		//} else if (keydesc.equals("right mouse button")) {
		//	return KeyMouse3;
		} else if (keydesc.startsWith("keycode")) {
			return Integer.parseInt(keydesc.substring(7));
		} else if (keydesc.length() == 1) {
			return keydesc.charAt(0);
		}
		return 0;
	}

	public boolean hasAccelerometer() { return false; }

	public double getAccelX() {
		return 0;
	}
	public double getAccelY() {
		return 0;
	}
	public double getAccelZ() {
		return 1;
	}

	public double [] getAccelVec() {
		return new double[] { 0,0,1 };
	}


	/*====== animation ======*/

	public void defineAnimation (String id,
	String [] frames, double speed) {
		el.defineAnimation(id,frames,speed);
	}

	public void defineAnimation (String id,
	String [] frames, double speed, boolean pingpong) {
		el.defineAnimation(id,frames, speed, pingpong);
	}

	public Animation getAnimation(String id) {
		return el.getAnimation(id);
	}

	public String getConfigPath(String filename) {
		return null;
		// not implemented yet.  midp should use the special configuration
		// features for this.
	}

	public int invokeUrl(String url,String target) {
		try {
			if (platformRequest(url)) {
				// XXX signal to user that applet must be quit first
			}
			return 1;
		} catch (ConnectionNotFoundException e) {
			return 0;
		}
	}

//	void paintExitMessage(Graphics g) { try {
//		setFont(g,debugmessage_font);
//		int height = (int) (getFontHeight(g,null) / el.y_scale_fac);
//		// clear background
//		setColor(g,el.bg_color);
//		drawRect(g, el.viewWidth()/2, el.viewHeight()/2,
//			9*el.viewWidth()/10, height*5, true,true, false);
//		setColor(g,debug_auxcolor2);
//		// draw colour bars
//		drawRect(g, el.viewWidth()/2, el.viewHeight()/2 - 5*height/2,
//			9*viewWidth()/10, 5, true,true, false);
//		drawRect(g, el.viewWidth()/2, el.viewHeight()/2 + 5*height/2,
//			9*viewWidth()/10, 5, true,true, false);
//		setColor(g,el.fg_color);
//		int ypos = el.viewHeight()/2 - 3*height/2;
//		StringTokenizer toker = new StringTokenizer(el.exit_message,"\n");
//		while (toker.hasMoreTokens()) {
//			drawString(g,toker.nextToken(),el.viewWidth()/2,ypos,0, false);
//			ypos += height+1;
//		}
//	} catch(java.lang.NullPointerException e) {
//		// this sometimes happens during drawString when the applet is exiting
//		// but calls repaint while the graphics surface is already disposed.
//		// See also bug 4791314:
//		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4791314
//	} }




	/* computation */

	public boolean and(int value, int mask) {
		return el.and(value, mask);
	}

	public double random(double min, double max) {
		return el.random(min, max);
	}

	public double random(double min, double max, double interval) {
		return el.random(min, max, interval);
	}

	public int random(int min, int max, int interval) {
		return el.random(min, max, interval);
	}

	/** Short and fast Math.atan2 replacement.  Average numerical error is
	 * less than 0.001 radians, maximum error is 0.005 radians. */
	public double atan2(double y,double x) {
		boolean xneg = x<0;
		boolean yneg = y<0;
		if (xneg) x = -x;
		if (yneg) y = -y;
		double res,r;
		if (x>y) { // 0 to 45 deg
			r = y/x;
		} else { //45 to 90 deg
			r = x/y;
		}
		// taken from http://www.restena.lu/convict/Jeunes/Math/arctan.htm
		// y = -0.30097 + 0.61955*x - 0.001659*x*x
		// x is between 0 and 100%, y between 0 and 45 degrees
		res = -0.30097 + 61.955*r - 16.59*r*r;
		// convert x/y angle back to y/x
		if (y>x) res = 90 - res;
		// convert degrees to radians (we should incorporate this into the
		// equation above one day)
		//res = (res/45) * Math.PI*0.25;
		res *= 0.017453293;
		if (xneg && yneg) return -Math.PI+res;
		if (xneg) return Math.PI-res;
		if (yneg) return -res;
		return res;
	}

	public JGPoint getTileIndex(double x, double y) {
		return el.getTileIndex(x, y);
	}

	public JGPoint getTileCoord(int tilex, int tiley) {
		return el.getTileCoord(tilex, tiley);
	}

	public JGPoint getTileCoord(JGPoint tileidx) {
		return el.getTileCoord(tileidx);
	}

	public double snapToGridX(double x, double gridsnapx) {
		return el.snapToGridX(x, gridsnapx);
	}

	public double snapToGridY(double y, double gridsnapy) {
		return el.snapToGridY(y, gridsnapy);
	}

	public void snapToGrid(JGPoint p,int gridsnapx,int gridsnapy) {
		el.snapToGrid(p,gridsnapx,gridsnapy);
	}

	public boolean isXAligned(double x,double margin) {
		return el.isXAligned(x,margin);
	}

	public boolean isYAligned(double y,double margin) {
		return el.isYAligned(y,margin);
	}

	public double getXAlignOfs(double x) {
		return el.getXAlignOfs(x);
	}

	public double getYAlignOfs(double y) {
		return el.getYAlignOfs(y);
	}

	// XXX please test these two methods

	public double getXDist(double x1, double x2) {
		return el.getXDist(x1, x2);
	}

	public double getYDist(double y1, double y2) {
		return el.getYDist(y1, y2);
	}

	/* We assume here that Midp phones do not typically support playing more
	* than 1 sample or 1 midi file at once.  So there is only 1 channel. */
	// see also:
	//http://wiki.forum.nokia.com/index.php/How_to_handle_multiple_sounds_in_Java_ME

	boolean audioenabled=true;

	Hashtable players = new Hashtable();
	Player cur_player = null;
	String cur_sample=null;
	boolean cur_sample_looping=false;
	String looping_clip_while_disabled=null;

	// XXX volume control is fixed
	int soundvolume=50;

	PlayerListener playerlist = new PlayerListener() {
		public void playerUpdate(Player player, String event, Object eventData){
			if (event.equals(PlayerListener.STARTED)) {
				((VolumeControl)player.getControl("VolumeControl"))
					.setLevel(soundvolume);
			}
		}
	};

	public void enableAudio() {
		if (audioenabled) return;
		audioenabled=true;
		if (looping_clip_while_disabled!=null) {
			playAudio(looping_clip_while_disabled);
		}
		//if (cur_sample_looping && cur_player!=null) {
		//	if (cur_player.getState()==Player.STARTED) return;
		//	try {
		//		cur_player.start();
		//	} catch (javax.microedition.media.MediaException e) {
		//		// XXX we should ignore this exception for platforms that cannot
		//		// handle sound or samples
		//		throw new JGameError(e.toString());
		//	}
		//}
		looping_clip_while_disabled=null;
	}
	public void disableAudio() {
		if (!audioenabled) return;
		audioenabled=false;
		stopAudio();
	}
	public void defineAudioClip(String clipid,String filename) {
		el.defineAudioClip(this,clipid,filename);
	}
	public String lastPlayedAudio(String channel) { return cur_sample; }
	public void playAudio(String clipid) {
		playAudio(null,clipid,false);
	}
	public void playAudio(String channel,String clipid,boolean loop) {
		if (loop)
			looping_clip_while_disabled=clipid;
		if (!audioenabled) return;
		try {
			if (cur_player!=null) {
				// player still running -> do not retrigger, this may yield
				// errors or sound delays
				if (cur_player.getState()==Player.STARTED) return;
				// player stopped and new player not equal to old player
				//    -> free audio device
				// However, I did not see a latency improvement when not
				// deallocating a player but reusing the just stopped one.
				if (cur_player != players.get(clipid)) cur_player.deallocate();
				cur_player=null;
			}
			cur_player = (Player)players.get(clipid);
			if (cur_player==null) {
				String resname = (String)el.audioclips.get(clipid);
				boolean is_wav = resname.toLowerCase().endsWith("wav");
				boolean is_mid = resname.toLowerCase().endsWith("mid")
						      || resname.toLowerCase().endsWith("midi");
				if (is_wav || is_mid) {
					InputStream in = getClass().getResourceAsStream(resname);
					cur_player = Manager.createPlayer(in,
						is_wav? "audio/x-wav" : "audio/midi");
					cur_player.addPlayerListener(playerlist);
					players.put(clipid,cur_player);
				}
			}
			if (cur_player!=null) {
				// looping audio can only be stopped with stopAudio!
				if (loop) cur_player.setLoopCount(-1);
				try {
					cur_player.start();
				} catch (MediaException e) {
					/* ignore, I found this is thrown for no good reason on
					 * Vairy Touch II when starting audio just after it
					 * was stopped */ 
				}
				cur_sample_looping=loop;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// XXX we should ignore this exception for platforms that cannot
			// handle sound or samples
			throw new JGameError(e.toString());
		}
	}
	public void stopAudio(String channel) { stopAudio(); }
	public void stopAudio() {
		if (cur_player!=null) cur_player.deallocate();
		looping_clip_while_disabled=null;
	}



	private static final int STORE_INT=0;
	private static final int STORE_DOUBLE=1;
	private static final int STORE_STRING=2;

	/* id -> Object */
	Hashtable store_data = new Hashtable();
	/* id -> number */
	Hashtable store_ids = new Hashtable();

	/** is initialised by storeInit */
	RecordStore store_impl = null;

	private void storeInit() {
		try {
			store_impl = RecordStore.openRecordStore("JGameStore",true);
			RecordEnumeration e = store_impl.enumerateRecords(null,null,false);
			while (e.hasNextElement()) {
				int recid = e.nextRecordId();
				storeGetRecord(recid);
			}
		} catch (Exception e) {
			// XXX remove this. In production code, ignore store errors
			throw new Error("Error initing store!");
		}
	}

	private void storeGetRecord(int recid) {
		try {
			byte [] data = store_impl.getRecord(recid);
			ByteArrayInputStream datai = new ByteArrayInputStream(data);
			DataInputStream dataib = new DataInputStream(datai);
			String id = dataib.readUTF();
			store_ids.put(id,new Integer(recid));
			int type = dataib.readInt();
			if (type==STORE_INT) {
				store_data.put(id,new Integer(dataib.readInt()));
			} else if (type==STORE_DOUBLE) {
				store_data.put(id,new Double(dataib.readDouble()));
			} else if (type==STORE_STRING) {
				store_data.put(id,dataib.readUTF());
			} else {
				throw new Error("Unexpected store record type "+type);
			}
			dataib.close();
		} catch (Exception e) {
			// XXX remove this. In production code, ignore store errors
			throw new Error("Error reading store!");
		}
	}

	private void storePutRecord(String id,Object value) {
		try {
			Integer recido = (Integer)store_ids.get(id);
			int recid=-1;
			if (recido!=null) recid = recido.intValue();
			//	recid = store_impl.getNextRecordId();
			ByteArrayOutputStream bout= new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream(bout);
			dout.writeUTF(id);
			if (value instanceof Integer) {
				dout.writeInt(STORE_INT);
				dout.writeInt(((Integer)value).intValue());
			} else if (value instanceof Double) {
				dout.writeInt(STORE_DOUBLE);
				dout.writeDouble(((Double)value).doubleValue());
			} else if (value instanceof String) {
				dout.writeInt(STORE_STRING);
				dout.writeUTF((String)value);
			} else {
				throw new Error("Unsupported store class "+value.getClass());
			}
			dout.flush();
			if (recid==-1) {
				recid = store_impl.addRecord(bout.toByteArray(),0,bout.size());
				store_ids.put(id,new Integer(recid));
			} else {
				store_impl.setRecord(recid,bout.toByteArray(),0,bout.size());
			}
			store_data.put(id,value);
			dout.close();
		} catch (Exception e) {
			// XXX remove this. In production code, ignore store errors
			throw new Error("Error writing store!");
		}
	}

	public void storeWriteInt(String id,int value) {
		storePutRecord(id,new Integer(value));
	}

	public void storeWriteDouble(String id,double value) {
		storePutRecord(id,new Double(value));
	}

	public void storeWriteString(String id,String value) {
		storePutRecord(id,value);
	}

	public void storeRemove(String id) {
		try {
			if (store_ids.containsKey(id)) {
				int recid = ((Integer)store_ids.get(id)).intValue();
				store_ids.remove(id);
				store_data.remove(id);
				store_impl.deleteRecord(recid);
			}
		} catch (Exception e) {
			// XXX remove this. In production code, ignore store errors
			throw new Error("Error removing record from store!");
		}
	}

	public boolean storeExists(String id) {
		return store_ids.containsKey(id);
	}

	public int storeReadInt(String id,int undef) {
		Integer ret = (Integer)store_data.get(id);
		if (ret==null) return undef;
		return ret.intValue();
	}

	public double storeReadDouble(String id,double undef) {
		Double ret = (Double)store_data.get(id);
		if (ret==null) return undef;
		return ret.doubleValue();
	}

	public String storeReadString(String id,String undef) {
		String ret = (String)store_data.get(id);
		if (ret==null) return undef;
		return ret;
	}

	/*====== options ======*/

	public void optsAddTitle(String title) {}

	public void optsAddNumber(String varname,String title,String desc,
	int decimals, double lower,double upper,double step, double initial) {}

	public void optsAddBoolean(String varname,String title,String desc,
	boolean initial) {}

	public void optsAddEnum(String varname,String title,String desc,
	String [] values, int initial) {}

	public void optsAddKey(String varname,String title,String desc,int initial){
	}

	public void optsAddString(String varname,String title,String desc,
	int maxlen, boolean isPassword, String initial) {}

	public void optsClear() {}


}

