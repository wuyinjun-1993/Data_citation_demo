Data_citation_demo
 in current version, it contains two approaches to generating citations for a query. One is tuple level reasoning, the other is schema level reasoning based on query execution. 
 
 
 The main function for tuple level reasoning lies in src/reasoning/Tuple_reasoning1.java. The main function for schema level reasoning lies in src/reasoning/Tuple_reasoning2.java. 
 
 
 The main function of UI lies in src/edu/upenn/cis/citation/ui/QBEApp.java. Currently, we are still working on that.

In order to change the database settings, you can go to src/edu/upenn/cis/citation/Pre_processing/populate_db.java. In the head of this java file, there are some parameters you can change:
 String db_url //it is the url of the database. You can comment current url address and use the other one which is the local url of psql database
 String usr_name; // the user name of the database you are using
 String passwd;  // the password of the database you are using


