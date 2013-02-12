package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 4: tiles and collision.  Like example 3, only the
 * pac-mans now collide with each other and with tiles.  This example
 * illustrates the use of collision IDs (aka cids or colids).  Both tiles and
 * objects have collision IDs, which can be used to check different kinds of
 * collision between different objects/tiles.
 */
public class Example4 extends JGEngine {

	public static void main(String [] args) {
		new Example4(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example4(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example4() { initEngineApplet(); }

	public void initCanvas() {
		// we set the background colour to same colour as the splash background
		setCanvasSettings(20,15,16,16,JGColor.black,new JGColor(255,246,199),null);
	}

	public void initGame() {
		setFrameRate(35,2);
		defineMedia("example3.tbl");
		setBGImage("mybackground");
		// create some game objects
		for (int i=0; i<20; i++)
			new MyObject();
		// create some tiles. "#" is our marble tile, "." is an empty space.
		setTiles(
			2, // tile x index
			2, // tile y index
			new String[] { "#####","#","#","#" }
				// A series of tiles. Each String represents a line of tiles.
		);
		setTiles(13,2,new String[] { "#####","....#","....#","....#" });
		setTiles(13,9,new String[] { "....#","....#","....#","#####" });
		setTiles(2,9,new String[] { "#....","#....","#....","#####" });
		// define the off-playfield tiles
		setTileSettings(
			"#", // tile that is found out of the playfield bounds
			2,   // tile cid found out of playfield bounds
			0    // which cids to preserve when setting a tile (not used here).
		);
	}

	public void doFrame() {
		moveObjects(null,0);
		// check object-object collision
		checkCollision(
			1, // cids of objects that our objects should collide with
			1  // cids of the objects whose hit() should be called
		);
		// check object-tile collision
		checkBGCollision(
			1+2, // collide with the marble and border tiles
			1    // cids of our objects
		);
	}

	/** Our user-defined object, which now bounces off other object and tiles.*/
	class MyObject extends JGObject {

		MyObject () {
			super("myobject",true, // name
				Example4.this.random(3*16,pfWidth()-4*16),  // xpos
				Example4.this.random(3*16,pfHeight()-4*16), // ypos
				1, // collision ID
				"myanim_l"// name of sprite or animation to use.
			);
			xspeed = random(-2,2);
			yspeed = random(-2,2);
		}

		/** Update the object. This method is called by moveObjects. */
		public void move() {
			if (xspeed < 0) setGraphic("myanim_l"); else setGraphic("myanim_r");
			// We don't need to check for the borders of the screen, like
			// we did in example 3.  Border collision is now handled by hit_bg.
		}

		/** Handle collision with background. Called by checkBGCollision.
		* Tilecid is the combined (ORed) CID of all tiles that this
		* object collides with.  Note: there are two other variants
		* of hit_bg available, namely one passing tilecid plus tile
		* coordinates for each tile that the object collides with, and one
		* passing the tile range that the object overlaps with at the moment
		* of collision.  */
		public void hit_bg(int tilecid) {
			// Look around to see which direction is free.  If we find a free
			// direction, move that way.
			if (!and(checkBGCollision(-xspeed,yspeed),3)) {
				xspeed = -xspeed;
			} else if (!and(checkBGCollision(xspeed,-yspeed),3)) {
				yspeed = -yspeed;
			} else if (!and(checkBGCollision(xspeed,-yspeed),3)
			&&         !and(checkBGCollision(-xspeed,-yspeed),3) ) {
				xspeed = -xspeed;
				yspeed = -yspeed;
			}
			// else do nothing. You might think this case never occurs
			// (otherwise, why would the object have collided?), but it
			// does occur because object-object collision might already
			// have reversed the direction of this object.  This is the kind
			// of stuff that makes object interaction difficult.
		}

		/** Handle collision with other objects. Called by checkCollision. */
		public void hit(JGObject obj) {
			// As a reaction to an object collision, we bounce in the
			// direction we came from.  We only do this when the area in that
			// direction seems clear of other objects, otherwise we might
			// start oscillating back and forth.
			// This collision problem is much more difficult than the tile
			// collision problem, because there may be multiple simultaneous
			// collisions, and the other objects are also moving at different
			// speeds.
			// We look ahead several steps in the opposite direction to see
			// if any other object is there.
			if (checkCollision(1,-3*xspeed,-3*yspeed)==0) {
				// reverse direction
				xspeed = -xspeed;
				yspeed = -yspeed;
			}
		}

	} /* end class MyObject */

}
