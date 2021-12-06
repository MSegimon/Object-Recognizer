import requests
from time import time, sleep

from credentials.serverCredentials import serverIP, serverFolder
url = serverIP + serverFolder
imagePath = 'TestImages/LivingRoom2.jpg'


def sendImage(imagePath):
	files = {'image': open(imagePath, 'rb')}
	x = requests.post(url, files=files, verify=False)
	return x.text

if __name__ == '__main__':
    start = time()
    print(sendImage(imagePath))
    print('Time:', time() - start)
