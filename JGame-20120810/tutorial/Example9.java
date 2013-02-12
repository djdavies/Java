package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 9: Using OpenGL.   Demostrates opengl-specific features.
 */
public class Example9 extends JGEngine {

	public static void main(String [] args) {
		// Listens to command line parameters [width] [height].
		// By default it starts as full-screen.
		//new Example9(StdGame.parseSizeArgs(args,0));
		new Example9(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example9(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example9() { initEngineApplet(); }

	public void initCanvas() { setCanvasSettings(40,30,16,16,null,null,null); }

	public void initGame() {
		// set video synced mode, consider 35 frames per second to be the
		// "normal" speed.
		setFrameRate(35,1);
		setVideoSyncedUpdate(true);
		// load some nice translucent images
		defineImage("neon1","-",0,"diamond1.png","-");
		defineImage("neon2","-",0,"diamond2.png","-");
		defineImage("neon3","-",0,"diamond3.png","-");
		// define background images for 4 layers of parallax scroll
		defineImage("bgimage","-",0,"twirly-192-rev-trans2.png","-");
		defineImage("bgimage-sm","-",0,"twirly-192-trans-sm.png","-");
		defineImage("bgimage-sm2","-",0,"twirly-192-trans-smsm.png","-");
		defineImage("bgimage-text","-",0,"parallax-illustration.png","-");
		setBGImage("bgimage");
		setBGImage(1,"bgimage-text",false,false);
		setBGImage(2,"bgimage-sm",true,true);
		setBGImage(3,"bgimage-sm2",true,true);
		// define colour behind lowest layer
		setBGColor(JGColor.yellow);
		// create some objects
		for (int i=0; i<25; i++) new NeonObject();
		// make the objects wrap around nicely
		setPFWrap(true,true,-50,-50);
		setPFSize(46,36);
	}

	// a translucent rotating zooming object
	class NeonObject extends JGObject {
		int type;
		double rot=0, rotinc;
		NeonObject() {
			super("neon",true,
				Example9.this.random(0,viewWidth()-50),
				Example9.this.random(0,viewHeight()-50),
				1,null, 
				Example9.this.random(-2,2), Example9.this.random(-2,2));
			rotinc = random(-0.07,0.07);
			type = random(1,3,1);
		}
		public void move() {
			// we may be running at variable frame rate: ensure that rotation
			// speed is constant by multiplying with gamespeed.
			rot += rotinc*getGameSpeed();
		}
		public void paint() {
			// blend mode is: source multiplier: alpha/destination multiplier: 1
			setBlendMode(1,0);
			// the extended openGL drawImage method
			drawImage(x, y, "neon"+type, /* regular image parameters */
				new JGColor(1.0,1.0,1.0), /* blend colour */
				0.5, /* alpha */
				rot, /* rotation */
				1.0 + 0.15*Math.sin(rot*4.3), /* zoom */
				true /* relative to playfield */
			);
		}
	}

	double zoom=1.0;
	double rotate=0;

	public void doFrame() {
		// scroll the different layers
		setViewOffset((int)(200*Math.sin(rot)),(int)(200*Math.cos(rot)), false);
		setBGImgOffset(1,-getMouseX(),-getMouseY(),false);
		setBGImgOffset(2,(200*Math.sin(rot))/2,(200*Math.cos(rot))/2, false);
		setBGImgOffset(3,(200*Math.sin(rot))/4,(200*Math.cos(rot))/4, false);
		// move the neon objects
		moveObjects(null,0);
		// increment the scroll animation
		rot += getGameSpeed()*0.01;
		// key handing
		if (getKey('V')) {
			setVideoSyncedUpdate(!getVideoSyncedUpdate());
			clearKey('V');
		}
		if (getKey(27)) {
			exitEngine("User exit");
		}
		// create zoom/rotate effect with mouse button
		if (getMouseButton(1)) {
			zoom += 0.02;
			rotate += 0.01;
		} else {
			if (zoom>1.0) zoom -= 0.02;
			if (zoom<1.0) zoom = 1.0;
			if (rotate>0) rotate -= 0.01;
			if (rotate<0) rotate = 0.0;
		}
		setViewZoomRotate(zoom,rotate);
	}

	double [] xpos = new double [5];
	double [] ypos = new double [5];
	double rot = 0.0;

	public void paintFrame() {
		// colour to use in case we do not have the gradient function
		setColor(JGColor.blue);
		setStroke(3.0);
		// blend mode is: source multiplier: alpha, destination multiplier: 1
		setBlendMode(1,0);
		// draw rectangle with gradient
		drawRect(viewWidth()/2,180,400,150,true,true,false, new JGColor[]
			{ JGColor.yellow, JGColor.red, JGColor.magenta, JGColor.blue});
		// draw polygons with gradient
		int i=0;
		for (double r=rot; r<rot+Math.PI*2.0; r += Math.PI*2.001/5.0) {
			xpos[i] = viewWidth()*(0.5 + 0.25 *Math.sin(r));
			ypos[i++] = viewHeight()*(0.5 + 0.4 *Math.cos(r));
		}
		drawPolygon(xpos,ypos,
			new JGColor [] { JGColor.blue, JGColor.red, JGColor.magenta,
				JGColor.cyan, JGColor.yellow }, xpos.length, true, false);
		i=0;
		for (double r=-rot; r<-rot+Math.PI*2.0; r += Math.PI*2.001/5.0) {
			xpos[i] = viewWidth()*(0.5 + 0.3 *Math.sin(r));
			ypos[i++] = viewHeight()*(0.5 + 0.45 *Math.cos(r));
		}
		drawPolygon(xpos,ypos,
			new JGColor [] { JGColor.blue, JGColor.red, JGColor.magenta, JGColor.cyan,
				JGColor.yellow }, xpos.length, false, false);
		// display some information
		setFont(new JGFont("arial",0,15));
		setColor(new JGColor(0,0.2,0,0.8));
		drawString("Back-end used: " + (isOpenGL() ? "OpenGL" : "AWT" ),
			viewWidth()/2,135,0);
		if (isOpenGL()) {
			drawString("Press 'V' to switch between normal and video synced.",
				viewWidth()/2,160,0);
		} else {
			drawString("(No video synced mode available in AWT)",
				viewWidth()/2,160,0);
		}
		setFont(new JGFont("arial",0,25));
		drawString("Video synced now: "+ (getVideoSyncedUpdate() ?"ON":"OFF"),
			viewWidth()/2,200,0);
	}

}
