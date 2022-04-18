import React, { Component } from 'react'
import { BrowserRouter, NavLink, Route, Routes} from 'react-router-dom'
import Header from './components/Header'
import Test from './components/Test'
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
            <NavLink className='list-group-item' to='/lsbc/home'>Home</NavLink>
            <NavLink className='list-group-item' to='/about'>About</NavLink>
            <NavLink className='list-group-item' to='/test'>Test</NavLink>
            <NavLink className='list-group-item' to='/header'>Header</NavLink>
          </div>
          <div>
            <Routes>
              <Route path='/lsbc/home' element={<Home />}></Route>
              <Route path='/test/a/b' element={<Test />}></Route>
              <Route path='/about' element={<About />}></Route>
              <Route path='/header' element={<Header />}></Route>
            </Routes>
          </div>
        </BrowserRouter>
      </div>
    )
  }
}
