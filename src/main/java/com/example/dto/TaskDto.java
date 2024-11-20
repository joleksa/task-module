package com.example.dto;

import com.example.task.taskEnum.TaskType;


public record TaskDto(
         TaskType taskType,
         String description,
         Long assignedUserId,
         Long createdById,
         Integer daysToEnd
) {

}
