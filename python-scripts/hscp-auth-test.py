import json
import requests
from requests.auth import HTTPBasicAuth
import logging
import time
from datetime import date, datetime
logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)5s - %(message)s")
# logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(levelname)5s - %(message)s - %(pathname)s:%(lineno)d")
log = logging.getLogger(__name__)


def do_test():
    # 先请求登录接口 token
    # 再定时请求 check 接口，如果离过期还有 1 分钟，则请求一次刷新接口
    log.info('start...')
    login_url = 'http://10.100.0.122/api/uaa/oauth/token?grant_type=password&username=wanglongbiao&password=wanglongbiao&scope=all&client_id=client&tenantCode=10001&type=account'
    resp = requests.get(url=login_url, auth=HTTPBasicAuth('client', 'secret'))
    if resp.status_code != 200:
        log.error(resp.json())
        return
    r = resp.json()
    log.info(f'resp: {r}')
    access_token = r['access_token']
    refresh_token = r['refresh_token']
    headers = {}
    while True:
        check_token_url = f'http://10.100.0.122/api/uaa/oauth/check_token?token={access_token}'
        refresh_token_url = f'http://10.100.0.122/api/uaa/oauth/token?grant_type=refresh_token&refresh_token={refresh_token}'
        headers['Authorization'] = f'Bearer {access_token}'
        check_resp = requests.get(url=check_token_url)
        if check_resp.status_code != 200:
            log.error(check_resp.json())
            break
        exp = check_resp.json()["exp"]
        log.info(f'check token success, token {access_token}, refresh {refresh_token} exp: {exp}') 
        time.sleep(30)
        if time.time() > exp - 60:
            refresh_resp = requests.get(refresh_token_url,auth=HTTPBasicAuth('client', 'secret'))
            if refresh_resp.status_code != 200:
                log.error(refresh_resp.json())
                continue
            refresh_r = refresh_resp.json()
            log.info(f'refresh token sucess, old: {access_token} new: {refresh_r["access_token"]}')
            access_token = refresh_r['access_token']
            refresh_token = refresh_r['refresh_token']
            # log.info(refresh_resp.json())
    log.info('end...')
    pass




if __name__ == '__main__':
    do_test()