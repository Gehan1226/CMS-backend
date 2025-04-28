package edu.metasync.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessResponse {
    private int status;
    private String message;
}
