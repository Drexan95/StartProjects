package Service;

import org.example.App;

import java.util.List;

/**
 * Класс выводит статистику
 */
public class Statistics {
    protected static void smallFloatStat(List<String> lines){
        if(App.s){
            System.out.println("Кол-во дробных чисел -" + lines.size());
        }
    }
    protected static void fullFloatStat(){
        if(App.f){
            System.out.println("Статистика дробных чисел:"+"\n"+ "Макс число "+ FileWriter.maxFloat+"\n"+"Мин число "+FileWriter.minFloat+"\n"+"Сумма чисел "+ FileWriter.sumFloat+"\n"+ "Среднее значение "+FileWriter.avgFloat);
        }
    }
    protected static void smallIntStat(List<String> lines){
        if(App.s){
            System.out.println("Кол-во целых чисел - "+lines.size());
        }
    }
    protected static void fullIntStat(){
        if(App.f) {
            System.out.println("Статистичка целых чисел :" + "\n" + "Макс число " + FileWriter.maxInt + "\n" + "Мин число " + FileWriter.minInt + "\n" + "Сумма чисел " + FileWriter.sumInt + "\n" + "Среднее значение " + FileWriter.avgInt + ",");
        }
    }
    protected static void smallStringStat() {
        if (App.s) {
            System.out.println("Кол-во строк -" + FileWriter.stringsCount);
        }
    }
    protected static void fullStringStat(){
        if(App.f) {
            System.out.println("Статистика строк:" + "\n" + " Кол-во строк -" + FileWriter.stringsCount + ", Самое короткая строка - " +FileWriter.minString + " символов, Самая длинная -" +FileWriter.maxString+ "символов");
        }
    }

}
