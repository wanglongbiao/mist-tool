import React, { Component } from 'react'
import { nanoid } from 'nanoid';

export default class Header extends Component {
  handleKeyUp = event => {
    let target = event.target
    if (event.keyCode != 13) return
    let value = target.value.trim()
    if (!value) return
    let todo = { id: nanoid(), name: value, done: false }
    this.props.addTodo(todo)
    target.value = ''
  }

  render() {
    return (
      <div>
        <h1>地图工具</h1>
        <input onKeyUp={this.handleKeyUp} ></input>
      </div>
    )
  }
}
