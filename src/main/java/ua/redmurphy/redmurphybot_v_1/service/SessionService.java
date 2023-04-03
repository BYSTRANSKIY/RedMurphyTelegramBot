package ua.redmurphy.redmurphybot_v_1.service;

import ua.redmurphy.redmurphybot_v_1.entity.Session;
import ua.redmurphy.redmurphybot_v_1.entity.enums.ActionStatus;
import ua.redmurphy.redmurphybot_v_1.entity.enums.TextStatus;

import java.util.Optional;

public interface SessionService {

    Optional<Session> findSession(Long chatId);

    void updateSessionTextStatus(Long chatId, TextStatus textStatus);

    void saveSession(Long chatId, TextStatus textStatus, ActionStatus actionStatus);

    void updateSessionActionStatus(Long chatId, ActionStatus actionStatus);

    void updateSessionActionId(Long chatId, ActionStatus actionStatus, int actionId);

    void updateSessionUnitId(Long chatId, int unitId);

    void updateSessionExerciseId(Long chatId, int exerciseId);

    void updateSessionAnswerId(Long chatId, int answerId);

    void updateSessionMessageId(Long chatId, Integer messageId);
}
