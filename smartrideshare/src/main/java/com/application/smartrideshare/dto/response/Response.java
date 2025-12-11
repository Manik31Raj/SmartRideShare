package com.application.smartrideshare.dto.response;

import com.application.smartrideshare.constatnt.Status;
import lombok.Data;

@Data
public class Response {

    private String fullname;
    private Status status;
    private String tempPassword;
}
