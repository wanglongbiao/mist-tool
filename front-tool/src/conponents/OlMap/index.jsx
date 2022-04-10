import React, { Component } from 'react'
import * as ol from 'ol'
import OSM from 'ol/source/OSM';
import TileLayer from 'ol/layer/Tile';
import 'ol/ol.css';
import './index.css'

export default class OlMap extends Component {
    constructor(props) {
        super(props)
        let center = localStorage.getItem('center')
        let zoom = localStorage.getItem('zoom')
        this.state = { center: [0,0], zoom: zoom == undefined ? 5 : zoom }
        this.map = new ol.Map({
            layers: [
                new TileLayer({
                    source: new OSM(),
                }),
            ],
            target: null,
            view: new ol.View({
                center: this.state.center,
                zoom: this.state.zoom,
            }),
        });
    }
    render() {
        console.log('render..');
        return (
            <div> Ol map
                <div id="map" className="map" tabIndex="0"></div>
            </div>
        )
    }

    componentDidMount() {
        console.log('componentDidMount..');
        this.map.setTarget('map')
    }
}
