
import { useState } from 'react';
import './uploadCard.css'


const UploadCard = () =>{

    const [metadata, setMetadata] = useState(false);

    return <div className='uploadCard' style={{height:metadata?"35vh" : "18vh"}}>
        <div className="key">
                    {/* File Input */}

        <label for="uploadFile1"
            class="flex bg-gray-800 hover:bg-gray-700 text-white text-base px-5 py-3 outline-none rounded w-max cursor-pointer mx-auto font-[sans-serif]">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 mr-2 fill-white inline" viewBox="0 0 32 32">
                <path
                d="M23.75 11.044a7.99 7.99 0 0 0-15.5-.009A8 8 0 0 0 9 27h3a1 1 0 0 0 0-2H9a6 6 0 0 1-.035-12 1.038 1.038 0 0 0 1.1-.854 5.991 5.991 0 0 1 11.862 0A1.08 1.08 0 0 0 23 13a6 6 0 0 1 0 12h-3a1 1 0 0 0 0 2h3a8 8 0 0 0 .75-15.956z"
                data-original="#000000" />
                <path
                d="M20.293 19.707a1 1 0 0 0 1.414-1.414l-5-5a1 1 0 0 0-1.414 0l-5 5a1 1 0 0 0 1.414 1.414L15 16.414V29a1 1 0 0 0 2 0V16.414z"
                data-original="#000000" />
            </svg>
            Upload File
            <input type="file" id='uploadFile1' class="hidden" />
        </label>

        {/* add logo */}
        <button class="whitespace-nowrap rounded-full bg-purple-100 px-2.5 py-0.5 text-sm text-purple-700" onClick={()=>setMetadata(!metadata)} style={{backgroundColor:'rgba(0, 128, 0, 0.6)',color:'white',fontWeight:'600'}} >
            Add MetaData
        </button>

        </div>
        <div className="metadata" style={{display: metadata ? "block" : "none"}} >

            <input type='text' placeholder='MetaData 1'
            class="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" />
            
            <input type='text' placeholder='MetaData 2'
            class="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" />
    
            <input type='text' placeholder='MetaData 3'
            class="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" />
    
        </div>
    </div>
}

export default UploadCard;