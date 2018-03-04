package Parser;

public class Tree {
	// ----------
    // Attributes
    // ----------
    
    Node root = null;  // Note the NULL root node of this tree.
    Node cur;     // Note the EMPTY current node of the tree we're building.


    // -- ------- --
    // -- Methods --
    // -- ------- --

    // Add a node: kind in {branch, leaf}.
    public void addNode(String name, String kind) {
        // Construct the node object.
        Node node = new Node();
        node.name = name;

        

        // Check to see if it needs to be the root node.
        if  (this.root == null) 
        {
            // We are the root node.
            this.root = node;
        }
        else
        {
            // We are the children.
            // Make our parent the CURrent node...
            node.parent = this.cur;
            // ... and add ourselves to the children array of the current node.
            this.cur.children.add(node);
        }
        // If we are an interior/branch node, then...
        if (kind == "branch")
        {
            // ... update the CURrent node pointer to ourselves.
            this.cur = node;
        }
    }
    
    
    
    // Resets the tree to empty
    public void reset() {
    	
    		this.root = null;
    		this.cur = null;
    }
    
    
    

    // Note that we're done with this branch of the tree...
    public void endChildren() {
        // ... by moving "up" to our parent node (if possible).
        if ((this.cur.parent != null) && (this.cur.parent.name != null))
        {
            this.cur = this.cur.parent;
        }
        else
        {
            // TODO: Some sort of error logging.
            // This really should not happen, but it will, of course.
        }
    }
    
    

    // Return a string representation of the tree.
    public String toString() {
        
        // Make the initial call to expand from the root.
    		String traversalResult = expand(this.root, 0);
        // Return the result.
        return traversalResult;
    }
    
    
    
    // Recursive function to handle the expansion of the nodes.
    public String expand(Node node, int depth)
    {
    		String traversalResult = "";
    	
        // Space out based on the current depth so
        // this looks at least a little tree-like.
        for (int i = 0; i < depth; i++)
        {
            traversalResult += "-";
        }

        // If there are no children (i.e., leaf nodes)...
        if (node.children.equals(null) || node.children.size() == 0)
        {
            // ... note the leaf node.
            traversalResult += "[" + node.name + "]";
            traversalResult += "\n";
        }
        else
        {
            // There are children, so note these interior/branch nodes and ...
            traversalResult += "<" + node.name + "> \n";
            // .. recursively expand them.
            for (int i = 0; i < node.children.size(); i++)
            {
                traversalResult = traversalResult + expand(node.children.get(i), depth + 1);
            }
        }
		return traversalResult;
    }
    
}
