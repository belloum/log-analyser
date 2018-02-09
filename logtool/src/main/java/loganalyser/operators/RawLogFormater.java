package loganalyser.operators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import loganalyser.beans.SoftLog;
import loganalyser.exceptions.RawLogException;

public class RawLogFormater extends FileExtractor {

	public static JSONArray extractJSON(File pRawLogFile) throws RawLogException {
		return extractJSON(pRawLogFile, null);
	}

	public static JSONArray extractJSON(File pRawLogFile, LogExtractorListener pListener) throws RawLogException {
		String content;
		try {
			content = readFile(pRawLogFile).replaceAll("}\n", "},");
			content = String.format("[%s]", content);
			return new JSONArray(content);
		} catch (IOException e) {
			throw new RawLogException(e.getMessage());
		}
	}

	public static String extractVeraId(File pRawLogFile) throws RawLogException {
		JSONArray jArr = extractJSON(pRawLogFile);
		try {
			return (jArr.length() > 0) ? jArr.getJSONObject(0).getString("vera_serial") : "no_vera";
		} catch (Exception e) {
			throw new RawLogException(e.getMessage());
		}
	}

	public static String extractUserId(File pRawLogFile) throws RawLogException {
		JSONArray jArr = extractJSON(pRawLogFile);
		try {
			return (jArr.length() > 0) ? jArr.getJSONObject(0).getString("user") : "no_user";
		} catch (Exception e) {
			throw new RawLogException(e.getMessage());
		}
	}

	public static List<SoftLog> extractLogs(File pRawLogFile) throws RawLogException {
		return extractLogs(pRawLogFile, null);
	}

	public static List<SoftLog> extractLogs(File pRawLogFile, LogExtractorListener pListener) throws RawLogException {
		List<SoftLog> myLogs = new ArrayList<>();

		// 1. Format logs
		if (pListener != null) {
			pListener.formatLogs();
		}
		JSONArray logs = extractJSON(pRawLogFile);

		// 2. Extract logs
		if (pListener != null) {
			pListener.startLogExtraction();
		}
		for (int i = 0; i < logs.length(); i++) {
			JSONObject jLog = logs.getJSONObject(i);
			try {
				myLogs.add(new SoftLog(jLog));
				if (pListener != null) {
					float progress = ((float) i * 100) / logs.length();
					pListener.logExtractionProgress((int) progress);
				}
			} catch (RawLogException exception) {
				System.out.println("No me gusta, porque hay un malformed log: " + exception);
			}
		}

		return myLogs;
	}
}
