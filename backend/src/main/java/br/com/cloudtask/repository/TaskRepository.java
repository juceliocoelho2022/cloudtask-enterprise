package br.com.cloudtask.repository;

import br.com.cloudtask.domain.Task;
import br.com.cloudtask.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    List<Task> findByOwnerIdAndStatusOrderByCreatedAtDesc(Long ownerId, TaskStatus status);
    Optional<Task> findByIdAndOwnerId(Long id, Long ownerId);
}
