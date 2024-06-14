import { useState } from 'react';
import './uploadCard.css';
import Metadata from '../../Jsons/dropDownJson.json';

const UploadCard = ({ handleObject, object }) => {
    const [fileName, setFileName] = useState('Plese Add File');
    const [dropdownOpen, setDropDownOpen] = useState(false);
    const [domain, setDomain] = useState('Domain');
    const [domains, setDomains] = useState([]);
    const [meta, setMeta] = useState({});
    const [table, setTable] = useState({
        fileName: '',
        metadataJson: {},
        fileArray: []
    });

    const handFields = (e) => {
        const { name, value } = e.target;

        setMeta((prevMeta) => ({
            ...prevMeta,
            [name]: value
        }));

        setTable((prevTable) => ({
            ...prevTable,
            metadataJson: {
                ...prevTable.metadataJson,
                [name]: value
            }
        }));
    };

    const handleSelection = (value) => {
        setDomain(value);
        setDomains(Metadata[value] || []);
        setDropDownOpen(false);
        setTable((prevTable) => ({
            ...prevTable,
            Domain: value
        }));
    };

    const handleFileChange = (event) => {
        if (event.target.files.length > 0) {
            const selectedFile = event.target.files[0];
            setFileName(selectedFile.name);
            setTable((prevTable) => ({
                ...prevTable,
                fileName: selectedFile.name,
                fileArray: [...prevTable.fileArray, selectedFile]
            }));
        }
    };

    const handleUploadButton = (e) => {
        e.preventDefault();
        if (domain === 'Domain') {
            alert('Please select a domain before uploading.');
            return;
        }
        handleObject(table);
        // console.log(object);


        // Clear all the fields and reset values
        setFileName('Plese Add File');
        setDomain('Domain');
        setDomains([]);
        setMeta({});
        setTable({
            fileName: '',
            metadataJson: {},
            fileArray: []
        });
    };

    return (
        <div className="uploadCard">
            <div className="key">
                <p className='filename'>FileName : {fileName}</p>
                <label htmlFor="uploadFile1" className="flex bg-gray-800 hover:bg-gray-700 text-white text-base px-5 py-3 outline-none rounded w-max cursor-pointer mx-auto font-[sans-serif]">
                    <svg xmlns="http://www.w3.org/2000/svg" className="w-6 mr-2 fill-white inline" viewBox="0 0 32 32">
                        <path d="M23.75 11.044a7.99 7.99 0 0 0-15.5-.009A8 8 0 0 0 9 27h3a1 1 0 0 0 0-2H9a6 6 0 0 1-.035-12 1.038 1.038 0 0 0 1.1-.854 5.991 5.991 0 0 1 11.862 0A1.08 1.08 0 0 0 23 13a6 6 0 0 1 0 12h-3a1 1 0 0 0 0 2h3a8 8 0 0 0 .75-15.956z" />
                        <path d="M20.293 19.707a1 1 0 0 0 1.414-1.414l-5-5a1 1 0 0 0-1.414 0l-5 5a1 1 0 0 0 1.414 1.414L15 16.414V29a1 1 0 0 0 2 0V16.414z" />
                    </svg>
                    Add File
                    <input type="file" id="uploadFile1" className="hidden" onChange={handleFileChange} />
                </label>
            </div>

            <div className="metadata">
                <div className="relative font-[sans-serif] w-max mx-auto">
                    <button
                        type="button"
                        className="px-5 py-2.5 rounded text-white text-sm font-semibold border-none outline-none bg-blue-600 hover:bg-blue-700 active:bg-blue-600"
                        onClick={() => setDropDownOpen(!dropdownOpen)}
                    >
                        {domain}
                        <svg xmlns="http://www.w3.org/2000/svg" className="w-3 fill-white inline ml-3" viewBox="0 0 24 24">
                            <path
                                fillRule="evenodd"
                                d="M11.99997 18.1669a2.38 2.38 0 0 1-1.68266-.69733l-9.52-9.52a2.38 2.38 0 1 1 3.36532-3.36532l7.83734 7.83734 7.83734-7.83734a2.38 2.38 0 1 1 3.36532 3.36532l-9.52 9.52a2.38 2.38 0 0 1-1.68266.69734z"
                                clipRule="evenodd"
                            />
                        </svg>
                    </button>

                    <ul className="absolute shadow-lg bg-white py-2 z-[1000] min-w-full w-max rounded max-h-96 overflow-auto" style={{ display: dropdownOpen ? 'block' : 'none' }}>
                        {Object.keys(Metadata).map((key, idx) => (
                            <li key={idx} className="py-2.5 px-5 hover:bg-blue-50 text-black text-sm cursor-pointer" onClick={() => handleSelection(key)}>{key}</li>
                        ))}
                    </ul>
                </div>

                {domains.map((item, idx) => (
                    <input
                        key={idx}
                        type="text"
                        placeholder={item}
                        name={item}
                        className="inputField px-4 py-1.5 text-sm rounded-md bg-white border border-gray-400 w-full outline-blue-500 my-2"
                        onChange={handFields}
                    />
                ))}

                <button
                    type="button"
                    className="uploadBtnForTable px-5 py-2.5 rounded-lg text-sm tracking-wider font-medium border border-current outline-none bg-green-700 hover:bg-transparent text-white hover:text-green-700 transition-all duration-300"
                    onClick={handleUploadButton}
                    disabled={fileName === 'Plese Add File'}
                >
                    Upload File
                </button>
            </div>
        </div>
    );
};

export default UploadCard;
