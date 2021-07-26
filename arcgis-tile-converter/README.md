
# 转换瓦片为 Arcgis Server 可发布的格式
## 程序功能
    该程序主要用来将从船讯网下载的瓦片转换为 arcgis server 上可发布的格式

## 程序运行方法
    配置 config.properties 文件，运行 Client

## 配置说明
    源目录：
     从船讯网下载海图（需要购买），格式类似于：
      D:\_wang_work\MapFiles\00-storted-map\base\hai-nan\L8\R111-C201.png
    目标目录：
     用来存放转换完成的，可用于发布在 Arcgis Server 上的文件，格式类似于：
      D:\_wang_work\SGDownload\hai-nan_ArcgisServerTiles\_alllayers\L14\R00001C6D\C0000337A.jpg

## 使用说明
    当文件全部转换完成以后，需要：
     1. 先使用 ArcMap10.2 新建地图，导入地图，生成发布文件，主义在最后一步勾选使用缓存文件
     2. 通过 Arcgis Server Manager 网页端发布 .sd 文件，以生成 conf.cdi，Conf.xml 这些文件和相关目录
     3. 将 _alllayers 文件夹拷贝到 ArcgisServer 的缓存目录，格式类似于 C:\arcgisserver\directories\arcgiscache\MyChina\Layers\_alllayers\