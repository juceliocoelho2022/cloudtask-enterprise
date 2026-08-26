package br.com.cloudtask.service;

import br.com.cloudtask.domain.Task;
import br.com.cloudtask.domain.TaskPriority;
import br.com.cloudtask.domain.TaskStatus;
import br.com.cloudtask.domain.User;
import br.com.cloudtask.dto.TaskRequest;
import br.com.cloudtask.dto.TaskResponse;
import br.com.cloudtask.exception.NotFoundException;
import br.com.cloudtask.repository.TaskRepository;
import br.com.cloudtask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    private TaskService taskService;

    private User owner;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, userRepository);
        owner = User.builder()
                .id(10L)
                .name("Jucelio")
                .email("jucelio@example.com")
                .passwordHash("encoded")
                .build();
    }

    @Test
    void shouldCreateTaskForAuthenticatedUser() {
        when(userRepository.findByEmailIgnoreCase(owner.getEmail())).thenReturn(Optional.of(owner));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(99L);
            return task;
        });

        TaskRequest request = new TaskRequest(
                "  Configurar CloudTask  ",
                "Validar CRUD",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                LocalDate.of(2026, 9, 1)
        );

        TaskResponse response = taskService.create(owner.getEmail(), request);

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.title()).isEqualTo("Configurar CloudTask");
        assertThat(response.status()).isEqualTo(TaskStatus.TODO);
        assertThat(response.priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 9, 1));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldFilterTasksByStatus() {
        Task task = Task.builder()
                .id(1L)
                .title("Teste")
                .description("Teste de filtro")
                .status(TaskStatus.DONE)
                .priority(TaskPriority.MEDIUM)
                .owner(owner)
                .build();

        when(userRepository.findByEmailIgnoreCase(owner.getEmail())).thenReturn(Optional.of(owner));
        when(taskRepository.findByOwnerIdAndStatusOrderByCreatedAtDesc(owner.getId(), TaskStatus.DONE))
                .thenReturn(List.of(task));

        List<TaskResponse> result = taskService.findAll(owner.getEmail(), TaskStatus.DONE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().status()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository).findByOwnerIdAndStatusOrderByCreatedAtDesc(owner.getId(), TaskStatus.DONE);
        verify(taskRepository, never()).findByOwnerIdOrderByCreatedAtDesc(anyLong());
    }

    @Test
    void shouldUpdateOwnedTask() {
        Task task = Task.builder()
                .id(5L)
                .title("Título antigo")
                .description("Descrição antiga")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.LOW)
                .owner(owner)
                .build();

        when(userRepository.findByEmailIgnoreCase(owner.getEmail())).thenReturn(Optional.of(owner));
        when(taskRepository.findByIdAndOwnerId(task.getId(), owner.getId())).thenReturn(Optional.of(task));

        TaskRequest request = new TaskRequest(
                "Título atualizado",
                "Descrição atualizada",
                TaskStatus.IN_PROGRESS,
                TaskPriority.CRITICAL,
                LocalDate.of(2026, 9, 2)
        );

        TaskResponse response = taskService.update(owner.getEmail(), task.getId(), request);

        assertThat(response.title()).isEqualTo("Título atualizado");
        assertThat(response.description()).isEqualTo("Descrição atualizada");
        assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(response.priority()).isEqualTo(TaskPriority.CRITICAL);
        assertThat(response.dueDate()).isEqualTo(LocalDate.of(2026, 9, 2));
    }

    @Test
    void shouldDeleteOwnedTask() {
        Task task = Task.builder()
                .id(7L)
                .title("Excluir")
                .description("Excluir tarefa")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.MEDIUM)
                .owner(owner)
                .build();

        when(userRepository.findByEmailIgnoreCase(owner.getEmail())).thenReturn(Optional.of(owner));
        when(taskRepository.findByIdAndOwnerId(task.getId(), owner.getId())).thenReturn(Optional.of(task));

        taskService.delete(owner.getEmail(), task.getId());

        verify(taskRepository).delete(task);
    }

    @Test
    void shouldRejectTaskFromAnotherUser() {
        when(userRepository.findByEmailIgnoreCase(owner.getEmail())).thenReturn(Optional.of(owner));
        when(taskRepository.findByIdAndOwnerId(404L, owner.getId())).thenReturn(Optional.empty());

        TaskRequest request = new TaskRequest(
                "Tentativa",
                "Não deve atualizar",
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null
        );

        assertThatThrownBy(() -> taskService.update(owner.getEmail(), 404L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Tarefa não encontrada.");
    }
}
