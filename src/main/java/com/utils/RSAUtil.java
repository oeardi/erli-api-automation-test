package com.utils;

import com.alibaba.fastjson2.JSONObject;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.codec.binary.Base64.*;

/**
 * RSAUtil
 *
 * @Author: 冷枫红舞
 */
public class RSAUtil {

    public static String rsaPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg/l6pugVPI5t6bX3HFlDYYGDWaatPFObl+W95mgBrwj+0Cv3hU29tYsBddfklrNi1aZoRWHsnH/HcHmcEnuuenSFYNQhdfzq/8+ujNNlujVMLSyT1G9vLbfqQ5F3cUL/KIjlzTetJZzWV4/spsS+hdg4zmIz+pSgO6wh2j6LdWYYrU/gzUwTe/8bRIv5zi8mp5xmp317gLEZ9T8FCigk6+VOJZkAK9rPga8yZpZ7U9wMncNwEU9QIUlqKVNXr+IsCoKGJqNDv5Wh+6dAbv4PhGC5WMuBi4lfhs/fs98qoaHWf92O3Tcl38LzM9dA6v7b2258v9IUkwInW3IrWWRqpwIDAQAB";

    /**
     * 给前端加密的公钥
     */
    public static String rsaPublicKeyToFront = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJIYRUrjj7xD8fk9QDXSHv4ewxnOhfSzdbfj13aBjDcB7s2uuc9kwz9hRjWgT6DQmDchIpDhcqAn8dGrlrIjGL3xqzgOQO2hKRhKsUvT3IPBKFfEfCeYEM6EQ4wDdwF8KKs+T1fe1xB5i7H1WQU/NA8PIyIhNf8UAmilpMbM+59QIDAQAB";

    public static String rsaSignPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCD+Xqm6BU8jm3ptfccWUNhgYNZpq08U5uX5b3maAGvCP7QK/eFTb21iwF11+SWs2LVpmhFYeycf8dweZwSe656dIVg1CF1/Or/z66M02W6NUwtLJPUb28tt+pDkXdxQv8oiOXNN60lnNZXj+ymxL6F2DjOYjP6lKA7rCHaPot1ZhitT+DNTBN7/xtEi/nOLyannGanfXuAsRn1PwUKKCTr5U4lmQAr2s+BrzJmlntT3Aydw3ART1AhSWopU1ev4iwKgoYmo0O/laH7p0Bu/g+EYLlYy4GLiV+Gz9+z3yqhodZ/3Y7dNyXfwvMz10Dq/tvbbny/0hSTAidbcitZZGqnAgMBAAECggEAb8CmqoTlzrRwwDF0wY2YsA1ic1zfXrERlQtWxa8WlegRFafVXgzZcsv1fZtGFpJqQ644p1/nYxIUiNqBXWlb8peOhdK7TtTPDbUIAnnTr10IV0xjFcUSqO0Osw6PzNnFhMZ/iUn3FV18ZwMZipIZHu9cEu83Qp3uHPmCZPMHVm3IRkDlrj0c++eFSL7HxfktFSTkiRZsriIYznRYXRHQJYzxQuJLvJpoV71CbzspSxQWhTTWK60tM4VEW9YgGQgzHm6Ds71y4518iZf7bAi3P4YIcPUWH0gCh8mYu7aHqa5octDWnTgKVSIEy/myYAQuRg8aNd9JiR9zNjbjPO3XcQKBgQD/4QF6x9Eqlc+lCl1C5FA9msgPo9BSS2T0shVNEmJpVlmMF5s7mdBaVNByLP3kudW24eeoEprYlOJv85uycVwkl4PHgFTzOR1hTO1Yy+4rnvAJp57rmPxVP3SSz8vwkX51OxDN9zPc4e4apI93vsQHE459e8JQxbRGnrpJCyjm+QKBgQCECXcI9SHrNv59MkL49/kWyprc8JxnJQ5V+4yzgn/J/S1czKsRNjfFkNrPgEYskyBi8uPquC3rWvUpIz/KQ1fBT092fv9jKV9a/q+fUFcydapLQK7bds96VIMtdf9wlosGXjUBhtj/41iulyOEPwjZAVQZ5/4SbwqqHakex1QmnwKBgQClGlspDStyre2okVJx6f24clnqdYniv9Epbor+CwHC9DudgFHimDHvR1Cki1Fbt2klswgV08NkQlUFZYuuDSUmWzlljfHCarUBZnCzXgqUzN4XbLfHTxRqMSKX5Eq0ND0DiZQjQgGN4Z4QRORpoIa+Biln4GYWVisnch3DKdc/iQKBgECL9/sTCt/X5JUDiyX243g213oGzg7GWLnD3UasFxiBgjmJfF9Pp/gXFkC2Lpf/3KdCohTOsPQLZSoVJ0wTm8TOpsQA1Yx+XzU2qFgziE0MpJFhL5nt/QOKaQ5CTvuVPKWCTFtQtqhxVZsUG7K8tfCP67M2bEhKrGX5VgHL4+F1AoGBAIOaqYstcc4QAA4inEWXfh7Qt1NGIh+ieupHFPZsHE2tDILyiqrcWufgpSwLkNVN5qRzIqY+TSwBrLcPIMnSVvA0hM/M4ZEZd5gRE2KNZ8oDi2iYO/gCss90fBMXKJWpF3nTCbJ6Zyz8nonvD36S29xTjE5ral6FdAXLwwpumrMY";

