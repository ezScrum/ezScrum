package ntut.csie.ezScrum.iteration.core;

public interface RelationEnum {
	//history's old value
	final public static String EMPTY_FIELD_NAME = null;
	final public String PARENT_OLD_VALUE = "2";
	final public String CHILD_OLD_VALUE = "3";
	final public String ZERO_OLD_VALUE = "0";
	final public String ZERO_NEW_VALUE = "0";
	final public String IMPLICATIONOF_OLD_VALUE = "8";
	final public String TRANSFORMTO_OLD_VALUE = "9";
	final public String TRANSFORMBY_OLD_VALUE = "10";
	
	//relation type description
	final public String PARENT_OF = "parent of";
	final public String CHILD_OF = "child of";
	final public String IMPLICATIONOF_OF = "implication of";
	final public String TRANSFORMTO_TO = "transform - create to";
	final public String TRANSFORMBY_BY = "transform - create by";
	
	
	final public int DESCRIPTION_UPDATE_VALUE = 6;
	final public int NOTES_UPDATE_VALUE = 3;
	
	final public int BUGNOTE_ADD_TYPE = 2;
	final public int ISSUE_NEW_TYPE = 1;
	final public int OTHER_TYPE = 0;
	
	/* Issue history 的 type */
	final public int RELEATIONSHIP_ADD_TYPE = 18;
	final public int RELEATIONSHIP_DELETE_TYPE = 19;

	/* Issue relation的type */
	final public int RELEATIONSHIP_DUPLICATE  = 0;
	final public int RELEATIONSHIP_RELATEDTO = 1;
	final public int RELEATIONSHIP_PARENTANDCHILD = 2;
	
	final public int RELEATIONSHIP_IMPLICATIONOF = 8;
	final public int RELEATIONSHIP_TRANSFORMTO = 9;
	final public int RELEATIONSHIP_TRANSFORMBY = 10;
	
	/* Kanban WorkItem Relation Type 
	 * A B 20 -> A 為 B 的 Parent
	 * */
	final public int KANBAN_RELEATIONSHIP_PARENT = 20;
	//final public int KANBAN_RELEATIONSHIP_CHILD = 21;
}