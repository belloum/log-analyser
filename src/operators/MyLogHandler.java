package operators;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import beans.MyLog;

/**
 * Handler to operate with MyLog representation
 * 
 * @param rootFile
 *            The file containing the logs
 * @param rootJSON
 *            The JSON representation of rootFile content
 * 
 * @author Antoine Riché
 * @since 05/24/17
 *
 */
public class MyLogHandler {

	File rootFile;
	JSONObject rootJSON;

	/**
	 * 
	 * @param pFile
	 *            The file containing the logs
	 * @throws Exception
	 *             The exception is thrown if the JSON does not contain the
	 *             right keys
	 */
	public MyLogHandler(File pFile) throws Exception {
		this.rootFile = pFile;
		try {
			this.rootJSON = new JSONObject(readFile(pFile));
		} catch (Exception e) {
			throw new Exception("Ubable to get rootJSON from: " + pFile.getAbsolutePath());
		}
	}

	/**
	 * 
	 * @return The file containing the logs
	 */
	public File getRootFile() {
		return rootFile;
	}

	/**
	 * 
	 * @param rootFile
	 *            The file containing the logs
	 */
	public void setRootFile(File rootFile) {
		this.rootFile = rootFile;
	}

	/**
	 * 
	 * @return The JSON representation of rootFile content
	 */
	public JSONObject getRootJSON() {
		return rootJSON;
	}

	/**
	 * 
	 * @param rootJSON
	 *            The JSON representation of rootFile content
	 */
	public void setRootJSON(JSONObject rootJSON) {
		this.rootJSON = rootJSON;
	}

	/**
	 * 
	 * @param file
	 *            The file containing the logs
	 * @return The content of file as String
	 * @throws Exception
	 *             The exception is thrown if the file does not exist
	 */
	public static String readFile(File file) throws Exception {
		FileInputStream in = new FileInputStream(file);
		byte[] r = new byte[(int) file.length()];
		in.read(r);
		in.close();
		return (new String(r));
	}

	/**
	 * Extract the logs from the rootFile and the rootJSON
	 * 
	 * @return Content of the rootFile as List of MyLog if the JSON
	 *         representation is well formed, null otherwise
	 * 
	 * @see MyLog
	 */
	public List<MyLog> extractLogs() {

		if (!this.rootJSON.has("days")) {
			System.out.println("Bad file");
			return null;
		}

		System.out.println("Extracting logs:");
		try {
			int total = 0;
			JSONArray days = this.rootJSON.getJSONArray("days");
			System.out.println("\t> " + this.rootFile.getName() + " contains " + days.length() + " days.");
			List<MyLog> mylogs = new ArrayList<>();

			for (int i = 0; i < days.length(); i++) {
				JSONObject day = days.getJSONObject(i);
				JSONArray logs = day.getJSONArray("logs");
				for (int j = 0; j < logs.length(); j++) {
					mylogs.add(new MyLog(logs.getJSONObject(j)));
				}
				System.out.println("\t> " + day.getString("date") + " has been treated (" + logs.length() + " logs)");
				total += logs.length();
			}

			System.out.println(total + " logs have been extracted.");
			System.out.println();
			return mylogs;

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}

		return null;
	}

}
