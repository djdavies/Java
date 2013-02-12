package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 2: game objects.  Animates some simple game objects.
 */
public class Example2 extends JGEngine {

	public static void main(String [] args) {
		new Example2(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example2(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example2() { initEngineApplet(); }

	public void initCanvas() { setCanvasSettings(20,15,16,16,null,null,null); }

	public void initGame() {
		setFrameRate(35,2);
		// create some game objects
		// (see below for the class definition)
		for (int i=0; i<20; i++)
			new MyObject();
	}

	public void doFrame() {
		// Move all objects.
		moveObjects(
			null,// object name prefix of objects to move (null means any)
			0    // object collision ID of objects to move (0 means any)
		);
	}

	/** We don't need to define paintFrame in this example. */
	public void paintFrame() {}

	/** Our user-defined object. */
	class MyObject extends JGObject {

		/** Constructor. */
		MyObject () {
			// Initialise game object by calling an appropriate constructor
			// in the JGObject class.
			super(
				"myobject",// name by which the object is known
				true,//true means add a unique ID number after the object name.
				     //If we don't do this, this object will replace any object
				     //with the same name.
				Example2.this.random(0,pfWidth()),  // X position
				Example2.this.random(0,pfHeight()), // Y position
				1, // the object's collision ID (used to determine which classes
				   // of objects should collide with each other)
				null // name of sprite or animation to use (null is none)
			);
			// Give the object an initial speed in a random direction.
			xspeed = random(-2,2);
			yspeed = random(-2,2);
		}

		/** Update the object. This method is called by moveObjects. */
		public void move() {
			// A very "classic" behaviour:
			// bounce off the borders of the screen.
			if (x >  pfWidth()-8 && xspeed>0) xspeed = -xspeed;
			if (x <            8 && xspeed<0) xspeed = -xspeed;
			if (y > pfHeight()-8 && yspeed>0) yspeed = -yspeed;
			if (y <            8 && yspeed<0) yspeed = -yspeed;
			// xspeed and yspeed are added to x and y automatically at the end
			// of the move() method.
		}

		/** Draw the object. */
		public void paint() {
			// Draw a yellow ball
			setColor(JGColor.yellow);
			drawOval(x,y,16,16,true,true);
		}

	} /* end class MyObject */

}
