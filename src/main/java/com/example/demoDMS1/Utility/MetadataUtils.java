package com.example.demoDMS1.Utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Arrays;

public class MetadataUtils {
    ObjectMapper objectMapper = new ObjectMapper();

    public static Metadata MetadataOperations(InputStream inputStream, String uploadPath,String fileName) throws Exception {
        Metadata metadata = extractMetadata(inputStream);
        printMetadata(metadata);

        metadata = new MetadataUtils().modifyMetadata(metadata);
        writeMetadataToFile(inputStream,metadata,uploadPath + "/" + fileName);
        printMetadata(metadata);
        return metadata;
    }

    public static Metadata extractMetadata(InputStream inputStream) throws Exception{
        Metadata metadata = new Metadata();
        Parser parser = new AutoDetectParser();
        ParseContext parseContext = new ParseContext();
        parser.parse(inputStream, new BodyContentHandler(), metadata, parseContext);

        // Close the input stream after parsing
        inputStream.close();
        return metadata;
    }

    public static void printMetadata(Metadata metadata){
        System.out.println("Metadata: ");
        for (String name : metadata.names()) {
            System.out.println(name + ": " + metadata.get(name));
        }
        System.out.println();
    }

    @Data
    @AllArgsConstructor
    class Author{
        private String name;
        private String authorId;
        private int age;
    }

//    different data types can be added to the metadata by converting it into string and when required, convert the string to the original data
    public Metadata modifyMetadata(Metadata metadata) {
        metadata.set("dc:title", "My document");
        metadata.set("dc:creator","Ashutosh Sharma");

        metadata.add("LineOfBusiness","MSME");
        metadata.add("SanctionDate",LocalDate.now().toString());

        Author author = new Author("Ashutosh","a123",23);

        try{
            metadata.add("dc:author",objectMapper.writeValueAsString(author));
            System.out.println(metadata.get("dc:author"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return metadata;
    }

    public static void writeMetadataToFile(InputStream inputStream, Metadata metadata, String filePath) throws IOException, SAXException, TikaException {
//        create parser instance
        AutoDetectParser parser = new AutoDetectParser();
        ParseContext parseContext = new ParseContext();

        try(FileOutputStream outputStream = new FileOutputStream(filePath)){
            parser.parse(inputStream, new BodyContentHandler(outputStream), metadata, parseContext);
        }
        catch(Exception e){
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
