package ua.redmurphy.redmurphybot_v_1.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ua.redmurphy.redmurphybot_v_1.entity.Unit;
import ua.redmurphy.redmurphybot_v_1.repository.UnitRepository;
import ua.redmurphy.redmurphybot_v_1.service.UnitService;

import java.util.Optional;

@Log4j
@Service
public class UnitServiceImpl implements UnitService {
    private long numberOfUnit;

    private final UnitRepository unitRepository;

    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
        this.numberOfUnit = count();
    }

    @Override
    public boolean checkAvailabilityUnit(long unitId) {
        return 0 <= numberOfUnit && numberOfUnit <= unitId;
    }

    @Override
    public int checkPosition(int unitId) {
        if (unitId == 1) {
            return -1;
        } else if (unitId == numberOfUnit) {
            return 1;
        }
        return 0;
    }

    @Override
    public long count() {
        return unitRepository.count();
    }

    @Override
    public Optional<Unit> findUnit(int id) {
        return unitRepository.findById(id);
    }

    @Override
    public void saveUnit(String fileId) {
        var unit = new Unit();
        unit.setFileId(fileId);
        unitRepository.save(unit);
        numberOfUnit++;
        log.debug("Unit saved count: " + numberOfUnit);
    }
}
