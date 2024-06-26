import './subBucket.css'

const SubBucket = ({ enable,handleDeleteBtn, handleVersionButton}) =>{
    return <div className="subBucket">
        <div className="versioning">
        <div className="flex items-center">
            <span className="pr-4 text-lg flex-1">{enable ? 'Versioning Enabled' : 'Versioning Suspended'}</span>
            <label className="relative cursor-pointer">
            <input type="checkbox" className="sr-only peer"  title='Enable Versioning on Bucket'
            onClick={handleVersionButton} 
            checked={enable}
            />
            <div
                className="w-11 h-3 flex items-center bg-gray-300 rounded-full peer peer-checked:after:translate-x-full after:absolute after:left-0 peer-checked:after:-left-1 after:bg-gray-300 peer-checked:after:bg-[#007bff] after:border after:border-gray-300 peer-checked:after:border-[#007bff] after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-[#007bff]">
            </div>
                        
            </label>
            </div>
        </div>
        <div className="delete">
        <button className="mr-4" title="Delete Bucket" 
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
        </div>
    </div>
}

export default SubBucket;