package com.furiapolitehnicii.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.dom4j.DocumentException;
import com.furiapolitehnicii.constants.MovieConstants;
import com.furiapolitehnicii.movierecommender.MovieRecommender;

public class Main {

	
	public static void main(String[] args) throws DocumentException, IOException {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Choose file for movie recommender: ");
		String userFile = bf.readLine();
		System.out.println("Choose the map file for this xml(optional, hit enter if the file does not exists): ");
		String userMapFile = bf.readLine();
		File xmlFile = new File(MovieConstants.RESOURCES__PATH + File.separator + userFile);
		File mapFile = new File(MovieConstants.RESOURCES__PATH + File.separator + userMapFile);
		
		MovieRecommender movieRecommender = new MovieRecommender(xmlFile, mapFile);
		movieRecommender.run();
		movieRecommender.viewRecommendations();

	}
}
