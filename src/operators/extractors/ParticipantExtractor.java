package operators.extractors;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import beans.participants.Participant;

public class ParticipantExtractor extends FileExtractor {

	public ParticipantExtractor() {
	}

	/**
	 * Get JSON representation of the specified file
	 * 
	 * @param extractedFile
	 *            the file
	 * @return the JSON representation
	 * @throws Exception
	 *             The file is not found or its content can not be format to
	 *             JSON
	 */
	public static JSONObject extractJSON(File extractedFile) throws Exception {
		String content = "";
		try {
			content = readFile(extractedFile);
		} catch (Exception e) {
			throw new IOException("File '" + extractedFile.getName() + "' not found");
		}

		try {
			return new JSONObject(content);
		} catch (Exception e) {
			throw new JSONException("Can no make a json from: " + content);
		}
	}

	/**
	 * Get JSON representation of the specified participant
	 * 
	 * @param extractedFile
	 *            the file
	 * @param participantName
	 *            the name of the participant
	 * @return the JSON representation
	 * @throws Exception
	 *             The file is not found, its content can not be format to JSON
	 *             or the participant is not in the JSON
	 */
	public static Participant extractParticipant(File extractedFile, String participantName) throws Exception {
		JSONObject jsonFile = extractJSON(extractedFile);
		if (jsonFile.has(participantName)) {
			Participant participant = new Participant(participantName);
			participant.fillWithJSON(jsonFile.getJSONObject(participantName));
			return participant;
		}

		else
			throw new IllegalArgumentException("Participant not found");
	}

}
