package pl.edu.pbs.serwiskomputerowy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pbs.serwiskomputerowy.model.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
}
