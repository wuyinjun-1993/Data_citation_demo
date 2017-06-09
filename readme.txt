Data_citation_demo

1. main function and test file:

1.1 test file:
The test file is in src/edu/upenn/cis/citation/test/test_output.java. In this file, you can generate your any query. But currently, we don't convert string to query. Instead, you need to specify the head variable, lambda variable, conditions, subgoals separately. There is an instruction and an example query in this java file.
The query results and the associated citations will be output to a excel file. You can give a name to this output file. The default name is "tuple_level.xlsx".
In the output file, the first several columns will list the query results. The following columns will list the covering sets and corresponding citations alternatively. 

1.2 main functions:
 in current version, it contains two approaches to generating citations for a query. One is tuple level reasoning, the other is schema level reasoning based on query execution.


 The main function for tuple level reasoning lies in src/reasoning/Tuple_reasoning1.java. The main function for schema level reasoning lies in src/reasoning/Tuple_reasoning2.java.


 The main function of UI lies in src/edu/upenn/cis/citation/ui/QBEApp.java. Currently, we are still working on that.


2. database:

2.1 How to link the database and how to change the configurations of database connection:

In order to change the database configurations, you can go to src/edu/upenn/cis/citation/Pre_processing/populate_db.java. In the head of this java file, there are some parameters you can change:
 String db_url //it is the url of the database. You can comment current url address and use the other one which is the local url of psql database
 String usr_name; // the user name of the database you are using
 String passwd;  // the password of the database you are using

The latest database dump is in iuphar.sql. You can load data in it into your local psql database by the following command in linux:
        psql < iuphar.sql

2.2 Views in the database:

The information from each view has been normalized and stored in separate tables in the database.

view_table(view, head_variables, name) // it stores the view_id, head variables and the name of each view. In order to avoid ambiguity, we put the relation name in the prefix of the attribute name. For example, if a view has a head variable "family_id" which comes from a relation "introduction", then the head variable stored in this table would be "introduction_family_id"

viwe2subgoals(view, subgoal_names, subgoal_origin_names) //it stores the view_id, relational subgoal name and the original names of each relational subgoal in the database. For example, suppose that the "family" relation appears twice in the body of that view, the names for both the two occurance are "family1" and "family2" respectively. So in the table view2subgoal, the value in the attribute subgoal_names for "family1" would be "family1" and the value in the attribute subgoal_origin_names would be "family" since it comes from "family" relation in the database

view2conditions(view, conditions) // it stores the view_id and conditions of the view. A view can have multiple conditions. For the attribute names that appears in some condition, they follow the same rule as head variables. That is, we use the relation name + attribute name to avoid ambiguity.

view2lambda_term(view, lambda_term, table_name) // it stores the view_id, lambda_term variable and the relation that the lambda_term varaible comes from. For the lambda_term variable, it follow the same rue of head variables.

For example, we have a view \lambda X. V1(X) :- R(X, Y), R(Y, Z). The name of this view is still V1. Since the relation R appears twice, they will be renamed as R1 and R2 separately. Besides, there is a join condition, which will be also a non-relational subgoal. 

So the view V1 will become: \lambda R1_X. V1(R1_X) :- R1(R1_X, R1_Y), R2(R2_Y, R2_Z), R1_Y = R2_Y 

We will store the view in the database in the following way:

In the table view_table(view, head_variables, name), we store the value ("V1", "R1_X", "V1")

In the table viwe2subgoals(view, subgoal_names, subgoal_origin_names), we store the value ("V1", "R1", "R") and ("V1", "R2", "R")

In the table view2conditions(view, conditions), we store the value ("V1", "R1_Y = R2_Y")

In the table view2lambda_term(view, lambda_term, table_name), we store the value ("V1", "R1_X", "R")  


3. The structure of the source code:

java_cup, pruebaGen come from Corecover algorithm. We don't use them in our current implementations

src/edu/upenn/cis/citation/ contains the majority of our code.

In this folder:

Corecover:
contains the code borrowed from the Corecover algorithm. We made some changes on this code to make it compatible with our ideas

Operation:
contains the code related to all the possible "selection" operations that can appear in the conditions of the datalog. For example, for the datalog: Q(x) :- family(fid, fname, type), fid = '3'. it contains a condition "fid = '3'", which contains an equality operation. In the Operation folder, we contain the following operations: equal("="), less than("<"), greater than(">"), greater than and equal to (">="), less than and equal to ("<="), not equal ("<>")

Pre_processing:
It contains some codes related to preprocessing. For example, populate_db.java is used for annotating the base relations in the database, view_operation.java is used for adding, editing, presenting and deleting views in the database. Query_operation is used for adding, editing, presenting and deleting citation queries in the database.

Aggregation:
It contains codes which are used for aggregating citations from each tuple in the query results.

citation_view:
It contains classes used to store the views, including parameterized and unparameterized views.

dao:
It contains front-end code

datalog:
It contains some codes on how to extend the head of query and how to covert datalog query to sql

gen_citation:
It contains the codes which is used to generate citation for a single view; If it is a parameterized view, this view should be evaluated first using the value from specific tuple.

output:
It contains the codes outputing the citations to excel file

reasoning:
It contains the codes for reasoning approach:
Tuple_reasoning1.java is for tuple level reasoning algorithm
Tuple_reasoning2.java is for semi-schema level reasoning algorithm
Tuple_reasoning2_opt.java is for the optimized semi-schema level reasoning algorithm

sort_citation_view_vec:
It contains the codes for sorting the views in a view vector;

ui:
It contains front-end code

test:
It contains the testing code. Currently it only contains one file, which can generate certain query and use tuple level reasoning algorithm to generate citations for each tuple in the query results.
