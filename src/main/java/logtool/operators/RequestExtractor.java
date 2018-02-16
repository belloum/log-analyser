package logtool.operators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import logtool.beans.RequestType;
import logtool.utils.Utils;

public class RequestExtractor {

	private static final String REQUEST_LOG_FORMAT = "elasticdump --input=http://localhost:9200/logstash-%s/event --sourceOnly --output=%s --searchBody \"{\\\"query\\\":{\\\"term\\\":{\\\"vera_serial\\\":\\\"%s\\\"}}}\"";

	/*
	 * 1: date 2:output 3: report_type 4:user.toLowerCase
	 */
	private static final String REQUEST_REPORT_FORMAT = "elasticdump --input=http://localhost:9200/logstash-%s --sourceOnly "
			+ "--output=%s --searchBody \"{\\\"query\\\": { \\\"bool\\\":{\\\"must\\\" : [{\\\"term\\\" :{\\\"report_type\\\" : \\\"%s\\\"}},{\\\"term\\\" :{\\\"user\\\" : \\\"%s\\\"}}]}}, \\\"_source\\\":[\\\"json\\\", \\\"user\\\"]}\"";

	// FIXME See all requests with desired sources
	// --input=http://localhost:9200/logstash-2018.02.15 --sourceOnly
	// --output=output.json --searchBody
	// "{\"query\":{\"dis_max\":{\"queries\":[{\"term\":{\"report_type\":\"daily\"}},{\"term\":{\"vera_serial\":\"45108765\"}}]}},
	// \"_source\":[\"json\", \"device\"]}";

	private static final String TEMP_FILE = "temp";
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	private static String logRequest(final String pOutputFile, final String pVeraId, final String pDay) {
		return request(RequestType.LogRequest, pOutputFile, pVeraId, pDay);
	}

	private static String reportRequest(final RequestType pRequestType, final String pOutputFile, final String pVeraId,
			final String pDay) {
		return request(pRequestType, pOutputFile, pVeraId, pDay);
	}

	private static String request(final RequestType pRequestType, String pOutputFile, final String pVeraId,
			final String pDay) {
		String request = null;
		pOutputFile = Utils.addFileExtension(pOutputFile, "json");
		final String identifier = pVeraId.toLowerCase();
		switch (pRequestType) {
		case LogRequest:
			request = String.format(REQUEST_LOG_FORMAT, pDay, pOutputFile, identifier);
			break;
		case DailyReportRequest:
			request = String.format(REQUEST_REPORT_FORMAT, pDay, pOutputFile, "daily", identifier);
			break;
		case WeeklyReportRequest:
			request = String.format(REQUEST_REPORT_FORMAT, pDay, pOutputFile, "weekly", identifier);
			break;
		}
		return request;
	}

	private static String iterateOverPeriod(final RequestType pRequestType, final String pOutputFile,
			final String pVeraId, final String pStartDay, final String pEndDay) throws ParseException {

		final Date date = DAY_FORMAT.parse(pStartDay);
		final Date endDate = DAY_FORMAT.parse(pEndDay);
		final List<String> requests = new ArrayList<>();

		if (date.equals(endDate)) {
			requests.add(removeFile(pOutputFile));
			requests.add(
					pRequestType == RequestType.LogRequest ? logRequest(pOutputFile, pVeraId, DAY_FORMAT.format(date))
							: reportRequest(pRequestType, pOutputFile, pVeraId, DAY_FORMAT.format(date)));
		}

		else {

			final Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.setTime(date);

			while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
				requests.add(removeFile(pOutputFile));
				requests.add(pRequestType == RequestType.LogRequest
						? logRequest(pOutputFile, pVeraId, DAY_FORMAT.format(calendar.getTime()))
						: reportRequest(pRequestType, pOutputFile, pVeraId, DAY_FORMAT.format(calendar.getTime())));
				requests.add(copyFileContentToDestFile(pOutputFile, TEMP_FILE));
				calendar.add(Calendar.DATE, 1);
			}
			requests.add(copyFileContentToDestFile(TEMP_FILE, pOutputFile));
			requests.add(removeFile(TEMP_FILE));
		}

		return StringUtils.join(requests, ";\n").concat(";");

	}

	public static String logRequests(final String pOutputFile, final String pVeraId, final String pStartDay,
			final String pEndDay) throws ParseException {
		return iterateOverPeriod(RequestType.LogRequest, pOutputFile, pVeraId, pStartDay, pEndDay);
	}

	public static String dailyReportRequests(final String pOutputFile, final String pVeraId, final String pStartDay,
			final String pEndDay) throws ParseException {
		return iterateOverPeriod(RequestType.DailyReportRequest, pOutputFile, pVeraId, pStartDay, pEndDay);
	}

	public static String weeklyReportRequests(final String pOutputFile, final String pVeraId, final String pStartDay,
			final String pEndDay) throws ParseException {
		return iterateOverPeriod(RequestType.WeeklyReportRequest, pOutputFile, pVeraId, pStartDay, pEndDay);
	}

	private static String removeFile(final String pFile) {
		return String.format("rm -f %s", pFile);
	}

	private static String copyFileContentToDestFile(final String pSrcFile, final String pDestFile) {
		return String.format("cat %s >> %s", pSrcFile, pDestFile);
	}

}
