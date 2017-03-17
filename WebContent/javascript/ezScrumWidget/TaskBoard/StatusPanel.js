Ext.ns('ezScrum')

/**
 * 負責產生Non Checkout Out、CheckOut與Done三個欄位 並且只有相對應ID的Story或Task可以移動到這上面來
 * 
 * @param id
 * @return
 */

ezScrum.StatusColumn = Ext.extend(Ext.Panel, {
	border : true,
	bodyBorder : false,
	columnWidth : .33,
	listeners : {
		render : function(panel) {
			var dropTarget = new Ext.dd.DropTarget(panel.body, {
				realTarget : panel,
				status : panel.status,
				ddGroup : panel.dragID,
				copy : false,
				overClass : 'over',
				add : function(component) {
					panel = this.realTarget;
					panel.add(component);
					panel.doLayout();
					panel.getParent().resetCellHeightUndeferred();
				},
				insert : function(index, component) {
					panel = this.realTarget;
					panel.insert(index,component);
					panel.doLayout();
					panel.getParent().resetCellHeightUndeferred();
				}
			});
		}
	},
	getParent : function() {
		return this.findParentBy(function(container, component){
			return true;
		});
	},
	getAllElementHeight:function(){
		//取得底下每個Element的高度
		var el = this;

		var promise = new Promise(function(resolve,reject){
			el.on('afterlayout',function(){
				var allPromise = [];
				
				for(var i=0;i<el.items.length;i++){
					allPromise.push(el.get(i).getElHeight())
				}
				
				Promise.all(allPromise).then(function(data){
					
					var sum = data.reduce(function(a, b){
						  return a + b;
						  }, 0);
					resolve(sum)
				},function(){
					var allDeferPromise = []
					for(var i=0;i<el.items.length;i++){
						allDeferPromise.push(el.get(i).getElHeightDeferred())
					}
					Promise.all(allDeferPromise).then(function(data){
						var sum = data.reduce(function(a, b){
							  return a + b;
							  }, 0);
						resolve(sum)
					})
				})
			})
		})  
		return promise;
	},
	getAllElementHeightUndeferred:function(){
		//取得底下每個Element的高度
		var el = this;

		var promise = new Promise(function(resolve,reject){
					var allDeferPromise = []
					for(var i=0;i<el.items.length;i++){
						allDeferPromise.push(el.get(i).getElHeightDeferred())
					}
					Promise.all(allDeferPromise).then(function(data){
						var sum = data.reduce(function(a, b) {
							  return a + b;
							  }, 0);
						resolve(sum)
					})			
		})  
		return promise;
	}
});
Ext.reg('ezScrumStatusColumn', ezScrum.StatusColumn);

function createStoryStatusPanel(storyID){
	var statusPanel = new Ext.Panel( {
		defaultType : 'ezScrumStatusColumn',
		layout : 'column',
		colspan : 3,
		items : [ {
			id : storyID + '_new',
			dragID : storyID,
			status : 'new'
		}, {
			id : storyID + '_assigned',
			dragID : storyID,
			status : 'assigned'
		}, {
			id : storyID + '_closed',
			dragID : storyID,
			status : 'closed'
		} ],
		resetCellHeight : function(h){
			if('undefined' == typeof(h)){
		        h=0;
		    }
			var that = this;
			var allPromise = [];
			for ( var i = 0; i < this.items.length; i++) {
				allPromise.push(this.get(i).getAllElementHeight());
			}
			Promise.all(allPromise).then(function(dataArray){
				var highEst = Math.max.apply(Math,dataArray);
				for ( var i = 0; i < 3; i++) {
					that.get(i).setHeight(highEst * 1.1);
				}
			},function(er){
				console.log("Render fail, please reload the browser " + er);
			})	
		},
		resetCellHeightUndeferred: function(h) {
			if('undefined' == typeof(h)){
		        h=0;
		    }
			var that = this
				var allPromise = [];
				for ( var i = 0; i < this.items.length; i++){				
					allPromise.push(this.get(i).getAllElementHeightUndeferred());				
				}
				Promise.all(allPromise).then(function(dataArray){
					var highEst = Math.max.apply(Math,dataArray)
					for ( var i = 0; i < 3; i++) {
						that.get(i).setHeight(highEst * 1.1);
					}
				},function(er){
					console.log("Render fail, please reload the browser " + er);
			})	
		}
	});
	return statusPanel;
}