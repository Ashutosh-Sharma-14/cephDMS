import Navbar from "../navbar/Navbar";
import Sidebar from "../sidebar/Sidebar";
import UploadCard from "../smallCompontes/uploadCard/UploadCard";
import UploadTable from "../table/UploadTable";
import '../uploadPg/upload.css'
import { useState } from "react";
import axios from "axios";



const Upload = () =>{

    // send handle upload funct to child and keyObject too and then try to send formdata from there

    const [object, setObject] = useState([]);
    const [keyObject, setKeyObject] = useState({
        bucketName:'',
        year:'',
        bankName:'',
        accountNo:''
    });


     const handleAllFileUpload = async (e) =>{
        e.preventDefault(); 
        // console.log(JSON.stringify(object[0].metadataJson));

        const listOfMapOfStrings = [];

        for(let i=0; i<object.length; i++)
            listOfMapOfStrings.push(object[i].metadataJson)

        // console.log(listOfMapOfStrings);
        

        let tempKey = `${keyObject.year}/${keyObject.bankName}/${keyObject.accountNo}`;

        const formData = new FormData();
            formData.append('bucketName', keyObject.bucketName);
            formData.append('objectKey', tempKey);
            formData.append('userRole', 'branch-manager');
            formData.append('metadata', JSON.stringify(listOfMapOfStrings));

            // formData.append(`multipartFiles`, object.fileArray);
            object.fileArray.forEach((file, index) => {
                formData.append(`multipartFiles[${index}]`, file);
              });

            console.log(formData)

            
            try {
                // Make POST request
                // const response = await axios.post('http://localhost:8080/upload-multiple-files-to-ceph', formData, {
                //     headers: {
                //         'Content-Type': 'multipart/form-data'
                //     }
                // });
        
                // console.log('Upload successful:', response);
                // Handle success response here
            } catch (error) {
                console.error('Error uploading files:', error);
                // Handle error here
            }





        
        }
    const handleObject = (e) =>{
        setObject([...object,e]);
    }

    const handleDelete = (fileNameOfThatRow) =>{
        // console.log(fileNameOfThatRow);
        const modifiedObject = object.filter(obj => obj.fileName !== fileNameOfThatRow);
        setObject(modifiedObject);

    }


    const handleInputValue = (e) => {
        const { name, value } = e.target;
        setKeyObject(prevState => ({ ...prevState, [name]: value }));
        console.log(keyObject);
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
            <div className="font-[sans-serif] space-x-4 space-y-4 text-center">
            
            {/* input btn */}
                <label
                htmlFor="UserEmail"
                className="relative block overflow-hidden border-b border-gray-200 bg-transparent pt-3 focus-within:border-blue-600"
                >
                <input
                    type="email"
                    id="UserEmail"
                    placeholder="Email"
                    name="bucketName"
                    className="peer h-8 w-full border-none bg-transparent p-0 placeholder-transparent focus:border-transparent focus:outline-none focus:ring-0 sm:text-sm"
                    onChange={handleInputValue}
                />

                <span
                    className="absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs"
                >
                    Bucket Name
                </span>
                </label>

            </div>


                {/* add key fields */}
                <div>
                    <input type='text' placeholder='Year'
                    className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" 
                    onChange={handleInputValue}
                    name="year"
                    />
                </div>
                <div>
                    <input type='text' placeholder='Bank Name'
                    className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" 
                    onChange={handleInputValue}
                    name="bankName"
                    />
                </div>
                <div>
                    <input type='text' placeholder='Account Number'
                    className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" 
                    onChange={handleInputValue}
                    name="accountNo"
                    />
                </div>

               

            {/* add card button */}
            {/* <div className="addCard">
                <button type="button"
                    className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-blue-700 hover:bg-transparent text-white hover:text-blue-700 transition-all duration-300" onClick={handCardButton}>
                        Add Card
                </button>
            </div> */}
            {/* upload button */}
            <button type="button"
                className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300"
                onClick={handleAllFileUpload}
                >
                    Upload Files
            </button>
            </div>
            <div className="mainUploadPg">
                <UploadCard handleObject={handleObject} object={object}/>
                <UploadTable object={object} handleObject={handleObject} handleDelete={handleDelete}/>
            </div>
        </div>
    </div>
    </div>
}

export default Upload;