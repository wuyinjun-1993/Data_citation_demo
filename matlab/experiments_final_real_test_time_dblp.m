file1 = 'final_real_test_full_dblp_tuple_agg_union.csv';

file2 = 'final_real_test_full_dblp_semi_schema_agg_union.csv';

file3 = 'final_real_test_full_dblp_schema.csv';

[TL] = csvread(file1);
[SSL] = csvread(file2);
[SL] = csvread(file3);

cg_time=TL(:,5);
TL_reasoning_time=TL(:,3);
SSL_reasoning_time=SSL(:,3);
SL_reasoning_time=SL(:,3);

[m,n] = size(TL);

table={'Query', 't_{cs} in TLA (s)', 't_{cs} in SSLA (s)', 't_{cs} in SLA (s)', 't_{cg} (s)'};
for i=1:m
    q_name = strcat('Q',int2str(i));
    table{i+1,1}= q_name;
    table{i+1,2} = num2str(TL_reasoning_time(i,1));
    table{i+1,3} = num2str(SSL_reasoning_time(i,1));
    table{i+1,4} = num2str(SL_reasoning_time(i,1));
    table{i+1,5} = num2str(cg_time(i,1));
end
T = cell2table(table);

writetable(T,'exp4/final_real_test_dblp.csv','Delimiter',',', 'WriteVariableNames',false);
