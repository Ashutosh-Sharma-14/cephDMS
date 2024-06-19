import './homeScreen.css'

const HomeScreen = () => {
    
    return (<>
    <section className="bg-gray-50">
  <div className="mx-auto max-w-screen-xl px-4 py-3 lg:flex lg:h-screen lg:items-center">
    <div className="mainContent mx-auto max-w-xl text-center">
      <h1 className="text-3xl font-extrabold sm:text-5xl">
        Document Management 
        <strong className="font-extrabold text-red-700 sm:block">System Using Ceph. </strong>
      </h1>

      <p className="mt-4 sm:text-xl/relaxed">
      A system that can stand by Your Traffic demand
      </p>

      <div className="mt-8 flex flex-wrap justify-center gap-4">
        <a
          className="block w-full rounded bg-red-600 px-12 py-3 text-sm font-medium text-white shadow hover:bg-red-700 focus:outline-none focus:ring active:bg-red-500 sm:w-auto"
          href="/"
        >
          Documentations
        </a>

        <a
          className="block w-full rounded px-12 py-3 text-sm font-medium text-red-600 shadow hover:text-red-700 focus:outline-none focus:ring active:text-red-500 sm:w-auto"
          href="/"
        >
          Learn More about System
        </a>
      </div>

    </div>
  </div>
</section>
    </>);

}

export default HomeScreen;