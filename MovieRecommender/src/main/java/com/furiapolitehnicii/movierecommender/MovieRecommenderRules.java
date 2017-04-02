package com.furiapolitehnicii.movierecommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.furiapolitehnici.models.Movie;
import com.furiapolitehnicii.constants.MapConstants;
import com.furiapolitehnicii.constants.MovieConstants;
import com.furiapolitehnicii.constants.RuleConstants;

public class MovieRecommenderRules {

	private File xmlFileForRules;
	private File mapFile;
	private File xmlFileForRecommendations;

	private List<Movie> recommendations = new ArrayList<Movie>();
	private Map<String, String> maps = new HashMap<String, String>();
	
	private static final String EXIT_MESSAGE = "Choose this option for partial recommendation (exit the Movie Recommendation)";

	public MovieRecommenderRules(File xmlFile, File xmlFileForRecommendations) {
		this.xmlFileForRules = xmlFile;
		this.xmlFileForRecommendations = xmlFileForRecommendations;
	}

	public MovieRecommenderRules(File xmlFile, File xmlFileForRecommendations, File mapFile) {
		this.xmlFileForRules = xmlFile;
		this.mapFile = mapFile;
		this.xmlFileForRecommendations = xmlFileForRecommendations;
	}

	private void loadMapFile() throws DocumentException {
		if (mapFile.exists() && mapFile.getName().endsWith(".xml")) {
			SAXReader reader = new SAXReader();
			Document document = reader.read(mapFile);
			String rootElement = document.getRootElement().getName();

			@SuppressWarnings("unchecked")
			List<Node> mapNodes = document.selectNodes("/" + rootElement + "/" + MapConstants.MAP_ELEMENT);
			for (Node mapNode : mapNodes) {
				String idMap = mapNode.valueOf("@" + MapConstants.ID_ATTRIBUTE);
				String actualContent = mapNode.getText();
				maps.put(actualContent, idMap);
			}

		} else {
			System.out.println("No map file was found. XML file is normally parsed.");
		}
	}

	private String getMapValue(String id) {
		if (maps.isEmpty()) {
			return id;
		} else {
			return maps.get(id);
		}
	}
	private static String getCorruptXMLMessage() {
		return "The XML file does not respect the structure for a movie recommender.";
	}

	@SuppressWarnings("unchecked")
	public void run() throws DocumentException, IOException {
		if (xmlFileForRules.exists() && xmlFileForRules.getName().endsWith(".xml")) {

			BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
			SAXReader reader = new SAXReader();
			Document document = reader.read(xmlFileForRules);
			loadMapFile();
			String rootElementName = document.getRootElement().getName();
			String rootQuestion = document.selectSingleNode("/" + rootElementName + "/" + RuleConstants.ROOT_QUESTION)
					.valueOf("@" + RuleConstants.QUETION_ATTRIBUTE);

			List<String> states = new ArrayList<String>();

			boolean isQuestion = true;
			List<Node> currentNodes = document.selectNodes("/" + rootElementName + "/" + RuleConstants.RULES_ELEMENT
					+ "/" + RuleConstants.RULE_ELEMENT + "[@if=" + "'" + rootQuestion + "'" + "]");

			while (currentNodes != null) {
				if (isQuestion == true) {
					int numberCount = 0;
					isQuestion = false;
					if(currentNodes.isEmpty()){
						System.out.println(getCorruptXMLMessage());
						break;
					}
					String question = currentNodes.get(0).valueOf("@" + RuleConstants.IF_ELEMENT);
					System.out.println(getMapValue(question));
					Map<Integer, String> answersMap = new HashMap<Integer, String>();

					for (Node currentNode : currentNodes) {
						String answer = currentNode.valueOf("@" + RuleConstants.THEN_ATTRIBUTE);
						numberCount++;
						answersMap.put(numberCount, answer);
						System.out.println(numberCount + ") " + getMapValue(answer));
					}
					System.out.println((++numberCount) + ") " + EXIT_MESSAGE);
					String userAnswer = bf.readLine();
					String xmlPathForRule = "/" + rootElementName + "/" + RuleConstants.RULES_ELEMENT + "/"
							+ RuleConstants.RULE_ELEMENT + "[@" + RuleConstants.IF_ATTRIBUTE + "='"
							+ answersMap.get(Integer.valueOf(userAnswer)) + "']";
					for (Node currentNode : currentNodes) {
						String answerNode = currentNode.valueOf("@" + RuleConstants.THEN_ATTRIBUTE);
						if (answerNode.equals(answersMap.get(Integer.valueOf(userAnswer)))) {
							states.add(currentNode.valueOf("@" + RuleConstants.STATE_ATTRIBUTE));
							break;
						}
					}
					currentNodes = document.selectNodes(xmlPathForRule);
				} else {
					if (currentNodes.isEmpty()) {
						break;
					}
					isQuestion = true;
					String question = currentNodes.get(0).valueOf("@" + RuleConstants.THEN_ATTRIBUTE);
					String xmlPathForRule = "/" + rootElementName + "/" + RuleConstants.RULES_ELEMENT + "/"
							+ RuleConstants.RULE_ELEMENT + "[@" + RuleConstants.IF_ATTRIBUTE + "='" + question + "']";
					currentNodes = document.selectNodes(xmlPathForRule);
				}
			}
			loadRecommendations(states);

		}
	}

	private void loadRecommendations(List<String> states) throws DocumentException {
		if (xmlFileForRecommendations.exists() && xmlFileForRecommendations.getName().endsWith(".xml")) {

			SAXReader reader = new SAXReader();
			Document document = reader.read(xmlFileForRecommendations);

			String rootElement = document.getRootElement().getName();
			@SuppressWarnings("unchecked")
			List<Node> ifNodes = document.selectNodes("/" + rootElement + "/" + RuleConstants.IF_ELEMENT);

			for (Node ifNode : ifNodes) {
				String state = ifNode.valueOf("@" + RuleConstants.STATE_ATTRIBUTE);
				boolean foundRecommendation = true;
				for (String givenState : states) {
					if (!state.contains(givenState)) {
						foundRecommendation = false;
					}
				}
				if (foundRecommendation == true) {
					Node recommendationNode = ifNode.selectSingleNode(MovieConstants.RECOMMENDATION_ELEMENT);

					String title = recommendationNode.valueOf("@" + MovieConstants.TITLE_ATTRIBUTE);
					String imdbURL = recommendationNode.valueOf("@" + MovieConstants.IMDB_URL_ATTRIBUTE);
					String imdbRating = recommendationNode.valueOf("@" + MovieConstants.IMDB_RATING_ATTRIBUTE);
					String photoURL = recommendationNode.valueOf("@" + MovieConstants.PHOTO_ATTRIBUTE);
					String description = recommendationNode.getText();

					Movie movie = new Movie(title, imdbURL, Double.valueOf(imdbRating), description, photoURL);
					recommendations.add(movie);
				}
			}
		}

	}

	public void viewRecommendations() {
		if (recommendations.size() != 0) {
			System.out.println("The following movies are recommended for you: ");
			for (Movie recommendation : recommendations) {
				System.out.println(recommendation);
				System.out.println("--------------------------------------------------------");
			}
		} else {
			System.out.println("No recommendations for you!");
		}
	}

}
