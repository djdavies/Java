package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 3: handling the different media.  An embellished version
 * of example 2, demonstrating media tables.  A media table defines a splash
 * screen, a backdrop image, a font, a sound, and an animation.
 */
public class Example3 extends JGEngine {

	public static void main(String [] args) {
		new Example3(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example3(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example3() { initEngineApplet(); }

	public void initCanvas() {
		// we set the background colour to same colour as the splash background
		setCanvasSettings(20,15,16,16,JGColor.black,new JGColor(255,246,199),null);
	}

	public void initGame() {
		setFrameRate(35,2);
		// load the media, defining:
		// "mybackground" - image that can be used as background image
		// "myanim_l" - animated pacman looking left
		// "myanim_r" - animated pacman looking right
		// "bounce"  - bounce sound
		// "myfont"  - image font
		// (see the table file for more explanation)
		defineMedia("example3.tbl");
		// Set the decorative background image
		setBGImage("mybackground");
		// wait a bit to show what the splash screen looks like
		try { Thread.sleep(4000); }
		catch (InterruptedException e) {}
		// create some game objects
		for (int i=0; i<20; i++)
			new MyObject();
	}

	public void doFrame() {
		// Move all objects.
		moveObjects(null,0);
	}

	public void paintFrame() {
		//Paints a line of text with our image font.
		drawImageString("HELLO WORLD",
			pfWidth()/2, 16, // coordinate
			0,        // center text
			"myfont", // image map to use
			32,       // character code of first sprite in image map
			4         // spacing, number of pixels to skip between letters
		);
	}

	/** Our user-defined object, much the same as in Example2, except the
	 * graphic is now a sprite animation rather than a sphere. */
	class MyObject extends JGObject {

		/** Constructor. */
		MyObject () {
			super("myobject",true, // name
				Example3.this.random(0,pfWidth()),
				Example3.this.random(0,pfHeight()), // position
				1, // collision ID
				"myanim_l"// name of sprite or animation to use.
				          // This should be the name as defined in defineImage
				          // or the media table.
			);
			// move in random direction.
			xspeed = random(-2,2);
			yspeed = random(-2,2);
		}

		/** Update the object. This method is called by moveObjects. */
		public void move() {
			// bounce off the borders of the screen, play a sound when
			// bouncing.
			if ( (x >  pfWidth()-16 && xspeed>0)
			||   (x <            0  && xspeed<0) ) {
				xspeed = -xspeed;
				playAudio("bounce");
			}
			if ( (y > pfHeight()-16 && yspeed>0)
			||   (y <            0  && yspeed<0) ) {
				yspeed = -yspeed;
				playAudio("bounce");
			}
			// Make pacman face left or right, depending on X direction.
			if (xspeed < 0) setGraphic("myanim_l"); else setGraphic("myanim_r");
		}

	} /* end class MyObject */

}
