package me.breakofday.happynewyear.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Files {

	private static final Logger logger = Logger.getLogger(Files.class.getName());
	private static final File mainDirectory = new File("plugins/HappyNewYear");

	static {
		if (!mainDirectory.exists()) {
			mainDirectory.mkdirs();
		}
	}

	private Files() {}

	public static File newFile(String path) {
		final File file = new File(mainDirectory, path);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			return file;
		} catch (IOException e) {
			logger.log(Level.SEVERE, file.getPath() + " 파일을 생성하지 못했습니다.");
			return file;
		}
	}

}