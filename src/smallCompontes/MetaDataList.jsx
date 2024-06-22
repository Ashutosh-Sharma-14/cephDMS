import React, { useState } from "react";

const MetaDataList = ({ metadata }) => {


    

    // console.log(metadata);
    return (
        <table className="metadata-table">
        <tbody>
            {Object.entries(metadata).map((item, index) => (
            <tr key={index} className="hover:bg-gray-50">
                {/* Assuming you want to display each metadata key-value pair */}
                {Object.entries(item).map(([key, value]) => (
                    
                <div key={key}>
                    {/* works fine the data its self store like these */}
                    <td className="p-4 text-[15px] text-gray-800">{key}</td> 
                    <td className="p-4 text-[15px] text-gray-800">{value}</td>
                </div>
                ))}
            </tr>
            ))}
        </tbody>
        </table>
    );
};

export default MetaDataList;


// delete bucket 
//

