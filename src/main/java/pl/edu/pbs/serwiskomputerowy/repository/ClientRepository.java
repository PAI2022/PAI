package pl.edu.pbs.serwiskomputerowy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pbs.serwiskomputerowy.model.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
