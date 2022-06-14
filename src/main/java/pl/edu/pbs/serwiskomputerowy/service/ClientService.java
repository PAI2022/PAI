package pl.edu.pbs.serwiskomputerowy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pbs.serwiskomputerowy.model.Client;
import pl.edu.pbs.serwiskomputerowy.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients() {return clientRepository.findAll();}

    public void saveClient(Client client){clientRepository.save(client);}

    public void deleteClient(Client client){clientRepository.delete(client);}

    public Optional<Client> getClientById(Integer id) {return clientRepository.findById(id);}

}
