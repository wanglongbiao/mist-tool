import React, { Component } from 'react'
import { BrowserRouter, Link, Route, Routes } from 'react-router-dom'
import About from './pages/About'
import Home from './pages/Home'

export default class App extends Component {
  render() {
    return (
      <div className='container'>
        <BrowserRouter>
          <div>
            <Link to='/home'>Home</Link>
            <Link to='/about'>About</Link>
          </div>
          <div>
            <Routes>
              <Route path='/home' element={<Home />}></Route>
              <Route path='/about' element={<About />}></Route>
            </Routes>
          </div>
        </BrowserRouter>
      </div>
    )
  }
}
