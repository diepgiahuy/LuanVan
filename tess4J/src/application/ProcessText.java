package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

public class ProcessText {
	public static final String MODELSPATH = "C:\\Users\\ACER\\Desktop\\UETsegmenter-master\\models\\";
	public static final String pathViSeg = "E:/Data_PDF/Segement/visegment.txt";
	public static final String pathEnSeg = "E:/Data_PDF/Segement/ensegment.txt";
	public static final String pathTransEn = "E:/Data_PDF/Segement/trans_en.txt";
	public static final String pathDataVi = "E:/Data_PDF/IOFile/output_vi.txt";
	public static final String pathDataEn = "E:/Data_PDF/IOFile/output_en.txt";
	public static final String pathEnFilter = "E:/Data_PDF/Segement/ensegment_output.txt";
	public static final String pathViFilter = "E:/Data_PDF/Segement/visegment_output.txt";
	private List<String> docTerms = new ArrayList<>();
	private List<String[]> termsDocsArray = new ArrayList<>();
	public Common common = new Common();
	private UETSegmenter segmenter = new UETSegmenter(MODELSPATH);

	public ProcessText() {
	}

	public void SegmentText(String inputFile, String outputFile) throws IOException {
		List<String> list = common.readFile(inputFile);
		for (String sentence : list) {
			String segmented = segmenter.segmentTokenizedText(sentence);
			// lsEnSeg.add(segmented);
			common.writeFile(outputFile, segmented);
		}
	}

	public String SegmentText_Sentence(String sentence) {
		String segmented = segmenter.segmentTokenizedText(sentence);
		return segmented;
	}

	public void TranslateFile() throws Exception {
		Translator http = new Translator();
		List<String> fileEn = common.readFile(pathDataEn);
		for (String sentence : fileEn) {
			String word = http.callUrlAndParseResult("en", "vi", sentence);
			common.writeFile(pathTransEn, word);
		}
		SegmentText(pathTransEn, pathEnSeg);
		SegmentText(pathDataVi, pathViSeg);
		GetResult();
	}

	public List<Map<CharSequence, Double>> parseFiles(List<String> document) {
		for (String sentence : document) {
			String[] tokenizedTerms = sentence.split("\\s");
			for (String term : tokenizedTerms) {
				term = term.replaceAll("([.?! ])", "");
				if (!docTerms.contains(term)) {
					docTerms.add(term);
				}
			}
			termsDocsArray.add(tokenizedTerms);
		}
		CosineSimilarity cosine = new CosineSimilarity();
		return cosine.getTF_IDF(termsDocsArray, docTerms);
	}

	public List<Map<CharSequence, Double>> parseQuerry(List<String> document) {
		List<String[]> termsQuerryArray = new ArrayList<>();
		for (String sentence : document) {
			sentence = sentence.replaceAll("([.?!])", "");
			String[] tokenizedTerms = sentence.split("\\s");
			termsQuerryArray.add(tokenizedTerms);
		}
		CosineSimilarity cosine = new CosineSimilarity();
		return cosine.getQuerryTFIDF(termsQuerryArray, termsDocsArray);
	}

	public void GetResult() throws Exception {
		int index = 0;
		int querryIndex = 0;
		int numOfSentence = 1;
		int size = 0;

		CosineSimilarity cosine = new CosineSimilarity();
		List<String> lsViSeg = common.readFile(pathViSeg);
		List<String> lsEnSeg = common.readFile(pathEnSeg);
		List<String> fileEn = common.readFile(pathDataEn);

		if (lsViSeg.size() < lsEnSeg.size()) {
			size = lsViSeg.size();
		} else
			size = lsEnSeg.size();
		int end = 0;
		for (int i = 0; i < size; i += 200) {
			end += 200;
			List<Map<CharSequence, Double>> vector1 = null;
			List<Map<CharSequence, Double>> vector2 = null;
			if (i + 200 >= size) {
				vector1 = parseFiles(lsViSeg.subList(i, lsViSeg.size()));
				vector2 = parseQuerry(lsEnSeg.subList(i, lsEnSeg.size()));

			} else {
				vector1 = parseFiles(lsViSeg.subList(i, end));
				vector2 = parseQuerry(lsEnSeg.subList(i, end));
			}

			for (Map<CharSequence, Double> key : vector2) {
				int count = i;
				index = i;
				double max = 0;
				for (Map<CharSequence, Double> map : vector1) {
					double point = cosine.cosineSimilarity(key, map);
					if (point > max) {
						max = point;
						index = count;
					}
					count++;
				}
				if (max >= 0.55) {
					common.writeFile(pathViFilter, "=========================== point: " + max
							+ "============= numofSentence: " + numOfSentence);
					common.writeFile(pathViFilter, lsViSeg.get(index));
					// common.writeFile(pathViFilter,lsEnSeg.get(querryIndex));
					common.writeFile(pathViFilter, fileEn.get(querryIndex));
					numOfSentence++;
				}
				System.out.println(max + "   " + index + "     " + querryIndex);
				querryIndex++;
			}
		}

	}

	public void splitText(String fullText, String path, int page) {
		fullText = fullText.replaceAll("(—{1,} )", "");
		fullText = fullText.replaceAll(
				"[^A-Za-zẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴắằẳẵặăấầẩẫậâáàãảạđếềểễệêéèẻẽẹíìỉĩịốồổỗộôớờởỡợơóòõỏọứừửữựưúùủũụýỳỷỹỵ\\.?\\-\\s!—0-9]",
				"");
		fullText = fullText.replaceAll("(\\.\\s*\\.\\s*\\.)", " ");
		fullText = fullText.replaceAll("(?m:^(?=[\r\n]|\\z)\n)", "");
		fullText = fullText.replaceAll("(?<=\n|^)[\t ]+|[\t ]+(?=$|\n)", "");
		fullText = fullText.replaceAll(
				"(?<=.)\n(?=[a-zắằẳẵặăấầẩẫậâáàãảạđếềểễệêéèẻẽẹíìỉĩịốồổỗộôớờởỡợơóòõỏọứừửữựưúùủũụýỳỷỹỵ0-9])", " ");
		List<String> sentences = Arrays.asList(fullText.split("\n"));
		try {
			common.writeFile(path, sentences);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
