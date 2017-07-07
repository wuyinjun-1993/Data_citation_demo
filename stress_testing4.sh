#!/bin/bash

k=3;

echo $k

true_str="true";

false_str="false";

for view_size in 5 6 7 8 9 10
do
	for round_times in {1..40}
	do	
		echo "$round_times"
		if [ $round_times -eq 1 ];
		then
			echo "start new view size"
			command="java -Xmx20480m  -jar stress_test4.jar $k $view_size ${true_str} ${true_str} ${true_str} ${false_str}"
			echo ${command}
			java -Xmx20480m -jar stress_test4.jar $k $view_size ${true_str} ${true_str} ${true_str} ${false_str}
			for execution_times in {1..2}
			do
				java -Xmx20480m -jar stress_test4.jar $k $view_size ${false_str} ${false_str} ${true_str} ${false_str}
			done

			for execution_times in {1..3}
                        do
				java -Xmx20480m -jar stress_test4.jar $k $view_size ${false_str} ${false_str} ${false_str} ${false_str}
                        done

		else
			command="jar -jar stress_test4.jar $k $view_size ${false_str} ${false_str} ${true_str} ${true_str}"

			java -Xmx20480m -jar stress_test4.jar $k $view_size ${false_str} ${false_str} ${true_str} ${true_str}
			for execution_times in {1..2}
			do
				java -Xmx20480m -jar stress_test4.jar $k $view_size ${false_str} ${false_str} ${true_str} ${false_str}
			done
			for execution_times in {1..3}
			do
				java -Xmx20480m -jar stress_test4.jar $k $view_size ${false_str} ${false_str} ${false_str} ${false_str}
			done
		fi
	done
done
