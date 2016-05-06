import requests

# Username (MD5 encoded)
username = r'21232f297a57a5a743894a0e4a801fc3'
# Password (MD5 encoded)
password = r'21232f297a57a5a743894a0e4a801fc3'
# Headers for request
headers = {
    "username": username,
    "password": password
}
# Sub URL for switch on
switch_on_url = "/aspectj/switch/on"
# Sub URL for switch off
switch_off_url = "/aspectj/switch/off"


def turn_aspectj_on_by_action_name(ezscrum_url, action_name):
    url = ezscrum_url + switch_on_url
    response = requests.post(url, data=action_name, headers=headers)

    print (response.status_code)
    if response.status_code is 200:
        return True
    else:
        return False


def turn_aspectj_off(ezscrum_url):
    url = ezscrum_url + switch_off_url
    response = requests.post(url, headers=headers)

    print (response.status_code)
    if response.status_code is 200:
        return True
    else:
        return False
