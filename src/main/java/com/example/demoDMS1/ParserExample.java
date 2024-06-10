package com.example.demoDMS1;

import com.example.demoDMS1.CustomParsers.WordStartingWithAParser;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import java.io.FileInputStream;
import java.io.InputStream;

public class ParserExample {
    public static void main(String[] args) throws Exception {
        // Create an instance of the custom parser
        WordStartingWithAParser parser = new WordStartingWithAParser();

        // Create input stream for the document to be parsed
        InputStream stream = new FileInputStream("src/main/resources/static/example.txt");

        // Create content handler to capture parsed content
        BodyContentHandler handler = new BodyContentHandler();

        // Create metadata object to hold metadata
        Metadata metadata = new Metadata();

        // Create parse context
        ParseContext context = new ParseContext();

        // Parse the document
        parser.parse(stream, handler, metadata, context);

        // Display the parsed content
        System.out.println("Parsed content:");
        System.out.println(handler.toString());

        // Close the input stream
        stream.close();
    }
}
