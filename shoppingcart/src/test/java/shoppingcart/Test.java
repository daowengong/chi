package shoppingcart;

import java.io.IOException;

import net.coobird.thumbnailator.Thumbnails;

public class Test {
	public static void main(String[] args) throws IOException {
		Thumbnails.of("C:\\Users\\Administrator\\Desktop\\GONGDAOWEN\\0.png").size(200, 300).toFile("C:\\Users\\Administrator\\Desktop\\0_1.png");
	}
}
