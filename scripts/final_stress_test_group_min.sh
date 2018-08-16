#!/bin/bash
trap "exit" INT

k=3;

echo $k

echo $1

echo $2

echo $3

echo $4

echo $5

echo $6

echo $7


true_str="true";

false_str="false";

view_size=$6

query_instance_size=$5

db_schema="IUPHAR"

path="../target/"

synthetic_dirc="synthetic_example/"

max_predicate_num=$7

#for view_size in 10 15 20 25 30 
#do
	for round_times in {1..$max_predicate_num}
	do	
		echo "$round_times"
		if [ $round_times -eq 1 ];
		then
			echo "start new view size"
			command="java -Xmx20480m  -jar final_stress_test_group_min.jar $k $view_size ${true_str} ${true_str} ${true_str} ${false_str}"
			echo ${command}
			java -Xmx20480m -jar "$path"final_stress_test_group_min.jar $k $view_size ${true_str} ${true_str} ${true_str} ${false_str} false $query_instance_size $1 $2 $3 $4 $synthetic_dirc
			for execution_times in {1..9}
			do
				java -Xmx20480m -jar "$path"final_stress_test_group_min.jar $k $view_size ${false_str} ${false_str} ${true_str} ${false_str} false $query_instance_size $1 $2 $3 $4 $synthetic_dirc
			done

			for execution_times in {1..10}
                        do
				java -Xmx20480m -jar "$path"final_stress_test_group_min.jar $k $view_size ${false_str} ${false_str} ${false_str} ${false_str} false $query_instance_size $1 $2 $3 $4 $synthetic_dirc
                        done
			for execution_times in {1..10}
                        do
				java -jar "../lib/"schema_level_reasoning_approx.jar $synthetic_dirc $db_schema $2 $3 $4
                        done

		else
			command="jar -jar final_stress_test_group_min.jar $k $view_size ${false_str} ${false_str} ${true_str} ${true_str}"

			java -Xmx20480m -jar "$path"final_stress_test_group_min.jar $k $view_size ${false_str} ${false_str} ${true_str} ${true_str} false $query_instance_size $1 $2 $3 $4 $synthetic_dirc
			for execution_times in {1..9}
			do
				java -Xmx20480m -jar "$path"final_stress_test_group_min.jar $k $view_size ${false_str} ${false_str} ${true_str} ${false_str} false $query_instance_size $1 $2 $3 $4 $synthetic_dirc
			done
			for execution_times in {1..10}
			do
				java -Xmx20480m -jar "$path"final_stress_test_group_min.jar $k $view_size ${false_str} ${false_str} ${false_str} ${false_str} false $query_instance_size $1 $2 $3 $4 $synthetic_dirc
			done
			for execution_times in {1..10}
                        do
                                java -jar "../lib/"schema_level_reasoning_approx.jar $synthetic_dirc $db_schema $2 $3 $4
                        done
		fi
	done
#done
