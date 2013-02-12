package tutorial;
import jgame.*;
import jgame.platform.*;

/** Tutorial example 8: Using StdGame.  Defines a minimal game with the
 * StdGame framework.  StdGame defines a game state machine with some useful
 * default behaviour in the different states.  
 */
public class Example8 extends StdGame {

	public static void main(String [] args) {
		new Example8(new JGPoint(640,480));
	}

	/** Application constructor. */
	public Example8(JGPoint size) { initEngine(size.x,size.y); }

	/** Applet constructor. */
	public Example8() { initEngineApplet(); }

	public void initCanvas() { setCanvasSettings(40,30,16,16,null,null,null); }

	public void initGame() {
		setFrameRate(35,2);
		defineImage("ball","-",0,"ball20-red.gif","-");
		// If you want to have highscores in StdGame, add the following line.
		setHighscores(
			10, // number of highscores
			new Highscore(0,"nobody"), // default entry for highscore
			25 // max length of the player name
		);
		// We don't need to do anything special here.  StdGame's doFrame will
		// be called now, which will do some initialisations and go to the 
		// Title state.
	}

	/** Called when a new level is started. */
	public void defineLevel() {
		// remove any remaining objects
		removeObjects(null,0);
		// create as many objects as the level number
		for (int i=0; i<=level; i++)
			new JGObject("ball",true,
				random(0,pfWidth()-20),random(0,pfHeight()-20),
				1, "ball", random(-1,1),random(-1,1));
	}

	/** Called when a new life is introduced (that is, at the beginning of the
	 * game and every time the player dies. */
	public void initNewLife() {
		// ... initialise player sprite ...
	}

	/** This is the most important method you have to fill in in StdGame. */
	public void doFrameInGame() {
		moveObjects(null,0);
		// we simply increment the player score to illustrate handling of score
		score++;
		// the main game state events, level done and life lost, are simulated
		// using keys.
		if (getKey('N')) {
			// Signal that the level is finished. We may call this method at
			// any point, including from within a game object.
			levelDone();
		}
		if (getKey('D')) {
			// Signal that we have lost a life.
			lifeLost();
		}
	}

	public void paintFrameInGame() {
		// display instructions
		setFont(new JGFont("arial",0,15));
		drawString("Press N for the next level, or D to lose a life.",
			pfWidth()/2,180,0);
		
	}

}
