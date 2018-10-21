package ideaeclipse.secureWalletSystem;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * String utilities each method is labeled
 *
 * @author ideaeclipse
 */
class Util {
    /**
     * Converts a string of data into a hash
     *
     * @param data data
     * @return hashed data
     */
    static String createHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts key into a string
     *
     * @param key key
     * @return computable string
     */
    static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Applys ECDSAsignature based on private key
     *
     * @param privateKey private key
     * @param input      input
     * @return byte signature
     */
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output;
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            output = dsa.sign();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    /**
     * Compares generated signature to verified signature {@link Transaction#generateSignature(PrivateKey)} {@link Transaction#verifySignature()}
     *
     * @param publicKey public
     * @param data      sender, recipient and value
     * @param signature previously generated signature {@link #applyECDSASig(PrivateKey, String)}
     * @return
     */
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
