package de.ncoder.elements;

public class Main {
	public static int value = 0;

	public static void main(String[] args) throws Exception {
		Manager manager = new Manager(args);
		manager.create();
	}
}
