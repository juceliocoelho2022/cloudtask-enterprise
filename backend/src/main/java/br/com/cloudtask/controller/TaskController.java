package br.com.cloudtask.controller;

import br.com.cloudtask.domain.TaskStatus;
import br.com.cloudtask.dto.TaskRequest;
import br.com.cloudtask.dto.TaskResponse;
import br.com.cloudtask.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponse> findAll(
            Authentication authentication,
            @RequestParam(required = false) TaskStatus status
    ) {
        return taskService.findAll(authentication.getName(), status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            Authentication authentication,
            @Valid @RequestBody TaskRequest request
    ) {
        return taskService.create(authentication.getName(), request);
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request
    ) {
        return taskService.update(authentication.getName(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long id) {
        taskService.delete(authentication.getName(), id);
    }
}
