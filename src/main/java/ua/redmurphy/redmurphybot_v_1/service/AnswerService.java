package ua.redmurphy.redmurphybot_v_1.service;

import ua.redmurphy.redmurphybot_v_1.entity.Answer;
import ua.redmurphy.redmurphybot_v_1.entity.Unit;

import java.util.Optional;

public interface AnswerService {

    boolean checkAvailabilityAnswer(long answerId);

    int checkPosition(int answerId);

    long count();

    Optional<Answer> findAnswer(int id);

    void saveAnswer(String fileId);
}
