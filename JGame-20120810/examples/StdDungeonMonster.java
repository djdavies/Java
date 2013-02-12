package examples;
import jgame.*;
/** A standard monster object that can move in 8 directions around a
 * tile-based dungeon, which may be maze-like or contain open spaces.  Typical
 * application: gauntlet, and a variety of others.  The monster moves in 8
 * directions along tile-aligned positions.  It can be made to occupy a space
 * on the tile map, blocking other objects.  The occupation is automatically
 * removed if the object is removed. It is assumed the object is 1x1 tile in
 * size, and the tileBBox is set to this.

 * <p>The public fields are the configuration fields which can be changed
 * at will during the object's lifetime.

 * <p>The object homes in on the specified home_in object, or can be made to
 * avoid it.  The object can be made to move randomly instead of homing in/
 * avoiding, with a specified probability between 0 and 1.  Not setting
 * home_in will make the object move randomly.

 * <p>The object can be made stationary by passing speed=0 or using the
 * short constructor. This can be used for objects that don't move but do
 * occupy space and otherwise act very much like tiles (i.e. "active tiles").

 * <p>This class only defines the move() method; hit() and hit_bg() are not
 * used.  Subclass it to customize.  To customize move(), call super.move()
 * somewhere inside your move method.  While in general you shouldn't touch
 * the object's position, direction, or speed in order to ensure correct
 * behaviour, you can set any of the configuration variables in the move()
 * method or somewhere else.

 */
