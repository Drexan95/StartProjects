
import java.util.Hashtable;
import java.util.Random;

public class Bank {

    private Hashtable<String, Account> accounts = new Hashtable<>();
    private final Random random = new Random();
    private final int CHECK_FRAUD = 50000;

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
            throws InterruptedException {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    /**
     * Метод переводит деньги между счетами.
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка счётов
     */
    public void transfer(Account fromAccountNum, Account toAccountNum, long amount) throws InterruptedException {
        int fromId = Integer.parseInt(fromAccountNum.getAccNumber());//Сравнению по айди во избежание гонки
        int toId = Integer.parseInt(toAccountNum.getAccNumber());

            synchronized ( (fromId<toId)?fromAccountNum:toAccountNum) {
                synchronized ( (fromId<toId)?toAccountNum:fromAccountNum) {
                   if(!checkForFraud(fromAccountNum,toAccountNum,amount)){
                       return;
                   }
                    fromAccountNum.transferTo(toAccountNum, amount);
                    // System.out.println("Счёт " + fromAccountNum.getAccNumber() + " Перевёл сумму " + amount + " руб" + " на счёт " + toAccountNum.getAccNumber());}
                }

            }
    }

    public boolean checkForFraud(Account fromAcc, Account toAcc, long amount) throws InterruptedException {
        if (!fromAcc.isFreeToOperate() || !toAcc.isFreeToOperate()) {//Счёта не заблокированы?
            return false;
        }
        try {
            if ((amount > CHECK_FRAUD && isFraud(fromAcc.getAccNumber(), toAcc.getAccNumber(), amount))) {//Проверка на мошеничество
                fromAcc.freeze();
                toAcc.freeze();
                // System.out.println("Счёта заблокированы!");
                return false;
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return true;
    }


    public long getBalance() {
        int sum = 0;
        for (Account account : getAccounts().values()) {
            sum += account.getMoney();
        }
        return sum;
    }

    public void createAccounts() {
        for (int i = 1; i <= 100; i++) {
            String accNumber = String.valueOf(i);
            long money = (long) (Math.random() * 100000);
            Account account = new Account(money, accNumber);
            accounts.put(accNumber, account);
        }

    }

    public synchronized Hashtable<String, Account> getAccounts() {
        return accounts;
    }

}
