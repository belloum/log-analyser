package logtool.settings;

import java.io.File;

/**
 * Interface which notifies Participant-settings updates
 * 
 * @author ariche
 *
 */
public interface ParticipantSettingsListener {
	void participantLogsFolderUpdated(File participantLogFolder);

	void participantRoutineFileUpdated(File participantRoutineFile);
}
