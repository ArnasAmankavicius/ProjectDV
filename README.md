# ProjectDV

### Description
Text Editing application that allows of encryption and decryption of file data.
It uses [Lanterna](https://github.com/mabe02/lanterna) for terminal user interface and JavaX for cryptography.

#### How it works
Application uses JavaX Cryptography API to encrypt and decrypt data using **AES/CBC/PKCS5Padding**. For key generation it uses an instance of **PBKDF2WithHmacSHA256** with the addition of **Base64** encoding.

The current encryption and decryption algorithm is as follows:
```java
public static byte[] mess(int opmode, byte[] src) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(opmode, key, new IvParameterSpec(decode(iv.getBytes())));

            return cipher.doFinal(src);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return src;
    }
```

Calling this method for encryption:
```java
byte[] buffer = Crypto.mess(Cipher.ENCRYPT_MODE, byteArr);
byte[] encoded = Crypto.encode(buffer);
```
Calling this method for decryption:
```java
byte[] decoded = Crypto.decode(buffer);
byte[] buffer = Crypto.mess(Cipher.DECRYPT_MODE, byteArr);
```

By default, data is encrypted 16 times. This can be changed in the applications setting menu under 
**"Change encryption/decryption iteration"** menu option.
![Iteration count](https://github.com/ArnasAmankavicius/ProjectDV/blob/master/ProjectDV%20Images/Change%20iteration%20count.png)
Encryption loop:
```java
for(int i = 0; i < iterationCount; i++)
  buffer = Crypto.mess(Cipher.ENCRYPT_MODE, buffer);
byte[] encoded = Crypto.encode(buffer);
```

Decryption loop:
```java
byte[] buffer = Crypto.decode(byteArr);
for(int i = 0; i < iterationCount; i++)
  buffer = Crypto.mess(Cipher.ENCRYPT_MODE, buffer);
```
