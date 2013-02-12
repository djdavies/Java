package examples;

import jgame.*;
import jgame.impl.JGameError;
import jgame.platform.*;

/** Simulates (cursor) keypresses using accelerometer, virtual keyboard, and
 * swipe controls. Also provides virtual keyboard and "compass"
 * visualisations.
 *
 * Writes the keypresses directly using setKey/clearKey. Tries to leave alone
 * the keymap state set by the engine so that it does not interfere if the
 * controls are not used.
 *
 * A "compass" is a visual aid that helps the player determine the direction
 * and magnitude of a control in cases where this is hard to determine
 * otherwise.  For example, for seeing where your finger is relative to the
 * center of a virtual joystick.  getCompassInfo() gets the compass
 * info of the most recent call to *ToKeys or handleVirtualKeyboard.
 */
public class StdVirtualKeyboard {

	/** compass is appropriate */
	protected boolean show_compass=false;
	/** use these to draw a compass */
	protected double compassx=0,compassy=0;

	/*=== settings ===*/

	/** Default is JGEngine.KeyUp. */
	public int KeyUp = JGEngine.KeyUp;
	/** Default is JGEngine.KeyDown. */
	public int KeyDown = JGEngine.KeyDown;
	/** Default is JGEngine.KeyLeft. */
	public int KeyLeft = JGEngine.KeyLeft;
	/** Default is JGEngine.KeyRight. */
	public int KeyRight = JGEngine.KeyRight;

	/** Threshold for determining when a direction is a diagonal (both
	 * horizontal and vertical keys are pressed).  A value between 0 and 1,
	 * default is 0.3. Set to 1 for 4-directional controls. Must not be 0.
	 *
	 * A diagonal must have similar relative magnitude of x and y, that is,
	 * min(x,y) &gt; relativethreshold*max(x,y). 
	 */
	public double relativethreshold=0.3;

	// keys set by virtual keyboard
	protected boolean [] keyset = new boolean [256+3];

	/** @return null if compass is not appropriate, otherwise returns double
	 * {xpos,ypos} */
	public double [] getCompassInfo() {
		if (!show_compass) return null;
		return new double[] {compassx, compassy};
	}

	protected JGEngine eng;

	public StdVirtualKeyboard(JGEngine eng) {
		this.eng = eng;
	}

	private boolean getKey(int keycode) {
		return eng.getKey(keycode);
	}

	private void setKey(int keycode) {
		eng.setKey(keycode);
		keyset[keycode]=true;
	}

	// only clear key set by virtual keyboard
	private void clearKey(int keycode) {
		if (!keyset[keycode]) return;
		eng.clearKey(keycode);
		keyset[keycode]=false;
	}

	/** Clear all keys set by the virtual keyboard.
	*/
	public void clearKeys() {
		clearKey(KeyUp);
		clearKey(KeyDown);
		clearKey(KeyLeft);
		clearKey(KeyRight);
	}

	protected double xdist=0,ydist=0;

	/** Translate the supplied accelerometer info into keypresses. A threshold
	* of 1.0 seems to work well.
	*
	* @param threshold threshold above which key should be pressed
	*/
	public void accelerometerToKeys(double [] accel, double threshold) {
		// some minor exponential smoothing to make the compass look better
		xdist = 0.5*xdist + 0.5*accel[0];
		ydist = 0.5*ydist + 0.5*accel[1];
		show_compass=true;
		compassx = xdist;
		compassy = ydist;
		boolean xlarger = Math.abs(xdist) > Math.abs(ydist);
		double relxy = Math.abs(xlarger ? ydist/xdist : xdist/ydist);
		// 8 directional keys
		clearKeys();
		if (xdist > threshold && (xlarger || relxy > relativethreshold)) {
			setKey(KeyRight);
			keyset[KeyRight] = true;
		}else if(xdist < -threshold && (xlarger || relxy > relativethreshold)) {
			setKey(KeyLeft);
		}
		if (ydist > threshold && (!xlarger || relxy > relativethreshold)) {
			setKey(KeyDown);
		}else if(ydist < -threshold && (!xlarger || relxy > relativethreshold)){
			setKey(KeyUp);
		}
	}

	/** Translate touches to cursor keys */
	public void handleVirtualKeyboard(int top,int bottom,int left,int right) {
		show_compass=false;
		if (eng.getMouseButton(1)) {
			int x=eng.getMouseX();
			int y=eng.getMouseY();
			if (x >= left && x < right
			&&  y >= top && y < bottom) {
				double xr = (x-left) / ((double)right-left);
				double yr = (y-top) / ((double)bottom-top);
				// check bottomleft ... topright line
				if (xr < yr) { // topleft
					// check topleft ... bottomright line
					if (xr > 1.0-yr) { // topright -> top
						clearKeys();
						setKey(KeyDown);
					} else { // bottomleft -> left
						clearKeys();
						setKey(KeyLeft);
					}
				} else { // bottomright
					if (xr > 1.0-yr) { // topright -> right
						clearKeys();
						setKey(KeyRight);
					} else { // bottomleft ->bottom
						clearKeys();
						setKey(KeyUp);
					}
				}
			}
		} else {
			clearKeys();
		}
	}

