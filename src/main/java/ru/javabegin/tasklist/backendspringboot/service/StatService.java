package ru.javabegin.tasklist.backendspringboot.service;

import org.springframework.stereotype.Service;
import ru.javabegin.tasklist.backendspringboot.entity.Stat;
import ru.javabegin.tasklist.backendspringboot.repo.StatRepository;

import javax.transaction.Transactional;

@Service
@Transactional
public class StatService {
    private final StatRepository repository;

    public StatService(StatRepository statRepository) {
        this.repository = statRepository;
    }

    public Stat findById(Long defaultId) {
        return repository.findById(defaultId).get();
    }
}
