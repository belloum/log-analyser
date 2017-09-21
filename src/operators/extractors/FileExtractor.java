package operators.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileExtractor {

	/**
	 * Reads the file
	 * 
	 * @param file
	 *            The file to read
	 * @return The content of file as String
	 * @throws IOException
	 *             Exception is thrown if the specified file does not exist or
	 *             if its content can not be red
	 */
	public static String readFile(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		byte[] r = new byte[(int) file.length()];
		in.read(r);
		in.close();
		return (new String(r));
	}

}
