import requests
import json
import MySQLdb

class GetAccount(object):

    DB_URL = "localhost"
    DB_USERNAME = ""
    DB_PASSWORD = ""
    DB_NAME = "account_ms"
    TABLE_NAME = "user"

    def setDBAccountPassword(self, DB_USERNAME, DB_PASSWORD):
        self.DB_USERNAME = DB_USERNAME
        self.DB_PASSWORD = DB_PASSWORD

    def getAllAccounts(self):

        db = MySQLdb.connect(self.DB_URL, self.DB_USERNAME, self.DB_PASSWORD, self.DB_NAME)

        cursor = db.cursor()
        
        sql = "SELECT * FROM " + self.TABLE_NAME

        try:

            cursor.execute(sql)

            results = cursor.fetchall()

            result_objs= []
            result_obj = {}
            
            for col in results:
                id = col[0]
                email = col[1]
                enabled = col[2]
                password = col[3]
                systemrole = col[4]
                username = col[5]
                nickname = col[6]
                result_obj={"id" : id, "email" : email, "enabled" : enabled, "password" : password, "systemrole" : systemrole, "username" : username, "nickname" : nickname}
                result_objs.append(result_obj)

            return result_objs
        except:
            return "Can not fetch the data of database!"

        db.close()
        
    
    def checkObjectInDatabase(self, value):
        objs=self.getAllAccounts()
        state=False
        for obj in objs:
            print value["email"]
            if value["username"]==obj["username"] and value["email"]==obj["email"] and value["nickname"]==obj["nickname"]:
                state=True
                break
        return state

    def checkItNow(self ,username):
        
        db = MySQLdb.connect(self.DB_URL, self.DB_USERNAME, self.DB_PASSWORD, self.DB_NAME)

        cursor = db.cursor()

        sql = "SELECT * FROM " + self.TABLE_NAME + " WHERE username = '" + username + "'"

        count = cursor.execute(sql)

        return count != 0

    def waitCheck(self, username):
        state = self.checkItNow(username)
        while(state != True):
            state = self.checkItNow(username)
    
#g=GetAccount()
#print(g.checkObjectInDatabase({"username" : "Mark", "email" : "Mark@mail.com", "nickname" : "Mark Idea"}))
#print(g.getAllAccounts())
#print(g.checkItNow("admin"))
