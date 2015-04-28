package shoppingcart;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
		
//		AutoConverter.newInstance().convert("C:\\Users\\Administrator\\Desktop\\test.pdf", null);
		FileConverter.autoConvert("C:\\Users\\Administrator\\Desktop\\test.tif", "C:/test/");
	}

	static abstract class FileConverter {
		/**
		 *  不区分大小写键的Map
		 */
		static final Map<String, FileConverter> converterMap = new HashMap<String, FileConverter>(){
			private static final long serialVersionUID = 1L;
			public FileConverter put(String key, FileConverter value) {
				return super.put(key != null ? key.toLowerCase() : null, value);
			};
			public FileConverter get(Object key) {
				return super.get(key != null ? key.toString().toLowerCase() : null);
			};
		};
		/**
		 *  默认注册pdf和tif转换器
		 */
		static {
			register(".pdf", new PdfConverter());
			register(".tif", new TiffConverter());
		}
		/**
		 *  注册转换器
		 */
		public static void register(String name, FileConverter converter) {
			converterMap.put(name, converter);
		}
		/**
		 *  获取转换器
		 */
		public static FileConverter getConverter(String name) {
			return converterMap.get(name);
		}
		/**
		 *  转换文件
		 */
		public static void autoConvert(String srcFile, String destFile) {
			String name = new File(srcFile).getName();
			if(name.contains(".")) {
				name = name.substring(name.lastIndexOf("."));
			}
			// 根据后缀获取转换器
			FileConverter fileConverter = converterMap.get(name);
			if(fileConverter == null) { // 默认转换器
				fileConverter = new DefaultConverter();
			}
			fileConverter.convert(srcFile, destFile);
		}
		
		public static void convert(String srcFile, String destFile, String name) {
			FileConverter fileConverter = converterMap.get(name);
			if(fileConverter == null) {
				throw new RuntimeException("don't support " + name + " convert.");
			}
			fileConverter.convert(srcFile, destFile);
		}
		
		public static void convert(String srcFile, String destFile, Class<? extends FileConverter> converterClz) {
			FileConverter fileConverter = null;
			try {
				fileConverter = converterClz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			fileConverter.convert(srcFile, destFile);
		}
		
		public void convert(String srcFile, String destFile) {
			try {
				// 检查目录是否存在
				checkDir(destFile);
				// 获取图像
				List<BufferedImage> images = getBufferedImages(new File(srcFile));
				// 输出图像到指定位置
				BufferedImage image = null;
				for (int i = 0, le = images.size(); i < le; i++) {
					// 压缩图像
					image = compress(images.get(i));
					// 输出图像
					ImageIO.write(image, "jpg", new File(destFile + i + ".jpg"));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public abstract List<BufferedImage> getBufferedImages(File srcFile);
		
		/**
		 * 检查目录，不存在则创建
		 */
		public void checkDir(String _dir){
			File dir = new File(_dir);
			if(!dir.exists()){
				dir.mkdirs();
			}
		}
		
		/**
		 * 压缩图像
		 */
		public BufferedImage compress(BufferedImage image){
			return image;
		} 
	}
	
	static class DefaultConverter extends FileConverter {
		@Override
		public List<BufferedImage> getBufferedImages(File srcFile) {
			List<BufferedImage> _images = new ArrayList<BufferedImage>();
			try {
				_images = Arrays.asList(Imaging.getBufferedImage(srcFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return _images;
		}
		
	}

	static class PdfConverter extends FileConverter {
		@Override
		public List<BufferedImage> getBufferedImages(File pdfFile) {
			List<BufferedImage> _images = new ArrayList<BufferedImage>();
			try {
				List<?> pages = PDDocument.load(pdfFile).getDocumentCatalog().getAllPages();
				for (int i = 0, le = pages.size(); i < le; i++) {
					PDPage page = (PDPage) pages.get(i);
					_images.add(page.convertToImage());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return _images;
		}
	}

	static class TiffConverter extends FileConverter {
		@Override
		public List<BufferedImage> getBufferedImages(File tiffFile) {
			List<BufferedImage> _images = new ArrayList<BufferedImage>();
			try {
				_images = Imaging.getAllBufferedImages(tiffFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return _images;
		}
	}
}
