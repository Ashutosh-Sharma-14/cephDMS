
import { useState } from 'react';
import './bucketCard.css'
import axios from 'axios';
import ReactLoading from "react-loading";
import swal from 'sweetalert';
import Swal from 'sweetalert2';
import SubBucket from './SubBucket';


const BucketCard = ({item, setCnt}) =>{
    const [enable,setEnable] = useState(false);
    const [open, setOpen] = useState(false);

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
            // setEnable(false);
            const uri = `http://localhost:8080/user/change-versioning?bucketName=${encodeURIComponent(item.bucketName)}`;
            console.log(uri)
            const res = await axios.post(uri);
            
            // Assuming res.data is a boolean indicating whether versioning was enabled
            // const enable = res.data;
            setEnable(res.data);
    
            // Using enable to conditionally set SweetAlert messages
            swal(
                !enable ? `Bucket versioning enabled` : `Bucket versioning is Suspended`,
                `Bucket Name: ${item.bucketName}`,
                !enable ? "success" : "success"
            );
    
            console.log(res.data);
        } catch (err) {
            // setEnable(false)
            swal(
               `Bucket versioning is Unsuccessful`,
                `${err} while enable versioning for ${item.bucketName}`,
                "error"
            );
            console.log('Error:', err);
        }
    }
    

    const handleDeleteBtn = async (e) =>{
        e.preventDefault();
        try {
            Swal.fire({
                title: `Are you sure you want to delete "${item.bucketName}" ?`,
                showCancelButton: true,
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                showLoaderOnConfirm: true,
                allowOutsideClick: () => !Swal.isLoading()
            }).then(async (result) => {
                console.log(result)
            if (result.isConfirmed) {
                const uri = `http://localhost:8080/user/delete-bucket?bucketName=${encodeURIComponent(item.bucketName)}`
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
        } catch (error) {
            console.error('Error deleting bucket:', error);
        }
        

    }


    const handleOpenAccordian = async (e) =>{
        e.preventDefault();
        if(!open){
        try{

            const uri = `http://localhost:8080/user/is-versioning-enabled?bucketName=${encodeURIComponent(item.bucketName)}`
            const res = await axios.get(uri);
            if(res.data === 'ENABLED') setEnable(true);
            else  setEnable(false);
            console.log(res)
            setOpen(!open);
        }catch(err){

        }
        }else {setOpen(!open); }
    }


    // console.log(item)

    return <div className='bucketCard'>
        <div className="objectCard"  >
        <div className="font-[sans-serif] space-y-4 max-w-6xl mx-auto mt-4">
          <div className="shadow-[0_2px_10px_-3px_rgba(6,81,237,0.3)] rounded-lg border-l-8 border-blue-600" role="accordion">
              <button type="button" className="w-full text-sm font-semibold text-left py-5 px-6 text-blue-600 flex items-center"  onClick={handleOpenAccordian} >
                  <img src={'/BucketSmbole.svg'} alt="" className="fill-current w-8 mr-4 shrink-0"  />
                  <span className="titleFileName mr-4" style={{fontSize:'1.4em'}}>
                    {/* {objectKey.substring(objectKey.lastIndexOf('/') + 1)} */}
                    {item.bucketName}


                      <span className="subTitle text-xs text-gray-600 mt-0.5 block font-medium">
                        {/* {new Date(lastModifiedTime).toLocaleString('en-IN', { timeZone: 'Asia/Kolkata' })} */}
                        <div className="deleteCont">
                             {/* <img src={fileLogo.svg} alt="" onClick={handleFileBtn} style={{display: !btn?'block':'none'}}  /> */}
                          {/* <span>  {handleFileSize(fileSize)}</span> */}
                          {open ? <ReactLoading />:< span style={{width:'10%',position:'absolute', marginTop:'20px',fontSize:'0.9em',color:'lightslategray'}}>
                            {new Date(item.creationDate).toLocaleString('en-IN', { timeZone: 'Asia/Kolkata' })}
                          </span>}
                        </div>
                    </span>


                  </span>
                  {
                  open ?
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 fill-current ml-auto shrink-0 rotate-180" viewBox="0 0 24 24">
                      <path fillRule="evenodd"
                          d="M11.99997 18.1669a2.38 2.38 0 0 1-1.68266-.69733l-9.52-9.52a2.38 2.38 0 1 1 3.36532-3.36532l7.83734 7.83734 7.83734-7.83734a2.38 2.38 0 1 1 3.36532 3.36532l-9.52 9.52a2.38 2.38 0 0 1-1.68266.69734z"
                          clipRule="evenodd" data-original="#000000"></path>
                  </svg>
                  :
                  <svg xmlns="http://www.w3.org/2000/svg" className="w-4 fill-current ml-auto shrink-0 -rotate-90" viewBox="0 0 24 24">
                      <path fillRule="evenodd"
                          d="M11.99997 18.1669a2.38 2.38 0 0 1-1.68266-.69733l-9.52-9.52a2.38 2.38 0 1 1 3.36532-3.36532l7.83734 7.83734 7.83734-7.83734a2.38 2.38 0 1 1 3.36532 3.36532l-9.52 9.52a2.38 2.38 0 0 1-1.68266.69734z"
                          clipRule="evenodd" data-original="#000000"></path>
                  </svg>
                }
              </button>

              <span className="pb-5 px-6" style={{ display:open?'block' : 'none' }} >
                    <SubBucket  enable={enable} handleDeleteBtn={handleDeleteBtn} handleVersionButton={handleVersionButton} />
              </span>
          </div>
          </div>
    </div>
    </div>
}

export default BucketCard;