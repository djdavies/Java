package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 5: user input.  Illustrates the user's keyboard state,
 * and mouse position and buttons.
 */
public class Example5 extends JGEngine {

	public static void main(String [] args) {
		new Example5(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example5(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example5() { initEngineApplet(); }

	/** Note: we have doubled the playfield size here. */
	public void initCanvas() { setCanvasSettings(40,30,16,16,null,null,null); }

	public void initGame() {
		setFrameRate(35,2);
		// load a single sprite
		defineImage(
			"ball", // graphic name
			"-", 0, // tile name and tile cid (in case we use it as a tile)
			"ball20-red.gif", // file
			"-" // graphical operation (may be one or two out of
			    //"x","y", "l","r","u")
		);
		// hide the mouse cursor
		setCursor(null);
	}

	/** A simple timer. */
	int gametimer=0;
	/** Mouse position of previous frame. */
	int prevmousex=0,prevmousey=0;

	public void doFrame() {
		moveObjects(null,0);
		gametimer++;
		// release objects when we press the mouse button or the shift key
		// The mouse button releases a continuous stream of objects, the shift
		// key only one.
		if (getMouseButton(1) || getKey(KeyShift)) {
			// When the shift key is pressed, we only want to release one
			// object, so the user can release them one at a time.
			// We can achieve this in a simple manner by clearing the key
			// state.  It will only be set again if the key is pressed again
			// (or autorepeat activates).  We can achieve the same thing for
			// the mouse button with clearMouseButton(1).  If we don't do
			// this, shift will act like the mouse button does.
			clearKey(KeyShift);
			// release a simple object in the direction in which the mouse moves
			new JGObject("obj",true, // name
				getMouseX()-10, getMouseY()-10, // coordinate
				1, "ball", // cid, sprite
				(getMouseX()-prevmousex)/3.0,
				(getMouseY()-prevmousey)/3.0, // object speed
				70 // expiry timer (expire after 70 frames)
			);
		}
		// remove all objects with the Enter key
		if (getKey(KeyEnter)) {
			// remove all objects
			removeObjects(null,0);
		}
		// remember old mouse position
		prevmousex = getMouseX();
		prevmousey = getMouseY();
	}

	public void paintFrame() {
		setColor(JGColor.white);
		// draw a custom mouse cursor: an animated rectangle
		drawRect(
			getMouseX(),getMouseY(), // coordinate equals mouse coordinate
			gametimer%20, gametimer%20, // vary size of rectangle
			false, // rectangle is not filled
			true   // center rectangle
		);
		// draw a description of the last key pressed
		if (getLastKey()!=0) {
			// getLastKey gets the last key pressed.
			// getKeyDesc translates a keycode to a human-readable String
			drawString("You pressed: "+getKeyDesc(getLastKey()),
				pfWidth()/2, 50, 0);
		} else {
			drawString("Press a key!", pfWidth()/2, 50, 0);
		}
		// draw some instructions
		drawString("Press the mouse button to launch objects",
			pfWidth()/2, 110, 0);
		drawString("Press the shift key to launch one object",
			pfWidth()/2, 140, 0);
		drawString("Press the enter key to remove all objects",
			pfWidth()/2, 170, 0);
	}
}
