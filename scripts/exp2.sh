#!/bin/bash
trap "exit" INT

#bash final_stress_test_group_full.sh $1 $2 $3 $4 $5 $6 $7> final_stress_test_group_full.txt

#bash final_stress_test_group_min.sh $1 $2 $3 $4 $5 $6 $7> final_stress_test_group_min.txt


echo "experiments done"

#java -jar ../target/process_text.jar ./ true false final_stress_test_group_full 3 

java -jar ../target/process_text.jar ./ false false final_stress_test_group_min 3

echo "experiments done"

cp *.csv ../matlab

cd ../matlab

mkdir -p exp2

#matlab -nodisplay -nodesktop -r "run experiments_final_stress_test_group_time.m;quit"

matlab -nodisplay -nodesktop -r "run experiments_final_stress_test_min_group_num_time.m;quit"

echo "matlab experiments done"
