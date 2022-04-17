import React, { Component } from 'react'
import axios from 'axios';
import PubSub from 'pubsub-js';

export default class Search extends Component {
    render() {
        return (
            <div>
                <input ref={c => this.keyword = c}></input>
                <button onClick={this.doSearch}>查询 github 用户</button>
            </div>
        )
    }

    doSearch = () => {
        PubSub.publish('topic1', {
            isFirst: false,
            isLoading: true
        })
        let url = 'https://api.github.com/users?q=' + this.keyword.value
        axios.get(url)
            .then(
                response => {
                    console.log('success', response.data);
                    PubSub.publish('topic1', {
                        isLoading: false,
                        users: response.data
                    })
                },
                error => {
                    console.log('failed', error);
                    PubSub.publish('topic1', {
                        isLoading: false,
                        err: error
                    })
                }
            )
    }
}
