
import './bucketCard.css'

const BucketCard = () =>{
    return<div className='bucketCard'>
        <div
      class="shadow-[0_5px_14px_-4px_rgba(0,0,0,0.3)] p-6 w-full max-w-sm rounded-lg font-[sans-serif] overflow-hidden mx-auto mt-4">
      <div class="flex items-center">
        <h3 class="text-2xl font-semibold  flex-1">Trends</h3>
        <div class="bg-gray-900 w-12 h-12 p-1 flex items-center justify-center rounded-full cursor-pointer">
          {/* <svg xmlns="http://www.w3.org/2000/svg" width="32px" viewBox="0 0 24 24">
            <g class="fill-yellow-400">
              <circle cx="12" cy="12" r="5" />
              <path fill="#f2b108"
                d="M21 13h-1a1 1 0 0 1 0-2h1a1 1 0 0 1 0 2zM4 13H3a1 1 0 0 1 0-2h1a1 1 0 0 1 0 2zm13.66-5.66a1 1 0 0 1-.66-.29 1 1 0 0 1 0-1.41l.71-.71a1 1 0 1 1 1.41 1.41l-.71.71a1 1 0 0 1-.75.29zM5.64 19.36a1 1 0 0 1-.71-.29 1 1 0 0 1 0-1.41l.71-.66a1 1 0 0 1 1.41 1.41l-.71.71a1 1 0 0 1-.7.24zM12 5a1 1 0 0 1-1-1V3a1 1 0 0 1 2 0v1a1 1 0 0 1-1 1zm0 17a1 1 0 0 1-1-1v-1a1 1 0 0 1 2 0v1a1 1 0 0 1-1 1zM6.34 7.34a1 1 0 0 1-.7-.29l-.71-.71a1 1 0 0 1 1.41-1.41l.71.71a1 1 0 0 1 0 1.41 1 1 0 0 1-.71.29zm12.02 12.02a1 1 0 0 1-.7-.29l-.66-.71A1 1 0 0 1 18.36 17l.71.71a1 1 0 0 1 0 1.41 1 1 0 0 1-.71.24z"
                data-original="#f2b108" />
            </g>
          </svg> */}
          <img src="/bucket.jpg" alt="" />
        </div>
      </div>

      <p class="text-sm  my-8 leading-relaxed">Lorem ipsum dolor sit amet, consectetur ipsum dolor sit amet, consectetur Lorem
        ipsum dolor sit amet, consectetur ipsum.</p>

      <div class="flex items-center">
        <h3 class="text-lg flex-1">Dark theme</h3>
        <label class="relative cursor-pointer">
          <input type="checkbox" class="sr-only peer"  />
          <div
            class="w-11 h-3 flex items-center bg-gray-300 rounded-full peer peer-checked:after:translate-x-full after:absolute after:left-0 peer-checked:after:-left-1 after:bg-gray-300 peer-checked:after:bg-[#007bff] after:border after:border-gray-300 peer-checked:after:border-[#007bff] after:rounded-full after:h-6 after:w-6 after:transition-all peer-checked:bg-[#007bff]">
          </div>
        </label>
      </div>
    </div>
    </div>
}

export default BucketCard;