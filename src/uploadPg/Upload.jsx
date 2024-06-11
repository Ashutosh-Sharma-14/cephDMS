import Navbar from "../navbar/Navbar";
import Sidebar from "../sidebar/Sidebar";
import UploadCard from "../smallCompontes/uploadCard/UploadCard";
import '../uploadPg/upload.css'


const Upload = () =>{
    return <div className="upload">
    <div className="navbar">
        <Navbar />
    </div>

    <div className="wrapper">
        <div className="sidebar">
            <Sidebar />
        </div>
        <div className="mainUploadPg">
            <UploadCard />
            <UploadCard />
            <UploadCard />
            <UploadCard />
            <UploadCard />
            <UploadCard />
            <UploadCard />
            <UploadCard />
            <UploadCard />
            <UploadCard />


        </div>
    </div>
    </div>
}

export default Upload;