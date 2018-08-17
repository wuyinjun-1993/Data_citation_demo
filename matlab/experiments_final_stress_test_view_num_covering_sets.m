file_name = 'experiments_final_stress_test_view_num_full.xlsx';
TL_sheet_name = 'Sheet2';
SL_sheet_name = 'Sheet4';
[TL] = csvread('final_stress_test_view_num_full_semi_schema_agg_intersection.csv');
[SL] = csvread('final_stress_test_view_num_full_schema.csv');
view_number = TL(:,1);
TL_total_derivation_time = TL(:,6);
citation_generation_time = SL(:, 4);


figure
yyaxis left
h1 = plot(view_number, TL_total_derivation_time,'b-+');
% h4 = plot(view_number, TL_covering_set_num,'-*');
xlabel('N_v','FontSize', 15);
ylabel('N_{cs}','FontSize', 15);
hold on;
yyaxis right
h5 = plot(view_number, citation_generation_time, '-o');
ylabel('t_{cg} (s)','FontSize', 15);
set([h1, h5],'LineWidth',2)
h = legend('N_{cs}', 't_{cg}', 'Location','northwest');
set(h,'FontSize',18); 
hold on;
% set([h1 h2 h3],'LineWidth',1)
% reasoning_time = [TL_reasoning_time, SSL_reasoning_time, SL_reasoning_time];
% clr = [0 1 0; 1 0 0; 0 0 1];
% colormap(clr);
% view_sub_num = view_number(1:3:end,:);
% 
% bar(view_sub_num, reasoning_time,'barwidth', 1, 'basevalue', 1);
% % bar(view_number, SSL_reasoning_time,'barwidth', .1, 'basevalue', 1);
% set(gca,'YScale','log')
% h = legend({'total derivation time of TL', 'total derivation time of SSL', 'total derivation time of SL', 'reasoning time of TL', 'reasoning time of SSL', 'reasoning time of SL'}, 'Position',[0.2 0.6 0.1 0.2]);
% set(h,'FontSize',12); 
% xlabel('N_{v}','FontSize', 15);
% ylabel('N_{cs}','FontSize', 15);
xt = get(gca, 'XTick');
%xlim([2, 39]);
set(gca, 'FontSize', 15)
saveas(gcf,'exp1/final_stress_test_view_num_full_covering_set_num.png')
