package dev.mcvapi.neoforgeserverjar.server;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

public class ServerBootstrap {
    public interface CLibrary extends Library {
			CLibrary INSTANCE = Native.load("c", CLibrary.class);

			int kill(int pid, int sig);
    }

    public static final int SIGINT = 2;
    public static final int SIGTERM = 15;

	public void startServer(String[] cmd) throws ServerStartupException {
		Process process = null;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(cmd);
			processBuilder.inheritIO();
			process = processBuilder.start();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				if (process.isAlive()) {
					try {
						forwardSignal(process.pid(), SIGINT);
						process.waitFor();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}));

			while (true) {
				try {
					process.waitFor();
					break;
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		} catch (IOException exception) {
			throw new ServerStartupException("Failed to start the NeoForge server.", exception);
		} finally {
			if (process != null && process.isAlive()) {
				process.destroy();
			}
		}
	}

	private void forwardSignal(long pid, int signal) {
		int pidInt = (int) pid;
		int result = CLibrary.INSTANCE.kill(pidInt, signal);
		if (result != 0) {
			System.err.println("Failed to send signal " + signal + " to process " + pid + ". Error code: " + result);
		}
	}

	@SuppressWarnings("InnerClassMayBeStatic")
	public static class ServerStartupException extends Exception {
		ServerStartupException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
