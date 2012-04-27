import java.awt.*; 
//import java.awt.event.*;
import javax.swing.*;

public class AndGate extends JFrame
{
		
	public void paint(Graphics orGfx)
	{
		orGfx.setColor(Color.black);
		//orGfx.drawArc(500,15,100,100,270,180);
		// (int x, int y, int height, int startAngle, int arcAngle)
		orGfx.drawArc(400,150,200,100,270,180); // arc of gate
		orGfx.drawLine(500,150,500,250); // vertical line of gate
		orGfx.drawLine(600, 200, 700, 200); // horizontal right line
		orGfx.drawLine(425, 170, 500, 170); // top left line
		orGfx.drawLine(425, 230, 500, 230); // bottom left line
	}
	
}
