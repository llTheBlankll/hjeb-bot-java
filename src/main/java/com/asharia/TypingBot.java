package com.asharia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TypingBot {
	private static final Logger log = LoggerFactory.getLogger(TypingBot.class);
	private final Robot robot;
	private int mouseX = 0;
	private int mouseY = 0;

	public TypingBot() throws AWTException {
		robot = new Robot();
		initializeCharToKeyCodeMap();
	}

	private void initializeCharToKeyCodeMap() {
	}

	public void write(String content) {
		for (char character : content.toCharArray()) {
			typeCharacter(character);
		}
	}

	private void typeCharacter(char character) {
		boolean isUpperCase = Character.isUpperCase(character);
		character = Character.toLowerCase(character);

		if (isUpperCase) {
			robot.keyPress(KeyEvent.VK_SHIFT);
		}

		int keyCode = KeyEvent.getExtendedKeyCodeForChar(character);
		robot.keyPress(keyCode);
		robot.keyRelease(keyCode);

		if (isUpperCase) {
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}
	}

	public void pressEnter() {
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
	}

	public void moveAndScroll() {
		// Mouse Scroll
		robot.mouseMove(mouseX, mouseY);
		robot.delay(250);
		robot.mouseWheel(5);
	}

	public void setMousePosition() throws InterruptedException {
		// Prompt the user to enter the coordinates of the screenshot by clicking the mouse
		// on the top-left and bottom-right corners of the area to be captured
		// The coordinates are then stored in the x1, y1, x2, and y2 variables
		final CountDownLatch latch = new CountDownLatch(1);
		JFrame frame = new JFrame("Select Area");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setOpacity(0.1f); // Make the window transparent
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); // Set cursor to cross hair

		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (mouseX == 0 && mouseY == 0) {
					mouseX = e.getXOnScreen();
					mouseY = e.getYOnScreen();
					log.debug("First point set: x={}, y={}", mouseX, mouseY);
					frame.dispose(); // Close the frame after the second point is selected
					latch.countDown();
				}
			}
		});

		frame.setVisible(true);
		// Wait for the user to select the area
		latch.await();
		log.debug("Position set successfully");
	}
}