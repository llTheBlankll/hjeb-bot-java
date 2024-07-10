package com.asharia;

import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;

public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			MessengerBot messengerBot = new MessengerBot();
			messengerBot.start();
		} catch (AWTException | InterruptedException | TesseractException | IOException e) {
			log.error("Error occurred", e);
		}
	}
}