package br.com.cloudtask.service;

import br.com.cloudtask.domain.Task;
import br.com.cloudtask.domain.TaskStatus;
import br.com.cloudtask.domain.User;
import br.com.cloudtask.dto.TaskRequest;
import br.com.cloudtask.dto.TaskResponse;
import br.com.cloudtask.exception.NotFoundException;
import br.com.cloudtask.repository.TaskRepository;
import br.com.cloudtask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll(String email, TaskStatus status) {
        User owner = getUser(email);
        List<Task> tasks = status == null
                ? taskRepository.findByOwnerIdOrderByCreatedAtDesc(owner.getId())
                : taskRepository.findByOwnerIdAndStatusOrderByCreatedAtDesc(owner.getId(), status);

        return tasks.stream().map(this::toResponse).toList();
    }

    @Transactional
    public TaskResponse create(String email, TaskRequest request) {
        User owner = getUser(email);

        Task task = Task.builder()
                .title(request.title().trim())
                .description(request.description())
                .status(request.status())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .owner(owner)
                .build();

        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse update(String email, Long id, TaskRequest request) {
        User owner = getUser(email);
        Task task = taskRepository.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));

        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setStatus(request.status());
        task.setPriority(request.priority());
        task.setDueDate(request.dueDate());

        return toResponse(task);
    }

    @Transactional
    public void delete(String email, Long id) {
        User owner = getUser(email);
        Task task = taskRepository.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));
        taskRepository.delete(task);
    }

    private User getUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
