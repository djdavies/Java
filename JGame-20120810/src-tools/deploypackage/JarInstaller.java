import java.util.jar.*;
import java.util.Enumeration;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;


/** A general-purpose installer that detects the current architecture, and
 * extracts appropriate architecture-specific files from its jar.  This is
 * useful for software packages that include system-specific native
 * libraries.  You should not need this installer if your application
 * is architecture-independent.  NOTE: processor architecture (i.e. x86 vs
 * ARM) is not considered yet!
 *
 * The installer looks for particular directories from the jar it
 * is packaged in, and extracts them.  The directories are the following:
 *
 * generic/ - system-independent files
 *
 * Put any system-independent code and resources in this directory.
 *
 * win32/, win64/, linux32/, linux64/, mac32/, mac64/ - system-dependent files
 *
 * For each system for which you have system-specific resources and native
 * libraries, define one of the directories above with the appropriate files
 * in it.  The intaller will detect the system and extract the appropriate
 * directory.  The absence of a directory signals that the respective system
 * is not supported.  So, if a system is supported but has no system-specific
 * files, create an empty directory for it.  
 *
 * By default, the installer produces a file requester letting the user
 * specify the destination directory where the files should be installed.
 *
 * You must place JarInstaller.class in the root directory of the jar.  It
 * looks itself up in order to obtain a path to its jar.
 * 
 */
public class JarInstaller implements ActionListener {

	static JarInstaller instance;

	public JarInstaller() {}


