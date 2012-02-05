package de.ncoder.elements.utils.settings;

import java.util.Scanner;

public abstract class SettingsFunction {
	public static final String DELIMITER = "[ \t]*,[ \t]*";

	private String name;

	public SettingsFunction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract String parse(String line);

	public static String getDelimiter() {
		return DELIMITER;
	}

	public static Scanner getScanner(String line) {
		Scanner s = new Scanner(line);
		s.useDelimiter(DELIMITER);
		return s;
	}

	public static final SettingsFunction IF = new SettingsFunction("if") {
		@Override
		public String parse(String line) {
			System.out.println(line);
			Scanner s = getScanner(line);
			if (!s.hasNextBoolean()) {
				return "";
			}
			boolean condition = s.nextBoolean();
			if (!s.hasNext()) {
				return "";
			}
			String trueValue = s.next();
			if (!s.hasNext()) {
				return "";
			}
			String falseValue = s.next();
			if (condition) {
				return trueValue;
			} else {
				return falseValue;
			}
		}
	};
	public static final SettingsFunction NN = new SettingsFunction("nn") {
		@Override
		public String parse(String line) {
			Scanner s = getScanner(line);
			while (s.hasNext()) {
				String next = s.next().trim();
				if (!next.isEmpty()) {
					return next;
				}
			}
			return "";
		}
	};
	public static final SettingsFunction MAX = new SettingsFunction("max") {
		@Override
		public String parse(String line) {
			Scanner s = getScanner(line);
			int value = Integer.MIN_VALUE;
			while (s.hasNextInt()) {
				int next = s.nextInt();
				if (next > value) {
					value = next;
				}
			}
			return (value != Integer.MIN_VALUE ? value : 0) + "";
		}
	};
	public static final SettingsFunction MIN = new SettingsFunction("min") {
		@Override
		public String parse(String line) {
			Scanner s = getScanner(line);
			int value = Integer.MAX_VALUE;
			while (s.hasNextInt()) {
				int next = s.nextInt();
				if (next < value) {
					value = next;
				}
			}
			return (value != Integer.MAX_VALUE ? value : 0) + "";
		}
	};
}
