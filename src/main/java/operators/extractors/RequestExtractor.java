package operators.extractors;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import exceptions.RequestException;

public class RequestExtractor {

	private final static Pattern mDayPattern = Pattern.compile("\\d{4}.\\d{2}.\\d{2}$");
	private final static Pattern mMonthPattern = Pattern.compile("\\d{4}.\\d{2}.[*]$");
	private final static Pattern mVeraPattern = Pattern.compile("\\d{8}$");

	private final static String URL_REQUEST_FORMAT = "elasticdump --input=http://localhost:9200/logstash-%s/event --sourceOnly --output=%s --searchBody '{\"query\":{\"term\":{\"vera_serial\":\"%s\"}}}'%s";

	public static String extractLogsAmongPeriod(String pPeriod, String pVeraId, boolean pSilent)
			throws RequestException {

		if (!mDayPattern.matcher(pPeriod).find() && !mMonthPattern.matcher(pPeriod).find()) {
			throw new RequestException(RequestException.PERIOD_DOES_NOT_MATCH_PATERN);
		} else if (!mVeraPattern.matcher(pVeraId).find())
			throw new RequestException(new StringBuffer(RequestException.VERA_DOES_NOT_MATCH_PATERN)
					.append(" - current is: ").append(pPeriod).toString());
		else {
			String outputFile = String.format("%s.json", pPeriod.replace(".", "_"));
			String silentMod = pSilent ? " --quiet" : "";

			return String.format(URL_REQUEST_FORMAT, pPeriod, outputFile, pVeraId, silentMod);
		}
	}

	public static String extractLogsAmongPeriods(List<String> pPeriods, String pOutputFile, String pVeraId, boolean pSilent)
			throws RequestException {

		StringBuilder strB = new StringBuilder();
		pPeriods.forEach(period -> {
			try {
				strB.append(storeLogsToOutputfile(period, pOutputFile, pVeraId, pSilent)).append(";");
			} catch (RequestException e) {
				e.printStackTrace();
			}
		});

		return strB.toString();
	}

	public static String storeLogsToOutputfile(String pPeriod, String pOutputFileName, String pVeraId, boolean pSilent)
			throws RequestException {

		String inputFile = String.format("%s.json", pPeriod.replace(".", "_"));

		String[] cmds = new String[] { extractLogsAmongPeriod(pPeriod, pVeraId, pSilent),
				String.format("cat %s >> %s", inputFile, pOutputFileName),
				String.format("echo %s has been extracted", pPeriod), String.format("rm -f %s", inputFile) };
		return StringUtils.join(cmds, ";");
	}

}
