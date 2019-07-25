import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

class AddressBookPane extends GridPane {
	private Stack<CommandButton.Memento> stack = new Stack<CommandButton.Memento>();
	private TextField jtfName = new TextField();
	private TextField jtfStreet = new TextField();
	private TextField jtfCity = new TextField();
	private TextField jtfState = new TextField();
	private TextField jtfZip = new TextField();
	private FlowPane jpButton = new FlowPane();
	private FirstButton jbtFirst;
	private NextButton jbtNext;
	private PreviousButton jbtPrevious;
	private LastButton jbtLast;
	private AddButton jbtAdd;
	private UndoButton jbtUndo;
	private RedoButton jbtRedo;
	private ArrayList<CommandButton> cba = new ArrayList<CommandButton>();
	private static int numOfPanes = 0;
	public static final int MAX_PANES = 3;
	public static final int UPDATE_FILE_PANES = 1;
	private RandomAccessFile raf;
	private EventHandler<ActionEvent> ae = e -> ((Command) e.getSource()).Execute();
	
	//decorate the main and secondary panes with buttons
	public static AddressBookPane getInstance() {
		AddressBookPane ap = new AddressBookPane();
		numOfPanes++;
		if (numOfPanes <= UPDATE_FILE_PANES) {
			Decorator.decorator(ap.jpButton, true, ap.cba);
			return ap;
		} else if (numOfPanes <= MAX_PANES) {
			Decorator.decorator(ap.jpButton, false, ap.cba);
			return ap;
		} else
			return null;
	}
	//open the file decipher and initialize the panes (javafx panes grids etc.)
	private AddressBookPane() {
		try {
			raf = new RandomAccessFile("address.dat", "rw");
		} catch (IOException ex) {
			System.out.print("Error: " + ex);
			System.exit(0);
		}
		jtfState.setAlignment(Pos.CENTER_LEFT);
		jtfState.setPrefWidth(100);
		jtfZip.setPrefWidth(70);
		cba.add(jbtFirst = new FirstButton(false, "First", raf, getPane()));
		cba.add(jbtNext = new NextButton(false, "Next", raf, getPane()));
		cba.add(jbtPrevious = new PreviousButton(false, "Previous", raf, getPane()));
		cba.add(jbtLast = new LastButton(false, "Last", raf, getPane()));
		cba.add(jbtAdd = new AddButton(true, "Add", raf, getPane()));
		cba.add(jbtUndo = new UndoButton(true, "Undo", raf, getPane(), stack));
		cba.add(jbtRedo = new RedoButton(true, "Redo", raf, getPane(), stack));
		jbtAdd.setOnAction(ae);
		jbtFirst.setOnAction(ae);
		jbtNext.setOnAction(ae);
		jbtPrevious.setOnAction(ae);
		jbtLast.setOnAction(ae);
		jbtRedo.setOnAction(ae);
		jbtUndo.setOnAction(ae);
		Label state = new Label("State");
		Label zp = new Label("Zip");
		Label name = new Label("Name");
		Label street = new Label("Street");
		Label city = new Label("City");
		GridPane p1 = new GridPane();
		p1.add(name, 0, 0);
		p1.add(street, 0, 1);
		p1.add(city, 0, 2);
		p1.setAlignment(Pos.CENTER_LEFT);
		p1.setVgap(8);
		p1.setPadding(new Insets(0, 2, 0, 2));
		GridPane.setVgrow(name, Priority.ALWAYS);
		GridPane.setVgrow(street, Priority.ALWAYS);
		GridPane.setVgrow(city, Priority.ALWAYS);
		GridPane adP = new GridPane();
		adP.setHgap(8);
		adP.add(jtfCity, 0, 0);
		adP.add(state, 1, 0);
		adP.add(jtfState, 2, 0);
		adP.add(zp, 3, 0);
		adP.add(jtfZip, 4, 0);
		adP.setAlignment(Pos.CENTER_LEFT);
		GridPane.setHgrow(jtfCity, Priority.ALWAYS);
		GridPane.setVgrow(jtfCity, Priority.ALWAYS);
		GridPane.setVgrow(jtfState, Priority.ALWAYS);
		GridPane.setVgrow(jtfZip, Priority.ALWAYS);
		GridPane.setVgrow(state, Priority.ALWAYS);
		GridPane.setVgrow(zp, Priority.ALWAYS);
		GridPane p4 = new GridPane();
		p4.add(jtfName, 0, 0);
		p4.add(jtfStreet, 0, 1);
		p4.add(adP, 0, 2);
		p4.setVgap(1);
		GridPane.setHgrow(jtfName, Priority.ALWAYS);
		GridPane.setHgrow(jtfStreet, Priority.ALWAYS);
		GridPane.setHgrow(adP, Priority.ALWAYS);
		GridPane.setVgrow(jtfName, Priority.ALWAYS);
		GridPane.setVgrow(jtfStreet, Priority.ALWAYS);
		GridPane.setVgrow(adP, Priority.ALWAYS);
		GridPane jpAddress = new GridPane();
		jpAddress.add(p1, 0, 0);
		jpAddress.add(p4, 1, 0);
		GridPane.setHgrow(p1, Priority.NEVER);
		GridPane.setHgrow(p4, Priority.ALWAYS);
		GridPane.setVgrow(p1, Priority.ALWAYS);
		GridPane.setVgrow(p4, Priority.ALWAYS);
		jpAddress.setStyle("-fx-border-color: grey;" + " -fx-border-width: 1;" + " -fx-border-style: solid outside ;");
		jpButton.setHgap(5);
		jpButton.setAlignment(Pos.CENTER);
		GridPane.setVgrow(jpButton, Priority.NEVER);
		GridPane.setVgrow(jpAddress, Priority.ALWAYS);
		GridPane.setHgrow(jpButton, Priority.ALWAYS);
		GridPane.setHgrow(jpAddress, Priority.ALWAYS);
		this.setVgap(5);
		this.add(jpAddress, 0, 0);
		this.add(jpButton, 0, 1);
		jbtFirst.Execute();
	}
	//function to handle the actions within the panes and activate the buttons action 
	public void actionHandled(ActionEvent e) {
		((Command) e.getSource()).Execute();
	}
	
	//set the text fields with the desired value fields
	public void SetName(String text) {
		jtfName.setText(text);
	}

	public void SetStreet(String text) {
		jtfStreet.setText(text);
	}

	public void SetCity(String text) {
		jtfCity.setText(text);
	}

	public void SetState(String text) {
		jtfState.setText(text);
	}

	public void SetZip(String text) {
		jtfZip.setText(text);
	}
	//get functions for the current values displayed on the pane at the moment
	public String GetName() {
		return jtfName.getText();
	}

	public String GetStreet() {
		return jtfStreet.getText();
	}

	public String GetCity() {
		return jtfCity.getText();
	}

	public String GetState() {
		return jtfState.getText();
	}

	public String GetZip() {
		return jtfZip.getText();
	}

	public AddressBookPane getPane() {
		return this;
	}
}