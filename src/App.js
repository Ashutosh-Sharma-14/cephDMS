import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from '../src/homePage/Home'
import Upload from "./uploadPg/Upload";

import './app.css'
import Login from "./loginPg/Login";
import Signup from "./SignUp/Signup";
import Download from "./downloadPg/Download";
import ListBuckets from "./smallCompontes/ListBuckets/ListBuckets";
import ListObject from "./smallCompontes/ListObject/ListObject";
import { initialState, reducer } from '../src/Jsons/reducer';
import { useReducer } from "react";
// import { initialState } from "./Jsons/initialState";


const App = () =>{

  const [state, dispatch] = useReducer(reducer, initialState);
  return <>
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home/>} />
          <Route path="/upload" element={<Upload />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/download" element={<Download />} />
          <Route path="/list-buckets" element={<ListBuckets />} />
          <Route path="/list-object" element={<ListObject />} />



        </Routes>
      </BrowserRouter>
    </div>
  </>
}


export default App;