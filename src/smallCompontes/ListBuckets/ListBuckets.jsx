import axios from "axios";
import Navbar from "../../navbar/Navbar";
import Sidebar from "../../sidebar/Sidebar";
import '../../uploadPg/upload.css'
import BucketCard from "../BucketCard";
import './listBuckets.css'
import { useState } from "react";
import ReactLoading from "react-loading";
import swal from "sweetalert";

// bucket creation date 

const ListBuckets = () =>{

    const [bucketName, setBucketName] = useState([]);
    const [disabled, setDisabled] = useState(false);
    const [bucket, setBucket] = useState(''); 


    const handleBtn = async (e) =>{
        e.preventDefault();
        try{
            setDisabled(true)
            const res = await axios.get('http://localhost:8080/user/list-buckets');
            setBucketName(res.data);
            setDisabled(false)
            console.log(res.data);
        }catch(err){
            swal(
                `Error while Fetching Bucket`,
                `${err}`,
                'error'
            );
            setDisabled(false)
            console.log('message',err);
        }
    }

    const addBucket = async (e) =>{
        e.preventDefault();
        try {
            const res = await axios.put(`http://localhost:8080/user/create-bucket?bucketName=${bucket}`);
            swal(
                `Bucket is Created Successfully`,
                `${res.data}`,
                "success"
            );
            console.log(res.data);
        } catch (err) {
            swal(
                `Error while creating Bucket`,
                `${err}`,
                'error'
            );
            console.log('message', err);
        }
        
        
    }

    
    return <div className="upload">
    <div className="navbar">
        <Navbar />
    </div>
    <div className="wrapper">
        <div className="sidebar">
            <Sidebar />
        </div>
        <div className="cardWrapper">
            <div className="uploadBtn">
            <div className="addBucket font-[sans-serif] space-x-4 space-y-4 text-center">
            
            {/* input btn */}
                <label
                htmlFor="UserEmail"
                className="relative block overflow-hidden border-b border-gray-200 bg-transparent pt-3 focus-within:border-blue-600"
                >
                <input
                    type="text"
                    id="UserEmail"
                    placeholder="Email"
                    className="peer h-8 w-full border-none bg-transparent p-0 placeholder-transparent focus:border-transparent focus:outline-none focus:ring-0 sm:text-sm"
                    onChange={(e)=>setBucket(e.target.value)}
                />

                <span
                    className="absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs"
                >
                    Create New Bucket
                </span>
                </label>
                
                <button type="button"
                disabled={bucket.length < 3}
                className="addBucketBtn px-5 py-2.5 rounded-lg 
                text-sm tracking-wider font-medium border 
                border-current outline-none bg-green-700 
                hover:bg-transparent text-white hover:text-green-700 transition-all duration-300"
                onClick={addBucket}
                >
                    Add Bucket
            </button>
                

            </div>

            <button type="button"
                className="px-5 py-2.5 rounded-lg 
                text-sm tracking-wider font-medium border 
                border-current outline-none bg-green-700 
                hover:bg-transparent text-white hover:text-green-700 transition-all duration-300"
                onClick={handleBtn}
                style={{scale:disabled?'0':'1', transition:'all 1s'}}
                >
                    Fetch Buckets
            </button>
            </div>
            {
                disabled ? 
                <div className="loadingLogo">
                <ReactLoading type="cylon" color="#007bff" width={'3%'} />
                <div>Fetching...</div>
                </div>
                :
                <div className="mainUploadPg">
                    {
                        bucketName.map((item, idx) => (
                            <BucketCard key={idx} item={item}  />
                          ))
                    }
                    {/* <BucketCard /> */}
                </div>
            }
            
        </div>
    </div>
    </div>
}

export default ListBuckets;