package testing;

import ideaeclipse.secureWalletSystem.*;

import java.security.Security;

public class Main {
    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Manager manager = new Manager();
        Wallet coinBase = new Wallet(manager);
        Wallet walletA = new Wallet(manager);
        Wallet walletB = new Wallet(manager);
        Transaction startingTransaction = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), 100f, null, manager);
        startingTransaction.generateSignature(coinBase.getPrivateKey());
        startingTransaction.processTransaction();
        System.out.println("Adding $100 to walletA");
        System.out.println("coinBase: " + coinBase.getBalance());
        System.out.println("A: " + walletA.getBalance());
        System.out.println("B: " + walletB.getBalance());
        System.out.println("A -> B:10");
        walletA.sendFunds(walletB.getPublicKey(), 10f).processTransaction();
        System.out.println("A: " + walletA.getBalance());
        System.out.println("B: " + walletB.getBalance());
        System.out.println("B -> A:5");
        walletB.sendFunds(walletA.getPublicKey(), 5f).processTransaction();
        System.out.println("A: " + walletA.getBalance());
        System.out.println("B: " + walletB.getBalance());
        System.out.println("A -> B:1000");
        Transaction overDraw = walletA.sendFunds(walletB.getPublicKey(), 1000f);
        if (overDraw != null)
            overDraw.processTransaction();
        System.out.println("A: " + walletA.getBalance());
        System.out.println("B: " + walletB.getBalance());

        walletA.printTransactionHistory();
        System.out.println();
        walletB.printTransactionHistory();
    }
}
