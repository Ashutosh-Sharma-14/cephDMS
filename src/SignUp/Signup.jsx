import { useState } from "react";
import { Link } from "react-router-dom";
import './signup.css'


const Signup = () =>{
  const [open, setOpen]= useState(false);
    return <>
    <div className="flex flex-col justify-center font-[sans-serif] text-[#333] sm:h-screen p-4">
      <div className="max-w-md w-full mx-auto border border-gray-300 rounded-md p-6">
        <div className="text-center mb-12">
          <Link to="/"><img
            src="/perfiosLogo.png" alt="logo" className='w-40 inline-block' />
          </Link>
        </div>
        <form>
          <div className="space-y-6">
            <div>
            <div class="relative font-[sans-serif] w-max mx-auto">
              <button type="button"
                onClick={()=>setOpen(!open)}
                class="dropDownBtn px-5 py-2.5 border border-gray-300 text-gray-800 text-sm outline-none bg-white hover:bg-gray-50">
                User Role
                <svg xmlns="http://www.w3.org/2000/svg" class="w-2 fill-gray-500 inline ml-3" viewBox="0 0 24 24">
                  <path fill-rule="evenodd"
                    d="M11.99997 18.1669a2.38 2.38 0 0 1-1.68266-.69733l-9.52-9.52a2.38 2.38 0 1 1 3.36532-3.36532l7.83734 7.83734 7.83734-7.83734a2.38 2.38 0 1 1 3.36532 3.36532l-9.52 9.52a2.38 2.38 0 0 1-1.68266.69734z"
                    clip-rule="evenodd" data-original="#000000" />
                </svg>
              </button>

              <ul style={{display:open ? 'block':'none',width:'40% !important'}} class='dorpDownList absolute shadow-[0_8px_19px_-7px_rgba(6,81,237,0.2)] bg-white py-2 z-[1000] min-w-full w-max divide-y max-h-96 overflow-auto'>
                <li class='py-3 px-5 hover:bg-gray-50 text-gray-800 text-sm cursor-pointer'>Ubi Zonal Admin</li>
                <li class='py-3 px-5 hover:bg-gray-50 text-gray-800 text-sm cursor-pointer'>Boi Zonal Admin</li>
                <li class='py-3 px-5 hover:bg-gray-50 text-gray-800 text-sm cursor-pointer'>Ubi Branch Officer</li>
              </ul>
            </div>
            </div>
            <div>
              <label className="text-sm mb-2 block">Email Id</label>
              <input name="email" type="text" className="bg-white border border-gray-300 w-full text-sm px-4 py-3 rounded-md outline-blue-500" placeholder="Enter email" />
            </div>
            <div>
              <label className="text-sm mb-2 block">Password</label>
              <input name="password" type="password" className="bg-white border border-gray-300 w-full text-sm px-4 py-3 rounded-md outline-blue-500" placeholder="Enter password" />
            </div>
            <div>
              <label className="text-sm mb-2 block">Confirm Password</label>
              <input name="cpassword" type="password" className="bg-white border border-gray-300 w-full text-sm px-4 py-3 rounded-md outline-blue-500" placeholder="Enter confirm password" />
            </div>
            {/* <div className="flex items-center">
              <input id="remember-me" name="remember-me" type="checkbox" className="h-4 w-4 shrink-0 text-blue-600 focus:ring-blue-500 border-gray-300 rounded" />
              <label for="remember-me" className="ml-3 block text-sm">
                I accept the <Link to="/" className="text-blue-600 font-semibold hover:underline ml-1">Terms and Conditions</Link>
              </label>
            </div> */}
          </div>
          <div className="!mt-10">
            <button type="button" className="w-full py-3 px-4 text-sm font-semibold rounded text-white bg-blue-500 hover:bg-blue-600 focus:outline-none">
              Create an account
            </button>
          </div>
          <p className="text-sm mt-6 text-center">Already have an account? <Link to="/login" className="text-blue-600 font-semibold hover:underline ml-1">Login here</Link></p>
        </form>
      </div>
    </div>
    </>
}

export default Signup;