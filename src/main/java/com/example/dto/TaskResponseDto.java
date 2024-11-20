package com.example.dto;

import com.example.task.taskEnum.TaskStatus;
import com.example.task.taskEnum.TaskType;

import java.time.LocalDateTime;


public record TaskResponseDto(TaskType taskType,
                              String description,
                              TaskStatus taskStatus,
                              LocalDateTime executionDate) {

}
