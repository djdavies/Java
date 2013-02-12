package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 6: scrolling and wrapping. */
public class Example6 extends JGEngine {

	public static void main(String [] args) {
		new Example6(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example6(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example6() { initEngineApplet(); }

	public void initCanvas() { setCanvasSettings(40,30,16,16,null,null,null); }

	public void initGame() {
		// we increase the framerate a bit because it is more critical
		// for scrolling.
		setFrameRate(45,2);
		// load a single sprite
		defineImage(
			"ball", // graphic name
			"-", 0, // tile name and tile cid (in case we use it as a tile)
			"ball20-red.gif", // file
			"-" // graphical operation (may be one or two out of
			    //"x","y", "l","r","u")
		);
		// load the background image
		defineImage("bgimage","-",0,"twirly-192.gif","-");
		setBGImage("bgimage");
		// hide the mouse cursor
		//setCursor(null);
		// Increase the playfield size to obtain a scrollable, wrappable area.
		// It is twice the width of the view, and two tiles higher.  The two
		// tiles are used as space for objects to disappear into before
		// wrapping around.  Without this space, objects would visibly jump
		// from top to bottom or vice versa.
		setPFSize(80,32);
		// Set the wrap-around mode to vertical.
		setPFWrap(
			false, // horizontal wrap
			true,  // vertical wrap
			-10, -10 // shift the center of the view to make objects wrap at
			       // the right moment (sprite size / 2).
		);
		// place some random objects that move up and down
		for (int i=0; i<40; i++) {
			new JGObject("muyobj", true, // name
				random(0,pfWidth()-20), random(0,pfHeight()-20), // pos
				1, "ball", // cid, sprite
				0.0, random(-2.0,2.0), // speed
				JGObject.suspend_off_view // Suspend when off view.
				    // Suspended objects will not move or participate in
					// collisions.
			);
		}
	}

	/** View offset. */
	int xofs=0,yofs=0;

	public void doFrame() {
		moveObjects(null,0);
		// Update the desired view offset according to mouse position.
		// The X offset is proportional to the mouse position.
		// The mouse position is a number between 0 and viewWidth.
		// We scale it to a number between 0 and pfWidth (playfield width).
		xofs =  getMouseX() * pfWidth() / viewWidth();
		// the Y offset changes proportional to the offset of the mouse
		// position from the center of the window.  If the mouse is in the
		// center, we don't scroll, if it is close to the upper or lower
		// border of the window, it scrolls quickly in that direction.
		yofs += (getMouseY() - viewHeight()/2) / 15;
		// Set the view offset.  Note that if our offset is out of the
		// playfield bounds, the position is clipped so that it is inside.
		// (this is only relevant for non-wrappable axes; a wrappable
		// axis is never out of bounds!)
		setViewOffset(
			xofs,yofs, // the position within the playfield
			true       // true means the given position is center of the view,
			           // false means it is topleft.
		);
	}

	public void paintFrame() {
		setColor(JGColor.black);
		setFont(new JGFont("Arial",0,20));
		// draw some info (note: the text is drawn relative to the view,
		// rather than the playfield)
		drawString("Move the mouse to scroll.", viewWidth()/2, 80, 0);
		drawString("Playfield offset is now ("+xofs+","+yofs+").",
			viewWidth()/2, 120, 0);
		// Indicate the corners of the playfield.  This text should be drawn
		// relative to the playfield.  The other draw methods can also draw
		// both relative to playfield and to view.  For text, relative to view
		// is the default; for other draw methods, relative to playfield is
		// default.
		drawString("TOP LEFT",     0,         8,             -1,
			true // indicate it should be drawn relative to playfield
		);
		drawString("BOTTOM LEFT",  0,         pfHeight()-20, -1, true);
		drawString("TOP RIGHT",    pfWidth(), 8,              1, true);
		drawString("BOTTOM RIGHT", pfWidth(), pfHeight()-20,  1, true);
	}
}
