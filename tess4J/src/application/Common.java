package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

//import ai.vitk.tok.Tokenizer;
//import ai.vitk.type.Token;
//import edu.stanford.nlp.ling.Word;
//import edu.stanford.nlp.process.PTBTokenizer;

public class Common {
	public String[] lsSpecialChar = { ".", ",", "\"", "/", "!", "?", "-", "(", ")", "+", "=", ":", ";" };

	public List<String> ReadAllFileInFolder(String pathFolder) {
		List<String> lsFileName = new ArrayList<>();
		File folder = new File(pathFolder);
		File[] lsFile = folder.listFiles();
		for (File file : lsFile) {
			lsFileName.add(file.getName());
		}
		return lsFileName;
	}

	public List<String> readFile(String path) throws IOException {
		List<String> list = new ArrayList<>();
		FileInputStream fileInPutStream = new FileInputStream(path);
		Reader reader = new java.io.InputStreamReader(fileInPutStream, "utf8");
		BufferedReader br = new BufferedReader(reader);
		String line;
		String str = "";
		while ((line = br.readLine()) != null) {
			line = line.replace("\uFEFF", "");
			String lst[] = line.split("(?<![A-Z][a-z][a-z]\\.)(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s");

			if (lst.length > 1) {
				for (String sentence : lst) {
					if (sentence.matches(
							"^[A-ZẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ].*[.?]\\s*$")) {
						if (!str.equals("")) {
							list.add(str + " " + sentence);
							str = "";
						} else
							list.add(sentence);
					} else {
						if (!str.equals(""))
							str += " " + sentence;
						else
							str += sentence;

					}
				}
			} else {
				if (line.matches(
						"^[A-ZẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ].*[.?]\\s*$")) {
					if (!str.equals("")) {
						list.add(str + " " + line);
						str = "";
					} else
						list.add(line);
				} else {
					str += line;
					if (str.matches(
							"[A-ZẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ].*[.?]\\s*$")) {
						list.add(str);
						str = "";
					}

				}
			}
		}
		if (!str.equals("")) {
			list.add(str);

		}
		br.close();
		reader.close();
		return list;
	}

	public void writeFile(String path, List<String> list) throws IOException {
		FileWriter fileWriter = new FileWriter(path, true);

		PrintWriter printWriter = new PrintWriter(fileWriter);
		for (String str : list) {
			printWriter.println(str);
		}
		printWriter.close();

	}

	public void writeFile(String path, String str) throws IOException {
		FileWriter fileWriter = new FileWriter(path, true);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.println(str);
		printWriter.close();

	}

	public void separateFile(List<String> listSen, int numOfSenInFile, String outFile) throws IOException {
		int count = 0;
		List<String> lsTmp = new ArrayList<>();
		int i = 1;
		for (String sen : listSen) {
			count++;
			lsTmp.add(sen);
			if (count == numOfSenInFile) {
				writeFile(outFile + String.valueOf(i), lsTmp);
				lsTmp.clear();
				count = 0;
				i++;
			}
		}
		if (!lsTmp.isEmpty()) {
			writeFile(outFile + String.valueOf(i), lsTmp);
		}
	}

	public void mergeFile(List<String> lsNameFile, String pathFolderMerge, String pathOut, String nameOut)
			throws IOException {
		List<String> list = new ArrayList<>();
		for (int i = 1; i <= 260; i++) {
			List<String> lsSen = readFile(pathFolderMerge + i);
			for (String str : lsSen) {
				list.add(str);
			}
		}
		writeFile(pathOut + nameOut, list);
	}

	public void checkRowsInFile(List<String> listFile, String pathFolder, int point) throws IOException {
		for (String name : listFile) {
			List<String> ls = readFile(pathFolder + name);
			if (ls.size() < point) {
				System.out.println(name);
			}
		}
	}

	public boolean isExistInListString(String item, List<String> list) {
		for (String str : list) {
			if (item.equals(str)) {
				return true;
			}
		}
		return false;
	}

	public List<String[]> splitListString(List<String> list, String key) {
		List<String[]> lsResult = new ArrayList<>();
		for (String str : list) {
			String[] arrStr = str.split(key);
			lsResult.add(arrStr);
		}
		return lsResult;
	}

	public int countUnSpecialChar(String[] arr) {
		int count = 0;
		for (String str : arr) {
			if (!isExistSpecialChar(str)) {
				count++;
			}
		}
		return count;
	}

	public boolean isExistSpecialChar(String str) {
		for (String charr : lsSpecialChar) {
			if (charr.equals(str)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkRows2File(String pathFile1, String pathFile2) throws IOException {
		List<String> lsSen1 = readFile(pathFile1);
		List<String> lsSen2 = readFile(pathFile2);
		if (lsSen1.size() == lsSen2.size()) {
			return true;
		}
		return false;
	}

	public void mergeSenAllFile(String pathFolderEn, String pathFolderVi, String pathEnOut, String pathViOut)
			throws IOException {
		List<String> lsNameFileEn = ReadAllFileInFolder(pathFolderEn);
		List<String> lsEn = new ArrayList<>();
		List<String> lsVi = new ArrayList<>();

		for (String nameFileEn : lsNameFileEn) {
			String nameFileVi = nameFileEn.substring(0, nameFileEn.length() - 6) + "vi.txt";
			List<String> senEn = readFile(pathFolderEn + nameFileEn);
			List<String> senVi = readFile(pathFolderVi + nameFileVi);

			lsEn.addAll(senEn);
			lsVi.addAll(senVi);
		}
		writeFile(pathEnOut, lsEn);
		writeFile(pathViOut, lsVi);
	}
}
