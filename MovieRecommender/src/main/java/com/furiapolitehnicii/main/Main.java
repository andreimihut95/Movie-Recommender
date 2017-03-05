package com.furiapolitehnicii.main;

import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class Main {
	
	public static void main(String[] args) throws DocumentException, IOException
	{
		File xmlFile = new File("src/main/resources/movies.xml");
		SAXReader reader = new SAXReader();
		Document document = reader.read(xmlFile);
		
		System.out.println(document.asXML());
	}
}
