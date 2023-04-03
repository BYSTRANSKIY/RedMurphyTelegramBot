package ua.redmurphy.redmurphybot_v_1.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ua.redmurphy.redmurphybot_v_1.entity.Answer;
import ua.redmurphy.redmurphybot_v_1.entity.Exercise;
import ua.redmurphy.redmurphybot_v_1.repository.AnswerRepository;
import ua.redmurphy.redmurphybot_v_1.repository.ExerciseRepository;
import ua.redmurphy.redmurphybot_v_1.service.AnswerService;

import java.util.Optional;

@Log4j
@Service
public class AnswerServiceImpl implements AnswerService {
    private long numberOfAnswer;

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
        this.numberOfAnswer = count();
    }

    @Override
    public boolean checkAvailabilityAnswer(long answerId) {
        return 0 <= numberOfAnswer && numberOfAnswer <= answerId;
    }

    @Override
    public int checkPosition(int answerId) {
        if (answerId == 1) {
            return -1;
        } else if (answerId == numberOfAnswer) {
            return 1;
        }
        return 0;
    }

    @Override
    public long count() {
        return answerRepository.count();
    }

    @Override
    public Optional<Answer> findAnswer(int id) {
        return answerRepository.findById(id);
    }

    @Override
    public void saveAnswer(String fileId) {
        var answer = new Answer();
        answer.setFileId(fileId);
        answerRepository.save(answer);
        numberOfAnswer++;
        log.debug("Answer saved, count: " + numberOfAnswer);
    }
}
