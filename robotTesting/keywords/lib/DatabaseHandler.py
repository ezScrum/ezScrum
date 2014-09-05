import MySQLdb

class DatabaseHandler:


    def __init__(self):
        pass
    
    def is_delete(self, tableName):
        reservedTableNameList = ["mantis_user_table", "mantis_tokens_table", "mantis_config_table"]
        isDeleteFlag = 1
        for name in reservedTableNameList:
            isIdentical = cmp(tableName, name)
            if isIdentical == 0:
                isDeleteFlag = 0
                break
        return isDeleteFlag
    
    def Clean_Database(self, hostUrl, account, password, databaseName):
        print 'clean database1'
        db = MySQLdb.connect(host=hostUrl, user=account, passwd=password, db=databaseName)
        cursor = db.cursor()

        cursor.execute("Show Tables from " + databaseName)
        result = cursor.fetchall()

        for record in result:
            tableName = record[0]
            isDelete = self.is_delete(tableName)
            if isDelete == 0:
                print "Reserve " + tableName

            else :
                print "TRUNCATE TABLE `" + tableName + "`"
                cursor.execute("TRUNCATE TABLE `" + tableName + "`")

        print 'Add admin'
        cursor.execute("INSERT INTO `account` VALUES (1, 'admin', 'admin', 'example@ezScrum.tw', '21232f297a57a5a743894a0e4a801fc3', 1, 1379910191599, 1379910191599)")
        cursor.execute("INSERT INTO `system` VALUES (1, 1)")
        db.commit()

        

#if __name__ == '__main__':
#    databaseHandler = DatabaseHandler()
#    databaseHandler.clean_database("localhost", "spark", "spark", "robottest")
