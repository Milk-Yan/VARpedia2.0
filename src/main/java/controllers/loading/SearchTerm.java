package main.java.controllers.loading;

import javafx.fxml.FXML;
import main.java.controllers.Controller;
import main.java.tasks.SearchTermTask;

/**
 * Controller for SearchTerm.fxml
 * @author wcho400
 *
 */
public class SearchTerm extends Controller {

	private SearchTermTask _task;

	/**
	 * method to return to the main menu
	 * ask for confirmation before going
	 */
	@FXML
	private void mainMenuPress() {

		// cancel current task before going back to main menu
		if (_task != null && !_task.isCancelled()) {
			_task.cancel();
		}

		mainMenu();
	}

	/**
	 * method to ensure correct tasks are cancelled when prompted
	 * @param task
	 */
	public void setTask(SearchTermTask task) {
		_task = task;
	}

}
