package com.example.task;

import com.example.dto.TaskFilterDto;
import com.example.dto.TaskResponseDto;
import com.example.task.taskEnum.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class TaskFilterService {

    private final TaskRepository taskRepository;
    private final TaskService taskService;
    private final int expirationDaysWarning;
    private final Clock clock;

    @Autowired
    public TaskFilterService(TaskRepository taskRepository,
                             TaskService taskService,
                             @Value("${task.expiration-warning-days}")
                       int expirationDaysWarning, Clock clock) {
        this.taskRepository = taskRepository;
        this.taskService = taskService;
        this.expirationDaysWarning = expirationDaysWarning;
        this.clock = clock;
    }

    public Page<TaskResponseDto> getAllTasksSortedAndPaginated(int pageNo, int pageSize, String field, String direction) {
        Page<Task> tasks = getPageableTasks(pageNo, pageSize, field, direction);
        List<TaskResponseDto> listTaskResponseDto =  tasks.getContent().stream()
                .map(taskService::getResponseDto).toList();

        return new PageImpl<>(listTaskResponseDto, tasks.getPageable(), tasks.getTotalElements());
    }

    private Page<Task> getPageableTasks(int pageNo, int pageSize,
                                        String field, String direction) {
        Pageable pageable = PageRequest.of(pageNo, pageSize)
                .withSort(Sort.Direction.fromString(direction), field);
        return taskRepository.findAll(pageable);
    }

    public List<TaskResponseDto> getWarnedTasks() {
        LocalDateTime warningDate = LocalDateTime.now(clock)
                .plusDays(expirationDaysWarning);
        return taskRepository.findWarnedTasks(warningDate).stream()
                .map(taskService::getResponseDto)
                .toList();
    }

    public List<TaskResponseDto> getExpiredTasks() {//searching by query
        LocalDateTime expiredDate = LocalDateTime.now(clock);
        return taskRepository.findExpiredTasks(expiredDate).stream()
                .map(taskService::getResponseDto)
                .toList();
    }

    public List<TaskResponseDto> getAssignedTasksToUser(Long userId) {//searching by query
        List<Task> assignedUser = taskRepository.findByAssignedUser(userId);
        return assignedUser.stream()
                .map(taskService::getResponseDto)
                .toList();
    }

    public List<TaskResponseDto> getCreatedTasksByUser(Long id) {//searching by query
        List<Task> createdBy = taskRepository.findByCreatedBy(id);
        return createdBy.stream()
                .map(taskService::getResponseDto)
                .toList();
    }

    public List<TaskResponseDto> getTaskByStatus(String status) {//searching by query
        return taskRepository.findListByTaskStatus(TaskStatus.valueOf(status)).stream()
                .map(taskService::getResponseDto)
                .toList();
    }

    public List<TaskResponseDto> getTaskByType(TaskFilterDto filterDto) {//searching by filter
        return taskRepository.findAll().stream()
                .filter(task -> task.getTaskType() == filterDto.taskType())
                .map(taskService::getResponseDto)
                .toList();
    }

    public List<TaskResponseDto> getTaskByCreationDateRange(TaskFilterDto filterDto) {//searching by filter
        return taskRepository.findAll().stream()
                .filter(task -> task.getCreationDate().isAfter(filterDto.startDate()) &&
                        task.getCreationDate().isBefore(filterDto.endDate()))
                .map(taskService::getResponseDto)
                .toList();
    }

    public List<TaskResponseDto> getTaskByExecutionDateRange(TaskFilterDto filterDto) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getExecutionDate() != null &&
                        task.getExecutionDate().isAfter(filterDto.startDate())
                        && task.getExecutionDate().isBefore(filterDto.endDate()))
                .map(taskService::getResponseDto)
                .toList();
    }
}
