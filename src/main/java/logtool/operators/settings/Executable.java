package logtool.operators.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logtool.beans.Routine;
import logtool.beans.activityresults.ActivityResult;
import logtool.utils.Configuration;

public interface Executable {

	public ProcessBuilder script(Routine pRoutine, File pFileToUse) throws Exception;

	public List<ActivityResult> getResults(File pFileToUse) throws Exception;

	public String getScriptName();

	public void validParameters() throws Exception;

	default String[] executeScript(ProcessBuilder pProcessBuilder) throws IOException, InterruptedException {

		File scriptDirectory = Configuration.SCRIPTS_FOLDER;
		Set<PosixFilePermission> perms = new HashSet<>();
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		perms.add(PosixFilePermission.OWNER_READ);

		for (File file : scriptDirectory.listFiles()) {
			if (!file.canExecute() || !file.canRead()) {
				Files.setPosixFilePermissions(file.toPath(), perms);
				System.out.println(String.format("%s is now readable and executable", file.getName()));
			}
		}

		pProcessBuilder.directory(scriptDirectory);
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
