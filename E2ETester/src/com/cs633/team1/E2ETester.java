package com.cs633.team1;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import javax.swing.table.TableModel;
import javax.swing.text.DefaultEditorKit;

import org.jfree.ui.RefineryUtilities;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private static JFrame frame;
    private static JTable table;
    private JTextField progressText;
    private JPanel p2;
    private RunTest rt;
    private static JButton graphButton;
    private static JButton runButton;
    private static JButton addButton;
    private static JButton deleteButton;
    private static JCheckBox repeatBox;
    @SuppressWarnings("rawtypes")
    private static JComboBox graphList;
    private static Preferences preferences;
    private static JDialog preferFrame;
    private static int numOfOriginalRows;

    
    /**
     * main method - program flow starts here
     * @param args
     */
    public static void main(String[] args) {
    	preferences = new Preferences();  //Load the preferences
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
            		comp.setForeground(Color.black);
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
		p3.setPreferredSize(new Dimension(240, 35));
		p3.setBackground(new Color(250, 250, 250));

		JPanel p4 = new JPanel();
		p4.setPreferredSize(new Dimension(250, 80));
		p4.setBorder(BorderFactory.createLineBorder(Color.black));
		p4.setBackground(new Color(250, 250, 250));

		// Layout dedicated for buttons
		FlowLayout layout_b = new FlowLayout(FlowLayout.CENTER, 12, 12);
		p3.setLayout(layout_b);

		addButton = new JButton("Add Row");
		addButton.setToolTipText("Add a row to the end of the table");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				addRow();
			}
		});
		p3.add(addButton);
		
		deleteButton = new JButton("Delete Row");
		deleteButton.setToolTipText("Delete the last row in the table");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent d) {
				deleteRow();
			}
		});
		p3.setLayout(new FlowLayout(FlowLayout.CENTER));
		p3.add(deleteButton);
		
		p4.add(p3);

		runButton = new JButton("Run Test");
		runButton.setToolTipText("Execute the test block");
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent b) {
				runButton.setEnabled(false);
				graphButton.setEnabled(false);
				graphList.setEnabled(false);
				addButton.setEnabled(false);
				deleteButton.setEnabled(false);
				changeProgressText("Progress: Running test script...");
				rt = new RunTest();
				rt.addPropertyChangeListener(this);
				rt.execute();
			}
		});
		p4.add(runButton);

		repeatBox = new JCheckBox("Continuous Test");
		repeatBox.setSelected(false);
		repeatBox.setToolTipText("<html>When checked, the test block will repeat indefinitely.  <br>See 'Edit-->Preferences' for settings.  <br>Uncheck the box to stop the test.</html>");
		repeatBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent c) {
				if (!repeatBox.isSelected()) {
					if (rt != null && !rt.isDone()) {
						rt.cancel(true);  // Allow the Sleep to terminate if the check box is unchecked
					}
				}
			}
		}
				);

		p4.add(repeatBox);

		add(p4);
		
		// Layout dedicated for graphs
		JPanel p5 = new JPanel();
		p5.setPreferredSize(new Dimension(250, 80));
		p5.setBorder(BorderFactory.createLineBorder(Color.black));
		p5.setBackground(new Color(250, 250, 250));

		FlowLayout layout_g = new FlowLayout(FlowLayout.CENTER, 12, 12);
		p5.setLayout(layout_g);

		// Drop-down list of available graphs
		String[] graphOptions = { "Line Graph - Response Time (All)", "Line Graph - Response Time (By Result)", "Bar Graph - Success/Failure Counts" };
		graphList = new JComboBox(graphOptions);
		graphList.setSelectedIndex(0);
		graphList.addActionListener(this);
		p5.add(graphList);
		
		graphButton = new JButton("Graph Results");
		graphButton.setToolTipText("Graph the results based on the option selected in the above drop-down");
		graphButton.setEnabled(false);
		graphList.setEnabled(false);
		graphButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent g) {
				switch (graphList.getSelectedIndex()) {
				case 0:
					graphResponseTime();
					break;
				case 1:
					graphResponseByResult();
					break;
				case 2:
					graphSuccessFailure();
					break;
				}
			}
		});
		p5.add(graphButton);

		add(p5);

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
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
         
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
        JMenuItem preferencesAction = new JMenuItem("Preferences");
        exitAction.setMnemonic(KeyEvent.VK_F);

        JMenuItem helpAction = new JMenuItem("User Guide     ");
        helpAction.setMnemonic(KeyEvent.VK_U);

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
        editMenu.addSeparator();
        editMenu.add(preferencesAction);
        helpMenu.add(helpAction);

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
        
        preferencesAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                loadPreferencesUI();
                preferences.reloadValues();  //Reload the values from the file in case they changed
            }
        });

        helpAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	//Open the online user guide
            	try {
            	    Desktop.getDesktop().browse(new URL("http://E2ETester.hushmoss.com/E2ETester_UserGuide.pdf").toURI());
            	} catch (Exception e) {}            }
        });

        
		// Create and set up the content pane.
		E2ETester newContentPane = new E2ETester();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		// frame.pack();
		frame.setSize(1366, 410);
		frame.setVisible(true);
	}

    /**
     * Add a new row to the table
     */
    public static void addRow() {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        model.insertRow(table.getRowCount(),new Object[]  {table.getRowCount() + 1,"","","","","","","",""});
    }

    /**
     * Delete the last row from the table
     */
    public static void deleteRow() {
        DefaultTableModel model = (DefaultTableModel)table.getModel();
        if (model.getRowCount() > 1) {
        	model.removeRow((model.getRowCount() - 1));
        }
    }

    /**
     * Run the test
     */
    public void runTest() {
        int numRows = table.getRowCount();
        TableModel model = table.getModel();
        boolean rowsFound = false;
        String response = "";
        long startTime = 0;
        long endTime = 0;
        long duration = 0;
        int i = 0;
        int numRowsOccupied = 0;

        //Figure out how many rows are occupied.  We will need that number for the continuous test.
        for (int j = (numRows - 1); j >= 0; j--) {
        	if (!model.getValueAt(j, 2).equals("")) {
        		numOfOriginalRows = j + 1;
        		break;
        	}
        }
        
        //Before we run the test, let's loop through the table and make sure all required fields are present
        for (int n = 0; n < numRows; n++) {
        	if (!model.getValueAt(n, 2).equals("")) {
        		if (model.getValueAt(n, 4).equals("")) {
                	JOptionPane.showMessageDialog(null, "XML Tag values missing - Please correct and try again");
                    runButton.setEnabled(true);
                    addButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                	return;
        		}
        		if (model.getValueAt(n, 5).equals("")) {
                	JOptionPane.showMessageDialog(null, "Expected Response values missing - Please correct and try again");
                    runButton.setEnabled(true);
                    addButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                	return;
        		}
        	}        	
        }
        
        // Loop through the table values and execute the web service
        try {
        	do {
		        for (; i < numRows; i++) {
		        	if (!model.getValueAt(i, 2).equals("")) {
		        		numRowsOccupied++;
	            		rowsFound = true;
	            		changeProgressText("Progress: Executing " + model.getValueAt(i, 2) + model.getValueAt(i, 3));
	            		startTime = System.nanoTime();  // get the time before the call
	            		try {
	            			ConsumeWebService service = new ConsumeWebService(model.getValueAt(i, 2).toString(), model.getValueAt(i, 3).toString(), model.getValueAt(i, 4).toString());
	            			service.callRestService();
		            		endTime = System.nanoTime();  // get the time after the call
		            		duration = (endTime - startTime)/1000000;  // get the duration in milliseconds
		            		response = service.getWebServiceResponse();
	            		} catch (Exception e) {
	            			response = "Error";
	            		}
	            		model.setValueAt(response, i, 6);
	            		if(response.equalsIgnoreCase(model.getValueAt(i, 5).toString()) && !response.equals("")) {
	            			model.setValueAt("<html><font color='green'>Success</font></html>", i, 7);
	            		} else {
	            			model.setValueAt("<html><font color='red'>Failed</font></html>", i, 7);
	            		}      
	            		model.setValueAt(duration, i, 8);
	            	}
		        }
		        if (repeatBox.isSelected() && rowsFound) {
		        	graphButton.setEnabled(true);
					graphList.setEnabled(true);
		        	changeProgressText("Progress: Waiting " + preferences.getLoopWaitCount() + " milliseconds before next test");
		        	try {
		        		Thread.sleep(preferences.getLoopWaitCount());
		        		if (preferences.isRewriteResults()) {
		        			initOutputCells();
		        		}
		        	} catch (InterruptedException e) {
		        		break;
		        	}
		        	//Repeat the test block for the next run if that setting is present
		        	if (!preferences.isRewriteResults()) {
		        		repeatTestBlock(numRowsOccupied);
		        		numRows = table.getRowCount();
		        		i = numRowsOccupied;
		        	} else {
		        		i = 0;
		        	}
		        }
        	} while(repeatBox.isSelected() && rowsFound);
        } catch (Exception e) 
        {
        	System.out.println(e.getMessage());
        }
        if (rowsFound) {
        	changeProgressText("Progress: Complete");
        	graphButton.setEnabled(true);
			graphList.setEnabled(true);
        } else {
        	changeProgressText("Progress: No endpoints specified");
        }
        runButton.setEnabled(true);
        addButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }
    
    /**
     * Repeat the original test block so we can loop through it again.
     * @param numOfOriginalRows
     */
    private void repeatTestBlock(int numRowsOccupied) {
        int numRows = table.getRowCount();
        int nextRowNum = numRowsOccupied + 1;
        TableModel model = table.getModel();
        
        for (int i = 0; i < numOfOriginalRows; i++) {
        	//If there aren't enough rows, add one
        	if (numRowsOccupied >= numRows) {
        		addRow();
        		numRows++;
        	}
        	model.setValueAt(nextRowNum, numRowsOccupied, 0);
        	model.setValueAt(model.getValueAt(i, 1), numRowsOccupied, 1);
        	model.setValueAt(model.getValueAt(i, 2), numRowsOccupied, 2);
        	model.setValueAt(model.getValueAt(i, 3), numRowsOccupied, 3);
        	model.setValueAt(model.getValueAt(i, 4), numRowsOccupied, 4);
        	model.setValueAt(model.getValueAt(i, 5), numRowsOccupied, 5);
        	model.setValueAt("", numRowsOccupied, 6);
        	model.setValueAt("", numRowsOccupied, 7);
        	model.setValueAt("", numRowsOccupied, 8);
        	nextRowNum++; 
        	numRowsOccupied++;
        }

    }

    /**
     * Graph the results of all tests
     */
    public void graphResponseTime() {
        int numRows = table.getRowCount();
        TableModel model = table.getModel();
        boolean rowsFound = false;
        int averageIndex = 0;
    	List<AverageResponse> responseList = new ArrayList<AverageResponse>();

        final LineGraph graph = new LineGraph("End-to-End Webservice Test Framework", "Web Service Response Time", false);

        //Average the response times before graphing
        if (preferences.isAverageGraph() && numRows > numOfOriginalRows) {
        	for (int i = 0; i < numRows; i++) {
        		if (i < numOfOriginalRows) {
        			if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {
	        			rowsFound = true;
        				AverageResponse average = new AverageResponse();
	        			average.setTestNum(i + 1);
	        			average.calcAverage(Integer.parseInt(model.getValueAt(i, 8).toString()));
	        			responseList.add(average);
        			}
        		} else {
        			if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {        			
        				averageIndex = i - ((i / numOfOriginalRows) * numOfOriginalRows); 
	        			rowsFound = true;
        				AverageResponse average = responseList.get(averageIndex);
	        			average.calcAverage(Integer.parseInt(model.getValueAt(i, 8).toString()));
	        			responseList.add(average);
        			}
        		}
        	}
        }
        
        //Graph the results
        if (preferences.isAverageGraph() && numRows > numOfOriginalRows) {
        	if (rowsFound) {
        		Iterator<AverageResponse> it = responseList.iterator();
        		while (it.hasNext()) {
        			AverageResponse average = it.next();
        			graph.addToDataset1(average.getTestNum(), average.getAvgResponse());
        		}
        	}
        	
        } else {        
	        for (int i=0; i < numRows; i++) {
	        	if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {
	        		rowsFound = true;
	        		graph.addToDataset1(Integer.parseInt(model.getValueAt(i, 0).toString()), Integer.parseInt(model.getValueAt(i, 8).toString()));
	        	}
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
     * Graph the results by success or failure
     */
    public void graphResponseByResult() {
        int numRows = table.getRowCount();
        TableModel model = table.getModel();
        boolean rowsFound = false;
        int averageIndex = 0;
    	List<AverageResponse> successList = new ArrayList<AverageResponse>();
    	List<AverageResponse> failedList = new ArrayList<AverageResponse>();

        final LineGraph graph = new LineGraph("End-to-End Webservice Test Framework", "Web Service Response Time by Result", true);

        //Average the response times before graphing
        if (preferences.isAverageGraph() && numRows > numOfOriginalRows) {
        	for (int i = 0; i < numRows; i++) {
        		if (i < numOfOriginalRows) {
        			if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {
	        			rowsFound = true;
        				AverageResponse average = new AverageResponse();
	        			average.setTestNum(i + 1);
	        			average.calcAverage(Integer.parseInt(model.getValueAt(i, 8).toString()));
	        			if (model.getValueAt(i, 7).toString().contains("Success")) {
	        				successList.add(average);
	        				failedList.add(new AverageResponse());  //Add in a dummy object as a placeholder
	        			} 
	        			if (model.getValueAt(i, 7).toString().contains("Failed")) {
	        				failedList.add(average);
	        				successList.add(new AverageResponse());  //Add in a dummy object as a placeholder
	        			} 
        			}
        		} else {
        			if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {        			
        				averageIndex = i - ((i / numOfOriginalRows) * numOfOriginalRows); 
	        			rowsFound = true;
	        			if (model.getValueAt(i, 7).toString().contains("Success")) {
	        				AverageResponse average = successList.get(averageIndex);
		        			average.calcAverage(Integer.parseInt(model.getValueAt(i, 8).toString()));
		        			successList.add(average);
	        			}
	        			if (model.getValueAt(i, 7).toString().contains("Failed")) {
	        				AverageResponse average = failedList.get(averageIndex);
		        			average.calcAverage(Integer.parseInt(model.getValueAt(i, 8).toString()));
		        			failedList.add(average);
	        			}
        			}
        		}
        	}
        }

        //Graph the results
        if (preferences.isAverageGraph() && numRows > numOfOriginalRows) {
        	if (rowsFound) {
        		Iterator<AverageResponse> it = successList.iterator();
        		while (it.hasNext()) {
        			AverageResponse average = it.next();
        			if (average.getNumRuns() > 0) {
        				graph.addToDataset1(average.getTestNum(), average.getAvgResponse());
        			}
        		}

        		Iterator<AverageResponse> it2 = failedList.iterator();
        		while (it2.hasNext()) {
        			AverageResponse average = it2.next();
        			if (average.getNumRuns() > 0) {
        				graph.addToDataset2(average.getTestNum(), average.getAvgResponse());
        			}
        		}
}
        	
        } else {        
            for (int i=0; i < numRows; i++) {
            	if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {
            		rowsFound = true;
            		if (model.getValueAt(i, 7).toString().contains("Success")) {
            			graph.addToDataset1(Integer.parseInt(model.getValueAt(i, 0).toString()), Integer.parseInt(model.getValueAt(i, 8).toString()));
            		}
            		if (model.getValueAt(i, 7).toString().contains("Failed")) {
            			graph.addToDataset2(Integer.parseInt(model.getValueAt(i, 0).toString()), Integer.parseInt(model.getValueAt(i, 8).toString()));
            		}
            	}
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
     * Graph success and failure counts
     */
    public void graphSuccessFailure() {
        int numRows = table.getRowCount();
        TableModel model = table.getModel();
        boolean rowsFound = false;
        int successCount = 0;
        int failureCount = 0;

        for (int i=0; i < numRows; i++) {
        	if (model.getValueAt(i, 8) != null && !model.getValueAt(i, 8).equals("")) {
        		rowsFound = true;
        		if (model.getValueAt(i, 7).toString().contains("Success")) {
        			successCount++;
        		}
        		if (model.getValueAt(i, 7).toString().contains("Failed")) {
        			failureCount++;
        		}
        	}
        }
        if (rowsFound) {
            final BarGraph graph = new BarGraph("End-to-End Webservice Test Framework", "Web Service Success/Failure Counts", successCount, failureCount);
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
        TableModel model = table.getModel();

        for (int i=0; i < numRows; i++) {
        	model.setValueAt("", i, 6);
        	model.setValueAt("", i, 7);
        	model.setValueAt("", i, 8);
        }    	

        graphButton.setEnabled(false);
		graphList.setEnabled(false);
    }

    /**
     *  Clear all text that may be in the cells 
     */
    public static void initAllCells() {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        TableModel model = table.getModel();

        for (int i=0; i < numRows; i++) {
        	for (int j=1; j < numCols; j++) {  //Don't clear the first column
        		model.setValueAt("", i, j);
        	}
        }    	
        
        graphButton.setEnabled(false);
		graphList.setEnabled(false);
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

    /**
     *  Save the input to a file
     */
    private static void saveDialogue() {
        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();
        String fileRecord;
        TableModel model = table.getModel();

        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        if (preferences.getDefaultPath() != null && !preferences.getDefaultPath().isEmpty()) {
        	fc.setCurrentDirectory(new File(preferences.getDefaultPath()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("End-To-End Tester File (*.e2e)", "e2e"));
        int retVal = fc.showSaveDialog(frame);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File selectedfile = fc.getSelectedFile();
            String fileName = selectedfile.getAbsolutePath();
            if (!fileName.endsWith(".e2e")) {
            	fileName = fileName + ".e2e";
            }
            preferences.setDefaultPath(fileName.substring(0,fileName.lastIndexOf(File.separator)), true);  //Save the path so the user doesn't have to navigate to it next time
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
    private static void openDialogue() {
    	BufferedReader br = null;
    	String line = "";
        int numRows = table.getRowCount();
        boolean resultsFound = false;
        TableModel model = table.getModel();

    	
        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        if (preferences.getDefaultPath() != null && !preferences.getDefaultPath().isEmpty()) {
        	fc.setCurrentDirectory(new File(preferences.getDefaultPath()));
        }
        fc.setFileFilter(new FileNameExtensionFilter("End-To-End Tester File (*.e2e)", "e2e"));
        int retVal = fc.showOpenDialog(frame);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File selectedfile = fc.getSelectedFile();
            String fileName = selectedfile.getAbsolutePath();
            preferences.setDefaultPath(fileName.substring(0,fileName.lastIndexOf(File.separator)), true);  //Save the path so the user doesn't have to navigate to it next time
        	try {
        		br = new BufferedReader(new FileReader(fileName));
        		int rowNum = 0;
        		numOfOriginalRows = 0;  //Track the number of rows in the file so the average graphs will work
        		while ((line = br.readLine()) != null) {
        			// If the saved file has more rows than what is currently on the screen, add a row
        			if (rowNum >= numRows) {
        				addRow();
        			}
        			if (!line.equals("")) {
	        			String[] tableColumns = line.split(",", -1);  // use comma as separator and include the blanks
	        			if (tableColumns.length != 9) {
	                    	if (rowNum > 0) {
	                    		initAllCells();
	                    		rowNum = 10; //Set it to the default number of rows
	                    	} else {
	                    		rowNum = model.getRowCount();
	                    	}
	                    	JOptionPane.showMessageDialog(null, selectedfile.getName() + " is not properly formatted");
	                    	break;
	        			} else {
		        			for (int i = 0; i < tableColumns.length; i++) {
		        				model.setValueAt(tableColumns[i], rowNum, i);
		        			}
		        			if (!model.getValueAt(rowNum, 2).equals("")) {
		        				numOfOriginalRows++;  //If there is an endpoint, consider it a populated row 
		        			}
	        				if (!model.getValueAt(rowNum, 6).equals("") && !model.getValueAt(rowNum, 7).equals("") && !model.getValueAt(rowNum, 8).equals("")) {
	        					resultsFound = true;
	        				}
	        			}
        			}
        			rowNum++;
        		}
        		//If the loaded file has fewer rows than the table, delete the excess rows
        		if (model.getRowCount() > rowNum) {
        			for (int i = model.getRowCount(); i > rowNum; i--) {
        				deleteRow();
        			}
        		}
        		if (resultsFound) {
        			graphButton.setEnabled(true);
    				graphList.setEnabled(true);
        		} else {
        			graphButton.setEnabled(false);
    				graphList.setEnabled(false);
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
    
    private static void loadPreferencesUI() {
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
        preferFrame = new JDialog(frame, "Preferences", Dialog.ModalityType.DOCUMENT_MODAL);
        preferFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        preferFrame.setResizable(false);
        
		// Create and set up the content pane.
		PreferencesUI preferContentPane = new PreferencesUI();
		preferContentPane.setOpaque(true); // content panes must be opaque
		preferFrame.setContentPane(preferContentPane);

		preferFrame.setSize(400, 215);

        //Center the preferences window on the screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
        Point newLocation = new Point(middle.x - (preferFrame.getWidth() / 2), 
                                      middle.y - (preferFrame.getHeight() / 2));
        preferFrame.setLocation(newLocation);
        
		// Display the window.
        preferFrame.setVisible(true);

    	
    }
    
    public static void closePreferWindow() {
    	preferFrame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

}
