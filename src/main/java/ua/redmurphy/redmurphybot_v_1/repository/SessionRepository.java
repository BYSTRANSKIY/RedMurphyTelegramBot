package ua.redmurphy.redmurphybot_v_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ua.redmurphy.redmurphybot_v_1.entity.Session;
import ua.redmurphy.redmurphybot_v_1.entity.enums.ActionStatus;
import ua.redmurphy.redmurphybot_v_1.entity.enums.TextStatus;

public interface SessionRepository extends JpaRepository<Session, Long> {

//    @Modifying
//    @Query("update Session s set s = :session where s.chatId = :chatId")
//    void updateSession(Long chatId, Session session);

    @Modifying
    @Query("update Session s set s.textStatus = :textStatus where s.chatId = :chatId")
    void updateSessionSetTextStatusForChatId(Long chatId, TextStatus textStatus);

    @Modifying
    @Query("update Session s set s.actionStatus = :actionStatus where s.chatId = :chatId")
    void updateSessionSetActionStatusForChatId(Long chatId, ActionStatus actionStatus);

    @Modifying
    @Query("update Session s set s.messageId = :messageId where s.chatId = :chatId")
    void updateSessionSetMessageIdForChatId(Long chatId, int messageId);

    @Modifying
    @Query("update Session s set s.unitId = :unitId where s.chatId = :chatId")
    void updateSessionSetUnitIdForChatId(Long chatId, int unitId);

    @Modifying
    @Query("update Session s set s.exerciseId = :exerciseId where s.chatId = :chatId")
    void updateSessionSetExerciseIdForChatId(Long chatId, int exerciseId);

    @Modifying
    @Query("update Session s set s.answerId = :answerId where s.chatId = :chatId")
    void updateSessionSetAnswerIdForChatId(Long chatId, int answerId);
}
