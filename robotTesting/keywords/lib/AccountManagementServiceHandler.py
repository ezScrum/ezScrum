import os
import subprocess
from subprocess import call
from subprocess import Popen, PIPE, STDOUT


def turn_on_ams():
    #command = 'java -jar acct-mgt-1.0.jar'
    #p = subprocess.Popen(['java', '-jar', 'D:/test.jar'], stdin=PIPE, stderr=subprocess.STDOUT, shell = True)
    #p = subprocess.Popen(['java', '-jar', 'D:/acct-mgt-1.0.jar'], stdin=PIPE, stderr=subprocess.STDOUT, shell = True)
    #os.system("D:/anfaworkspace/ezScrum/robotTesting/keywords/lib/test.bat")
    #p = subprocess.Popen("D:/anfaworkspace/ezScrum/robotTesting/keywords/lib/test.bat", stdin=PIPE, stderr=subprocess.STDOUT, shell = True)
    p = subprocess.Popen("test.bat", cwd=r"D:/")
    #subprocess.check_output(["echo", "Hello World!"])
    #return p
    #stdout, stderr = p.communicate()
    #print p.returncode
    
def test():
    print "test"

def wfile():
    file_ = open("ouput.txt", "w")
    subprocess.Popen(["host", ipAddress], stdout=file_) 
