
package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

public class SampleController {
	@FXML
	Button MyButton;
	@FXML
	Button BtnChoose;
	@FXML
	Button BtnGetText;
	@FXML
	javafx.scene.control.TextArea TextArea;
	@FXML
	CheckBox CBox_Vi;
	@FXML
	CheckBox CBox_En;
	public static final String DESTFOLDER = "E:/Data_PDF/";
	public static int NumofPage = 1;
	public static final String pathViSeg = "E:/Data_PDF/Segement/visegment.txt";
	public static final String pathViGSeg = "E:/Data_PDF/Segement/ensegment.txt";
	public String path;
	public ProcessText proccesText = new ProcessText();

	public BufferedImage Preprocess(String fileName) throws FileNotFoundException {
		Mat myImg = Imgcodecs.imread(DESTFOLDER + fileName);
		Mat gray = new Mat();
		Imgproc.cvtColor(myImg, gray, Imgproc.COLOR_BGR2GRAY);
		// Imgproc.adaptiveThreshold(gray, gray, 255,
		// Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 40);
		Imgproc.threshold(gray, gray, 250, 255, Imgproc.THRESH_BINARY_INV);
		final List<MatOfPoint> points = new ArrayList<>();
		final Mat hierarchy = new Mat();
		Imgproc.findContours(gray.clone(), points, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Mat temp_contour = null;
		for (int i = 0; i < points.size(); i++) {
			temp_contour = points.get(i);
			double contourarea = Imgproc.contourArea(temp_contour);
			if (contourarea > 4000) {
				Imgproc.drawContours(myImg, points, i, new Scalar(255, 255, 255), -1);
				Core.bitwise_not(myImg, temp_contour);
				Imgproc.cvtColor(temp_contour, temp_contour, Imgproc.COLOR_BGR2GRAY);
				Core.bitwise_and(gray, temp_contour, gray);
			}
		}
		//Size size = new Size(1831, 2717);
		//Imgproc.resize(gray, gray, size);
		//Rect rectCrop = new Rect(90, 125, 1630, 2375);
		//gray = gray.submat(rectCrop);
		Mat image_output = gray.clone();
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", gray, mob);
		byte ba[] = mob.toArray();
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(new ByteArrayInputStream(ba));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Imgcodecs.imwrite(DESTFOLDER + "test.jpg", image_output);
		return bi;
	}

	public static void convertToImg(String sourceDir, String destinationDir) {
		try {
			File sourceFile = new File(sourceDir);
			File destinationFile = new File(destinationDir);
			if (!destinationFile.exists()) {
				destinationFile.mkdir();
				System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
			}
			if (sourceFile.exists()) {
				final PDDocument document = PDDocument.load(sourceFile);
				PDFRenderer pdfRenderer = new PDFRenderer(document);
				NumofPage = document.getNumberOfPages();
				// document.getNumberOfPages()
				for (int page = 0; page < NumofPage; ++page) {
					BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300,
							org.apache.pdfbox.rendering.ImageType.RGB);
					String fileName = destinationDir + "image-" + page + ".jpg";
					ImageIOUtil.writeImage(bim, fileName, 300);
				}
				document.close();
				System.out.println("Image saved at -> " + destinationFile.getAbsolutePath());
			} else {
				System.err.println(sourceFile.getName() + " File does not exist");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void TranslateFile() {
		try {
			proccesText.TranslateFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ChooseFile() throws IOException {
		FileChooser fileChooser = new FileChooser();
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			BtnChoose.setText("File selected: " + selectedFile.getName());
		} else {
			BtnChoose.setText("File selection cancelled.");
			return;
		}
		System.load("C:/Users/ACER/Downloads/opencv/build/java/x64/opencv_java341.dll");
		if (selectedFile.getName().matches(".*\\.pdf")) {
			System.out.println("============");
			convertToImg(selectedFile.getPath(), DESTFOLDER);
		}
		ITesseract instance = new Tesseract();
		instance.setDatapath("C:\\Users\\ACER\\Desktop\\LuanVan\\tess4J\\tessdata");
		if (CBox_Vi.isSelected()) {
			instance.setLanguage("vie");
			path = "E:/Data_PDF/IOFile/output_vi.txt";
		}
		if (CBox_En.isSelected()) {
			instance.setLanguage("eng");
			path = "E:/Data_PDF/IOFile/output_en.txt";
		}
		try {
			if (NumofPage > 1) {
				for (int page = 0; page < NumofPage; page++) {
					// String fileName = DESTFOLDER + "image-" + page + ".jpg";
					// Preprocess("image-" + page + ".jpg");
					// File ImageFile = new File(fileName);
					String result = instance.doOCR(Preprocess("image-" + page + ".jpg"));
					if(page == NumofPage - 1)
					{
						proccesText.splitText(result, path , 1);
					}
					else
					proccesText.splitText(result, path , 0);

					// TextArea.setText(result);
				}
			}
			if (NumofPage == 1) {
				// String fileName = DESTFOLDER + selectedFile.getName();
				// Preprocess(selectedFile.getName());
				// File ImageFile = new File(fileName);
				String result = instance.doOCR(Preprocess(selectedFile.getName()));
				proccesText.splitText(result, path , 1);
				// TextArea.setText(result);
			}
		} catch (Exception e) {
		}
	}
}