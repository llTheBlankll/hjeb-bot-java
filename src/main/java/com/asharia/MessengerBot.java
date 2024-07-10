package com.asharia;

import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessengerBot {

	private static final Logger log = LoggerFactory.getLogger(MessengerBot.class);

	// Other Classes
	private final ScreenReader screenReader = new ScreenReader();
	private final TypingBot typingBot = new TypingBot();

	private final List<Question> questions = new ArrayList<>();

	public MessengerBot() throws AWTException, InterruptedException {
		if (!screenReader.setPosition()) {
			throw new RuntimeException("Failed to set position");
		}

		typingBot.setMousePosition();

		// Set possible questions
		questions.add(new Question("MAGULO", "Messy"));
		questions.add(new Question("MALUPIT", "Cruel"));
		questions.add(new Question("SAGABAL", "obstacle"));
		questions.add(new Question("HANDOG", "offering"));
		questions.add(new Question("NAPAGOD", "tired"));
	}

	private boolean isQuestionFound(String message) {
		for (Question question : questions) {
			if (message.contains(question.getQuestion())) {
				return true;
			}
		}

		return false;
	}

	private String answerQuestion(String message) {
		for (Question question : questions) {
			if (message.contains(question.getQuestion())) {
				return question.getAnswer();
			}
		}
		return null;
	}

	private boolean isCorrect(String message) {
		return message.contains("correct");
	}

	private boolean isNextButtonFound(String message) {
		return message.contains("Next");
	}

	public void start() throws TesseractException, IOException, InterruptedException {
		String message;
		while (true) {
			// ! FIRST STEP: Check for questions
			while (true) {
				typingBot.moveAndScroll();
				message = screenReader.readScreen();
				if (isQuestionFound(message)) {
					log.info("Question found: {}", message);
					String answer = answerQuestion(message);
					if (answer == null) {
						log.error("Failed to answer question: {}", message);
						System.exit(1);
					}

					log.info("Answer: {}", answer);
					typingBot.write(answer);
//					typingBot.pressEnter();
					break;
				}
				log.error("Waiting for the question...");
				Thread.sleep(1000);
			}

			// ! SECOND STEP: Check if the answer is correct
			while (true) {
				typingBot.moveAndScroll();
				message = screenReader.readScreen();
				if (isCorrect(message)) {
					log.info("Correct answer: {}", message);
					break;
				}
				log.error("Waiting for the correct answer...");
				Thread.sleep(1000);
			}

			// ! THIRD STEP: Check for the existence of the next button.
			while (true) {
				typingBot.moveAndScroll();
				message = screenReader.readScreen();
				if (isNextButtonFound(message)) {
					log.info("Next button found: {}", message);
					log.info("Moving to the next question");
					typingBot.write("Next");
//					typingBot.pressEnter();
					break;
				}
				log.error("Looking for the next button...");
				Thread.sleep(1000);
			}
		}
	}
}