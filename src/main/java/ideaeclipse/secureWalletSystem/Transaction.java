package ideaeclipse.secureWalletSystem;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedList;
import java.util.List;

/**
 * This class takes information and processes it into a transaction
 * This class also has security measures in place to verify the signature of the transaction. I.e no tampering
 *
 * @author ideaeclipse
 */
public class Transaction {
    private final PublicKey from, to;
    private final Float value;
    private final List<TransactionOutput> inputtedRecords;
    private final List<TransactionOutput> outputs;
    private final Manager manager;
    private byte[] signature;
    private String id;
    private int sequence = 0;

    /**
     * @param from      who is sending the money
     * @param to        who is getting the money
     * @param value     amount of money
     * @param inputList wallets transaction history, first transaction is null
     * @param manager   ledger manager
     */
    public Transaction(final PublicKey from, final PublicKey to, final Float value, final List<TransactionOutput> inputList, final Manager manager) {
        this.from = from;
        this.to = to;
        this.value = value;
        if (inputList != null)
            this.inputtedRecords = inputList;
        else
            this.inputtedRecords = new LinkedList<>();
        this.outputs = new LinkedList<>();
        this.manager = manager;
    }

    /**
     * This is the main logic method
     * first the signature is verified to ensure all data is the same at time of processing vs time of creation
     * Transaction record is checked in comparison to wallet
     * A positive value transaction is created for the getter
     * A negative value transaction is created for the sender
     * both transactions along with their new hashed ids are added to the ledger
     *
     * @return status of the transaction
     */
    public boolean processTransaction() {
        if (verifySignature()) {
            if (manager.getMap().values().containsAll(inputtedRecords)) {
                id = calulateHash();
                outputs.add(new TransactionOutput(this.to, value, id));
                outputs.add(new TransactionOutput(this.from, -value, id));
                for (TransactionOutput o : this.outputs) {
                    manager.getMap().put(o.getParentId(), o);
                }
            }
        }
        return true;
    }

    /**
     * At a pon of creating of the transaction a unique signature is generated
     *
     * @param privateKey senders private key
     * @return transaction object
     */
    public Transaction generateSignature(PrivateKey privateKey) {
        String data = Util.getStringFromKey(from) + Util.getStringFromKey(to) + Float.toString(value);
        signature = Util.applyECDSASig(privateKey, data);
        return this;
    }

    /**
     * At time of processing the signature gets verified with the information that will be added to the ledger
     * This insures validity and that no data inside the transaction has been tampered with in-between the time of creating and the time of processing
     *
     * @return whether the signature is the same
     */
    public boolean verifySignature() {
        String data = Util.getStringFromKey(from) + Util.getStringFromKey(to) + Float.toString(value);
        return Util.verifyECDSASig(from, data, signature);
    }

    /**
     * Hashes a new transaction id based on multiple factors
     * Sender,getter,value,nonce(sequence), random number
     * The reason for a random number is there was a error in the ledger that if two wallets performed identical transactions only the first would be registered
     *
     * @return hashed Transaction id
     */
    private String calulateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return Util.createHash(
                Util.getStringFromKey(this.from) +
                        Util.getStringFromKey(this.to) +
                        Float.toString(value) +
                        Integer.toString(sequence)
                        + (Math.random() * 100)
        );
    }
}
