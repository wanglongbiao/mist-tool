#!/usr/bin/python3

import re
import time
import os
import requests
import util
import logging
from config import config
import json

logging.basicConfig(level=logging.INFO,
                    format='%(asctime)s %(levelname)s - %(message)s - "%(pathname)s:%(lineno)d"')
log = logging.getLogger(__name__)
log.info('start')
proxy = '10.100.2.16:7890'
proxies = {
    'http': f'http://{proxy}',
    'https': f'https://{proxy}'
}

url_template = config['tianditu_image']
url_template = config['seamap_water']

# 目标文件夹
root = config['dest_path']
should_sleep = url_template.find('tiandi') > -1
host = re.search('//(.+?)/', url_template).group(1)
s = requests.session()
sleep_in_second = 1


def download(start_zoom, end_zoom, start_x=0, start_y=0, end_x=0, end_y=0):
    # 根据条件, 下载瓦片, 是程序的入口
    while start_zoom <= end_zoom:
        max_x = 2 ** start_zoom if end_x == 0 else end_x + 1
        max_y = 2 ** start_zoom if end_y == 0 else end_y + 1
        start_x = 0 if start_x == 0 else start_x
        for x in range(start_x, max_x):
            path = root.format(str(start_zoom), str(x))
            os.makedirs(path, exist_ok=True)
            start_y = 0 if start_y == 0 else start_y
            for y in range(start_y, max_y):
                save_file(start_zoom, x, y, path)
        start_zoom = start_zoom + 1


def save_file(z, x, y, path):
    # 将文件保存到本地
    global sleep_in_second
    global url_template
    filename = path + str(y) + '.png'
    if os.path.exists(filename):
        with open(filename, 'rb') as f:
            hex = f.read(1).hex()
            if hex == '89' or hex == 'ff':
                log.info(f'skip {filename}')
                return
            else:
                log.info(f'file {filename} existed, but is not png!')

    url = url_template.replace('$z', str(z)).replace(
        '$x', str(x)).replace('$y', str(y))
    headers = {"Accept": "image/avif,image/webp,image/apng,image/*,*/*;q=0.8",
               "Accept-Encoding": "gzip, deflate",
               "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8,en-US;q=0.7,zh-HK;q=0.6",
               "Cache-Control": "no-cache",
               "DNT": "1",
               "Host": host,
               "Pragma": "no-cache",
               "Proxy-Connection": "keep-alive",
               "User-Agent": "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36"}
    r = s.get(url, headers=headers, stream=True)
    if should_sleep:
        time.sleep(0.5)
    log.info(f'get {url}')
    if r.status_code != 200:
        sleep_in_second = sleep_in_second * 2
        # log.error(
        #     f'status code != 200, sleep {sleep_in_second}s {r.reason}')
        # time.sleep(sleep_in_second)
        # save_file(z, x, y, path)
        return

    with open(filename, 'wb') as fd:
        for chunk in r.iter_content(chunk_size=256):
            fd.write(chunk)


def download_by_rectangle(top_left_x, top_left_y, right_bottom_x, right_bottom_y, start_zoom, end_zoom):
    while start_zoom <= end_zoom:
        top_left = util.deg2num(top_left_x, top_left_y, start_zoom)
        right_bottom = util.deg2num(right_bottom_x, right_bottom_y, start_zoom)
        download(start_zoom, start_zoom,
                 top_left[0], top_left[1], right_bottom[0], right_bottom[1])
        start_zoom = start_zoom + 1


if __name__ == '__main__':
    # 下载 0 ~ 9
    # download(9, 9)
    # download_by_rectangle(104.366363, 41.772887, 126.82069, 14.746151, 11, 13)
    # download_by_rectangle(104.366363, 41.772887, 126.82069, 14.746151, 14, 14)
    # 下载一部分, 适合中间中断后, 继续下载, 现在有了判断文件是否已存在, 这个用法可以忽略
    # download(8, 8, 73, 62)
    # 大半个中国, 104.366363,41.772887,126.82069,14.746151
    # download(7, 7)
    # download_by_rectangle(104.366363, 41.772887, 126.82069, 14.746151, 13,13)
    # 渤海,
    # download_by_rectangle(116.486458, 41.39718, 122.902474, 35.411182, 14, 18)
    # download_by_rectangle(104.366363, 41.772887, 126.82069, 14.746151, 12, 12)
    # 福建
    # download_by_rectangle(116.63873, 26.292242, 121.050683, 22.690904, 16, 17)
    # 天津中海油
    download(12, 12)
    download_by_rectangle(113, 42, 128, 31, 13, 16)
    print('------------------------------------------------------')
