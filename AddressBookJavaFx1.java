//Amit Brener 203349600
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
//includes the main function that create the address book panes
public class AddressBookJavaFx1 extends Application {
	public static void main(String[] args) {
		launch(args);
	}
	//start method create the max number of panes allowed
	@Override
	public void start(Stage primaryStage) throws Exception {
		for (int i = 1; i <= AddressBookPane.MAX_PANES; i++) {
			AddressBookPane p = AddressBookPane.getInstance();
			if (p != null) {
				Pane pane = p.getPane();
				Scene scene = new Scene(pane);
				scene.getStylesheets().add("styles.css");
				primaryStage.setTitle("AddressBook");
				primaryStage.setScene(scene);
				primaryStage.show();
				primaryStage.setAlwaysOnTop(true);
				primaryStage = new Stage();
			} else
				System.out.println("Singleton Vioaltion");
		}
	}
}


