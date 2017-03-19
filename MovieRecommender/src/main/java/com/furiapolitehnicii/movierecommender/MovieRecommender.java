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

public class MovieRecommender {

	private File xmlFile;
	private File mapFile;
	private List<Movie> recommendations = new ArrayList<Movie>();
	private Map<String, String> maps = new HashMap<String, String>();

	public MovieRecommender(File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public MovieRecommender(File xmlFile, File mapFile) {
		this.xmlFile = xmlFile;
		this.mapFile = mapFile;
	}

	private static String getCorruptXMLMessage() {
		StringBuilder message = new StringBuilder();
		message.append(
				"The XML file does not respect the structure for a movie recommender.\nThe XML file shall contain only 4 elements: ");
		message.append(MovieConstants.QUESTION_ELEMENT);
		message.append(", ");
		message.append(MovieConstants.ANSWER_ELEMENT);
		message.append(", ");
		message.append(MovieConstants.FINAL_ANSWER_ELEMENT);
		message.append(", ");
		message.append(MovieConstants.RECOMMENDATION_ELEMENT);
		message.append('.');
		message.append("Process aborted!");
		return message.toString();
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

	public void run() throws DocumentException, IOException {
		if (xmlFile.exists() && xmlFile.getName().endsWith(".xml")) {

			BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
			SAXReader reader = new SAXReader();
			Document document = reader.read(xmlFile);
			String rootElementName = document.getRootElement().getName();
			Node currentNode = document.selectSingleNode("/" + rootElementName + "/" + MovieConstants.QUESTION_ELEMENT);
			loadMapFile();
			while (currentNode != null
					&& !currentNode.getName().equalsIgnoreCase(MovieConstants.RECOMMENDATION_ELEMENT)) {

				if (currentNode.getName().equalsIgnoreCase(MovieConstants.QUESTION_ELEMENT)) {
					String question = currentNode.valueOf("@" + MovieConstants.QUESTION_ATTRIBUTE);
					System.out.println(getMapValue(question));
					@SuppressWarnings("unchecked")
					List<Node> answerNodes = currentNode
							.selectNodes(MovieConstants.ANSWER_ELEMENT + "|" + MovieConstants.FINAL_ANSWER_ELEMENT);
					int numberCount = 0;
					Map<Integer, String> answersMap = new HashMap<Integer, String>();
					for (Node answerNode : answerNodes) {
						numberCount++;
						String answer = answerNode.valueOf("@" + MovieConstants.ANSWER_ATTRIBUTE);
						answersMap.put(numberCount, answer);
						System.out.println(numberCount + ")" + getMapValue(answer));
					}
					String userAnswer = bf.readLine();
					String xPathForAnswerNode = MovieConstants.ANSWER_ELEMENT + "[@answer=" + "'"
							+ answersMap.get(Integer.valueOf(userAnswer)) + "'" + "]";
					Node auxNode = currentNode.selectSingleNode(xPathForAnswerNode);
					if (auxNode == null) {
						xPathForAnswerNode = MovieConstants.FINAL_ANSWER_ELEMENT + "[@answer=" + "'"
								+ answersMap.get(Integer.valueOf(userAnswer)) + "'" + "]";
						currentNode = currentNode.selectSingleNode(xPathForAnswerNode);
					} else {
						currentNode = auxNode;
					}
				} else if (currentNode.getName().equalsIgnoreCase(MovieConstants.ANSWER_ELEMENT)) {
					currentNode = currentNode.selectSingleNode(MovieConstants.QUESTION_ELEMENT);

				} else if (currentNode.getName().equals(MovieConstants.FINAL_ANSWER_ELEMENT)) {
					@SuppressWarnings("unchecked")
					List<Node> recommendationNodes = currentNode.selectNodes(MovieConstants.RECOMMENDATION_ELEMENT);
					for (Node recommendationNode : recommendationNodes) {
						String title = recommendationNode.valueOf("@" + MovieConstants.TITLE_ATTRIBUTE);
						String imdbURL = recommendationNode.valueOf("@" + MovieConstants.IMDB_URL_ATTRIBUTE);
						String imdbRating = recommendationNode.valueOf("@" + MovieConstants.IMDB_RATING_ATTRIBUTE);
						String photoURL = recommendationNode.valueOf("@" + MovieConstants.PHOTO_ATTRIBUTE);
						String description = recommendationNode.getText();

						Movie movie = new Movie(title, imdbURL, Double.valueOf(imdbRating), description, photoURL);
						recommendations.add(movie);
					}
					currentNode = currentNode.selectSingleNode(MovieConstants.RECOMMENDATION_ELEMENT);

				} else {
					System.out.println(getCorruptXMLMessage());
					break;
				}
			}
			bf.close();
		} else {
			System.out.println("XML file was not found. Process aborted!");
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
