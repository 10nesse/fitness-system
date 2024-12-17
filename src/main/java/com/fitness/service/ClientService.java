package com.fitness.service;

import com.fitness.entity.Client;
import com.fitness.entity.User;
import com.fitness.repository.ClientRepository;
import com.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    public ClientService(ClientRepository clientRepository, UserRepository userRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByUser_Email(email);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public void deleteById(Long id) {
        clientRepository.deleteById(id);
    }

    public Client updateClient(Client client) {
        Client existingClient = clientRepository.findById(client.getId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + client.getId()));

        if (!existingClient.getEmail().equals(client.getEmail()) && clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Client with this email already exists.");
        }

        User userToSet;
        if (!existingClient.getUser().getId().equals(client.getUser().getId())) {
            User newUser = userRepository.findById(client.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + client.getUser().getId()));

            if (clientRepository.existsByUser(newUser)) {
                throw new RuntimeException("User is already associated with another client.");
            }
            userToSet = newUser;
        } else {
            userToSet = existingClient.getUser();
        }

        existingClient.setFirstName(client.getFirstName());
        existingClient.setLastName(client.getLastName());
        existingClient.setEmail(client.getEmail());
        existingClient.setPhoneNumber(client.getPhoneNumber());
        existingClient.setUser(userToSet);

        return clientRepository.save(existingClient);
    }

    public Client createClient(Client client) {
        User user = userRepository.findById(client.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + client.getUser().getId()));

        if (clientRepository.existsByUser(user)) {
            throw new RuntimeException("User is already associated with another client.");
        }
        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Client with this email already exists.");
        }

        client.setUser(user);
        return clientRepository.save(client);
    }




}
