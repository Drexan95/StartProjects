![unnamed](https://user-images.githubusercontent.com/86364162/142389789-a3c8c66d-f675-4277-a53a-828f76817456.png)

YahoooSearchBot is a Search Engine SpringBoot application

Stack:JDBC,JSOUP,JSON,JPA Hibernate,Spring Boot.

Rest API provides interface to start/stop scanning sites given in application.yml file, extract text from html elements  and collect lemmas from it using morphology library and store in SQL database with info where it's found and how many times.All transactions to db provides by JPA.
You can customize which sites to index, just change application.yml sites.

After indexing is complete you can search pages by query request,application calculate relevancy  and return sorted pages with small snippet of text where words occur and url head.
Also you can customize search request: choose in which site to search, limit or set offset of the results. 

![unnamed (1)](https://user-images.githubusercontent.com/86364162/142390680-13842fe1-1ed0-45cf-8ed9-909d5f018e24.png)

Frontend sadly not plugged in properly.
