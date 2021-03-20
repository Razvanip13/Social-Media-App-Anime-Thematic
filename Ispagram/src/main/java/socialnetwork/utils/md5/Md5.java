package socialnetwork.utils.md5;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.DigestUtils;

public class Md5 {
    public static void main(String[] args) {
        String password = "";
        System.out.println("MD5 in hex: " + md5(password));
    }

    public static String md5(String password) {
        String md5 = null;
        if(null == password) return null;

        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");


            digest.update(password.getBytes(), 0, password.length());


            md5 = new BigInteger(1, digest.digest()).toString(16);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }
}
