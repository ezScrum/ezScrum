import requests
import os
class UploadPlugin:

    def __init__(self):
        pass

    def add_plugin(self, url):
        dir = os.path.realpath(__file__)
        filename = os.path.normpath(dir + '/../../testData/redminePlugin.war')
        files = {'file': ('redminePlugin.war', open(filename, 'rb'))}
        f = open (filename)
        r =  requests.post(url=url,  files =  files )
        #print r.status_code
        #print r.headers

