package loganalyser.operators.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import loganalyser.beans.Routine;
import loganalyser.beans.activityresults.ActivityResult;
import loganalyser.utils.Configuration;

public interface Executable {

	public ProcessBuilder script(Routine pRoutine, File pFileToUse) throws Exception;

	public List<ActivityResult> getResults(File pFileToUse) throws Exception;

	public String getScriptName();

	public void validParameters() throws Exception;

	default String[] executeScript(ProcessBuilder pProcessBuilder) throws IOException, InterruptedException {

		pProcessBuilder.directory(Configuration.SCRIPTS_FOLDER);
		System.out.println(pProcessBuilder.command());
		Process process = pProcessBuilder.start();
		process.waitFor();

		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString().split(System.getProperty("line.separator"));
	}

}
