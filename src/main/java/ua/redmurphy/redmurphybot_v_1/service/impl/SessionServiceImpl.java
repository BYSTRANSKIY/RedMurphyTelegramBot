package ua.redmurphy.redmurphybot_v_1.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.redmurphy.redmurphybot_v_1.entity.Session;
import ua.redmurphy.redmurphybot_v_1.entity.enums.ActionStatus;
import ua.redmurphy.redmurphybot_v_1.entity.enums.TextStatus;
import ua.redmurphy.redmurphybot_v_1.repository.SessionRepository;
import ua.redmurphy.redmurphybot_v_1.service.SessionService;

import java.util.Optional;

@Log4j
@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    public SessionServiceImpl(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Optional<Session> findSession(Long chatId) {
        return sessionRepository.findById(chatId);
    }

    @Override
    @Transactional
    public void updateSessionTextStatus(Long chatId, TextStatus textStatus) {
        sessionRepository.updateSessionSetTextStatusForChatId(chatId, textStatus);
        log.debug("textStatus updated, chatId: " + chatId);
    }

    @Override
    @Transactional
    public void updateSessionActionStatus(Long chatId, ActionStatus actionStatus) {
        sessionRepository.updateSessionSetActionStatusForChatId(chatId, actionStatus);
        log.debug("actionStatus updated, chatId: " + chatId);
    }

    @Override
    @Transactional
    public void updateSessionActionId(Long chatId, ActionStatus actionStatus, int actionId) {
        switch (actionStatus) {
            case UNIT -> updateSessionUnitId(chatId, actionId);
            case EXERCISE -> updateSessionExerciseId(chatId, actionId);
            case ANSWER -> updateSessionAnswerId(chatId, actionId);
            default -> log.error("Error update session actionId");
        }
        log.debug("updateSessionActionId");
    }

    @Override
    @Transactional
    public void updateSessionUnitId(Long chatId, int unitId) {
        sessionRepository.updateSessionSetUnitIdForChatId(chatId, unitId);
        log.debug("updateSessionUnitId");
    }

    @Override
    @Transactional
    public void updateSessionExerciseId(Long chatId, int exerciseId) {
        sessionRepository.updateSessionSetExerciseIdForChatId(chatId, exerciseId);
        log.debug("updateSessionExerciseId");
    }

    @Override
    @Transactional
    public void updateSessionAnswerId(Long chatId, int answerId) {
        sessionRepository.updateSessionSetAnswerIdForChatId(chatId, answerId);
        log.debug("updateSessionAnswerId");
    }

    @Override
    @Transactional
    public void updateSessionMessageId(Long chatId, Integer messageId) {
        sessionRepository.updateSessionSetMessageIdForChatId(chatId, messageId);
        log.debug("updateSessionMessageId");
    }

    @Override
    public void saveSession(Long chatId, TextStatus textStatus, ActionStatus actionStatus) {
        var sessionStatus = new Session();
        sessionStatus.setChatId(chatId);
        sessionStatus.setTextStatus(textStatus);
        sessionStatus.setActionStatus(actionStatus);
        sessionRepository.save(sessionStatus);
        log.debug("Status saved chatId:" + chatId);
    }
}
