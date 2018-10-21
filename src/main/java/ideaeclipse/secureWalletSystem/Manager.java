package ideaeclipse.secureWalletSystem;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores the "ledger" or official transaction history
 * Each wallet stores their own sub history but this map stores data for all wallets
 * All transaction records get verified based on data in this map
 *
 * @author ideaeclipse
 */
public class Manager {
    private final Map<String, TransactionOutput> map;

    public Manager() {
        this.map = new HashMap<>();
    }

    public Map<String, TransactionOutput> getMap() {
        return map;
    }
}
