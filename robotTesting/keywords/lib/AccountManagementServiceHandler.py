import sys, os
import shlex, subprocess
from subprocess import call
from subprocess import Popen, PIPE, STDOUT

p = None

def turnOn():
    #p = subprocess.Popen('start',shell=True)
    #proc = subprocess.Popen('start', stdin = subprocess.PIPE, stdout = subprocess.PIPE,shell=True)
    #call(["java -jar acct-mgt-1.0.jar"])
    #if(num == 1):
    p = subprocess.Popen(['java','-jar','Microservice/AccountManagement/acct-mgt-1.0.jar'])
        #return p
        #p.kill()
    #os.system("java -jar acct-mgt-1.0.jar")
    #stdout, stderr = proc.communicate('dir c:\\')
    #stdout
    #cmd_line = "java -jar D:/acct-mgt-1.0.jar run"
    #p = subprocess.Popen(cmd_line, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    #out = p.communicate()[0]
    #print out
def turnOff():
    p = subprocess.Popen(['taskkill','/F','/IM','java.exe'])


    

 
