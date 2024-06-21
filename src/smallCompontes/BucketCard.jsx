
import { useState } from 'react';
import './bucketCard.css'
import axios from 'axios';
import ReactLoading from "react-loading";
import swal from 'sweetalert';

const BucketCard = ({item}) =>{
    const [enable,setEnable] = useState(false);
    const handleVersionButton =  async (e) =>{
        e.preventDefault();

        try{
            const uri = `http://localhost:8080/user/enable-versioning?bucketName=${encodeURIComponent(item)}`
            const res = await axios.post(uri);
            setEnable(res.data);
            swal(
                 enable ? `Bucket versioning enabled`: `Bucket versioning is already enabled`, 
                 enable ? `Bucket Name : ${item}`: `Bucket Name : ${item}`, 
                 enable ? "success" : "info");
            console.log(res);
        }catch(err){
            console.log('message',err);
        }
    }


    return <div className='bucketCard'>
            {
            // enable ? <div className='loadingLogo'>
            //     <ReactLoading type='bars' color="#007bff"/>
            //     <div className="div">Enabling...</div>
            // </div>
            // :
                <div class="shadow-[0_5px_14px_-4px_rgba(0,0,0,0.3)] p-6 w-full max-w-sm rounded-lg font-[sans-serif] overflow-hidden mx-auto mt-4">
        <div class="flex items-center">
            <h3 class="text-2xl font-semibold  flex-1">{item}</h3>
            <div class=" w-12 h-12 p-1 flex items-center justify-center rounded-full cursor-pointer">
            <img src="/bucket.png" alt="" />
            </div>
        </div>

        <p class="text-sm  my-8 leading-relaxed">
            {/* Data About bucket  */}
        </p>

        <div class="flex items-center">
            <h3 class="pr-4 text-lg flex-1">Enable Versioning</h3>
            <label class="relative cursor-pointer">
            <input type="checkbox" class="sr-only peer" onClick={handleVersionButton} checked={enable}  />
            <div
                class="w-11 h-3 flex items-center bg-gray-300 rounded-full peer peer-checked:after:translate-x-full after:absolute after:left-0 peer-checked:after:-left-1 after:bg-gray-300 peer-checked:after:bg-[#007bff] after:border after:border-gray-300 peer-checked:after:border-[#007bff] after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-[#007bff]">
            </div>
            </label>
        </div>
        </div>
        }
    </div>
}

export default BucketCard;