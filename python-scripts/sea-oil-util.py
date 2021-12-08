import os
import re
import logging

logging.basicConfig(level=logging.INFO)
log = logging.getLogger()
# walk throuth sql files, get area id and area name, fill in to new file


def work():
    directory = r'C:\wang-work\doc\海管平台信息'
    for file_name in os.listdir(directory):
        if not file_name.endswith('.sql'):
            continue
        f = os.path.join(directory, file_name)
        log.info(f'file name: {f}')
        id = 1
        for line in open(f, encoding='utf-8'):
            m = re.match(".+t_area.+VALUES.+?'(\d+)'.+?'(.+?)'", line)
            if not m:
                continue
            area_id = m.group(1)
            area_name = m.group(2)
            log.info(f'id {area_id}, name {area_name}')
            sql_template = f"INSERT INTO `hscp`.`t_area_user`(`id`, `area_id`, `area_name`, `org_id`, `user_name`, `is_show`, `create_time`, `update_time`, `share_status`) VALUES ({id}, '{area_id}', '{area_name}', '2217', 'PYHY', 1, now(), now(), 3);\n"
            dest_file = os.path.join(directory, f't_area_user_{file_name}')
            with open(dest_file, encoding='utf-8', mode='a+') as out:
                if os.path.getsize(dest_file) is 0:
                    out.write('delete from hscp.t_area_user;\n')
                out.write(sql_template)
            id = id + 1





if __name__ == '__main__':
    # line = "INSERT INTO `hscp`.`t_area`(`area_id`, `area_name`, `area_type`, `area_shape`, `area_outside_color`, `area_outside_opacity`, `area_outside_style`, `area_inside_color`, `area_inside_opacity`, `area_radius`, `visual_radius`, `area_point`, `user_name`, `area_des`, `is_show`, `is_active`, `is_del`, `is_statistics`, `region_code`, `create_time`, `update_time`, `area_flashing_style`, `is_flashing`, `polygon_coords`, `line_coords`, `compress_line_coords`, `inner_buffer_size`, `out_buffer_size`, `target_type`, `ship_type`, `group_id`, `org_id`) VALUES ('1452526071202320384', 'B41-EP24-2 to PCSS1 Cable1 路由-核心区', 7, 'POLYGON', '#445DA7', '100.0', '实线', '#445DA7', '50.0', NULL, NULL, NULL, 'ycs', NULL, 0, 0, 0, 0, '[\"ShenZhenHY\"]', '2021-10-25 14:42:57', '2021-10-25 08:16:39', NULL, 0, ST_GeomFromText('POLYGON((113.86096026848 20.5150324833624, 113.860658499621 20.5158702842809, 113.860536017271 20.5167474514267, 113.860597531477 20.5176302757762, 113.860840681328 20.5184848303294, 113.861256125222 20.5192782739893, 113.861827899473 20.5199801137867, 113.862534031546 20.5205633769163, 113.863347384385 20.5210056475146, 113.864236699394 20.5212899283006, 113.865167797977 20.5214052939342, 113.866104895419 20.5213473109615, 113.867011976542 20.5211182081917, 113.867854180219 20.5207267909614, 113.868599139445 20.5201881025906, 113.869218225427 20.5195228460669, 113.869687647837 20.5187565882139, 113.878060806724 20.5013375521671, 113.878362458761 20.500499734345, 113.878484837169 20.4996225667677, 113.878423242131 20.4987397585084, 113.87818004375 20.4978852348355, 113.877764590508 20.4970918335686, 113.877192849627 20.4963900432992, 113.876486793212 20.4958068319397, 113.875673553816 20.4953646105898, 113.874784381872 20.4950803725011, 113.873853445043 20.494965040198, 113.872916515594 20.495023045821, 113.872009596157 20.4952521608034, 113.871167536633 20.4956435814299, 113.870422695312 20.496182266999, 113.8698036956 20.4968475176198, 113.869334326108 20.4976137694702, 113.86096026848 20.5150324833624))'), NULL, NULL, NULL, NULL, NULL, '', 1423672328525747918, '2217');"
    # m = re.match(".+t_area.+VALUES.+?'(\d+)'.+?'(.+?)'", line)
    # m = re.match(".+t_area", line)
    # log.info(m)
    # work()
    for f in os.listdir('.'):
        log.info(f'file: {f}')
        path = os.path.join('.', f)
        log.info(f'file2: {path}')
