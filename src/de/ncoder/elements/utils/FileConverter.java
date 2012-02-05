package de.ncoder.elements.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import de.ncoder.elements.Manager;
import de.ncoder.elements.engine.World;

public class FileConverter {
	public static void convert(File file, Manager manager) throws IOException {
		File backup = new File(file.getParent(), file.getName().replaceAll(".elm", " BACKUP.elm"));
		copy(file, backup);

		String read;
		try {
			read = read(openCompressed(file));
		} catch (ZipException e) {
			System.err.println(e.getClass() + ": " + e.getMessage() + " ->Trying uncompressed mode.");
			read = read(openUncompressed(file));
		}
		String converted = convert(read);
		saveCompressed(file, converted);

		check(file, manager);
		backup.delete();
	}

	public static void check(File file, Manager manager) throws IOException {
		Reader r = openCompressed(file);
		manager.updateXStream();
		Object loaded = manager.getXStream().fromXML(r);
		if (!(loaded instanceof World)) {
			throw new ClassCastException("The object in the file are not a instance of World.");
		}
	}

	public static void saveCompressed(File file, String converted) throws IOException {
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(file));
		ZipEntry entry = new ZipEntry("data.xml");
		zout.putNextEntry(entry);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(zout));
		bw.write(converted);
		bw.close();
		zout.close();
	}

	public static Reader openCompressed(File file) throws ZipException, IOException {
		ZipFile zFile = new ZipFile(file);
		return new InputStreamReader(zFile.getInputStream(zFile.getEntry("data.xml")));
	}

	public static Reader openUncompressed(File file) throws IOException {
		return new FileReader(file);
	}

	public static String convert(String contents) throws IOException {
		contents = contents.replaceAll("de\\.ncoder\\.elements\\.World", "de.ncoder.elements.engine.World");
		contents = contents.replaceAll("de\\.ncoder\\.elements\\.Element", "de.ncoder.elements.engine.Element");
		contents = contents.replaceAll("de\\.ncoder\\.elements\\.element\\.Spring", "de.ncoder.elements.element.Spawner");
		contents = contents.replaceAll("de\\.ncoder\\.elements\\.element\\.TNT", "de.ncoder.elements.element.Fuse");
		contents = contents.replaceAll("de\\.ncoder\\.elements\\.element\\.ActiveTNT", "de.ncoder.elements.element.ActiveFuse");
		contents = contents.replaceAll("<color>[\n| ]*<red>(.*)</red>[\n| ]*<green>(.*)</green>[\n| ]*<blue>(.*)</blue>[\n| ]*<alpha>(.*)</alpha>[\n|  ]*</color>", "<color>$1, $2, $3, $4</color>");
		contents = contents.replaceAll("<background>[\n| ]*<red>(.*)</red>[\n| ]*<green>(.*)</green>[\n| ]*<blue>(.*)</blue>[\n| ]*<alpha>(.*)</alpha>[\n|  ]*</background>", "<background>$1, $2, $3, $4</background>");
		contents = contents.replaceAll("elem\\.", "de.ncoder.elements.element.");

		contents = contents.replaceAll("de\\.ncoder\\.elements\\.engine\\.World", "World");
		return contents;
	}

	public static String read(Reader r) throws IOException {
		BufferedReader br = new BufferedReader(r);
		StringBuilder bob = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			bob.append(line);
			bob.append(System.lineSeparator());
		}
		br.close();
		return bob.toString();
	}

	public static void copy(File source, File target) throws IOException {
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(target);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}
}
