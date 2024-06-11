const HomeScreen = () => {
    
    return (<>
    <section class="bg-gray-50">
  <div class="mx-auto max-w-screen-xl px-4 py-32 lg:flex lg:h-screen lg:items-center">
    <div class="mx-auto max-w-xl text-center">
      <h1 class="text-3xl font-extrabold sm:text-5xl">
        Document Management 
        <strong class="font-extrabold text-red-700 sm:block">System Using Ceph. </strong>
      </h1>

      <p class="mt-4 sm:text-xl/relaxed">
      A system that can stand by Your Traffic demand
      </p>

      <div class="mt-8 flex flex-wrap justify-center gap-4">
        <a
          class="block w-full rounded bg-red-600 px-12 py-3 text-sm font-medium text-white shadow hover:bg-red-700 focus:outline-none focus:ring active:bg-red-500 sm:w-auto"
          href="/"
        >
          Documentations
        </a>

        <a
          class="block w-full rounded px-12 py-3 text-sm font-medium text-red-600 shadow hover:text-red-700 focus:outline-none focus:ring active:text-red-500 sm:w-auto"
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