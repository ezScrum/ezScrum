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
					panel.getParent().resetCellHeight();
				}
			});
		}
	},
	getParent : function() {
		return this.findParentBy(function(container, component) {
			return true;
		});
	},
	getAllElementHeight:function()
	{
		//取得底下每個Element的高度
		var h=0;
		for(var i=0;i<this.items.length;i++)
		{
			h+=this.get(i).getHeight();
		}
		return h;
	}
});
Ext.reg('ezScrumStatusColumn', ezScrum.StatusColumn);

function createStoryStatusPanel(storyID) {
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
		resetCellHeight : function(h) {
			if('undefined' == typeof(h)){
		        h=0;
		    }
			for ( var i = 0; i < this.items.length; i++) {
				var h2 = this.get(i).getAllElementHeight();
				if (h2 > h)
					h = h2;
			}
			for ( var i = 0; i < 3; i++) {
				this.get(i).setHeight(h * 1.1);
			}
		}
	});
	return statusPanel;
}