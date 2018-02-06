package loganalyser.ui.components;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class MyTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyTable(TableModel pTableModel) {
		super(pTableModel);
		setUp();
	}

	public MyTable(Object[][] pData, Object[] pHeaders) {
		this(new DefaultTableModel(pData, pHeaders) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Class<?> getColumnClass(int column) {
				try {
					Double.parseDouble(pData[0][column].toString());
					return Double.class;
				} catch (Exception e) {
					return String.class;
				}
			}

			public boolean isCellEditable(int row, int column) {
				return false;
			};

			@Override
			public Object getValueAt(int row, int column) {
				try {
					return Integer.parseInt(super.getValueAt(row, column).toString());
				} catch (Exception e) {
					return super.getValueAt(row, column);
				}
			}
		});
	}

	private void setUp() {
		setEnabled(false);
		getTableHeader().setReorderingAllowed(false);
		getTableHeader().setResizingAllowed(false);
		setAutoCreateRowSorter(true);
	}

}
