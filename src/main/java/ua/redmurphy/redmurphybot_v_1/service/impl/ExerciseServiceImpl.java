package ua.redmurphy.redmurphybot_v_1.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ua.redmurphy.redmurphybot_v_1.entity.Exercise;
import ua.redmurphy.redmurphybot_v_1.repository.ExerciseRepository;
import ua.redmurphy.redmurphybot_v_1.service.ExerciseService;

import java.util.Optional;

@Log4j
@Service
public class ExerciseServiceImpl implements ExerciseService {
    private long numberOfExercise;

    private final ExerciseRepository exerciseRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
        this.numberOfExercise = count();
    }

    @Override
    public boolean checkAvailabilityExercise(long exerciseId) {
        return 0 <= numberOfExercise && numberOfExercise <= exerciseId;
    }

    @Override
    public int checkPosition(int exerciseId) {
        if (exerciseId == 1) {
            return -1;
        } else if (exerciseId == numberOfExercise) {
            return 1;
        }
        return 0;
    }

    @Override
    public long count() {
        return exerciseRepository.count();
    }

    @Override
    public Optional<Exercise> findExercise(int id) {
        return exerciseRepository.findById(id);
    }

    @Override
    public void saveExercise(String fileId) {
        var exercise = new Exercise();
        exercise.setFileId(fileId);
        exerciseRepository.save(exercise);
        numberOfExercise++;
        log.debug("Exercise saved, count: " + numberOfExercise);
    }
}
