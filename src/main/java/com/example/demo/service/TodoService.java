package com.example.demo.service;




import com.example.demo.model.TodoItem;
import com.example.demo.dto.TodoCreateRequest;
import com.example.demo.dto.TodoResponse;
import com.example.demo.dto.TodoUpdateRequest;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repo.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> list(Boolean completed) {
        List<TodoItem> items = (completed == null) ? repo.findAll() : repo.findByCompleted(completed);
        return items.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TodoResponse get(Long id) {
        TodoItem item = repo.findById(id).orElseThrow(() -> new NotFoundException("Todo not found: " + id));
        return toResponse(item);
    }

    @Transactional
    public TodoResponse create(TodoCreateRequest req) {
        TodoItem item = new TodoItem();
        item.setTitle(req.getTitle());
        item.setDescription(req.getDescription());
        return toResponse(repo.save(item));
    }

    @Transactional
    public TodoResponse update(Long id, TodoUpdateRequest req) {
        TodoItem item = repo.findById(id).orElseThrow(() -> new NotFoundException("Todo not found: " + id));
        item.setTitle(req.getTitle());
        item.setDescription(req.getDescription());
        item.setCompleted(req.isCompleted());
        return toResponse(repo.save(item));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("Todo not found: " + id);
        repo.deleteById(id);
    }

    private TodoResponse toResponse(TodoItem it) {
        return new TodoResponse(it.getId(), it.getTitle(), it.getDescription(), it.isCompleted(), it.getCreatedAt(), it.getUpdatedAt());
    }
}