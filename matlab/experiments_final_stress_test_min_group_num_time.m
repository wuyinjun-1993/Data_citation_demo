file_name = 'exp_final_stress_test_group_min.xlsx';
TL_sheet_name = 'Sheet2';
SSL_sheet_name = 'Sheet3';
SL_sheet_name = 'Sheet4';
covering_set_time_column = 3;
gap = 6;
query_time_column = 9;

file1 = 'final_stress_test_group_min_tuple.csv';

file2 = 'final_stress_test_group_min_semi_schema.csv';

file3 = 'final_stress_test_group_min_schema.csv';

[TL] = csvread(file1);
[SSL] = csvread(file2);
[SL] = csvread(file3);

view_number = TL(:,1);
% view_number = view_number*2;
TL_total_derivation_time = TL(:,covering_set_time_column);
TL_reasoning_time = TL(1:gap:end,query_time_column);
SSL_total_derivation_time = SSL(:,covering_set_time_column);
SSL_reasoning_time = SSL(1:gap:end,query_time_column);
SL_total_derivation_time = SL(:,2);
% SL_reasoning_time = SL(1:3:end,3);

reasoning_time = [TL_reasoning_time, SSL_reasoning_time];
clr = [0 1 0; 1 0 0];
colormap(clr);
view_sub_num = view_number(1:gap:end,:);

bar(view_sub_num, reasoning_time,'barwidth', 1, 'basevalue', 1);
hold on;
% bar(view_number, SSL_reasoning_time,'barwidth', .1, 'basevalue', 1);
% set(gca,'YScale','log')


h1 = plot(view_number, TL_total_derivation_time,'g-*');

h2 = plot(view_number, SSL_total_derivation_time,'r-o');
h3 = plot(view_number, SL_total_derivation_time,'b-+');
set([h1 h2 h3],'LineWidth',1)

h = legend('t_{qe} of TLA', 't_{qe} of SSLA', 't_{cs} of TLA', 't_{cs} of SSLA', 't_{cs} of SLA');
set(h,'FontSize',15); 
xlabel('N_p','FontSize', 15);
ylabel('time (s)','FontSize', 15);
xt = get(gca, 'XTick');
xlim([2, 50]);
set(gca, 'FontSize', 15)
saveas(gcf,'exp2/final_stress_test_group_min_time.png')
