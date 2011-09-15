package com.cssweb.android.common;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.cssweb.android.trade.util.TradeUtil;

public class AESAgloism {
	private static final String aeskey = TradeUtil.g_pubKey;
	
    public static String getKey(String custid){ 
		StringBuilder sb = new StringBuilder(); 
		sb.append(getbylen(2));
		if (custid.length()>9){
			sb.append(String.valueOf(custid.length()));
		}else{
			sb.append("0"+String.valueOf(custid.length()));
		} 
		sb.append(getbylen(18 - custid.length())).append(custid);
		sb.append(getbylen(2));
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		sb.append(sdf.format(new Date()));
		sb.append(getbylen(2));
		return sb.toString();
	}

	public static byte[] makeAESKey(String key) {
		byte[] b = new byte[32];
		byte[] hexCode = hex2byte(key);
		for (int i = 0; i < 32; i++) {
			if (i < 26) {
				b[i] = hexCode[i];
			} else {
				b[i] = (byte) Integer.parseInt(getbylen(1));
			}
		}
		return b;
	}
	
    public static String encrypt(byte[] clearText, byte[] aesKey) throws Exception {
        // Decode Base data into a private key.
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");

        // Initialize the cipher instance for encryption with the private key.
        Cipher desCipher = null;
        String transform = "AES";
        try {
            desCipher = Cipher.getInstance(transform);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("No such algorithm (" + transform + "): " + e);
        } catch (NoSuchPaddingException e) {
            throw new Exception("No such padding (" + transform + "): " + e);
        }

        try {
            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
            throw new Exception("Invalid key", e);
        }

        // Encrypt the cleartext
        byte[] cipherText = null;
        try {
            cipherText = desCipher.doFinal(clearText);
            //return new String(Base64.encode(cipherText));
            return byte2hex(cipherText).toLowerCase();
        } catch (IllegalStateException e) {
            throw new Exception("Illegal state", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("Illegal block size", e);
        } catch (BadPaddingException e) {
            throw new Exception("Bad padding", e);
        }
   }
    
    public static String encrypt(byte[] clearText) throws Exception {
        // Decode Base data into a private key.
        SecretKeySpec secretKeySpec = new SecretKeySpec(aeskey.getBytes(), "AES");

        // Initialize the cipher instance for encryption with the private key.
        Cipher desCipher = null;
        String transform = "AES";
        try {
            desCipher = Cipher.getInstance(transform);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("No such algorithm (" + transform + "): " + e);
        } catch (NoSuchPaddingException e) {
            throw new Exception("No such padding (" + transform + "): " + e);
        }

        try {
            // Initialize the cipher for encryption
            desCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
            throw new Exception("Invalid key", e);
        }

        // Encrypt the cleartext
        byte[] cipherText = null;
        try {
            cipherText = desCipher.doFinal(clearText);
            //return new String(Base64.encode(cipherText));
            return byte2hex(cipherText).toLowerCase();
        } catch (IllegalStateException e) {
            throw new Exception("Illegal state", e);
        } catch (IllegalBlockSizeException e) {
            throw new Exception("Illegal block size", e);
        } catch (BadPaddingException e) {
            throw new Exception("Bad padding", e);
        }
   }

   public static String decrypt(byte[] cipherText, byte[] aesKey) throws Exception {
       // Decode Base data into a private key.
       SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");

       // Initialize the cipher instance for encryption with the private key.
       Cipher aesCipher = null;
       String transform = "AES";
       try {
           aesCipher = Cipher.getInstance(transform);
       } catch (NoSuchAlgorithmException e) {
           throw new Exception("No such algorithm (" + transform + "): " + e);
       } catch (NoSuchPaddingException e) {
           throw new Exception("No such padding (" + transform + "): " + e);
       }

       try {
           // Initialize the cipher for decryption
           aesCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
       } catch (InvalidKeyException e) {
           throw new Exception("Invalid key", e);
       }

       // Decrypt the ciphertext
       byte[] clearText = null;
       try {
           clearText = aesCipher.doFinal(cipherText);
       } catch (IllegalStateException e) {
           throw new Exception("Illegal state", e);
       } catch (IllegalBlockSizeException e) {
           throw new Exception("Illegal block size", e);
       } catch (BadPaddingException e) {
           throw new Exception("Bad padding", e);
       }
       return new String(clearText);
   }

   public static byte[] hex2byte(String strhex) {
		if (strhex == null) {
			return null;
		}
		int l = strhex.length();
		if (l % 2 == 1)
		{
			return null;
		}
		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; i++)
		{
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
		}
		return b;
	}

	public static String byte2hex (byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++)
		{
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
			{
				hs = hs + "0" + stmp;
			}
			else
			{
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}
	
	private static String getbylen(int len){
		String base = "1234567890";
		String temp = "";
		int i,p;
		for (i = 0; i < len; i++) {
			p = (int) (Math.random() * 10);
			temp += base.substring(p, p + 1);
		}
		return temp;
	}
    
	public static String encrypt(String strText, String key) throws Exception {
    	return AESAgloism.encrypt(strText.getBytes(), key.getBytes());
    }
	
	public static void main(String arg[]) throws Exception{
		String strkey = TradeUtil.g_pubKey;
//		String key = AESAgloism.getKey(strkey);
		String str = "1$101010042613$1300239808751";
		String aa=AESAgloism.encrypt(str.getBytes(), strkey.getBytes());
//		String aa1=AESAgloism.encrypt(str,key);
		System.out.println(aa);
//		System.out.println(aa1);
//		String bb=AESAgloism.decrypt(Base64.decode(aa.toCharArray()), key.getBytes());
//		String bb1=AESAgloism.decrypt(Base64.decode(aa1.toCharArray()), key.getBytes());
//		System.out.println(bb);
//		System.out.println(bb1);
		
//		String bb3 = AESAgloism.decrypt("29dlo*%AO+3i16BaweTw.lc!)61K{9^5".getBytes(), strkey.getBytes());
		//String bb3 = AESAgloism.decrypt(Base64.decode("125373b91736340b74d315a33dd94bbe2ed3f31f4ac0c69939e6e".toCharArray()), strkey.getBytes());
//		System.out.println(bb3);
	}
}