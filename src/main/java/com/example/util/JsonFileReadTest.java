package com.example.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class JsonFileReadTest {
	private static final String FILE_PATH_JSON = "D:\\my_data2\\2.my_projects-2\\50.norcom\\1.Document\\3.InstallationAndConfiguration\\";
	private static final String FILE_NAME = "enron_small.json";
	//private static final String fName = FILE_PATH_JSON + FILE_NAME;

	public static void main(String[] args) throws IOException {
		// raedfileWay1();
		// readFileWay2();
		// FILE_PATH_JSON + FILE_NAME

		// Method #1 - Read all lines as a Stream
		// fileStreamUsingFiles(fName);
		// System.out.println();

		// Method #2 - Read file with a filter
		// filterFileData(fName);
		// System.out.println();

		// Method #3 - In Java8, 'BufferedReader' has the 'lines()' method which
		// returns the file content as a Stream
		// fileStreamUsingBufferedReader(fName);
	}

	@SuppressWarnings("deprecation")
	public static void readFileWay2() throws IOException {
		LineIterator it = FileUtils.lineIterator(new File(FILE_PATH_JSON + FILE_NAME), "UTF-8");
		try {
			while (it.hasNext()) {
				String line = it.nextLine();
				line = line.replaceAll("_id", "id");
				System.out.println(line);
			}
		} finally {
			LineIterator.closeQuietly(it);
		}
	}

	public static void raedfileWay1() throws FileNotFoundException, IOException {
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(FILE_PATH_JSON + FILE_NAME);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				System.out.println(line);
			}
			// note that Scanner suppresses exceptions
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

	// Method #1
	public static void fileStreamUsingFiles(String fileName) {
		try {
			Stream<String> lines = Files.lines(Paths.get(fileName));
			System.out.println("<!-----Read all lines as a Stream-----!>");
			lines.forEach(System.out::println);
			lines.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	// Method #2
	public static void filterFileData(String fileName) {
		try {
			Stream<String> lines = Files.lines(Paths.get(fileName)).filter(line -> line.startsWith("s"));
			System.out.println("<!-----Filtering the file data using Java8 filtering-----!>");
			lines.forEach(System.out::println);
			lines.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	// Method #3
	public static void fileStreamUsingBufferedReader(String fileName) {
		try {
			BufferedReader br = Files.newBufferedReader(Paths.get(fileName));
			Stream<String> lines = br.lines().map(str -> str.toUpperCase());
			System.out.println("<!-----Read all lines by using BufferedReader-----!>");
			lines.forEach(System.out::println);
			lines.close();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

}
