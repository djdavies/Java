import java.io.*;

/** a small example wrapper that calls your main class with the appropriate
* -Djava.library.path. */
public class RunClass implements Runnable {

	BufferedReader rd;

	RunClass(InputStream stream) {
		rd = new BufferedReader(new InputStreamReader(stream));
		new Thread(this).start();
	}

	public void run() {
		try {
			while (true) {
				String line = rd.readLine();
				if (line==null) return;
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String [] command = new String[] {
		"",        // the appropriate command is filled in by the wrapper
		"-Djava.library.path=.",   // include anything else you need here
		"examples.webwars.WebWars" // fill in your own class here
	};


	public static void main(String [] args) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		Process process=null;
		try {
			command[0] = "javaw";
			process = runtime.exec(command);
		} catch (IOException e1) {
			System.out.println("RunClass: Did not find '" + command[0]
				+ "', trying another command.");
			//e1.printStackTrace();
			try {
				command[0] = "java";
				process = runtime.exec(command);
			} catch (IOException e2) {
					System.out.println("RunClass: Did not find '" + command[0]
					+ "', trying another command.");
				//e2.printStackTrace();
				try {
					command[0] = System.getProperty("java.home")+File.separator
						+ "bin" + File.separator + "javaw";
					process = runtime.exec(command);
				} catch (IOException e3) {
					System.out.println("RunClass: Did not find '" + command[0]
						+ "', trying another command.");
					//e3.printStackTrace();
					command[0] = System.getProperty("java.home")+File.separator
						+ "bin" + File.separator + "java";
					process = runtime.exec(command);
				}
			}
		}
		if (process!=null) {
			InputStream outs = process.getInputStream();
			new RunClass(outs);
			InputStream errs = process.getErrorStream();
			new RunClass(errs);
			process.waitFor();
		}
	}

}
