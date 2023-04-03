package ua.redmurphy.redmurphybot_v_1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.redmurphy.redmurphybot_v_1.entity.Unit;

public interface UnitRepository extends JpaRepository<Unit, Integer> {
}
