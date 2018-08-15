file1 = 'final_stress_test_view_num_full_tuple_agg_intersection.csv';

file2 = 'final_stress_test_view_num_full_semi_schema_agg_intersection.csv';

file3 = 'final_stress_test_view_num_full_schema.csv';


file_name = 'experiments_final_stress_test_group_num_full.xlsx';
TL_sheet_name = 'Sheet1';
SSL_sheet_name = 'Sheet2';
SL_sheet_name = 'Sheet3';
[TL] = csvread(file1);
[SSL] = csvread(file2);
[SL] = csvread(file3);
view_number = TL(:,1);
%view_number = view_number * 2;
TL_total_derivation_time = TL(:,2);
citation_generation_time = TL(:,5);
TL_covering_set_num = TL(:,6);
TL_reasoning_time = TL(1:3:end,9);
TL_query_time = TL(1:3:end,8);
SSL_total_derivation_time = SSL(:,2);
SSL_covering_set_num = SSL(:,6);
yyaxis left
h4 = plot(view_number, TL_covering_set_num,'-*');
xlabel('N_p','FontSize', 15);
ylabel('N_{cs}','FontSize', 15);
hold on;
yyaxis right
h5 = plot(view_number, citation_generation_time, '-o');
ylabel('t_{cg} (s)','FontSize', 15);
set([h4, h5],'LineWidth',2)
h = legend('N_{cs}', 't_{cg}');
set(h,'FontSize',18); 
xlim([0, 50]);
xt = get(gca, 'XTick');
% xlim([-1, 50]);
set(gca, 'FontSize', 15)
hold off;

SSL_reasoning_time = SSL(1:3:end,9);
SSL_query_time = SSL(1:3:end,8);
SL_total_derivation_time = SL(:,2);
% SL_reasoning_time = SL(1:3:end,3);

reasoning_time = [TL_reasoning_time, SSL_reasoning_time];
clr = [0 1 0; 1 0 0];
colormap(clr);
view_sub_num = view_number(1:3:end,:);

[row, col] = size(TL_reasoning_time);
performance = zeros(row, 2, 2);
performance(:,1,:) = [TL_reasoning_time, TL_query_time];
performance(:,2,:) = [SSL_reasoning_time, SSL_query_time];
plotBarStackGroups(performance, view_sub_num)
h1 = plot(view_number, TL_total_derivation_time,'g-*');
h2 = plot(view_number, SSL_total_derivation_time,'r-o');
h3 = plot(view_number, SL_total_derivation_time,'k-+');
set([h1 h2 h3],'LineWidth',1)
% bar(view_sub_num, reasoning_time,'barwidth', 1, 'basevalue', 1);
% bar(view_number, SSL_reasoning_time,'barwidth', .1, 'basevalue', 1);
% set(gca,'YScale','log')
h = legend('t_{re} of TLA', 't_{qe} of TLA', 't_{re} of SSLA', 't_{qe} of SSLA', 't_{cs} of TLA', 't_{cs} of SSLA', 't_{cs} of SLA');
set(h,'FontSize',15); 
xlabel('N_p','FontSize', 15);
ylabel('time (s)','FontSize', 15);
xt = get(gca, 'XTick');
xlim([-1, 50]);
set(gca, 'FontSize', 15)
saveas(gcf,'final_stress_test_group_full_time.png')
