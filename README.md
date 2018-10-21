# Secure-Wallet-System
The purpose of this project was to learn about the values of hashing and writing code that verifies information before execution of a specific task

## How to use
* This project allows you to create wallet objects that can transfer funds to one another securely, all data is hashed and stored in a centralized object which every wallet instance that is created will have access to
* In order to create a wallet you must first make a manager
```java
 Manager manager = new Manager();
```
* Nothing else is required besides instantiating the object, this will initialize the map inside it and allow the wallets to access the centralized transaction data
* After creating a manager you have to create a wallet, in this case we will create three
```java
Wallet coinBase = new Wallet(manager);
Wallet walletA = new Wallet(manager);
Wallet walletB = new Wallet(manager);
```
#### Setting original balances
* In this case our "bank" will be the wallet names coinBase, this wallet will always have a negative balance
* You can send any amount of money from coinbase to any wallet using the following:
```java
Transaction startingTransaction = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), 100f, null, manager);
startingTransaction.generateSignature(coinBase.getPrivateKey());
startingTransaction.processTransaction();
```
* This example will send 100 dollars from our bank to walletA
* This code should only be used to distribute money from nothing. By creating a transaction object yourself you remove checks like, whether the wallet has a large enough balance to process the transaction
* Balances will then look like
```java
System.out.println("Adding $100 to walletA");
System.out.println("coinbase: " + coinBase.getBalance());
System.out.println("A: " + walletA.getBalance());
System.out.println("B: " + walletB.getBalance());
```
* Result:
```text
Adding $100 to walletA
coinBase: -100.0
A: 100.0
B: 0.0
```
#### Sending money from wallet to wallet
* All transactions from wallet to wallet should use the .sendFunds method
* Next we will send money from walletA to walletB
```java
walletA.sendFunds(walletB.getPublicKey(), 10f).processTransaction();
System.out.println("A: " + walletA.getBalance());
System.out.println("B: " + walletB.getBalance());
```
* In this example walletA creates a transaction to send 10 dollars to walletB
* Result:
```text
A -> B:10
A: 90.0
B: 10.0
```
* Now lets return some money from B to A
```java
System.out.println("B -> A:5");
walletB.sendFunds(walletA.getPublicKey(), 5f).processTransaction();
System.out.println("A: " + walletA.getBalance());
System.out.println("B: " + walletB.getBalance());
```
* Result:
```text
B -> A:5
A: 95.0
B: 5.0
```
* This shows that money can be sent back and forth without an issue
* Here's an example of trying to overDraw a wallet
```java
System.out.println("A -> B:1000");
Transaction overDraw = walletA.sendFunds(walletB.getPublicKey(), 1000f);
if (overDraw != null)
    overDraw.processTransaction();
System.out.println("A: " + walletA.getBalance());
System.out.println("B: " + walletB.getBalance());
```
* Every time a transaction is created there should be a null check to ensure that the transaction is possible to be processed
* In this case we implemented one because we know that A cannot fulfill a $1000 transaction
* Result:
```text
A -> B:1000
A: 95.0
B: 5.0
```
* As you can see the transaction did not get processed

#### Viewing transaction history and how it is stored
* When calling the view transaction history method in the wallet it will printout all the stored transaction associated with that wallet
* This is walletA's transaction history
```java
 walletA.printTransactionHistory();
``` 
* Result:
```text
58e07fc6ace6f65a9b9308157ed45b28df9dd7e4df5214de79a7def425e7be93=Recipient: MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAE705ipjNWvL+5WOiJey5t6yitFeiOKyGCv//T4Q3O4TgvO4Fx/zLWX+mXHho+5yvb Value: 100.0 Id: 58e07fc6ace6f65a9b9308157ed45b28df9dd7e4df5214de79a7def425e7be93
e90c1c4164cb6d9d9047b0291fe057bc931ae51fb1b187d41637153cd14fa043=Recipient: MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAE705ipjNWvL+5WOiJey5t6yitFeiOKyGCv//T4Q3O4TgvO4Fx/zLWX+mXHho+5yvb Value: 5.0 Id: e90c1c4164cb6d9d9047b0291fe057bc931ae51fb1b187d41637153cd14fa043
290828784847985f94c4701815907e569f9e3f450bbc67ee25486d47b9b3f68b=Recipient: MEkwEwYHKoZIzj0CAQYIKoZIzj0DAQEDMgAE705ipjNWvL+5WOiJey5t6yitFeiOKyGCv//T4Q3O4TgvO4Fx/zLWX+mXHho+5yvb Value: -10.0 Id: 290828784847985f94c4701815907e569f9e3f450bbc67ee25486d47b9b3f68b
```
* The important information here is the value column, as you can see the value columns sum is the current balance of A at the end of these 3 transactions
* Every transaction is stored including deposits and withdraws
