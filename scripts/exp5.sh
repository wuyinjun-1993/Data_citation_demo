#!/bin/bash
trap "exit" INT

#bash final_stress_test_group_full.sh $1 $2 $3 $4 > final_stress_test_group_full.txt

bash final_real_test_full.sh $1 $2 $3 $4 DBLP-NSF > final_real_test_full_dblp.txt


echo "experiments done"

#java -jar ../target/process_text.jar ./ true false final_stress_test_group_full 3 

java -jar ../target/process_text.jar ./ true false final_real_test_full_dblp 1 10

echo "experiments done"

cp *.csv ../matlab

cd ../matlab

mkdir -p exp5

#matlab -nodisplay -nodesktop -r "run experiments_final_stress_test_group_time.m;quit"

echo "matlab experiments done"
