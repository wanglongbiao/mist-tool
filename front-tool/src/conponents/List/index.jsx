import React, { Component } from 'react'
import Item from '../Item'

export default class List extends Component {

  render() {
    const { todos, deleteTodo , updateTodo} = this.props
    return (
      <div>
        <ul>
          {
            todos.map(todo => {
              return <Item key={todo.id} {...todo} deleteTodo={deleteTodo} updateTodo={updateTodo} />
            })
          }
        </ul>
        <input />
      </div>
    )
  }

}
