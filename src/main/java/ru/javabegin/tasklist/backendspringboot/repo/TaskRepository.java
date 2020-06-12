package ru.javabegin.tasklist.backendspringboot.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.javabegin.tasklist.backendspringboot.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:title is null OR :title='' OR lower(t.title) LIKE lower(concat('%', :title, '%'))) AND" +
            "(:completed is null OR t.completed = :completed) AND" +
            "(:priorityId is null OR t.priority.id = :priorityId) AND" +
            "(:categoryId is null OR t.category.id = :categoryId)")
    Page<Task> findByParams(@Param("title") String title,
                            @Param("completed") Integer completed,
                            @Param("priorityId") Long priorityId,
                            @Param("categoryId") Long categoryId,
                            Pageable pageable);
}
