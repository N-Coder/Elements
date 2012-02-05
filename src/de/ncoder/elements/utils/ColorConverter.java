package de.ncoder.elements.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.SingleValueConverter;

public class ColorConverter implements SingleValueConverter {
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class type) {
		return type.equals(Color.class);
	}

	@Override
	public Object fromString(String content) {
		String[] stringValues = content.split(",");
		List<Integer> values = new ArrayList<Integer>();
		for (int i = 0; i < stringValues.length; i++) {
			if (stringValues[i] != null) {
				stringValues[i] = stringValues[i].trim();
				if (!stringValues[i].isEmpty()) {
					values.add(Integer.parseInt(stringValues[i]));
				}
			}
		}
		if (values.size() == 1) {
			return new Color(values.get(0));
		} else if (values.size() == 3) {
			return new Color(values.get(0), values.get(1), values.get(2));
		} else if (values.size() == 4) {
			return new Color(values.get(0), values.get(1), values.get(2), values.get(3));
		}
		return null;
	}

	@Override
	public String toString(Object object) {
		Color color = (Color) object;
		return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + (color.getAlpha() == 255 ? "" : ", " + color.getAlpha());
	}
}
