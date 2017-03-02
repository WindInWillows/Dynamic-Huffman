import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		String filename = "F:\\Java\\workspace\\Dynamic Huffman\\src\\test.txt";
		DynamicHuffman dh = new DynamicHuffman(filename);
		try {
			dh.encode();
			
			dh.decode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
