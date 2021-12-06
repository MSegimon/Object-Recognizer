from time import sleep, time
from flask import Flask, request

import Object
from betterImage import enhance


def main(path):
    #Enhance the image
    enhance(path)

    #Get objects in the image
    objects = Object.GetObjects(path)

    #Create string that will be sent to the client
    output = ""
    for object in objects:
        if object.HasGoodConfidence():
            output += "There is a " + object.GetName() + " to your "
        else:
            output += "There is an unknown object to your "

        pos = object.GetRelativePosition()
        if pos == Object.Direction.LEFT:
            output += ("left. ")
        elif pos == Object.Direction.SLIGHT_LEFT:
            output += ("slight left. ")
        elif pos == Object.Direction.RIGHT:
            output += ("right. ")
        elif pos == Object.Direction.SLIGHT_RIGHT:
            output += ("slight right. ")
        elif pos == Object.Direction.CENTER:
            output += ("center. ")

    #Return string to client
    return output


#Create the flask app
app = Flask(__name__)

runFiles = 'runFiles/'

from credentials.serverCredentials import serverFolder
@app.route('/' + serverFolder, methods=['GET', 'POST'])
def root():
	if request.method == 'POST':
		f = request.files['image']  # THIS GETS THE IMAGE FROM THE REQUEST
		f.save(runFiles + 'received.jpg')

        #Call main function
		string = main(runFiles + 'received.jpg')
		return string
	else:
		return 'Hello, World!'


from credentials.serverCredentials import ssl_path
app.run(host='0.0.0.0', ssl_context=ssl_path)
