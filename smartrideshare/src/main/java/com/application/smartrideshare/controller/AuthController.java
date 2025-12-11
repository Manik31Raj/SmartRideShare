package com.application.smartrideshare.controller;


import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.dto.request.EmailRequest;
import com.application.smartrideshare.dto.request.LoginRequest;
import com.application.smartrideshare.dto.request.LogoutRequest;
import com.application.smartrideshare.dto.response.LoginResponse;
import com.application.smartrideshare.dto.request.ResetPasswordRequest;
import com.application.smartrideshare.dto.response.ResetPasswordResponse;
import com.application.smartrideshare.model.*;
import com.application.smartrideshare.repository.LogsRepository;
import com.application.smartrideshare.service.impl.AuthServiceImpl;
import com.application.smartrideshare.service.impl.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "*") // allow frontend to call it
public class AuthController {

    private final AuthServiceImpl authServiceImpl;
    private final LogsRepository logsRepository;
    private final EmailService emailService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        Role role = loginRequest.getRole();


        boolean valid = authServiceImpl.verifyCredentials(username, password, role);
        log.info("Login attempt for user: {} with role: {}", username, role);
        LoginResponse response = new LoginResponse();


        if (valid) {
            if(role == Role.ADMIN) {
                Admin admin = authServiceImpl.getAdminByUsername(username);
                response.setStatus(Status.SUCCESS);
                response.setUsername(username);
                response.setRole(role);
                response.setFullname(admin.getFullname());
                response.setFirstLogin(admin.isFirstLogin());


                if (admin.isFirstLogin()) {
                    response.setNextStep(Status.RESET_PASSWORD_REQUIRED);
                   } else{
                           if(!(authServiceImpl.checkUserLoginStatus(username) == LogsStatus.LOGIN)) {
                               authServiceImpl.recordLogActivity(username, role, LogsStatus.LOGIN);
                               log.info("Admin : {} logged in successfully.", username);
                           }
                    log.info("Admin already logged in!!");
                }
            }
            else if(role == Role.DRIVER) {
                Driver user = authServiceImpl.getDriverByUsername(username);
                response.setStatus(Status.SUCCESS);
                response.setUsername(username);
                response.setRole(role);
                response.setFullname(user.getFullname());
                response.setFirstLogin(user.isFirstLogin());

                if (user.isFirstLogin()) {
                    response.setNextStep(Status.RESET_PASSWORD_REQUIRED);
                }else{
                    if(!(authServiceImpl.checkUserLoginStatus(username) == LogsStatus.LOGIN)) {
                        authServiceImpl.recordLogActivity(username, role, LogsStatus.LOGIN);
                        log.info("Driver : {} logged in successfully.", username);
                    }
                    log.info("Driver already logged in!!");
                }

            }
            else if(role == Role.PASSENGER) {
                Passenger passenger= authServiceImpl.getPassengerByUsername(username);
                response.setStatus(Status.SUCCESS);
                response.setUsername(username);
                response.setRole(role);
                response.setFullname(passenger.getFullname());
                response.setFirstLogin(passenger.isFirstLogin());

                if (passenger.isFirstLogin()) {
                    response.setNextStep(Status.RESET_PASSWORD_REQUIRED);
                }else{
                    if(!(authServiceImpl.checkUserLoginStatus(username) == LogsStatus.LOGIN)) {
                        authServiceImpl.recordLogActivity(username, role, LogsStatus.LOGIN);
                        log.info("Passenger : {} logged in successfully.", username);
                    }
                    log.info("Passenger already logged in!!");
                }
            }

        } else{
            response.setStatus(Status.ERROR);
        }

        log.info("Response: {}", response);
        return response;
    }

    @PostMapping("/reset-password")
    public ResetPasswordResponse resetPassword(@RequestBody ResetPasswordRequest req) {
        String username = req.getUsername();
        String oldPassword = req.getOldPassword();
        String newPassword = req.getNewPassword();
        Role role = req.getRole();

        Status status = authServiceImpl.resetPassword(username,oldPassword, newPassword,role);

        ResetPasswordResponse response= new ResetPasswordResponse();
        response.setStatus(status);

        if(status == Status.PASSWORD_RESET_SUCCESS) {
            try {


                String subject = "Password Reset Successful - SmartRideShare";
                String body = "Hello, \n\nYour password has been successfully changed. " +
                        "If you did not perform this action, please contact support.";

                emailService.sendSimpleEmail(
                        username,
                        subject,
                        body
                );
                log.info("Email sent successfully!");
            } catch (Exception e) {
                log.info("Error sending email: " + e.getMessage());
            }
        }

        log.info("Response  : {} ", response);
        return response;
    }

    @PostMapping("/logout")
    public Status logout(@RequestBody LogoutRequest logoutRequest) {
        String username = logoutRequest.getUsername();
        Role role = logoutRequest.getRole();

        if(username.isEmpty()) {
            log.warn("Logout attempt with empty username");
            return Status.ERROR;
        }

        if(authServiceImpl.checkUserLoginStatus(username) == LogsStatus.LOGOUT) {
            log.info("User: {} is already logged out", username);
            return Status.USER_AlREADY_LOGGED_OUT;
        }

        authServiceImpl.recordLogActivity(username, role, LogsStatus.LOGOUT);
        log.info("Logout recorded for user: {} with role: {}", username, role);
        return Status.LOGOUT_SUCCESS;
    }
}