	protected double [][][] keycoords=null;
	/** Initialise polygons for painting virtual keyboard (slow) */
	public void paintVirtualKeyboardInit(int top,int bottom,int left,
	int right,double xgap,double ygap,double cxmargin,double cymargin) {
		keycoords = new double [4][2][3];
		double centerx = (left+right)/2.0;
		double centery = (top+bottom)/2.0;
		double xscale = right - left;
		double yscale = bottom - top;
		// top
		keycoords[0][0][0]=left + xscale*0.5*cxmargin;
		keycoords[0][1][0]=top;
		keycoords[0][0][1]=right - xscale*0.5*cxmargin;
		keycoords[0][1][1]=top;
		keycoords[0][0][2]=centerx;
		keycoords[0][1][2]=centery - yscale*0.5*ygap;
		// bottom
		keycoords[1][0][0]=left + xscale*0.5*cxmargin;
		keycoords[1][1][0]=bottom;
		keycoords[1][0][1]=right - xscale*0.5*cxmargin;
		keycoords[1][1][1]=bottom;
		keycoords[1][0][2]=centerx;
		keycoords[1][1][2]=centery + yscale*0.5*ygap;
		// left
		keycoords[2][0][0]=left;
		keycoords[2][1][0]=top + yscale*0.5*cymargin;
		keycoords[2][0][1]=left;
		keycoords[2][1][1]=bottom - yscale*0.5*cymargin;
		keycoords[2][0][2]=centerx - xscale*0.5*xgap;
		keycoords[2][1][2]=centery;
		// right
		keycoords[3][0][0]=right;
		keycoords[3][1][0]=top + yscale*0.5*cymargin;
		keycoords[3][0][1]=right;
		keycoords[3][1][1]=bottom - yscale*0.5*cymargin;
		keycoords[3][0][2]=centerx + xscale*0.5*xgap;
		keycoords[3][1][2]=centery;
	}
	/** Paint the virtual keyboard; call paintVirtualKeyboardInit first.
	*/
	public void paintVirtualKeyboard(JGColor colorPressed,
	JGColor colorReleased) {
		if (keycoords==null) throw new JGameError(
			"Call paintVirtualKeyboardInit() first!");
		for (int i=0; i<4; i++) {
			boolean dirPressed = false;
			if (getKey(KeyUp) && i==0) dirPressed=true;
			if (getKey(KeyDown) && i==1) dirPressed=true;
			if (getKey(KeyLeft) && i==2) dirPressed=true;
			if (getKey(KeyRight) && i==3) dirPressed=true;
			eng.setColor(dirPressed ? colorPressed : colorReleased);
			eng.drawPolygon(keycoords[i][0],keycoords[i][1],null, 3,
				true,false);
		}
	}

	/** reset threshold, # of frames that position is stationary before
	* resetting center point. We found larger values than 1 are not very
	* useful. */
	public int mposstationaryreset=1;

	// swipeToKeys4Dir state
	protected boolean was_pressed=false;
	protected boolean stop_when_no_motion=true;
	protected int dir_was_set=0;  // 1=up 2=right 3=down 4=left
	protected int mxstart=0, mystart=0;
	protected int prevdx=0,prevdy=0;
	protected int mposstationary=0;

	/** translate swipe movements to keypresses (4-directional), for maze
	* games.
	* A swipe will translate to a key being held until another swipe or a tap
	* is performed.
	*
	* @param threshold minimum swipe distance; 25 is a good default
	*/
	public void swipeToKeys4Dir(int threshold) {
		show_compass=false;
		if (eng.getMouseButton(1)) {
			if (!was_pressed) {
				was_pressed=true;
				dir_was_set=0;
				mxstart = eng.getMouseX();
				mystart = eng.getMouseY();
				if (stop_when_no_motion) clearKeys();
			} else {
				int dx = eng.getMouseX() - mxstart;
				int dy = eng.getMouseY() - mystart;
				if (Math.abs(dx-prevdx) + Math.abs(dy-prevdy) < 3) {
					mposstationary++;
					if (mposstationary >= mposstationaryreset) {
						was_pressed=false;
						stop_when_no_motion=false;
					}
				} else {
					mposstationary=0;
				}
				prevdx = dx;
				prevdy = dy;
				if ((dir_was_set==0 || dir_was_set==4) 
				&& dx > threshold && dx > dy) {
					clearKeys();
					setKey(KeyRight);
					mxstart += dx;
					mystart += dy;
					dir_was_set=2;
				} else if ((dir_was_set==0 || dir_was_set==2) 
				&& dx < -threshold && dx < dy) {
					clearKeys();
					setKey(KeyLeft);
					mxstart += dx;
					mystart += dy;
					dir_was_set=4;
				} else if ((dir_was_set==0 || dir_was_set==1) 
				&& dy > threshold && dy > dx) {
					clearKeys();
					setKey(KeyDown);
					mxstart += dx;
					mystart += dy;
					dir_was_set=3;
				} else if ((dir_was_set==0 || dir_was_set==3) 
				&& dy < -threshold && dy < dx) {
					clearKeys();
					setKey(KeyUp);
					mxstart += dx;
					mystart += dy;
					dir_was_set=1;
				}
			}
		} else {
			was_pressed=false;
			stop_when_no_motion=true;
		}
	}

}
