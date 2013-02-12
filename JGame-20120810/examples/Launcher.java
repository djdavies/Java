package examples;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
public class Launcher {
	public static String [] [] gameinfo = {
		{"Caverns of Fire","examples.cavernsoffire.CavernsOfFire",
			//"examples/caverns_of_fire.tbl",
			"html/cavernsoffire-desc.txt",
			"html/grabs/cavernsoffire-grab-thumb.jpg" },
		{"Caverns of Fire SE","examples.cavensoffire.CavernsOfFire",
			"html/cavernsoffire-desc.txt",
			"html/grabs/cavernsoffire-scroll-grab2-thumb.gif" },
		{"Chain Reaction", "examples.ChainReaction",
			"html/chainreaction-desc.txt",
			"html/grabs/chainreaction-grab-thumb.gif" },
		{"Dingbat Attack JOGL","examples.dingbats.Dingbats",
			"html/dingbats-desc.txt",
			"html/grabs/dingbats-grab-thumb.jpg" },
		{"Dungeons of Hack","examples.DungeonsOfHack",
			"html/dungeonsofhack-desc.txt",
			"html/grabs/dungeonsofhack-grab-thumb.gif" },
		{"Dungeons of Hack SE","examples.DungeonsOfHack",
			"html/dungeonsofhack-desc.txt",
			"html/grabs/dungeonsofhack-scr-grab-thumb.gif" },
		{"Guardian", "examples.guardian.Guardian",
			"html/guardian-desc.txt",
			"html/grabs/guardian-grab-thumb.gif" },
		{"Insecticide", "examples.Insecticide",
			"html/insecticide-desc.txt",
			"html/grabs/insecticide-grab-thumb.gif" },
		{"Matrix Miner", "examples.matrixminer.MatrixMiner",
			"html/matrixminer-desc.txt",
			"html/grabs/matrixminer-grab-thumb.gif"},
		{"Munchies", "examples.Munchies",
			"html/munchies-desc.txt",
			"html/grabs/munchies-grab-thumb.gif"},
		{"Nebula Alpha", "examples.nebulaalpha.NebulaAlpha",
			"html/nebulaalpha-desc.txt",
			"html/grabs/nebulaalpha-grab-thumb.jpg"},
		{"Ogrotron", "examples.ogrotron.Ogrotron",
			"html/ogrotron-desc.txt",
			"html/grabs/ogrotron-grab-thumb.jpg"},
		{"Packet Storm", "examples.packetstorm.PacketStorm",
			"html/packetstorm-desc.txt",
			"html/grabs/packetstorm-grab-thumb.gif"},
		{"Pub Man", "examples.PubMan",
			"html/pubman-desc.txt",
			"html/grabs/pubman-grab-thumb.gif"},
		{"Ramjet", "examples.Ramjet",
			"html/ramjet-desc.txt",
			"html/grabs/ramjet-grab-thumb.gif"},
		{"Space Run", "examples.SpaceRun",
			"html/spacerun-desc.txt",
			"html/grabs/spacerun-grab-thumb.gif"},
		{"Space Run II", "examples.SpaceRunII",
			"html/spacerunii-desc.txt",
			"html/grabs/spacerun-grab-thumb.gif"},
		{"Space Run III", "examples.SpaceRunIII",
			"html/spaceruniii-desc.txt",
			"html/grabs/spaceruniii-grab-thumb.gif"},
		{"Water World", "examples.waterworld.WaterWorld",
			"html/waterworld-desc.txt",
			"html/grabs/waterworld-grab-thumb.gif"},
	};
	static JFrame frame;
	static JList list;
	static JTextArea desc_area;
	static JLabel grab;
	static JComboBox resolution;
	public static void main(String [] args) {
		Dimension scrsize = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame("JGame Launcher");
		frame.setSize(630,456);
		frame.setLocation(scrsize.width/2-630/2,scrsize.height/2-456/2);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		} );
		JPanel descpanel = new JPanel();
		descpanel.setBackground(Color.white);
		descpanel.setLayout(new BoxLayout(descpanel,BoxLayout.Y_AXIS));
		JPanel gamepanel = new JPanel();
		gamepanel.setLayout(new BoxLayout(gamepanel,BoxLayout.X_AXIS));
		JPanel butpanel = new JPanel();
		butpanel.setLayout(new BoxLayout(butpanel,BoxLayout.X_AXIS));
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new BoxLayout(mainpanel,BoxLayout.Y_AXIS));
		mainpanel.setBackground(new Color(200,200,255));
		String [] namelist = new String[gameinfo.length];
		for (int i=0; i<gameinfo.length; i++)
			namelist[i] = gameinfo[i][0];
		list = new JList(namelist);
		list.setFont(new Font("Helvetica",Font.BOLD,15));
		JScrollPane list_pane = new JScrollPane(list);
		list_pane.setPreferredSize(new Dimension(270,300));
		desc_area = new JTextArea(
			"Welcome to\n        the JGame games collection.\n"
			+"Version: 3.5, 25 apr 2011.\n\n"
			+"Select your game!\n"
			+"   SE means scrolling edition.\n\n"
			+"   JOGL means: OpenGL only.\n\n"
			+"Visit the website at\nwww.13thmonkey.org/~boris/jgame/\n\n");
		desc_area.setEditable(false);
		desc_area.setFont(new Font("Arial",Font.BOLD,15));
		JScrollPane desc_pane = new JScrollPane(desc_area);
		desc_pane.setPreferredSize(new Dimension(630-270,300));
		grab = new JLabel();
		resolution = new JComboBox(new String[] {
			"Fullscreen","320x240","600x400","640x480","800x600",
			"1020x680","1024x768","1280x1024","1600x1200" } );
		resolution.setMaximumRowCount(20);
		resolution.setFont(new Font("Arial",Font.BOLD,25));
		resolution.setMaximumSize(new Dimension(200,50));
		resolution.setBackground(Color.white);
		JButton startbut = new JButton("Start Game !");
		startbut.setFont(new Font("Arial",Font.BOLD|Font.ITALIC,25));
		startbut.setBackground(Color.white);
		JButton quitbut = new JButton("Quit");
		quitbut.setFont(new Font("Arial",Font.BOLD,25));
		quitbut.setBackground(Color.white);
		descpanel.add(grab);
		descpanel.add(desc_pane);
		gamepanel.add(list_pane);
		gamepanel.add(descpanel);
		butpanel.add(startbut);
		butpanel.add(resolution);
		butpanel.add(quitbut);
		mainpanel.add(gamepanel);
		mainpanel.add(butpanel);
		frame.getContentPane().add(mainpanel);
		frame.setVisible(true);
		list.addListSelectionListener(new ListSelectionListener() {
			 public void valueChanged(ListSelectionEvent e) {
			 	int idx = list.getSelectedIndex();
				if (idx < 0) return;
				desc_area.setText(
						formatText(readTextFile(gameinfo[idx][2]),35)
						+"\n\nPress Esc or Shift-Esc to quit."
				);
				grab.setIcon(new ImageIcon(Launcher.class.getClassLoader().
					getResource(gameinfo[idx][3])));
			 }
		} );
		startbut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			 	int idx = list.getSelectedIndex();
				if (idx < 0) return;
				String res = (String)resolution.getSelectedItem();
				int width=0, height=0;
				if (!res.equals("Fullscreen")) {
					width=Integer.parseInt(res.substring(0,res.indexOf("x")));
					height=Integer.parseInt(res.substring(res.indexOf("x")+1));
				}
				try {
					Class gamecls = getClass().getClassLoader()
							.loadClass(gameinfo[idx][1]);
					//String [] args2 = new String[] { };
					Object args2;
					if (idx==4 || idx==5 || idx==0 || idx==1) {
						args2 = Array.newInstance(String.class,3);
						((String[])args2)[0] =
							(idx==4||idx==0 ? "no": "")+"scroll";
						((String[])args2)[1] = ""+width;
						((String[])args2)[2] = ""+height;
					} else {
						args2 = Array.newInstance(String.class,2);
						((String[])args2)[0] = ""+width;
						((String[])args2)[1] = ""+height;
					}
					Method main = gamecls.getMethod("main", new Class[] {
						args2.getClass() } );
					frame.setVisible(false);
					main.invoke(null,new Object[] {args2});
				} catch (ClassNotFoundException f) {
					System.err.println("Error: cannot find class "
						+gameinfo[idx][1]);
				} catch (NoSuchMethodException f) {
					System.err.println("Error: no main in "
						+gameinfo[idx][1]);
				} catch (Exception f) {
					System.err.println("Error executing "
						+gameinfo[idx][1]+":");
					f.printStackTrace();
				}
			}
		} );
		quitbut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		} );
	}
	public static String formatText(String htmltext, int maxlen) {
		StringBuffer formatted=new StringBuffer();
		StringTokenizer toker = new StringTokenizer(htmltext," \t\n\r<>",true);
		int linefill=0;
		boolean in_html=false;
		while (toker.hasMoreTokens()) {
			String tok = toker.nextToken();
			if (in_html) {
				if (tok.trim().toLowerCase().equals("br")) {
					//formatted.append("\n");
					//linefill=0;
				}
				if (tok.trim().toLowerCase().equals("p")) {
					//formatted.append("\n\n");
					//linefill=0;
				}
				//skip
				if (tok.equals(">")) in_html=false;
			} else {
				if (tok.equals("<")) {
					in_html=true;
				} else if (tok.equals("\t") || tok.equals("\n")
				|| tok.equals("\r") ) {
					formatted.append(" ");
					// skip
				} else {
					if (linefill > 0 && linefill + tok.length() > maxlen 
					&& !tok.equals(" ")) {
						formatted.append("\n");
						linefill=0;
					}
					formatted.append(tok);
					linefill += tok.length();
				}
			}
		}
		return formatted.toString();
	}
	public static String readTextFile(String path) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
				Launcher.class.getClassLoader().getResourceAsStream(path)));
			StringBuffer text = new StringBuffer();
			String line;
			while (true) {
				line = reader.readLine();
				if (line==null) break;
				text.append(line);
				text.append("\n");
			}
			reader.close();
			return text.toString();
		} catch (IOException e) {
			System.out.println("Error reading file '"+path+"'.");
			return null;
		}
	}

}
