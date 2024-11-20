package com.example.task;


import com.example.BusinessException;
import com.example.appUser.AppUser;
import com.example.dto.AppUserDto;
import com.example.task.taskEnum.TaskStatus;
import com.example.task.taskEnum.TaskType;
//import com.example.user.AppUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.Clock;
import java.time.LocalDateTime;


@Entity
@Table(name = "TASKS")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TaskType taskType;
    private String description;
    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus;
    private Long createdBy;
    private Long assignedUser;
    private LocalDateTime creationDate;
    private LocalDateTime modificationDate;
    private LocalDateTime executionDate;

    public void setOptionalExecutionDate(Integer daysToEnd, Clock clock) {
        if (daysToEnd != null) {
            this.executionDate = LocalDateTime.now(clock).plusDays(daysToEnd);
        }
    }

    public void assignTo(Task task, AppUserDto assignedUser) {
        if (task.getTaskStatus() != TaskStatus.NEW) {
            throw new BusinessException("Task must be in NEW status");
        }
        if (assignedUser.id() == task.getAssignedUser()) {
            throw new BusinessException("Task is already assigned to this user");
        }
        this.assignedUser = assignedUser.id();
    }
}
