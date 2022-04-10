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
        let vectorSource = new VectorSource()
        let osmTileLayer = new TileLayer({
            source: new OSM(),
        })
        let debugTileLayer = new TileLayer({
            source: new TileDebug(),
        })
        let xyzTileLayer = new TileLayer({
            source: new XYZ({ url: 'http://localhost/land-map/{z}/{x}/{y}.png' }),
        })
        this.map = new ol.Map({
            layers: [
                osmTileLayer,
                debugTileLayer
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
    }
}
