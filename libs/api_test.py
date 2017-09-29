import hashlib
from wsgiref.handlers import format_date_time
from datetime import datetime
import time
from time import mktime
from collections import OrderedDict
import requests

def get_para(api_key='af09a360b7e4485b98f8bb768a38c9d8',secret='85237047-65cc-46bf-95f3-4a382faf75d1', p={}):
    md5 = hashlib.md5()
    t = int(time.time())
    tt = format_date_time(t)
    hashdata = {"api_key":api_key,"t":t}
    hashdata.update(p)
    #print(hashdata)
    hashdata = OrderedDict(sorted(hashdata.items(), key=lambda t: t[0]))
    #print(hashdata)
    hash_str = ''.join('{}={}'.format(k,v) for k,v in hashdata.items())
    md5.update((hash_str + secret).encode())
    return {'From':api_key,'Date':tt,'Authorization':md5.hexdigest(),'Api-Locale':'zh-CN'}

def get_async_key():
    para = {'from': 'ST_EZVVG1X5','to':'ST_D8NNN9ZK','date':'2017-10-20','time': '12:00','adult':1,'child':0}
    h = get_para(p=para)
    r = requests.get("https://alpha.api.detie.cn/api/v2/online_solutions", params=para, headers=h)
    print('url ',r.url)
    print('result ',r.json())
    return r.json()['async']

def recv_result(api_async):
    p = {'async_key':api_async}
    h = get_para(p=p)
    rs = requests.get("https://alpha.api.detie.cn/api/v2/async_results/{}".format(api_async), headers=h)
    #print('url ',rs.url)
    print('result ', rs.text)


key = get_async_key()

recv_result("38fe0986f02f957e4214f1fab5cddcda")