import React, { Component } from 'react'

export default class Footer extends Component {
  render() {
    let { todos } = this.props
    let count1 = todos.reduce((pre, curr) => pre + (curr.done ? 1 : 0), 0)
    return (
      <div>
        <lable>
          <input type='checkbox' checked={count1 == todos.length} onChange={this.handleCheckAll} /> 已选{count1} / 总数 {todos.length}
        </lable>
      </div>
    )
  }

  handleCheckAll = (event) => {
    this.props.toggleCheckAll(event.target.checked)
  }
}
