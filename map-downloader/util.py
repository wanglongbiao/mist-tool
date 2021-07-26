import math


def deg2num(longitude, latitude, zoom):
    # print('zoom {} lon {} lat {}'.format(zoom, longitude, latitude))
    lat_rad = math.radians(latitude)
    n = 2.0 ** zoom
    xtile = int((longitude + 180.0) / 360.0 * n)
    ytile = int((1.0 - math.asinh(math.tan(lat_rad)) / math.pi) / 2.0 * n)
    print('zoom {} x {} y {}'.format(zoom, xtile, ytile))
    return (xtile, ytile)


def num2deg(xtile, ytile, zoom):
    n = 2.0 ** zoom
    longitude = xtile / n * 360.0 - 180.0
    lat_rad = math.atan(math.sinh(math.pi * (1 - 2 * ytile / n)))
    latitude = math.degrees(lat_rad)
    return (longitude, latitude)





if __name__ == '__main__':
    for zoom in range(8,13):
        # 海南
        download_by_rectangle(105,22,112,17,zoom)
        # deg2num(105, 22, zoom)
        # deg2num(112, 17, zoom)
