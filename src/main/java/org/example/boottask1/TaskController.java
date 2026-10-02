package org.example.boottask1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final Map<Long, Task> tasks = new LinkedHashMap<>();
    private AtomicLong nextId = new AtomicLong(1);

    @Value("${tasktracker.max-tasks}")
    private int maxTasks;

    @Value("${tasktracker.default-page-size}")
    private int defaultPageSize;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (tasks.size() >= maxTasks) {
            log.warn("Cannot create task: limit of {} reached", maxTasks);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task limit reached");
        }
        long i = nextId.getAndIncrement();
        task.setId(i);
        tasks.put(task.getId(), task);
        log.info("Created task {}", task.getId());
        return ResponseEntity.created(URI.create("/api/tasks/" + task.getId())).body(task);
    }

    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) Boolean completed,
                                @RequestParam(required = false) Integer limit) {
        int pageSize = limit == null ? defaultPageSize : limit;
        log.debug("Listing tasks: completed={}, limit={}", completed, pageSize);
        if (pageSize < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit must be positive");
        }

        List<Task> ret = new ArrayList<>();
        for (Task task : tasks.values()) {
            task.setMaxTask(maxTasks);
            if (completed == null || task.isCompleted() == completed) {
                ret.add(task);
                if (ret.size() == pageSize) {
                    break;
                }
            }
        }
        return ret;
    }

    @GetMapping("/{id}")
    public Task getTask(@PathVariable Long id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return task;
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        task.setId(id);
        tasks.put(id, task);
        log.info("Updated task {}", id);
        return task;
    }

    @PatchMapping("/{id}/complete")
    public Task completeTask(@PathVariable Long id) {
        Task task = getTask(id);
        task.setCompleted(true);
        log.info("Completed task {}", id);
        return task;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        tasks.remove(id);
        log.info("Deleted task {}", id);
        return ResponseEntity.noContent().build();
    }
}
