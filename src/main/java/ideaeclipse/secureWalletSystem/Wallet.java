package ideaeclipse.secureWalletSystem;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * All wallet information is stored here
 * Every wallet has a public and private key to signature transactions
 *
 * @author ideaeclipse
 */
public class Wallet {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private final Manager manager;

    private Map<String, TransactionOutput> transactionRecord;

    public Wallet(final Manager manager) {
        this.manager = manager;
        this.transactionRecord = new HashMap<>();
        generateKeyPair();
    }

    /**
     * Generates keypairs for each wallet
     */
    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds up the total of all transactions from the managers transaction history
     *
     * @return current balance
     */
    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item : manager.getMap().entrySet()) {
            if (item.getValue().getReciepent().equals(this.publicKey)) {
                transactionRecord.put(item.getValue().getParentId(), item.getValue());
                total += item.getValue().getValue();
            }
        }
        return total;
    }

    /**
     * Creates a transaction for you to send money to another wallet
     *
     * @param to    other wallet
     * @param value amount
     * @return proposed transaction
     */
    public Transaction sendFunds(final PublicKey to, final Float value) {
        if (getBalance() < value) {
            return null;
        }
        List<TransactionOutput> inputList = new LinkedList<>();
        float total = 0;
        for (Map.Entry<String, TransactionOutput> entry : this.transactionRecord.entrySet()) {
            total += entry.getValue().getValue();
            inputList.add(entry.getValue());
            if (total > value)
                break;
        }

        return new Transaction(this.publicKey, to, value, inputList, this.manager).generateSignature(this.privateKey);
    }

    /**
     * Prints out wallets transaction history to console
     *
     * @see TransactionOutput#toString()
     */
    public void printTransactionHistory() {
        getBalance();
        for (Map.Entry<String, TransactionOutput> o : this.transactionRecord.entrySet()) {
            System.out.println(o);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
