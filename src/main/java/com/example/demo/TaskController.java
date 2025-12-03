package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final List<Task> tasks = new ArrayList<>();
    private long nextId = 1;

    static class Task {
        public long id;
        public String title;
        public boolean done;

        public Task(long id, String title, boolean done) {
            this.id = id;
            this.title = title;
            this.done = done;
        }
    }

    @GetMapping
    public List<Task> getAll() {
        return tasks;
    }

    @GetMapping("/{id}")
    public Task getById(@PathVariable long id) {
        return tasks.stream()
                .filter(t -> t.id == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PostMapping
    public Task create(@RequestBody Task task) {
        Task newTask = new Task(nextId++, task.title, task.done);
        tasks.add(newTask);
        return newTask;
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable long id, @RequestBody Task task) {
        Task existing = getById(id);
        existing.title = task.title;
        existing.done = task.done;
        return existing;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        tasks.removeIf(t -> t.id == id);
    }
}