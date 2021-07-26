import datetime
import os

# print(str_from_date)
start_date = '2018-07-03'
end_date = '2019-10-01'

start = datetime.datetime.strptime(start_date, '%Y-%m-%d')
end = datetime.datetime.strptime(end_date, '%Y-%m-%d')

while start < end:
    start = start + datetime.timedelta(days=1)
    os.system('mysql -uroot -pAdmin123 gateway_target -e "alter table t_target_snapshot drop PARTITION p_t_target_snapshot_%s"' % start.strftime('%Y%m%d'))
    # print('mysql -uroot -pAdmin123 gateway_target -e "alter table t_target_snapshot drop PARTITION p_t_target_snapshot_%s"' % start.strftime('%Y%m%d'))
