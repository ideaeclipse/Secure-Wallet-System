package ideaeclipse.secureWalletSystem;

import java.security.PublicKey;

/**
 * Core of every transaction has to transaction Outputs
 * <p>
 * This stores who is affected, the value and a hashed Id to be stored in a transaction record map
 *
 * @author ideaeclipse
 */
public class TransactionOutput {
    private final PublicKey reciepent;
    private final float value;
    private final String parentId;

    public TransactionOutput(final PublicKey reciepent, final float value, final String parentId) {
        this.reciepent = reciepent;
        this.value = value;
        this.parentId = Util.createHash(Util.getStringFromKey(reciepent) + Float.toString(value) + parentId);
    }

    public PublicKey getReciepent() {
        return reciepent;
    }

    public float getValue() {
        return value;
    }

    public String getParentId() {
        return parentId;
    }

    /**
     * @return all data in a readable format
     */
    @Override
    public String toString() {
        return "Recipient: " + Util.getStringFromKey(this.reciepent) + " Value: " + this.value + " Id: " + this.parentId;
    }
}
