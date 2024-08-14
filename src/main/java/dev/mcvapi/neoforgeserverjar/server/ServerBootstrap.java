package dev.mcvapi.neoforgeserverjar.server;

import java.io.IOException;

public class ServerBootstrap {
	public void startServer(String[] cmd) throws ServerStartupException {
		try {
			Process process = new ProcessBuilder(cmd)
				.command(cmd)
				.inheritIO()
				.start();

			// Forward Ctrl+C signal to the child process
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				process.destroy();
				try {
					process.waitFor();
				} catch (InterruptedException e) {
				}
			}));

			while (process.isAlive()) {
				try {
					process.waitFor();
					break;
				} catch (InterruptedException ignore) {
				}
			}
		} catch (IOException exception) {
			throw new ServerStartupException("Failed to start the NeoForge server.", exception);
		}
	}

	@SuppressWarnings("InnerClassMayBeStatic")
	public static class ServerStartupException extends Exception {
		ServerStartupException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
