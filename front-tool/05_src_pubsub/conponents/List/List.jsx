import React, { Component } from 'react'
import PubSub from 'pubsub-js';

export default class
    extends Component {
    state = {
        users: [],
        isFirst: true,
        isLoading: false,
        err: ''
    }

    componentDidMount(){
        console.log('subscribe.');
        PubSub.subscribe('topic1', (_, data)=>{
           this.setState(data) 
        })
    }

    render() {
        let { users, isFirst, isLoading, err } = this.state
        return (
            isFirst ? '欢迎使用' :
                isLoading ? '加载中。。。' :
                    err ? <h2>出错了。。。</h2> :
                        <div>
                            {
                                users.map(user => {
                                    return <span id={user.login}>
                                        <img src={user.avatar_url}></img>{user.login}
                                    </span>
                                })
                            }
                        </div>
        )
    }
}
