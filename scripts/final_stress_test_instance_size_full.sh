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


true_str="true";

false_str="false";

view_size=$6

max_instance_size=$5

query_size=3

path="../target/"

synthetic_dir="synthetic_example/"

instance_size=10;

for ((i=1;instance_size < max_instance_size;i++))
do

	instance_size=$((instance_size*10))

	echo "instance_size::"$instance_size

	if ((instance_size > max_instance_size))
	then
		instance_size=$max_instance_size
	fi
#	for round_times in {1..100}
#	do	
#		echo "$round_times"
		if [ $i -eq 1 ];
		then
			echo "start new view size"
			command="java -Xmx20480m  -jar final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${true_str} ${true_str} ${true_str} ${true_str}"
			echo ${command}
			java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${true_str} ${true_str} ${true_str} ${false_str} ${true_str} $1 $2 $3 $4 $synthetic_dir
			for execution_times in {1..2}
                        do
                               java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${true_str} ${false_str} ${true_str} $1 $2 $3 $4 $synthetic_dir
                        done
			for execution_times in {1..3}
			do
				java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${true_str} ${false_str} ${false_str} $1 $2 $3 $4 $synthetic_dir
			done
			for execution_times in {1..3}
			do
				java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${false_str} ${false_str} ${true_str} $1 $2 $3 $4 $synthetic_dir
			done
			for execution_times in {1..3}
			do
				java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${false_str} ${false_str} ${false_str} $1 $2 $3 $4 $synthetic_dir
			done
			for execution_times in {1..3}
                      do
                                java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${false_str} ${true_str} ${false_str} $1 $2 $3 $4 $synthetic_dir
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
			command="java -Xmx20480m  -jar final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${true_str} ${false_str} ${true_str} ${false_str} ${true_str}"
                        echo ${command}
                        java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${true_str} ${true_str} ${false_str} ${true_str} $1 $2 $3 $4 $synthetic_dir
                        for execution_times in {1..2}
                        do
                               java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${true_str} ${false_str} ${true_str} $1 $2 $3 $4 $synthetic_dir
                        done
                        for execution_times in {1..3}
                        do
                               java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${true_str} ${false_str} ${false_str} $1 $2 $3 $4 $synthetic_dir
                        done
                        for execution_times in {1..3}
                        do
                                java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${false_str} ${false_str} ${true_str} $1 $2 $3 $4 $synthetic_dir
                        done
                        for execution_times in {1..3}
                        do
                                java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${false_str} ${false_str} ${false_str} $1 $2 $3 $4 $synthetic_dir
                        done
                        for execution_times in {1..3}
                       do
                                java -Xmx20480m -jar "$path"final_stress_test_instance_size_full.jar $query_size $view_size $instance_size ${false_str} ${false_str} ${false_str} ${true_str} ${false_str} $1 $2 $3 $4 $synthetic_dir
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
#	done
done
