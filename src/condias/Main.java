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
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

public class Main {

	private static Set<UvvisFile> uvvisFiles = new HashSet<>();

	public static void main(String[] args) {

		Pair<String, String> dirs = getDirectory(args);
		Path p = Paths.get(dirs.getLeft());
		try {
			System.out.println("Lese alle .csv-Dateien in '" +
					p.toFile().getCanonicalPath() +
					"' (inklusive Unterordner)");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			Files.walk(p)
					.filter(path -> path.getFileName().toString().endsWith(".csv"))
					.forEach(path -> {
						if (path.toFile().isFile()) {
							String fileName = path.getFileName().toString();
							System.out.println("Lese " + fileName);

							UvvisFile uvvisFile = new UvvisFile(fileName);
							uvvisFiles.add(uvvisFile);

							try {
								List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_16);
								uvvisFile.addPoints(lines);
							} catch (IOException e) {
								e.printStackTrace();
								return;
							}
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		writeToFile(dirs.getRight());
	}

	private static void writeToFile(String targetDir) {
		File target = new File(targetDir + "\\Erg-UVVIS.csv");

		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);

		try (PrintStream out = new PrintStream(new FileOutputStream(target))) {
			// List of all x values (sorted)
			List<Float> x_values = uvvisFiles.stream()
					.flatMap(uf -> uf.getPoints().keySet().stream())
					.distinct()
					.sorted()
					.collect(Collectors.toList());

			List<UvvisFile> sortedFiles = uvvisFiles.stream()
					.sorted()
					.collect(Collectors.toList());

			// print header
			out.print(";");
			out.println(String.join(";", sortedFiles.stream()
					.map(UvvisFile::getDescription)
					.collect(Collectors.toList())));

			// Print line for each x value
			x_values.forEach(x -> {
				out.print(nf.format(x)); // Print x value
				out.print(";"); // Print separator
				out.println(String.join(";", sortedFiles.stream()
						.map(uf -> {
							if (uf.getPoints().containsKey(x)) {
								return nf.format(uf.getPoints().get(x)); // Print x value if existing
							} else {
								return ""; // Print empty if not existing
							}
						})
						.collect(Collectors.toList())));
			});

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static Pair<String, String> getDirectory(String[] args) {
		if (args.length != 2) {
			throw new IllegalArgumentException("Es wird exakt ein Quell- und ein Zielpfad erwartet");
		}

		return Pair.of(args[0], args[1]);
	}

}
