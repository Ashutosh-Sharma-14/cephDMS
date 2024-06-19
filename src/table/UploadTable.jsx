
import Row from '../smallCompontes/uploadCard/Row';
import './uploadtable.css'

const UploadTable = ({object, handleDelete}) =>{
  


    return <div className='uploadtable'>
        <div className="font-sans overflow-y">
      <table className="min-w-full bg-white">
        <thead className="bg-gray-100 whitespace-nowrap">
          <tr>
            <th className="p-4 text-left text-xs font-semibold text-gray-800">
              File Name
            </th>
            <th className="p-4 text-left text-xs font-semibold text-gray-800">
              Domain
            </th>
            <th className="p-4 text-left text-xs font-semibold text-gray-800">
              MetaData
            </th>
            <th className="p-4 text-left text-xs font-semibold text-gray-800">
              Actions
            </th>
          </tr>
        </thead>

        <tbody className="whitespace-nowrap tbodyClass" >

          {
              object.map((item, idx) => (
                  <Row key={idx} item={item} handleDelete={handleDelete}  />
                ))
                  
          }  

        </tbody>
      </table>
    </div>
    </div>
}


export default UploadTable;