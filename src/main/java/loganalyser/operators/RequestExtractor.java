package loganalyser.operators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class RequestExtractor {

	public enum RequestType {
		LogRequest, ReportRequest
	}

	private static final String REQUEST_LOG_FORMAT = "elasticdump --input=http://localhost:9200/logstash-%s/event --sourceOnly --output=%s --searchBody \"{\\\"query\\\":{\\\"term\\\":{\\\"vera_serial\\\":\\\"%s\\\"}}}\"";
	private static final String TEMP_FILE = "temp";
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyy.MM.dd");

	private static String logRequest(final String pOutputFile, final String pVeraId, final String pDay) {
		return request(RequestType.LogRequest, pOutputFile, pVeraId, pDay);
	}

	private static String reportRequest(final String pOutputFile, final String pVeraId, final String pDay) {
		return request(RequestType.ReportRequest, pOutputFile, pVeraId, pDay);
	}

	private static String request(final RequestType pRequestType, String pOutputFile, final String pVeraId, final String pDay) {
		String request = null;
		pOutputFile = pOutputFile.endsWith(".json") ? pOutputFile : String.format("%s.json", pOutputFile);
		switch (pRequestType) {
		case LogRequest:
			request = String.format(REQUEST_LOG_FORMAT, pDay, pOutputFile, pVeraId);
			break;
		case ReportRequest:
			request = "Not implemented yet";
			break;
		}
		return request;
	}

	private static String iterateOverPeriod(final RequestType pRequestType, final String pOutputFile, final String pVeraId,
			final String pStartDay, final String pEndDay) throws ParseException {

		final Date date = DAY_FORMAT.parse(pStartDay);
		final Date endDate = DAY_FORMAT.parse(pEndDay);
		final List<String> requests = new ArrayList<>();

		if (date.equals(endDate)) {
			requests.add(removeFile(pOutputFile));
			requests.add(
					pRequestType == RequestType.LogRequest ? logRequest(pOutputFile, pVeraId, DAY_FORMAT.format(date))
							: reportRequest(pOutputFile, pVeraId, DAY_FORMAT.format(date)));
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
						: reportRequest(pOutputFile, pVeraId, DAY_FORMAT.format(calendar.getTime())));
				// requests.add(logRequest(pOutputFile, pVeraId,
				// DAY_FORMAT.format(calendar.getTime())));
				requests.add(copyFileContentToDestFile(pOutputFile, TEMP_FILE));
				calendar.add(Calendar.DATE, 1);
			}
			requests.add(copyFileContentToDestFile(TEMP_FILE, pOutputFile));
			requests.add(removeFile(TEMP_FILE));
		}

		return StringUtils.join(requests, ";\n").concat(";");

	}

	public static String logRequests(final String pOutputFile, final String pVeraId, final String pStartDay, final String pEndDay)
			throws ParseException {
		return iterateOverPeriod(RequestType.LogRequest, pOutputFile, pVeraId, pStartDay, pEndDay);
	}

	public static String reportRequests(final String pOutputFile, final String pVeraId, final String pStartDay, final String pEndDay)
			throws ParseException {
		return iterateOverPeriod(RequestType.ReportRequest, pOutputFile, pVeraId, pStartDay, pEndDay);
	}

	private static String removeFile(final String pFile) {
		return String.format("rm -f %s", pFile);
	}

	private static String copyFileContentToDestFile(final String pSrcFile, final String pDestFile) {
		return String.format("cat %s >> %s", pSrcFile, pDestFile);
	}

}
