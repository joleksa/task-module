package com.example.task;

import com.example.dto.AssignTaskDto;
import com.example.dto.TaskDto;
import com.example.dto.TaskResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/task-management")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority({'admin:create', 'editor:create'})")
    ResponseEntity<TaskResponseDto> createNewTask(@RequestBody TaskDto taskDto) {
        return ResponseEntity
                .ok()
                .body(taskService.createTask(taskDto));
    }

    @PatchMapping("/close")
    @PreAuthorize("hasAnyAuthority({'admin:update', 'editor:update', 'user:update'})")
    ResponseEntity<TaskResponseDto> closeTask(@RequestParam Long id) {
        return ResponseEntity
                .ok()
                .body(taskService.closeTask(id));
    }

    @PatchMapping("/in-progress")
    @PreAuthorize("hasAnyAuthority({'admin:update', 'editor:update', 'user:update'})")
    ResponseEntity<TaskResponseDto> inProgressTask(@RequestParam Long id) {
        TaskResponseDto body = taskService.inProgressTask(id);
        return ResponseEntity
                .ok()
                .body(body);
    }

    @PatchMapping("/cancel")
    @PreAuthorize("hasAnyAuthority({'admin:update', 'editor:update'})")
    ResponseEntity<TaskResponseDto> cancelTask(@RequestParam Long id) {
        TaskResponseDto responseBody = taskService.cancelTask(id);
        return ResponseEntity
                .ok()
                .body(responseBody);
    }
    @PatchMapping("/assign-task")
    @PreAuthorize("hasAnyAuthority({'admin:update', 'editor:update'})")
    ResponseEntity<TaskResponseDto> assignTask(@RequestBody AssignTaskDto assignTaskDto) {
        TaskResponseDto body = taskService.assignTask(assignTaskDto);
        return ResponseEntity
                .ok()
                .body(body);
    }

}
