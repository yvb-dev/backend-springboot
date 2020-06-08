package ru.javabegin.tasklist.backendspringboot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.javabegin.tasklist.backendspringboot.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE " +
            "(:title is null or :title='' or lower(c.title) like lower(concat('%',:title,'%')))" +
            "order by c.title asc")
    List<Category> findByTitle(@Param("title") String title);

    List<Category> findAllByOrderByTitleAsc();
}
