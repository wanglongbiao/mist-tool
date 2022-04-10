import React, { Component } from 'react'
import Header from './conponents/Header'
import OlMap from './conponents/OlMap'

export default class App extends Component {
  render() {
    return (
      <div>
        <Header />
        <OlMap />
      </div>
    )
  }
}
