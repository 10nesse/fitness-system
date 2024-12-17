// src/main/java/com/fitness/controller/ClientController.java
package com.fitness.controller.api;

import com.fitness.entity.Client;
import com.fitness.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client createdClient = clientService.createClient(client);
        return ResponseEntity.status(201).body(createdClient);
    }

    @PostMapping("/edit")
    public String updateClient(@ModelAttribute Client client) {
        clientService.updateClient(client);
        return "redirect:/web/clients";
    }

    // Другие методы (если есть)
}
