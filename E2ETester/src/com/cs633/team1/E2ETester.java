package com.cs633.team1;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * End-to-End RESTful web service tester
 * Users can enter a description, endpoint, input, XML tag to test against, and an expected result.
 * When the test is run, it will compare the actual result with the expected result and show a success/failed message along with the response time.
 * Users can then optionally graph the results, save the output or open an existing saved file.
 * @author mckeon
 *
 */
@SuppressWarnings("serial")
public class E2ETester extends JPanel implements ActionListener,
PropertyChangeListener {
	static JFrame frame;
    static JTable table;
    JTextField progressText;
    JPanel p2;
    private RunTest rt;
    static JButton graphButton;
    static JButton runButton;
    static String defaultPath;
    JButton repeatButton;
    
    /**
     * main method - program flow starts here
     * @param args
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    /**
     * Constructor
     * Initialize 10 rows and define the UI layout.
     */
	public E2ETester() {
		super(new GridLayout(1, 0));

		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 10);
		setLayout(layout);

		JPanel p1 = new JPanel();
		p1.setPreferredSize(new Dimension(1285, 195));
		p1.setBorder(BorderFactory.createLineBorder(Color.gray));

        // Define the column names
        String[] columnNames = {"",
                                "Description",
                                "Endpoint",
                                "Input",
                                "XML Tag",
                                "Expected Response",
                                "Actual Response",
                                "Results",
                                "Run Time (ms)"
                                };

        Object[][] data = {
        		{"1", "", "", "", "", "", "", "", ""},
        		{"2", "", "", "", "", "", "", "", ""},
        		{"3", "", "", "", "", "", "", "", ""},
        		{"4", "", "", "", "", "", "", "", ""},
        		{"5", "", "", "", "", "", "", "", ""},
        		{"6", "", "", "", "", "", "", "", ""},
        		{"7", "", "", "", "", "", "", "", ""},
        		{"8", "", "", "", "", "", "", "", ""},
        		{"9", "", "", "", "", "", "", "", ""},
        		{"10", "", "", "", "", "", "", "", ""},
        };

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
            		comp.setForeground(Color.black);
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
				column.setPreferredWidth(75);
				break;
			case 8: // run time
				column.setPreferredWidth(93);
				break;
			default:
				column.setPreferredWidth(140);
			}
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        //Allow the cells to be edited with a single click
        DefaultCellEditor singleClick = new DefaultCellEditor(new JTextField());
        singleClick.setClickCountToStart(1);
        
        //Set the editor as default on every column
        for (int i = 0; i < table.getColumnCount(); i++) {
        	table.setDefaultEditor(table.getColumnClass(i), singleClick);
        }

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
        progressText.setBorder(javax.swing.BorderFactory.createEmptyBorder()); //remove the border
        progressText.setFont(new Font(progressText.getFont().getFontName(), Font.ITALIC, progressText.getFont().getSize()));
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
    	//Set the GUI look and feel
    	
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
    	
        //Create and set up the window.
        frame = new JFrame("End-to-End Webservice Test Framework");
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
        JMenuItem saveAction = new JMenuItem("Save     ");
        saveAction.setMnemonic(KeyEvent.VK_S);
        JMenuItem openAction = new JMenuItem("Open     ");
        openAction.setMnemonic(KeyEvent.VK_O);
        JMenuItem exitAction = new JMenuItem("Exit     ");
        exitAction.setMnemonic(KeyEvent.VK_X);
        
        JMenuItem cutAction =   new JMenuItem(new DefaultEditorKit.CutAction());
        cutAction.setText("Cut               Ctl+X");
        cutAction.setMnemonic(KeyEvent.VK_T);
        JMenuItem copyAction =  new JMenuItem(new DefaultEditorKit.CopyAction());
        copyAction.setText("Copy            Ctl+C");
        copyAction.setMnemonic(KeyEvent.VK_C);
        JMenuItem pasteAction = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteAction.setText("Paste            Ctl+V");
        pasteAction.setMnemonic(KeyEvent.VK_P);
        JMenuItem clearResultsAction = new JMenuItem("Clear Results");
        exitAction.setMnemonic(KeyEvent.VK_R);
        JMenuItem clearAllAction = new JMenuItem("Clear All Fields");
        exitAction.setMnemonic(KeyEvent.VK_A);


        fileMenu.add(saveAction);
        fileMenu.add(openAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);
        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.addSeparator();
        editMenu.add(clearResultsAction);
        editMenu.add(clearAllAction);

        // Add listeners to the menu items
        saveAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                saveDialogue();
            }
        });

        openAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                openDialogue();
            }
        });
        
        exitAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	int dialogButton = 0;
				int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to exit?","Exit",dialogButton);
            	if(dialogResult == JOptionPane.YES_OPTION){
            		System.exit(0);
            	}
            }
        });

        clearResultsAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	int dialogButton = 0;
				int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to clear the results?","Clear Results",dialogButton);
            	if(dialogResult == JOptionPane.YES_OPTION){
            		initOutputCells(); 
            	}
            }
        });

        clearAllAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	int dialogButton = 0;
				int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure you want to clear all fields?","Clear All",dialogButton);
            	if(dialogResult == JOptionPane.YES_OPTION){
            		initAllCells(); 
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

    /**
     * Add a new row to the table
     */
    public static void addRow() {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.insertRow(table.getRowCount(),new Object[]  {table.getRowCount() + 1,"","","","","","","",""});
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

    /**
     * Run the test
     */
    public void runTest() {
        int numRows = table.getRowCount();
        javax.swing.table.TableModel model = table.getModel();
        boolean rowsFound = false;

        // Loop through the table values and execute the web service
        try {
	        for (int i=0; i < numRows; i++) {
	        	if (!model.getValueAt(i, 2).equals("")) {
            		rowsFound = true;
            		changeProgressText("Progress: Executing " + model.getValueAt(i, 2) + model.getValueAt(i, 3));
            		long startTime = System.nanoTime();  // get the time before the call
            		ConsumeWebService service = new ConsumeWebService(model.getValueAt(i, 2).toString(), model.getValueAt(i, 3).toString(), model.getValueAt(i, 4).toString());
            		service.callRestService();
            		long endTime = System.nanoTime();  // get the time after the call
            		long duration = (endTime - startTime)/1000000;  // get the duration in milliseconds
            		String response = service.getWebServiceResponse();
            		model.setValueAt(response, i, 6);
            		if(response.equals(model.getValueAt(i, 5))) {
            			model.setValueAt("Success", i, 7);
            		} else {
            			model.setValueAt("Failed", i, 7);
            		}      
            		model.setValueAt(duration, i, 8);
            	}
	        }
        } catch (Exception e) 
        {
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

    /**
     * Graph the results
     */
    public void graphResults() {
        int numRows = table.getRowCount();
        javax.swing.table.TableModel model = table.getModel();
        boolean rowsFound = false;

        final LineGraph graph = new LineGraph("End-to-End Webservice Test Framework", "Web Service Response Time");

        for (int i=0; i < numRows; i++) {
        	if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {
        		rowsFound = true;
        		graph.addToDataset(Integer.parseInt(model.getValueAt(i, 0).toString()), Integer.parseInt(model.getValueAt(i, 8).toString()));
        	}
        }
        if (rowsFound) {
	        graph.pack();
	        RefineryUtilities.centerFrameOnScreen(graph);
	        graph.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        graph.setVisible(true);
        } else {
        	JOptionPane.showMessageDialog(null, "Unable to graph - no valid results found");
        }
    }

    /**
     *  Clear any text that may be in the output cells from a previous run
     */
    public static void initOutputCells() {
        int numRows = table.getRowCount();
        javax.swing.table.TableModel model = table.getModel();

        for (int i=0; i < numRows; i++) {
        	model.setValueAt("", i, 6);
        	model.setValueAt("", i, 7);
        	model.setValueAt("", i, 8);
        }    	

        graphButton.setEnabled(false);
    }

    /**
     *  Clear all text that may be in the cells 
     */
    public static void initAllCells() {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        javax.swing.table.TableModel model = table.getModel();

        for (int i=0; i < numRows; i++) {
        	for (int j=1; j < numCols; j++) {  //Don't clear the first column
        		model.setValueAt("", i, j);
        	}
        }    	
        
        graphButton.setEnabled(false);
    }

    /**
     * Alter the text that is show in the progress bar.
     * @param textValue
     */
	public void changeProgressText(String textValue) {
		progressText.setText(textValue);
		progressText.repaint();
		progressText.revalidate();
	}

	/**
     * Invoked when the property changes
     */
	public void propertyChange(PropertyChangeEvent evt) {

	}

	/**
     * Invoked when an action is performed
     */
	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	/**
	 *  Run the test through SwingWorker.
	 *  The SwingWorker framework allows for multi-threading so progress messages can be updated while the program runs through the test script.
	 * @author mckeon
	 *
	 */
	
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

    /**
     *  Save the input to a file
     */
    public static void saveDialogue() {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        String fileRecord;
        javax.swing.table.TableModel model = table.getModel();

        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        String defaultPath = getDefaultPath();
        if (!defaultPath.equals("")) {
        	fc.setCurrentDirectory(new File(defaultPath));
        }
        fc.setFileFilter(new FileNameExtensionFilter("End-To-End Tester File (*.e2e)", "e2e"));
        int retVal = fc.showSaveDialog(frame);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File selectedfile = fc.getSelectedFile();
            String fileName = selectedfile.getAbsolutePath();
            if (!fileName.endsWith(".e2e")) {
            	fileName = fileName + ".e2e";
            }
            setDefaultPath(fileName.substring(0,fileName.lastIndexOf(File.separator)));  //Save the path so the user doesn't have to navigate to it next time
            try {
	            FileWriter fstream = new FileWriter(fileName);
	            BufferedWriter out = new BufferedWriter(fstream);
	
		        for (int i=0; i < numRows; i++) {
		        	fileRecord = "";
		        	for (int j=0; j < numCols; j++) {
		        		fileRecord = fileRecord + model.getValueAt(i, j);
		        		if ((numCols - j) > 1) {
		        			fileRecord = fileRecord + ",";
		        		}
		        	}
		        	out.write(fileRecord);
		        	out.newLine();
		        }
		        
		        out.close();
	        	JOptionPane.showMessageDialog(null, "File Saved");		
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        
    }
    
	/**
	 *  Open the input file
	 */
    public static void openDialogue() {
    	BufferedReader br = null;
    	String line = "";
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        boolean rowsFound = false;
        javax.swing.table.TableModel model = table.getModel();

    	
        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        String defaultPath = getDefaultPath();
        if (!defaultPath.equals("")) {
        	fc.setCurrentDirectory(new File(defaultPath));
        }
        fc.setFileFilter(new FileNameExtensionFilter("End-To-End Tester File (*.e2e)", "e2e"));
        int retVal = fc.showOpenDialog(frame);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File selectedfile = fc.getSelectedFile();
            String fileName = selectedfile.getAbsolutePath();
            setDefaultPath(fileName.substring(0,fileName.lastIndexOf(File.separator)));  //Save the path so the user doesn't have to navigate to it next time
        	try {
        		br = new BufferedReader(new FileReader(fileName));
        		int rowNum = 0;
        		while ((line = br.readLine()) != null) {
        			// If the saved file has more rows than what is currently on the screen, add a row
        			if (rowNum >= numRows) {
        				addRow();
        			}
        			String[] tableColumns = line.split(",");  // use comma as separator
        			for (int i=0; i < tableColumns.length; i++) {
        				model.setValueAt(tableColumns[i], rowNum, i);
        				
        			}
        			if (!rowsFound) {  // see if we should enable the graph button
        				if (!model.getValueAt(rowNum, 6).equals("") && !model.getValueAt(rowNum, 7).equals("") && !model.getValueAt(rowNum, 8).equals("")) {
        					rowsFound = true;
        				}
        			}
        			rowNum++;
        		}
        		if (rowsFound) {
        			graphButton.setEnabled(true);
        		}
        	} catch (FileNotFoundException e) {
        		e.printStackTrace();
        	} catch (IOException e) {
        		e.printStackTrace();
        	} finally {
        		if (br != null) {
        			try {
        				br.close();
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        		}
        	}
        }
    	
    }
    
    /**
     * Get the default path for the open/save dialogue.
     * This will anchor the user in the last folder they navigated to.
     * @return defaultPath
     */
    private static String getDefaultPath() {
    	BufferedReader br = null;
    	String line = "";
    	String iniFileName = "E2ETester.ini";
    	
		File iniFile = new File(iniFileName);
		if (!iniFile.exists()) {
			defaultPath = "";
			return defaultPath;
		}

		try {
			br = new BufferedReader(new FileReader(iniFileName));
			line = br.readLine(); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
		
		defaultPath = line;
		return defaultPath;
	}
    
    /**
     * Saves the last path that the user navigated to in the open/save dialogue.
     * @param path
     */
    private static void setDefaultPath(String path) {
    	String fileName = "E2ETester.ini";
    	if (defaultPath.equals(path)) {
    		return;
    	}
    	
    	defaultPath = path;
    	
        FileWriter fstream = null;
		try {
			fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(path);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

 }


