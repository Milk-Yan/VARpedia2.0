package application;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 * Creates the Pane of buttons for a MediaPlayer.
 * Functionality includes play/pause, fast forward,
 * slow down, forward, backward, mute.
 * @author Milk
 *
 */
public class ButtonPane extends HBox{

	public ButtonPane(MediaPlayer player) {
		
		// create Buttons
		Button btnPlayPause = new Button("Pause");
		Button btnFastForward = new Button("Fast Forward");
		Button btnSlowDown = new Button("Slow Down");
		Button btnForward = new Button("Forward");
		Button btnBackward = new Button("Backward");
		Button btnMute = new Button("Mute");
		btnPlayPause.setPrefWidth(65);

		// Button functionality
		btnPlayPause.setOnAction(playEvent -> {
			if (player.getStatus() == Status.PAUSED) {
				player.play();
				btnPlayPause.setText("Pause");
			} else if (player.getStatus() == Status.PLAYING) {
				player.pause();
				btnPlayPause.setText("Play");
			}
		});

		btnFastForward.setOnAction(fastForwardEvent -> {
			Double fasterRate = player.getRate() + 0.5;
			player.setRate(fasterRate);
		});

		btnSlowDown.setOnAction(slowDownEvent -> {
			Double slowerRate = player.getRate() - 0.5;
			if (slowerRate != 0) {
				player.setRate(slowerRate);
			}

		});

		btnForward.setOnAction(forwardEvent -> {
			Duration newTime = player.getCurrentTime().add(Duration.seconds(5));
			player.seek(newTime);
		});

		btnBackward.setOnAction(backwardEvent -> {
			Duration newTime = player.getCurrentTime().subtract(Duration.seconds(5));
			player.seek(newTime);
		});

		btnMute.setOnAction(muteEvent -> {
			if (player.isMute()) {
				player.setMute(false);
			} else {
				player.setMute(true);
			}
		});

		getChildren().addAll(btnPlayPause,btnFastForward,btnSlowDown, btnForward,btnBackward,btnMute);
		setAlignment(Pos.CENTER);
	}
}
