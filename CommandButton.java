import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;
import javafx.scene.control.Button;

//command button class on all of its variations 
interface Command {
	public void Execute();
}

class CommandButton extends Button implements Command {
	public final static int NAME_SIZE = 32;
	public final static int STREET_SIZE = 32;
	public final static int CITY_SIZE = 20;
	public final static int STATE_SIZE = 10;
	public final static int ZIP_SIZE = 5;
	public final static int RECORD_SIZE = (NAME_SIZE + STREET_SIZE + CITY_SIZE + STATE_SIZE + ZIP_SIZE);
	protected AddressBookPane abp;
	protected RandomAccessFile raf;
	protected boolean visable;

	public CommandButton(boolean visable, String text, RandomAccessFile raf, AddressBookPane p) {
		super(text);
		this.raf = raf;
		this.abp = p;
		this.visable = visable;
	}

	public void setPane(AddressBookPane pane) {
		abp = pane;
	}

	public void setRandomAccessFile(RandomAccessFile r) {
		raf = r;
	}

	public void Execute() {
	}
	//write an address using the helping method from the FixedLengthStringIO1 class
	public void writeAddress(long position, Address a) {
		try {
			raf.seek(position);
			FixedLengthStringIO1.writeFixedLengthString(a.getName(), NAME_SIZE, raf);
			FixedLengthStringIO1.writeFixedLengthString(a.getStreet(), STREET_SIZE, raf);
			FixedLengthStringIO1.writeFixedLengthString(a.getCity(), CITY_SIZE, raf);
			FixedLengthStringIO1.writeFixedLengthString(a.getState(), STATE_SIZE, raf);
			FixedLengthStringIO1.writeFixedLengthString(a.getZip(), ZIP_SIZE, raf);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	// write an address from the memento for the undo button
	public void writeAddressFromMemento(long position, Memento m) {
		writeAddress(position, m.getAddress());
	}

	// read an address from the specified location
	public Address readAddress(long position) throws IOException {
		raf.seek(position);
		String name = FixedLengthStringIO1.readFixedLengthString(NAME_SIZE, raf);
		String street = FixedLengthStringIO1.readFixedLengthString(STREET_SIZE, raf);
		String city = FixedLengthStringIO1.readFixedLengthString(CITY_SIZE, raf);
		String state = FixedLengthStringIO1.readFixedLengthString(STATE_SIZE, raf);
		String zip = FixedLengthStringIO1.readFixedLengthString(ZIP_SIZE, raf);
		return new Address(name, street, city, state, zip);
	}

	// set the relevant address to the view
	public void setAddress(Address add) {
		abp.SetName(add.getName());
		abp.SetStreet(add.getStreet());
		abp.SetCity(add.getCity());
		abp.SetState(add.getState());
		abp.SetZip(add.getZip());
	}

	// the memento object that contain the changes
	public static class Memento {
		private Address a;

		protected Memento(Address a) {
			this.a = a;
		}

		private Address getAddress() {
			return a;
		}
	}
}

//Button that adds an address directly into the file
class AddButton extends CommandButton {
	public AddButton(boolean update, String text, RandomAccessFile raf, AddressBookPane p) {
		super(update, text, raf, p);
	}

	@Override
	public void Execute() {
		try {
			writeAddress(raf.length(),
					new Address(abp.GetName(), abp.GetStreet(), abp.GetCity(), abp.GetState(), abp.GetZip()));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

//displays the next address from the file
class NextButton extends CommandButton {
	public NextButton(boolean update, String text, RandomAccessFile raf, AddressBookPane p) {
		super(update, text, raf, p);
	}

	@Override
	public void Execute() {
		try {
			if (raf.length() == 0) {
				setAddress(new Address("", "", "", "", ""));
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			long currentPosition = raf.getFilePointer();
			if (currentPosition < raf.length())
				setAddress(readAddress(currentPosition));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

//displays the previous address from the file
class PreviousButton extends CommandButton {
	public PreviousButton(boolean update, String text, RandomAccessFile raf, AddressBookPane p) {
		super(update, text, raf, p);
	}

	@Override
	public void Execute() {
		try {
			if (raf.length() == 0) {
				setAddress(new Address("", "", "", "", ""));
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			long currentPosition = raf.getFilePointer();
			if (currentPosition > raf.length())
				currentPosition = raf.length();
			if (currentPosition - 2 * 2 * RECORD_SIZE >= 0)
				setAddress(readAddress(currentPosition - 2 * 2 * RECORD_SIZE));
			else
				;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
//display the last address from the file
class LastButton extends CommandButton {
	public LastButton(boolean update, String text, RandomAccessFile raf, AddressBookPane p) {
		super(update, text, raf, p);
	}

	@Override
	public void Execute() {
		try {
			if (raf.length() == 0) {
				setAddress(new Address("", "", "", "", ""));
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			long lastPosition = raf.length();
			if (lastPosition > 0)
				setAddress(readAddress(lastPosition - 2 * RECORD_SIZE));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
//display the first address from file
class FirstButton extends CommandButton {
	public FirstButton(boolean update, String text, RandomAccessFile raf, AddressBookPane p) {
		super(update, text, raf, p);
	}

	@Override
	public void Execute() {
		try {
			if (raf.length() == 0) {
				setAddress(new Address("", "", "", "", ""));
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if (raf.length() > 0)
				setAddress(readAddress(0));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
//undo button revert written addresses from the file 
class UndoButton extends CommandButton {
	private Stack<Memento> stack;

	public UndoButton(boolean update, String text, RandomAccessFile raf, AddressBookPane p, Stack<Memento> stack) {
		super(update, text, raf, p);
		this.stack = stack;
	}

	@Override
	public void Execute() {
		try {
			long lastPosition = raf.length();
			if (lastPosition > 0) {
				Address a = readAddress(lastPosition - 2 * RECORD_SIZE);
				stack.push(new Memento(a));
				raf.setLength(lastPosition - 2 * RECORD_SIZE);
				if (raf.length() > 0)
					setAddress(readAddress(0));
				else
					setAddress(new Address("", "", "", "", ""));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
//redo button that rewrite the address that was removed
class RedoButton extends CommandButton {
	private Stack<Memento> stack;

	public RedoButton(boolean update, String text, RandomAccessFile raf, AddressBookPane p, Stack<Memento> stack) {
		super(update, text, raf, p);
		this.stack = stack;
	}

	@Override
	public void Execute() {
		try {
			if (!stack.isEmpty()) {
				writeAddressFromMemento(raf.length(), stack.pop());
				long lastPosition = raf.length();
				if (lastPosition > 0)
					setAddress(readAddress(lastPosition - 2 * RECORD_SIZE));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}