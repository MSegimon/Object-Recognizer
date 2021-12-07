import requests
from time import time, sleep
import base64

from credentials.serverCredentials import serverIP, serverFolder
url = serverIP + serverFolder
imagePath = 'TestImages/LivingRoom2.jpg'


def sendImage(imagePath):
    with open(imagePath, "rb") as image_file:
        encoded_string = base64.b64encode(image_file.read())
    
    files = {'image64': encoded_string}
    #print(files)

    x = requests.get(url, files=files)
    return x.text

if __name__ == '__main__':
    start = time()
    print(sendImage(imagePath))
    print('Time:', time() - start)
