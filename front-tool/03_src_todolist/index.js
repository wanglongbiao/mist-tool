import React from "react";
import ReactDOM from "react-dom";
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App'

let container = document.getElementById('root')
let root = createRoot(container)
root.render(<App />)

