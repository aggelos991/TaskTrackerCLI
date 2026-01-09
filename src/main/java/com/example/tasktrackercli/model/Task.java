package com.example.tasktrackercli.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@Setter
public class Task {

    private final Long id;
    private String description;
    private TaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
