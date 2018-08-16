
file1 = 'final_stress_test_instance_size_full_tuple_agg_intersection.csv';

file2 = 'final_stress_test_instance_size_full_semi_schema_agg_intersection.csv';

file3 = 'final_stress_test_instance_size_full_schema.csv';

[TL] = csvread(file1);
[SSL] = csvread(file2);
[SL] = csvread(file3);





view_number = TL(:,1);
TL_total_derivation_time = TL(:,3);
TL_reasoning_time = TL(:,9);
SSL_total_derivation_time = SSL(:,3);
SSL_reasoning_time = SSL(:,9);
SL_total_derivation_time = SL(:,2);
SL_reasoning_time = SL(:,3);
h1 = semilogx(view_number, TL_total_derivation_time,'g-*');
hold on;
h2 = semilogx(view_number, SSL_total_derivation_time,'r-o');
h3 = semilogx(view_number, SL_total_derivation_time,'b-+');
set([h1 h2 h3],'LineWidth',2)
reasoning_time = [TL_reasoning_time, SSL_reasoning_time];
clr = [0 1 0; 1 0 0; 0 0 1];
colormap(clr);
view_sub_num = view_number;

% bar(view_sub_num, reasoning_time,'width', 1, 'barwidth', 500);
% bar(view_number, SSL_reasoning_time,'barwidth', .1, 'basevalue', 1);
% set(gca,'XScale','log')
h = legend({'t_{cs} of TLA', 't_{cs} of SSLA', 't_{cs} of SL'}, 'Location', 'best');
set(h,'FontSize',15); 
xlabel('N_t','FontSize', 25);
ylabel('t_{cs}(s)','FontSize', 25);
xt = get(gca, 'XTick');
xlim([50, 10^7]);
ylim([0 160]);
set(gca, 'FontSize', 15)
saveas(gcf,'exp3/final_stress_test_instance_size_full_time.png')
