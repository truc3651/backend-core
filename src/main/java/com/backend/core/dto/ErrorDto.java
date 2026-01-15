package com.backend.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private String message;
    private String path;
    private HttpStatus status;
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public ErrorDto(String message, String path, HttpStatus status) {
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = OffsetDateTime.now();
    }
}
