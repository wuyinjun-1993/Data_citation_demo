#!/bin/bash
trap "exit" INT
query_size=4;

echo $query_size

true_str="true";

false_str="false";

path="../target/"


mkdir -p "$path"synthetic_example
mkdir -p "$path"reasoning_results

mkdir -p synthetic_example
mkdir -p reasoning_results

#for query_size in 4 5 6 7 8 9 10
#do
	for round_times in {1..50}
	do	
		echo "$round_times"
		if [ $round_times -eq 1 ];
		then
			echo "start new view size"
			command="java -Xmx20480m  -jar final_stress_test_view_num_full.jar $query_size ${true_str} ${true_str} ${true_str} ${false_str} ${true_str}"
			echo ${command}
			java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${true_str} ${true_str} ${true_str} ${false_str} ${false_str} ${true_str}
			for execution_times in {1..2}
                        do
                               java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} ${false_str} ${true_str}
                        done
			for execution_times in {1..3}
			do
				java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} ${false_str} ${false_str}
			done
			for execution_times in {1..3}
			do
				java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} ${false_str} ${true_str}
			done
			for execution_times in {1..3}
			do
				java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} ${false_str} ${false_str}
			done
			for execution_times in {1..3}
                        do
                                java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${false_str} ${true_str} ${false_str} ${false_str}
                        done
#			for execution_times in {1..9}
#			do
#				java -Xmx20480m -jar final_stress_test_view_num_min.jar $query_size $view_size ${false_str} ${false_str} ${true_str} ${false_str}
#			done

#			for execution_times in {1..10}
#                        do
#				java -Xmx20480m -jar final_stress_test_view_num_min.jar $query_size $view_size ${false_str} ${false_str} ${false_str} ${false_str}
 #                       done
#			for execution_times in {1..10}
 #                       do
#				java -jar schema_level_reasoning_approx.jar
  #                      done

		else
			command="jar -jar final_stress_test_view_num_min.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} ${true_str} ${true_str}"

			java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} ${true_str} ${true_str}
			for execution_times in {1..2}
                        do     
                               java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} ${false_str} ${true_str}
                        done
			for execution_times in {1..3}
                        do
                                java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} ${false_str} ${false_str}
                        done
                        for execution_times in {1..3}
                        do
                                java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} ${false_str} ${true_str}
                        done
                        for execution_times in {1..3}
                        do
                                java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} ${false_str} ${false_str}
                        done
			for execution_times in {1..3}
                        do
                                java -Xmx20480m -jar "$path"final_stress_test_view_num_full.jar $query_size  ${false_str} ${false_str} ${false_str} ${true_str} ${false_str} ${false_str}
                        done

#			for execution_times in {1..9}
#			do
#				java -Xmx20480m -jar final_stress_test_view_num_min.jar $query_size $view_size ${false_str} ${false_str} ${true_str} ${false_str}
#			done
#			for execution_times in {1..10}
#			do
#				java -Xmx20480m -jar final_stress_test_view_num_min.jar $query_size $view_size ${false_str} ${false_str} ${false_str} ${false_str}
#			done
#			for execution_times in {1..10}
 #                       do
 #                             java -jar schema_level_reasoning_approx.jar
#                        done
		fi
	done
#done
