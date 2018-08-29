ready-to-use bank app

REST API:

    router.post("/accounts").blockingHandler(accountRouter::createAccount, false);
    router.get("/accounts").blockingHandler(accountRouter::getAllAccounts, false);
    router.get("/accounts/:accountId").blockingHandler(accountRouter::getAccountById, false);
    router.delete("/accounts/:accountId").blockingHandler(accountRouter::deleteAccount, false);

    router.post("/transactions").blockingHandler(transactionRouter::createTransaction, false);
    router.post("/transactions/deposit/:accountId").blockingHandler(transactionRouter::depositMoney, false);
    router.get("/transactions").blockingHandler(transactionRouter::getAllTransactions, false);
    router.get("/transactions/:transactionId").blockingHandler(transactionRouter::getTransactionById, false);

TBD: 
 Swagger.yaml
