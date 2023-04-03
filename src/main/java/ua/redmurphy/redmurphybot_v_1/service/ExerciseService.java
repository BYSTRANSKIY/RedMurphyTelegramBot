package ua.redmurphy.redmurphybot_v_1.service;

import ua.redmurphy.redmurphybot_v_1.entity.Exercise;
import ua.redmurphy.redmurphybot_v_1.entity.Unit;

import java.util.Optional;

public interface ExerciseService {

    boolean checkAvailabilityExercise(long exerciseId);

    int checkPosition(int exerciseId);

    long count();

    Optional<Exercise> findExercise(int id);

    void saveExercise(String fileId);
}
