package loganalyser.operators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileExtractor {

	private static final Logger log = LoggerFactory.getLogger(FileExtractor.class);

	/**
	 * Reads the file
	 * 
	 * @param file
	 *            The file to read
	 * @return The content of file as String
	 * @throws IOException
	 *             Exception is thrown if the specified file does not exist or if
	 *             its content can not be red
	 */
	public static String readFile(File pFile) throws IOException {
		FileInputStream in = new FileInputStream(pFile);
		byte[] r = new byte[(int) pFile.length()];
		in.read(r);
		in.close();
		return (new String(r));
	}

	public static boolean saveFile(String pContent, File pOutputFile) {
		Writer writer;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pOutputFile), "utf-8"));
			writer.write(pContent);
			writer.flush();
			writer.close();
			log.info("{} has been saved in {}", pOutputFile.getName(), pOutputFile.getParentFile().getAbsolutePath());
			return true;
		} catch (IOException e) {
			System.out.println("Exception while writing content: " + e);
			return false;
		}
	}

}
