import React, { Component } from 'react'
import List from './conponents/List/List';
import Search from './conponents/Search/Search';
export default class App extends Component {
    render() {
        return (
            <div>
                <Search />
                <List />
            </div>
        );
    }

    getUserData = () => {

    }
}
