package com.example.task;

import com.example.BusinessException;
import com.example.dto.AppUserDto;
import com.example.dto.AssignTaskDto;
import com.example.dto.TaskDto;
import com.example.dto.TaskResponseDto;
import com.example.task.taskEnum.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserClient userClient;
    private final Clock clock;

    public TaskService(TaskRepository taskRepository, Clock clock, UserClient userClient) {
        this.taskRepository = taskRepository;
        this.userClient = userClient;
        this.clock = clock;
    }


    public TaskResponseDto createTask(TaskDto taskDto) {
        AppUserDto assignedUserDto = userClient.findUserById(taskDto.assignedUserId()).getBody();
        AppUserDto createdByUserDto = userClient.findUserById(taskDto.createdById()).getBody();

        Long assignedUserId = assignedUserDto.id();
        Long createdByUserId = createdByUserDto.id();

        Task task = createNewTask(taskDto, assignedUserId, createdByUserId);
        return getResponseDto(taskRepository.save(task));
    }

    private Task createNewTask(TaskDto taskDto, Long assignedUserId, Long createdByUserId) {
        Integer daysToEnd = taskDto.daysToEnd();
        Task task = Task.builder()
                .taskType(taskDto.taskType())
                .description(taskDto.description())
                .taskStatus(TaskStatus.NEW)
                .assignedUser(assignedUserId)
                .createdBy(createdByUserId)
                .creationDate(LocalDateTime.now(clock))
                .modificationDate(LocalDateTime.now(clock))
                .build();
        task.setOptionalExecutionDate(daysToEnd,clock);
        return task;
    }

    public Task findTaskById(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isEmpty()) {
            throw new BusinessException("Task doesn't exist");
        }
        return taskOptional.get();
    }

    public TaskResponseDto closeTask(Long id) {
        Task task = findTaskById(id);
        if (task.getTaskStatus() == TaskStatus.CLOSED) {
            throw new BusinessException("Task is already closed");
        }
        task.setTaskStatus(TaskStatus.CLOSED);
        return getResponseDto(saveModificatedTask(task));
    }

    public TaskResponseDto inProgressTask(Long id) {
        Task task = findTaskById(id);
        if (task.getTaskStatus() == TaskStatus.IN_PROGRESS) {
            throw new BusinessException("Task is already in progress");
        }
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        return getResponseDto(saveModificatedTask(task));
    }

    public TaskResponseDto cancelTask(Long id) {
        Task task = findTaskById(id);
        if (task.getTaskStatus() == TaskStatus.CANCELLED) {
            throw new BusinessException("Task is already cancelled");
        }
        task.setTaskStatus(TaskStatus.CANCELLED);
        return getResponseDto(saveModificatedTask(task));
    }

    public TaskResponseDto assignTask(AssignTaskDto assignTaskDto) {
        Task task = findTaskById(assignTaskDto.taskId());
        AppUserDto assignedUser = userClient.findUserById(assignTaskDto.userId()).getBody();
        task.assignTo(task, assignedUser);
        return getResponseDto(saveModificatedTask(task));
    }

    TaskResponseDto getResponseDto(Task task) {
        return new TaskResponseDto(task.getTaskType(),
                task.getDescription(), task.getTaskStatus(), task.getExecutionDate());
    }

    private Task saveModificatedTask(Task task) {
        task.setModificationDate(LocalDateTime.now(clock));
        return taskRepository.save(task);
    }
}
