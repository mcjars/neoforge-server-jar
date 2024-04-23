package dev.mcvapi.neoforgeserverjar;

import dev.mcvapi.neoforgeserverjar.server.ServerBootstrap;
import dev.mcvapi.neoforgeserverjar.utils.ErrorReporter;

import java.io.File;
import java.lang.management.ManagementFactory;

public class NeoForgeServerJAR {
	public static void main(final String[] args) {
		String directoryPath = "libraries/net/neoforged/neoforge";
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
		String[] cmd = new String[vmArgs.length + 2];
		cmd[0] = "java";

		System.arraycopy(vmArgs, 0, cmd, 1, vmArgs.length);

		boolean windows = System.getProperty("os.name").startsWith("Windows");
		cmd[1 + vmArgs.length] = "@libraries/net/neoforged/neoforge/" + forgeVersion + "/" + (windows ? "win" : "unix")
				+ "_args.txt";

		try {
			new ServerBootstrap().startServer(cmd);
		} catch (ServerBootstrap.ServerStartupException exception) {
			exception.printStackTrace();
			System.exit(1);
		}
	}
}
