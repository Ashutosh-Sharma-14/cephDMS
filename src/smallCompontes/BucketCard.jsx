
import { useState } from 'react';
import './bucketCard.css'
import axios from 'axios';
import ReactLoading from "react-loading";
import swal from 'sweetalert';
import Swal from 'sweetalert2';

const BucketCard = ({item, setCnt}) =>{
    const [enable,setEnable] = useState(false);

    // const handleVersionButton =  async (e) =>{
    //     e.preventDefault();

    //     try{
    //         const uri = `http://localhost:8080/user/enable-versioning?bucketName=${encodeURIComponent(item)}`
    //         const res = await axios.post(uri);
    //         setEnable(res.data);
    //         swal(
    //              enable ? `Bucket versioning enabled`: `Bucket versioning is already enabled`, 
    //              enable ? `Bucket Name : ${item}`: `Bucket Name : ${item}`, 
    //              enable ? "success" : "info");
    //         console.log(res);
    //     }catch(err){
    //         console.log('message',err);
    //     }
    // }

    const handleVersionButton = async (e) => {
        e.preventDefault();
    
        try {
            setEnable(false);
            const uri = `http://localhost:8080/user/enable-versioning?bucketName=${encodeURIComponent(item)}`;
            const res = await axios.post(uri);
            
            // Assuming res.data is a boolean indicating whether versioning was enabled
            // const enable = res.data;
            setEnable(res.data);
    
            // Using enable to conditionally set SweetAlert messages
            swal(
                !enable ? `Bucket versioning enabled` : `Bucket versioning is already enabled`,
                `Bucket Name: ${item}`,
                !enable ? "success" : "info"
            );
    
            console.log(res);
        } catch (err) {
            setEnable(false)
            swal(
               `Bucket versioning is Unsuccessful`,
                `${err} while enable versioning for ${item}`,
                "error"
            );
            console.log('Error:', err);
        }
    }
    

    const handleDeleteBtn = async (e) =>{
        e.preventDefault();
        try{

            Swal.fire({
            title: `Enter Bucket Name "${item}"`,
            input: 'text',
            inputAttributes: {
                autocapitalize: 'off'
            },
            showCancelButton: true,
            confirmButtonText: 'Submit',
            showLoaderOnConfirm: true,
            preConfirm: (name) => {
                return name;
            },
            allowOutsideClick: () => !Swal.isLoading()
            }).then(async (result) => {
                console.log(result)
            if (result.value == item && result.isConfirmed) {
                const uri = `http://localhost:8080/user/delete-bucket?bucketName=${encodeURIComponent(item)}`
                const res = await axios.delete(uri);
                // Swal.fire({
                // title: `${result.value} Bucket Deleted Successful`,
                // icon: 'success'
                // });
                setCnt(prev => prev+1);
            }else{
                Swal.fire({
                    title: `Cancelled`,
                    icon: 'info'
                });
            }
            });



        }catch(err){
        }

    }


    return <div className='bucketCard'>
            {
            // enable ? <div className='loadingLogo'>
            //     <ReactLoading type='bars' color="#007bff"/>
            //     <div className="div">Enabling...</div>
            // </div>
            // :
                <div className="notification shadow-[0_5px_14px_-4px_rgba(0,0,0,0.3)] p-6 w-full max-w-sm rounded-lg font-[sans-serif] overflow-hidden mx-auto mt-4">
                    
                    <div className="flex items-center">
                        <h3 className="text-2xl font-semibold  flex-1">{item}</h3>
                        <div className=" w-12 h-12 p-1 flex items-center justify-center rounded-full cursor-pointer">
                        <img src="/bucket.png" alt=""  />
                        </div>
                    </div>

                    <p className="text-sm  my-8 leading-relaxed">
                        {/* Data About bucket  */}
                    </p>

                    <div className="flex items-center">
                        <h3 className="pr-4 text-lg flex-1">Enable Versioning</h3>
                        <label className="relative cursor-pointer">
                        <input type="checkbox" className="sr-only peer" onClick={handleVersionButton} checked={enable}  />
                        <div
                            className="w-11 h-3 flex items-center bg-gray-300 rounded-full peer peer-checked:after:translate-x-full after:absolute after:left-0 peer-checked:after:-left-1 after:bg-gray-300 peer-checked:after:bg-[#007bff] after:border after:border-gray-300 peer-checked:after:border-[#007bff] after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-[#007bff]">
                        </div>
                        
                        </label>
                        <div className="deleteBtn">
                            <button className="mr-4" title="Delete Bucket" 
                            onClick={handleDeleteBtn}
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" className="w-5 fill-red-500 hover:fill-red-700" viewBox="0 0 24 24">
                                <path
                                    d="M19 7a1 1 0 0 0-1 1v11.191A1.92 1.92 0 0 1 15.99 21H8.01A1.92 1.92 0 0 1 6 19.191V8a1 1 0 0 0-2 0v11.191A3.918 3.918 0 0 0 8.01 23h7.98A3.918 3.918 0 0 0 20 19.191V8a1 1 0 0 0-1-1Zm1-3h-4V2a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v2H4a1 1 0 0 0 0 2h16a1 1 0 0 0 0-2ZM10 4V3h4v1Z"
                                    data-original="#000000" />
                                <path d="M11 17v-7a1 1 0 0 0-2 0v7a1 1 0 0 0 2 0Zm4 0v-7a1 1 0 0 0-2 0v7a1 1 0 0 0 2 0Z"
                                    data-original="#000000" />
                                </svg>
                            </button>
                            </div>
                    </div>
        </div>
        }
    </div>
}

export default BucketCard;