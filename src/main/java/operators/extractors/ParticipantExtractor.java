package operators.extractors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import beans.participants.Participant;
import loganalyser.operators.FileExtractor;

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

	public static List<Participant> extractParticipants(File pParticipantsFile) throws Exception {
		List<Participant> participants = new ArrayList<>();
		JSONObject jsonParticipants = extractJSON(pParticipantsFile);
		jsonParticipants.keys().forEachRemaining((name) -> {
			Participant participant = new Participant(name.toString());
			try {
				participant.fillWithJSON(jsonParticipants.getJSONObject(name.toString()));
				participants.add(participant);
			} catch (Exception exception) {
				System.out.println("Unable to collect info for: " + name.toString() + "\n" + exception);
			}
		});
		return participants;
	}

	/**
	 * Get JSON representation of the specified participant
	 * 
	 * @param pParticipantFile
	 *            the file
	 * @param pParticipantName
	 *            the name of the participant
	 * @return the JSON representation
	 * @throws Exception
	 *             The file is not found, its content can not be format to JSON
	 *             or the participant is not in the JSON
	 */
	public static Participant extractParticipant(File pParticipantFile, String pParticipantName) throws Exception {
		try {
			return extractParticipants(pParticipantFile).stream()
					.filter(participant -> participant.getName().equals(pParticipantName)).findFirst().get();
		} catch (Exception e) {
			throw new IllegalArgumentException("Participant not found");
		}
	}

	public static boolean validateFile(File pParticipantsFile) {
		try {
			return !extractParticipants(pParticipantsFile).isEmpty();
		} catch (Exception exception) {
			throw new IllegalArgumentException("Invalid file: " + exception.getMessage());
		}

	}

}
