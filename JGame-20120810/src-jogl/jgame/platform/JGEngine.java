package jgame.platform;
import jgame.impl.*;
import jgame.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;
import java.applet.*;
import java.awt.event.*;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import java.nio.IntBuffer;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.Animator;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import com.sun.opengl.util.j2d.TextureRenderer;
import com.sun.opengl.util.j2d.TextRenderer;

import com.sun.opengl.util.texture.*;

import java.awt.event.*;

import javax.sound.sampled.*;

public abstract class JGEngine extends Applet implements JGEngineInterface {


	JREImage imageutil = new JREImage();

	EngineLogic el = new EngineLogic(imageutil,false,false);
	JREEngine jre = new JREEngine(el,this);


	private Thread thread=null;
	Animator anim=null;

	GLU glu = new GLU();

	JGCanvas canvas=null;

	double viewzoom = 1.0;
	double viewrotate = 0.0;
	// unproject information for translating mouse pos
	//boolean up_initialised=false;
    //int up_viewport[] = new int[4];
    //double up_mvmatrix[] = new double[16];
    //double up_projmatrix[] = new double[16];
	//double up_wcoord[] = new double[4];

	/** Should engine thread run or halt? Set by start() / stop()*/
	boolean running=true;


	/** True means the game is running as an applet, false means
	 * application. */
	boolean i_am_applet=false;

	///** Keycode of cursor key. */
	//public static final int KeyUp=38,KeyDown=40,KeyLeft=37,KeyRight=39;
	//public static final int KeyShift=16;
	//public static final int KeyCtrl=17;
	//public static final int KeyAlt=18;
	//public static final int KeyEsc=27;
	//public static final int KeyEnter=10;
	public static final int KeyBackspace=KeyEvent.VK_BACK_SPACE;
	public static final int KeyTab=KeyEvent.VK_TAB;
	///** Keymap equivalent of mouse button. */
	//public static final int KeyMouse1=256, KeyMouse2=257, KeyMouse3=258;
	
	/*====== platform-dependent variables ======*/

	/** indicates if game update should be driven by video synced GL Animator
	 * rather than timer driven JGEngineThread */
	boolean gl_driven_update=false;

	/** enable use of NPOT if available. */
	boolean enable_npot=false;


	/* images */


	/* screen state */

	TextureRenderer background=null;

	TextureRenderer buffer=null;

	// cached Graphics objects
	Graphics2D bgg=null;
	Graphics2D bufg=null;

	/** graphics to use for draw functions; null means we're not in paint
	 * mode. */
	Graphics buf_gfx=null;
	/** GL context to use for draw functions; null means we're not in paint
	 * mode. */
	GL cur_gl=null;

	/*====== images ======*/


	public JGImage getImage(String imgname) {
		return el.getImage(imgname);
	}


