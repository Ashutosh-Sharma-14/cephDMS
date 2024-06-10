package com.example.demoDMS1.CustomParsers;

import org.apache.tika.parser.AbstractParser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;

public class WordStartingWithAParser extends AbstractParser {
    private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.application("hello"));
    public static final String HELLO_MIME_TYPE = "application/hello";

    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
            throws IOException, SAXException, TikaException {

        metadata.set(Metadata.CONTENT_TYPE, HELLO_MIME_TYPE);

        // Regular expression to match words starting with "a"
        Pattern pattern = Pattern.compile("\\b[aA]\\w*\\b");

        XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();

        // Read input stream and process words
        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder wordBuffer = new StringBuilder();
        while ((bytesRead = stream.read(buffer)) != -1) {
            String text = new String(buffer, 0, bytesRead);
            Matcher matcher = pattern.matcher(text);
            int lastMatchEnd = 0;
            while (matcher.find()) {
                // Process each word starting with "a"
                String word = matcher.group();
                xhtml.startElement("p");
                xhtml.characters(word.toCharArray(), 0, word.length());
                xhtml.endElement("p");
                // Adjust buffer position to continue searching from the end of the matched word
                lastMatchEnd = matcher.end();
            }
            // Append remaining text to the buffer for the next iteration
            wordBuffer.append(text.substring(lastMatchEnd));
        }
        // Handle any remaining text in the buffer
        Matcher matcher = pattern.matcher(wordBuffer.toString());
        while (matcher.find()) {
            String word = matcher.group();
            xhtml.startElement("p");
            xhtml.characters(word.toCharArray(), 0, word.length());
            xhtml.endElement("p");
        }

        xhtml.endDocument();
    }
}
