package ua.redmurphy.redmurphybot_v_1.service;

import ua.redmurphy.redmurphybot_v_1.entity.Unit;

import java.util.Optional;

public interface UnitService {

    boolean checkAvailabilityUnit(long unitId);

    int checkPosition(int unitId);

    long count();

    Optional<Unit> findUnit(int id);

    void saveUnit(String fileId);
}
