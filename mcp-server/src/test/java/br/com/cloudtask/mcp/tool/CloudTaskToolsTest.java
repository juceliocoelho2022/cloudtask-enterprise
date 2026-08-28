package br.com.cloudtask.mcp.tool;

import br.com.cloudtask.mcp.client.CloudTaskApiClient;
import br.com.cloudtask.mcp.model.TaskCommand;
import br.com.cloudtask.mcp.model.TaskPriority;
import br.com.cloudtask.mcp.model.TaskStatus;
import br.com.cloudtask.mcp.model.TaskView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudTaskToolsTest {

    @Mock
    private CloudTaskApiClient apiClient;

    @InjectMocks
    private CloudTaskTools tools;

    @Test
    void shouldListTasksWithoutStatusFilter() {
        when(apiClient.listTasks(null)).thenReturn(List.of());

        assertThat(tools.listTasks(null)).isEmpty();
        verify(apiClient).listTasks(null);
    }

    @Test
    void shouldCreateTaskWithDefaults() {
        TaskView created = new TaskView(
                1L,
                "Prepare release",
                null,
                TaskStatus.TODO,
                TaskPriority.MEDIUM,
                null,
                null,
                null
        );
        when(apiClient.createTask(any(TaskCommand.class))).thenReturn(created);

        TaskView result = tools.createTask("Prepare release", null, null, null, null);

        ArgumentCaptor<TaskCommand> captor = ArgumentCaptor.forClass(TaskCommand.class);
        verify(apiClient).createTask(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(TaskStatus.TODO);
        assertThat(captor.getValue().priority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(result).isEqualTo(created);
    }

    @Test
    void shouldNormalizePortuguesePriorityAndLongDate() {
        TaskView created = new TaskView(
                2L,
                "Finalizar documentação do CloudTask",
                null,
                TaskStatus.TODO,
                TaskPriority.HIGH,
                LocalDate.parse("2026-08-30"),
                null,
                null
        );
        when(apiClient.createTask(any(TaskCommand.class))).thenReturn(created);

        tools.createTask(
                "Finalizar documentação do CloudTask",
                null,
                "a fazer",
                "alta",
                "30 de agosto de 2026"
        );

        ArgumentCaptor<TaskCommand> captor = ArgumentCaptor.forClass(TaskCommand.class);
        verify(apiClient).createTask(captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(TaskStatus.TODO);
        assertThat(captor.getValue().priority()).isEqualTo(TaskPriority.HIGH);
        assertThat(captor.getValue().dueDate()).isEqualTo(LocalDate.parse("2026-08-30"));
    }

    @Test
    void shouldUpdateOnlyTaskStatus() {
        TaskView current = new TaskView(
                7L,
                "Document MCP",
                "Keep the description",
                TaskStatus.TODO,
                TaskPriority.HIGH,
                LocalDate.parse("2026-09-01"),
                null,
                null
        );
        when(apiClient.findTask(7L)).thenReturn(current);
        when(apiClient.updateTask(any(), any(TaskCommand.class))).thenReturn(current);

        tools.updateTaskStatus(7L, "concluída");

        ArgumentCaptor<TaskCommand> captor = ArgumentCaptor.forClass(TaskCommand.class);
        verify(apiClient).updateTask(org.mockito.ArgumentMatchers.eq(7L), captor.capture());
        assertThat(captor.getValue().status()).isEqualTo(TaskStatus.DONE);
        assertThat(captor.getValue().title()).isEqualTo(current.title());
        assertThat(captor.getValue().priority()).isEqualTo(current.priority());
        assertThat(captor.getValue().dueDate()).isEqualTo(current.dueDate());
    }

    @Test
    void shouldRejectInvalidStatus() {
        assertThatThrownBy(() -> tools.listTasks("blocked"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status");
    }
}
