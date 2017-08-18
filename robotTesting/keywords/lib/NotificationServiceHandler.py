import sys, os
import shlex, subprocess
from subprocess import call
from subprocess import Popen, PIPE, STDOUT

def NotificationTurnOn():
    np = subprocess.Popen(['C:/Program Files/Java/jdk1.8.0_77/bin/java.exe','-jar','Microservice/NotificationService/notificationService-1.0.jar'])

def NotificationTurnOff():
    np = subprocess.Popen(['taskkill','/F','/IM','java.exe'])

def testA():
    subprocess.call(['C:/Program Files/Java/jdk1.8.0_77/bin/java.exe','-jar','Microservice/NotificationService/test.jar'])
