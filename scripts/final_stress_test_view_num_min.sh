#!/bin/bash
trap "exit" INT
k=3;

echo $1

echo $2

echo $3

echo $4

query_size=4

query_instance_size=10000

echo $k

true_str="true";

false_str="false";

synthetic_dir="synthetic_example/"

path="../target/"

db_schema="IUPHAR"


#for query_size in 4 5 6 7 8 9 10
#do
	for round_times in {1..50}
	do	
		echo "$round_times"
		if [ $round_times -eq 1 ];
		then
			echo "start new view size"
			command="java -Xmx20480m  -jar final_stress_test_view_num_min.jar $query_size ${true_str} ${true_str} ${true_str} ${false_str}"
			echo ${command}
			java -Xmx20480m -jar "$path"final_stress_test_view_num_min.jar $query_size ${true_str} ${true_str} ${true_str} ${false_str} true $query_instance_size $1 $2 $3 $4 $synthetic_dir
			for execution_times in {1..9}
			do
				java -Xmx20480m -jar "$path"final_stress_test_view_num_min.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} true $query_instance_size $1 $2 $3 $4 $synthetic_dir
			done

			for execution_times in {1..10}
                        do
				java -Xmx20480m -jar "$path"final_stress_test_view_num_min.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} true $query_instance_size $1 $2 $3 $4 $synthetic_dir
                       done
			for execution_times in {1..10}
                        do
				java -jar "../lib/"schema_level_reasoning_approx.jar $synthetic_dir $db_schema $2 $3 $4
                        done

		else
			command="jar -jar final_stress_test_view_num_min.jar $query_size ${false_str} ${false_str} ${true_str} ${true_str} true $query_instance_size $1 $2 $3 $4 $synthetic_dir"

			java -Xmx20480m -jar "$path"final_stress_test_view_num_min.jar $query_size ${false_str} ${false_str} ${true_str} ${true_str} true $query_instance_size $1 $2 $3 $4 $synthetic_dir
			for execution_times in {1..9}
			do
				java -Xmx20480m -jar "$path"final_stress_test_view_num_min.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} true $query_instance_size $1 $2 $3 $4 $synthetic_dir
			done
			for execution_times in {1..10}
			do
				java -Xmx20480m -jar "$path"final_stress_test_view_num_min.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} true $query_instance_size $1 $2 $3 $4 $synthetic_dir
			done
			for execution_times in {1..10}
                        do
                              java -jar "../lib/"schema_level_reasoning_approx.jar $synthetic_dir $db_schema $2 $3 $4
                        done
		fi
	done
#done
