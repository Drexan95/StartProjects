
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        bank.createAccounts();
      Account account1 = bank.getAccounts().get("1");
      Account account2 = bank.getAccounts().get("2");
        System.out.println("Main сумма денег в банке: "+bank.getBalance());
     new Thread(()-> {
         try {
             bank.transfer(account1,account2,randomWithRange(10000,55000));
             System.out.println("Thread1 сумма денег в банке: "+bank.getBalance());
         } catch (InterruptedException exception) {
             exception.printStackTrace();
         }
     }).start();
        new Thread(()-> {
            try {
                bank.transfer(account2,account1,randomWithRange(10000,55000));
                System.out.println("Thread2 сумма денег в банке: "+bank.getBalance());
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }).start();

    }
  static   long randomWithRange(long min, long max)
    {
        long range = (max - min) + 1;
        return (long)(Math.random() * range) + min;
    }
}
