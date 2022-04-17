import React, { Component } from 'react'
import { BrowserRouter, NavLink, Route, Routes } from 'react-router-dom'
import MyNavLink from './components/MyNavLink'
import About from './pages/About'
import Home from './pages/Home'

export default class App extends Component {
  render() {
    return (
      <div className='row'>
        <BrowserRouter>
          <div>
            <MyNavLink to='/home2'>MyNavLink</MyNavLink>
            <NavLink className='list-group-item' to='/home'>Home</NavLink>
            <NavLink className='list-group-item' to='/about'>About</NavLink>
          </div>
          <div>
            <Routes>
              <Route path='/home' element={<Home />}></Route>
              <Route path='/home2' element={<Home />}></Route>
              <Route path='/about' element={<About />}></Route>
            </Routes>
          </div>
        </BrowserRouter>
      </div>
    )
  }
}
