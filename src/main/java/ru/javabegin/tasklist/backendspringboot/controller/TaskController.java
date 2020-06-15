package ru.javabegin.tasklist.backendspringboot.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javabegin.tasklist.backendspringboot.entity.Task;
import ru.javabegin.tasklist.backendspringboot.search.TaskSearchValues;
import ru.javabegin.tasklist.backendspringboot.service.TaskService;
import ru.javabegin.tasklist.backendspringboot.util.MyLogger;

import java.util.List;
import java.util.NoSuchElementException;


// Если возникнет exception - вернется код  500 Internal Server Error, поэтому не нужно все действия оборачивать в try-catch

// используем @RestController вместо обычного @Controller, чтобы все ответы сразу оборачивались в JSON
// иначе пришлось бы выполнять лишнюю работу, использовать @ResponseBody для ответа, указывать тип отправки JSON

// Названия методов могут быть любыми, главное не дублировать их имена и URL mapping
@RestController
@RequestMapping("/task") // базовый адрес
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // получение всех данных
    @GetMapping("/all")
    public ResponseEntity<List<Task>> findAll() {
        MyLogger.showMethodName("task: findAll() ---------------------------------------------------------------- ");
        return ResponseEntity.ok(taskService.findAll());
    }

    // добавление
    @PostMapping("/add")
    public ResponseEntity<Task> add(@RequestBody Task task) {
        MyLogger.showMethodName("task: add() ---------------------------------------------------------------- ");
        // проверка на обязательные параметры
        if (task.getId() != null && task.getId() != 0) {
            // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
            return new ResponseEntity("redundant param: id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }
        // если передали пустое значение title
        if (task.getTitle() == null || task.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(taskService.add(task)); // возвращаем созданный объект со сгенерированным id
    }

    // обновление
    @PutMapping("/update")
    public ResponseEntity<Task> update(@RequestBody Task task) {
        MyLogger.showMethodName("task: update() ---------------------------------------------------------------- ");
        // проверка на обязательные параметры
        if (task.getId() == null || task.getId() == 0) {
            return new ResponseEntity("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }
        // если передали пустое значение title
        if (task.getTitle() == null || task.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        // save работает как на добавление, так и на обновление
        taskService.update(task);
        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
    }

    // удаление по id
    // параметр id передаются не в BODY запроса, а в самом URL
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        MyLogger.showMethodName("task: delete() ------------------------------------------------------------------------ ");
        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            taskService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
    }

    // получение объекта по id
    @GetMapping("/id/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        MyLogger.showMethodName("task: findById() ---------------------------------------------------------------------- ");
        Task task = null;
        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            task = taskService.findById(id);
        } catch (NoSuchElementException e) { // если объект не будет найден
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(task);
    }

    // поиск по любым параметрам TaskSearchValues
    @PostMapping("/search")
    public ResponseEntity<Page<Task>> search(@RequestBody TaskSearchValues taskSearchValues) {
        MyLogger.showMethodName("PriorityController: search() ---------------------------------------------------------- ");
        // имитация загрузки (для тестирования индикаторов загрузки)
               //        imitateLoading();

        // исключить NullPointerException
        //т.к. разный фронтенд может по разному присылать null (пример: undefined)
        String title = taskSearchValues.getTitle() != null ? taskSearchValues.getTitle() : null;
        // конвертируем Boolean в Integer
        Integer completed = taskSearchValues.getCompleted() != null ? taskSearchValues.getCompleted() : null;

        Long priorityId = taskSearchValues.getPriorityId() != null ? taskSearchValues.getPriorityId() : null;
        Long categoryId = taskSearchValues.getCategoryId() != null ? taskSearchValues.getCategoryId() : null;

        //parameters for paging
        Integer pageNumber = taskSearchValues.getPageNumber() != null ? taskSearchValues.getPageNumber() : null;
        Integer pageSize = taskSearchValues.getPageSize() != null ? taskSearchValues.getPageSize() : null;

        //parameters for sorting
        String sortColumn = taskSearchValues.getSortColumn() != null ? taskSearchValues.getSortColumn() : null;
        String sortDirection = taskSearchValues.getSortDirection() != null ? taskSearchValues.getSortDirection() : null;

        Sort.Direction direction = (sortDirection == null || sortDirection.trim().toLowerCase().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC);

        Sort sort = Sort.by(direction, sortColumn);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        // если вместо текста будет пусто или null - вернутся все категории
        Page<Task> result = taskService.findByParams(title, completed, priorityId, categoryId, pageRequest);
        return ResponseEntity.ok(result);
    }
}

