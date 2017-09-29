/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.returntrue.util.encrypt;

import java.security.SecureRandom;

/**
 *
 * @author JosephWang
 */
public class Random {

    /**
     * 隨機英文字母
     *
     * @param stringLength 字串長度
     * @return 為A~Z以及a~z組成的字串
     */
    public static String randomAlphabet(int stringLength) {
    	SecureRandom generator = new SecureRandom();
        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(max);
        char tempChar;
        for (int i = 0; i < stringLength; i++) {
            // 65~90、97~122
            tempChar = (char) (generator.nextInt(2) == 0 ? (generator.nextInt(26) + 65) : (generator.nextInt(26) + 97));
//            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
