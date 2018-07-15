
package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity {

	public Double cosineSimilarity(final Map<CharSequence, Double> leftVector,
			final Map<CharSequence, Double> rightVector) {
		if (leftVector == null || rightVector == null) {
			throw new IllegalArgumentException("Vectors must not be null");
		}
		final Set<CharSequence> intersection = getIntersection(rightVector, leftVector);
		final double dotProduct = dot(leftVector, rightVector, intersection);
		double d1 = 0.0d;
		for (final CharSequence key : intersection) {
			if (Double.isNaN(leftVector.get(key)) || Double.isInfinite(leftVector.get(key)))
				d1 += 0;
			else {
				d1 += Math.pow(leftVector.get(key), 2);
				// System.out.println(leftVector.get(key) + " d1");
				// System.out.println(Math.pow(leftVector.get(key), 2) + " d1
				// pow");

			}
		}
		// System.out.println("Math.sqrt(d1)" + Math.sqrt(d1));
		double d2 = 0.0d;
		for (final CharSequence key : intersection) {
			if (Double.isNaN(rightVector.get(key)) || Double.isInfinite(rightVector.get(key)))
				d2 += 0;
			else {
				d2 += Math.pow(rightVector.get(key), 2);
				// System.out.println(rightVector.get(key) + " d2");
				// System.out.println(Math.pow(rightVector.get(key), 2) + " d2
				// pow");
			}
		}
		// System.out.println("Math.sqrt(d2)" + Math.sqrt(d2));
		double cosineSimilarity;
		if (d1 <= 0.0 || d2 <= 0.0) {
			cosineSimilarity = 0.0;
		} else {
			// System.out.println(d1 + " " + Math.sqrt(d1));
			// System.out.println(d2 + " " + Math.sqrt(d2));
			cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
		}
		return cosineSimilarity;
	}

	private Set<CharSequence> getIntersection(final Map<CharSequence, Double> leftVector,
			final Map<CharSequence, Double> rightVector) {
		final Set<CharSequence> intersection = new HashSet<>(leftVector.keySet());
		intersection.retainAll(rightVector.keySet());
		// System.out.println(leftVector);
		// System.out.println(rightVector);
		// System.out.println(intersection);
		return intersection;
	}

	private double dot(final Map<CharSequence, Double> leftVector, final Map<CharSequence, Double> rightVector,
			final Set<CharSequence> intersection) {
		double dotProduct = 0;
		for (final CharSequence key : intersection) {
			if (!Double.isNaN(rightVector.get(key)) && !Double.isInfinite(rightVector.get(key))
					&& !Double.isNaN(leftVector.get(key)) && !Double.isInfinite(leftVector.get(key))) {
				dotProduct += leftVector.get(key) * rightVector.get(key);
				// System.out.println(dotProduct + " dot ");
				// System.out.println( leftVector.get(key) + " dot leftVector
				// ");
				// System.out.println( rightVector.get(key) + " dot
				// rightVector");

			}
		}
		return dotProduct;
	}

	public List<Map<CharSequence, Double>> getQuerryTFIDF(List<String[]> listQuerry, List<String[]> document) {
		double tf;
		double idf = 0;
		double tfidf;
		List<Map<CharSequence, Double>> DataSet = new ArrayList<>();
		for (String[] querry : listQuerry) {
			Map<CharSequence, Double> Vector = new HashMap<CharSequence, Double>();
			for (String term : querry) {
				tf = getTF(querry, term);
				// System.out.println("word: " + term + " tf: " + tf + "
				// Querry");
				idf = getIDF(document, term);
				// System.out.println("word: " + term + " idf: " + idf + "
				// Querry");
				tfidf = tf * idf;
				// System.out.println("word: " + term + " tfidf: " + tfidf + "
				// Querry");
				Vector.put(term, tfidf);
			}
			DataSet.add(Vector);
		}
		return DataSet;

	}

	public double getTF(String[] listWordOfSentence, String word) {
		double count = 0;
		for (String s : listWordOfSentence) {
			if (s.equalsIgnoreCase(word)) {
				count++;
			}
		}
		return count / listWordOfSentence.length;
	}

	public double getIDF(List<String[]> listSentence, String word) {
		double count = 0;
		for (String[] listWordInSentence : listSentence) {
			for (String wordInSentence : listWordInSentence) {
				if (wordInSentence.equalsIgnoreCase(word)) {
					count++;
					break;
				}
			}
		}
		return 1 + Math.log(listSentence.size() / count);
	}

	public List<Map<CharSequence, Double>> getTF_IDF(List<String[]> listSentence, List<String> wordOfAllSentence) {
		double tf;
		double idf = 0;
		double tfidf;
		List<Map<CharSequence, Double>> DataSet = new ArrayList<>();
		for (String[] Sentence : listSentence) {
			Map<CharSequence, Double> Vector = new HashMap<CharSequence, Double>();
			for (String word : wordOfAllSentence) {
				tf = getTF(Sentence, word);
				// System.out.println("word: " + word + " tf: " + tf);
				idf = getIDF(listSentence, word);
				// System.out.println("word: " + word + " idf: " + idf);
				tfidf = tf * idf;
				Vector.put(word, tfidf);
				// System.out.println("word: " + word + " tfidf: " + tfidf);
			}
			DataSet.add(Vector);
		}
		return DataSet;
	}

}
