public class Account {
    private long money;
    private String accNumber;
    private volatile boolean freeToOperate;

    public Account(long money, String accNumber) {
        freeToOperate = true;
        this.money = money;
        this.accNumber = accNumber;
    }

    public Account(String accNumber) {
        this.accNumber = accNumber;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    /**
     * Снятие денег со счёта
     * @param amount
     * @return
     */
    protected boolean withdraw(long amount) {
        //  System.out.println("Сумма денег счёта: " +accNumber+"-"+this.money+"руб");
        if (amount > money || !this.isFreeToOperate()) {
            // System.out.println("Недостаточно денег на счёте!");
            return false;
        } else
            money = money - amount;
        //  System.out.println("Остаток на банковском счёте - "+accNumber+" - " + getMoney() +"руб.");
        return true;
    }

    /**
     * Пополнение счёта
     * @param amount
     */
    protected void fundAccount(long amount) {

        money = money + amount;
        // System.out.println("Остаток банковском на счёте "+accNumber+" - " + getMoney() +"руб.");
    }

    /**
     * Перевод между аккаунтами
     * @param receiverAcc
     * @param amount
     * @return
     */
    protected boolean transferTo(Account receiverAcc, long amount) {

        if (withdraw(amount) && receiverAcc.isFreeToOperate()) {
            receiverAcc.fundAccount(amount);
            return true;
        } else
            //  System.out.println("Операция недоступна");
            return false;
    }

    protected void freeze() {
        freeToOperate = false;
    }

    public boolean isFreeToOperate() {
        return freeToOperate;
    }
}
