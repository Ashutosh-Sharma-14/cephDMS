import { Link } from "react-router-dom";


const Signup = () =>{
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
              <label className="text-sm mb-2 block">User Role</label>
              <input name="text" type="text" className="bg-white border border-gray-300 w-full text-sm px-4 py-3 rounded-md outline-blue-500" placeholder="Enter user role" />
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