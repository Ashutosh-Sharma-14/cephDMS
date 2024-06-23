import React from "react";

const MetaDataList = ({ metadata }) => {
    return (
        <table className="min-w-full bg-white">
            <thead className="bg-gray-100 whitespace-nowrap">
                <tr>
                    <th className="p-4 text-left text-xs font-semibold text-gray-800">
                        Key
                    </th>
                    <th className="p-4 text-left text-xs font-semibold text-gray-800">
                        Values
                    </th>
                </tr>
            </thead>
            <tbody className="whitespace-nowrap">
                {Object.entries(metadata).map(([key, value], index) => (
                    <tr key={index} className="hover:bg-gray-50">
                        <td className="p-4 text-[15px] text-gray-800">{key}</td>
                        <td className="p-4 text-[15px] text-gray-800">{value}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
};

export default MetaDataList;
