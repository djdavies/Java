package createjad;

import java.util.jar.*;
import java.util.*;

import java.io.*;


/** Create J2ME JAD from a JAR. It extracts the MANIFEST from the Jar, and
 * adds the MIDlet-Jar-Size attribute. */
public class Jar2Jad {

	public static boolean extractJarDir(String jarfile,String outfile)
	throws Exception {
		long filesize = new File(jarfile).length();
		JarInputStream jar = new JarInputStream(new FileInputStream(jarfile));
		//while (true) {
		//	JarEntry jarentry = (JarEntry)jar.getNextJarEntry();
		//	if (jarentry==null) break;
		//	String maindir = jarentry.getName();
		//	// get rid of "./" and "/" prefix
		//	if (maindir.startsWith(".")) 
		//		maindir = maindir.substring(1);
		//	if (maindir.startsWith(File.separator))
		//		maindir = maindir.substring(1);
		//	// Find manifest
		//	System.out.println(maindir);
		//	if (maindir.equals("META-INF/MANIFEST.MF")) {
		//		java.io.InputStream ins = jar;

				java.io.FileOutputStream outs = new java.io.FileOutputStream(outfile);
				Manifest man = jar.getManifest();
				if (man==null) return false;
				man.write(outs);
				byte [] buf = ("MIDlet-Jar-Size: "+filesize).getBytes();
				outs.write(buf,0,buf.length);
				outs.close();
		//		// copy buffered (is a lot faster than one byte at a time)
		//		byte[] buf = new byte[8192];
		//		int total_bytes=0;
		//		while (true) {
		//			//outs.write(ins.read());
		//			int len = ins.read(buf);
		//			if (len < 0) break;
		//			outs.write(buf,0,len);
		//			total_bytes += len;
		//		}
		//		buf = ("MIDlet-Jar-Size: "+total_bytes).getBytes();
		//		outs.write(buf,0,buf.length);
		//		outs.close();
		//		//ins.close();
		//	}
		//}
		//return false;
		// remove empty lines from the generated jad which may have been
		// introduced by manifest write.
		// some systems have trouble with empty lines
		List<String> lines = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(outfile));
		while (true) {
			String line = in.readLine();
			if (line==null) break;
			lines.add(line);
		}
		in.close();
		PrintWriter outp = new PrintWriter(outfile);
		for (String s : lines) {
			if (s.trim().equals("")) continue;
			outp.println(s);
		}
		outp.close();
		return true;
	}

	public static void main(String [] args) throws Exception {
		if (args.length != 2) {
			System.err.println("Usage: java -jar jar2jad [jarfile] [jadfile]");
			System.exit(1);
		}
		if (!extractJarDir(args[0],args[1])) {
			System.out.println("Error: Could not find manifest in Jar.");
			System.exit(1);
		} else {
			System.out.println("Jad created.");
		}
	}

}
