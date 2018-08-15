
file1 = 'final_stress_test_view_num_min_tuple_agg_intersection.csv';

file2 = 'final_stress_test_view_num_min_semi_schema_agg_intersection.csv';

file3 = 'final_stress_test_view_num_min_schema.csv';

[TL] = csvread(file1);
[SSL] = csvread(file2);
[SL] = csvread(file3);

view_number = TL(:,1);
TL_total_derivation_time = TL(:,3);
TL_reasoning_time = TL(1:3:end,1);
SSL_total_derivation_time = SSL(:,3);
SSL_reasoning_time = SSL(1:3:end,1);
SL_total_derivation_time = SL(:,2);
SL_reasoning_time = SL(1:3:end,3);
h1 = plot(view_number, TL_total_derivation_time,'g-*');
hold on;
h2 = plot(view_number, SSL_total_derivation_time,'r-o');
h3 = plot(view_number, SL_total_derivation_time,'b-+');
set([h1 h2 h3],'LineWidth',2)
reasoning_time = [TL_reasoning_time, SSL_reasoning_time, SL_reasoning_time];
clr = [0 1 0; 1 0 0; 0 0 1];
colormap(clr);
view_sub_num = view_number(1:3:end,:);

% bar(view_sub_num, reasoning_time,'barwidth', 1, 'basevalue', 1);
% bar(view_number, SSL_reasoning_time,'barwidth', .1, 'basevalue', 1);
% set(gca,'YScale','log')
h = legend({'t_{cs} of TLA', 't_{cs} of SSLA', 't_{cs} of SLA'},'Location','southwest');
set(h,'FontSize',15); 
xlabel('N_v','FontSize', 15);
ylabel('time (s)','FontSize', 15);
xt = get(gca, 'XTick');
xlim([2, 39]);
ylim([0, 30]);
set(gca, 'FontSize', 15)

saveas(gcf,'exp1/final_stress_test_view_num_min_time.png')
