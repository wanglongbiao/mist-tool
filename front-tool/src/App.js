import React, { Component } from 'react'
import Header from './conponents/Header'
import List from './conponents/List'
import OlMap from './conponents/OlMap'

export default class App extends Component {
  state = {
    todos: [
      { id: 100, name: 'eat', done: true },
      { id: 101, name: 'sleep', done: true },
      { id: 102, name: 'coding', done: false },
    ]
  }
  render() {
    return (
      <div>
        <Header addTodo={this.addTodo} />
        <List todos={this.state.todos} />

        {/* <OlMap /> */}
      </div>
    )
  }

  addTodo = (todo) => {
    this.setState({ todos: [todo, ...this.state.todos] })
  }
}
