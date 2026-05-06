package com.ossflow.journal.portability;

import com.ossflow.journal.competitionlog.application.CompetitionLogService;
import com.ossflow.journal.note.application.NoteService;
import com.ossflow.journal.trainingsession.application.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JournalExporter {

    private final NoteService noteService;
    private final TrainingSessionService trainingSessionService;
    private final CompetitionLogService competitionLogService;

    public Map<String, Object> exportFor(Long ownerId) {
        Pageable all = PageRequest.of(0, Integer.MAX_VALUE);
        var notes = noteService.list(ownerId, null, null, null, null, all).getContent();
        var trainingSessions = trainingSessionService.list(ownerId, all).getContent();
        var competitionLogs = competitionLogService.list(ownerId, all).getContent();
        return Map.of(
                "notes", notes,
                "trainingSessions", trainingSessions,
                "competitionLogs", competitionLogs
        );
    }
}
