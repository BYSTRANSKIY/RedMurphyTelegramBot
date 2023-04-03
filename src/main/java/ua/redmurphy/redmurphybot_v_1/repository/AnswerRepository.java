package ua.redmurphy.redmurphybot_v_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.redmurphy.redmurphybot_v_1.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
}
