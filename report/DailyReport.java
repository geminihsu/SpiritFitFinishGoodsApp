package spirit.fitness.scanner.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.mashape.unirest.http.HttpClientHelper;
import com.mashape.unirest.http.HttpResponse;

import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.http1.Http1Codec;
import spirit.fitness.scanner.AppMenu;
import spirit.fitness.scanner.common.Constrant;
import spirit.fitness.scanner.common.HttpRequestCode;
import spirit.fitness.scanner.restful.FGRepositoryImplRetrofit;
import spirit.fitness.scanner.restful.HttpRestApi;
import spirit.fitness.scanner.restful.ShippingRepositoryImplRetrofit;
import spirit.fitness.scanner.restful.listener.InventoryCallBackFunction;
import spirit.fitness.scanner.util.ExcelHelper;
import spirit.fitness.scanner.util.LocationHelper;
import spirit.fitness.scanner.util.PrinterHelper;
import spirit.fitness.scanner.zonepannel.ZoneMenu;
import spirit.fitness.scanner.model.Itembean;
import spirit.fitness.scanner.model.Reportbean;

public class DailyReport  {

	public final static int REPORT = 0;
	public final static int MIN_QUANTITY = 1;

	public JFrame frame;
	private String items;

	private ProgressMonitor progressMonitor;
	private JButton btnDone;

	private String result;

	private int type;

	public DailyReport(List<Reportbean> data, int _type) {
		type = _type;

		displayTable(data);

	}

