package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 1: a minimal program.  A "bare skeleton" program
 * displaying a moving text "hello world".
 *
 * In order to run as both applet and application, you need to define a main()
 * method (this is the entry point for an application) and a parameterless
 * constructor (this is the entry point for an applet).  We use a second
 * constructor with a size parameter to initialise the engine as an
 * application.
 */
public class Example1 extends JGEngine {

	public static void main(String [] args) {
		// We start the engine with a fixed window size (which happens to
		// be twice the size of the defined playfield, scaling the playfield
		// by a factor 2).  Normally, you'd want this size to be configurable,
		// for example by means of command line parameters.
		new Example1(new JGPoint(640,480));
	}

	/** The parameterless constructor is called by the browser, in case we're
	 * an applet. */
	public Example1() {
		// This inits the engine as an applet.
		initEngineApplet(); 
	}

	/** We use a separate constructor for starting as an application. */
	public Example1(JGPoint size) {
		// This inits the engine as an application.
		initEngine(size.x,size.y); 
	}

	/** This method is called by the engine when it is ready to intialise the
	 * canvas (for an applet, this is after the browser has called init()).
	 * Note that applet parameters become available here and not
	 * earlier (don't try to access them from within the parameterless
	 * constructor!).  Use isApplet() to check if we started as an applet.
	 */
	public void initCanvas() {
		// The only thing we need to do in this method is to tell the engine
		// what canvas settings to use.  We should not yet call any of the
		// other game engine methods here!
		setCanvasSettings(
			20,  // width of the canvas in tiles
			15,  // height of the canvas in tiles
			16,  // width of one tile
			16,  // height of one tile
			     //    (note: total size = 20*16=320  x  15*16=240)
			null,// foreground colour -> use default colour white
			null,// background colour -> use default colour black
			null // standard font -> use default font
		);
	}

	/** This method is called when the engine has finished initialising and is
	 * ready to produce frames.  Note that the engine logic runs in its own
	 * thread, separate from the AWT event thread, which is used for painting
	 * only.  This method is the first to be called from within its thread.
	 * During this method, the game window shows the intro screen. */
	public void initGame() {
		// We can set the frame rate, load graphics, etc, at this point. 
		// (note that we can also do any of these later on if we wish)
		setFrameRate(
			35,// 35 = frame rate, 35 frames per second
			2  //  2 = frame skip, skip at most 2 frames before displaying
			   //      a frame again
		);
	}

	/** A timer used to animate the "hello world" text. */
	double texttimer=0;

	/** Game logic is done here.  No painting can be done here, define
	* paintFrame to do that. */
	public void doFrame() {
		// Increment the angle of the moving text.
		texttimer += 0.05;
	}

	/** Any graphics drawing beside objects or tiles should be done here.
	 * Usually, only status / HUD information needs to be drawn here. */
	public void paintFrame() {
		setColor(JGColor.yellow);
		// Draw a text that moves around in a circle.
		// Note: viewWidth returns the width of the view;
		//       viewHeight the height.
		drawString("Hello world",
			viewWidth()/2  + 50*Math.sin(texttimer), // xpos
			viewHeight()/2 + 50*Math.cos(texttimer), // ypos
			0 // the text alignment
			  // (-1 is left-aligned, 0 is centered, 1 is right-aligned)
		);
	}
}