    public static String rsaPrivatekeyToFront = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIkhhFSuOPvEPx+T1ANdIe/h7DGc6F9LN1t+PXdoGMNwHuza65z2TDP2FGNaBPoNCYNyEikOFyoCfx0auWsiMYvfGrOA5A7aEpGEqxS9Pcg8EoV8R8J5gQzoRDjAN3AXwoqz5PV97XEHmLsfVZBT80Dw8jIiE1/xQCaKWkxsz7n1AgMBAAECgYAvMt8WOmUYmuJTHxGBGuQcablfFi+Q5RWnoxrK54RmeEfcpDkmbEusMs2vqINAAY69tFx1zwjAB93yiYk6S6mbhuKx1R3FUT7VcUhDYr7A5LaBoBQxqdkW5P37lUDEw8uIyovOlmCwTZfN0RYkSERuwdyhZm3x6jgKN9Xxczu/gQJBAMFmnlH1ON0oAVmsDwmiwxqKUI8F+yr/SccT1/79PW6SSanbjKBoNk40pMqz4LMSRNTqv2w4yn23B7pSEw7tzpUCQQC1hFFhdpxYl4xuzHGEaiqlRFvBqtnLM5ICl+iZl7DjxM5hCv85+lIoH0jnMYIRnKnTYSunRxxSP9uAweEr40XhAkBf2VvdgosMECebKYrKW8AWBzalq3EvfhYyc8M4vIkVo6qeZBDt7rf7SlvhmgRiu0shurXkDMFOTMenr8WCQJmBAkBuUnWD3ys3TSS1UtzLthslR17dXpHwxu8/VESy1VdVmBA3Ow/UlSfJ6vnMSnLbcXRmXPDDQp0cAGPQ5gWNPx0BAkACgFhMOiZgn1n/pWmv/ZSnj4Hv1CxsvD+a1J+XN/ryGqIcgWXxGtt60w4V3E9pQd/eGKKnpolv7JMDH8u1cv8c";

    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  // 用于封装随机产生的公钥与私钥

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 随机生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0, publicKeyString); // 0表示公钥
        keyMap.put(1, privateKeyString); // 1表示私钥
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        // base64编码的公钥
        byte[] decoded = decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        //Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    // https://blog.csdn.net/centralperk/article/details/8538697

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        // 64位解码加密后的字符串
        byte[] inputByte = decodeBase64(str.getBytes("UTF-8"));
        // base64编码的私钥
        byte[] decoded = decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        //Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    public static byte[] decryptBase64(String key) {
        return Base64.getDecoder().decode(key);
    }

    /**
     * BASE64 编码
     *
     * @param key 需要Base64编码的字节数组
     * @return 字符串
     */
    public static String encryptBase64(byte[] key) {
        return new String(Base64.getEncoder().encode(key));
    }

    /**
     * 公钥加密
     *
     * @param encryptingStr
     * @param publicKeyStr
     * @return
     */
    public static String encryptByPublicKey(String encryptingStr, String publicKeyStr) {
        try {
            // 将公钥由字符串转为UTF-8格式的字节数组
            byte[] publicKeyBytes = decryptBase64(publicKeyStr);
            // 获得公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            // 取得待加密数据
            byte[] data = encryptingStr.getBytes("UTF-8");
            KeyFactory factory;
            factory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = factory.generatePublic(keySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // 返回加密后由Base64编码的加密信息
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return encryptBase64(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 私钥解密
     *
     * @param encryptedStr
     * @param privateKeyStr
     * @return
     */
    public static String decryptByPrivateKey(String encryptedStr, String privateKeyStr) {
        try {
            // 对私钥解密
            byte[] privateKeyBytes = decryptBase64(privateKeyStr);
            // 获得私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            // 获得待解密数据
            byte[] data = decryptBase64(encryptedStr);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = factory.generatePrivate(keySpec);
            // 对数据解密
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            // 返回UTF-8编码的解密信息
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String bigEncryptByPublicKey(String input, String rsaPublicKey) {

        String result = "";
        try {
            // 将Base64编码后的公钥转换成PublicKey对象
            byte[] buffer = decodeBase64(rsaPublicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            // 加密
            Cipher cipher = Cipher.getInstance("RSA");
            //Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] inputArray = input.getBytes();
            int inputLength = inputArray.length;

            // 最大加密字节数，超出最大字节数需要分组加密
//            System.out.println("[调试信息] [bigEncryptByPublicKey] 加密字节数：" + inputLength);

            // 标识
            int offSet = 0;
            byte[] resultBytes = {};
            byte[] cache = {};
            while (inputLength - offSet > 0) {
                if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
                    offSet += MAX_ENCRYPT_BLOCK;
                } else {
                    cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
                    offSet = inputLength;
                }
                resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
                System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
            }
            result = new String(encodeBase64(resultBytes), "utf-8");
        } catch (Exception e) {
            System.out.println("[调试信息] [bigEncryptByPublicKey] rsaEncrypt error:" + e.getMessage());
        }

//        System.out.println("[调试信息] [bigEncryptByPublicKey] 加密的结果：" + result);
        return result;
    }

    public static String bigDecryptByPrivateKey(String text, String privateKey)
            throws Exception {

        byte[] keyBytes = decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        PrivateKey privateK = keyFactory.generatePrivate(keySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Cipher cipher = Cipher.getInstance("RSA/None/OAEPWITHSHA-256ANDMGF1PADDING");
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        //byte[] encryptedData = text.getBytes(charset);
        byte[] encryptedData = decodeBase64(text.getBytes());
        //byte[] encryptedData = cipher.doFinal(textBytes);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return new String(decryptedData);
    }

    public static void main(String[] args) throws Exception {
//        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDvhbjnjKHqYhsoPBCo71p9Hb0dUVmeGOn+AEU4UBrR3yMzdeBVMjlPt+srrVtffJnf1kQS33iUsIBsm5J6EogfuaA5fFfgahe5EJnmZHwQN5eRSd02mg1ENKC7b6y7bEu6p2HnlumLHbm3AjvsSICGXq1O1GGtd+Jt1KxLbGNWOwIDAQAB";
//        //String msg = "{\"mobile\":\"18801029695\",\"verificationCode\":\"1234\",\"loginPwd\":\"123456\",\"inviteCode\":1234}";
//        String msg ="{\"submitType\":\"presave\",\"phoneNum\":\"1234567890\",\"emailAddress\":\"123@qq.com\",\"merchantName\":\"ab cd\",\"firstName\":\"ab\",\"lastName\":\"cd\",\"birthDate\":\"2021-07-05\",\"gender\":\"Male\",\"merchantStateName\":\"Abia\",\"merchantCityName\":\"Akwete\",\"merchantAddress\":\"test addredd\",\"bvnNum\":\"1234\",\"cardIdPhoto\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"untiliyBill\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"agentCertificatePhoto\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"businessName\":\"test bn\",\"businessDecription\":\"test bd\",\"categoryName\":\"Charities,Education and Membership\",\"businessStateName\":\"Abia\",\"businessCityName\":\"Akwete\",\"businessAddress\":\"test ba\",\"businessLocalGovernmentName\":\"Bende\",\"businessStartDate\":\"2021-07-15\",\"dailyGrossSales\":\"50-100k\",\"dailyGrossProfit\":\"10-50k\",\"businessLicense\":[],\"businessPhotos\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"shopPhoto\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"status\":\"Uncommitted\"}";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTjLOI+09TsSR1rAdKMfsw5dlmj1X6C0kP6L6KnshrlCI6cLbpLjYb82hT+TjNTmjs2+32MFzeoWybOVha59OaAFp5po8Vdk+wFq8TLLK79Ijj/ykoOgnh3//FLhkG6RYiqh5UKz4UcZWb8uUAcQgioxPARKzssJxODtH8AJWqrwIDAQAB";
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJOMs4j7T1OxJHWsB0ox+zDl2WaPVfoLSQ/ovoqeyGuUIjpwtukuNhvzaFP5OM1OaOzb7fYwXN6hbJs5WFrn05oAWnmmjxV2T7AWrxMssrv0iOP/KSg6CeHf/8UuGQbpFiKqHlQrPhRxlZvy5QBxCCKjE8BErOywnE4O0fwAlaqvAgMBAAECgYBmpmp5EC2eEg1FeEsSCTeySxY2Td4IuTU5RjjZg6H3hfVSJvx5uVaXl32rfiVQYA6LSm6A6iCSGkfOrdSUWdhRpZy/Z5j7at0FhQaXVA7tIkU4wc9USHsRKPdSvhtOeczNBqmz2W7Il8wRIefbdAWI9QZU0hclq7GkE+y2IsU0IQJBAO3VLEUj+16Q4EJP5WdsDgDkgbss+ffMa16PigTnv4KG4+OnGtjq9n7sZcWphEoFVc57sVAhKtZbcylJO3/YfTkCQQCe0goOtehQIz8rl1A1PpZoaovAwY/bFtAVYo4d5TIJKt7x/6M4mB9WTAvL6VsMYVBQZ1tTNhDewqAABS+tpk8nAkBzMs4+ry8SnxoBBLIZZj1iMUylXSO2400egGhZ9+bLUosPueFHMIg08kxZccKiF/N4EHHSXj5hleoyFUa8piABAkEAkiOTPZYE/jhwmBvToirFG69uiR+Sz3ZeYk8dlkSfle/s1aD0/856h13SPP+s1+dzj4iLcDX/r/pGX75l8NNnYQJBALRhNxZYv6Ttgkx8VnGGlrtK3QGqwXushGysAvucM0jJb33OZi4xCsHVFJ2/gQp1MnTOKHYCVbZwWhCiPGAz40w=";
//        //System.out.println(bigDecryptByPrivateKey(msg, publicKey));
//       String str = "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";
//        //String str = bigEncryptByPublicKey(msg, publicKey);
//        str = "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";
//        System.out.println(bigDecryptByPrivateKey(str, privateKey));
//		genKeyPair();

        String test = "{\"id\":14,\"applicationId\":\"2107180044011923\",\"aggregatorId\":\"256620052224002\",\"merchantId\":null,\"phoneNum\":\"+2341234567890\",\"emailAddress\":\"xxqrwerwq@qq.com\",\"firstName\":\"1ab2\",\"lastName\":\"3cd4\",\"merchantName\":\"1ab2 3cd4\",\"birthDate\":\"2021-07-06\",\"gender\":\"Male\",\"merchantStateId\":null,\"merchantStateName\":\"Abia\",\"merchantCityId\":null,\"merchantCityName\":\"Akwete\",\"merchantAddress\":\"test addredd\",\"bvnNum\":\"1234\",\"occupation\":\"\",\"cardIdPhoto\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"untiliyBill\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"agentCertificatePhoto\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"businessName\":\"test bn\",\"businessDecription\":\"test bd\",\"categoryId\":null,\"categoryName\":\"Charities,Education and Membership\",\"secondCategoryId\":null,\"secondCategoryName\":null,\"businessStateId\":null,\"businessStateName\":\"Abia\",\"businessCityId\":null,\"businessCityName\":\"Akwete\",\"businessAddress\":\"test ba\",\"businessLocalGovernmentId\":null,\"businessLocalGovernmentName\":\"Bende\",\"businessWebsite\":null,\"businessHours\":null,\"businessStartDate\":\"2021-07-15\",\"dailyGrossSales\":\"50-100k\",\"dailyGrossProfit\":\"10-50k\",\"businessLicense\":null,\"businessPhotos\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\",\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210719/u833.png\"],\"shopPhoto\":[\"https://owallet-test-public-referer.oss-cn-hongkong.aliyuncs.com/files/merchant/network/20210714/警示_2.png\"],\"status\":\"Uncommitted\",\"createTime\":1626620290469,\"updateTime\":1626663795112}";
        //test="hello";

        String data = "";
        String secText = RSAUtil.bigEncryptByPublicKey(test, publicKey);
        //data="V+bUgA9RhNYaloKcKCAcmo/0+W06GFkBYW1NawkeCxc92bzhsfnscbo1EJlLPJPQC1c4DoFQq1EKq52htwSGU3Pzv1dkh/yExYts093Da/vQsjr7N5EGKFN9wgfLfiRF9S7q/A/9ZHf4d/HIduLdDv9BAnGOGtDGqFftaiK45V8=";
        secText = RSAUtil.bigDecryptByPrivateKey(secText, privateKey);
        System.out.println("[调试信息] [main] 解密：" + secText);
        System.out.println("[调试信息] [main] =====================================================================");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNum", 1);
        String encryptText = RSAUtil.bigEncryptByPublicKey(jsonObject.toJSONString(), rsaPublicKey);
        System.out.println("[调试信息] [main]：" + encryptText);
    }

}
