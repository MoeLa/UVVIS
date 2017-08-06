package condias;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class UvvisFile implements Comparable<UvvisFile> {

	private String fileName;
	private Map<Float, Float> points = new HashMap<>();

	public UvvisFile(String fileName) {
		this.fileName = fileName;
	}

	public String getDescription() {
		String[] tokens = fileName.split("_");
		String[] tokens2 = tokens[3].split("\\.");

		return tokens2[0];
	}

	public Map<Float, Float> getPoints() {
		return points;
	}

	public void addPoints(List<String> lines) {
		boolean start = false;
		for (String line : lines) {
			if (line.equals("nm,Ext,Min/Max")) {
				start = true;
				continue;
			}

			if (start) {
				Pair<Float, Float> entry = parse(line);
				points.put(entry.getLeft(), entry.getRight());
			}
		}
	}

	private Pair<Float, Float> parse(String line) {
		String[] tokens = line.split(",");

		Float left = Float.valueOf(tokens[0].trim());
		Float right = Float.valueOf(tokens[1].trim());

		return Pair.of(left, right);
	}

	@Override
	public int compareTo(UvvisFile o) {
		Integer v1 = Integer.valueOf(getDescription());
		Integer v2 = Integer.valueOf(o.getDescription());

		return v1.compareTo(v2);
	}

}