public class StdDungeonMonster extends JGObject {
	/* config */
	/** Prefix to use for directional animation;
	* null indicates non-directional graphic */
	public String gfx_prefix=null;
	/** indicates current graphic suffix, "" is none */
	public String cur_gfx_suffix="";
	/** don't stop animating when monster stops moving */
	public boolean continuous_anim=true;
	/** cid mask of tiles that block */
	public int block_mask=0;
	/** cid mask that object should use to indicate occupation */
	public int occupy_mask;
	/** object to home in on, null=none (random movement) */
	public JGObject home_in=null;
	/** true = avoid home_in position */
	public boolean avoid=false;
	/** chance that object moves randomly */
	public double random_proportion;
	/* state */
	JGPoint occupied=null;
	/** When initialised, the object will snap to grid to the nearest free
	 * position, then it will start moving in the direction specified by
	 * home_in, random_proportion.  The object's graphic can be made
	 * directional by setting is_directional.  This will add the suffix
	 * "ul", "u", "ur", "dl", "d", "dr", "l", or "r" to the graphic string
	 * to indicate the direction of movement.  It is possible to define keys
	 * for the diagonal directions, but you can pass 0 as keycode if you don't
	 * want this.
	* @param x  position in pixels
	* @param y  position in pixels
	* @param graphic  graphic, and prefix of directional graphic if directional
	* @param is_directional  true = add direction suffix to graphic
	* @param continuous_anim  don't stop animating when monster stops moving
	* @param block_mask  cid mask of tiles that block
	* @param occupy_mask  cid mask that object should use to indicate occupation
	* @param speed  0 = stationary object
	* @param home_in  object to home in on, null=none (random movement)
	* @param avoid  true = avoid home_in position
	* @param random_proportion chance that object moves randomly
	*/
	public StdDungeonMonster(String name, boolean unique_id,
	double x,double y, int cid,
	String graphic, boolean is_directional, boolean continuous_anim,
	int block_mask, int occupy_mask, double speed, 
	JGObject home_in,boolean avoid,double random_proportion) {
		super(name,unique_id, x,y, cid, graphic);
		setTileBBox(0,0,eng.tileWidth(),eng.tileHeight());
		setDirSpeed(0,0,speed);
		if (is_directional) gfx_prefix=graphic;
		this.continuous_anim=continuous_anim;
		this.block_mask=block_mask;
		this.occupy_mask=occupy_mask;
		this.home_in=home_in;
		this.avoid=avoid;
		this.random_proportion=random_proportion;
	}
	/** Create static monster that just occupies space.
	* @param x  position in pixels
	* @param y  position in pixels
	* @param graphic  graphic to use
	* @param occupy_mask  cid mask that object should use to indicate occupation
	*/
	public StdDungeonMonster(String name, boolean unique_id,
	double x,double y, int cid,
	String graphic, int occupy_mask) {
		super(name,unique_id, x,y, cid, graphic);
		setTileBBox(0,0,eng.tileWidth(),eng.tileHeight());
		setDirSpeed(0,0,0.0);
		this.occupy_mask=occupy_mask;
	}
	/** Moves the object around, and ensures it occupies
	 * space.  If you override this, be sure to call super.move() and don't
	 * touch x, y, xspeed, yspeed, xdir, ydir. */
	public void move() {
		if (occupied==null
		||  (xspeed==0 && yspeed==0)
		||  ( xdir==0 && ydir==0 &&  (!isXAligned() || !isYAligned()) )  ) {
			// make sure we're occupying space and are on the grid when we're
			// not moving
			if (occupied!=null)
				eng.andTileCid(occupied.x,occupied.y,~occupy_mask);
			snapToGrid(eng.tileWidth(),eng.tileHeight());
			occupied = getCenterTile();
			eng.orTileCid(occupied.x,occupied.y,occupy_mask);
		} else if (!isXAligned() || !isYAligned()) {
			// we're moving and not aligned -> move until tile aligned
			if (!continuous_anim) startAnim();
		} else { // tile aligned -> ready to change direction
			int prevxdir=xdir, prevydir=ydir;
			snapToGrid();
			JGPoint cen = getCenterTile();
			// determine direction
			setDir(0,0);
			int newxdir=0,newydir=0;
			boolean xdir_any=false,ydir_any=false; // alternate directions
			if (home_in!=null && eng.random(0.0001,0.9999)>random_proportion) {
				int basedir = avoid ? -1 : 1;
				if (home_in.x < x) newxdir = -basedir;
				if (home_in.x > x) newxdir = basedir;
				if (home_in.y < y) newydir = -basedir;
				if (home_in.y > y) newydir = basedir;
				if (Math.abs(home_in.x-x) > Math.abs(home_in.y-y)) {
					ydir_any=true;
				} else {
					xdir_any=true;
				}
			} else { // random
				newxdir = eng.random(-1,1,1);
				newydir = eng.random(-1,1,1);
				xdir_any=true;
				ydir_any=true;
			}
			// check if we can go this way
			xdir = newxdir;
			ydir = newydir;
			checkIfBlocked(this,block_mask,prevxdir,prevydir);
			if (xdir==0 && ydir==0) {
				// if not, try an alternate direction
				if (xdir_any) {
					if (newxdir!=0) xdir = -newxdir;
					else            xdir = eng.random(-1,1,2);
					ydir = newydir;
					checkIfBlocked(this,block_mask,prevxdir,prevydir);
				} else if (ydir_any) {
					xdir = newxdir;
					if (newydir!=0) ydir = -newydir;
					else            ydir = eng.random(-1,1,2);
					checkIfBlocked(this,block_mask,prevxdir,prevydir);
				}
			}
			// occupy new tile, or same tile if we didn't move
			if (occupied!=null)
				eng.andTileCid(occupied.x,occupied.y,~occupy_mask);
			occupied = new JGPoint(cen.x+xdir,cen.y+ydir);
			eng.orTileCid(occupied.x,occupied.y,occupy_mask);
			if (!continuous_anim) {
				if (xdir!=0 || ydir!=0) startAnim();
				else                    stopAnim();
			}
			if (gfx_prefix!=null) {
				if (ydir <  0 && xdir <  0) cur_gfx_suffix="ul";
				if (ydir <  0 && xdir == 0) cur_gfx_suffix="u";
				if (ydir <  0 && xdir >  0) cur_gfx_suffix="ur";
				if (ydir == 0 && xdir <  0) cur_gfx_suffix="l";
				if (ydir == 0 && xdir >  0) cur_gfx_suffix="r";
				if (ydir >  0 && xdir <  0) cur_gfx_suffix="dl";
				if (ydir >  0 && xdir == 0) cur_gfx_suffix="d";
				if (ydir >  0 && xdir >  0) cur_gfx_suffix="dr";
				setGraphic(gfx_prefix+cur_gfx_suffix);
			}
		}
	}
	/** Removes object and object's occupation. */
	public void destroy() {
		if (occupied!=null)
			eng.andTileCid(occupied.x,occupied.y,~occupy_mask);
	}
	/** Check if we aren't blocked in the xdir,ydir direction we're currently
	* going, and change direction if we're partially blocked.
	* This is a static method that can be applied to any JGObject.  It
	* modifies the xdir and ydir appropriately.
	* @param prevxdir  direction we were going previously (such as, last frame)
	* @param prevydir  direction we were going previously (such as, last frame)
	*/
	public static void checkIfBlocked(JGObject o,int block_mask,
	int prevxdir,int prevydir) {
		JGPoint cen = o.getCenterTile();
		boolean can_go_h=!and(o.eng.getTileCid(cen,o.xdir,0),block_mask);
		boolean can_go_v=!and(o.eng.getTileCid(cen,0,o.ydir),block_mask);
		if (o.xdir!=0 && o.ydir!=0
		&&  and(o.eng.getTileCid(cen,o.xdir,o.ydir),block_mask) ) {
			// blocked diagonally -> see if we can move horiz. or vert.
			if (can_go_h && can_go_v) {
				// alternate direction
				if (prevxdir!=0)      o.xdir=0;
				else if (prevydir!=0) o.ydir=0;
				else                  o.xdir=0; // arbitrary choice
			} else if (can_go_h) {
				o.ydir=0;
			} else if (can_go_v) {
				o.xdir=0;
			} else {
				o.xdir=0;
				o.ydir=0;
			}
		}
		if (o.xdir!=0 && !can_go_h) o.xdir=0;
		if (o.ydir!=0 && !can_go_v) o.ydir=0;
	}
}
