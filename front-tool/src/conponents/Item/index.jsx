import React, { Component } from 'react'

export default class Item extends Component {
    state = {}
    render() {
        const { id, name, done } = this.props
        return (
            <li style={{backgroundColor: this.state.hover ? '#ddd' : 'white' }} onMouseEnter={this.handleMourse} onMouseLeave={this.handleMourse}>
                <label>
                    <input type='checkbox' defaultChecked={done} />
                    <span>{name}</span>
                </label>
                <button style={{ display: this.state.hover ? 'inline' : 'none' }}>删除</button>
            </li>
        )
    }

    handleMourse = event => {
        console.log(event);
        switch (event.type) {
            case 'mouseenter':
                this.setState({ hover: true })
                break
            case 'mouseleave':
                this.setState({ hover: false })
                break
        }
    }
}
