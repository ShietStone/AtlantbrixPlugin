package org.abp.loading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class FileLoader {

	public static String loadFile(File file, String backup) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			StringBuilder stringBuilder = new StringBuilder();
			
			int input;
			while((input = fileInputStream.read()) != -1)
				stringBuilder.append((char) input);
			
			fileInputStream.close();
			return stringBuilder.toString();
		} catch(Exception exception) {
			return backup;
		}
	}
	
	public static void saveFile(File file, String content) {
		try {
			String filePath = file.getAbsolutePath();
			file.delete();
			
			File newFile = new File(filePath);
			FileWriter fileWriter = new FileWriter(newFile);
			fileWriter.write(content);
			fileWriter.close();
		} catch(Exception exception) {
			
		}
	}
}
