package utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Utils {

	public static LinkedHashMap<String, ?> sortMap(Map<String, ?> pMap) {
		return pMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
				Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}

	public static Image scaleImg(File pImgFile, int pWidth, int pHeight) throws IOException {
		return new ImageIcon(ImageIO.read(pImgFile)).getImage().getScaledInstance(pWidth, pHeight,
				java.awt.Image.SCALE_SMOOTH);
	}

//	public static JPanel panelWithImg(File pImg, Component pContent) {
//		JPanel frame = new JPanel(new BorderLayout());
//		try {
//			JLabel img = new JLabel(new ImageIcon(scaleImg(pImg, 50, 50)));
//			img.setBorder(new EmptyBorder(0, 0, 0, 10));
//			frame.add(img, BorderLayout.WEST);
//		} catch (IOException e) {
//			System.err.println("Unable to load file from res");
//		}
//
//		frame.add(pContent, BorderLayout.CENTER);
//		return frame;
//	}
}
