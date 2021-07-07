import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final String MOVEMENTLIST = "data/movementList.csv";

    public Main() throws IOException {
    }


    public static void main(String[] args) throws IOException
    {
       parseCSV(getTransactions(MOVEMENTLIST));
    }



    public static void parseCSV(List<Transaction> transactions) throws IOException {
        System.out.println("Общий расход "+ transactions.stream().skip(1).mapToDouble(transaction ->//РАСХОД
                Double.parseDouble(transaction.outcome.replaceAll("\"","").replace(',', '.'))).sum()+"руб.");
        System.out.println("Общий доход "+transactions.stream().skip(1).mapToDouble(transaction ->//ДОХОД
                Double.parseDouble(transaction.income)).sum()+"руб.");
        /**
         * Общие счёта
         */
        Map<String,Double> billList = new HashMap<>();
        for (int i=1;i<transactions.size();i++) {
            Pattern pattern = Pattern.compile("\\D[^+\\d\\s].+\\s?\\D\\s{9,}");
            Matcher matcher = pattern.matcher(transactions.get(i).descritpion);
            matcher.find();
            String shortInfo = transactions.get(i).descritpion.substring(matcher.start(), matcher.end()).trim().replaceAll("\\\"", "");
            Double value =Double.parseDouble( transactions.get(i).outcome.replaceAll("\"","").replace(',', '.'));
            if(!billList.containsKey(shortInfo)){
                billList.put(shortInfo,value);
            }
            else {
                double sum =billList.get(shortInfo);
                sum +=value;
                billList.put(shortInfo,sum);
            }
        }
        System.out.println("Общий расход по организациям: "+"\n");
        for(String bills:billList.keySet()){
            System.out.printf("%-30s -----> %.2f %-10s\n", bills, billList.get(bills), "руб");
        }
    }


    /**
     * Создаю лист обьектов Транзакий из истории csv.
     * @param MOVEMENTLIST
     * @return
     * @throws IOException
     */

    private static List<Transaction> getTransactions(String MOVEMENTLIST) throws IOException {
        List<String> operations = Files.readAllLines(Paths.get(MOVEMENTLIST));
        List<Transaction> transactions = new ArrayList<Transaction>();
        for(String operation : operations){
            String[] splitedInfo = operation.split(",");
            ArrayList<String> infoList = new ArrayList<String>();
            for(int i=0;i< splitedInfo.length;i++){
                if(IsColumnPart(splitedInfo[i])){
                    String lastText = infoList.get(infoList.size()-1);
                    infoList.set(infoList.size()-1,lastText+","+splitedInfo[i]);
                }
                infoList.add(splitedInfo[i]);
            }
            Transaction transaction = new Transaction();
            transaction.accountType = infoList.get(0);
            transaction.accountNumber = infoList.get(1);
            transaction.currency = infoList.get(2);
            transaction.date = infoList.get(3);
            transaction.reference = infoList.get(4);
            transaction.descritpion = infoList.get(5);
            transaction.income = infoList.get(6);
            transaction.outcome = infoList.get(7);
            transactions.add(transaction);

        }
        return transactions;
    }



    /**
     * Проверка является ли сумма заключенная в кавычки частью столбца
     */
    private static boolean IsColumnPart(String text) {
        String trimText = text.trim();
        return trimText.indexOf("\"") == trimText.lastIndexOf("\"") && trimText.endsWith("\"");
    }


}
