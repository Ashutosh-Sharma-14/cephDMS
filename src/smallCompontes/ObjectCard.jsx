
import { useState } from 'react';
import './objectCard.css'
import fileLogo from '../Jsons/fileLogo.json'
import MetaDataList from './MetaDataList';




const ObjectCard = ({ objectKey, metadata, lastModifiedTime, fileSize }) =>{

 

  // console.log(objectKey);
  console.log(toString(metadata));
  // console.log(lastModifiedTime);
  // console.log(fileSize);



    const [open, setOpen] = useState(false);

    return <div className="objectCard">
        <div className="font-[sans-serif] space-y-4 max-w-6xl mx-auto mt-4">
          <div className="shadow-[0_2px_10px_-3px_rgba(6,81,237,0.3)] rounded-lg border-l-8 border-blue-600" role="accordion">
              <button type="button" className="w-full text-sm font-semibold text-left py-5 px-6 text-blue-600 flex items-center" onClick={()=>setOpen(!open)}>
                  <img src={fileLogo[objectKey.substring(objectKey.lastIndexOf('.') + 1)] || fileLogo['default']} alt="" className="fill-current w-8 mr-4 shrink-0"  />
                  <span className="mr-4">
                    {objectKey.substring(objectKey.lastIndexOf('/') + 1)}
                      <span className="text-xs text-gray-600 mt-0.5 block font-medium">
                        {new Date(lastModifiedTime).toLocaleString('en-IN', { timeZone: 'Asia/Kolkata' })}
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
                      <table className="min-w-full bg-white">
                        <thead className="bg-gray-100 whitespace-nowrap">
                          <tr>
                            <th className="p-4 text-left text-xs font-semibold text-gray-800">
                              Key
                            </th>
                            <th className="p-4 text-left text-xs font-semibold text-gray-800">
                              Values
                            </th>
                          </tr>
                        </thead>
                            <MetaDataList metadata={metadata}/>
                        </table>
                    </div>
              </div>
          </div>
          </div>
    </div>
}

export default ObjectCard;