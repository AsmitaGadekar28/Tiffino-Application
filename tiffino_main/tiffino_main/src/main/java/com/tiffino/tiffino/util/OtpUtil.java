package com.tiffino.tiffino.util;


import java.security.SecureRandom;



public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();

    // 6 digit OTP generate karega
    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);  // 100000 - 999999
        return String.valueOf(otp);
    }
}
