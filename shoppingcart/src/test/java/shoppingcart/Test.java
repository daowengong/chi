package shoppingcart;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.Imaging;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class Test {
	public static void main(String[] args) throws IOException {
		// Thumbnails.of("C:\\Users\\Administrator\\Desktop\\GONGDAOWEN\\0.png").size(200,
		// 300).toFile("C:\\Users\\Administrator\\Desktop\\0_1.png");
		DefaultConverter.register(".pdf", new PdfConverter());
		DefaultConverter.register(".tif", new TiffConverter());
		
//		AutoConverter.newInstance().convert("C:\\Users\\Administrator\\Desktop\\test.pdf", null);
		DefaultConverter.newInstance().convert("C:\\Users\\Administrator\\Desktop\\test.tif", "C:/test/");
	}

	interface FileConverter {
		void convert(String file1, String file2);
	}

	static class DefaultConverter implements FileConverter {
		private static final DefaultConverter autoConverter = new DefaultConverter();
		
		static final Map<String, FileConverter> converterMap = new HashMap<String, FileConverter>();
		static {
			register(".gif", new PdfConverter());
		}
		
		public static DefaultConverter newInstance(){
			return autoConverter;
		}

		public static void register(String regexp, FileConverter converter) {
			converterMap.put(regexp, converter);
		}

		public void convert(String file1, String file2) {
			String name = new File(file1).getName();
			if(name.contains("."))
				name = name.substring(name.lastIndexOf("."));
			converterMap.get(name).convert(file1, file2);
		}
		
		public void checkDir(String _dir){
			File dir = new File(_dir);
			if(!dir.exists()){
				dir.mkdirs();
			}
		}
	}

	static class PdfConverter extends DefaultConverter {
		public void convert(String pdfFile, String pdfDir) {
			try {
				checkDir(pdfDir);
				List<?> pages = PDDocument.load(pdfFile).getDocumentCatalog().getAllPages();
				for (int i = 0, le = pages.size(); i < le; i++) {
					PDPage page = (PDPage) pages.get(i);
					ImageIO.write(page.convertToImage(), "jpg", new File(pdfDir + i + ".jpg"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	static class TiffConverter extends DefaultConverter {
		public void convert(String tiffFile, String tiffDir) {
			try {
				checkDir(tiffDir);
				List<BufferedImage> images = Imaging.getAllBufferedImages(new File(tiffFile));
				for(int i = 0, le = images.size(); i < le; i ++){
					ImageIO.write(images.get(i), "jpg", new File(tiffDir + i + ".jpg"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
