#!/bin/bash
trap "exit" INT

bash final_stress_test_view_num_full.sh $1 $2 $3 $4 > final_stress_test_view_num_full.txt

bash final_stress_test_view_num_min.sh $1 $2 $3 $4 > final_stress_test_view_num_min.txt

echo "experiments done"

java -jar ../target/process_text.jar ./ true false final_stress_test_view_num_full 4 

java -jar ../target/process_text.jar ./ false false final_stress_test_view_num_min 4

cp *.csv ../matlab

cd ../matlab

mkdir -p exp1

matlab -nodisplay -nodesktop -r "run experiments_final_stress_test_view_num_covering_sets.m; quit"

matlab -nodisplay -nodesktop -r "run experiments_final_stress_test_view_num_time.m;quit"

matlab -nodisplay -nodesktop -r "run experiments_final_stress_test_min_view_num_time.m;quit"

echo "matlab execution done"
