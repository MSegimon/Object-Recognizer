import requests
from time import time, sleep

url = 'http://141.94.70.32:5000/'
imagePath = 'TestImages/LivingRoom2.jpg'


def sendImage(imagePath):
	files = {'image': open(imagePath, 'rb')}
	x = requests.post(url, files=files)
	return x.text

if __name__ == '__main__':
    start = time()
    print(sendImage(imagePath))
    print('Time:', time() - start)