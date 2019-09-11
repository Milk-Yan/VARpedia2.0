package main.java.controllers;

import main.java.application.WikiApplication;

public abstract class Controller {

	protected WikiApplication _mainApp;

	public void setMainApplication(WikiApplication mainApp) {
		if (_mainApp == null) {
			_mainApp = mainApp;
		} else {
			throw new IllegalArgumentException("The main application of a controller cannot be set more than once.");
		}
	}
}
