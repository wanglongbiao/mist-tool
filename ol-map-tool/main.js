import 'ol/ol.css'
import Map from 'ol/Map'
import { OSM, TileDebug, XYZ } from 'ol/source'
import TileLayer from 'ol/layer/Tile'
import View from 'ol/View'
import { fromLonLat, toLonLat } from 'ol/proj'
import MousePosition from 'ol/control/MousePosition'
import { toStringXY, toStringHDMS } from 'ol/coordinate'
import { ScaleLine } from 'ol/control';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import VectorSource from 'ol/source/Vector';
import VectorLayer from 'ol/layer/Vector';
import { Circle } from 'ol/geom';
import WKT from 'ol/format/WKT';

// 初始化位置和缩放
let lastZoom = localStorage.lastZoom ? localStorage.lastZoom : 0
let lastCenter = localStorage.lastCenter ? localStorage.lastCenter.split(',') : [120, 30]
lastCenter = [113.45201845355483,22.119551510037564]
lastZoom = 12

console.log("last zoom " + lastZoom);
console.log("last center  " + lastCenter);
// 本机地图
let localMapUrl = 'http://localhost/land-map/{z}/{x}/{y}.png' // zoom/col/row
// 海图海面
let seaMapWatersLayerUrl = 'http://10.102.0.8:8000/ecdis-data/WMTS/Waters/Symbolized/WebMercator/{z}/{y}/{x}.png'
// 海图陆面
let seaMapTerrainLayerUrl = 'http://10.102.0.8:8000/ecdis-data/WMTS/Terrain/Symbolized/WebMercator/{z}/{y}/{x}.png'
// 天地图-地图
let tiandituVectorLayerUrl = 'http://10.102.0.8:8000/tianditu/land/{z}/{x}/{y}.png'
// 天地图-卫星
let tiandituImageLayerUrl = 'http://10.102.0.8:8000/tianditu/satellite/{z}/{x}/{y}.png'
// 天地图-地名
let tiandituCvaLayerUrl = 'http://10.102.0.8:8000/tianditu/name/{z}/{x}/{y}.png'

let localSeaMapUrl = 'http://localhost/{z}/{x}/{y}.png'


let vectorSource = new VectorSource()
let osmTileLayer = new TileLayer({
    source: new OSM(),
    // visible: false
})
let debugTileLayer = new TileLayer({
    source: new TileDebug(),
})
let localSeaMapLayer = new TileLayer({
    source: new XYZ({ url: localSeaMapUrl }),
})
let satelliteMapLayer = new TileLayer({
    source: new XYZ({ url: tiandituImageLayerUrl }),
})
let vectorLayer = new VectorLayer({ source: vectorSource })
// , new TileLayer({
//     source: new XYZ({ url: tiandituImageLayerUrl }),
// })
// , new TileLayer({
//     source: new XYZ({ url: tiandituVectorLayerUrl }),
// })
// , new TileLayer({
//     source: new XYZ({ url: seaMapTerrainLayerUrl }),
// })

var map = new Map({
    layers: [
        osmTileLayer
        // , localSeaMapLayer
        , satelliteMapLayer
        , vectorLayer
        , debugTileLayer
    ],
    target: 'map',
    view: new View({
        zoom: lastZoom,
        center: fromLonLat(lastCenter),
        constrainResolution: true,
        minZoom: 0
    }),
})

// 显示经纬度
var mousePositionControl = new MousePosition({
    coordinateFormat: (c) => toStringXY(c, 6),
    projection: 'EPSG:4326',
    className: 'custom-mouse-position',
    target: 'mouse-position',
    undefinedHTML: ''
})
map.addControl(mousePositionControl)

// 记住最后位置和缩放级别
map.on('moveend', function (e) {
    localStorage.lastZoom = map.getView().getZoom()
    localStorage.lastCenter = toLonLat(map.getView().getCenter())
    // vectorSource.clear()
    // vectorSource.addFeature(new Feature(new Point(map.getView().getCenter())))
})

// 添加比例尺
map.addControl(new ScaleLine({ units: 'metric' }))

// 添加原点
vectorSource.addFeature(new Feature(new Point([0, 0])))
vectorSource.addFeature(new Feature(new Point(fromLonLat([120.317777, 26.948889]))))
vectorSource.addFeature(new Feature(new Point(fromLonLat([120.318, 26.948889]))))

// test circle
function addTestCircle() {
    let c1 = new Circle(fromLonLat([120, 38.37]), 5000)
    let c2 = c1.clone()
    // c2.transform('EPSG:3857', "EPSG:4326")

    let circleFeature = new Feature({
        geometry: c1,
    });
    // let circleFeature2 = new Feature({
    //     geometry: c2,
    // });

    vectorSource.addFeature(circleFeature)
    // vectorSource.addFeature(circleFeature2)
}
addTestCircle()

// test wkt
function testWkt() {
    let format = new WKT()
    let option = {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857'
    }
    // let lineFeature = format.readFeature(pl19.geoCoords, option)
    // let innerFeature = format.readFeature(pl19.innerBufferGeoCoords, option)
    // let outerFeature = format.readFeature(pl19.outerBufferGeoCoords, option)
    // let totalFeature = format.readFeature(pl19.totalInnerBuffer, option)
    // let totalOuterFeature = format.readFeature(pl19.totalOuterBuffer, option)
    let wkt1 = 'POLYGON ((108.91296386718746 18.2552737236136, 109.09835815429683 18.2927646679165, 109.1457366943359 18.198532912099026, 108.99261474609371 18.14079485121475, 108.86936187744138 18.116323816056436, 108.91296386718746 18.2552737236136))'

    let bufferWktFeature = format.readFeature(wkt1, option)
    // let lineWktFeature = format.readFeature(lineWkt, option)
    vectorSource.addFeatures([bufferWktFeature])
    // vectorSource.addFeatures([lineWktFeature])
}
testWkt()