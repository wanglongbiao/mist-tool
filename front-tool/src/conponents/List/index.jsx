import React, { Component } from 'react'
import Item from '../Item'

export default class List extends Component {

  render() {
    const { todos } = this.props
    return (
      <div>
        <ul>
          {
            todos.map(todo => {
              return <Item key={todo.id} {...todo} />
            })
          }
        </ul>
        <input />
      </div>
    )
  }

}
