import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/*
 *  
 */
public class DynamicHuffman {
	/* To flag whether the char code has occured or not. */
//	private boolean[] hasFlag;
	private String path = "F:\\Java\\workspace\\Dynamic Huffman\\src\\";
	private String outFileName = path + "huffman.out";
	
	/* The number of character in ASCII. */
	private static final int SIZE = 65536;

	private static final char EOF = (char) 3;
	
	/* To storage the leaf node. */
//	private LeafNode lf_head;
	
	private Node[] LeafList;
	
	/* The root of the huffman tree. */
//	private Node head;
	
	private int e;
//	private int r = SIZE / 2;
	private final char ZERO = 'N'; 
	
	private DataOutputStream dos;
	private DataInputStream dis;
	BufferedReader br;
	InputStreamReader isr;
	FileInputStream fis;
	FileOutputStream fos;
	
	private int min_id = 2 * SIZE - 1;
	
	private Node NYT = null;
	private Node root = null;
//	private Node levelTail = null;
	
	private ArrayList<Node> NodeList = null;
	
	public DynamicHuffman(){
		LeafList = new Node[SIZE*2];
		e = getE(SIZE);
		NYT = new Node(ZERO, min_id , 0);
		root = NYT;
		root.parent = new Node('R', min_id , 0);
		NodeList = new ArrayList<Node>();
	}
	
	/*
	 * To construct a dynamic huffman tree to use a filename.
	 */
	public DynamicHuffman(String filename) {
		this();
		readFile(filename);
	}
	
	/*
	 * The final display function, to  decompress the file and create a text 
	 * file the same as the the file before compressed.
	 * return: true for decompressed successfully, false for failed.
	 */
	public void decode() throws IOException{
		reset();
		int cur = 0;
		String line = br.readLine();
		char[] bits = line.toCharArray();
		Node n = root;
		boolean flag = true;
		while(cur < bits.length){
			n = root;
			/* Judge whether the node is a leaf node*/
			
			while(flag){
				if(n.left == null && n.right == null){
					if(n.equals(NYT)){
						String num = "";
						int len = cur+e;
						while(cur < len){
							num += bits[cur++];
						}
						char ch = (char)transToChar(num);
						fos.write(ch);
						encodeUpdate(ch);
						break;
					}
					else {
						char ch = n.data;
						fos.write(ch);
						update(NYT.parent.right);
						break;
					}
				}
				else{
					char tmp = bits[cur++];
					if(tmp == '0'){
						n = n.left;
					}
					else n = n.right;
				}
			}
		}
		
		fos.close();
	}

	/*
		 * The final display function, to  compress the file and create a new 
		 * coding text file.
		 * return: true for compress successfully, false for failed.
		 */
		public void encode() throws IOException{
			int ch_num;
			char ch;
			if(br == null) return;
		
			while((ch_num = br.read()) != -1){
				ch = (char) ch_num;
				try{
				/* Has existed. */
				if(LeafList[ch_num] != null){
					Node n = findPos(ch);
					produceCode(n);
					update(n);
				}
				else {
					produceCode(NYT);
					
					for(int i=0;i<e;i++){
						char bit = (char) ((ch_num>>(e-i-1))&0x1);
						bit = (char)((int)bit + (int) '0');
						outBit(bit);
					}
					min_id -= 2;
					NYT.right = new Node(ch,min_id + 1, 0);
					LeafList[ch_num] = NYT.right;
					
					NYT.left = new Node(ZERO, min_id, 0);
					NYT.left.parent = NYT;
					NYT = NYT.left;
					
					/* NYT has been update; */
					NodeList.add(NYT.parent);
					NodeList.add(NYT.parent.right);
					update(NYT.parent.right);
				}
				}catch(ArrayIndexOutOfBoundsException e){
					System.out.println(ch_num);
				}
			}
	
	//		fos.close();
		}

	private void encodeUpdate(char ch) {
		min_id -= 2;
		NYT.right = new Node(ch,min_id + 1, 0);
		LeafList[(int)ch] = NYT.right;
		
		NYT.left = new Node(ZERO, min_id, 0);
		NYT.left.parent = NYT;
		NYT = NYT.left;
		
		/* NYT has been update; */
		NodeList.add(NYT.parent);
		NodeList.add(NYT.parent.right);
		update(NYT.parent.right);
		
	}

