import Navbar from "../navbar/Navbar";
import Sidebar from "../sidebar/Sidebar";
import UploadCard from "../smallCompontes/uploadCard/UploadCard";
import '../uploadPg/upload.css'


const Upload = () =>{
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
            <div class="font-[sans-serif] space-x-4 space-y-4 text-center">
            
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

            {/* upload button */}
            <button type="button"
                class="px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300">
                    Upload Files
            </button>
            </div>
            <div className="mainUploadPg">
                <UploadCard />
                <UploadCard />
                <UploadCard />
                <UploadCard />
                <UploadCard />
                <UploadCard />
                <UploadCard />
                <UploadCard />
                <UploadCard />
                <UploadCard />
            </div>
        </div>
    </div>
    </div>
}

export default Upload;