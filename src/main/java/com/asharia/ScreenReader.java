package com.asharia;

import dev.mccue.imgscalr.Scalr;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ScreenReader {

	private static final Logger log = LoggerFactory.getLogger(ScreenReader.class);
	private final Robot robot = new Robot();
	private final ITesseract tess = new Tesseract();

	// Screenshot position
	private int x1 = 0;
	private int y1 = 0;
	private int x2 = 0;
	private int y2 = 0;

	public ScreenReader() throws AWTException {
		tess.setLanguage("eng");
		tess.setOcrEngineMode(1);
		tess.setPageSegMode(1);
	}

	private BufferedImage scaleImage(BufferedImage image) {
		image = Scalr.apply(image, Scalr.OP_ANTIALIAS, Scalr.OP_GRAYSCALE, Scalr.OP_BRIGHTER);
		log.debug("Applying Anti-aliasing, Grayscale, and Brightness");
		image = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, 3840, 2160);
		log.debug("Resizing image to 4k");
		return image;
	}

	private BufferedImage captureScreen(){
		// Calculate width and height from the coordinates
		int width = Math.abs(x2 - x1);
		int height = Math.abs(y2 - y1);

		// Ensure the starting point (top-left corner) is the minimum of the two points
		int startX = Math.min(x1, x2);
		int startY = Math.min(y1, y2);

		Rectangle rectangle = new Rectangle(startX, startY, width, height);
		log.debug("Reading screen at: ({}, {}, {}, {})", x1, y1, x2, y2);
		return robot.createScreenCapture(rectangle);
	}

	public String readScreen() throws IOException, TesseractException {
		// Convert image to black and white
		BufferedImage image = captureScreen();
		image = scaleImage(image);
		// Running OCR on the image
		return tess.doOCR(image);
	}

	public boolean setPosition() throws InterruptedException {
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
				if (x1 == 0 && y1 == 0) {
					x1 = e.getXOnScreen();
					y1 = e.getYOnScreen();
					log.debug("First point selected at: ({}, {})", x1, y1);
				} else if (x2 == 0 && y2 == 0) {
					x2 = e.getXOnScreen();
					y2 = e.getYOnScreen();
					log.debug("Second point selected at: ({}, {})", x2, y2);
					frame.dispose(); // Close the frame after the second point is selected
					latch.countDown();
				}
			}
		});

		frame.setVisible(true);
		// Wait for the user to select the area
		latch.await();

		// Check if the coordinates are valid
		if (x1 <= 0 || y1 <= 0 || x2 <= 0 || y2 <= 0) {
			log.error("Invalid coordinates");
			return false;
		}

		log.debug("Position set successfully");
		return true;
	}
}