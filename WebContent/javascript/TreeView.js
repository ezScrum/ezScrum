var node="/";
var tree;

function loadTree(){
	tree = new dhtmlXTreeObject("projectFileMenu","100%","100%",0);
	
	tree.setImagePath("images/fileview/");
	
	tree.setOnClickHandler(
		function(id){
			openPathDocs(id);
	});

	tree.insertNewItem(0,"/","root",0,"folderClosed.gif",0,0,"");
	autoselectNode();
}
		
function autoselectNode(){
	tree.selectItem(node,true);
	tree.openItem(node);
} 		
		
function openPathDocs(id){
	if( id.endsWith('tempNode')){
		return;
	}
	
	var child = tree.hasChildren(id);
	var type = id.substr(0,id.indexOf(':'));
	if( child == 0 && type != "file" && !noChildNodeList.get(id) ){
		tree.insertNewItem(id,id+"tempNode","Loading...",0,"Loading.gif",0,0,"");
		setTimeout('openPathDocsHandler(\"' + id + '\")',100);
	}
}

var noChildNodeList = new Hash(); 

function openPathDocsHandler(id){
	var modifyId = id.substr(id.indexOf(':')+1);
	new Ajax.Request('getSubFile.do',
 	{
 		parameters: { parent: modifyId },
    	onSuccess: function(transport){
    		tree.deleteItem(id+"tempNode");
    			
      		var pathlist = transport.responseXML.documentElement.getElementsByTagName("subpath");
      		
      		for( var i = 0; i< pathlist.length ; i++ ){
      			var name = pathlist[i].firstChild.nodeValue;
      			var type = pathlist[i].getAttribute('type');
      			var path = modifyId;
      			if( modifyId == "/" ){
      				path += name;
     			} else {
   					path += "/" + name;
   				}
      			var img = getIMG(name,type);
      			tree.insertNewItem(id,type+":"+path,name,0,img,0,0,"");
      		}
      		if( pathlist.length == 0 ){
      			noChildNodeList.set(id,true); 
      		}
      	},
      	onFailure: function(){
      		window.alert("與SVN連線失敗,請重新再試!!");
    		tree.deleteItem(id+"tempNode");
      	}
  	});		
}

/*
type為dir or file, 若為dir則回傳資料夾圖片src,
若為file,則根據副檔名決定
*/
function getIMG(name,type){
	var img = "iconText.gif";
	if( type == "dir" ){
      	img = "folderClosed.gif";
    } else {
    	var extend = getExtend(name);
    	if( extend != "" && extend != null ){
			img = "fileicons/" + extend + ".png";	
		}
    }
    
    return img;
}

//存在圖檔的副檔名, 若是知道javascipt確認檔案是否存在的方法,
//可直接取得副檔名然後接png去取得icon
//ex. name='test.java', extend='java' icon='java.png'
var extendArray = ['ai','aiff','c','chm','conf','cpp','css','csv','deb','divx','doc','docx','file',
'gif','gz','hjp','htm','html','iso','java','jpeg','jpg','js','mov','mp3','mpg','odc','odf','odg',
'odi','odp','ods','ogg','pdf','pgp','php','pl','png','ppt','pptx','ps','py','ram','rar','rb','rm',
'rpm','rtf','sql','swf','sxc','sxd','sxi','sxw','tar','tex','tgz','txt','vcf','wav','wma','wmv',
'xls','xml','xpi','xvid','zip' ];

//取得副檔名名稱
function getExtend(name){
	var lastindex = name.lastIndexOf('.');
	var extendName = name.substr( lastindex+1 ); 
	if( isExtendExist(extendName) ){
		return extendName;
	}
	return null;
}

//確認副檔名是否有存在的圖片
function isExtendExist(name){
	var index = extendArray.indexOf(name);
	if( index == -1 )
		return false;
	return true;
}
