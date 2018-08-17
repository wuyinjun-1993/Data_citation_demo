% file_name = 'experiments_final_stress_test_view_num_full.xlsx';
% TL_sheet_name = 'Sheet2';
% SSL_sheet_name = 'Sheet3';
% SL_sheet_name = 'Sheet4';

gap = 3;

file1 = 'final_stress_test_view_num_full_tuple_agg_intersection.csv';

file2 = 'final_stress_test_view_num_full_semi_schema_agg_intersection.csv';

file3 = 'final_stress_test_view_num_full_schema.csv';

% [TL] = csvread(file_name, TL_sheet_name);
% [SSL] = csvread(file_name, SSL_sheet_name);
% [SL] = csvread(file_name, SL_sheet_name);

[TL] = csvread(file1);
[SSL] = csvread(file2);
[SL] = csvread(file3);

view_number = TL(:,1);
TL_total_derivation_time = TL(:,2);
TL_reasoning_time = TL(1:gap:end,9);
SSL_total_derivation_time = SSL(:,2);
SSL_reasoning_time = SSL(1:gap:end,9);
SL_total_derivation_time = SL(:,2);
SL_reasoning_time = SL(1:gap:end,3);
h1 = semilogy(view_number, TL_total_derivation_time,'g-*');
hold on;
h2 = semilogy(view_number, SSL_total_derivation_time,'r-o');
h3 = semilogy(view_number, SL_total_derivation_time,'b-+');
set([h1 h2 h3],'LineWidth',1)
reasoning_time = [TL_reasoning_time, SSL_reasoning_time, SL_reasoning_time];
clr = [0 1 0; 1 0 0; 0 0 1];
colormap(clr);
view_sub_num = view_number(1:gap:end,:);

bar(view_sub_num, reasoning_time,'barwidth', 1, 'basevalue', 1);
% bar(view_number, SSL_reasoning_time,'barwidth', .1, 'basevalue', 1);
set(gca,'YScale','log')
% h = columnlegend(2,{'t_{cs} of TLA', 't_{cs} of SSLA', 't_{cs} of SLA', 't_{re} of TLA', 't_{re} of SSLA', 't_{re} of SLA'}, 'location', 'northwest');
h = legend({'t_{cs} of TLA', 't_{cs} of SSLA', 't_{cs} of SLA', 't_{re} of TLA', 't_{re} of SSLA', 't_{re} of SLA'}, 'location', 'best');
set(h,'FontSize',15); 
xlabel('N_{v}','FontSize', 15);
ylabel('time (s)','FontSize', 15);
xt = get(gca, 'XTick');
%xlim([2, 39]);
% ylim([10^-6, 10^4]);
set(gca, 'FontSize', 15)

saveas(gcf,'exp1/final_stress_test_view_num_full_time.png')

