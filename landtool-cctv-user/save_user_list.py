import os
import requests
import re
import json
import websockets
import asyncio

# landtool config
host = 'http://10.79.32.41:8081'
host = 'http://ld.landtool.com:8081'
# ws url
video_server_ws_uri = "ws://10.79.32.47:15554"
video_server_ws_uri = "ws://10.8.0.5:15554"

jsessionid = '08aff7de-d9db-45a9-83f2-8964f255b752'
headers = {"Content-Type": "application/json"}
headers['Cookie'] = f'JSESSIONID={jsessionid}' 
headers['Host'] = '10.79.32.41:8081'
headers['Origin'] = host
headers['Pragma'] = 'no-cache'
headers['Referer'] = 'http://10.79.32.41:8081/lanwebapp/admin/modules/org/user_edit.html'
headers['User-Agent'] = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.87 Safari/537.36'


# 1. 批量添加用户
def save_user_list():
    url = f'{host}/org/user/save'
    data = json.loads(
        '{"userstatus":3,"isreceivemsg":0,"education":"","jobtitle":"1","joblevel":""}')
    with open(r'./users.txt', encoding='utf-8') as file:
        for line in file:
            line = line.strip()
            if len(line) == 0:
                continue
            print('----------------')
            # print(str(len(line)))
            data['loginname'] = line
            data['password'] = line + "123"
            data['chinesename'] = line
            data['spellfirst'] = line
            resp = requests.post(url, json=data, headers=headers)
            print(resp.text)
            # print(json.dumps(data))


# 2. 批量关联用户和组
def save_user_group():
    url = f'{host}/org/usergroup/save'
    data = {"groupId": "79", "userId": "2043"}

    # for i in range(2035, 2123):
    with open(r'./userids.txt', encoding='utf-8') as file:
        for line in file:
            line = line.strip()
            if len(line) == 0:
                continue
            print('----------------')
            data['userId'] = line
            # print(data)
            resp = requests.post(url, json=data, headers=headers)
            print(resp.text)


# 6. 更新光电管理信息，流地址
def update_landtool_cctv_info(cctv):
    code = str(cctv['id'])
    # if code != 823:
    # 查询光电 id
    resp = requests.get(f'{host}/sys/pe_manage/info/{code}', headers=headers)
    resp_json = resp.json()
    id = resp_json['pemanage']['id']
    # db_code = resp_json['pemanage']['code']
    # print(code)
    # print(resp_json)
    update_data = {"white": "", "red": "", "altitude": 70, "linkType": "海兰信接口", "whiteSub": "",
                   "redSub": "", "hlsWhiteMain": "", "hlsWhiteSub": "", "hlsRedMain": "", "hlsRedSub": ""}
    update_data['id'] = id
    update_data['code'] = code
    update_data['white'] = cctv['whiteday']['rtmpmainurl']
    update_data['whiteSub'] = cctv['whiteday']['rtmpsuburl']
    update_data['hlsWhiteMain'] = cctv['whiteday']['httphlsmainurl']
    update_data['hlsWhiteSub'] = cctv['whiteday']['httphlsuburl']

    update_data['red'] = cctv['infrared']['rtmpmainurl']
    update_data['redSub'] = cctv['infrared']['rtmpsuburl']
    update_data['hlsRedMain'] = cctv['infrared']['httphlsmainurl']
    update_data['hlsRedSub'] = cctv['infrared']['httphlsuburl']
    print(update_data)
    resp = requests.post(
        url=f'{host}/sys/pe_manage/save', headers=headers, json=update_data)
    print(resp.text)


# 3. 连接 video server 的 ws
async def get_cctv_list_from_ws():
    send = '{"msg_type":1,"coasttype":1}'
    async with websockets.connect(video_server_ws_uri) as websocket:
        await websocket.send(send)
        print(f"> {send}")
        map = {}
        async for msg in websocket:
            # print(f"{msg}")
            cctv = json.loads(msg)
            if 'id' not in cctv:
                continue
            id = cctv['id']
            if id in map:
                print(f'keys {map.keys()}')
                print(f'ws is over, count {len(map)}..')
                break
            map[id] = cctv
            # if id != 743:
            # continue
            save_or_update_cctv(cctv)
            update_landtool_cctv_info(cctv)


# 4. 生成 nginx.conf 推流配置文件的 application 部分
def gen_nginx():
    with open('cctv-list.json', encoding='utf-8') as file:
        resp = json.load(file)
        # print(resp['data'])
        set = {0}
        for cctv in resp['data']:
            id = str(cctv['cctvId'])
            if id in set:
                continue
            set.add(id)
            id = re.sub('^7', 'sd', id)
            id = re.sub('^8', 'hb', id)
            id = re.sub('^9', 'ln', id)
            print(f'''
            application {id}_whiteday_sub {{
                live on;
            }}
            application {id}_infrared_sub {{
                live on;
            }}''')


# 5. 更新或新增光电基本信息
def save_or_update_cctv(cctv):
    code = str(cctv['id'])
    # query
    resp = requests.get(
        f'{host}/sys/pebaseinfo/info/{code}', headers=headers)
    # print(resp.text)
    data = resp.json()
    request_data = {"status": "1"}
    if data['peBaseInfo'] is not None:
        print(f'update {code}')
        objectid = data['peBaseInfo']['objectid']
        request_data['objectid'] = objectid
        print(f'objectid {objectid}')
    else:
        print(f'save {code}')
    request_data['code'] = code
    request_data['name'] = cctv['name']
    request_data['longitude'] = cctv['longitude']
    request_data['latitude'] = cctv['latitude']
    request_data['ip'] = cctv['whiteday']['ip']
    request_data['aIp'] = cctv['infrared']['ip']
    if code.startswith('7'):
        request_data['province'] = "山东省"
    if code.startswith('8'):
        request_data['province'] = "河北省"
    if code.startswith('9'):
        request_data['province'] = "辽宁省"
    # print(f'request_data {request_data}')
    resp = requests.post(f'{host}/sys/pebaseinfo/save',
                         json=request_data, headers=headers)
    print(resp.text)


# 7. 设置光电的单位权限
def set_cctv_org(cctv={'id': 0}):
    code = str(cctv['id'])
    # 获取组织机构列表
    org_list = requests.get(
        f'{host}/org/unit/queryUnitTreeList', headers=headers).json()['unitList']
    print(f'len of org_list {len(org_list)}')
    url = f'{host}/sys/pePermission/setOptoPermission'
    for org in org_list:
        org_id = org["unitid"]
        print(f'org id {org_id} name {org["unitname"]}')
        request_data = {}
        request_data['unitid'] = org_id
        request_data['peCode'] = code
        request_data['defaultLevel'] = 1
        request_data['controlLevel'] = 1
        print(f'cctv union org request_data {request_data}')
        resp = requests.post(url, headers=headers,json=request_data)
        print(f'cctv union org resp {resp.text}')


if __name__ == '__main__':
    # update_cctv()
    # fetch_cctv_ws()
    print('start..')
    # asyncio.get_event_loop().run_until_complete(get_cctv_list_from_ws())
    # set_cctv_org()
    print('end..')
    gen_nginx()
