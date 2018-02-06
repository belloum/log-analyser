package loganalyser.ui.components;

import java.awt.GridLayout;
import java.util.LinkedHashMap;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.json.JSONObject;

import loganalyser.old.ui.MyButton;
import loganalyser.operators.FileSelector;
import loganalyser.ui.tabs.Configurable;

public class Menu extends JPanel implements Configurable {

	private static final long serialVersionUID = 1L;

	private LinkedHashMap<MenuItem, JButton> items = new LinkedHashMap<>();

	int mCurrentPosition;
	private MenuSelector mListener;
	private LogFrame mFilePanel;

	public Menu() {
		JSONObject sections;
		try {
			sections = configuration();
		} catch (Exception e) {
			System.err.println(String.format("Unfound menu in configuration file %s",
					Configurable.configurationFile().getName()));
			sections = new JSONObject();
		}

		setLayout(new GridLayout(sections.length(), 1));
		for (int i = 1; i <= sections.length(); i++) {
			MenuItem item = new MenuItem(i, sections.getJSONObject(String.valueOf(i)));
			items.put(item, new MyButton(item.label, event -> this.mListener.goTo(item.className)));
		}

		build();
	}

	private void build() {
		items.forEach((item, btn) -> add(btn));
	};

	public void addNavigationListener(MenuSelector pNavigationListener) {
		this.mListener = pNavigationListener;
	}

	public void addFileSelector(FileSelector pFileSelector) {
		this.mFilePanel.addFileSelectorListener(pFileSelector);
	}

	@Override
	public void setEnabled(boolean pEnabled) {
		super.setEnabled(pEnabled);
		items.forEach((item, btn) -> btn.setEnabled(pEnabled));
	}

	public void disableMenu() {
		items.forEach((item, btn) -> btn.setEnabled(!item.isFileDependant));
	}

	public interface MenuSelector {
		void goTo(String pSection);
	}

	@Override
	public String configurationSection() {
		return "menu";
	}

	public class MenuItem {
		String label;
		Boolean isFileDependant;
		String className;

		public MenuItem(int pPosition, JSONObject pJSONObject) {
			this.label = pJSONObject.getString("label");
			this.className = pJSONObject.getString("class_name");
			this.isFileDependant = pJSONObject.getBoolean("need_file");
		}

	}

}
