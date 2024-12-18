package com.fitness.service;

import com.fitness.entity.Client;
import com.fitness.entity.Trainer;
import com.fitness.entity.User;
import com.fitness.repository.TrainerRepository;
import com.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;

    public TrainerService(TrainerRepository trainerRepository, UserRepository userRepository) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
    }

    public Optional<Client> findByUserUsername(String username) {
        return trainerRepository.findByUser_Username(username);
    }

    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    public Optional<Trainer> findById(Long id) {
        return trainerRepository.findById(id);
    }

    public Trainer saveTrainer(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    public void deleteById(Long id) {
        trainerRepository.deleteById(id);
    }


    public Trainer createTrainer(Trainer trainer) {
        // Проверка существования пользователя
        User user = userRepository.findById(trainer.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + trainer.getUser().getId()));

        // Проверка, не связан ли пользователь уже с другим тренером
        if (trainerRepository.existsByUser(user)) {
            throw new RuntimeException("User is already associated with another trainer.");
        }

        // Проверка уникальности email
        if (trainerRepository.existsByEmail(trainer.getEmail())) {
            throw new RuntimeException("Trainer with this email already exists.");
        }

        trainer.setUser(user);
        return trainerRepository.save(trainer);
    }



}
