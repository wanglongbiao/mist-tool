import React, { Component } from 'react'
import Header from './conponents/Header'
import List from './conponents/List'
import Footer from './conponents/Footer'
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
        <List todos={this.state.todos} deleteTodo={this.deleteTodo} updateTodo={this.updateTodo} />
        <Footer />

        {/* <OlMap /> */}
      </div>
    )
  }

  addTodo = (todo) => {
    this.setState({ todos: [todo, ...this.state.todos] })
  }

  deleteTodo = (id) => {
    let newList = this.state.todos.filter(todo => {
      if (todo.id != id) return todo
    })
    this.setState({ todos: newList })
  }

  updateTodo = (id, done) => {
    let newList = this.state.todos.map(todo => {
      return { ...todo, done: todo.id == id ? done : todo.done }
    })
    this.setState({ todos: newList })
  }
}
