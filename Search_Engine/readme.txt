Work in progress
Frontend not plugged in yet
SpringBoot app Search engine scan the sites given in application file using ForkJoinPool,
collect text from html files and extract lemmas from it using morphology library and store in SQL database
RestControllers provides interface to search pages by query request,application calculate relevancy  and return sorted pages with small snippet of text where words occur.