	public JGPoint getImageSize(String imgname) {
		return el.getImageSize(imgname);
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

	public void defineMedia(String filename) {
		el.defineMedia(this,filename);
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


	/** we assume cur_gl == gl */
	void drawObject(Graphics g, GL gl, JGObject o) {
		if (!o.is_suspended) {
			// XXX x and y are rounded here on all platforms
			// may look nicer if not rounded
			drawImage(gl,(int)o.x,(int)o.y,o.getImageName(),
				null,1.0, 0,1.0, true);
			try {
				o.paint();
			} catch (JGameError ex) {
				exitEngine(dbgExceptionToString(ex));
			} catch (Exception e) {
				dbgShowException(o.getName(),e);
			}
		}
		// note that the debug bbox of suspended objects will be visible
		if ((debugflags&JGEngine.BBOX_DEBUG)!=0) {
			JGColor oldcol = cur_gl_color;
			double oldstroke = cur_gl_stroke;
			setStroke(1);
			setColor(el.fg_color);
			JGRectangle bbox = o.getBBox();
			if (bbox!=null) { // bounding box defined
				//bbox = el.scalePos(bbox,true);
				drawRect(bbox.x,bbox.y,bbox.width,bbox.height,false,false);
			}
			bbox = o.getTileBBox();
			if (bbox!=null) { // tile bounding box defined
				//bbox = el.scalePos(bbox,true);
				drawRect(bbox.x,bbox.y,bbox.width,bbox.height,false,false);
				setColor(debug_auxcolor1);
				bbox = o.getTileBBox();
				bbox = getTiles(bbox);
				bbox.x *= el.tilex;
				bbox.y *= el.tiley;
				bbox.width *= el.tilex;
				bbox.height *= el.tiley;
				//bbox = el.scalePos(bbox,true);
				drawRect(bbox.x,bbox.y,bbox.width,bbox.height,false,false);
				setColor(debug_auxcolor2);
				bbox = o.getCenterTiles();
				bbox.x *= el.tilex;
				bbox.y *= el.tiley;
				bbox.width *= el.tilex;
				bbox.height *= el.tiley;
				//bbox = el.scalePos(bbox,true);
				drawRect(bbox.x+2,bbox.y+2,bbox.width-4,bbox.height-4,
					false,false);
			}
			setColor(oldcol);
			setStroke(oldstroke);
		}
		//o.frameFinished();
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
		cur_gl_color=col;
		col.impl=new Color(col.r,col.g,col.b);
		g.setColor((Color)col.impl);
	}

	void setColor(GL gl, JGColor col) {
		cur_gl_color=col;
		//System.out.println("r:"+col.r+" a:"+col.alpha);
		gl.glColor4d(col.r/255.0,col.g/255.0,col.b/255.0,col.alpha/255.0);
	}



	/** Internal draw method, do not call directly.  Use setTile instead.
	* xi,yi are tile indexes relative to the tileofs, that is, the top left
	* of the bg, + 1. They must be within both the tilemap and the view. */
	public void drawTile(int xi,int yi,int tileid) {
		if (background == null) return;
		// determine position within bg
		int x = el.moduloFloor(xi+1,el.viewnrtilesx+3) * el.scaledtilex;
		int y = el.moduloFloor(yi+1,el.viewnrtilesy+3) * el.scaledtiley;
		// draw
		if (bgg==null) {
			bgg = background.createGraphics(); //XXX when to dispose?
			bgg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
		}
		Integer tileid_obj = new Integer(tileid);
		JREImage img = (JREImage)el.getTileImage(tileid_obj);
		// define background behind tile in case the tile is null or
		// transparent.
		if (img==null||el.images_transp.containsKey(tileid_obj)) {
			// background image is drawn as separate texture
			//EngineLogic.BGImage bg_image = (EngineLogic.BGImage)
			//	el.bg_images.get(0);
			//if (bg_image==null) {
				bgg.setColor(
					new Color(el.bg_color.r,el.bg_color.g,el.bg_color.b,0));
				bgg.fillRect(x,y,el.scaledtilex,el.scaledtiley);
			//} else {
			//	int xtile = el.moduloFloor(xi,bg_image.tiles.x);
			//	int ytile = el.moduloFloor(yi,bg_image.tiles.y);
			//	bgg.drawImage(((JREImage)el.getImage(bg_image.imgname)).img,
			//		x, y, x+el.scaledtilex, y+el.scaledtiley,
			//		xtile*el.scaledtilex, ytile*el.scaledtiley, 
			//		(xtile+1)*el.scaledtilex, (ytile+1)*el.scaledtiley,
			//		(Color)el.bg_color.impl,
			//		null);
			//}
		}
		if (img!=null) bgg.drawImage(img.img,x,y,this);
		background.markDirty(x,y,el.scaledtilex,el.scaledtiley);
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




//	void copyBGToBuf(Graphics bufg, int sx1,int sy1,int sx2,int sy2,
//	int dx1,int dy1) {
//		//System.out.println("("+sx1+","+sy1+")-("+sx2+","+sy2+")");
//		if (sx2<=sx1 || sy2<=sy1) return;
//		int barrelx = el.scaleXPos(el.moduloFloor(el.xofs,el.tilex),false);
//		int barrely = el.scaleYPos(el.moduloFloor(el.yofs,el.tiley),false);
//		int barreldx = (sx1==0) ? barrelx : 0;
//		int barreldy = (sy1==0) ? barrely : 0;
//		barrelx = (sx1==0) ? 0 : barrelx;
//		barrely = (sy1==0) ? 0 : barrely;
//		int dx2 = dx1 + sx2-sx1;
//		int dy2 = dy1 + sy2-sy1;
//		//bufg.drawImage(background,
//		//	dx1*el.scaledtilex-barreldx, dy1*el.scaledtiley-barreldy,
//		//	dx2*el.scaledtilex-barreldx, dy2*el.scaledtiley-barreldy,
//		//	barrelx+sx1*el.scaledtilex, barrely+sy1*el.scaledtiley,
//		//	barrelx+sx2*el.scaledtilex, barrely+sy2*el.scaledtiley,
//		//	this);
//	}



	/*====== math ======*/


	public double moduloXPos(double x) {
		return el.moduloXPos(x);
	}

	public double moduloYPos(double y) {
		return el.moduloYPos(y);
	}



	public void setProgressBar(double pos) {
		canvas.setProgressBar(pos);
	}

	public void setProgressMessage(String msg) {
		canvas.setProgressMessage(msg);
	}

	public void setAuthorMessage(String msg) {
		canvas.setAuthorMessage(msg);
	}

	/** JGCanvas is internally used by JGEngine for updating and drawing objects
	 * and tiles, and handling keyboard/mouse events. 
	 */
	class JGCanvas extends GLCanvas implements GLEventListener {

		// GL methods

		/** Called by drawable to indicate mode or device has changed */
		public void displayChanged (GLAutoDrawable drawable,
		boolean modeChanged,boolean deviceChanged) {
			System.out.println ("glcanvas displayChanged()");
		}
		/** Called after OpenGL is init'ed */
		public void init(GLAutoDrawable drawable) {
			System.out.println ("glcanvas init()");
			GL gl = drawable.getGL();
			gl.setSwapInterval(1);
			// set erase color
			gl.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f ); //blk
			// set drawing color and point size
			gl.glColor3f( 1.0f, 1.0f, 1.0f ); 
			gl.glPointSize(1.0f); //a 'dot' is 1 by 1 pixel
			// setting "nicest" for everything slows down to a crawl on some
			// machines.  So we leave defaults, assuming they will be right
			// for accelerated graphics.
			//gl.glEnable(GL.GL_LINE_SMOOTH); // antialiased line drawing
			//gl.glEnable(GL.GL_POLYGON_SMOOTH); // antialiased line drawing
			//gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
			//gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
			//gl.glHint(GL.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
			//gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
			if (gl_driven_update && anim==null) {
				// make sure animator runs, in case gl_driven_update is set
				// initially.  Otherwise, setVideoSyncedUpdate will start the
				// animator through a call to display().
				anim = new Animator(drawable);
				anim.start();
				prev_gl_time = System.currentTimeMillis();
			}
		}

		int w=250,h=250;

		/** Called to indicate the drawing surface has been moved and/or
		* resized */
		public void reshape(GLAutoDrawable drawable,
		int x, int y, int width, int height) {
			System.out.println ("glcanvas reshape()");
			GL gl = drawable.getGL(); 
			GLU glu = new GLU(); 
			gl.glViewport( 0, 0, width, height ); 
			w = width;
			h = height;
			gl.glMatrixMode( GL.GL_PROJECTION );  
			gl.glLoadIdentity(); 
			glu.gluOrtho2D( 0.0, width, 0.0, height); 
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		long prev_gl_time = 0;

		double max_gl_refresh_rate=95.0;

		/** Called by drawable to initiate drawing  */
		public void display(GLAutoDrawable drawable) {
		//System.out.println ("display()");
		GL gl = drawable.getGL();
		GLU glu = new GLU();
		// reset ortho viewmode
		gl.glMatrixMode( GL.GL_PROJECTION );  
		gl.glLoadIdentity(); 
		glu.gluOrtho2D( 0.0, w, 0.0, h); 
		gl.glMatrixMode(GL.GL_MODELVIEW);
		// set default state
		gl.glLoadIdentity();
		modelview_matrix_pf_relative=false;
		// XXX alpha 1 or bg_color.alpha?
		gl.glClearColor(el.bg_color.r, el.bg_color.g, el.bg_color.b, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glShadeModel(GL.GL_SMOOTH);
		if (!is_initialised) {
			if (initpainter!=null) {
				initpainter.getListCellRendererComponent(null,
						gl,0,false,false);
			}
			return;
		}
		if (el.is_exited) {
			paintExitMessage(gl);
			return;
		}
		synchronized (el.objects) {
			if (gl_driven_update && running) {
				// start animator if not yet started
				if (anim==null) {
					anim = new Animator(drawable);
					anim.start();
					prev_gl_time = System.currentTimeMillis();
				}
				double w = el.width;
				double h = el.height;
				// get unproject info for translating mouse pos
				/*gl.glFlush();
				gl.glGetIntegerv(GL.GL_VIEWPORT, up_viewport, 0);
				gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, up_mvmatrix, 0);
				gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, up_projmatrix, 0);
				up_initialised=true;*/
				// determine game speed
				long cur_gl_time = System.currentTimeMillis();
				long target_time = prev_gl_time+(long)(1000.0/el.fps);
				long elapsed = cur_gl_time - prev_gl_time;
				double elapsed_ideal = 1000.0/el.fps;
				double elapsed_min = 1000.0/max_gl_refresh_rate;
				double elapsed_max = (1000.0/el.fps)*(1+el.maxframeskip);
				double gamespeed = elapsed/elapsed_ideal;
				double gamespeed_min = elapsed_min/elapsed_ideal;
				double gamespeed_max = elapsed_max/elapsed_ideal;
				//System.out.println(""+gamespeed+" max"+gamespeed_max+" min"+
				//	gamespeed_min);
				if (gamespeed<gamespeed_min) {
					gamespeed = gamespeed_min;
					long ms_too_fast = (long)(elapsed_min - elapsed);
					if (ms_too_fast > 2) {try {
						Thread.sleep(ms_too_fast);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} }
				}
				if (gamespeed>gamespeed_max) {
					gamespeed = gamespeed_max;
					// we accept that the game slows down
				}
				setGameSpeed(gamespeed);
				prev_gl_time=cur_gl_time;
				if ((debugflags&MSGSINPF_DEBUG)!=0) refreshDbgFrameLogs();
				doFrameAll();
				el.updateViewOffset();
			}
			// update parallax layers below background
			setupModelviewMatrix(gl,true);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glColor4f(1,1,1,1);
			//gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			double x = 0;
			double y = 0;
			double w = el.width;
			double h = el.height;
			Texture tex;
			TextureCoords tc;
			for (int i=el.bg_images.size()-1; i>=0; i--) {
				EngineLogic.BGImage bg_image = (EngineLogic.BGImage)
					el.bg_images.get(i);
				if (bg_image==null) continue;
				JREImage bg_image_i = (JREImage)el.getImage(bg_image.imgname);
				tex = getTexture(bg_image_i);
				tc = tex.getImageTexCoords();
				// scale to POT if coords not [0,1]
				// XXX in some implementations tc.right/tc.bottom return large
				// integers (equal image size) instead of values between 0
				// and 1.
				// In fact, it only does so when the texture is NPOT.  This
				// must be a bug in Texture.  So, we test for unequal to 1.0
				// rather than less than 1.0.
				if (Math.abs(tc.right()-tc.left()) != 1.0
				||  Math.abs(tc.bottom()-tc.top()) != 1.0
				||  !enable_npot) {
					tex = getPOTStretchedTexture(bg_image_i);
					tc = tex.getImageTexCoords();
				//} else {
				//	System.out.println("lr"+tc.left()+" "+tc.right());
				//	System.out.println("tb"+tc.top()+" "+tc.bottom());
				}
				tex.bind();
				tex.enable();
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					bg_image.wrapx ? GL.GL_REPEAT : GL.GL_CLAMP);
				gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					bg_image.wrapy ? GL.GL_REPEAT : GL.GL_CLAMP);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
					GL.GL_LINEAR);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
					el.smooth_magnify ? GL.GL_LINEAR : GL.GL_NEAREST);
				// determine offset and size
				JGPoint imgsize = bg_image_i.getSize();
				double uscale,vscale, xofs,yofs, xsize,ysize;
				double x1=0,y1=0, xs=w,ys=h;
				uscale = 1.0 / imgsize.x;
				if (bg_image.wrapx) {
					xofs = bg_image.xofs * uscale;
					xsize = w * uscale / el.x_scale_fac;
				} else {
					x1 = -bg_image.xofs * el.x_scale_fac;
					xs = imgsize.x * el.x_scale_fac;
					xofs = 0.0;
					xsize = 1.0;
				}
				vscale = 1.0 / imgsize.y;
				if (bg_image.wrapy) {
					yofs = bg_image.yofs * vscale;
					ysize = h * vscale / el.y_scale_fac;
				} else {
					y1 = h - (imgsize.y - bg_image.yofs) * el.y_scale_fac;
					ys = imgsize.y * el.y_scale_fac;
					yofs = 0.0;
					ysize = 1.0;
				}
				gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(xofs, yofs);
				gl.glVertex3d(x1,    y1+ys, 0f);
				gl.glTexCoord2d(xofs+xsize, yofs);
				gl.glVertex3d(x1+xs, y1+ys, 0f);
				gl.glTexCoord2d(xofs+xsize, yofs+ysize);
				gl.glVertex3d(x1+xs, y1, 0f);
				gl.glTexCoord2d(xofs, yofs+ysize);
				gl.glVertex3d(x1,    y1, 0f);
				gl.glEnd();
				tex.disable();
			}

			// update background
			int bgsizex = el.tilex*(el.viewnrtilesx+3);
			int bgsizey = el.tiley*(el.viewnrtilesy+3);
			if (background==null) {
				background = new TextureRenderer(bgsizex,bgsizey,true);
				background.setSmoothing(true);
				el.invalidateBGTiles();
			}
			// paint any part of bg which is not yet defined
			el.repaintBG(JGEngine.this);
			// draw background as texture
			gl.glColor4f(1,1,1,1);
			tex = background.getTexture();
			tc = tex.getSubImageTexCoords(0,0,bgsizex,bgsizey);
			// coordinate range to display
			int x1 = el.tilex + el.xofs;
			int y1 = el.tiley + el.yofs;
			int x2 = (el.viewnrtilesx+1)*el.tilex + el.xofs;
			int y2 = (el.viewnrtilesy+1)*el.tiley + el.yofs;
			// determine modulo and wrap
			int screensx1 = (int)Math.floor(x1/(double)bgsizex);
			int screensy1 = (int)Math.floor(y1/(double)bgsizey);
			int screensx2 = (int)Math.floor(x2/(double)bgsizex);
			int screensy2 = (int)Math.floor(y2/(double)bgsizey);
			x1 -= screensx1*bgsizex;
			y1 -= screensy1*bgsizey;
			x2 -= screensx2*bgsizex;
			y2 -= screensy2*bgsizey;
			boolean wrapx = screensx2 > screensx1;
			boolean wrapy = screensy2 > screensy1;
			// image coordinates <-> uv coordinates
			double uwidth = Math.abs(tc.right()-tc.left());
			double vwidth = Math.abs(tc.bottom()-tc.top());
			double uofs = Math.min(tc.right(),tc.left());
			double vofs = Math.min(tc.bottom(),tc.top());
			double xscale = bgsizex / uwidth;
			double yscale = bgsizey / vwidth;
			boolean canwrapx = uwidth==1.0 && enable_npot;
			boolean canwrapy = vwidth==1.0 && enable_npot;
			if (!canwrapx) {
				uofs   += 0.5/xscale;
				uwidth -= 1.0/xscale;
			}
			if (!canwrapy) {
				vofs   += 0.5/yscale;
				vwidth -= 1.0/yscale;
			}
			// determine if texture needs to be tiled.  this is true if the
			// texture boundary is crossed _and_ the u or v is not 1.0.
			boolean dotilex = wrapx && !canwrapx;
			boolean dotiley = wrapy && !canwrapy;
			// determine coords + uvs of the four corners
			double wmid=w, hmid=h, wend=w, hend=h;
			double t1x1 = uofs + x1/xscale;
			double t1y1 = vofs + y1/yscale;
			double t1x2 = uofs + x2/xscale;
			double t1y2 = vofs + y2/yscale;
			double t2x1=uofs, t2y1=vofs, t2x2=t1x2, t2y2=t1y2;
			// define bottom/left coords if wrap
			if (wrapx) {
				if (dotilex) {
					wmid = (bgsizex - x1)*el.x_scale_fac;
					t1x2 = uofs + uwidth;
				} else {
					t1x2 += 1.0;
				}
			}
			if (wrapy) {
				if (dotiley) {
					hmid = (bgsizey - y1)*el.y_scale_fac;
					t1y2 = vofs + vwidth;
				} else {
					t1y2 += 1.0;
				}
			}
			tex.bind();
			tex.enable();
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				canwrapx ? GL.GL_REPEAT : GL.GL_CLAMP);
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				canwrapy ? GL.GL_REPEAT : GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				el.smooth_magnify ? GL.GL_LINEAR : GL.GL_NEAREST);
			gl.glBegin(GL.GL_QUADS);
			// topleft
			gl.glTexCoord2d(t1x1, t1y1); gl.glVertex3d(x,      y+h, 0f);
			gl.glTexCoord2d(t1x2, t1y1); gl.glVertex3d(x+wmid, y+h, 0f);
			gl.glTexCoord2d(t1x2, t1y2); gl.glVertex3d(x+wmid, y+h-hmid, 0f);
			gl.glTexCoord2d(t1x1, t1y2); gl.glVertex3d(x,      y+h-hmid, 0f);
			if (dotilex) {
				// right / topright
				gl.glTexCoord2d(t2x1,t1y1); gl.glVertex3d(x+wmid, y+h, 0f);
				gl.glTexCoord2d(t2x2,t1y1); gl.glVertex3d(x+wend, y+h, 0f);
				gl.glTexCoord2d(t2x2,t1y2); gl.glVertex3d(x+wend, y+h-hmid, 0f);
				gl.glTexCoord2d(t2x1,t1y2); gl.glVertex3d(x+wmid, y+h-hmid, 0f);
			}
			if (dotiley) {
				// bottom / bottomleft
				gl.glTexCoord2d(t1x1,t2y1); gl.glVertex3d(x,      y+h-hmid, 0f);
				gl.glTexCoord2d(t1x2,t2y1); gl.glVertex3d(x+wmid, y+h-hmid, 0f);
				gl.glTexCoord2d(t1x2,t2y2); gl.glVertex3d(x+wmid, y  , 0f);
				gl.glTexCoord2d(t1x1,t2y2); gl.glVertex3d(x,      y  , 0f);
			}
			if (dotilex&&dotiley) {
				// bottomright
				gl.glTexCoord2d(t2x1,t2y1); gl.glVertex3d(x+wmid, y+h-hmid, 0f);
				gl.glTexCoord2d(t2x2,t2y1); gl.glVertex3d(x+wend, y+h-hmid, 0f);
				gl.glTexCoord2d(t2x2,t2y2); gl.glVertex3d(x+wend, y, 0f);
				gl.glTexCoord2d(t2x1,t2y2); gl.glVertex3d(x+wmid, y, 0f);
			}
			gl.glEnd();
			tex.disable();
			//System.out.println(tc.right()+" "+tc.bottom()+" "+(tc.right()==1.0));
			//System.out.println(" t1x1:"+t1x1+" t1x2:"+t1x2+" t2x1:"+t2x1+" t2x2:"+t2x2);
			//System.out.println("wmid:"+wmid+" wend:"+wend);
			//System.out.println(" tx2:"+tx2+" ty2:"+ty2+" wmid"+wmid+" wend"+wend);


			gl.glDisable(GL.GL_BLEND);


//
//			// uvs for topleft and bottomright of viewport:
//			// texture coordinates of texture minus invisible area 
//			double tx1 = tc.left() + el.tilex/xscale;
//			double ty1 = tc.top()  + el.tiley/yscale;
//			double tx2 = tc.right() - el.tilex*2.0/xscale;
//			double ty2 = tc.bottom() - el.tiley*2.0/yscale;
//			// add scroll offset
//			tx1 += el.xofs/xscale;
//			tx2 += el.xofs/xscale;
//			ty1 += el.yofs/yscale;
//			ty2 += el.yofs/yscale;
//			// Enable blending, using the SrcOver rule
//			// Use the GL_MODULATE texture function to effectively multiply
//			// each pixel in the texture by the current alpha value
//			//gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
//
//			double x = 0;
//			double y = 0;
//			double w = el.width;
//			double h = el.height;
//			tex.bind();
//			tex.enable();
//			// ensure repeating texture for scrolling
//			// XXX only works for non power of 2 texture support
//			// without npot, non-scrolling background will still work
//			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
//				GL.GL_CLAMP);
//				//GL.GL_REPEAT);
//			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
//				GL.GL_CLAMP);
//				//GL.GL_REPEAT);
//			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
//				GL.GL_LINEAR);
//			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
//				el.smooth_magnify ? GL.GL_LINEAR : GL.GL_NEAREST);
//			gl.glBegin(GL.GL_QUADS);
//			// determine wrap, ensure t?? within [0,tc.botright()]
//			boolean wrapx = (int)Math.floor(tx1) < (int)Math.floor(tx2);
//			boolean wrapy = (int)Math.floor(ty1) < (int)Math.floor(ty2);
//			tx1 -= (int)Math.floor(tx1);
//			ty1 -= (int)Math.floor(ty1);
//			tx2 -= (int)Math.floor(tx2);
//			ty2 -= (int)Math.floor(ty2);
//			double wmid=w, hmid=h, wend=w, hend=h;
//			double txm=tx2, tym=ty2;
//			if (wrapx) {
//				wmid = (1 - tx1)*xscale*el.x_scale_fac;
//				wend = w;
//				txm=1;
//			}
//			if (wrapy) {
//				hmid = (1 - ty1)*yscale*el.y_scale_fac;
//				hend = h;
//				tym=1;
//			}
//			// topleft
//			gl.glTexCoord2d(tx1, ty1); gl.glVertex3d(x,      y+h, 0f);
//			gl.glTexCoord2d(txm, ty1); gl.glVertex3d(x+wmid, y+h, 0f);
//			gl.glTexCoord2d(txm, tym); gl.glVertex3d(x+wmid, y+h-hmid, 0f);
//			gl.glTexCoord2d(tx1, tym); gl.glVertex3d(x,      y+h-hmid, 0f);
//			if (wrapx) {
//				// right / topright
//				gl.glTexCoord2d(0,   ty1); gl.glVertex3d(x+wmid, y+h, 0f);
//				gl.glTexCoord2d(tx2, ty1); gl.glVertex3d(x+wend, y+h, 0f);
//				gl.glTexCoord2d(tx2, tym); gl.glVertex3d(x+wend, y+h-hmid, 0f);
//				gl.glTexCoord2d(0,   tym); gl.glVertex3d(x+wmid, y+h-hmid, 0f);
//			}
//			if (wrapy) {
//				// bottom / bottomleft
//				gl.glTexCoord2d(tx1, 0);   gl.glVertex3d(x,      y+h-hmid, 0f);
//				gl.glTexCoord2d(txm, 0);   gl.glVertex3d(x+wmid, y+h-hmid, 0f);
//				gl.glTexCoord2d(txm, ty2); gl.glVertex3d(x+wmid, y  , 0f);
//				gl.glTexCoord2d(tx1, ty2); gl.glVertex3d(x,      y  , 0f);
//			}
//			if (wrapx&&wrapy) {
//				// bottomright
//				gl.glTexCoord2d(0,   0);   gl.glVertex3d(x+wmid, y+h-hmid, 0f);
//				gl.glTexCoord2d(tx2, 0);   gl.glVertex3d(x+wend, y+h-hmid, 0f);
//				gl.glTexCoord2d(tx2, ty2); gl.glVertex3d(x+wend, y, 0f);
//				gl.glTexCoord2d(0,   ty2); gl.glVertex3d(x+wmid, y, 0f);
//			}
//			gl.glEnd();
//			tex.disable();

			// update foreground
			if (buffer==null) {
				buffer = new TextureRenderer(
					el.tilex*el.nrtilesx, el.tiley*el.nrtilesy,true);
				buffer.setSmoothing(true);
				bufg = buffer.createGraphics();
			}
			// clear buffer with transparent value
			bufg.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
			//bufg.fillRect(0,0,el.tilex*el.viewnrtilesx, el.tiley*el.viewnrtilesy);
			bufg.setComposite(AlphaComposite.SrcOver);
			//bufg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			// ensure smooth edges for sprites
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				GL.GL_CLAMP);
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				GL.GL_CLAMP);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				el.smooth_magnify ? GL.GL_LINEAR : GL.GL_NEAREST);
			// draw objects
			buf_gfx = bufg; // enable objects to draw on buffer gfx.
			cur_gl = gl; // set current gl context for drawing ops
			gl.glEnable(GL.GL_BLEND);
			setBlendMode(cur_gl_blend_src,cur_gl_blend_dst);
			//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
			//gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			//gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			// TextRenderer sets some mysterious opengl state that enables
			// blending on some graphics cards (a driver bug?)
			drawString(gl,"   ",0,0,0,false);
			for (int i=0; i<el.objects.size; i++) {
				drawObject(bufg, gl, (JGObject)el.objects.values[i]);
			}
			buf_gfx = null; // we're finished with the object drawing
			// draw status
			if (bufg!=null) paintFrame(bufg);
			//buffer.markDirty(0,0,el.width,el.height);
			gl.glDisable(GL.GL_BLEND);
			cur_gl = null; // end of gl drawing sequence
			// draw buffer as texture
			tex = buffer.getTexture();
			tex.bind();
			tex.enable();
			gl.glEnable(GL.GL_BLEND);
			// enable thick lines
			//gl.glDisable(GL.GL_LINE_SMOOTH);
			gl.glBegin(GL.GL_QUADS);
			//gl.glTexCoord2d(0, 0); gl.glVertex3d(x  , y+h, 0f);
			//gl.glTexCoord2d(1, 0); gl.glVertex3d(x+w, y+h, 0f);
			//gl.glTexCoord2d(1, 1); gl.glVertex3d(x+w, y  , 0f);
			//gl.glTexCoord2d(0, 1); gl.glVertex3d(x  , y  , 0f);
			gl.glEnd();
			gl.glDisable(GL.GL_BLEND);
			tex.disable();
		} }


		// part of the "official" method of handling keyboard focus
		public boolean isFocusTraversable() { return true; }

		/*====== init stuff ======*/


		int canvwidth,canvheight;
		public JGCanvas (int winwidth, int winheight) {
			super(new GLCapabilities());
			setSize(winwidth,winheight);
			canvwidth = winwidth;
			canvheight = winheight;
		}


		/** Determines whether repaint will show the game graphics or do
		 * nothing. */
		boolean is_initialised=false;
		/** paint interface that is used when the canvas is not initialised (for
		 * displaying status info while starting up, loading files, etc. */
		private ListCellRenderer initpainter=null;
		String progress_message="Please wait, loading files .....";
		String author_message="JGame "+JGameVersionString;
		/** for displaying progress bar, value between 0.0 - 1.0 */
		double progress_bar=0.0;

		void setInitialised() {
			is_initialised=true; 
			initpainter=null;
		}
		void setInitPainter(ListCellRenderer painter) {
			initpainter=painter;
		}
		void setProgressBar(double pos) {
			progress_bar=pos;
			if (!is_initialised && initpainter!=null) repaint(100);
		}
		void setProgressMessage(String msg) {
			progress_message=msg;
			if (!is_initialised && initpainter!=null) repaint(100);
		}
		void setAuthorMessage(String msg) {
			author_message=msg;
			if (!is_initialised && initpainter!=null) repaint(100);
		}

		/*====== paint ======*/


	}






	/*====== debug ======*/

	//XXX state variable that was originally static
	int debugflags = 8;
	static final int BBOX_DEBUG = 1;
	static final int GAMESTATE_DEBUG = 2;
	static final int FULLSTACKTRACE_DEBUG = 4;
	static final int MSGSINPF_DEBUG= 8;

	private static int dbgframelog_expiry=80;
	private JGFont debugmessage_font = new JGFont("Arial",0,12);
	JGColor debug_auxcolor1 = JGColor.green;
	JGColor debug_auxcolor2 = JGColor.magenta;

	private Hashtable dbgframelogs = new Hashtable(); // old error msgs
	private Hashtable dbgnewframelogs = new Hashtable(); // new error msgs
	/** flags indicating messages are new */
	private Hashtable dbgframelogs_new = new Hashtable();
	/** objects that dbgframes correspond to (JGObject) */
	private Hashtable dbgframelogs_obj = new Hashtable();
	/** time that removed objects are dead (Integer) */
	private Hashtable dbgframelogs_dead = new Hashtable();

	/** Refresh message logs for this frame. */
	private void refreshDbgFrameLogs() {
		dbgframelogs_new = new Hashtable(); // clear "new" flag
		for (Enumeration e=dbgnewframelogs.keys(); e.hasMoreElements();) {
			String source = (String) e.nextElement();
			Object log = dbgnewframelogs.get(source);
			dbgframelogs.put(source,log);
			dbgframelogs_new.put(source,"yes");
		}
		dbgnewframelogs = new Hashtable();
	}

	/** Paint the messages. XXX debug text does not follow view zoom/rotate */
	void paintDbgFrameLogs() {
		// we use an absolute font size
		Font oldfont = cur_gl_font;
		JGColor oldcol = cur_gl_color;
		cur_gl_font = new Font(debugmessage_font.name,debugmessage_font.style,
			(int)debugmessage_font.size);
		for (Enumeration e=dbgframelogs.keys(); e.hasMoreElements();) {
			String source = (String) e.nextElement();
			Vector log = (Vector) dbgframelogs.get(source);
			if (dbgframelogs_new.containsKey(source)) {
				// new message
				setColor(el.fg_color);
			} else {
				// message from previous frame
				setColor(debug_auxcolor1);
			}
			JGObject obj = el.getObject(source);
			if (obj==null) {
				// retrieve dead object
				obj = (JGObject) dbgframelogs_obj.get(source);
				// message from deleted object
				setColor(debug_auxcolor2);
				if (obj!=null) {
					// tick dead timer
					int deadtime=0;
					if (dbgframelogs_dead.containsKey(source)) 
						deadtime = ((Integer)dbgframelogs_dead.get(source))
							.intValue();
					if (deadtime < dbgframelog_expiry) {
						dbgframelogs_dead.put(source,new Integer(deadtime+1));
					} else {
						dbgframelogs_obj.remove(source);
						dbgframelogs_dead.remove(source);
					}
				}
			}
			double lineheight = (debugmessage_font.getSize()+1)/el.y_scale_fac;
			if (obj!=null) {
				double running_y = obj.y - lineheight*log.size();
				//JGPoint scaled = el.scalePos(obj.x-el.xofs,
				//	obj.y-el.yofs + lineheight/3,false);
				//scaled.y -= lineheight*log.size();
				for (Enumeration f=log.elements(); f.hasMoreElements(); ) {
					drawString((String)f.nextElement(),obj.x,running_y,-1,true);
					running_y += lineheight;
				}
			} else {
				if (!source.equals("MAIN")) {
					dbgframelogs.remove(source);
				} else {
					if (dbgframelogs_new.containsKey(source)) {
						// new message
						setColor(el.fg_color);
					} else {
						// message from previous frame
						setColor(debug_auxcolor1);
					}
					//int ypos = el.scaleYPos(el.viewHeight(),false);
					double ypos = el.viewHeight() - lineheight*log.size();
					for (Enumeration f=log.elements(); f.hasMoreElements(); ) {
						drawString((String)f.nextElement(),0,ypos,-1,false);
						ypos += lineheight;
					}
				}
			}
		}
		cur_gl_font = oldfont;
		cur_gl_color = oldcol;
	}

	public void dbgShowBoundingBox(boolean enabled) {
		if (enabled) debugflags |=  BBOX_DEBUG;
		else         debugflags &= ~BBOX_DEBUG;
	}

	public void dbgShowGameState(boolean enabled) {
		if (enabled) debugflags |=  GAMESTATE_DEBUG;
		else         debugflags &= ~GAMESTATE_DEBUG;
	}

	public void dbgShowFullStackTrace(boolean enabled) {
		if (enabled) debugflags |=  FULLSTACKTRACE_DEBUG;
		else         debugflags &= ~FULLSTACKTRACE_DEBUG;
	}

	public void dbgShowMessagesInPf(boolean enabled) {
		if (enabled) debugflags |=  MSGSINPF_DEBUG;
		else         debugflags &= ~MSGSINPF_DEBUG;
	}

	public void dbgSetMessageExpiry(int ticks) {dbgframelog_expiry = ticks;}

	public void dbgSetMessageFont(JGFont font) { debugmessage_font=font; }

	public void dbgSetDebugColor1(JGColor col) { debug_auxcolor1=col; }

	public void dbgSetDebugColor2(JGColor col) { debug_auxcolor2=col; }


	public void dbgPrint(String msg) { dbgPrint("MAIN",msg); }

	public void dbgPrint(String source,String msg) {
		if ((debugflags&MSGSINPF_DEBUG)!=0) {
			Vector log = (Vector)dbgnewframelogs.get(source);
			if (log==null) log = new Vector(5,15);
			if (log.size() < 19) {
				log.add(msg);
			} else if (log.size() == 19) {
				log.add("<messages truncated>");
			}
			dbgnewframelogs.put(source,log);
			JGObject obj = el.getObject(source);
			if (obj!=null) { // store source object
				dbgframelogs_obj.put(source,obj);
				dbgframelogs_dead.remove(source);
			}
		} else {
			System.out.println(source+": "+msg);
		}
	}

	public void dbgShowException(String source, Throwable e) {
		ByteArrayOutputStream st = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(st));
		if ((debugflags&FULLSTACKTRACE_DEBUG)!=0) {
			dbgPrint(source,st.toString());
		} else {
			StringTokenizer toker = new StringTokenizer(st.toString(),"\n");
			if (toker.hasMoreTokens())
				dbgPrint(source,toker.nextToken());
			if (toker.hasMoreTokens())
				dbgPrint(source,toker.nextToken());
			if (toker.hasMoreTokens())
				dbgPrint(source,toker.nextToken());
		}
	}

	public String dbgExceptionToString(Throwable e) {
		ByteArrayOutputStream st = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(st));
		if ((debugflags&FULLSTACKTRACE_DEBUG)!=0) {
			return st.toString();
		} else {
			StringTokenizer toker = new StringTokenizer(st.toString(),"\n");
			String ret = toker.nextToken()+"\n";
			ret       += toker.nextToken()+"\n";
			if (toker.hasMoreTokens())
				ret   += toker.nextToken();
			return ret;
		}
	}



	public void exitEngine(String msg) {
		if (msg!=null) {
			System.err.println(msg);
			el.exit_message=msg;
		}
		System.err.println("Exiting JGEngine.");
		if (!i_am_applet) System.exit(0);
		destroy();
		// repaint applet window so that exit error is displayed
		canvas.repaint();
	}






	/** Construct engine, but do not initialise it yet. 
	* Call initEngine or initEngineApplet to initialise the engine. */
	public JGEngine() {
		imageutil.setComponent(this);
	}

	public void initEngineComponent(int width,int height) {
		i_am_applet=false;
		jre.create_frame=false;
		el.winwidth=width;
		el.winheight=height;
		init();
	}

	/** Init engine as applet; call this in your engine constructor.  Applet
	 * init() will start the game.
	 */
	public void initEngineApplet() {
		i_am_applet=true;
		// we get the width/height only after init is called
	}

	/** Init engine as application.  Passing (0,0) for width, height will
	 * result in a full-screen window without decoration.  Passing another
	 * value results in a regular window with decoration.
	 * @param width  real screen width, 0 = use screen size
	 * @param height real screen height, 0 = use screen size */
	public void initEngine(int width,int height) {
		i_am_applet=false;
		jre.create_frame=true;
		if (width==0) {
			Dimension scrsize = Toolkit.getDefaultToolkit().getScreenSize();
			el.winwidth = scrsize.width;
			el.winheight = scrsize.height;
			jre.win_decoration=false;
		} else {
			el.winwidth=width;
			el.winheight=height;
			jre.win_decoration=true;
		}
		init();
	}

	public void setCanvasSettings(int nrtilesx,int nrtilesy,int tilex,int tiley,
	JGColor fgcolor, JGColor bgcolor, JGFont msgfont) {
		// XXX check if we're within initCanvas
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


	public void requestGameFocus() {
		canvas.requestFocus();
	}

	// note: these get and set methods do not delegate calls

	public boolean isApplet() { return i_am_applet; }
	public boolean isMidlet() { return false; }
	public boolean isOpenGL() { return true; }
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

	public boolean getVideoSyncedUpdate() { return gl_driven_update; }

	public int getOffscreenMarginX() { return el.offscreen_margin_x; }
	public int getOffscreenMarginY() { return el.offscreen_margin_y; }

	public double getXScaleFactor() { return el.x_scale_fac; }
	public double getYScaleFactor() { return el.y_scale_fac; }
	public double getMinScaleFactor() { return el.min_scale_fac; }



	/** Initialise engine; don't call directly.  This is supposed to be called
	 * by the applet viewer or the initer.
	 */
	public void init() {
		jre.storeInit();
		if (el.winwidth==0) {
			// get width/height from applet dimensions
			el.winwidth=getWidth();
			el.winheight=getHeight();
			// if not sane, read them from parameters
			// "canvaswidth","canvasheight"
			if (el.winwidth<=1 || el.winheight<=1) {
				el.winwidth = Integer.parseInt(getParameter("canvaswidth"));
				el.winheight = Integer.parseInt(getParameter("canvasheight"));
			}
		}
		initCanvas();
		if (!el.view_initialised) {
			exitEngine("Canvas settings not initialised, use setCanvasSettings().");
		}
		if (!i_am_applet && jre.create_frame) {
			jre.createWindow(this,jre.win_decoration);
		}
		//setAudioLatency(getAudioLatencyPlatformEstimate());

		el.initPF();
		// ensure initPF before creating canvas
		canvas = new JGCanvas(
			el.winwidth - (el.canvas_xofs > 0 ? el.canvas_xofs*2 : 0),
			el.winheight - (el.canvas_yofs > 0 ? el.canvas_yofs*2 : 0)
		);

		// setting gl listener in canvas constructor gives black screen
		// should we do this before adding the glcanvas to ensure we catch the
		// init/reshape events?
		canvas.addGLEventListener(canvas);
		jre.canvas = canvas;

		jre.clearKeymap();
		canvas.addMouseListener(jre);
		canvas.addMouseMotionListener(jre);
		canvas.addFocusListener(jre);



		// set bg color so that the canvas's padding is in the proper color
		canvas.setBackground(getAWTColor(el.bg_color));
		if (jre.my_win!=null) jre.my_win.setBackground(getAWTColor(el.bg_color));
		// determine default font size (unscaled)
		el.msg_font = new JGFont("Helvetica",0,
			(int)(16.0/(640.0/(el.tilex * el.nrtilesx))));
		setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		add(canvas);
		if (!JGObject.setEngine(this)) {
			/** yes, you see that right.  I've used a random interface with a
			 * method that allows me to pass a Graphics.  We shall move this
			 * stuff to JGCanvas later, i suppose */
			canvas.setInitPainter(new ListCellRenderer () {
				public Component getListCellRendererComponent(JList d1,
				Object value, int d2, boolean initialise, boolean d4) {
					GL gl = (GL) value;
					setFont(el.msg_font);
					setColor(gl,el.fg_color);
					drawString(gl,"JGame is already running in this VM",
						el.viewWidth()/2,el.viewHeight()/3,0,false);
					return null;
				} } );
			return;
		}
		el.is_inited=true;
		canvas.setInitPainter(new ListCellRenderer () {
			public Component getListCellRendererComponent(JList d1,
			Object value, int d2, boolean initialise, boolean d4) {
				GL gl = (GL) value;
				cur_gl = gl;
				setFont(el.msg_font);
				setColor(el.fg_color);
				JGImage splash = el.existsImage("splash_image") ?
						el.getImage("splash_image")  :  null;
				if (splash!=null) {
					gl.glEnable(GL.GL_BLEND);
					gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
					gl.glColor4f(1,1,1,1);
					JGPoint splash_size=getImageSize("splash_image");
					drawImage(gl,viewWidth()/2-splash_size.x/2,
						Math.max(0,viewHeight()/4-splash_size.y/2),
						"splash_image",
						null, 1, 0, 1, false);
					gl.glDisable(GL.GL_BLEND);
				}
				drawString(canvas.progress_message,
					viewWidth()/2,viewHeight()/2,0,false);
				// paint the right hand side black in case the bar decreases
				setColor(el.bg_color);
				drawRect((int)(viewWidth()*(0.1+0.8*canvas.progress_bar)),
						(int)(viewHeight()*0.75),
						(int)(viewWidth()*0.8*(1.0-canvas.progress_bar)),
						(int)(viewHeight()*0.05), true,false, false);
				// left hand side of bar
				setColor(el.fg_color);
				drawRect((int)(viewWidth()*0.1), (int)(viewHeight()*0.75),
						(int)(viewWidth()*0.8*canvas.progress_bar),
						(int)(viewHeight()*0.05), true,false, false);
				// length stripes
				drawRect((int)(viewWidth()*0.1), (int)(viewHeight()*0.75),
						(int)(viewWidth()*0.8),
						(int)(viewHeight()*0.008), true,false, false);
				drawRect((int)(viewWidth()*0.1),
						(int)(viewHeight()*(0.75+0.046)),
						(int)(viewWidth()*0.8),
						(int)(viewHeight()*0.008), true,false, false);
				drawString(canvas.author_message,
					viewWidth()-16,
					viewHeight()-getFontHeight(el.msg_font)-10,
					1,false);
				return null;
			} } );
		if (jre.my_win!=null) {
			jre.my_win.setVisible(true);
			jre.my_win.validate();
			// insets are known, resize window
			jre.setWindowSize(jre.win_decoration);
		}
		// initialise keyboard handling
		canvas.addKeyListener(jre);
		canvas.requestFocus();
		canvas.repaint();
		thread = new Thread(new JGEngineThread());
		thread.start();
	}


	abstract public void initCanvas();

	abstract public void initGame();

	public void start() {
		running=true; 
		// restart animator if necessary
		canvas.repaint();
	}

	public void stop() {
		// game thread will stop animator if running
		running=false; 
	}

	public void startApp() {
		if (!el.is_inited) {
			init();
		} else {
			start();
		}
	}

	public void pauseApp() { stop(); }

	public void destroyApp(boolean unconditional) { destroy(); }

	public boolean isRunning() { return running; }

	public void wakeUpOnKey(int key) { jre.wakeUpOnKey(key); }

	public void destroy() {
		// kill game threads
		el.is_exited=true;
		if (anim!=null) {
			anim.stop();
			anim=null;
		}
		// applets cannot interrupt threads; their threads will 
		// be destroyed for them (not always, though ...).
		if (thread!=null) {
			if (!i_am_applet) thread.interrupt();
			try {
				thread.join(2000); // give up after 2 sec
			} catch (InterruptedException e) {
				e.printStackTrace();
				// give up
			}
		}
		// remove frame??
		// close files?? that appears to be unnecessary
		// reset global variables
		if (el.is_inited) {
			JGObject.setEngine(null);
		}
		// stop all samples
		disableAudio();
		System.out.println("JGame engine disposed.");
	}

	public void setViewOffset(int xofs,int yofs,boolean centered) {
		el.setViewOffset(xofs,yofs,centered);
	}

	public void setBGImgOffset(int depth, double xofs, double yofs,
	boolean centered) { el.setBGImgOffset(depth,xofs,yofs,centered); }

	public void setViewZoomRotate(double zoom, double rotate) {
		viewzoom = zoom;
		viewrotate = rotate;
	}

	public void setPFSize(int nrtilesx,int nrtilesy) {
		el.setPFSize(nrtilesx,nrtilesy);
	}

	public void setPFWrap(boolean wrapx,boolean wrapy,int shiftx,int shifty) {
		el.setPFWrap(wrapx,wrapy,shiftx,shifty);
	}


	public void setFrameRate(double fps, double maxframeskip) {
		el.setFrameRate(fps, maxframeskip);
	}

	public void setVideoSyncedUpdate(boolean value) {
		// XXX check if we're within initCanvas
		gl_driven_update = value;
		if (value) {
			// display() starts animator
			if (canvas!=null) canvas.repaint();
		} else {
			// remove animator
			if (anim!=null) {
				anim.stop();
				anim=null;
			}
			// reset game speed
			setGameSpeed(1.0);
		}
	}

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
		Color bgcol = new Color(bgcolor.r,bgcolor.g,bgcolor.b);
		if (canvas!=null) canvas.setBackground(bgcol);
		if (jre.my_win!=null) jre.my_win.setBackground(bgcol);
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

	public void setMouseCursor(int cursor) {
		if (cursor==DEFAULT_CURSOR)
			canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		else if (cursor==CROSSHAIR_CURSOR)
			canvas.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		else if (cursor==HAND_CURSOR)
			canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
		else if (cursor==WAIT_CURSOR)
			canvas.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		else if (cursor==NO_CURSOR)
			canvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
				null_image, new Point(0,0), "hidden" ) );
	}

	/** 1x1 pixel image with transparent colour */
	private BufferedImage null_image = 
			new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	/** Set mouse cursor, null means hide cursor.
	* @param cursor is of type java.awt.Cursor */
	public void setMouseCursor(Object cursor) {
		if (cursor==null) {
			canvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
					null_image, new Point(0,0), "hidden" ) );
		} else {
			canvas.setCursor((Cursor)cursor);
		}
	}


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
		jre.audioNewFrame();
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
		el.gamestate = el.gamestate_nextframe;
		el.gamestate_nextframe = new Vector(10,20);
		el.gamestate_nextframe.addAll(el.gamestate);
		// we assume that state transitions will not initiate new state
		// transitions!
		invokeGameStateMethods("start",el.gamestate_new);
		el.gamestate_new.clear();
		el.flushRemoveList();
		el.flushAddList();
		try {
			doFrame();
		} catch (JGameError ex) {
			exitEngine(dbgExceptionToString(ex));
		} catch (Exception ex) {
			dbgShowException("MAIN",ex);
		}
		invokeGameStateMethods("doFrame",el.gamestate);
		el.frameFinished();
	}

	private void invokeGameStateMethods(String prefix,Vector states) {
		for (Enumeration e=states.elements(); e.hasMoreElements(); ) {
			String state = (String) e.nextElement();
			jre.tryMethod(this,prefix+state,new Object[]{});
		}
	}

	public void doFrame() {}

	void paintFrame(Graphics g) {
		buf_gfx=g;
		setColor(g,el.fg_color);
		setFont(el.msg_font);
		try {
			paintFrame();
		} catch (JGameError ex) {
			exitEngine(dbgExceptionToString(ex));
		} catch (Exception ex) {
			dbgShowException("MAIN",ex);
		}
		invokeGameStateMethods("paintFrame",el.gamestate);
		if ((debugflags&GAMESTATE_DEBUG)!=0) {
			String state="{";
			for (Enumeration e=el.gamestate.elements(); e.hasMoreElements(); ) {
				state += (String)e.nextElement();
				if (e.hasMoreElements()) state +=",";
			}
			state += "}";
			setFont(el.msg_font);
			setColor(g,el.fg_color);
			drawString(state,el.viewWidth(),
					el.viewHeight()-(int)getFontHeight(el.msg_font), 1);
		}
		if ((debugflags&MSGSINPF_DEBUG)!=0) paintDbgFrameLogs();
		buf_gfx=null;
	}

	public void paintFrame() {}

	public Graphics getBufferGraphics() { return buf_gfx; }

	/* some convenience functions for drawing during repaint and paintFrame()*/

	public void setColor(JGColor col) {
		// buf_gfx can be phased out once buffer unused
		if (buf_gfx!=null) setColor(buf_gfx,col);
		if (cur_gl!=null) setColor(cur_gl,col);
		cur_gl_color=col;
	}

	public Color getAWTColor(JGColor col) {
		return new Color(col.r,col.g,col.b);
	}

	public void setFont(JGFont font) {
		setFont(buf_gfx,font); 
	}

	public void setFont(Graphics g,JGFont jgfont) {
		Font font = new Font(jgfont.name,jgfont.style,(int)jgfont.size);
		font=font.deriveFont((float)(jgfont.size*el.min_scale_fac));
		cur_gl_font = font;
		if (canvas!=null && g!=null) g.setFont(font);
	}


	public void setStroke(double thickness) {
		//Graphics2D g = (Graphics2D) buf_gfx;
		//g.setStroke(new BasicStroke((float)(thickness*el.min_scale_fac)));
		cur_gl_stroke=thickness;
		/*if (cur_gl!=null) {
			int [] linewidth = new int[2];
			cur_gl.glGetIntegerv(GL.GL_SMOOTH_LINE_WIDTH_RANGE,linewidth,0);
			System.out.println("asd"+linewidth[0]+" "+linewidth[1]);
		}*/
	}

	public void setBlendMode(int src_func, int dst_func) {
		cur_gl_blend_src = src_func;
		cur_gl_blend_dst = dst_func;
		if (cur_gl==null) return;
		int srcmode = src_func==0 ? GL.GL_ONE :
			(src_func==1 ? GL.GL_SRC_ALPHA : GL.GL_ONE_MINUS_SRC_ALPHA);
		int dstmode = dst_func==0 ? GL.GL_ONE :
			(dst_func==1 ? GL.GL_SRC_ALPHA : GL.GL_ONE_MINUS_SRC_ALPHA);
		cur_gl.glBlendFunc(srcmode,dstmode);
	}

	public double getFontHeight(JGFont jgfont) {
		Font font;
		if (jgfont==null) {
			font=cur_gl_font;
		} else {
			font = new Font(jgfont.name,jgfont.style,(int)jgfont.size);
		}
		TextRenderer ren = getTextRenderer(font);
		FontRenderContext fontrc = ren.getFontRenderContext();
		Rectangle2D fontbounds = font.getMaxCharBounds(fontrc);
		return fontbounds.getHeight();
	}

	static int smallestPowerOfTwo(int i) {
		int ret=1;
		while (ret<i) ret <<= 1;
		return ret;
	}

	Texture getTexture(JREImage img) {
		if (img.texture==null) {
			// get buffered image
			JGPoint size = img.getSize();
			BufferedImage bimg = 
				JREImage.createRGBA8Image(img.getPixels(),size.x,size.y);
			//BufferedImage bimg = (BufferedImage)
			//	img.toDisplayCompatible(1,JGColor.black,false,false).img;
			img.texture = TextureIO.newTexture(bimg,false/*mipmap*/);
		}
		return (Texture)img.texture;
	}

	/** get texture stretched to nearest POT */
	Texture getPOTStretchedTexture(JREImage img) {
		if (img.stretched_texture==null) {
			JGPoint size = img.getSize();
			int xpot = smallestPowerOfTwo(size.x);
			int ypot = smallestPowerOfTwo(size.y);
			int [] pixels = img.getPixels();
			//System.out.println(""+pixels.length);
			IntBuffer inbuf = IntBuffer.wrap(pixels);
			inbuf.rewind();
			IntBuffer outbuf = IntBuffer.allocate(xpot*ypot);
			outbuf.rewind();
			GLU glu = new GLU();
			if (glu.gluScaleImage(GL.GL_RGBA,
					size.x, size.y, GL.GL_UNSIGNED_BYTE, inbuf,
					xpot, ypot, GL.GL_UNSIGNED_BYTE, outbuf) != 0) 
			{
				throw new JGameError("Texture fails to stretch.");
			}
			BufferedImage bimg = 
				JREImage.createRGBA8Image(outbuf.array(),xpot,ypot);
			img.stretched_texture = TextureIO.newTexture(bimg,false);
		}
		return (Texture)img.stretched_texture;
	}

	boolean modelview_matrix_pf_relative=false;

	void setupModelviewMatrix(GL gl,boolean pf_relative) {
		if (pf_relative==modelview_matrix_pf_relative) return;
		modelview_matrix_pf_relative = pf_relative;
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (!pf_relative) return;
		int w = el.width;
		int h = el.height;
		// do zoom/rotate effect
		gl.glTranslated(w/2.0,h/2.0,0);
		gl.glRotated(viewrotate/Math.PI*180.0, 0, 0, 1.00);
		gl.glScaled(viewzoom,viewzoom, 1.0);
		/*double rotcenx =  w/2*Math.cos(tt/180.0*Math.PI)
						+ h/2*Math.sin(tt/180.0*Math.PI);
		double rotceny =  w/2*Math.sin(tt/180.0*Math.PI)
						- h/2*Math.cos(tt/180.0*Math.PI);*/
		gl.glTranslated(-w/2.0,-h/2.0,0);
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
		if (cur_gl==null) return;
		setColor(cur_gl,cur_gl_color);
		cur_gl.glLineWidth((float)cur_gl_stroke);
		cur_gl.glBegin(GL.GL_LINES);
		cur_gl.glVertex3d(el.scaleXPos(x1,pf_relative),
			el.height - el.scaleYPos(y1,pf_relative), 0.0);
		cur_gl.glVertex3d(el.scaleXPos(x2,pf_relative),
			el.height - el.scaleYPos(y2,pf_relative), 0.0);
		cur_gl.glEnd();
	}

	public void drawPolygon(double [] x,double [] y, JGColor [] col,int len,
	boolean filled, boolean pf_relative) {
		if (cur_gl==null) return;
		setupModelviewMatrix(cur_gl,pf_relative);
		drawPolygonInner(x,y,col,len,filled,pf_relative);
	}
	/** private version without setupmodelviewmatrix */
	void drawPolygonInner(double [] x,double [] y, JGColor [] col,int len,
	boolean filled, boolean pf_relative) {
		setColor(cur_gl,cur_gl_color);
		if (filled) {
			cur_gl.glBegin(GL.GL_POLYGON);
		} else {
			cur_gl.glLineWidth((float)cur_gl_stroke);
			cur_gl.glBegin(GL.GL_LINE_LOOP);
		}
		JGColor prevcol=null;
		for (int i=0; i<len; i++) {
			if (col!=null) {
				if (col[i]!=prevcol) setColor(cur_gl,col[i]);
				prevcol = col[i];
			}
			cur_gl.glVertex3d(el.scaleXPos(x[i],pf_relative),
				el.height - el.scaleYPos(y[i],pf_relative), 0.0);
		}
		cur_gl.glEnd();
	}


	public void drawRect(double x,double y,double width,double height,
	boolean filled, boolean centered, double thickness, JGColor color) {
		if (color!=null) setColor(color);
		setStroke(thickness);
		drawRect(x,y,width,height,filled,centered,true,null,null);
	}

	public void drawRect(double x,double y,double width,double height,
	boolean filled,boolean centered) {
		drawRect(x,y,width,height,filled,centered,true,null,null);
	}

	public void drawRect(double x,double y,double width,double height,
	boolean filled,boolean centered, boolean pf_relative) {
		drawRect(x,y,width,height,filled,centered,pf_relative,null,null);
	}

	public void drawRect(double x,double y,double width,double height,
	boolean filled, boolean centered,boolean pf_relative,
	JGColor [] shadecol) {
		drawRect(x,y,width,height,filled,centered,pf_relative,shadecol,null);
	}

	public void drawRect(double x,double y,double width,double height,
	boolean filled, boolean centered,boolean pf_relative,
	JGColor [] shadecol,String tileimage) {
		if (cur_gl==null) return;
		setupModelviewMatrix(cur_gl,pf_relative);
		if (centered) {
			x -= (width/2);
			y -= (height/2);
		}
		JGRectangle r = el.scalePos(x, y, width, height, pf_relative);
		if (tileimage==null) {
			setColor(cur_gl,cur_gl_color);
		} else {
			setColor(cur_gl,JGColor.white);
		}

		double x1=0,y1=0,x2=0,y2=0, relx2=0,rely2=0;
		Texture tex=null;
		if (tileimage!=null) {
			JREImage img = (JREImage)el.getImage(tileimage);
			//draw in current gl context
			tex = getTexture(img);
			JGPoint size = img.getSize();
			// get uv coords
			TextureCoords coord = tex.getSubImageTexCoords(0,0,size.x,size.y);
			x1 = coord.left();
			y1 = coord.top();
			x2 = coord.right();
			y2 = coord.bottom();
			tex.bind();
			tex.enable();
			cur_gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				el.smooth_magnify ? GL.GL_LINEAR : GL.GL_NEAREST);
			double relxsize = width / (double)size.x;
			double relysize = height / (double)size.y;
			relx2 = x1 + (x2-x1) * relxsize;
			rely2 = y1 + (y2-y1) * relysize;
		}
		if (filled || tileimage!=null) {
			cur_gl.glBegin(GL.GL_QUADS);
		} else {
			cur_gl.glLineWidth((float)cur_gl_stroke);
			cur_gl.glBegin(GL.GL_LINE_LOOP);
		}
		//System.out.println(""+r.x+" "+r.y+" "+r.width+" "+r.height);
		if (tileimage!=null) cur_gl.glTexCoord2d(x1,y1);
		if (shadecol!=null) setColor(cur_gl,shadecol[0]);
		cur_gl.glVertex3d(r.x,         el.height-r.y, 0);
		if (tileimage!=null) cur_gl.glTexCoord2d(relx2,y1);
		if (shadecol!=null) setColor(cur_gl,shadecol[1]);
		cur_gl.glVertex3d(r.x+r.width, el.height-r.y, 0);
		if (tileimage!=null) cur_gl.glTexCoord2d(relx2,rely2);
		if (shadecol!=null) setColor(cur_gl,shadecol[2]);
		cur_gl.glVertex3d(r.x+r.width, el.height-r.y-r.height, 0);
		if (tileimage!=null) cur_gl.glTexCoord2d(x1,rely2);
		if (shadecol!=null) setColor(cur_gl,shadecol[3]);
		cur_gl.glVertex3d(r.x,         el.height-r.y-r.height, 0);
		cur_gl.glEnd();
		if (tileimage!=null) {
			tex.disable();
		}
	}

	public void drawOval(double x,double y,double width,double height,
	boolean filled, boolean centered, double thickness, JGColor color) {
		if (color!=null) setColor(color);
		setStroke(thickness);
		drawOval(x,y,width,height,filled,centered,true);
	}

	public void drawOval(double x,double y, double width,double height,
	boolean filled, boolean centered) {
		drawOval(x,y,width,height,filled,centered,true);
	}

	public void drawOval(double x,double y, double width,double height,
	boolean filled,boolean centered, boolean pf_relative) {
		if (cur_gl==null) return;
		setupModelviewMatrix(cur_gl,pf_relative);
		x = el.scaleXPos(x,pf_relative);
		y = el.scaleYPos(y,pf_relative);
		if (width<0) width = -width;
		if (height<0) height = -height;
		int scaledwidth = el.scaleXPos(width,false);
		int scaledheight = el.scaleYPos(height,false);
		int maxscale = Math.max(scaledwidth,scaledheight);
		if (!centered) {
			x += (scaledwidth/2);
			y += (scaledheight/2);
		}
		if (ovalx==null) genOvalPolygons(256.0);
		cur_gl.glPushMatrix();
		cur_gl.glTranslated(x,el.height-y,0);
		//cur_gl.glScaled(0.1,0.1,1);
		cur_gl.glScaled(width/512.0,height/512.0,1);
		cur_gl.glTranslated(0,-el.height,0);
		if (maxscale > 100) {
			drawPolygonInner(ovalx_lg,ovaly_lg,null,ovalx_lg.length,filled,false);
		} else if (maxscale > 16) {
			drawPolygonInner(ovalx,ovaly,null,ovalx.length,filled,false);
		} else {
			drawPolygonInner(ovalx_sm,ovaly_sm,null,ovalx_sm.length,filled,false);
		}
		cur_gl.glPopMatrix();
		//if (filled) {
		//	buf_gfx.fillOval((int)x,(int)y,(int)width,(int)height);
		//} else {
		//	buf_gfx.drawOval((int)x,(int)y,(int)width,(int)height);
		//}
	}

	double [] ovalx=null,ovaly=null;
	double [] ovalx_sm=null,ovaly_sm=null;
	double [] ovalx_lg=null,ovaly_lg=null;

	JGColor [] ovals, ovals_sm, ovals_lg;
	JGColor [] ovalshade;

	void genOvalPolygons(double radius) {
		ovalshade = new JGColor[4];
		for (int i=0; i<ovalshade.length; i++)
			ovalshade[i] = new JGColor(50*i,30*i,60*i,255);
		ovalx = new double[20];
		ovaly = new double[20];
		ovals = new JGColor[20];
		genOvalPolygon(ovalx,ovaly,ovals,radius,20);
		ovalx_sm = new double[8];
		ovaly_sm = new double[8];
		ovals_sm = new JGColor[8];
		genOvalPolygon(ovalx_sm,ovaly_sm,ovals_sm,radius,8);
		ovalx_lg = new double[50];
		ovaly_lg = new double[50];
		ovals_lg = new JGColor[50];
		genOvalPolygon(ovalx_lg,ovaly_lg,ovals_lg,radius,50);
	}

	void genOvalPolygon(double[]ovalx,double[]ovaly,JGColor[]ovals,
	double radius,int segments){
		int i=0;
		for (double r=0; r < 2.0*Math.PI; r += Math.PI*2.001/segments) {
			ovalx[i] = radius*Math.sin(r);
			ovaly[i] = radius*Math.cos(r);
			ovals[i] = ovalshade[(int) ((r / Math.PI)*2.0) ];
			i++;
		}
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


	public void drawImage(double x,double y,String imgname) {
		if (cur_gl==null) return;
		drawImage(cur_gl,x,y,imgname,null,1.0, 0, 1.0, true);
	}

	public void drawImage(double x,double y,String imgname,boolean pf_relative){
		if (cur_gl==null) return;
		drawImage(cur_gl,x,y,imgname,null,1.0, 0, 1.0, pf_relative);
	}

	/** Extended version of drawImage for platforms with opengl capabilities.
	 * On platforms without support for accelerated blending, rotation,
	 * scaling, this call is equivalent to drawImage(x,y,imgname,pf_relative).
	 *
	 * rotation and scaling are centered around the image center.
	 *
	 * @param blend_col colour to blend with image, null=(alpha,alpha,alpha)
	 * @param alpha  alpha (blending) value, 0=transparent, 1=opaque
	 * @param rot  rotation of object in degrees (radians)
	 * @param scale  scaling of object (1 = normal size).
	 */
	public void drawImage(double x,double y,String imgname, JGColor blend_col,
	double alpha, double rot, double scale, boolean pf_relative) {
		if (cur_gl==null) return;
		drawImage(cur_gl,x,y,imgname,blend_col,alpha,rot,scale,pf_relative);
	}

	// get rid of gl parameter?
	void drawImage(GL gl,double x,double y,String imgname, JGColor blend_col,
	double alpha, double rot, double scale, boolean pf_relative) {
		setupModelviewMatrix(gl,pf_relative);
		if (imgname==null) return;
		x = el.scaleXPos(x,pf_relative);
		y = el.scaleYPos(y,pf_relative);
		JREImage img = (JREImage)el.getImage(imgname);
		if (img!=null) {
			//draw in current gl context
			Texture tex = getTexture(img);
			JGPoint size = img.getSize();
			// get uv coords
			TextureCoords coord = tex.getSubImageTexCoords(0,0,size.x,size.y);
			double x1 = coord.left();
			double y1 = coord.top();
			double x2 = coord.right();
			double y2 = coord.bottom();
			tex.bind();
			tex.enable();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				el.smooth_magnify ? GL.GL_LINEAR : GL.GL_NEAREST);
			if (blend_col==null) {
				gl.glColor4d(alpha,alpha,alpha,alpha);
			} else {
				gl.glColor4d(blend_col.r/255.0, blend_col.g/255.0,
					blend_col.b/255.0, alpha);
			}
			// size of unscaled image
			double txsize = size.x*scale;
			double tysize = size.y*scale;
			// size of scaled image
			size = el.scalePos(size.x,size.y,false);
			double txsize_s = size.x*scale;
			double tysize_s = size.y*scale;
			// determine topleft of scaled image
			x -= (txsize_s - size.x)/2;
			y -= (tysize_s - size.y)/2;
			// Render image right-side up
			y = el.height - y - tysize_s;
			gl.glPushMatrix();
			gl.glTranslated(x+txsize_s/2.0, y+tysize_s/2.0, 0);
			gl.glScaled(el.x_scale_fac,el.y_scale_fac,1.0);
			gl.glRotated(rot/Math.PI*180.0,0,0,1);
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2d(x1,y1); gl.glVertex3d(-txsize/2,+tysize/2, 0f);
			gl.glTexCoord2d(x2,y1); gl.glVertex3d(+txsize/2,+tysize/2, 0f);
			gl.glTexCoord2d(x2,y2); gl.glVertex3d(+txsize/2,-tysize/2, 0f);
			gl.glTexCoord2d(x1,y2); gl.glVertex3d(-txsize/2,-tysize/2, 0f);
			gl.glEnd();
			gl.glPopMatrix();
			tex.disable();
		}
	}


	public void drawString(String str, double x, double y, int align,
	JGFont font, JGColor color) {
		if (font!=null) setFont(font);
		if (color!=null) setColor(color);
		drawString(cur_gl, str, x,y, align, false);
	}

	public void drawString(String str, double x, double y, int align) {
		drawString(cur_gl, str, x,y, align, false);
	}

	public void drawString(String str, double x, double y, int align,
	boolean pf_relative) {
		drawString(cur_gl, str, x,y, align, pf_relative);
	}

	/** Font -} TextRenderer */
	Hashtable textren = new Hashtable();

	/** last font set with setFont, for use with gl */
	Font cur_gl_font = new Font("Arial",0,12);

	/** last color set with setColor, for use with gl */
	JGColor cur_gl_color=JGColor.white;

	/** last stroke set with setStroke */
	double cur_gl_stroke=1.0;

	/** last blend function set */
	int cur_gl_blend_src=1,cur_gl_blend_dst=-1;

	TextRenderer getTextRenderer(Font font) {
		TextRenderer ren = (TextRenderer) textren.get(font);
		if (ren==null) {
			ren = new TextRenderer(font,true,true);
			textren.put(font,ren);
		}
		return ren;
	}

	// get rid of gl parameter?
	/** Internal function for writing on gl drawable. */
	void drawString(GL gl, String str, double x, double y, int align,
	boolean pf_relative) {
		if (gl==null) return;
		setupModelviewMatrix(gl,pf_relative);
		if (str.equals("")) return;
		x = el.scaleXPos(x,pf_relative);
		y = el.scaleYPos(y,pf_relative);
		Font font = cur_gl_font;
		TextRenderer ren = getTextRenderer(font);
		FontRenderContext fontrc = ren.getFontRenderContext();
		// XXX a lot of time in spent in TextLayout.<init>
		TextLayout layout = new TextLayout(str, font, fontrc);
		Rectangle2D strbounds = layout.getBounds();
		int xpos,ypos;
		if (align==-1) {
			xpos = (int)(x-strbounds.getMinX());
			ypos = (int)(y-strbounds.getMinY());
		} else if (align==0) {
			xpos = (int)(x-strbounds.getCenterX());
			ypos = (int)(y-strbounds.getMinY());
		} else {
			xpos = (int)(x-strbounds.getMaxX());
			ypos = (int)(y-strbounds.getMinY());
		}
		ren.begin3DRendering();
		if (el.outline_thickness>0) {
			//Color origcol = g.getColor();
			JGColor c=el.outline_colour;
			ren.setColor(c.r/255.0f,c.g/255.0f,c.b/255.0f,c.alpha/255.0f);
			int real_thickness=Math.max(
				el.scaleXPos(el.outline_thickness,false),1 );
			for (int i=-real_thickness; i<=real_thickness; i++) {
				if (i==0) continue;
				ren.draw3D(str,xpos+i,el.height-ypos, 0, 1.0f);
			}
			for (int i=-real_thickness; i<=real_thickness; i++) {
				if (i==0) continue;
				ren.draw3D(str,xpos,el.height-(ypos+i), 0, 1.0f);
			}
			//g.setColor(origcol);
		}
		JGColor c=cur_gl_color;
		ren.setColor(c.r/255.0f,c.g/255.0f,c.b/255.0f,c.alpha/255.0f);
		try {
			ren.draw3D(str,xpos,el.height-ypos, 0, 1.0f);
		} catch (Exception e) {
			//e.printStackTrace();
			//System.out.println("Internal error printing '"+str+"'");
		}
		ren.end3DRendering();
	}

	// to be phased out
	/** Internal function for writing on both buffer and screen.  */
	void drawString(Graphics g, String str, double x, double y, int align,
	boolean pf_relative) {
		if (g==null) return;
		if (str.equals("")) return;
		x = el.scaleXPos(x,pf_relative);
		y = el.scaleYPos(y,pf_relative);
		Font font = g.getFont();
		FontRenderContext fontrc = ((Graphics2D)g).getFontRenderContext();
		//Rectangle2D fontbounds = font.getMaxCharBounds(fontrc);
		//Rectangle2D stringbounds = getStringBounds(str, fontrc);
		// XXX a lot of time in spent in TextLayout.<init>
		TextLayout layout = new TextLayout(str, font, fontrc);
		Rectangle2D strbounds = layout.getBounds();
		int xpos,ypos;
		if (align==-1) {
			xpos = (int)(x-strbounds.getMinX());
			ypos = (int)(y-strbounds.getMinY());
		} else if (align==0) {
			xpos = (int)(x-strbounds.getCenterX());
			ypos = (int)(y-strbounds.getMinY());
		} else {
			xpos = (int)(x-strbounds.getMaxX());
			ypos = (int)(y-strbounds.getMinY());
		}
		if (el.outline_thickness>0) {
			Color origcol = g.getColor();
			setColor(el.outline_colour);
			int real_thickness=Math.max(
				el.scaleXPos(el.outline_thickness,false),1 );
			for (int i=-real_thickness; i<=real_thickness; i++) {
				if (i==0) continue;
				g.drawString(str,xpos+i,ypos);
			}
			for (int i=-real_thickness; i<=real_thickness; i++) {
				if (i==0) continue;
				g.drawString(str,xpos,ypos+i);
			}
			g.setColor(origcol);
		}
		g.drawString(str,xpos,ypos);
	}

	public void drawImageString(String string, double x, double y, int align,
	String imgmap, int char_offset, int spacing) {
		el.drawImageString(this,string,x,y,align,imgmap,char_offset,spacing,false);
	}

	public void drawImageString(String string, double x, double y, int align,
	String imgmap, int char_offset, int spacing,boolean pf_relative) {
		el.drawImageString(this,string,x,y,align,imgmap,char_offset,spacing,pf_relative);
		ImageMap map = (ImageMap) el.imagemaps.get(imgmap);
		if (map==null) throw new JGameError(
				"Font image map '"+imgmap+"' not found.",true );
		if (align==0) {
			x -= (map.tilex+spacing) * string.length()/2;
		} else if (align==1) {
			x -= (map.tilex+spacing) * string.length();
		}
		for (int i=0; i<string.length(); i++) {
			int imgnr = -char_offset+string.charAt(i);
			String lettername = imgmap+"#"+string.charAt(i);
			if (!el.existsImage(lettername)) {
				el.defineImage(lettername, "FONT", 0,
					el.getSubImage(imgmap,imgnr),
					"-", 0,0,0,0);
			}
			JGImage letter = getImage(lettername);
			drawImage(cur_gl, x,y,lettername, null,1.0, 0,1.0, pf_relative);
			x += map.tilex + spacing;
		}
	}

	/* input */


	public JGPoint getMousePos() {
		int viewx = el.tilex*el.viewnrtilesx;
		int viewy = el.tiley*el.viewnrtilesy;
		double mx = jre.mousepos.x - viewx/2;
		double my = jre.mousepos.y - viewy/2;
		return new JGPoint(
			viewx/2 + (int)
			(1/viewzoom*(mx*Math.cos(-viewrotate) + my*Math.sin(-viewrotate))),
			viewy/2 + (int)
			(1/viewzoom*(-mx*Math.sin(-viewrotate) + my*Math.cos(-viewrotate)))
		);
		// somehow i didn't get unproject to work...
		//glu.gluProject(
		//	jre.mousepos.x*el.x_scale_fac, jre.mousepos.y*el.y_scale_fac, 0.0,
		//	up_mvmatrix, 0,
		//	up_projmatrix, 0, 
		//	up_viewport, 0, 
		//	up_wcoord, 0);
		//return new JGPoint(
		//	(int)(up_wcoord[0]/el.x_scale_fac),
		//	(int)(up_wcoord[1]/el.y_scale_fac));
		//return new JGPoint(jre.mousepos.x, jre.mousepos.y);
	}
	public int getMouseX() { return getMousePos().x; }
	public int getMouseY() { return getMousePos().y; }

	public boolean getMouseButton(int nr) { return jre.mousebutton[nr]; }
	public void clearMouseButton(int nr) { jre.mousebutton[nr]=false; }
	public void setMouseButton(int nr) { jre.mousebutton[nr]=true; }
	public boolean getMouseInside() { return jre.mouseinside; }

	public boolean getKey(int key) { return jre.keymap[key]; }
	public void clearKey(int key) { jre.keymap[key]=false; }
	public void setKey(int key) { jre.keymap[key]=true; }

	public int getLastKey() { return jre.lastkey; }
	public char getLastKeyChar() { return jre.lastkeychar; }
	public void clearLastKey() { jre.clearLastKey(); }

	/** Non-static version for the sake of the interface. */
	public String getKeyDesc(int key) { return JREEngine.getKeyDescStatic(key); }

	public static String getKeyDescStatic(int key) { return JREEngine.getKeyDescStatic(key); }


	/** Non-static version for the sake of the interface. */
	public int getKeyCode(String keydesc) { return JREEngine.getKeyCodeStatic(keydesc); }

	public static int getKeyCodeStatic(String keydesc) {
		return JREEngine.getKeyCodeStatic(keydesc); 
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
		if (isApplet()) return null;
		File jgamedir;
		try {
			jgamedir = new File(System.getProperty("user.home"), ".jgame");
		} catch (Exception e) {
			// probably AccessControlException of unsigned webstart
			return null;
		}
		if (!jgamedir.exists()) {
			// try to create ".jgame"
			if (!jgamedir.mkdir()) {
				// fail
				return null;
			}
		}
		if (!jgamedir.isDirectory()) return null;
		File file = new File(jgamedir,filename);
		// try to create file if it didn't exist
		try {
			file.createNewFile();
		} catch (IOException e) {
			return null;
		}
		if (!file.canRead()) return null;
		if (!file.canWrite()) return null;
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

	public int invokeUrl(String url,String target) {
		if (isApplet()) {
			try {
				getAppletContext().showDocument(new URL(url),target);
			} catch (MalformedURLException e) {
				return 0;
			}
			return -1;
		} else {
			return 0;
		}
	}

	void paintExitMessage(GL gl) { try {
		setFont(debugmessage_font);
		int height = (int) (getFontHeight(null) / el.y_scale_fac);
		cur_gl = gl;
		drawRect(el.viewWidth()/2, el.viewHeight()/2,
			9*el.viewWidth()/10, height*5, true,true, false);
		setColor(debug_auxcolor2);
		// draw colour bars
		drawRect(el.viewWidth()/2, el.viewHeight()/2 - 5*height/2,
			9*viewWidth()/10, 5, true,true, false);
		drawRect(el.viewWidth()/2, el.viewHeight()/2 + 5*height/2,
			9*viewWidth()/10, 5, true,true, false);
		setColor(el.fg_color);
		int ypos = el.viewHeight()/2 - 3*height/2;
		StringTokenizer toker = new StringTokenizer(el.exit_message,"\n");
		while (toker.hasMoreTokens()) {
			drawString(toker.nextToken(),el.viewWidth()/2,ypos,0, false);
			ypos += height+1;
		}
	} catch(java.lang.NullPointerException e) {
		// this sometimes happens during drawString when the applet is exiting
		// but calls repaint while the graphics surface is already disposed.
		// See also bug 4791314:
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4791314
	} }






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

	public double atan2(double y,double x) {
		return Math.atan2(y,x);
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



	/** Engine thread, executing game action. */
	class JGEngineThread implements Runnable {
		private long target_time=0; /* time at which next frame should start */
		private int frames_skipped=0;
		public JGEngineThread () {}
		public void run() { try {
			try {
				initGame();
			} catch (Exception e) {
				e.printStackTrace();
				throw new JGameError("Exception during initGame(): "+e);
			}
			canvas.setInitialised();
			target_time = System.currentTimeMillis()+(long)(1000.0/el.fps);
			while (!el.is_exited) {
				if (!gl_driven_update)
					if ((debugflags&MSGSINPF_DEBUG)!=0) refreshDbgFrameLogs();
				long cur_time = System.currentTimeMillis();
				if (!running) {
					if (anim!=null) {
						anim.stop();
						anim=null;
					}
					// wait in portions of 1/2 sec until running is set;
					// reset target time
					Thread.sleep(500);
					target_time = cur_time+(long)(1000.0/el.fps);
					// ensure screen remains updated on exposure events
					canvas.repaint();
				} else if (gl_driven_update) {
					// display() starts animator
					if (anim==null) canvas.repaint();
					// Animator takes care of game state update
					Thread.sleep(500);
					target_time = cur_time+(long)(1000.0/el.fps);
				} else if (cur_time < target_time+(long)(500.0/el.fps)) {
					// we lag behind less than 1/2 frame -> do full frame.
					// This empirically produces the smoothest animation
					synchronized (el.objects) {
						doFrameAll();
						el.updateViewOffset();
					}
					canvas.repaint();
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
				} else {
					// we lag behind a little -> frame skip
					synchronized (el.objects) {
						doFrameAll();
						el.updateViewOffset();
					}
					// if we skip too many frames in succession, draw a frame
					if ((++frames_skipped) > el.maxframeskip) {
						canvas.repaint();
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
			exitEngine("Error in main:\n"+dbgExceptionToString(e));
		} }
	}


	/*===== audio =====*/

//	/** TEST **/
//
//	/* String -> Clip */
//	Hashtable audioclips = new Hashtable();
//	/* String -> AudioInputStream */
//	Hashtable audioiss = new Hashtable();
//
//
//	private void loadAudioClip(String clipid) {
//		String filename = (String)el.audioclips.get(clipid);
//		try {
//			URL url = this.getClass().getResource(filename);
//			AudioInputStream ins=AudioSystem.getAudioInputStream(url);
//			AudioFormat format = ins.getFormat();
//			DataLine.Info info = new DataLine.Info(Clip.class, format);
//			Clip clip = (Clip)AudioSystem.getLine(info);
//			//Clip clip = AudioSystem.getClip();
//			clip.open(ins);
//			audioclips.put(clipid,clip);
//			audioiss.put(clipid,ins);
//		} catch (UnsupportedAudioFileException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (LineUnavailableException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public void playAudio(String clipid) {
//		Clip clip = (Clip) audioclips.get(clipid);
//		AudioInputStream ins = (AudioInputStream) audioiss.get(clipid);
//		if (clip==null || ins==null) {
//			loadAudioClip(clipid);
//			clip = (Clip) audioclips.get(clipid);
//			ins = (AudioInputStream) audioiss.get(clipid);
//		}
//		//if (clip.isRunning()) clip.stop();
//		//if (clip.isOpen()) clip.close();
//		//try {Thread.sleep(1000); } catch (Exception e) {}
//		/*try {
//			clip.open(ins);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}*/
//		new Thread(new ClipRunnable(clip)).start();
//		//if (clip.isRunning()) return;//clip.stop();
//		//clip.start();
//	}
//
//	class ClipRunnable implements Runnable {
//		Clip clip;
//		ClipRunnable(Clip clip) {
//			this.clip = clip;
//		}
//		public void run() {
//			clip.stop();
//			try {Thread.sleep(50); } catch (Exception e) {}
//			clip.setFramePosition(0); // rewind
//			//clip.setMicrosecondPosition(0); // rewind
//			clip.start();
//		}
//	}

	/** ORIGINAL **/

	public void enableAudio() { jre.enableAudio(); }

	public void disableAudio() { jre.disableAudio(); }

	public void defineAudioClip(String clipid,String filename) {
		el.defineAudioClip(this,clipid,filename);
	}

	public String lastPlayedAudio(String channel) { return jre.lastPlayedAudio(channel); }

	public void playAudio(String clipid) { jre.playAudio(this,clipid); }

	public void playAudio(String channel,String clipid,boolean loop) {
		jre.playAudio(this,channel,clipid,loop);
	}

	public void stopAudio(String channel) { jre.stopAudio(channel); }

	public void stopAudio() { jre.stopAudio(); }


	/*===== store =====*/

	public void storeWriteInt(String id,int value) {
		jre.storeWriteInt(id,value);
	}

	public void storeWriteDouble(String id,double value) {
		jre.storeWriteDouble(id,value);
	}

	public void storeWriteString(String id,String value) {
		jre.storeWriteString(id,value);
	}

	public void storeRemove(String id) {
		jre.storeRemove(id);
	}

	public boolean storeExists(String id) {
		return jre.storeExists(id);
	}

	public int storeReadInt(String id,int undef) {
		return jre.storeReadInt(id,undef);
	}

	public double storeReadDouble(String id,double undef) {
		return jre.storeReadDouble(id,undef);
	}

	public String storeReadString(String id,String undef) {
		return jre.storeReadString(id,undef);
	}

	/*====== options ======*/

	public void optsAddTitle(String title) {
		jre.optsAddTitle(title);
	}

	public void optsAddNumber(String varname,String title,String desc,
	int decimals, double lower,double upper,double step, double initial) {
		jre.optsAddNumber(varname,title,desc,decimals,lower,upper,step,initial);
	}
	public void optsAddBoolean(String varname,String title,String desc,
	boolean initial) {
		jre.optsAddBoolean(varname,title,desc,initial);
	}
	public void optsAddEnum(String varname,String title,String desc,
	String [] values, int initial) {
		jre.optsAddEnum(varname,title,desc,values,initial);
	}

	public void optsAddKey(String varname,String title,String desc,int initial){
		jre.optsAddKey(varname,title,desc,initial);
	}

	public void optsAddString(String varname,String title,String desc,
	int maxlen, boolean isPassword, String initial) {
		jre.optsAddString(varname,title,desc,maxlen,isPassword,initial);
	}

	public void optsClear() {
		jre.optsClear();
	}

}

