#!/bin/bash
trap "exit" INT

#bash final_stress_test_group_full.sh $1 $2 $3 $4 > final_stress_test_group_full.txt

bash final_stress_test_instance_size_full.sh $1 $2 $3 $4 > final_stress_test_instance_size_full.txt


echo "experiments done"

#java -jar ../target/process_text.jar ./ true false final_stress_test_group_full 3 

java -jar ../target/process_text.jar ./ true true final_stress_test_instance_size_full 3

echo "experiments done"

cp *.csv ../matlab

cd ../matlab

mkdir -p exp3

matlab -nodisplay -nodesktop -r "run experiments_final_stress_test_group_time.m;quit"

echo "matlab experiments done"
