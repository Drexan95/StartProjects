# Data Writer
____
__Данное приложение обрабатывает текстовые файлы лежащие в папке data, после чего сортирует их по буквенным строка, целым и дробным числам.
Далее собирает статистку: кол-во строк, самое длинное и короткое значение, а так же сумма и среднее арифметическое для чисел, после чего записывает каждый тип в отдельный текстовый файл.__

:grey_exclamation: _Java v.11.0.19_

Для запуска приложения понадобится скачать и установить JDK для вашей операционной системы

[Ссылка JDK](<https://www.oracle.com/java/technologies/javase/jdk20-archive-downloads.html>)

:orange_book: Использована библиотека **Apache Commons Cli**  v1.2

Данный фреймворк упрощает работу с командной строкой, позволяя легко настраивать параметры запуска приложения.
```yaml
Option option = new Option("e","example",true,"Описание параметра");
```
***Параметры***:

+ е - параметр указывает как будет выглядит опция в командной строке(-е).
+ example - развёрнутое название параметра.
+ true - указывает должен ли параметр иметь аргументы.
+ "Описание параметра" - развёрнутое описание логики и цели данного параметра.

[Ссылка на мавен репозиторий](<https://mvnrepository.com/artifact/commons-cli/commons-cli/1.2>)

Данное приложение было создано и упаковано с помощью **Maven** v3.9.1

***Параметры запуска приложения***

+ s - выводит краткую статистику в консоль.
+ f - выводит полную статистику в консоль.
+ а - по умолчанию данные перезаписываются в файлы, параметр нужно указывать если необходимо добавить данные в уже существующие файлы
+ o - c помощью данного параметры можно указать путь сохранения полученных данных.
+ p - с помощью этого параметра можно задать префикс к названию файлов записи.
+ Так же в конце необходимо указать название исходных файлов в формате .txt.

:page_facing_up: *Пример запуска* :

java -jar Data_Writer-1.0-jar-with-dependencies.jar -f -p new_ -o somepath\\somedirectory in1.txt in2.txt

