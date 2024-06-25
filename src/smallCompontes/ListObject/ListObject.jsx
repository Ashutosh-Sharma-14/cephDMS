import React, {  useEffect, useState } from "react";
import axios from "axios";
import Navbar from "../../navbar/Navbar";
import Sidebar from "../../sidebar/Sidebar";
import ObjectCard from "../ObjectCard";
import "./listobject.css";
import "../../uploadPg/upload.css";
import ReactLoading from 'react-loading';
import swal from "sweetalert";


const ListObject = () => {
  const [loading, setLoading] = useState(false);
  const [cnt, setCnt] = useState(0);

  const [elements, setElements] = useState([]);
  const [hide,setHide] = useState(false);

  const [values, setValues] = useState({
    bucketName: "",
    year: "",
    bankName: "",
    accountNo: "",
    maxKey: "",
    search:''
  });

  const [response, setResponse] = useState({
    objectKeys:[],
    metadata:[],
    lastModifiedTime:[],
    fileSizes:[],
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


    const fetchObject = async () => {
        try {
          const prefix = buildPrefix(
            values.year,
            values.bankName,
            values.accountNo
          );
          const maxKeys = values.maxKey === '' ? '100' : values.maxKey;
          localStorage.setItem('continuationToken', '');
          setLoading(true);
          const res = await axios.get(`http://localhost:8080/user/list-objects-paginated`, {
            params: {
              bucketName: values.bucketName,
              prefix: prefix,
              maxKeys: maxKeys,
              continuationToken: '',
            },
          });
    
          setResponse(res.data);
          localStorage.setItem('continuationToken', res.data.continuationToken || '');
        //   console.log('setToken', res.data.continuationToken);
        //   console.log(res.data.metadata);
          setLoading(false);
          // console.log(response.objectKeys.length)
        } catch (e) {
          console.log(e);
        }
      };

      useEffect(()=>{

        const fetchMoreObject = async () =>{
            const prefix = buildPrefix(
                values.year,
                values.bankName,
                values.accountNo
              );
            setLoading(true);
            const res = await axios.get(`http://localhost:8080/user/list-objects-paginated`, {
                params: {
                  bucketName: values.bucketName,
                  prefix: prefix,
                  maxKeys: values.maxKey,
                  continuationToken: localStorage.getItem('continuationToken'),
                },
              });
            // console.log(res.data);
            localStorage.setItem('continuationToken',res.data.continuationToken);
            if(res.data.continuationToken === null){
            swal(
                "End of Object in " ,
                `${values.year} ${values.bankName} ${values.accountNo}`,
                "info"
            )
            }

            setResponse(res.data);
      
            setLoading(false);
              
      
      
        }
        if (cnt > 0) fetchMoreObject();
          
      
      
      },[cnt])

   
      // const handleInputChange = () => {
      //   const updatedElements = elements.map((element) => {
      //     const elementText = element.innerText ? element.innerText.trim().toLowerCase() : '';
      //     if (elementText.includes(values.search.toLowerCase())) {
      //       element.style.display = 'none'; // Hide matching elements
      //       return true;
      //     } else {
      //       element.style.display = 'block'; // Ensure non-matching elements are visible
      //       return false;
      //     }
      //   });
      
      //   setHide(updatedElements.some(isHidden => isHidden)); // Set hide to true if any element is hidden
      // };
      
    
  
  //     useEffect(() => {
  //       // Function to fetch elements and set state
  //       const fetchElements = () => {
  //         const allElements = document.body.getElementsByTagName('div');
  //         const elementsArray = Array.from(allElements);
  //         setElements(elementsArray);
  //         console.log(elements)
  //       };
  //       fetchElements(); // Fetch elements on component mount
  //     }, []); // Only run once on mount
    
  //     useEffect(() => {
  //       handleInputChange();
  //     }, [values.search]); // Run when values.search changes
    

  const handleInputValue = (e) => {
    const { name, value } = e.target;
    setValues((prevState) => ({ ...prevState, [name]: value }));
  };

 
  

  const filteredObjects = response.objectKeys.reduce((acc, objectKey, index) => {
    const metadata = response.metadata[index];

    // Check if objectKey or metadata contains the search value
    if (
        objectKey.toLowerCase().includes(values.search.toLowerCase()) ||
        (metadata && Object.values(metadata).some(value => value.toLowerCase().includes(values.search.toLowerCase())))
    ) {
        acc.push({
            objectKey,
            metadata: metadata ? metadata : {}, // Ensure metadata exists
            lastModifiedTime: response.lastModifiedTime[index],
            fileSize: response.fileSizes[index]
        });
    }
    return acc;
}, []);

    
  

  return (
    <div className="upload">
      <div className="navbar">
        <Navbar />
      </div>
      <div className="wrapper">
        <div className="sidebar">
          <Sidebar />
        </div>
        <div className="cardWrapper">
          <div className="uploadBtn">
            <div className="inputFieldForApi font-[sans-serif] space-x-4 space-y-4 text-center">
              <label
                htmlFor="UserEmail"
                className="relative block overflow-hidden border-b border-gray-200 bg-transparent pt-3 focus-within:border-blue-600"
              >
                <input
                  type="text"
                  id="UserEmail"
                  placeholder=""
                  name="bucketName"
                  onChange={handleInputValue}
                  className="peer h-8 w-full border-none bg-transparent p-0 placeholder-transparent focus:border-transparent focus:outline-none focus:ring-0 sm:text-sm"
                />
                <span className="absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs">
                  Bucket Name
                </span>
              </label>

                {/* search Tab */}

              <label
                htmlFor="UserEmail"
                style={{marginBottom:'12px',scale:!loading?'1':'0', transition:'all 1s'}}
                className="relative block overflow-hidden border-b border-gray-200 bg-transparent pt-3 focus-within:border-blue-600"
              >
                <input
                  type="text"
                  id="UserEmail"
                  title="Search anything MetaData, Prefix, filName etc."
                  placeholder=""
                  name="search"
                  onChange={handleInputValue}
                  className="peer h-8 w-full border-none bg-transparent p-0 placeholder-transparent focus:border-transparent focus:outline-none focus:ring-0 sm:text-sm"
                />
                <span className="absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs">
                  Search...
                </span>
              </label>

              <div>
                <input
                  type="text"
                  placeholder="Year"
                  className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500"
                  onChange={handleInputValue}
                  name="year"
                />
              </div>
              <div>
                <input
                  type="text"
                  placeholder="Bank Name"
                  className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500"
                  onChange={handleInputValue}
                  name="bankName"
                />
              </div>
              <div>
                <input
                  type="text"
                  placeholder="Account Number"
                  className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500"
                  onChange={handleInputValue}
                  name="accountNo"
                />
              </div>
              <div>
                <input
                  type="number"
                  placeholder="Max Object"
                  className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500"
                  onChange={handleInputValue}
                  name="maxKey"
                />
              </div>
            </div>
            <button
              type="button"
              onClick={fetchObject}
              style={{scale:loading?'0':'1', transition:'all 1s'}}
              className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300"
            >
              Fetch Objects
            </button>
          </div>
          <div className="mainUploadPgForObjectCard">
            {
            loading ?
            <div className="loadingLogo">
                <ReactLoading type="cylon" color="#007bff" width={'3%'} /> 
                <div className="">Fetching...</div>

            </div>
            :  filteredObjects.map((object, index) => (
              <ObjectCard
                key={index}
                bucketName={values.bucketName}
                objectKey={object.objectKey}
                metadata={object.metadata}
                lastModifiedTime={object.lastModifiedTime}
                fileSize={object.fileSize}
                query={hide}
              />
            ))
            
            }
          </div>
          <div className="fetchMoreObject" style={{scale:values.maxKey <= 0?'0':'1',transition:'all 1s'}} >
                <button type="button"
                    onClick={()=> setCnt(cnt=>cnt+1)}
                    className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-orange-700 outline-none bg-transparent hover:bg-orange-700 text-orange-700 hover:text-white transition-all duration-300">
                        Fetch next {values.maxKey} Object
                </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ListObject;