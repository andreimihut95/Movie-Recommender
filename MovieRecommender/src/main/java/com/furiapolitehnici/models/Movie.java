package com.furiapolitehnici.models;

public class Movie {

	private String title;
	private String imdbURL;
	private double imdbRating;
	private String description;
	private String photoURL;

	public Movie(String title, String imdbURL, double imdbRating, String description, String photoURL) {
		this.title = title;
		this.imdbURL = imdbURL;
		this.imdbRating = imdbRating;
		this.description = description;
		this.photoURL = photoURL;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImdbURL() {
		return imdbURL;
	}

	public void setImdbURL(String imdbURL) {
		this.imdbURL = imdbURL;
	}

	public double getImdbRating() {
		return imdbRating;
	}

	public void setImdbRating(double imdbRating) {
		this.imdbRating = imdbRating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhotoURL() {
		return photoURL;
	}

	public void setPhotoURL(String photoURL) {
		this.photoURL = photoURL;
	}

	@Override
	public String toString() {
		StringBuilder toString = new StringBuilder();
		toString.append("Title: " + title);
		toString.append(System.lineSeparator());
		toString.append("IMDB URL: " + imdbURL);
		toString.append(System.lineSeparator());
		toString.append("IMDB rating: " + imdbRating);
		toString.append(System.lineSeparator());
		toString.append("Photo URL: " + photoURL);
		toString.append(System.lineSeparator());
		toString.append("Description: " + description);
		
		return toString.toString();
	}

}
