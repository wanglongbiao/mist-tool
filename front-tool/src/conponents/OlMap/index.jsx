import React, { Component } from 'react'
import * as ol from 'ol'
import { OSM, TileDebug, XYZ, Vector as VectorSource } from 'ol/source'
import TileLayer from 'ol/layer/Tile'
import { fromLonLat, toLonLat } from 'ol/proj'
import MousePosition from 'ol/control/MousePosition'
import { toStringXY, toStringHDMS } from 'ol/coordinate'
import { ScaleLine } from 'ol/control';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import VectorLayer from 'ol/layer/Vector';
import { Circle } from 'ol/geom';
import WKT from 'ol/format/WKT';
import 'ol/ol.css';
import './index.css'
export default class OlMap extends Component {
    constructor(props) {
        super(props)
        let center = localStorage.center ? localStorage.center.split(',') : [130, 20]
        let zoom = localStorage.zoom ? localStorage.zoom : 3
        this.state = { center: center, zoom: zoom }
        let osmTileLayer = new TileLayer({
            source: new OSM(),
        })
        let debugLayer = new TileLayer({
            source: new TileDebug(),
        })
        let xyzTileLayer = new TileLayer({
            source: new XYZ({ url: 'http://localhost/land-map/{z}/{x}/{y}.png' }),
        })
        this.vectorLayer = new VectorLayer({
            source: new VectorSource()
        })
        this.map = new ol.Map({
            layers: [
                osmTileLayer,
                this.vectorLayer,
                debugLayer
            ],
            target: null,
            view: new ol.View({
                center: fromLonLat(this.state.center),
                zoom: this.state.zoom,
            }),
        });
    }
    render() {
        console.log('render..');
        return (
            <div> Ol map
                <div id="map" className="map"></div>
            </div>
        )
    }

    componentDidMount() {
        console.log('componentDidMount..');
        this.map.setTarget('map')

        // 显示经纬度
        var mousePositionControl = new MousePosition({
            coordinateFormat: (c) => toStringXY(c, 6),
            projection: 'EPSG:4326',
            className: 'custom-mouse-position',
            target: 'mouse-position',
            undefinedHTML: ''
        })
        this.map.addControl(mousePositionControl)
        // 记住最后位置和缩放级别
        this.map.on('moveend', function (e) {
            localStorage.zoom = e.target.getView().getZoom()
            localStorage.center = toLonLat(e.target.getView().getCenter())
            // vectorSource.clear()
            // vectorSource.addFeature(new Feature(new Point(map.getView().getCenter())))
        })
        // 添加比例尺
        this.map.addControl(new ScaleLine({ units: 'metric' }))

        // 添加原点
        // vectorSource.addFeature(new Feature(new Point([0, 0])))
        // vectorSource.addFeature(new Feature(new Point(fromLonLat([120.317777, 26.948889]))))
        // vectorSource.addFeature(new Feature(new Point(fromLonLat([120.318, 26.948889]))))
        this.addWkt()
    }

    addWkt = wkt => {
        let format = new WKT()
        let option = {
            dataProjection: 'EPSG:4326',
            featureProjection: 'EPSG:3857'
        }
        let wkt1 = 'POLYGON ((108.91296386718746 18.2552737236136, 109.09835815429683 18.2927646679165, 109.1457366943359 18.198532912099026, 108.99261474609371 18.14079485121475, 108.86936187744138 18.116323816056436, 108.91296386718746 18.2552737236136))'
        let feature = format.readFeature(wkt1, option)
        this.vectorLayer.getSource().addFeature(feature)
    }
}
