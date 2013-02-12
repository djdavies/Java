package examples;
import jgame.*;

/** A standard object that moves around a maze. Typical application: pac-man.
 * This class only defines the move() method; hit() and hit_bg() are not used.
 * Subclass it to customize.  To customize move(), simply call super.move()
 * somewhere inside your move method.  Note that this class's move() works by
 * determining the appropriate xdir and ydir.  You can do some limited
 * operations on xdir and ydir before calling super.move().  In particular,
 * reversing direction can be done without problems.  It is assumed the object
 * is 1x1 tile in size, and the tileBBox is set to this.

 * <p>The public fields are the configuration fields which can be changed
 * at will during the object's lifetime.

 * <p>The object homes in on the specified home_in object, or can be made to
 * avoid it.  The object can be made to move randomly instead of homing in/
 * avoiding, with a specified probability between 0 and 1.  Not setting
 * home_in will make the object move randomly.

 * <p>The object tries to keep moving forward, and never make u-turns (like
 * pac-man ghosts) to help get it around cul de sacs.  There is no AI for
 * finding complex routes through the maze.

 */
public class StdMazeMonster extends JGObject {
	/* config */
	/** null indicates non-directional graphic */
	public String gfx_prefix=null;
	/** cid mask of tiles that block */
	public int block_mask;
	/** object's speed, overrides xspeed, yspeed. */
	public double speed;
	/** object to home in on, null=none (random movement) */
	public JGObject home_in;
	/** true = avoid home_in position */
	public boolean avoid;
	/** chance that object moves randomly */
	public double random_proportion;
	/* state */
	int newxdir,newydir;
	JGPoint cen; // center tile
	/** When initialised, the object will snap to grid to the nearest free
	 * position, then it will start moving in the direction specified by
	 * xdir, ydir.  The object's graphic can be made
	 * directional by setting is_directional.  This will add the suffix "l",
	 * "r", "u", or "d" to the graphic string to indicate the direction of
	 * movement.
	* @param x  position in pixels
	* @param y  position in pixels
	* @param graphic  prefix of graphic
	* @param is_directional  add direction suffix to graphic
	* @param home_in  object to home in on, null=none (random movement)
	* @param avoid  true = avoid home_in position
	* @param random_proportion chance that object moves randomly
	* @param block_mask  cid mask of tiles that block
	*/
	public StdMazeMonster(String name,boolean unique_id,
	double x,double y, int cid,
	String graphic, boolean is_directional,
	int block_mask,int xdir,int ydir, double speed,
	JGObject home_in,boolean avoid,double random_proportion) {
		super(name,unique_id,x,y,cid, null);
		setDirSpeed(xdir,ydir, speed);
		snapToGrid(eng.tileWidth(),eng.tileHeight());
		setTileBBox(0,0,eng.tileWidth(),eng.tileHeight());
		if (is_directional) gfx_prefix=graphic;
		this.block_mask=block_mask;
		this.speed=speed;
		this.home_in=home_in;
		this.avoid=avoid;
		this.random_proportion=random_proportion;
	}
	/** Moves the object around.  If you override this, be sure to call
	 * super.move() and don't touch x, y, xspeed, yspeed. Don't touch xdir,
	 * ydir except by possibly reversing them. */
	public void move() {
		cen = getCenterTile();
		double gamespeed = eng.getGameSpeed();
		if (isXAligned(xspeed*gamespeed/1.1)
		&&  isYAligned(yspeed*gamespeed/1.1)) {
			snapToGrid(xspeed*gamespeed/1.1,yspeed*gamespeed/1.1);
			// determine preferred direction
			if (home_in==null 
			||  eng.random(0,0.9999) < random_proportion) { // random
				chooseRandomDir();
			} else { // home in
				int basedir = avoid ? -1 : 1;
				if (!avoid) { // prefer diagonal approach
					if (Math.abs(x-home_in.x) > Math.abs(y-home_in.y)) {
						newxdir = (x > home_in.x) ? -basedir : basedir;
						newydir=0;
					} else {
						newxdir=0;
						newydir = (y > home_in.y) ? -basedir : basedir;
					}
				} else {
					if (Math.abs(x-home_in.x) < Math.abs(y-home_in.y)) {
						newxdir = (x > home_in.x) ? -basedir : basedir;
						newydir=0;
					} else {
						newxdir=0;
						if (Math.abs(y-home_in.y) < 1.0) { // same y position
							newydir = eng.random(-1,1,2);
						} else {
							newydir = (y > home_in.y) ? -basedir : basedir;
						}
					}
				}
				// if preferred direction is blocked or a u-turn, try the
				// other one
				if (and(eng.getTileCid(cen,newxdir,newydir),block_mask)
				||  newxdir == -xdir && newydir == -ydir) {
					if (newxdir!=0) { // try approaching vertically
						newxdir=0;
						newydir = (y > home_in.y) ? -basedir : basedir;
					} else { // or horizontally
						newxdir = (x > home_in.x) ? -basedir : basedir;
						newydir=0;
					}
				}
			}
			if (newxdir == -xdir && newydir == -ydir) { // refuse u-turn
				newxdir = xdir;
				newydir = ydir;
			}
			if (and(eng.getTileCid(cen,newxdir,newydir),block_mask)) {
				// we're blocked in our new direction ->
				// try a random direction
				chooseRandomDir();
			}
			setDir(newxdir,newydir);
		}
		if (gfx_prefix!=null) {
			if (xdir == 1) setGraphic(gfx_prefix+"r");
			else if (xdir == -1) setGraphic(gfx_prefix+"l");
			else if (ydir == 1) setGraphic(gfx_prefix+"d");
			else if (ydir == -1) setGraphic(gfx_prefix+"u");
		}
	}
	private void chooseRandomDir() {
		newxdir=-xdir;
		newydir=-ydir;
		// XXX we never try going straight on first
		int rot_dir = eng.random(-1,1,2);
		// try all 4 compass directions, try u-turn last
		for (int i=0; i<4; i++) {
			/* rotate 90 degrees */
			int newxdir_t =  rot_dir*newydir;
			newydir =       -rot_dir*newxdir;
			newxdir = newxdir_t;
			if (!and(eng.getTileCid(cen,newxdir,newydir),block_mask)) return;
		}
		// blocked on all sides
		newxdir=0;
		newydir=0;
	}
}
