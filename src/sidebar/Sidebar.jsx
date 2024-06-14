import { Link } from "react-router-dom";
import './sidebar.css'


const Sidebar = () => {
    
    return (<>
    <nav className="bg-white shadow-lg h-screen fixed top-0 left-0 min-w-[250px] py-6 px-4 font-[sans-serif] overflow-auto">
      <a href="/"><img src="/perfiosLogo.png" alt="logo" className='w-[160px]' />
      </a>

      <ul className="mt-6 ulElement">
      <h6 className="text-blue-600 text-sm font-bold px-4">Info</h6>

        <li>
          <Link to="/"
            className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
            
            <span>Documentation</span>
          </Link>
        </li>
      </ul>

      <div className="mt-6 ulElement">
        <h6 className="text-blue-600 text-sm font-bold px-4">Bucket Operation</h6>
        <ul className="mt-3">
          <li>
            <a href="/list-buckets"
              className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
              
              <span>List Buckets</span>
            </a>
          </li>
          <li>
            <a href="/"
              className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
              
              <span>Enable Versioning</span>
            </a>
          </li>
          {/* <li>
            <a href="/"
              className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
              
              <span>Schedules</span>
            </a>
          </li>
          <li>
            <a href="/"
              className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
              
              <span>Promote</span>
            </a>
          </li> */}
        </ul>
      </div>

      <div className="mt-6 ulElement">
        <h6 className="text-blue-600 text-sm font-bold px-4">Object Operations</h6>
        <ul className="mt-3">
          <li>
          <Link to="/upload"
            className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
            
            <span>Upload Multiple Files</span>
          </Link>
          </li>
          <li>
            <a href="/download"
              className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
              <span>Download File / Folder</span>
            </a>
          </li>
        </ul>
      </div>

      <div className="mt-6 ulElement">
        <h6 className="text-blue-600 text-sm font-bold px-4">Actions</h6>
        <ul className="mt-3">
          <li>
            <a href="/"
              className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
              
              <span>Profile</span>
            </a>
          </li>
          <li>
            <a href="/"
              className="text-black hover:text-blue-600 text-sm flex items-center hover:bg-blue-50 rounded px-4 py-3 transition-all">
              
              <span>Logout</span>
            </a>
          </li>
        </ul>
      </div>
    </nav>
    </>);

}

export default Sidebar;