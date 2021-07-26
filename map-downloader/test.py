import re

url_template = 'http://t0.tianditu.gov.cn/vec_w/wmts?tk=36d4e0be0e5f5ee7271a321f11a64d3b&layer=vec&style=default&tilematrixset=w&Service=WMTS&Request=GetTile&Version=1.0.0&Format=tiles&TileMatrix={z}&TileCol={x}&TileRow={y}'

server = re.search(r't(\d)', url_template).group(1)
server = (int(server) + 1) % 8

url_template = re.sub(r't\d', 't' + str(server), url_template)

sum_png = 0
for zoom in range(20):
    row = 2 ** zoom
    count = 2 ** (zoom * 2)
    sum_png += count
    print(f'zoom:{zoom}\t目录-列（x）:{row} \t文件-行（y）:{row}\t本级文件总数:{count:,}\t 所有文件总数:{sum_png:,}')
