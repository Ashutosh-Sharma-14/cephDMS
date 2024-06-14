import Navbar from "../../navbar/Navbar";
import Sidebar from "../../sidebar/Sidebar";
import '../../uploadPg/upload.css'
import BucketCard from "../BucketCard";
import './listBuckets.css'


const ListBuckets = () =>{

    
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
                    className="peer h-8 w-full border-none bg-transparent p-0 placeholder-transparent focus:border-transparent focus:outline-none focus:ring-0 sm:text-sm"
                />

                <span
                    className="absolute start-0 top-2 -translate-y-1/2 text-xs text-gray-700 transition-all peer-placeholder-shown:top-1/2 peer-placeholder-shown:text-sm peer-focus:top-2 peer-focus:text-xs"
                >
                    Bucket Name
                </span>
                </label>

            </div>

            <button type="button"
                className="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300">
                    Fetch Buckets
            </button>
            </div>
            <div className="mainUploadPg">
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />

                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />
                <BucketCard />


            </div>
            <div className="pagination">
            <ul class="flex space-x-4 justify-center">
                <li class="flex items-center justify-center shrink-0 bg-gray-300 w-10 h-10 rounded-full">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-3 fill-gray-400" viewBox="0 0 55.753 55.753">
                    <path
                        d="M12.745 23.915c.283-.282.59-.52.913-.727L35.266 1.581a5.4 5.4 0 0 1 7.637 7.638L24.294 27.828l18.705 18.706a5.4 5.4 0 0 1-7.636 7.637L13.658 32.464a5.367 5.367 0 0 1-.913-.727 5.367 5.367 0 0 1-1.572-3.911 5.369 5.369 0 0 1 1.572-3.911z"
                        data-original="#000000" />
                    </svg>
                </li>
                <li
                    class="flex items-center justify-center shrink-0 bg-blue-500  border-2 border-blue-500 cursor-pointer text-base font-bold text-white w-10 h-10 rounded-full">
                    1
                </li>
                <li
                    class="flex items-center justify-center shrink-0 hover:bg-gray-50  border-2 cursor-pointer text-base font-bold text-[#333] w-10 h-10 rounded-full">
                    2
                </li>
                <li
                    class="flex items-center justify-center shrink-0 hover:bg-gray-50  border-2 cursor-pointer text-base font-bold text-[#333] w-10 h-10 rounded-full">
                    3
                </li>
                <li
                    class="flex items-center justify-center shrink-0 hover:bg-gray-50  border-2 cursor-pointer text-base font-bold text-[#333] w-10 h-10 rounded-full">
                    4
                </li>
                <li class="flex items-center justify-center shrink-0 hover:bg-gray-50 border-2 cursor-pointer w-10 h-10 rounded-full">
                    <svg xmlns="http://www.w3.org/2000/svg" class="w-3 fill-gray-400 rotate-180" viewBox="0 0 55.753 55.753">
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

export default ListBuckets;