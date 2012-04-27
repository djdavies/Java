/* CM1203: Assignment 2
 * Daniel Davies: C1120627
 * Question 1
 * Gates.java */

// Import modules
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gates extends JFrame implements ActionListener
{
	// Define everything I need
	JPanel palette,tools;
    JButton orGate, andGate;
    JButton redButt, blueButt;
    JButton clear;
    MyCanvas drawarea; // for later use, own class, where I will 'draw'
    int gate; // 1=or, 2=and
    Color background = new Color(128,128,127);	// colour 'hack'
    OrGate gateOr; // OR gate class
    AndGate gateAnd; // AND gate class
    // Coordinate integers
    int mx = 0;
	int my = 0;
	int mx1 = 0;
	int my1 = 0;
	int lineCount = 0; // counts number of line CLICKS on 'canvas'
	Color currentColour = new Color(0,0,0); // red or blue line
  //  Color ovalColour = new Color(81,81,81);  
    int lineColour; // 1 = red, 2 = blue
    int update = 0; // 0 or 1
    int posA; // +ve AND
    int posO; // +ve OR
    int negA; // -ve AND
    int negO; // -ve OR
    int outputA; // output for AND gate
    int outputO; // outfor four OR gate

     
    public Gates()
    {
	setSize(800,600); 
	palette = new JPanel(); // Watch where this gets placed to
	palette.setLayout(new GridLayout(2,3,5,5)); // grid layout
    setLocation(200,100); // Looks good on my screen, 1280x800
    setTitle("Logic Gates"); 
    // Instance of classes
	gateOr = new OrGate(); // OrGate.java
	gateAnd = new AndGate(); // AndGate.java
	
	// Assign components with variables
	orGate = new JButton("OR Gate"); // I refer to this text later
	andGate = new JButton("AND Gate");
	palette.add(orGate); // not really a palette, the lines would be
	orGate.addActionListener(this);
	palette.add(andGate);
	andGate.addActionListener(this);
	getContentPane().add(palette,"North"); // Gate buttons on top
	
	// red and blue button area
	tools = new JPanel(); // more palette than tools
	tools.setLayout(new GridLayout(3,1,5,5));
	redButt = new  JButton("Red");
	redButt.setBackground(Color.red);
	blueButt = new JButton("Blue");
	blueButt.setBackground(Color.blue);
	clear = new JButton("Clear"); // Fairly useless, but does something
	tools.add(clear);
	clear.addActionListener(this); // Have the buttons listen for actions
	tools.add(redButt);
	redButt.addActionListener(this);
	tools.add(blueButt);
	blueButt.addActionListener(this);
	getContentPane().add(tools,"West"); // put them over west/left
	
	// Drawing area
	drawarea = new MyCanvas(); // MyCanvas class
	getContentPane().add(drawarea,"Center"); // Draw stuff in the centre
	setVisible(true); // visible true for all elements
	} 
	       
    public void actionPerformed(ActionEvent ev)
    {
		if ("OR Gate".equals(ev.getActionCommand())) // if button with "OR Gate" text
		{ 
			gate = 1; // assign it a int value
			repaint(); // so gates won't overlap
		}
		else if ("AND Gate".equals(ev.getActionCommand()))
		{
			gate = 2;
			repaint();
		}
		if("Red".equals(ev.getActionCommand()))
		{
			lineColour = 1; // red, for truth table
			lineCount += 1; // How many lines has the user drawn?
			System.out.println("Linecount is " + lineCount);
			posA += 1; // build up the 'truth table'
			posO += 1; // A and O = _A_ND, _O_R
			System.out.println("Positives are " + posA + ", " + posO); // for developer reference
		}
		else if("Blue".equals(ev.getActionCommand()))
		{
			lineColour = 2; // blue, for truth table
			lineCount += 1;
			System.out.println("Linecount is " + lineCount);
			negA += 1; // 'negs' would be 0s in a truth table
			negO += 1;
			System.out.println("Negatives are " + negA + ", " + negO);

		}
		// A limited 'clear' button, but the intention was there
		if ("Clear".equals(ev.getActionCommand()))
		{
			update = 1;
			repaint();
		}
    }
    
    public static void main(String args[])
    {
		new Gates(); // Calling main prog
		// To keep up success rate, restart the program
		{JOptionPane.showMessageDialog(null, "Restart the program after every (un)successful output");}
    }


// ----------------------------- MYCANVAS CLASS ------------------------
	class MyCanvas extends JPanel implements MouseListener,  MouseMotionListener
	{
		public MyCanvas()
		{
			addMouseListener(this); 
			addMouseMotionListener( this);

			setVisible(true); // Better to set visible than to not
		}
		
    
		public void paint(Graphics gfx) 
		{	

			//super.paint(gfx); // Works fine on GNU/Linux disabled
			//gfx.setColor(ovalColour); // grey or yellow
			gfx.setColor(currentColour);
			if (gate == 1) // If user selected or gate...
			{ 
				gateOr.paint(gfx); // see the other class/file
				// Draw oval
				gfx.setColor(Color.gray);
				gfx.fillOval(701,190,20,20);
			}
			else if (gate == 2) // If user selected and gate
			{
				gateAnd.paint(gfx);	
				// Draw oval
				gfx.setColor(Color.gray);
				gfx.fillOval(701,190,20,20); // tight squeeze, but oval fits
			} 
			// If red, and no lines, then draw on top input
			if (lineColour == 1 && lineCount == 1) // red line
			{
				currentColour = new Color(255,0,0); // red rgb
				gfx.setColor(currentColour);
				gfx.drawLine(300, 170, 425, 170); // top of gate
			}
			// If blue and no lines, draw on top 300, 170, 425, 170 ,, 300, 230, 425, 230
			else if (lineColour == 2 && lineCount == 1) // blue line
			{
				currentColour = new Color(0,0,255); // blue rgb
				gfx.setColor(currentColour);
				gfx.drawLine(300, 170, 425, 170); // top of gate
			}
			// If red and 1 lines, draw on bottom
			else if (lineColour == 1 && lineCount > 1) // red
			{
				currentColour = new Color(255,0,0); // red rgb
				gfx.setColor(currentColour);
				gfx.drawLine(300, 230, 425, 230); // bottom of gate
			}
			// If blue and 1 lines, draw on bottom
			else if (lineColour == 2 && lineCount > 1)
			{
				currentColour = new Color(0,0,255); // blue rgb
				gfx.setColor(currentColour);
				gfx.drawLine(300, 230, 425, 230); // bottom of gate
			}
			// If user gets stuck, and if red, lots of clicking
			else if (lineColour == 1 && lineCount > 2)
			{
				currentColour = new Color(255,0,0); // red rgb
				gfx.setColor(currentColour);
				gfx.drawLine(300, 230, 425, 230); // bottom of gate
				gfx.drawLine(300, 170, 425, 170); // top of gate
			}
			// If user stuck, blue line, lots of clicks
			else if (lineColour == 2 && lineCount > 2)
			{
				currentColour = new Color(0,0,255); // blue rgb
				gfx.setColor(currentColour);
				gfx.drawLine(300, 230, 425, 230); // bottom of gate
				gfx.drawLine(300, 170, 425, 170); // top of gate		
			}
			if (lineCount == 3)
			{
				// If user has confused the logic by too many clicks
				{JOptionPane.showMessageDialog(null, "You've clicked too many times. Please restart the program.");}
				System.out.println("Too many clicks!");

			}
			// Check truth tables, then draw oval colour (light)
			if (negA == 2) // 2 blue for AND gate
			{	
				// no change on light.
			}
			else if (posA == 1) 
			{
				// no light change.
			}
			else if (posA == 2)
			{
				// light changes.
				gfx.setColor(Color.yellow);
				gfx.fillOval(701,190,20,20);
			}
			else if (negO == 2)
			{
				// no light change
			}
			else if (posO == 1)
			{
				// light changes.
				gfx.setColor(Color.yellow);
				gfx.fillOval(701,190,20,20);
			}
			else if (posO == 2)
			{
				// light changes
				
			}
	}
		
		
		public void update(Graphics gfx)
		{
			super.paint(gfx);
		}
				
		// Loads of mouse stuff, not all used, but needed
		public void mouseReleased(MouseEvent e)
		{
			mx1 = e.getX();
			my1 = e.getY();
			repaint();
		}

		public void mouseDragged(MouseEvent e)
		{
			mx1 = e.getX();
			my1 = e.getY();
			repaint();
		}

		public void mousePressed(MouseEvent e)
		{
			if (gate == 0)
			{
				{JOptionPane.showMessageDialog(null, "Please select a logic gate first");}
			}
			mx = e.getX();
			my = e.getY();
			repaint();
		}
		
		// All of this needs to be enabled, sigh..
		public void mouseClicked(MouseEvent e)
		{}
		public void mouseEntered(MouseEvent e)
		{}
		public void mouseExited(MouseEvent e)
		{}
		public void mouseMoved(MouseEvent e)
		{}
	}
}
