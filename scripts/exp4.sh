#!/bin/bash
trap "exit" INT

#bash final_stress_test_group_full.sh $1 $2 $3 $4 > final_stress_test_group_full.txt

bash final_real_test_full.sh $1 $2 $3 $4 IUPHAR > final_real_test_full_iuphar.txt


echo "experiments done"

#java -jar ../target/process_text.jar ./ true false final_stress_test_group_full 3 

java -jar ../target/process_text.jar ./ true false final_real_test_full_iuphar 1 10

echo "experiments done"

cp *.csv ../matlab

cd ../matlab

mkdir -p exp4

matlab -nodisplay -nodesktop -r "run experiments_final_real_test_time_iuphar.m;quit"

echo "matlab experiments done"
