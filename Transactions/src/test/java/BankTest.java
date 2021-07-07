import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;


public class BankTest extends TestCase {
    Bank bank = new Bank();
    List<Thread> testThreads = new ArrayList<Thread>();
    @Override
    protected void setUp() throws Exception {
        bank.createAccounts();

    }

    static long randomWithRange(long min, long max) {
        long range = (max - min) + 1;
        return (long) (Math.random() * range) + min;
    }

    public void testTransfer() throws InterruptedException {
        long beforeTransfer = bank.getBalance();//Баланс до транзакций
        long maxId = bank.getAccounts().size();
        for (int i = 0; i < 2000; i++) {//Cоздаю 2 тысячи потоков

            String fromAccNum = String.valueOf(randomWithRange(1, maxId));//Создаю переменные аккаунтов со случайными айди и делаю между ними переводы
            String toAccNum = String.valueOf(randomWithRange(1, maxId));
            Account account1 = bank.getAccounts().get(fromAccNum);
            Account account2 = bank.getAccounts().get(toAccNum);
            if (!fromAccNum.equals(toAccNum)) {//Не переводить самому себе
                Thread t = new Thread(() -> {
                    try {
                        long amount = randomWithRange(10000, 52000);//Случайная сумма для перевода
                        bank.transfer(account1, account2, amount);
                        System.out.println(Thread.currentThread() + "Cумма денег в банке: " + bank.getBalance());
                    } catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }

                });
                t.start();
                testThreads.add(t);

            }

        }
        for (Thread t : testThreads) {
           try {
               t.interrupt();
               t.join();
           } catch (InterruptedException e){
               e.printStackTrace();
           }
        }
        long afterTansfer = bank.getBalance();

        assertEquals(beforeTransfer, afterTansfer);
    }
}