	public void actionPerformed(ActionEvent e) { 
		String cmd = e.getActionCommand().toLowerCase();
		if (cmd.startsWith("browse")) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showOpenDialog(installdirtf);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				installdirtf.setText(""+file+ File.separator + 
					app_default_dir);
				//This is where a real application would open the file.
				//log.append("Opening: " + file.getName() + "." + newline);
			} else {
				//log.append("Open command cancelled by user." + newline);
			}
		} else if (cmd.startsWith("extract")) {
			selected_dir = installdirtf.getText();
		} else if (cmd.startsWith("cancel")) {
			System.exit(0);
		}
	}


	public static String getSystemArchitecture() {
		String arch = System.getProperty("os.name").toLowerCase();
		String bitness = System.getProperty("sun.arch.data.model");
		if (arch.startsWith("linux")) {
			return "linux"+bitness; // XXX assuming x86
		}
		if (arch.startsWith("windows")) {
			return "win"+bitness;
		}
		if (arch.startsWith("mac")) {
			return "mac"+bitness; // XXX assuming x86
		}
		return null;
	}

	/** Extracts all files from jar that are prefixed with dir.  Destination
	 * is destdir.  Returns the number of items (files+dirs) extracted.
	 */
	public static int extractJarDir(String jarfile,String dir,String destdir) 
	throws Exception {
		int nr_extracted=0;
		System.out.println("Extracting from: "+jarfile);
		System.out.println("Subdirectory: "+dir);
		JarInputStream jar = new JarInputStream(
			new FileInputStream(jarfile) );
		//JarFile jar = new JarFile(jarfile);
		//Enumeration files = jar.entries();
		//while (files.hasMoreElements()) {
			//JarEntry jarentry = (JarEntry)files.nextElement();
		while (true) {
			JarEntry jarentry = (JarEntry)jar.getNextJarEntry();
			if (jarentry==null) break;
			String maindir = jarentry.getName();
			// get rid of "./" and "/" prefix
			if (maindir.startsWith(".")) 
				maindir = maindir.substring(1);
			if (maindir.startsWith(File.separator))
				maindir = maindir.substring(1);
			// skip META-INF
			if (maindir.startsWith("META-INF")) continue;
			// check if dir matches given dir pattern
			if (maindir.startsWith(dir)) {
				// chop dir pattern
				maindir = maindir.substring(dir.length()+1);
				// extract
				File outfile = new File(destdir + File.separator + maindir);
				System.out.println("Extracting: "+outfile);
				// prepare for writing item, and write it
				if (jarentry.isDirectory()) {
					// replace file with directory?
					if (outfile.exists() && outfile.isFile()) {
						if (!confirmOverwrite()) {
							throw new Exception("Extraction aborted.");
						}
						outfile.delete();
					}
					if (outfile.exists() && outfile.isDirectory()) {
						// replace directory with directory? we need not do
						// anything
					} else if (!outfile.mkdir()) {
						throw new Exception("Failed to create directory.");
					}
					nr_extracted++;
					continue;
				} else {
					// replace directory with file -> not possible (yet)
					if (outfile.isDirectory()) {
						throw new Exception(
						"A directory is in the way. "
						+"Please remove files and directories and try again.");
					}
					// replace file with file -> ask first
					if (outfile.exists()) {
						if (!confirmOverwrite()) {
							throw new Exception("Extraction aborted.");
						}
						outfile.delete();
					}
					// get the streams
					//java.io.InputStream ins = jar.getInputStream(jarentry);
					java.io.InputStream ins = jar;
					java.io.FileOutputStream outs = new java.io.FileOutputStream(outfile);
					// copy buffered (is a lot faster than one byte at a time)
					byte[] buf = new byte[8192];
					while (true) {
						//outs.write(ins.read());
						int len = ins.read(buf);
						if (len < 0) break;
						outs.write(buf,0,len);
					}
					nr_extracted++;
					outs.close();
					//ins.close();
				}
			} else {
				//System.out.println("SKIPPED:"+maindir);
			}
		}
		return nr_extracted;
	}
	// settings
	static String app_name="MyApplication";
	static String app_title=app_name + " Installer";
	static String splashscreen = "install_splash.jpg";
	static String app_default_dir = "MyApplication";
	// gui elements
	static JFrame frame;
	static JTextField installdirtf;
	static String selected_dir=null;
	class InstallPanel extends JPanel {
		Image bg;
		InstallPanel() {
			bg = new ImageIcon(getClass().getResource(splashscreen)).getImage();
			//setSize(600,300);
		}
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(bg,0,0,null);
			g.setColor(new Color(255,255,255));
			//g.drawString(install_text,100,100);
		}
	}
	private static String getDestinationDirectory(String initialpath) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		frame = new JFrame(app_title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel0 = new JPanel();
		panel0.setLayout(new BoxLayout(panel0,BoxLayout.Y_AXIS));
		JPanel panel0b = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1,BoxLayout.X_AXIS));
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.Y_AXIS));
		JPanel panel3a = new JPanel();
		panel3a.setLayout(new BoxLayout(panel3a,BoxLayout.X_AXIS));
		JPanel panel3b = new JPanel();
		panel3b.setBackground(Color.white);
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		installdirtf = new JTextField(initialpath + File.separator
			+ app_default_dir);
		JButton browsebut = new JButton("Browse...");
		browsebut.addActionListener(instance);
		JButton extractbut = new JButton("Extract");
		extractbut.addActionListener(instance);
		JButton cancelbut = new JButton("Cancel");
		cancelbut.addActionListener(instance);
		java.net.URL imageURL = instance.getClass().getResource(splashscreen);
		JLabel msglab = new JLabel(
			"<html>"
			+"<b>Extract Program</b><br><br>"
			+"This will extract "+app_name+" to a directory"
			+" on your system."
			+"<br><br><br>"
			+"Select the installation directory below."
			+"</html>");
		//msglab.setSize(300,270);
		msglab.setPreferredSize(new Dimension(400,270));
		msglab.setVerticalAlignment(SwingConstants.TOP);
		JLabel splashlab = new JLabel(new ImageIcon(imageURL));
		splashlab.setBorder(border);
		panel1.add(splashlab);
		panel1.add(panel2);
		//JScrollPane msglabsc = new JScrollPane(msglab);
		//msglabsc.setPreferredSize(new Dimension(300,270));
		panel2.setBorder(border);
		panel3a.add(installdirtf);
		panel3a.add(browsebut);
		panel3b.add(msglab);
		panel2.add(panel3b);
		panel2.add(panel3a);
		//lab.setIconTextGap(0);
		//panel2.add(new JarInstaller().new InstallPanel());
		//panel2.setPreferredSize(new Dimension(600,300));
		panel0.add(panel1);
		panel0.add(panel0b);
		panel0b.add(extractbut);
		panel0b.add(cancelbut);
		panel0b.setBorder(border);
		frame.add(panel0);
		frame.pack();
		frame.setVisible(true);
		selected_dir = null;
		try {
			while (selected_dir==null) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {}
		//System.out.println(selected_dir);
		//System.exit(0);
		return selected_dir;
	}
	private static void showError(String error) {
		JOptionPane.showMessageDialog(frame,
			error, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private static void showMessage(String msg) {
		JOptionPane.showMessageDialog(frame, msg);

	}
	private static boolean overwriteConfirmed=false;
	private static boolean confirmOverwrite() {
		if (overwriteConfirmed) return true;
		if (!requestYesNo("Files will be overwritten! Continue?",
		"Continue","Cancel")) return false;
		overwriteConfirmed = true;
		return true;
	}
	private static boolean requestYesNo(String msg,String yesMsg,String noMsg) {
		Object [] options = new String[] {yesMsg,noMsg};
		int n = JOptionPane.showOptionDialog(frame,
			msg, msg,
			JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
			null,  options, options[0]);
		return n == 0;
	}
	/** this should pop up a requester for system architecture */
	private static String requestSystemArch() {
		return "win32";
	}
// http://stackoverflow.com/questions/1529611/how-to-write-a-java-program-which-can-extract-a-jar-file-and-store-its-data-in-s
	public static void main(String [] args) {
		instance = new JarInstaller();
		// to get the URL of the jar, get the URL of a known resource inside
		// the jar. XXX I did not find a method for doing this without such a
		// known resource.
		String myclass = instance.getClass().getName();
		String mypath = ClassLoader.getSystemClassLoader()
			.getResource(myclass+".class").getPath();
		// the path is anything after a protocol specification and
		// before the last 'jar!'
		int startjar = mypath.indexOf(":"); // -1 = start of string
		int endjar = mypath.lastIndexOf("jar!");
		if (endjar >= 0) {
			String myjar = mypath.substring(startjar+1, endjar+3);
			String destination = getDestinationDirectory(
				new File(myjar).getParent());
			String arch = getSystemArchitecture();
			if (arch==null) arch = requestSystemArch();
			try {
				// extract architecture-specific files
				int extracted = extractJarDir(myjar,arch,destination);
				if (extracted == 0) {
					showError("System architecture not supported");
				}
				// extract generic files (may be 0)
				extracted = extractJarDir(myjar,"generic",destination);
				showMessage("Finished!");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				showError("I/O error extracting files.");
			} catch (Exception e) {
				showError(e.getMessage());
			}
		} else {
			showError("JarInstaller cannot find its Jar!");
		}
		System.exit(0);
	}
}
