package de.mhus.lib.core.util;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Table implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	LinkedList<TableColumn> columns = new LinkedList<>();
	HashMap<String, Integer> columnsIndex = new HashMap<>();
	LinkedList<TableRow> rows = new LinkedList<>();
	
	public Table() {
	}
	
	public Table(ResultSet res) throws SQLException {
		ResultSetMetaData meta = res.getMetaData();
		int count = meta.getColumnCount();
		for (int i = 0; i < count; i++) {
			addHeader(meta.getColumnName(i+1), meta.getColumnTypeName(i+1));
		}
		
		while (res.next()) {
			TableRow row = new TableRow();
			row.setTable(this);
			for (int i = 0; i < count; i++) {
				row.appendData(res.getObject(i+1));
			}
			getRows().add(row);
		}
		res.close();
	}
	
	public List<TableColumn> getColumns() {
		return columns;
	}
	
	public List<TableRow> getRows() {
		return rows;
	}

	public TableColumn addHeader(String name, String type) {
		TableColumn col = new TableColumn();
		col.setName(name);
		col.setType(type);
		columnsIndex.put(name, columns.size());
		columns.add(col);
		return col;
	}
	
	public TableRow addRow(Object ... data) {
		TableRow row = new TableRow();
		row.setTable(this);
		row.setData(data);
		rows.add(row);
		return row;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		
		out.writeUTF(name);
		
		out.writeInt(columns.size());
		for (TableColumn col : columns)
			out.writeObject(col);
		
		out.writeInt(rows.size());
		for (TableRow row : rows)
			out.writeObject(row);
	}
	private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException {
		
		name = in.readUTF();
		{
			int size = in.readInt();
			//columns.clear();
			columns = new LinkedList<>();
			// columnsIndex.clear();
			columnsIndex = new HashMap<>();
			for (int i = 0; i < size; i++) {
				TableColumn col = (TableColumn) in.readObject();
				columnsIndex.put(col.getName(), columns.size());
				columns.add(col);
			}
		}
		{
			int size = in.readInt();
			// rows.clear();
			rows = new LinkedList<>();
			for (int i = 0; i < size; i++) {
				TableRow row = (TableRow) in.readObject();
				row.setTable(this);
				rows.add(row);
			}
		}
	}
	/*
	 private void readObjectNoData()
	     throws ObjectStreamException {
		 
	 }
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getColumnIndex(String name) {
		Integer ret = columnsIndex.get(name);
		return ret == null ? -1 : ret;
	}
	
	@Override
	public String toString() {
		return columns.toString() + rows.toString();
	}

	public int getColumnSize() {
		return columns.size();
	}
	
	public int getRowSize() {
		return rows.size();
	}
	
}
