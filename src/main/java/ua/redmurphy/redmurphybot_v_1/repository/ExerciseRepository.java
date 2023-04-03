package ua.redmurphy.redmurphybot_v_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.redmurphy.redmurphybot_v_1.entity.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
}
