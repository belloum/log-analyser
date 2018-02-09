package operators.extractors;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import beans.DailyReport;
import loganalyser.operators.FileExtractor;

public class ReportExtractor extends FileExtractor {

	public enum ReportType {
		DAILY, WEEKLY
	}

	public static String getRequests(String formattedPeriod, ReportType reportType, boolean silent) throws Exception {
		Pattern dayPattern = Pattern.compile("\\d{4}.\\d{2}.\\d{2}$");
		Pattern monthPattern = Pattern.compile("\\d{4}.\\d{2}.[*]$");
		Matcher dayMatcher = dayPattern.matcher(formattedPeriod);
		Matcher monthMatcher = monthPattern.matcher(formattedPeriod);

		String report = "";
		switch (reportType) {
		case DAILY:
			report = "daily";
			break;
		case WEEKLY:
			report = "weekly";
			break;
		}

		if (!dayMatcher.find() && !monthMatcher.find())
			throw new Exception("Invalid period");

		else {
			StringBuilder fileBuilder = new StringBuilder(formattedPeriod.replace(".", "_"));
			fileBuilder.append(".json");
			String outputFile = fileBuilder.toString();
			String silentMod = silent ? " --quiet" : "";

			return String.format(Locale.FRANCE,
					"elasticdump --input=http://localhost:9200/logstash-%s --sourceOnly --output=%s --searchBody '{\"query\":{\"term\":{\"report_type\":\"%s\"}}}'%s",
					formattedPeriod, outputFile, report, silentMod);

		}
	}

	public static JSONArray extractJSONArray(File extractedFile) throws Exception {
		String content = readFile(extractedFile);
		content = "[" + content;
		content = content.replace("}\n", "},");
		content += "]";
		return new JSONArray(content);
	}

	public static JSONObject extractJSONReport(JSONObject jReport) throws Exception {
		String json = jReport.getString("message");
		if (json.contains("daily")) {
			String body = json.split("\\|json=")[1];
			return new JSONObject(body);
		}
		return null;
	}

	public static JSONObject extractJSONAtPosition(File extractedFile, int position) throws Exception {
		JSONObject jReport = extractJSONArray(extractedFile).getJSONObject(position);
		return extractJSONReport(jReport);
	}

	public static Map<String, DailyReport> extractReports(File extractedFile) throws Exception {
		Map<String, DailyReport> map = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		JSONArray array = extractJSONArray(extractedFile);
		for (int i = 0; i < array.length(); i++) {
			DailyReport d = new DailyReport(array.getJSONObject(i));
			calendar.setTime(d.getDate());
			String key = String.format("%02d/%02d/%d", calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
			map.put(key, d);
		}
		return map;
	}

	public static DailyReport extractReport(File extractedFile, String dateKey) throws Exception {
		Map<String, DailyReport> map = extractReports(extractedFile);
		return map.containsKey(dateKey) ? map.get(dateKey) : null;
	}
}
