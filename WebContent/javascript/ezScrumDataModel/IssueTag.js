var IssueTagRecord = Ext.data.Record.create([ {name:'Id', sortType:'asInt'}, 'Name' ]);

var IssueTagReader = new Ext.data.XmlReader({
   record: 'IssueTag',
   idPath : 'Id',
   successProperty: 'Result'
}, IssueTagRecord);