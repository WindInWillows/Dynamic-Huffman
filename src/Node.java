/*
 * This class is used to create nodes for the Huffman tree.
 */
public class Node {
	char data;
	Node left, right, parent;
	int weight,id;
	Node pre, post;
	
	public Node(char data, int id, int weight){
		this.data = data;
		this.id = id;
		this.weight = weight;
		pre = post = parent = left = right = null;
	}
}
