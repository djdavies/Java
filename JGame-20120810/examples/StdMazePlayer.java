package examples;
import jgame.*;

/** A standard object that allows the player to move in 4 directions around a
 * maze. Typical application: pac-man.  This class only defines the move()
 * method; hit() and hit_bg() are not used.  Subclass it to customize.  To
 * customize move(), call super.move() somewhere inside your move method.  You
 * are not adviced to touch x, y, xdir, ydir, xspeed, yspeed.  It is assumed
 * the object is 1x1 tile in size, and the tileBBox is set to this.

 * <p>The public fields are the configuration fields which can be changed
 * at will during the object's lifetime.

 * <p>You can make movement stop by setting the stop_moving flag.  The player
 * will move on to the next aligned position, then halt until the flag is
 * cleared.  In some games, the player should freeze when the movement keys
 * are used for firing or special actions.

 */
public class StdMazePlayer extends JGObject {
	/* config */
	/** null indicates non-directional graphic */
	public String gfx_prefix=null;
	/** indicates current graphic suffix, "" is none */
	public String cur_gfx_suffix="";
	/** don't stop animating when player stops moving */
	public boolean continuous_anim;
	/** cid mask of tiles that block */
	public int block_mask;
	/** object's speed, overrides xspeed, yspeed. */
	public double speed;
	/** movement key */
	public int key_up,key_down,key_left,key_right;
	/** set to true to disable movement (for example, for firing) */
	public boolean stop_moving;
	/* state */
	int moving=0;
	/** When initialised, the object will snap to grid to the nearest free
	 * position.  The object's graphic can be made
	 * directional by setting is_directional.  This will add the suffix "u",
	 * "d", "l", or "r" to the graphic string to indicate the direction of
	 * movement.
	* @param x  position in pixels
	* @param y  position in pixels
	* @param graphic  prefix of graphic
	* @param is_directional  true = add direction suffix to graphic
	* @param continuous_anim  don't stop animating when player stops moving
	* @param block_mask  cid mask of tiles that block
	*/
	public StdMazePlayer(String name, double x,double y, int cid,
	String graphic, boolean is_directional, boolean continuous_anim,
	int block_mask, double speed,
	int key_up,int key_down, int key_left, int key_right) {
		super(name,false, x,y, cid, graphic);
		snapToGrid(eng.tileWidth(),eng.tileHeight());
		setTileBBox(0,0,eng.tileWidth(),eng.tileHeight());
		setDir(0,1);
		if (is_directional) gfx_prefix=graphic;
		this.continuous_anim=continuous_anim;
		this.key_up=key_up;
		this.key_down=key_down;
		this.key_left=key_left;
		this.key_right=key_right;
		this.block_mask=block_mask;
		this.speed=speed;
	}

	/** Moves the object around.  If you override this, be sure to call
	 * super.move() and don't touch x, y, xspeed, yspeed, xdir, ydir. */
	public void move() {
		boolean go_left=false;
		boolean go_right=false;
		boolean go_up=false;
		boolean go_down=false;
		if (!stop_moving) {
			go_left=eng.getKey(key_left);
			go_right=eng.getKey(key_right);
			go_up=eng.getKey(key_up);
			go_down=eng.getKey(key_down);
		}
		JGPoint cen = getCenterTile();
		double rawspeed = speed*eng.getGameSpeed();
		if (!isXAligned(rawspeed/2.0) || !isYAligned(rawspeed/2.0)) {
			moving=1;
			if (!continuous_anim) startAnim();
			// see if we should reverse direction in order to move towards
			// the opening closest to us
			if (isXAligned(rawspeed/2.0)) {
				if (go_left && !(go_up||go_down)
				&& eng.getYAlignOfs(y)*ydir > 0
				&& !and(eng.getTileCid(cen,-1,0),block_mask)) { 
					ydir = -ydir;
				}
				if (go_right && !(go_up||go_down)
				&& eng.getYAlignOfs(y)*ydir > 0
				&& !and(eng.getTileCid(cen,1,0),block_mask)) { 
					ydir = -ydir;
				}
			}
			if (isYAligned(rawspeed/2.0)) {
				if (go_up && !(go_left||go_right)
				&& eng.getXAlignOfs(x)*xdir > 0
				&& !and(eng.getTileCid(cen,0,-1),block_mask)) { 
					xdir = -xdir;
				}
				if (go_down && !(go_left||go_right)
				&& eng.getXAlignOfs(x)*xdir > 0
				&& !and(eng.getTileCid(cen,0,1),block_mask)) { 
					xdir = -xdir;
				}
			}
		} else { // ready to change direction
			if ((go_left||go_right)&&(go_up||go_down)) {
				// multiple directions at once -> alternate direction
				if (ydir<0) go_up=false;
				if (ydir>0) go_down=false;
				if (xdir<0) go_left=false;
				if (xdir>0) go_right=false;
			}
			snapToGrid(rawspeed/2.0,rawspeed/2.0);
			moving=0;
			if (!continuous_anim) stopAnim();
			if (go_up) {
				if (!and(eng.getTileCid(cen,0,-1),block_mask)) {
					setDir(0,-1);
					if (gfx_prefix!=null) {
						cur_gfx_suffix="u";
						setGraphic(gfx_prefix+cur_gfx_suffix);
					}
				}
				moving=1;
			}
			if (go_down) {
				if (!and(eng.getTileCid(cen,0,1),block_mask)) {
					setDir(0,1);
					if (gfx_prefix!=null) {
						cur_gfx_suffix="d";
						setGraphic(gfx_prefix+cur_gfx_suffix);
					}
				}
				moving=1;
			}
			if (go_left) {
				if (!and(eng.getTileCid(cen,-1,0),block_mask)) {
					setDir(-1,0);
					if (gfx_prefix!=null) {
						cur_gfx_suffix="l";
						setGraphic(gfx_prefix+cur_gfx_suffix);
					}
				}
				moving=1;
			}
			if (go_right) {
				if (!and(eng.getTileCid(cen,1,0),block_mask)) {
					setDir(1,0);
					if (gfx_prefix!=null) {
						cur_gfx_suffix="r";
						setGraphic(gfx_prefix+cur_gfx_suffix);
					}
				}
				moving=1;
			}
			if (and(eng.getTileCid(cen,xdir,ydir),block_mask)) moving=0;
		}
		setSpeed(moving==1  ? speed : 0);
	}
}
