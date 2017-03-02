/* To storage the leaf node of the huffman tree*/
public class LeafNode {
	LeafNode next;
	Node leaf;
	
	public LeafNode(Node n){
		this.leaf = n;
		next = null;
	}
}
