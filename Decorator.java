
import java.util.ArrayList;
import javafx.scene.layout.FlowPane;
//decorator for the special looking pane (the main pane has the redo/undo buttons and the other 2 dont)
public class Decorator {
	public static void decorator(FlowPane jpButton, boolean update, ArrayList<CommandButton> cba) {
		if (update)
			for (int i = 0; i < cba.size(); i++)
				jpButton.getChildren().add(cba.get(i));
		else
			for (int i = 0; i < cba.size(); i++)
				if (!cba.get(i).visable)
					jpButton.getChildren().add(cba.get(i));

	}
}
