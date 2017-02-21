package edu.upenn.cis.citation.ui;

import edu.upenn.cis.citation.dao.Database;
import javafx.scene.control.TreeItem;

public class TreeNode {
     private String name;

     public TreeNode(String name) {
         this.name = name;
     }

     public TreeNode() {
        this.name = "";
     }

     public String getName() {
         return this.name;
     }

    public static TreeItem<TreeNode> createTreeItem(String s) {
        TreeItem<TreeNode> treeItem = new TreeItem<TreeNode>(new TreeNode(s));
        treeItem.setExpanded(true);
        for (String attr : Database.getAttrList(s)) {
            treeItem.getChildren().add(new TreeItem<TreeNode>(new TreeNode(attr)));
        }
        return treeItem;
    }
}
