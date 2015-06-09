package com.cs633.team1;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultEditorKit;

import org.jfree.ui.RefineryUtilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("serial")
public class E2ETester extends JPanel implements ActionListener,
		PropertyChangeListener {
	JTable table;
	JTextField progressText;
	JPanel p2;
	private RunTest rt;
	JButton graphButton;
	JButton runButton;
	JButton repeatButton;

	public E2ETester() {
		super(new GridLayout(1, 0));

		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 10);
		setLayout(layout);

		JPanel p1 = new JPanel();
		p1.setPreferredSize(new Dimension(1285, 195));
		p1.setBorder(BorderFactory.createLineBorder(Color.gray));

		// Define the column names
		String[] columnNames = { "", "Description", "Endpoint", "Input",
				"XML Tag", "Expected Response", "Actual Response", "Results",
				"Run Time (ms)" };

		Object[][] data = { { "1", "", "", "", "", "", "", "", "" },
				{ "2", "", "", "", "", "", "", "", "" },
				{ "3", "", "", "", "", "", "", "", "" },
				{ "4", "", "", "", "", "", "", "", "" },
				{ "5", "", "", "", "", "", "", "", "" },
				{ "6", "", "", "", "", "", "", "", "" },
				{ "7", "", "", "", "", "", "", "", "" },
				{ "8", "", "", "", "", "", "", "", "" },
				{ "9", "", "", "", "", "", "", "", "" },
				{ "10", "", "", "", "", "", "", "", "" }, };

		// int numRows = 10;
		// DefaultTableModel model = new DefaultTableModel(numRows,
		// columnNames.length) {
		DefaultTableModel model = new DefaultTableModel(data, columnNames) {
			// Make the first column and the last 3 columns uneditable
			@Override
			public boolean isCellEditable(int row, int column) {
				switch (column) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					return true;
				case 0:
				case 6:
				case 7:
				case 8:
					return false;
				default:
					return false;
				}
			}

		};

		model.setColumnIdentifiers(columnNames);
		table = new JTable(model) {
			public Component prepareRenderer(TableCellRenderer renderer,
					int index_row, int index_col) {
				Component comp = super.prepareRenderer(renderer, index_row,
						index_col);
				if (index_col == 0 || index_col > 5) {
					comp.setBackground(new Color(250, 250, 250));
				} else {
					comp.setBackground(Color.white);
				}
				return comp;
			}
		};

		table.setPreferredScrollableViewportSize(new Dimension(1275, 160));
		table.setFillsViewportHeight(true);
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Sans Serif", Font.BOLD, 12));

		// Set the column widths
		TableColumn column = null;
		for (int i = 0; i < table.getColumnCount(); i++) {
			column = table.getColumnModel().getColumn(i);
			switch (i) {
			case 0: // row number
				column.setPreferredWidth(30);
				break;
			case 1: // Description
				column.setPreferredWidth(150);
				break;
			case 2: // endpoint
				column.setPreferredWidth(350);
				break;
			case 7: // results
				column.setPreferredWidth(80);
				break;
			case 8: // run time
				column.setPreferredWidth(90);
				break;
			default:
				column.setPreferredWidth(140);
			}
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		p1.add(scrollPane);

		add(p1);

		p2 = new JPanel();
		p2.setPreferredSize(new Dimension(1275, 30));
		p2.setBorder(BorderFactory.createLineBorder(Color.black));

		// Layout dedicated for progress message
		FlowLayout layout_p = new FlowLayout(FlowLayout.LEFT, 5, 5);
		p2.setLayout(layout_p);

		progressText = new JTextField(114);
		progressText.setEditable(false);
		progressText.setText("Progress:");
		p2.add(progressText);

		add(p2);

		JPanel p3 = new JPanel();
		p3.setPreferredSize(new Dimension(250, 140));
		p3.setBorder(BorderFactory.createLineBorder(Color.black));

		// Layout dedicated for buttons
		FlowLayout layout_b = new FlowLayout(FlowLayout.CENTER, 12, 12);
		p3.setLayout(layout_b);

		JButton addButton = new JButton("Add Row");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				addRow();
			}
		});
		p3.add(addButton);

		runButton = new JButton("Run Test");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent b) {
				runButton.setEnabled(false);
				graphButton.setEnabled(false);
				changeProgressText("Progress: Running test script...");
				rt = new RunTest();
				rt.addPropertyChangeListener(this);
				rt.execute();
			}
		});
		p3.add(runButton);

		repeatButton = new JButton("Continious Test");
		repeatButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent b) {
				if (repeatButton.isEnabled()) {
					repeatButton.setEnabled(false);
					runButton.setEnabled(false);
					graphButton.setEnabled(false);
					changeProgressText("Progress: Running test script (repeat)...");
					rt = new RunTest();
					rt.addPropertyChangeListener(this);
					rt.execute();

				} else {
					repeatButton.setEnabled(true);
					runButton.setEnabled(true);
					graphButton.setEnabled(true);
				}

			}
		});
		p3.add(repeatButton);

		graphButton = new JButton("Graph Results");
		graphButton.setEnabled(false);
		graphButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent c) {
				graphResults();
			}
		});
		p3.add(graphButton);

		add(p3);

	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("End-to-End Webservice Test Framework");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// Creates a menubar for a JFrame
		JMenuBar menuBar = new JMenuBar();

		// Add the menubar to the frame
		frame.setJMenuBar(menuBar);
		// Define and add two drop down menu to the menubar
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);

		// Create and add simple menu item to one of the drop down menu
		JMenuItem saveAction = new JMenuItem("Save                Ctl+S");
		saveAction.setMnemonic(KeyEvent.VK_S);
		JMenuItem openAction = new JMenuItem("Open               Ctl+O");
		openAction.setMnemonic(KeyEvent.VK_O);
		JMenuItem exitAction = new JMenuItem("Exit");
		exitAction.setMnemonic(KeyEvent.VK_X);

		JMenuItem cutAction = new JMenuItem(new DefaultEditorKit.CutAction());
		cutAction.setText("Cut               Ctl+X");
		cutAction.setMnemonic(KeyEvent.VK_T);
		JMenuItem copyAction = new JMenuItem(new DefaultEditorKit.CopyAction());
		copyAction.setText("Copy            Ctl+C");
		copyAction.setMnemonic(KeyEvent.VK_C);
		JMenuItem pasteAction = new JMenuItem(
				new DefaultEditorKit.PasteAction());
		pasteAction.setText("Paste          Ctl+V");
		pasteAction.setMnemonic(KeyEvent.VK_P);

		fileMenu.add(saveAction);
		fileMenu.add(openAction);
		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		editMenu.add(cutAction);
		editMenu.add(copyAction);
		editMenu.add(pasteAction);

		// Add listeners to the menu items
		saveAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("You have clicked on the save action");
			}
		});

		openAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("You have clicked on the open action");
			}
		});

		exitAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int dialogButton = 0;
				int dialogResult = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to exit?", "Exit", dialogButton);
				if (dialogResult == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		// Create and set up the content pane.
		E2ETester newContentPane = new E2ETester();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		// frame.pack();
		frame.setSize(1366, 460);
		frame.setVisible(true);
	}

	public void addRow() {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.insertRow(table.getRowCount(), new Object[] {
				table.getRowCount() + 1, "", "", "", "", "", "", "", "" });
	}

	public void repeatTest() {
		int numRows = table.getRowCount();
		javax.swing.table.TableModel model = table.getModel();
		boolean rowsFound = false;

		// Loop through the table values and execute the web service
		try {
			while (!repeatButton.isEnabled()) {
				for (int i = 0; i < numRows; i++) {
					if (!model.getValueAt(i, 2).equals("")) {
						rowsFound = true;
						changeProgressText("Progress: Executing "
								+ model.getValueAt(i, 2)
								+ model.getValueAt(i, 3));
						long startTime = System.nanoTime(); // get the time
															// before the call
						ConsumeWebService service = new ConsumeWebService(model
								.getValueAt(i, 2).toString(), model.getValueAt(
								i, 3).toString(), model.getValueAt(i, 4)
								.toString());
						service.callRestService();
						long endTime = System.nanoTime(); // get the time after
															// the call
						long duration = (endTime - startTime) / 1000000; // get
																			// the
																			// duration
																			// in
																			// milliseconds
						String response = service.getWebServiceResponse();
						model.setValueAt(response, i, 6);
						if (response.equals(model.getValueAt(i, 5))) {
							model.setValueAt("Success", i, 7);
						} else {
							model.setValueAt("Failed", i, 7);
						}
						model.setValueAt(duration, i, 8);
						// Thread.sleep(500);
					}
				}

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (rowsFound) {
			changeProgressText("Progress: Complete");
		} else {
			changeProgressText("Progress: No endpoints specified");
		}
	}

	public void runTest() {
		int numRows = table.getRowCount();
		javax.swing.table.TableModel model = table.getModel();
		boolean rowsFound = false;

		// Loop through the table values and execute the web service
		try {
			do {
				for (int i = 0; i < numRows; i++) {
					if (!model.getValueAt(i, 2).equals("")) {
						rowsFound = true;
						changeProgressText("Progress: Executing "
								+ model.getValueAt(i, 2)
								+ model.getValueAt(i, 3));
						long startTime = System.nanoTime(); // get the time
															// before
															// the call
						ConsumeWebService service = new ConsumeWebService(model
								.getValueAt(i, 2).toString(), model.getValueAt(
								i, 3).toString(), model.getValueAt(i, 4)
								.toString());
						service.callRestService();
						long endTime = System.nanoTime(); // get the time after
															// the
															// call
						long duration = (endTime - startTime) / 1000000; // get
																			// the
																			// duration
																			// in
																			// milliseconds
						String response = service.getWebServiceResponse();
						model.setValueAt(response, i, 6);
						if (response.equals(model.getValueAt(i, 5))) {
							model.setValueAt("Success", i, 7);
						} else {
							model.setValueAt("Failed", i, 7);
						}
						model.setValueAt(duration, i, 8);

					}
				}
				if (!repeatButton.isEnabled()) {
					Thread.sleep(6000);
				}
			} while (!repeatButton.isEnabled());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (rowsFound) {
			changeProgressText("Progress: Complete");
		} else {
			changeProgressText("Progress: No endpoints specified");
		}
		runButton.setEnabled(true);
		graphButton.setEnabled(true);
	}

	public void graphResults() {
		int numRows = table.getRowCount();
		javax.swing.table.TableModel model = table.getModel();
		boolean rowsFound = false;

		final LineGraph graph = new LineGraph(
				"End-to-End Webservice Test Framework",
				"Web Service Response Time");

		for (int i = 0; i < numRows; i++) {
			if (model.getValueAt(i, 8) != null
					&& !model.getValueAt(i, 8).equals("")) {
				rowsFound = true;
				graph.addToDataset(
						Integer.parseInt(model.getValueAt(i, 0).toString()),
						Integer.parseInt(model.getValueAt(i, 8).toString()));
			}
		}
		if (rowsFound) {
			graph.pack();
			RefineryUtilities.centerFrameOnScreen(graph);
			graph.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			graph.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(null,
					"Unable to graph - no valid results found");
		}
	}

	// Clear any text that may be in the output cells from a previous run
	public void initOutputCells() {
		int numRows = table.getRowCount();
		javax.swing.table.TableModel model = table.getModel();

		for (int i = 0; i < numRows; i++) {
			model.setValueAt(null, i, 6);
			model.setValueAt(null, i, 7);
			model.setValueAt(null, i, 8);
		}
	}

	public void changeProgressText(String textValue) {
		progressText.setText(textValue);
		progressText.repaint();
		progressText.revalidate();
	}

	public void propertyChange(PropertyChangeEvent evt) {

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	// The SwingWorker framework allows for multi-threading so progress messages
	// an be updated while the program runs through the test script.
	class RunTest extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {
			initOutputCells();
			runTest();
			return null;
		}

		public void addPropertyChangeListener(ActionListener actionListener) {
			// TODO Auto-generated method stub

		}
	}

	// The SwingWorker framework allows for multi-threading so progress messages
	// an be updated while the program runs through the test script.
	class RepeatTest extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {
			initOutputCells();
			repeatTest();
			return null;
		}

		public void addPropertyChangeListener(ActionListener actionListener) {
			// TODO Auto-generated method stub

		}
	}

	// main method - program flow starts here
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();

			}
		});
	}

}
