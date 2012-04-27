import java.awt.*; 
//import java.awt.event.*;
import javax.swing.*;

public class OrGate extends JFrame
{
	public void paint(Graphics orGfx)
	{
		orGfx.setColor(Color.black);
		//orGfx.drawArc(500,15,100,100,270,180);
		// (int x, int y, int width, int height, int startAngle, int arcAngle)
		orGfx.drawArc(400,150,200,100,270,180); // arc of gate
		orGfx.drawArc(470,150,60,100,270,180); // left arc
		orGfx.drawLine(600, 200, 700, 200); // horizontal right line
		orGfx.drawLine(425, 170, 525, 170); // top left line
		orGfx.drawLine(425, 230, 525, 230); // bottom left line
	}
	
}

