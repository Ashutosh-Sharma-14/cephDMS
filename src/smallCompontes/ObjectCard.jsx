
import { useState } from 'react';
import './objectCard.css'
import fileLogo from '../Jsons/fileLogo.json'
import MetaDataList from './MetaDataList';
import swal from 'sweetalert';
import Swal from 'sweetalert2';
import axios from 'axios';




const ObjectCard = ({ objectKey, metadata, lastModifiedTime, fileSize, query, bucketName, cnt }) =>{

  const [btn, setBtn] = useState(false);


  // console.log(objectKey);
  // console.log(toString(metadata));
  // console.log(lastModifiedTime);
  // console.log(fileSize);
  // console.log(bucketName)

  const handleFileSize  = (bytes) => {

    if (bytes === 0) return '0 Byte';

    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
  
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }


    const [open, setOpen] = useState(false);

    const segments = objectKey.split('/');
    const fileInfo = {
      year: segments[0],
      bankName: segments[1],
      accountNo: segments[2],
      fileName: segments.slice(3).join('/') // Join remaining segments for fileName
    };
    
    function buildPrefix(fileYear, bankName, accountNo) {
      let prefixBuilder = `${fileYear}/${bankName}/${accountNo}/`;
      return prefixBuilder;
  }
   

    const handleFileBtn = async () =>{
      let tempKey = buildPrefix(fileInfo.year,fileInfo.bankName,fileInfo.accountNo);
      // console.log('file', tempKey);


      // Assuming you have an instance of DownloadRequestDTO or its equivalent in JavaScript
          const downloadRequest = {
              bucketName: bucketName,
              objectKey: tempKey === '' ? `${fileInfo.fileName}` : tempKey  + `${fileInfo.fileName}`,
              versionId: ''
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
          // console.log( 'oekokef', bucketName)
          // Constructing the URL with query parameters
          const url = new URL('http://localhost:8080/user/download-file-from-ceph'); 
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
  console.log(objectKey)

  const handleDeleteBtn = async (e)=>{
    e.preventDefault();
    // cnt(prev => prev+1);
    let tempKey = buildPrefix(fileInfo.year,fileInfo.bankName,fileInfo.accountNo);

        try {
            Swal.fire({
                title: `Are you sure you want to delete "${fileInfo.fileName}" ?`,
                showCancelButton: true,
                confirmButtonText: 'Yes',
                cancelButtonText: 'No',
                showLoaderOnConfirm: true,
                allowOutsideClick: () => !Swal.isLoading()
            }).then(async (result) => {
                console.log(result)
            if (result.isConfirmed) {
                const formData = new FormData();
                formData.append('bucketName', bucketName);
                formData.append('objectKey', objectKey);

                const uri = `http://localhost:8080/user/delete-object-versions-and-delete-markers?bucketName=${encodeURIComponent(bucketName)}&objectKey=${encodeURIComponent(objectKey)}`
                const res = await axios.delete(uri, {
                  data: formData,
                  headers: {
                    'Content-Type': 'application/x-www-form-urlencoded' // Set appropriate content type
                  }
                });
                Swal.fire({
                title: `${fileInfo.fileName} File Deleted Successful`,
                icon: 'success'
                });
                console.log(res);
                cnt(prev => prev+1);
            }else{
                Swal.fire({
                    title: `Cancelled`,
                    icon: 'info'
                });
            }
            });
        } catch (error) {
              Swal.fire({
                title: `Unsuccessful Object Deletion`,
                icon: 'info'
            });
            console.error('Error deleting Object:', error);
        }
        

  }

    

    return <div className="objectCard" style={{display:query?'none':'block'}} >
        <div className="font-[sans-serif] space-y-4 max-w-6xl mx-auto mt-4">
          <div className="shadow-[0_2px_10px_-3px_rgba(6,81,237,0.3)] rounded-lg border-l-8 border-blue-600" role="accordion">
              <button type="button" className="w-full text-sm font-semibold text-left py-5 px-6 text-blue-600 flex items-center" onClick={()=>setOpen(!open)}>
                  <img src={fileLogo[objectKey.substring(objectKey.lastIndexOf('.') + 1)] || fileLogo['default']} alt="" className="fill-current w-8 mr-4 shrink-0"  />
                  <span className="titleFileName mr-4">
                    {/* {objectKey.substring(objectKey.lastIndexOf('/') + 1)} */}
                    {fileInfo.fileName}
                      <span className="subTitle text-xs text-gray-600 mt-0.5 block font-medium">
                        {new Date(lastModifiedTime).toLocaleString('en-IN', { timeZone: 'Asia/Kolkata' })}
                        <div className="deleteCont">
                             <img src={fileLogo.svg} alt="" onClick={handleFileBtn} style={{display: !btn?'block':'none',transition:'all 1s'}}  />
                            
                             <button className="mr-4 ml-4" title="Delete Bucket" 
                                  onClick={handleDeleteBtn}
                                  >
                                      {/* Delete */}
                                      <svg xmlns="http://www.w3.org/2000/svg" className="w-5 fill-red-500 hover:fill-red-700" viewBox="0 0 24 24">
                                      <path
                                          d="M19 7a1 1 0 0 0-1 1v11.191A1.92 1.92 0 0 1 15.99 21H8.01A1.92 1.92 0 0 1 6 19.191V8a1 1 0 0 0-2 0v11.191A3.918 3.918 0 0 0 8.01 23h7.98A3.918 3.918 0 0 0 20 19.191V8a1 1 0 0 0-1-1Zm1-3h-4V2a1 1 0 0 0-1-1H9a1 1 0 0 0-1 1v2H4a1 1 0 0 0 0 2h16a1 1 0 0 0 0-2ZM10 4V3h4v1Z"
                                          data-original="#000000" />
                                      <path d="M11 17v-7a1 1 0 0 0-2 0v7a1 1 0 0 0 2 0Zm4 0v-7a1 1 0 0 0-2 0v7a1 1 0 0 0 2 0Z"
                                          data-original="#000000" />
                                      </svg>
                              </button>

                          <span>  {handleFileSize(fileSize)}</span>
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

              <div className="pb-5 px-6" style={{ display:open?'block' : 'none' }}>
                  <div className="font-sans overflow-x-auto">
                            <div className="keyPath">
                                  <div className="flex items-start max-md:flex-col gap-y-6 gap-x-3 max-w-screen-lg mx-auto px-4 font-[sans-serif]">

                                  <div className="w-full">
                                    {/* <div className="w-full h-1 rounded-xl bg-green-500"></div> */}
                                    <div className="mt-2 mr-4 flex">
                                      <div className="ml-2">
                                        <h6 className="text-base font-bold text-green-400">Year : {fileInfo.year}</h6>
                                      </div>
                                    </div>
                                  </div>
                                  <div className="w-full">
                                    {/* <div className="w-full h-1 rounded-xl bg-green-500"></div> */}
                                    <div className="mt-2 mr-4 flex">
                                      
                                      <div className="ml-2">
                                        <h6 className="text-base font-bold text-green-400">Bank Name : {fileInfo.bankName} </h6>
                                      </div>

                                    </div>
                                  </div>

                                  <div className="w-full">
                                    {/* <div className="w-full h-1 rounded-xl bg-green-500"></div> */}
                                    <div className="mt-2">
                                      <h6 className="text-base font-bold text-green-400">Account Number : {fileInfo.accountNo} </h6>
                                    </div>
                                  </div>
                                </div>
                            </div>
                            <MetaDataList metadata={metadata}/>
                    </div>
              </div>
          </div>
          </div>
    </div>
}

export default ObjectCard;