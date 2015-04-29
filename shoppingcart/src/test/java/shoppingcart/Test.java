package shoppingcart;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.PixelDensity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class Test {
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		File pdfs = new File("C:\\Users\\Administrator\\Desktop\\test\\pdfs");
		for (File f : pdfs.listFiles()) {
			FileConverter.autoConvert(f.getPath(), "C:/tmp/{name}_{suffix}/{index}.jpg");
		}
		long end_1 = System.currentTimeMillis();
		File tifs = new File("C:\\Users\\Administrator\\Desktop\\test\\tifs");
		for (File f : tifs.listFiles()) {
			FileConverter.autoConvert(f.getPath(), "C:/tmp/{name}_{suffix}/{index}.jpg");
		}
		long end_2 = System.currentTimeMillis();
		System.out.println((end_1 - start) / 1000.0);
		System.out.println((end_2 - start) / 1000.0);
	}

	/**
	 * 文件转换器
	 * @code FileConverter.autoConvert("C:/9.jpg", "C:/{name}/{index}.jpg");
	 * @result 输出文件 C:/9/1.jpg
	 * @params <pre>destFileFormat:
	 *   {name}      = 文件名称
	 *   {extension} = 扩展名
	 *   {suffix}    = 后缀名（不包含点）
	 *   {index}     = 第几页（默认1）
	 *</pre>
	 */
	static abstract class FileConverter {
		/**
		 * 不区分大小写键的Map
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
		 * 默认注册pdf和tif转换器
		 */
		static {
			register(".pdf", new PdfConverter());
			register(".tif", new TiffConverter());
		}
		/**
		 * 注册转换器
		 */
		public static void register(String name, FileConverter converter) {
			converterMap.put(name, converter);
		}
		/**
		 * 获取转换器
		 */
		public static FileConverter getConverter(String name) {
			return converterMap.get(name);
		}
		/**
		 * 转换文件-自动根据扩展名转换
		 */
		public static void autoConvert(String srcFile, String destFileFormat) {
			String name = new File(srcFile).getName();
			if(name.contains(".")) {
				name = name.substring(name.lastIndexOf("."));
			}
			// 根据后缀获取转换器
			FileConverter fileConverter = converterMap.get(name);
			if(fileConverter == null) { // 默认转换器
				fileConverter = new DefaultConverter();
			}
			fileConverter.convert(srcFile, destFileFormat);
		}
		/**
		 * 转换文件-根据指定的转换器名称转换
		 */
		public static void convert(String srcFile, String destFileFormat, String name) {
			FileConverter fileConverter = converterMap.get(name);
			if(fileConverter == null) {
				throw new RuntimeException("don't support " + name + " convert.");
			}
			fileConverter.convert(srcFile, destFileFormat);
		}
		/**
		 * 转换文件-根据指定的转换器类转换
		 */
		public static void convert(String srcFile, String destFileFormat, Class<? extends FileConverter> converterClz) {
			FileConverter fileConverter = null;
			try {
				fileConverter = converterClz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			fileConverter.convert(srcFile, destFileFormat);
		}
		/**
		 * 转换文件
		 */
		public void convert(String srcFile, String destFileFormat) {
			try {
				SystemFile _srcFile = new SystemFile(srcFile);
				destFileFormat = destFileFormat.replace("{name}", _srcFile.name);
				destFileFormat = destFileFormat.replace("{extension}", _srcFile.extension);
				destFileFormat = destFileFormat.replace("{suffix}", _srcFile.suffix);
				// 获取图像
				List<BufferedImage> images = getBufferedImages(_srcFile.file);
				// 输出图像到指定位置
				BufferedImage image = null;
				for (int i = 0, le = images.size(); i < le; i++) {
					image = images.get(i);
					SystemFile destFile = checkFile(destFileFormat.replace("{index}", String.valueOf(i + 1))); // 替换页码
					// 输出图像
//					ImageIO.write(image, destFile.suffix, destFile.file); 
					Imaging.writeImage(image, destFile.file, ImageFormats.TIFF, getCompressParams());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public abstract List<BufferedImage> getBufferedImages(File srcFile);
		
		/**
		 * 检查文件，如果目录不存在则创建
		 */
		public SystemFile checkFile(String _file){
			SystemFile file = new SystemFile(_file);
			if(!file.file.getParentFile().exists()){
				file.file.getParentFile().mkdirs();
			}
			return file;
		}
		
		/**
		 * 压缩图像参数
		 */
		public Map<String, Object> getCompressParams() {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(ImagingConstants.PARAM_KEY_PIXEL_DENSITY, PixelDensity.createFromPixelsPerCentimetre(300, 300)); // 300 DPI
			return map;
		}
	}
	/**
	 * 文件
	 */
	static class SystemFile {
		File file;
		String name;
		String extension;
		String suffix;
		String dir;
		
		public SystemFile(String filepath){
			this.file = new File(filepath);
			String filename = this.file.getName();
			int lastIndex = filename.lastIndexOf(".");
			if(lastIndex != -1){
				this.name = filename.substring(0, lastIndex);
				this.extension = filename.substring(lastIndex);
			} else {
				this.extension = filename;
			}
			if(this.extension.length() > 1){
				this.suffix = this.extension.substring(1);
			}
			this.dir = this.file.getParent();
		}
	}
	/**
	 * 默认文件转换器
	 */
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
	/**
	 * PDF文件转换器
	 */
	static class PdfConverter extends FileConverter {
		@Override
		public List<BufferedImage> getBufferedImages(File pdfFile) {
			List<BufferedImage> _images = new ArrayList<BufferedImage>();
			PDDocument pdf = null;
			try {
				pdf = PDDocument.load(pdfFile);
				List<?> pages = pdf.getDocumentCatalog().getAllPages();
				for (int i = 0, le = pages.size(); i < le; i++) {
					PDPage page = (PDPage) pages.get(i);
					_images.add(page.convertToImage());
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(pdf != null) {
					try {
						pdf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			 
			return _images;
		}
	}
	/**
	 * TIFF文件转换器
	 */
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