		/*
			 * To update the weight information of the tree node.
			 */
			private void update(Node n){
				if(n.parent == null){
					Node parent = NYT.parent;
					n.parent = parent;
					n.weight++;
					parent.weight++;
					
					/* If the current node's id isn't the max one in the 
					 * nodes with the same weight, swap this node with the 
					 * max id node.
					 */
					while(!parent.equals(root)) {
						parent = parent.parent;
						if(parent.equals(root)) {
							increaseWeight(root);
							return;
						}
						Node max = findMax(parent);
						if(!max.equals(parent)){
							swap(parent, max);
						}
						parent.weight++;
//						increaseWeight(parent);
					}
				}
				else {
					int weight = root.weight;
					do{
						Node max = findMax(n);
						if(! max.equals(n) && !max.equals(root)){
							swap(n, max);
						}
						n.weight++;
//						increaseWeight(n);
						n = n.parent;
					}while(! n.equals(root));
					if(root.weight == weight) root.weight++;
				}
			}

	private int transToChar(String num) {
		BigInteger bi = new BigInteger(num, 2);
		return bi.intValue();
	}

	private void reset() {
		min_id = 2 * SIZE - 1;
		NYT = new Node(ZERO, min_id , 0); 
		root = NYT;
		NodeList = new ArrayList<Node>();
		LeafList = new Node[SIZE*2];
		
		try {
			fis = new FileInputStream(outFileName);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			fos = new FileOutputStream(path + "decode.txt");
			dos = new DataOutputStream(fos);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.gc();
	}

	/*
	 * To produce code for a character.
	 */
	private void produceCode(Node n){
		char bit[] = new char[50];
		int count = 0;
		while(n != root){
			if(n.parent.left == n){
				bit[count++] = '0';
			}
			else bit[count++] = '1';
			n = n.parent;
		}
		for (int i = count-1; i>=0; i--){
			outBit(bit[i]);
		}
	}

	private void increaseWeight(Node n) {
		n.weight++;
	}

	/* To swap the current node with the node that has
	 * the biggest id. But the id is not swap.
	 * */
	private void swap(Node n, Node max) {

		int tmp_id = n.id;
		n.id = max.id;
		max.id = tmp_id;
		Node np = n.parent;
		Node maxp = max.parent;
		if(max == root) return;
		if(np.equals(maxp)){
			Node tmp = np.left;
			np.left = max;
			np.right = tmp;
			return;
		}
		Node t = (np.left.equals(n)) ? (np.left = max) : (np.right = max);
		t = (maxp.left.equals(max)) ? (maxp.left = n) : (maxp.right = n);
		n.parent = maxp;
		max.parent = np;
		
	}
	
	/* The function is used to find the max id node with the same weight. */
	private Node findMax(Node n) {
		/* Don't forget to make the pre field of the id_max node null
		 * after swap.
		 * */

		int weight = n.weight;
		LinkedList<Node> lk = new LinkedList<Node>();
		Node cur = root;
		lk.add(cur);
		while(!lk.isEmpty()){
			cur = lk.remove();
			if(cur.weight == weight) return cur;
			if(cur.right != null) lk.addLast(cur.right);
			if(cur.left != null) lk.addLast(cur.left);
		}
		return null;
	}

	private Node findPos(char ch) {
		int ch_num = (int) ch;
		return LeafList[ch_num];
	}

	/*
	 * Read a text file and return the its stream.
	 * return: FileInputStream.
	 */
	private void readFile(String filename){
//		File file = new File(filename);
		try {
			fis = new FileInputStream(filename);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			
			fos = new FileOutputStream(outFileName);
//			dos = new DataOutputStream(fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		return new FileInputStream(null);
	}
	
	/* To get a bit from file. */
	private char getBit(){
		return 0;
	}
	
	/* To output a bit to file. */
	private void outBit(char bit){
		
		try {
			fos.write(bit);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* According to the size of the character set, caculate
	 * the size of the init coding head;
	 * */
	private int getE(int s) {
		int e = 0;
		do{
			s /= 2;
			e++;
		}while(s != 1);
		e--;
		return e;
	}
	
}
