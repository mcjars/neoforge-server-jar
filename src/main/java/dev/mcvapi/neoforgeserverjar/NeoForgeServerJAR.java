package dev.mcvapi.neoforgeserverjar;

import dev.mcvapi.neoforgeserverjar.server.ServerBootstrap;
import dev.mcvapi.neoforgeserverjar.utils.ErrorReporter;

import java.io.File;
import java.lang.management.ManagementFactory;

public class NeoForgeServerJAR {
	public static void main(final String[] args) {
		String directoryPath = null;
		File neoforgeDir = new File("libraries/net/neoforged/neoforge");
		File forgeDir = new File("libraries/net/neoforged/forge");

		if (neoforgeDir.exists() && neoforgeDir.isDirectory()) {
			directoryPath = neoforgeDir.getPath();
		} else if (forgeDir.exists() && forgeDir.isDirectory()) {
			directoryPath = forgeDir.getPath();
		} else {
			ErrorReporter.error("10", true);
		}

		String forgeVersion = null;
		File directory = new File(directoryPath);
		File[] filesAndDirs = directory.listFiles();

		if (filesAndDirs == null) {
			ErrorReporter.error("08", true);
		}

		assert filesAndDirs != null;
		for (File fileOrDir : filesAndDirs) {
			if (fileOrDir.isDirectory()) {
				forgeVersion = fileOrDir.getName();
			}
		}

		if (forgeVersion == null) {
			ErrorReporter.error("09", true);
		}

		String[] vmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
		String[] cmd = new String[vmArgs.length + args.length + 2];

		String javaHome = System.getenv("JAVA_HOME");
		if (javaHome == null) {
			cmd[0] = "java";
		} else {
			cmd[0] = javaHome + "/bin/java";
		}

		System.arraycopy(vmArgs, 0, cmd, 1, vmArgs.length);

		boolean windows = System.getProperty("os.name").startsWith("Windows");
		cmd[1 + vmArgs.length] = "@libraries/net/neoforged/neoforge/" + forgeVersion + "/" + (windows ? "win" : "unix")
				+ "_args.txt";

		System.arraycopy(args, 0, cmd, 2 + vmArgs.length, args.length);

		try {
			new ServerBootstrap().startServer(cmd);
		} catch (ServerBootstrap.ServerStartupException exception) {
			exception.printStackTrace();
			System.exit(1);
		}
	}
}
