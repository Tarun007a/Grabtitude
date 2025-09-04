package com.project.grabtitude.helper;

import org.springframework.beans.factory.annotation.Value;

public class AppConstants {
    public static final String page = "0";
    public static final String size = "10";
    public static final String keyword = "";
    public static final String token = "";
    public static final String password = "";
    public static final String newPassword = "";
    public static final String otpResetForgetPasswordReturnMessage = "Password was changed successfully";
    public static final int maxOtoLimit = 5;
    public static final int otpSize = 6;
    @Value("${admin.verification.mail}")
    public static String adminVerificationMail;

}
