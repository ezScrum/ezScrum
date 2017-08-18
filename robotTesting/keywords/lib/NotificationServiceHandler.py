import sys, os
import shlex, subprocess
from subprocess import call
from subprocess import Popen, PIPE, STDOUT

def NotificationTurnOn():
    np = subprocess.Popen(['java','-jar','Microservice/NotificationService/notificationService-1.0.jar'])

def NotificationTurnOff():
    np = subprocess.Popen(['taskkill','/F','/IM','java.exe'])
