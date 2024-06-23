


import '../homePage/home.css'
import Navbar from '../navbar/Navbar';
import Sidebar from '../sidebar/Sidebar';
import HomeScreen from '../HomeScreen/HomeScreen'
import ObjectCard from '../smallCompontes/ObjectCard';


function Home() {
  return (
    <div className="App">
        <div className='navbar'>
          <Navbar />
        </div>
        <div className='wrapper'>
            <div className='sidebar'>
              <Sidebar />
            </div>
            <div className='home'>
              <HomeScreen />
            </div>
        </div>
    </div>
  );
}

export default Home;
