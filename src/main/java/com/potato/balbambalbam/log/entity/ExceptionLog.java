package com.potato.balbambalbam.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "exception_log")
public class ExceptionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exception_log_id")
    private Long exceptionLogId;

    @Column(name = "exception_info_id")
    private Long exceptionInfoId;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "class_name")
    private String className;

    @Column(name = "request_path")
    private String requestPath;
}
