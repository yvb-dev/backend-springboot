package ru.javabegin.tasklist.backendspringboot.service;


import org.springframework.stereotype.Service;
import ru.javabegin.tasklist.backendspringboot.entity.Priority;
import ru.javabegin.tasklist.backendspringboot.repo.PriorityRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PriorityService {
    private final PriorityRepository repository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.repository = priorityRepository;
    }

    public List<Priority> findAllByOrderByIdAsc() {
        return repository.findAllByOrderByIdAsc();
    }

    public Priority add(Priority priority) {
        return repository.save(priority);
    }

    public Priority update(Priority priority) {
        return repository.save(priority);
    }

    public Priority findById(Long id) {
        return repository.findById(id).get();
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<Priority> findByTitle(String title) {
        return repository.findByTitle(title);
    }
}
