package condias;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.tuple.Pair;

public class Main {

	private static final MultiValuedMap<String, String> entries = new ArrayListValuedHashMap<>();

	public static void main(String[] args) {

		Pair<String, String> dirs = getDirectory(args);
		Path p = Paths.get(dirs.getLeft());

		try {
			Files.walk(p).forEach(path -> {
				if (path.toFile().isFile()) {
					try {
						entries.put(" File", path.getFileName().toString());
						List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_16);
						treatLines(lines);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		writeToFile(dirs.getRight());
	}

	private static void writeToFile(String targetDir) {
		File target = new File(targetDir + "\\Erg.csv");
		try (PrintStream out = new PrintStream(new FileOutputStream(target))) {
			entries.keySet().stream().sorted().forEach(key -> {
				out.print(key);
				out.print(';');
				out.println(String.join(";", entries.get(key)));
			});

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void treatLines(List<String> lines) {
		boolean start = false;
		for (String line : lines) {
			if (line.equals("nm,Ext,Min/Max")) {
				start = true;
				continue;
			}

			if (start) {
				Pair<String, String> entry = parse(line);
				entries.put(entry.getLeft(), entry.getRight());
			}
		}
	}

	private static Pair<String, String> parse(String line) {
		String[] tokens = line.split(",");

		String left = tokens[0].trim().replace('.', ',');
		String right = tokens[1].trim().replace('.', ',');

		return Pair.of(left, right);
	}

	private static Pair<String, String> getDirectory(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException("Es wird exakt ein Quell- und ein Zielpfad erwartet");
		}

		return Pair.of(args[0], args[1]);
	}

}
