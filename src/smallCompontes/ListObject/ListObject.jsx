import { useState } from "react";
import Navbar from "../../navbar/Navbar";
import Sidebar from "../../sidebar/Sidebar";
import '../../uploadPg/upload.css'
import ObjectCard from "../ObjectCard";
import './listobject.css'
import axios from "axios";


const ListObject = () =>{

    const [values, setValues] = useState({
        'bucketName':'',
        'year':'',
        'bankName':'',
        'accountNo':'',
        'maxKey':''
    })

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

    const fetchObject = async () =>{
        // console.log(values);
        try{
            const prefix = buildPrefix(values.year,values.bankName,values.accountNo);
            const res = await axios.get(`http://localhost:8080/user/list-objects-paginated?bucketName=${values.bucketName}&prefix=${prefix}&maxKeys=${values.maxKey===''?'100':values.maxKey}&continuationToken=${values.maxKey}`)
            
            console.log(res.data)

        }catch(e){
            console.log(e);
        }
    }

    const handleInputValue = (e) =>{
        const { name, value } = e.target;
        setValues(prevState => ({ ...prevState, [name]: value }));

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
            <div className="inputFieldForApi font-[sans-serif] space-x-4 space-y-4 text-center">
            
            {/* input btn */}
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

                <span
                    className="absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs"
                >
                    Bucket Name
                </span>
                </label>

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

                <div>
                    <input type='number' placeholder='Max Object'
                    className="px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500" 
                    onChange={handleInputValue}
                    name="maxKey"
                    />
                </div>

            </div>

            <button type="button"
            onClick={fetchObject}
                className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300">
                    Fetch Objects
            </button>
            </div>
            <div className="mainUploadPgForObjectCard">

                <ObjectCard />



            </div>
            <div className="pagination">
            <ul className="flex space-x-4 justify-center">
                <li className="flex items-center justify-center shrink-0 bg-gray-300 w-10 h-10 rounded-full">
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-3 fill-gray-400" viewBox="0 0 55.753 55.753">
                    <path
                        d="M12.745 23.915c.283-.282.59-.52.913-.727L35.266 1.581a5.4 5.4 0 0 1 7.637 7.638L24.294 27.828l18.705 18.706a5.4 5.4 0 0 1-7.636 7.637L13.658 32.464a5.367 5.367 0 0 1-.913-.727 5.367 5.367 0 0 1-1.572-3.911 5.369 5.369 0 0 1 1.572-3.911z"
                        data-original="#000000" />
                    </svg>
                </li>
                <li
                    className="flex items-center justify-center shrink-0 bg-blue-500  border-2 border-blue-500 cursor-pointer text-base font-bold text-white w-10 h-10 rounded-full">
                    1
                </li>
                <li
                    className="flex items-center justify-center shrink-0 hover:bg-gray-50  border-2 cursor-pointer text-base font-bold text-[#333] w-10 h-10 rounded-full">
                    2
                </li>
                <li
                    className="flex items-center justify-center shrink-0 hover:bg-gray-50  border-2 cursor-pointer text-base font-bold text-[#333] w-10 h-10 rounded-full">
                    3
                </li>
                <li
                    className="flex items-center justify-center shrink-0 hover:bg-gray-50  border-2 cursor-pointer text-base font-bold text-[#333] w-10 h-10 rounded-full">
                    4
                </li>
                <li className="flex items-center justify-center shrink-0 hover:bg-gray-50 border-2 cursor-pointer w-10 h-10 rounded-full">
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-3 fill-gray-400 rotate-180" viewBox="0 0 55.753 55.753">
                    <path
                        d="M12.745 23.915c.283-.282.59-.52.913-.727L35.266 1.581a5.4 5.4 0 0 1 7.637 7.638L24.294 27.828l18.705 18.706a5.4 5.4 0 0 1-7.636 7.637L13.658 32.464a5.367 5.367 0 0 1-.913-.727 5.367 5.367 0 0 1-1.572-3.911 5.369 5.369 0 0 1 1.572-3.911z"
                        data-original="#000000" />
                    </svg>
                </li>
                </ul>
            </div>
        </div>
    </div>
    </div>
}

export default ListObject;