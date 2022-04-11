import React, { Component } from 'react'

export default class Item extends Component {
    render() {
        const { id, name, done } = this.props
        return (
            <li>
                <label>
                    <input type='checkbox' defaultChecked={done} />
                    <span>{name}</span>
                </label>
                <button>删除</button>
            </li>
        )
    }
}
