#!/bin/bash

trap "exit" INT

echo $1

echo $2

echo $3

echo $4

echo $5

true_str="true";

false_str="false";

path="../target/"

real_dir="../real_example/"$5"/"

for query_size in 0 1 2 3 4 5 6 7
do
		echo "$query_size"
		if [ $query_size -eq 0 ];
		then
			echo "start new view size"
			command="java -Xmx20480m  -jar final_real_test_full.jar $query_size ${true_str} ${true_str} ${false_str} ${true_str}"
			echo ${command}
			java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${true_str} ${true_str} ${false_str} ${true_str} $real_dir $1 $2 $3 $4
			for execution_times in {1..9}
			do
				java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${true_str} ${false_str} ${true_str} $real_dir $1 $2 $3 $4
			done

			for execution_times in {1..10}
                       do
                                java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${true_str} ${false_str} ${false_str} $real_dir $1 $2 $3 $4

			done

			for execution_times in {1..10}
                        do
				java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${false_str} ${false_str} ${true_str} $real_dir $1 $2 $3 $4
                        done
			for execution_times in {1..10}
                        do
                                java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} $real_dir $1 $2 $3 $4
                        done
			for execution_times in {1..10}
                        do
                                java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} $real_dir $1 $2 $3 $4
                        done


		else

			for execution_times in {1..10}
			do
				java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${true_str} ${false_str} ${true_str} $real_dir $1 $2 $3 $4
			done
			for execution_times in {1..10}
                        do
                                java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${true_str} ${false_str} ${false_str} $real_dir $1 $2 $3 $4
                        done
			for execution_times in {1..10}
			do
				java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${false_str} ${false_str} ${true_str} $real_dir $1 $2 $3 $4
			done
			for execution_times in {1..10}
                        do
                                java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${false_str} ${false_str} ${false_str} $real_dir $1 $2 $3 $4
                        done
			for execution_times in {1..10}
                       do
                                java -Xmx20480m -jar "$path"final_real_test_full.jar $query_size ${false_str} ${false_str} ${true_str} ${false_str} $real_dir $1 $2 $3 $4
                        done

		fi
done
