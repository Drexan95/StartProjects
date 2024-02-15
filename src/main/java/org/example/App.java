package org.example;

import Service.Eraser;
import Service.FileFinder;
import Service.FileReader;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    static String originPath = "D:\\GitProjects\\Data_Writer\\data\\";
  public   static List<File> files = new ArrayList<>();
  public static   String o = "";
   public static   String p = "";
   public static boolean s;
   public static boolean f;
   public static boolean append;
  static   CommandLine cmd;

    public static void main( String[] args ) throws IOException {
        try {
            FileFinder.getFiles(args, originPath);
        }catch (FileNotFoundException e){
            System.out.println("Не удаётся найти указанный файл");
        }
        averageRun(args);
    }

    /**
     * Метод создаёт параметры для запуска
     * @param args
     */
     private static void creatCmdOptions(String[] args){
         Options options = new Options();
         Option smallStat = new Option("s","smallStatistics",false,"Shows small statistics");
         smallStat.setRequired(false);
         options.addOption(smallStat);
         Option fullStat = new Option("f","fullStatistics",false,"Shows full statistics");
         fullStat.setRequired(false);
         options.addOption(fullStat);
         Option appendMode = new Option("a","append",false,"Append data to existing files");
         appendMode.setRequired(false);
         options.addOption(appendMode);
         Option selectPath = new Option("o","output",true,"Specifies directory path to copy with this structure: Disk:\\somepath\\somedirectory\\");
         selectPath.setRequired(false);
         options.addOption(selectPath);
         Option prefix = new Option("p","prefix",true,"Add prefix to files");
         prefix.setRequired(false);
         options.addOption(prefix);
         CommandLineParser parser = new BasicParser();
         HelpFormatter formatter = new HelpFormatter();

         try {
             cmd = parser.parse(options,args);
         } catch (ParseException exception){
             System.out.println(exception.getMessage()+"\n"+"Неверно указаны параметры запуска");
             formatter.printHelp("Data_Writer",options);
             System.exit(1);
             return;
         }
         s = cmd.hasOption("smallStatistics");
         f = cmd.hasOption("fullStatistics");
         o = cmd.getOptionValue("output",originPath);
            FileFinder.checkDirectory();
         p = cmd.getOptionValue("prefix","");
         append = cmd.hasOption("append");
     }

    public static void averageRun(String[] args) throws IOException {
     creatCmdOptions(args);
        try {
            if (!append) {
                Eraser.clearFile(o + p + "floats.txt");
                Eraser.clearFile(o + p + "strings.txt");
                Eraser.clearFile(o + p + "integers.txt");
            }

        } catch (FileNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
        FileReader.readLine(FileFinder.readFiles(files));
        FileReader.writeLines();
    }
}
