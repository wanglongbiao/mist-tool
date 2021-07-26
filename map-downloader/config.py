

token = '36d4e0be0e5f5ee7271a321f11a64d3b'  # me
token = '99a08f767025604d8cbb9da28b0f6828'  # shipxy 2021.05
config = {
    # 天地图-卫星
    'tianditu_image': f'http://t0.tianditu.gov.cn/img_w/wmts?tk={token}&LAYER=img&STYLE=default&TILEMATRIXSET=w&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&FORMAT=tiles&TILEMATRIX=$z&TILECOL=$x&TILEROW=$y',
    # 天地图-地图
    'tianditu_land': f'http://t7.tianditu.gov.cn/vec_w/wmts?tk={token}&SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=vec&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&&TILEMATRIX=$z&TILECOL=$x&TILEROW=$y',
    
    # 天地图-名称
    # 'tianditu_name': 'http://t0.tianditu.gov.cn/cva_w/wmts?tk=36d4e0be0e5f5ee7271a321f11a64d3b&layer=cva&style=default&tilematrixset=w&Service=WMTS&Request=GetTile&Version=1.0.0&Format=tiles&TileMatrix=$z&TileCol=$x&TileRow=$y',
    # 谷歌卫星
    # 'google_satellite': 'https://khms2.google.com/kh/v=894?x=$x&y=$y&z=$z',
    # 本地海图-海面
    'seamap_water': 'http://10.102.0.8:8000/WMTS/Waters/Symbolized/WebMercator/$z/$y/$x.png',
    # 本地海图-陆地
    'seamap_terrain': 'http://10.102.0.8:8000//WMTS/Terrain/Symbolized/WebMercator/$z/$y/$x.png',
    # 目标路径
    # 'dest_path': r'd:/tile-data/tian-jin/sea-water/'
    # 'dest_path': r'../tianditu/satellite/{}/{}/'
    'dest_path': r'd:/data/tian-jin-map/water/{}/{}/',
    'dest_path_terrian': r'd:/data/tian-jin-map/terrian/{}/{}/'
}
