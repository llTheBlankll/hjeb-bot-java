package com.asharia;

public class Question {
	private String question;
	private String answer;

	public Question() {

	}

	public Question(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}

	public String getQuestion() {
		return question;
	}

	public Question setQuestion(String question) {
		this.question = question;
		return this;
	}

	public String getAnswer() {
		return answer;
	}

	public Question setAnswer(String answer) {
		this.answer = answer;
		return this;
	}
}