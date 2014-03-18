package org.benetech.daisyimgexample;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class DaisyXmlParser {
	private static final String namespace = null;
	private static final String bookTag = "dtbook";
	private static final String imgGroupTag = "imggroup";
	   
    public ImageGroup parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    
    private ImageGroup readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "dtbook");
        ImageGroup group = null;
        System.out.println("Starting parse");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the img tag
            if (name.equals("imggroup")) {
            	System.out.println("Pasring imggroup");
                group = readImageGroup(parser);
            } else {
                skip(parser);
            }
        }  
        return group;
    }
    
    private ImageGroup readImageGroup(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, namespace, "imggroup");
        List<DaisyImage> daisyImages = new ArrayList<DaisyImage>();
        String prodNote = "";
        String caption = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("img")){
            	System.out.println("Parsing image");
                daisyImages.add(readImage(parser));
                System.out.println("Parsing image complete");
            } else if(name.equals("prodnote")) {
            	System.out.println("Parsing prodnote");
            	prodNote = readProdNote(parser);
            	System.out.println("Parsing prodnote complete");
            } else if(name.equals("caption")) {
            	System.out.println("PArsing caption");
            	caption = readCaption(parser);
            	System.out.println("Pasring caption complete");
            } else {
                skip(parser);
            }
        }
        return new ImageGroup(daisyImages, prodNote, caption);
    }
    
    private DaisyImage readImage(XmlPullParser parser) throws XmlPullParserException, IOException {
    	parser.require(XmlPullParser.START_TAG, namespace, "img");
        String src = parser.getAttributeValue(namespace, "src");
        String alt = parser.getAttributeValue(namespace, "alt");
        parser.nextTag();
        return new DaisyImage(src, alt);
    }
    
    private String readProdNote(XmlPullParser parser) throws XmlPullParserException, IOException {
    	String prodNote = "";
    	parser.require(XmlPullParser.START_TAG, namespace, "prodnote");
    	if(parser.getAttributeValue(namespace, "showin").equals("xlp")) {
    		prodNote = readText(parser);
    	} else {
    		readText(parser);
    	}
    	parser.require(XmlPullParser.END_TAG, namespace, "prodnote");
    	return prodNote;
    }
    
    private String readCaption(XmlPullParser parser) throws XmlPullParserException, IOException {
    	String caption = "";
    	parser.require(XmlPullParser.START_TAG, namespace, "caption");
    	caption = readText(parser);
    	parser.require(XmlPullParser.END_TAG, namespace, "caption");
    	return caption;
    }
    
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }
}
