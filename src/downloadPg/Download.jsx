import Navbar from "../navbar/Navbar";
import Sidebar from "../sidebar/Sidebar";
import { useState } from "react";
import '../uploadPg/upload.css'
import './download.css'
import ReactLoading from 'react-loading';
import swal from "sweetalert";


const Download = () =>{

    const [keyObject, setKeyObject] = useState({
        bucketName:'',
        year:'',
        bankName:'',
        accountNo:'',
        fileName:'',
        versionID:''
    });

    function buildPrefix(fileYear, bankName, accountNo) {
        let prefixBuilder = '';
            if (fileYear && fileYear !== '') {
                prefixBuilder += fileYear;
                if (bankName && bankName !== '') {
                    prefixBuilder += `/${bankName}`;
                    if (accountNo && accountNo !== '') {
                        prefixBuilder += `/${accountNo}/`;
                    }
                }
            }
        
    
        return prefixBuilder;
    }

    const [btn,setBtn] = useState(false);

    const handleInputValue = (e) => {
        const { name, value } = e.target;
        setKeyObject(prevState => ({ ...prevState, [name]: value }));
        // console.log(keyObject);
    }

    const handleFileBtn = async () =>{
        let tempKey = buildPrefix(keyObject.year,keyObject.bankName,keyObject.accountNo);
        // console.log('file', tempKey);


        // Assuming you have an instance of DownloadRequestDTO or its equivalent in JavaScript
            const downloadRequest = {
                bucketName: keyObject.bucketName,
                objectKey: tempKey === '' ? `/${keyObject.fileName}` : tempKey,
                versionId: keyObject.versionID
            };

            // console.log(tempKey)

            // Function to construct FormData object
            function createFormData(downloadRequest) {
                const formData = new FormData();

                // Append properties to FormData object
                formData.append('bucketName', downloadRequest.bucketName);
                formData.append('objectKey', downloadRequest.objectKey);
                formData.append('versionId', downloadRequest.versionId);

                return formData;
            }

            // Example usage:
            const formData = createFormData(downloadRequest);

            // Constructing the URL with query parameters
            const url = new URL('http://localhost:8080/user/download-file-from-ceph'); // Replace with your actual endpoint URL
            url.search = new URLSearchParams(formData).toString();

            // Making the GET request using fetch
            try {
                setBtn(true)
                const response = await fetch(url.toString(), {
                    method: 'GET',
                });
                setBtn(false)
                swal({
                    title: "File Download Successful",
                    text: "The files are downloaded in /Downloads Folder",
                    icon: "success",
                })
    
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
    
                const responseData = await response.json(); // Assuming response is JSON
                console.log('Download successful:', responseData);
            } catch (error) {
                swal({
                    title: "File Download Unsuccessful",
                    icon: "error",
                })
                setBtn(false)
                console.error('Error downloading files:', error);
            }

    }


    const handleFolderBtn = async () =>{
        // Assuming keyObject contains the necessary parameters like year, bankName, and accountNo
        // let tempKey = `${keyObject.year}/${keyObject.bankName}/${keyObject.accountNo}`;
        
        let tempKey = buildPrefix(keyObject.year,keyObject.bankName,keyObject.accountNo);
        // console.log('folder', tempKey);

        // Constructing the URL with query parameters
        const url = new URL('http://localhost:8080/user/download-multiple-files-from-ceph');
        url.searchParams.append('prefix', tempKey);
        url.searchParams.append('bucketName',keyObject.bucketName);

        if(keyObject.accountNo <= 0 || keyObject.bankName === '' || keyObject.accountNo<=0){
            swal(
                'Enter Prefix',
                `${keyObject.year <= 0? 'Year,':''} ${keyObject.bankName===''?'Bank Name,':''} ${keyObject.accountNo<= 0 ? 'Account No':''}`,
                'info'
            )
            return;
        }

        try {
            setBtn(true)
            const response = await fetch(url.toString(), {
                method: 'GET',
            });
            setBtn(false)
            swal({
                title: "Folder Download Successful",
                text: "The Folders are downloaded in /Downloads Folder",
                icon: "success",
            })

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const responseData = await response.json(); // Assuming response is JSON
            console.log('Download successful:', responseData);
        } catch (error) {
            swal({
                title: "Folder Download Unsuccessful",
                icon: "warning",
            })
            setBtn(false)

            console.error('Error downloading files:', error);
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
                    <input type='number' placeholder='Year'
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
                    <input type='number' placeholder='Account Number'
                    className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" 
                    onChange={handleInputValue}
                    name="accountNo"
                    />
                </div>

               
{/* 
            <button type="button"
                className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300">
                    Download Files
            </button> */}
            </div>
            <div className="mainUploadPg">
                {
                    btn ?
                    <div className="loadingLogo">
                        <ReactLoading type="cylon" color="#007bff" width={'3%'} /> 
                        <div className="">Downloading...</div>
                    </div> :
                    <>
                <div className="downloadFolder">
                    <div className=""> Folder</div>
                    <button type="button"
                        className="btn px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300"
                        onClick={handleFolderBtn}
                        title="Enter perfix"
                        >
                            Download 
                    </button>
                </div>
                <div className="downloadObject">
                    <div className=""> File</div>
                    <label
                        htmlFor="fileName"
                        className="labelClassName relative block overflow-hidden border-b border-gray-200 bg-transparent pt-3 focus-within:border-blue-600"

                        >
                        <input
                            type="text"
                            id="fileName"
                            name="fileName"
                            placeholder="File Name"
                            onChange={handleInputValue}
                            className="peer h-8 w-full border-none bg-transparent p-0 placeholder-transparent focus:border-transparent focus:outline-none focus:ring-0 sm:text-sm"
                        />

                        <span
                            className="labelSpan absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs"
                        >
                            {/* File Name */}
                    </span>
                    </label>
                    <label
                        htmlFor="VersionID"
                        className="labelClassName relative block overflow-hidden border-b border-gray-200 bg-transparent pt-3 focus-within:border-blue-600"

                        >
                        <input
                            type="text"
                            id="VersionID"
                            name="versionID"
                            placeholder="Version ID"
                            onChange={handleInputValue}
                            className="peer h-8 w-full border-none bg-transparent p-0 placeholder-transparent focus:border-transparent focus:outline-none focus:ring-0 sm:text-sm"
                        />

                        <span
                            className="labelSpan absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs"
                        >
                            {/* Version ID */}
                    </span>
                    </label>
                    <button type="button"
                        onClick={handleFileBtn}
                        title="Enter prefix"
                        className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300">
                            Download 
                    </button>
                </div>
                </>
                }
            </div>
        </div>
    </div>
    </div>
}

export default Download;