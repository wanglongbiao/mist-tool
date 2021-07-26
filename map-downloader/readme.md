# 使用说明
在 client.py 中配置 url_template 为 XYZ 格式，配置文件保存路径，修改 main 函数中的参数后就可以执行了

# 功能
- 根据 XYZ 格式的源，下载瓦片。
- 自动跳过已下载的文件。
- 根据经纬度下载矩形区域。


# 问题
clash 可能导致网络请求错误

# 配置
```python
# 地名
tiandituKey = ''
tiandituCtaLayerUrl = 'http://t0.tianditu.gov.cn/cta_w/wmts?tk=' + tiandituKey + '&layer=cta&style=default&tilematrixset=w&Service=WMTS&Request=GetTile&Version=1.0.0&Format=tiles&TileMatrix={z}&TileCol={x}&TileRow={y}'

```
