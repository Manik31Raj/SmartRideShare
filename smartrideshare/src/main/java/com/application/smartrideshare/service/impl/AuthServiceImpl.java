package com.application.smartrideshare.service.impl;

import com.application.smartrideshare.constatnt.Status;
import com.application.smartrideshare.model.*;
import com.application.smartrideshare.repository.AdminRepository;
import com.application.smartrideshare.repository.DriverRepository;
import com.application.smartrideshare.repository.LogsRepository;
import com.application.smartrideshare.repository.PassengerRepository;
import com.application.smartrideshare.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final LogsRepository logsRepository;
    private final EmailService emailService;


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";
    private static final int PASSWORD_LENGTH = 8;


    public String generateTempPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public void recordLogActivity(String username, Role role, LogsStatus logStatus) {
        logsRepository.save(Logs.builder()
                .username(username)
                .role(role)
                .statusOfAccount(logStatus)
                .build());
    }

    public LogsStatus checkUserLoginStatus(String username) {
        Optional<Logs> optionalActivity = logsRepository.findTopByUsernameOrderByTimestampDesc(username);

        if (optionalActivity.isPresent()) {
            Logs activity = optionalActivity.get();
            return activity.getStatusOfAccount();
        } else {
            return LogsStatus.NO_ACTIVITY;
        }
    }


    public boolean verifyCredentials(String username, String password,Role role) {
        if(role == Role.DRIVER) {
            return driverRepository.findByUsername(username)
                    .map(user -> user.getPassword().equals(password) && user.getRole() == role)
                    .orElse(false);
        } else if(role == Role.PASSENGER) {
            return passengerRepository.findByUsername(username)
                    .map(user -> user.getPassword().equals(password) && user.getRole() == role)
                    .orElse(false);
        }
        return adminRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password) && user.getRole() == role)
                .orElse(false);
    }


    public Admin getAdminByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }

    public Driver getDriverByUsername(String username) {
        return driverRepository.findByUsername(username).orElse(null);
    }

    public Passenger getPassengerByUsername(String username) {
        return passengerRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public Status resetPassword(String username, String oldPassword, String newPassword, Role role) {

        if(role.equals(Role.ADMIN)){
            return (Status) adminRepository.findByUsername(username)
                    .map(user -> {

                        if (!user.getPassword().equals(oldPassword)) {
                            return Status.PASSWORD_MISMATCH; // ❌ old password does not match
                        }

                        if(oldPassword.equals(newPassword)) {
                            return Status.PASSWORD_SAME_AS_OLD; // ❌ new password is same as old password
                        }

                        user.setPassword(newPassword);
                        user.setFirstLogin(false); // ✅ password changed, mark first login as done
                        adminRepository.save(user);
                        return Status.PASSWORD_RESET_SUCCESS;
                    })
                    .orElse(Status.USER_NOT_FOUND);
        }
        if(role.equals(Role.DRIVER))
        {
            return (Status) driverRepository.findByUsername(username)
                    .map(user -> {

                        if (!user.getPassword().equals(oldPassword)) {
                            return Status.PASSWORD_MISMATCH; // ❌ old password does not match
                        }

                        if(oldPassword.equals(newPassword)) {
                            return Status.PASSWORD_SAME_AS_OLD; // ❌ new password is same as old password
                        }

                        user.setPassword(newPassword);
                        user.setFirstLogin(false); // ✅ password changed, mark first login as done
                        driverRepository.save(user);
                        return Status.PASSWORD_RESET_SUCCESS;
                    })
                    .orElse(Status.USER_NOT_FOUND);
        }
        return (Status) passengerRepository.findByUsername(username)
                .map(user -> {

                    if (!user.getPassword().equals(oldPassword)) {
                        return Status.PASSWORD_MISMATCH; // ❌ old password does not match
                    }

                    if(oldPassword.equals(newPassword)) {
                        return Status.PASSWORD_SAME_AS_OLD; // ❌ new password is same as old password
                    }

                    user.setPassword(newPassword);
                    user.setFirstLogin(false); // ✅ password changed, mark first login as done
                    passengerRepository.save(user);
                    return Status.PASSWORD_RESET_SUCCESS;
                })
                .orElse(Status.USER_NOT_FOUND);
    }

    public String AddAdmin(String username, String fullname, Role role, String department, int age, String contactNumber, boolean isFirstLogin) {
        if (adminRepository.findByUsername(username).isPresent()) {
            return "User already exists"; // User already exists
        }

        String tempPassword = generateTempPassword();
        log.info("Generated temporary password for fullname :{} , tempPassword : {} ",fullname, tempPassword);


        Admin admin = Admin.builder()
                .username(username)
                .password(tempPassword)
                .role(role)
                .age(age)
                .department(department)
                .contactNumber(contactNumber)
                .fullname(fullname)
                .isFirstLogin(isFirstLogin)
                .createdAt(Instant.now())
                .build();
        adminRepository.save(admin);

        try {


            String subject = "Temporary Password Notification";
            String body = "Hello " + fullname + ",\n\n" + "Your account has been created successfully. Here is your temporary password: " + tempPassword + "\n\n" +
                    "Please log in and change your password at your earliest convenience.\n\n" +
                    "Best regards,\n" +
                    "SmartRideShare Team";

            emailService.sendSimpleEmail(
                    username,
                    subject,
                    body
            );
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.info("Error sending email: " + e.getMessage());
        }

        return tempPassword;
    }

    public String AddDriver(String username, String fullname, Role role, String department, int age, String contactNumber, String licenseNumber, String vehicleModel, String vehiclePlate, boolean isFirstLogin) {
        if (driverRepository.findByUsername(username).isPresent()) {
            return "User already exists"; // User already exists
        }

        String tempPassword = generateTempPassword();
        log.info("Generated temporary password for fullname :{} , tempPassword : {} ",fullname, tempPassword);


        Driver driver = Driver.builder()
                .username(username)
                .password(tempPassword)
                .role(role)
                .age(age)
                .licenseNumber(licenseNumber)
                .vehicleModel(vehicleModel)
                .vehiclePlate(vehiclePlate)
                .contactNumber(contactNumber)
                .fullname(fullname)
                .isFirstLogin(isFirstLogin)
                .createdAt(Instant.now())
                .build();
        driverRepository.save(driver);


        try {


            String subject = "Temporary Password Notification";
            String body = "Hello " + fullname + ",\n\n" + "Your account has been created successfully. Here is your temporary password: " + tempPassword + "\n\n" +
                    "Please log in and change your password at your earliest convenience.\n\n" +
                    "Best regards,\n" +
                    "SmartRideShare Team";

            emailService.sendSimpleEmail(
                    username,
                    subject,
                    body
            );
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.info("Error sending email: " + e.getMessage());
        }

        return tempPassword;
    }

    public String AddPassenger(String username, String fullname, Role role, int age, String contactNumber, boolean isFirstLogin) {
        if (passengerRepository.findByUsername(username).isPresent()) {
            return "User already exists"; // User already exists
        }

        String tempPassword = generateTempPassword();
        log.info("Generated temporary password for fullname :{} , tempPassword : {} ",fullname, tempPassword);


        Passenger passenger = Passenger.builder()
                .username(username)
                .password(tempPassword)
                .role(role)
                .age(age)
                .contactNumber(contactNumber)
                .fullname(fullname)
                .isFirstLogin(isFirstLogin)
                .createdAt(Instant.now())
                .build();
        passengerRepository.save(passenger);

        try {
            String subject = "Temporary Password Notification";
            String body = "Hello " + fullname + ",\n\n" + "Your account has been created successfully. Here is your temporary password: " + tempPassword + "\n\n" +
                    "Please log in and change your password at your earliest convenience.\n\n" +
                    "Best regards,\n" +
                    "SmartRideShare Team";

            emailService.sendSimpleEmail(
                    username,
                    subject,
                    body
            );
            log.info("Email sent successfully!");
        } catch (Exception e) {
            log.info("Error sending email: " + e.getMessage());
        }

        return tempPassword;
    }

    public boolean deleteAdminById(Long id) {
        if (id == null) return false;
        if (!adminRepository.existsById(id)) return false;
        adminRepository.deleteById(id);
        return true;
    }

    public boolean deleteDriverById(Long id) {
        if (id == null) return false;
        if (!driverRepository.existsById(id)) return false;
        driverRepository.deleteById(id);
        return true;
    }

    public boolean deletePassengerById(Long id) {
        if (id == null) return false;
        if (!passengerRepository.existsById(id)) return false;
        passengerRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public List<Passenger> getAllPassengers() {
        return passengerRepository.findAll();
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }




}
