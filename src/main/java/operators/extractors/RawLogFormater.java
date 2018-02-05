package operators.extractors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import beans.SoftLog;
import exceptions.RawLogException;

public class RawLogFormater extends FileExtractor {

	public static void validateRawLogFile(File pRawLogFile) throws RawLogException {
		JSONArray jArray = extractJSON(pRawLogFile);
		for (int i = 0; i < jArray.length(); i++) {
			try {
				new SoftLog(jArray.getJSONObject(i));
			} catch (RawLogException e) {
				if (StringUtils.isEmpty(e.getMessage()) || !e.getMessage().contains(RawLogException.INVALID_DEVICE)) {
					throw e;
				}
			}
		}
	}

	public static JSONArray extractJSON(File pRawLogFile) throws RawLogException {
		String content;
		try {
			content = readFile(pRawLogFile).replace("}\n", "},");
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
		validateRawLogFile(pRawLogFile);
		List<SoftLog> myLogs = new ArrayList<>();
		JSONArray logs = extractJSON(pRawLogFile);
		logs.forEach(log -> {
			try {
				myLogs.add(new SoftLog(((JSONObject) log)));
			} catch (RawLogException exception) {
				System.out.println("No me gusta, porque hay un malformed log: " + exception);
			}
		});

		return myLogs;
	}
}
