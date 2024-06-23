
import { useState, useEffect } from 'react';
import './objectCard.css'
import fileLogo from '../Jsons/fileLogo.json'
import MetaDataList from './MetaDataList';




const ObjectCard = ({ objectKey, metadata, lastModifiedTime, fileSize, query }) =>{


  // console.log(objectKey);
  // console.log(toString(metadata));
  // console.log(lastModifiedTime);
  // console.log(fileSize);

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
                        <span>  {handleFileSize(fileSize)}</span>
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