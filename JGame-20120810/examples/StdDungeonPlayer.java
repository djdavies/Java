package examples;
import jgame.*;

/** A standard object that allows the player to move in 8 directions around a
 * tile-based dungeon, which may be maze-like or contain open spaces.  Typical
 * application: gauntlet, and a variety of others.  The player moves in 8
 * directions along tile-aligned positions.  It can be made to occupy a space
 * on the tile map, blocking other objects.  The occupation is automatically
 * removed if the object is removed. It is assumed the object is 1x1 tile in
 * size, and the tileBBox is set to this.

 * <p>The public fields are the configuration fields which can be changed
 * at will during the object's lifetime.

 * <p>You can make movement stop by setting the stop_moving flag.  The
 * player will move on to the next aligned position, then halt until the flag
 * is cleared.  In some games, the player should stop when the movement keys
 * are used for firing or doing actions.

 * <p>This class only defines the move() method; hit() and hit_bg() are not
 * used.  Subclass it to customize.  To customize move(), call super.move()
 * somewhere inside your move method.  While in general you shouldn't touch
 * the object's position, direction, or speed in order to ensure correct
 * behaviour, you can set any of the configuration variables in the move()
 * method or somewhere else.

 */
public class StdDungeonPlayer extends JGObject {
	/* config */
	/** null indicates non-directional graphic */
	public String gfx_prefix=null;
	/** don't stop animating when player stops moving */
	public boolean continuous_anim;
	/** cid mask of tiles that block */
	public int block_mask;
	/** cid mask that object should use to indicate occupation */
	public int occupy_mask;
	//public double speed;
	/** Movement keys. */
	public int key_up,key_down,key_left,key_right;
	//public int key_upleft,key_downleft,key_upright,key_downright;
	/** Set to true to disable movement (for example, for firing). */
	public boolean stop_moving;
	/* state */
	JGPoint occupied=null;
	/** When initialised, the object will snap to grid to the nearest free
	 * position.  The object's graphic can be made
	 * directional by setting is_directional.  This will add the suffix
	 * "ul", "u", "ur", "dl", "d", "dr", "l", or "r" to the graphic string
	 * to indicate the direction of movement.  It is possible to define keys
	 * for the diagonal directions, but you can pass 0 as keycode if you don't
	 * want this.
	* @param x  position in pixels
	* @param y  position in pixels
	* @param graphic  prefix of graphic
	* @param is_directional  true = add direction suffix to graphic
	* @param continuous_anim  don't stop animating when player stops moving
	* @param block_mask  cid mask of tiles that block
	* @param occupy_mask  cid mask that object should use to indicate occupation
	*/
	public StdDungeonPlayer(String name, double x,double y, int cid,
	String graphic, boolean is_directional, boolean continuous_anim,
	int block_mask, int occupy_mask, double speed, 
	int key_up,int key_down, int key_left, int key_right) {
	//int key_upleft,int key_downleft, int key_upright, int key_downright) {
		super(name,false, x,y, cid, graphic);
		setTileBBox(0,0,eng.tileWidth(),eng.tileHeight());
		setDir(0,0);
		if (is_directional) gfx_prefix=graphic;
		this.continuous_anim=continuous_anim;
		this.key_up=key_up;
		this.key_down=key_down;
		this.key_left=key_left;
		this.key_right=key_right;
		/*this.key_upleft=key_upleft;
		this.key_downleft=key_downleft;
		this.key_upright=key_upright;
		this.key_downright=key_downright;*/
		this.block_mask=block_mask;
		this.occupy_mask=occupy_mask;
		setSpeed(speed,speed);
	}
	/** Moves the object around, and ensures it occupies
	 * space.  If you override this, be sure to call super.move() and don't
	 * touch x, y, xspeed, yspeed, xdir, ydir. */
	public void move() {
		if ( occupied==null 
		||   (xdir==0 && ydir==0 && (!isXAligned() || !isYAligned()) )  ) {
			// make sure we're occupying space and are on the grid when we're
			// not moving
			if (occupied!=null)
				eng.andTileCid(occupied.x,occupied.y,~occupy_mask);
			snapToGrid(eng.tileWidth(),eng.tileHeight());
			occupied = getCenterTile();
			eng.orTileCid(occupied.x,occupied.y,occupy_mask);
		} else if (!isXAligned() || !isYAligned()) {
			// move until tile aligned
			if (!continuous_anim) startAnim();
		} else { // tile aligned -> ready to change direction
			int prevxdir=xdir, prevydir=ydir;
			snapToGrid();
			JGPoint cen = getCenterTile();
			// determine direction
			setDir(0,0);
			if (!stop_moving) {
				if (eng.getKey(key_left))  xdir = -1;
				if (eng.getKey(key_right)) xdir = 1;
				if (eng.getKey(key_up))    ydir = -1;
				if (eng.getKey(key_down))  ydir = 1;
			}
			StdDungeonMonster.checkIfBlocked(this,block_mask,prevxdir,prevydir);
			// occupy new tile, or same tile if we didn't move
			if (occupied!=null)
				eng.andTileCid(occupied.x,occupied.y,~occupy_mask);
			occupied = new JGPoint(cen.x+xdir,cen.y+ydir);
			eng.orTileCid(occupied.x,occupied.y,occupy_mask);
			if (!continuous_anim) {
				if (xdir!=0 || ydir!=0) startAnim();
				else                    stopAnim();
			}
		}
	}
	/** Removes object and object's occupation. */
	public void destroy() {
		// remove occupation
		if (occupied!=null)
			eng.andTileCid(occupied.x,occupied.y,~occupy_mask);
	}
}