	private void displayTable(List<Reportbean> data) {

		JFrame.setDefaultLookAndFeelDecorated(false);
		JDialog.setDefaultLookAndFeelDecorated(false);
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Item Result");
		frame.setLocationRelativeTo(null);
		frame.setBounds(50, 50, 1200, 600);
		frame.setUndecorated(true);
		frame.setResizable(false);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Constrant.FRAME_BORDER_BACKGROUN_COLOR));
		panel.setBackground(Constrant.BACKGROUN_COLOR);
		// adding panel to frame
		frame.add(panel);

		placeComponents(panel, data);

		// frame.setLocationRelativeTo(null);
		// frame.setSize(1000, 500);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

				frame.dispose();
				frame.setVisible(false);
			}
		});

	}

	private void placeComponents(JPanel panel, List<Reportbean> data) {

		/*
		 * We will discuss about layouts in the later sections of this tutorial. For now
		 * we are setting the layout to null
		 */
		panel.setLayout(null);

		// ScrollPane for Result
		JScrollPane scrollZonePane = new JScrollPane();

		scrollZonePane.setBackground(Constrant.TABLE_COLOR);
		panel.add(scrollZonePane);

		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

		Object rowDataReport[][] = new Object[10][12];
		System.out.println(data.size());

		int prevTotal = 0;
		int shippedTotal = 0;
		int receivedTotal = 0;
		int scrappedTotal = 0;
		int shippableOnHandTotal = 0;
		int returnUnshippableTotal = 0;
		int showroom = 0;
		int reworkTotal = 0;
		int qcTotal = 0;
		int Total = 0;
		
		for (int i = 0; i < 10; i++) {

			/*if (i == 10) {
				for (int j = 0; j < 12; j++) {
					rowDataReport[i][0] = " TOTAL";
					rowDataReport[i][1] = "";
					rowDataReport[i][2] = prevTotal;
					rowDataReport[i][3] = shippedTotal;
					rowDataReport[i][4] = receivedTotal;
					rowDataReport[i][5] = scrappedTotal;
					rowDataReport[i][6] = shippableOnHandTotal;
					rowDataReport[i][7] = returnUnshippableTotal;
					;
					rowDataReport[i][8] = showroom;
					rowDataReport[i][9] = reworkTotal;
					rowDataReport[i][10] = qcTotal;
					rowDataReport[i][11] = Total;
				}
			} else {*/
				for (int j = 0; j < 12; j++) {
					rowDataReport[i][0] = " " + data.get(i).Model;
					rowDataReport[i][1] = data.get(i).FG;
					rowDataReport[i][2] = data.get(i).total;
					rowDataReport[i][3] = data.get(i).unshippable;
					rowDataReport[i][4] = data.get(i).zone1;
					rowDataReport[i][5] = data.get(i).unshippable;
					rowDataReport[i][6] = data.get(i).zone1;
					rowDataReport[i][7] = data.get(i).returnItem;

					rowDataReport[i][8] = data.get(i).showRoom;
					rowDataReport[i][9] = data.get(i).total;
					rowDataReport[i][10] = data.get(i).showRoom;
					rowDataReport[i][11] = data.get(i).total;
					
					prevTotal += data.get(i).total;
					shippedTotal += data.get(i).unshippable;
					receivedTotal += data.get(i).zone1;
					scrappedTotal += data.get(i).unshippable;
					shippableOnHandTotal += data.get(i).zone1;
					returnUnshippableTotal += data.get(i).returnItem;
					showroom += data.get(i).showRoom;
					reworkTotal += data.get(i).total;
					qcTotal += data.get(i).showRoom;
					Total += data.get(i).total;
					
				//}
			}
		}

		String zone = "";

		Object columnNames[] = { "Model#", "FG", "Previous", "Shipped", "Received", "Scrapped", "Shippable/On Hand", "Return/Unshippable",
				"ShowRoom", "Rework","QC","Total" };
		Font font = new Font("Verdana", Font.BOLD, 15);
		final Class[] columnClass = new Class[] { String.class, String.class, Integer.class, Integer.class,
				Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };

		DefaultTableModel model = new DefaultTableModel(rowDataReport, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {

				return false;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return columnClass[columnIndex];
			}
		};

	

		

		JTable table = new JTable(model);
		table.getTableHeader().setFont(font);
		table.getTableHeader().setBackground(Constrant.DISPALY_ITEMS_TABLE_COLOR);
		table.setBackground(Constrant.DISPALY_ITEMS_TABLE_COLOR);
		table.setFont(font);
		table.setRowHeight(40);
		TableColumn modelNo = table.getColumnModel().getColumn(0);
		modelNo.setPreferredWidth(90);
		TableColumn modelTitle = table.getColumnModel().getColumn(1);
		modelTitle.setPreferredWidth(180);
		TableColumn prevCcolumn = table.getColumnModel().getColumn(2);
		prevCcolumn.setPreferredWidth(90);
		TableColumn shippingColumn = table.getColumnModel().getColumn(3);
		shippingColumn.setPreferredWidth(90);
		TableColumn receivedColumn = table.getColumnModel().getColumn(4);
		receivedColumn.setPreferredWidth(100);
		TableColumn scrappedColumn = table.getColumnModel().getColumn(5);
		scrappedColumn.setPreferredWidth(100);
		TableColumn onHand = table.getColumnModel().getColumn(6);
		onHand.setPreferredWidth(200);
		TableColumn returnQty = table.getColumnModel().getColumn(7);
		returnQty.setPreferredWidth(200);
		TableColumn column = table.getColumnModel().getColumn(8);
		column.setPreferredWidth(120);
		
		TableColumn qCcolumn = table.getColumnModel().getColumn(10);
		qCcolumn.setPreferredWidth(50);
		table.setCellSelectionEnabled(false);
		table.setColumnSelectionAllowed(false);
		table.setEnabled(false);

	
		int heigh = 0;

		if (50 * rowDataReport.length + 20 > 530)
			heigh = 530;
		else
			heigh = 430;
		scrollZonePane.setBounds(5, 5, 1190, 427);
		
		scrollZonePane.setViewportView(table);

		panel.add(scrollZonePane);
		
		Border border = LineBorder.createBlackLineBorder();
		
		JLabel modelLabel = new JLabel(" Total                                          "+prevTotal+"             "+shippedTotal+"      "+receivedTotal+"               "+shippedTotal+"                       "+receivedTotal+"                                "+shippedTotal+"                   "+shippedTotal + "  "+receivedTotal+"       "+shippedTotal+ " "+receivedTotal);
		
		modelLabel.setBounds(5, 418, 1190, 50);
		modelLabel.setOpaque(true);
		modelLabel.setBackground(Constrant.DISPALY_ITEMS_TABLE_COLOR);
		modelLabel.setFont(font);
		modelLabel.setBorder(border);
		panel.add(modelLabel);
		
		Font btnFont = new Font("Verdana", Font.BOLD, 18);
		btnDone = new JButton("Export To Excel");
		btnDone.setFont(btnFont);
		btnDone.setBounds(5, 520, 200, 50);

		if (type == MIN_QUANTITY)
			btnDone.setText("Update Model Quantity");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							ExcelHelper exp = new ExcelHelper();

							if (type == REPORT) {
								exp.fillData(table,
										new File("C:\\Users\\geminih\\Downloads\\" + timeStamp + "_report.xls"));

								JOptionPane.showMessageDialog(null, "Export " + timeStamp + ".xls' successfully",
										"Message", JOptionPane.INFORMATION_MESSAGE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				// HttpRestApi.postData(result);
			}
		});
		panel.add(btnDone);

		JButton exitDone = new JButton("Exit");
		exitDone.setFont(btnFont);
		exitDone.setBounds(220, 520, 200, 50);

		exitDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				frame.setVisible(false);
			}
		});
		panel.add(exitDone);
	}

	

}
