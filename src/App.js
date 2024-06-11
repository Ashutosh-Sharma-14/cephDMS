import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from '../src/homePage/Home'
import Upload from "./uploadPg/Upload";

import './app.css'
import Login from "./loginPg/Login";
import Signup from "./SignUp/Signup";

const App = () =>{
  return <>
    <div>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home/>} />
          <Route path="/upload" element={<Upload />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />

        </Routes>
      </BrowserRouter>
    </div>
  </>
}


export default App;