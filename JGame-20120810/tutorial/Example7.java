package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 7: Game states and timers.  Defines a simple game
 * state machine, with a title screen, start game, and in-game state.
 * Defines two timers, one ending the StartGame state, and one shooting a
 * bullet from an object.
 */
public class Example7 extends JGEngine {

	public static void main(String [] args) {
		new Example7(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example7(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example7() { initEngineApplet(); }

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
		// determine the game state that the first frame will be in. Setting
		// this will result in the engine calling startTitle once, and
		// doFrameTitle() and paintFrameTitle() every frame.  Nothing
		// happens if you do not define one of these methods.
		setGameState("Title");
	}


	/* Title screen */

	/** Called when the Title state is entered. */
	public void startTitle() {
		// we need to remove all game objects when we go from in-game to the
		// title screen
		removeObjects(null,0);
	}

	public void paintFrameTitle() {
		drawString("Title state. Press space to go to InGame",pfWidth()/2,90,0);
	}

	public void doFrameTitle() {
		if (getKey(' ')) {
			// ensure the key has to be pressed again to register
			clearKey(' ');
			// Set both StartGame and InGame states simultaneously.
			// When setting a state, the state becomes active only at the
			// beginning of the next frame.
			setGameState("StartGame");
			addGameState("InGame");
			// set a timer to remove the StartGame state after a few seconds,
			// so only the InGame state remains.
			new JGTimer(
				70, // number of frames to tick until alarm
				true, // true means one-shot, false means run again
				      // after triggering alarm
				"StartGame" // remove timer as soon as the StartGame state
				            // is left by some other circumstance.
				            // In particular, if the game ends before
				            // the timer runs out, we don't want the timer to
				            // erroneously trigger its alarm at the next
				            // StartGame.
			) {
				// the alarm method is called when the timer ticks to zero
				public void alarm() {
					removeGameState("StartGame");
				}
			};
		}
	}

	/** The StartGame state is just for displaying a start message. */
	public void paintFrameStartGame() {
		drawString("We are in the StartGame state.",pfWidth()/2,90,0);
	}

	/** Called once when game goes into the InGame state. */
	public void startInGame() {
		// when the game starts, we create a game object
		new Shooter();
	}

	public void paintFrameInGame() {
		drawString("This is a dummy game.  Press space to end the game.",
			pfWidth()/2,150,0);
		drawString("The ball shoots other balls using a JGTimer.",
			pfWidth()/2,180,0);
	}

	public void doFrameInGame() {
		moveObjects(null,0);
		// Space will get us back to the title screen.
		if (getKey(' ')) {
			// ensure the key has to be pressed again to register
			clearKey(' ');
			// return to the Title state
			setGameState("Title");
		}
	}

	/** Shooter is a stationary object that shoots balls using a JGTimer. */
	class Shooter extends JGObject {
		Shooter() {
			super("shooter",true, // name
				Example7.this.random(16,pfWidth()-40),
				Example7.this.random(16,pfHeight()-40), // pos
				1, "ball"  // cid, sprite
			);
			// we define a timer as an inner class, that shoots a bullet every
			// 10 frames.
			new JGTimer(
				10,    // timer alarms after 10 frames
				false, // false = restart after alarm, true = one-shot
				Shooter.this // Parent.  The timer will remove itself 
				             // automatically when the parent is removed.
			) {
				public void alarm() {
					// shoot a bullet in a random direction
					new JGObject("bullet",true,x,y,2,"ball",
						random(-5,5),random(-5,5),// speed
						JGObject.expire_off_pf    // expiry mode
					);
				}
			};
		}
	}

}
