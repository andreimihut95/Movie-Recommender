package com.furiapolitehnicii.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.dom4j.DocumentException;

import com.furiapolitehnicii.constants.MovieConstants;
import com.furiapolitehnicii.movierecommender.MovieRecommenderRules;

public class MainRules {
	private static File rules = new File(MovieConstants.RESOURCES__PATH + File.separator + "rules.xml");
	private static File maps =new File(MovieConstants.RESOURCES__PATH + File.separator + "maps.xml");
	private static File recommendations =new File(MovieConstants.RESOURCES__PATH + File.separator + "recommendations.xml");

	public static void main(String[] args) throws IOException, DocumentException {
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Default config? (Y/N)");
		String option = bf.readLine();
		if(option.equalsIgnoreCase("n")){
			System.out.println("Choose file for Q/A rules: ");
			String userFile = bf.readLine();
			System.out.println("Choose file for recommendation rules: ");
			String recommFile = bf.readLine();
			System.out.println("Choose the map file for this xml(optional, hit enter if the file does not exists): ");
			String userMapFile = bf.readLine();
			
			
		   rules = new File(MovieConstants.RESOURCES__PATH + File.separator + userFile);
		   maps = new File(MovieConstants.RESOURCES__PATH + File.separator + userMapFile);
		   recommendations = new File(MovieConstants.RESOURCES__PATH+ File.separator + recommFile);
				
		}
		
		
		MovieRecommenderRules movieRecommender = new MovieRecommenderRules(rules,recommendations,maps);
		movieRecommender.run();
		movieRecommender.viewRecommendations();
	}

}
