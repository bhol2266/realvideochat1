package com.bhola.realvideochat1.ZegoCloud;

import android.os.Build;


import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class ZegoCloud_Signature {


    public static String bytesToHex(byte[] bytes) {
        StringBuffer md5str = new StringBuffer();
        //Replace each byte of the array into hexadecimal and connect it to an md5 string
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString();
    }

    // Signature=md5(AppId + SignatureNonce + ServerSecret + Timestamp)
    public static String GenerateSignature(long appId, String signatureNonce, String serverSecret, long timestamp) {
        String str = String.valueOf(appId) + signatureNonce + serverSecret + String.valueOf(timestamp);
        String signature = "";
        try {
            //Create an object that provides the information digest algorithm, initialized to the md5 algorithm object
            MessageDigest md = MessageDigest.getInstance("MD5");
            // get byte array after calculation
            byte[] bytes = md.digest(str.getBytes("utf-8"));
            //Replace each byte of the array into hexadecimal and connect it to an md5 string
            signature = bytesToHex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }


    public Map<String, Object> getSignature() {

        //Generate hexadecimal random string (16 bits)
        byte[] bytes = new byte[8];
        SecureRandom sr = null;
        //Use SecureRandom to obtain a high-strength secure random number generator
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sr = SecureRandom.getInstanceStrong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sr.nextBytes(bytes);
        String signatureNonce = bytesToHex(bytes);
        long appId = 1889863973L;       //Use your appId and serverSecret, add uppercase L or lowercase l after the number to indicate long type
        String serverSecret = "6bb439062e57ac5441158d90f36756eb";
        long timestamp = System.currentTimeMillis() / 1000L;


        Map<String,Object> data=new HashMap<>();
        data.put("signatureNonce",signatureNonce);
        data.put("timestamp",timestamp);
        data.put("signature",GenerateSignature(appId, signatureNonce, serverSecret, timestamp));

        return  data;
    }
}
