public class Transaction {
    public String accountType, accountNumber, currency, date, reference, descritpion, income, outcome;

    @Override
    public String toString(){
        return accountType+"\t" + accountNumber+"\t" +currency+"\t" + date +"\t"+ reference+"\t" +descritpion +"\t"+income+"\t" +outcome+"\n";
    }
}

